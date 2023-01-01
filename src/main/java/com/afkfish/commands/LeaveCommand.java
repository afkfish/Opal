package com.afkfish.commands;

import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.Interaction;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.afkfish.Opal.players;
import static com.afkfish.Opal.schedulers;

public class LeaveCommand extends ServerCommand {
	@Override
	public void execute(Interaction interaction, CompletableFuture<InteractionOriginalResponseUpdater> response, Server server, EmbedBuilder embed) {
		Optional<ServerVoiceChannel> voiceChannel = interaction.getApi().getYourself().getConnectedVoiceChannel(server);

		players.get(server.getId()).stopTrack();
		voiceChannel.ifPresent(ServerVoiceChannel::disconnect);

		players.remove(server.getId());
		schedulers.remove(server.getId());

		embed.setTitle("Left voice channel");
		response.thenAccept(updater -> updater.addEmbed(embed).update());
	}
}
