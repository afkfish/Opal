package com.afkfish.commands;

import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.Interaction;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.afkfish.Opal.schedulers;

public class ClearCommand implements Command {
	@Override
	public void execute(Interaction interaction) {
		CompletableFuture<InteractionOriginalResponseUpdater> response = interaction.respondLater();

		Optional<Server> server;
		if ((server = interaction.getServer()).isEmpty()) {
			response.thenAccept(updater -> updater.setContent("This command can only be used in a server!").update());
			return;
		}

		if (!schedulers.containsKey(server.get().getId()) || schedulers.get(server.get().getId()).queue.isEmpty()) {
			response.thenAccept(updater -> updater.setContent("Queue is empty!").update());
			return;
		}

		schedulers.get(server.get().getId()).queue.clear();
		response.thenAccept(updater -> updater.setContent("Queue cleared!").update());
	}
}
