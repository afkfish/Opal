package com.afkfish.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;

import java.util.concurrent.CompletableFuture;

import static com.afkfish.Opal.players;
import static com.afkfish.Opal.schedulers;

public class PlayAudioLoadHandler implements AudioLoadResultHandler {
	private final CompletableFuture<InteractionOriginalResponseUpdater> response;
	private static final Logger LOGGER = LogManager.getLogger(PlayAudioLoadHandler.class);
	private final long serverId;

	public PlayAudioLoadHandler(CompletableFuture<InteractionOriginalResponseUpdater> response, Long serverId) {
		this.response = response;
		this.serverId = serverId;
	}
	@Override
	public void trackLoaded(AudioTrack track) {
		//check if a track scheduler exists for the server, if not create one and add it to the map
		//then add the track to the scheduler
		if (schedulers.containsKey(serverId)) {
			LOGGER.info("Scheduler exists for server " + serverId);
		} else {
			LOGGER.info("Scheduler does not exist for server " + serverId);
			TrackScheduler scheduler = new TrackScheduler(players.get(serverId));
			schedulers.put(serverId, scheduler);
		}

		// if a song is already playing, the track will be queued
		if (players.get(serverId).getPlayingTrack() == null) {
			LOGGER.info("Playing track: " + track.getInfo().title + " because nothing was playing");
			EmbedBuilder embed = new EmbedBuilder()
					.setTitle("Now Playing")
					.setDescription(track.getInfo().title);
			response.thenAccept(originalInteraction -> originalInteraction.addEmbed(embed).update());
			players.get(serverId).playTrack(track);
		} else {
			LOGGER.info("Queued track: " + track.getInfo().title + " because something was playing");
			EmbedBuilder embed = new EmbedBuilder()
					.setTitle("Queued")
					.setDescription(track.getInfo().title);
			response.thenAccept(originalInteraction -> originalInteraction.addEmbed(embed).update());
			schedulers.get(serverId).queue.add(track);
		}
	}

	@Override
	public void playlistLoaded(AudioPlaylist playlist) {
		//check if a track scheduler exists for the server, if not create one and add it to the map
		EmbedBuilder embed = new EmbedBuilder();

		TrackScheduler scheduler;
		if (schedulers.containsKey(serverId)) {
			LOGGER.info("Scheduler exists for server " + serverId);
			scheduler = schedulers.get(serverId);
		} else {
			LOGGER.info("Creating new scheduler for server " + serverId);
			scheduler = new TrackScheduler(players.get(serverId));
			schedulers.put(serverId, scheduler);
		}

		//add the tracks to the scheduler
		//if the playlist is a result of a YouTube search, the first track will be added
		if (playlist.isSearchResult()) {
			LOGGER.info("Search result, appending first to queue");
			scheduler.queue.add(playlist.getTracks().get(0));
		} else {
			if (playlist.getTracks().size() > 20) {
				LOGGER.info("Playlist has more than 20 tracks, appending first 20 to queue");
				for (int i = 0; i < 20; i++) {
					scheduler.queue.add(playlist.getTracks().get(i));
				}
			} else {
				LOGGER.info("Appending all tracks to queue from playlist");
				scheduler.queue.addAll(playlist.getTracks());
			}
		}

		// play the first track if nothing is playing
		if (players.get(serverId).getPlayingTrack() == null) {
			LOGGER.info("Playing first track from playlist, because nothing was playing");
			players.get(serverId).playTrack(playlist.getTracks().get(0));
			schedulers.get(serverId).queue.remove(0);
			embed.setTitle("Now Playing");
			embed.setDescription(playlist.getTracks().get(0).getInfo().title);
			response.thenAccept(originalInteraction -> originalInteraction.setContent("Playing " + playlist.getTracks().get(0).getInfo().title).update());
			return;
		}
		embed.setTitle("Queued");
		embed.setDescription("Added " + playlist.getTracks().size() + " track(s) to queue");
		response.thenAccept(originalInteraction -> originalInteraction.addEmbed(embed).update());
	}

	@Override
	public void noMatches() {
		LOGGER.error("No matches found");
		EmbedBuilder embed = new EmbedBuilder()
				.setTitle("No matches found")
				.setDescription("No matches were found for the given query");
		response.thenAccept(originalInteraction -> originalInteraction.addEmbed(embed).update());
	}

	@Override
	public void loadFailed(FriendlyException throwable) {
		// Notify the user that everything exploded
		LOGGER.error("Load failed", throwable);
		EmbedBuilder embed = new EmbedBuilder()
				.setTitle("Load failed")
				.setDescription("Something went wrong while loading the track");
		response.thenAccept(originalInteraction -> originalInteraction.addEmbed(embed).update());
	}
}
