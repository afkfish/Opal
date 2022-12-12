package com.afkfish;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

import static com.afkfish.Opal.players;
import static com.afkfish.Opal.schedulers;

public class PlayAudioLoadHandler implements AudioLoadResultHandler {
	private final CompletableFuture<InteractionOriginalResponseUpdater> response;
	private static final Logger DEFAULT_LOGGER = LoggerFactory.getLogger(PlayAudioLoadHandler.class);
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
			DEFAULT_LOGGER.info("Scheduler exists for server " + serverId);
			schedulers.get(serverId).queue.add(track);
		} else {
			DEFAULT_LOGGER.info("Scheduler does not exist for server " + serverId);
			TrackScheduler scheduler = new TrackScheduler(players.get(serverId));
			schedulers.put(serverId, scheduler);
			scheduler.queue.add(track);
		}

		// if a song is already playing, the track will be queued
		if (players.get(serverId).getPlayingTrack() == null) {
			DEFAULT_LOGGER.info("Playing track: " + track.getInfo().title + " because nothing was playing");
			response.thenAccept(originalInteraction -> originalInteraction.setContent("Playing " + track.getInfo().title).update());
			players.get(serverId).playTrack(track);
		} else {
			DEFAULT_LOGGER.info("Queued track: " + track.getInfo().title + " because something was playing");
			response.thenAccept(originalInteraction -> originalInteraction.setContent("Queued " + track.getInfo().title).update());
		}
	}

	@Override
	public void playlistLoaded(AudioPlaylist playlist) {
		//check if a track scheduler exists for the server, if not create one and add it to the map
		//then add the tracks to the scheduler
		if (schedulers.containsKey(serverId)) {
			TrackScheduler scheduler = schedulers.get(serverId);
			scheduler.queue.addAll(playlist.getTracks());
			response.thenAccept(originalInteraction -> originalInteraction.setContent("Added " + playlist.getTracks().size() + " tracks to the queue.").update());
		} else {
			TrackScheduler scheduler = new TrackScheduler(players.get(serverId));
			schedulers.put(serverId, scheduler);
			scheduler.queue.addAll(playlist.getTracks());
			response.thenAccept(originalInteraction -> originalInteraction.setContent("Playing " + playlist.getTracks().get(0).getInfo().title + " and added " + (playlist.getTracks().size() - 1) + " tracks to the queue.").update());
		}

		// if a song is already playing, the track will be queued
		if (players.get(serverId).getPlayingTrack() == null) {
			players.get(serverId).playTrack(playlist.getTracks().get(0));
		}
	}

	@Override
	public void noMatches() {
		DEFAULT_LOGGER.error("No matches found");
		response.thenAccept(originalInteraction -> originalInteraction.setContent("No matches found").update());
	}

	@Override
	public void loadFailed(FriendlyException throwable) {
		// Notify the user that everything exploded
	}
}
