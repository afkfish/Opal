package com.afkfish.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.Interaction;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.afkfish.Opal.players;
import static com.afkfish.Opal.schedulers;

public class SkipCommand implements Command{
	@Override
	public void execute(Interaction interaction) {
		CompletableFuture<InteractionOriginalResponseUpdater> response = interaction.respondLater();

		Optional<Server> server = interaction.getServer();
		if (server.isEmpty()) {
			return;
		}

		// if the scheduler is present and the queue is not empty,
		// send a message saying "Skipped to next song in queue: " and the song's title,
		// then play the next song in the queue
		if (schedulers.containsKey(server.get().getId()) && !schedulers.get(server.get().getId()).queue.isEmpty()) {
			AudioTrack track = schedulers.get(server.get().getId()).queue.get(0).makeClone();
			// remove the first song in the queue
			schedulers.get(server.get().getId()).queue.remove(0);
			players.get(server.get().getId()).playTrack(track);
			response.thenAccept(updater -> updater.setContent("Skipped to next song in queue: " + track.getInfo().title).update());
		}
		// else, stop the player (simulating a skip) and send "Skipped" to the channel
		else {
			players.get(server.get().getId()).stopTrack();
			response.thenAccept(updater -> updater.setContent("Skipped").update());
		}
	}
}
