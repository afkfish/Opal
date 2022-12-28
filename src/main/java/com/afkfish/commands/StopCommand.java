package com.afkfish.commands;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.Interaction;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;

import java.util.concurrent.CompletableFuture;

import static com.afkfish.Opal.players;

public class StopCommand extends ServerCommand {
	@Override
	public void execute(Interaction interaction, CompletableFuture<InteractionOriginalResponseUpdater> response, Server server, EmbedBuilder embed) {
		players.get(server.getId()).stopTrack();
		embed.setTitle("Stopped");
		response.thenAccept(updater -> updater.addEmbed(embed).update());
	}
}
