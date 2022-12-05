package com.afkfish.commands;

import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.Interaction;

import java.util.Optional;

public class PlayCommand implements Command{
	@Override
	public void execute(Interaction interaction) {
		interaction.respondLater();
		Optional<Server> server = interaction.getServer();
		Optional<ServerVoiceChannel> channel;
		if (server.isPresent()) {
			channel = interaction.getUser().getConnectedVoiceChannel(server.get());
			channel.ifPresent(ServerVoiceChannel::connect);
		}

		System.out.println();
	}
}
