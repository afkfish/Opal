package com.afkfish.commands;

import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.Interaction;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;

import java.util.concurrent.CompletableFuture;

import static com.afkfish.Opal.players;

public class PauseCommand extends ServerCommand {
	@Override
	public void execute(Interaction interaction, CompletableFuture<InteractionOriginalResponseUpdater> response, Server server, EmbedBuilder embed) {
		// check if there is a player for the server
		if (players.containsKey(server.getId()) && !players.get(server.getId()).isPaused()) {
			// get the player for the server and pause it
			// if the player is already paused, send a message to the user
			players.get(server.getId()).setPaused(true);
			embed.setTitle("Paused");
			response.thenAccept(updater -> updater.addEmbed(embed).update());
		}
		// else, send a message saying that the bot is not playing anything
		else {
			embed.setDescription("The bot is not playing anything.");
			response.thenAccept(updater -> updater.addEmbed(embed).update());
		}
	}
}
