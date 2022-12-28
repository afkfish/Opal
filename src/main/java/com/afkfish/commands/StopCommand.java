package com.afkfish.commands;

import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.Interaction;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.afkfish.Opal.players;

public class StopCommand implements Command{
	@Override
	public void execute(Interaction interaction) {
		CompletableFuture<InteractionOriginalResponseUpdater> response = interaction.respondLater();

		Optional<Server> server;
		if ((server = interaction.getServer()).isPresent()) {
			players.get(server.get().getId()).stopTrack();
		}

		response.thenAccept(updater -> updater.setContent("Stopped playing!").update());
	}
}
