package com.afkfish.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.Interaction;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;

import java.util.concurrent.CompletableFuture;

import static com.afkfish.Opal.players;
import static com.afkfish.Opal.schedulers;

public class SkipCommand extends ServerCommand {
	@Override
	public void execute(Interaction interaction, CompletableFuture<InteractionOriginalResponseUpdater> response, Server server, EmbedBuilder embed) {
		// if the scheduler is present and the queue is not empty,
		// send a message saying "Skipped to next song in queue: " and the song's title,
		// then play the next song in the queue
		if (schedulers.containsKey(server.getId()) && !schedulers.get(server.getId()).queue.isEmpty()) {
			AudioTrack track = schedulers.get(server.getId()).queue.get(0).makeClone();
			// remove the first song in the queue
			schedulers.get(server.getId()).queue.remove(0);
			players.get(server.getId()).playTrack(track);
			embed.setTitle("Skipped");
			embed.setDescription("Playing next song in queue: " + track.getInfo().title);
			response.thenAccept(updater -> updater.addEmbed(embed).update());
		}
		// else, stop the player (simulating a skip) and send "Skipped" to the channel
		else {
			players.get(server.getId()).stopTrack();
			embed.setTitle("Skipped");
			response.thenAccept(updater -> updater.addEmbed(embed).update());
		}
	}
}
