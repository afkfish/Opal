package com.afkfish.commands;

import org.javacord.api.audio.AudioConnection;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.Interaction;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;

import java.util.concurrent.CompletableFuture;

import static com.afkfish.Opal.players;
import static com.afkfish.Opal.schedulers;

public class LeaveCommand extends ServerCommand {
	@Override
	public void execute(Interaction interaction, CompletableFuture<InteractionOriginalResponseUpdater> response, Server server, EmbedBuilder embed) {
		server.getAudioConnection().ifPresent(AudioConnection::close);

		players.remove(server.getId());
		schedulers.remove(server.getId());

		embed.setTitle("Left voice channel");
		response.thenAccept(updater -> updater.addEmbed(embed).update());
	}
}
