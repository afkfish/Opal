package com.afkfish.commands;

import com.afkfish.audio.LavaplayerAudioSource;
import com.afkfish.audio.PlayAudioLoadHandler;
import org.javacord.api.audio.AudioSource;
import org.javacord.api.entity.channel.ServerVoiceChannel;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.Interaction;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static com.afkfish.Opal.playerManager;
import static com.afkfish.Opal.players;

public class PlayCommand extends ServerCommand {
	@Override
	@SuppressWarnings("OptionalGetWithoutIsPresent")
	public void execute(Interaction interaction, CompletableFuture<InteractionOriginalResponseUpdater> response, Server server, EmbedBuilder embed) {
		Optional<ServerVoiceChannel> channel;

		channel = interaction.getUser().getConnectedVoiceChannel(server);
		if (channel.isEmpty()) {
			embed.setTitle("Error");
			embed.setDescription("You must be in a voice channel to use this command.");
			response.thenAccept(updater -> updater.addEmbed(embed).update());
			return;
		}

		//check if a player exists for the server, if not create one and add it to the map
		if (!players.containsKey(server.getId())) {
			players.put(server.getId(), playerManager.createPlayer());
		}

		AudioSource source = new LavaplayerAudioSource(interaction.getApi(), players.get(server.getId()));
		if (!channel.get().getConnectedUsers().contains(interaction.getApi().getYourself())) {
			channel.get()
					.connect()
					.thenAccept(audioConnection -> audioConnection.setAudioSource(source));
		}

		String query = interaction.asSlashCommandInteraction().get().getArgumentByName("query").get().getStringValue().get();

		// check if url strats with http or https, if not, search YouTube
		final String urlMatch = "^(http|https)://.*$";
		if (!query.matches(urlMatch)) {
			query = "ytsearch:" + query;
		}
		playerManager.loadItem(query, new PlayAudioLoadHandler(response, server.getId()));
	}
}
