package com.afkfish.commands;

import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.Interaction;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.afkfish.Opal.players;

public class PauseCommand implements Command {
	@Override
	public void execute(Interaction interaction) {
		CompletableFuture<InteractionOriginalResponseUpdater> response = interaction.respondLater();
		Optional<Server> server = interaction.getServer();

		if (server.isEmpty()) {
			response.thenAccept(updater -> updater.setContent("This command can only be used in a server!").update());
			return;
		}

		// check if there is a player for the server
		if (players.containsKey(server.get().getId())) {
			// get the player for the server and pause it
			// if the player is already paused, send a message to the user
			players.get(server.get().getId()).setPaused(true);
			response.thenAccept(updater -> updater.setContent("Paused!").update());
		}
		// else, send a message saying that the bot is not playing anything
		else {
			response.thenAccept(updater -> updater.setContent("I'm not playing anything!").update());
		}
	}
}
