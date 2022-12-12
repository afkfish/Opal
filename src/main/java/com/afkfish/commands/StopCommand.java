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
		CompletableFuture<InteractionOriginalResponseUpdater> response = interaction.respondLater(true);

		Optional<Server> server;
		if ((server = interaction.getServer()).isPresent()) {
			players.get(server.get().getId()).stopTrack();
		}

		response.thenAccept(originalInteraction -> originalInteraction.setContent("Stopped").update());
	}
}
