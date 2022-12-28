package com.afkfish.commands;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.Interaction;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;

import java.util.concurrent.CompletableFuture;

import static com.afkfish.Opal.schedulers;

public class ClearCommand extends ServerCommand {
	@Override
	public void execute(Interaction interaction, CompletableFuture<InteractionOriginalResponseUpdater> response, Server server, EmbedBuilder embed) {
		if (!schedulers.containsKey(server.getId()) || schedulers.get(server.getId()).queue.isEmpty()) {
			embed.setTitle("Empty Queue");
			response.thenAccept(updater -> updater.addEmbed(embed).update());
			return;
		}

		schedulers.get(server.getId()).queue.clear();
		embed.setTitle("Queue Cleared");
		response.thenAccept(updater -> updater.addEmbed(embed).update());
	}
}
