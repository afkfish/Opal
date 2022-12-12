package com.afkfish.commands;

import com.afkfish.LavaplayerAudioSource;
import com.afkfish.PlayAudioLoadHandler;
import org.javacord.api.audio.AudioSource;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.Interaction;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.afkfish.Opal.playerManager;
import static com.afkfish.Opal.players;

public class PlayCommand implements Command{
	@Override
	public void execute(Interaction interaction) {
		CompletableFuture<InteractionOriginalResponseUpdater> response = interaction.respondLater(true);
		Optional<Server> server = interaction.getServer();
		Optional<ServerVoiceChannel> channel;

		if (server.isEmpty()) {
			response.thenAccept(updater -> updater.setContent("This command can only be used in a server!").update());
			return;
		}

		channel = interaction.getUser().getConnectedVoiceChannel(server.get());
		if (channel.isEmpty()) {
			response.thenAccept(originalInteraction -> originalInteraction.setContent("You are not in a voice channel!").update());
			return;
		}

		//check if a player exists for the server, if not create one and add it to the map
		if (!players.containsKey(server.get().getId())) {
			players.put(server.get().getId(), playerManager.createPlayer());
		}

		AudioSource source = new LavaplayerAudioSource(interaction.getApi(), players.get(server.get().getId()));
		if (!channel.get().getConnectedUsers().contains(interaction.getApi().getYourself())) {
			channel.get()
					.connect()
					.thenAccept(audioConnection -> audioConnection.setAudioSource(source));
		}

		String url = interaction.asSlashCommandInteraction().get().getArgumentByName("query").get().getStringValue().get();

		playerManager.loadItem(url, new PlayAudioLoadHandler(response, server.get().getId()));
	}
}
