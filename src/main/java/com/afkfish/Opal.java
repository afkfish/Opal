package com.afkfish;

import com.afkfish.audio.TrackScheduler;
import com.afkfish.commands.*;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;

import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

public class Opal {
	private static final HashMap<String, ServerCommand> serverCommands = new HashMap<>();
	public static final HashMap<Long, AudioPlayer> players = new HashMap<>();
	public static final HashMap<Long, TrackScheduler> schedulers = new HashMap<>();
	public static final Logger LOGGER = LogManager.getLogger(Opal.class);
	public static AudioPlayerManager playerManager;

	public static void main(String[] args) {
		playerManager = new DefaultAudioPlayerManager();
		playerManager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
		AudioSourceManagers.registerRemoteSources(playerManager);

		long testServerId = Long.parseLong(System.getenv("TEST_SERVER_ID"));

		final String token = System.getenv("TOKEN");
		DiscordApi api = new DiscordApiBuilder()
				.setToken(token)
				.setWaitForServersOnStartup(false)
				.addIntents(Intent.MESSAGE_CONTENT)
				.login().join();

		addCommands(api);

		api.addInteractionCreateListener(event -> {
			Optional<SlashCommandInteraction> interaction = event.getSlashCommandInteraction();
			interaction.ifPresent(slashCommandInteraction -> {
						ServerCommand command = serverCommands.get(slashCommandInteraction.getFullCommandName());
						if (command != null) command.validate(event.getInteraction());
						else LOGGER.info("Invalid command was passed!" + slashCommandInteraction.getFullCommandName());
					}
			);
		});
	}

	public static void addCommands(DiscordApi api) {
		SlashCommand.with("p", "play",
						Collections.singletonList(SlashCommandOption.createStringOption("query", "The song to play", true)))
				.createGlobal(api)
				.join();
		serverCommands.put("p", new PlayCommand());

		SlashCommand.with("play", "play",
						Collections.singletonList(SlashCommandOption.createStringOption("query", "The song to play", true)))
				.createGlobal(api)
				.join();
		serverCommands.put("play", new PlayCommand());

		SlashCommand.with("pause", "pause")
				.createGlobal(api)
				.join();
		serverCommands.put("pause", new PauseCommand());

		SlashCommand.with("stop", "stop")
				.createGlobal(api)
				.join();
		serverCommands.put("stop", new StopCommand());

		SlashCommand.with("skip", "skip")
				.createGlobal(api)
				.join();
		serverCommands.put("skip", new SkipCommand());

		SlashCommand.with("queue", "queue")
				.createGlobal(api)
				.join();
		serverCommands.put("queue", new QueueCommand());

		SlashCommand.with("clear", "clear the queue")
				.createGlobal(api)
				.join();
		serverCommands.put("clear", new ClearCommand());

		SlashCommand.with("leave", "leave the voice channel")
				.createGlobal(api)
				.join();
		serverCommands.put("leave", new LeaveCommand());

		LOGGER.info("Commands added!");
	}
}