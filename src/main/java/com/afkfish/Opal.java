package com.afkfish;

import com.afkfish.audio.TrackScheduler;
import com.afkfish.commands.Command;
import com.afkfish.commands.PlayCommand;
import com.afkfish.commands.SkipCommand;
import com.afkfish.commands.StopCommand;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandInteraction;
import org.javacord.api.interaction.SlashCommandOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

public class Opal {
	private static final HashMap<String, Command> commands = new HashMap<>();
	public static HashMap<Long, AudioPlayer> players = new HashMap<>();
	public static HashMap<Long, TrackScheduler> schedulers = new HashMap<>();
	private static final Logger DEFAULT_LOGGER = LoggerFactory.getLogger(Opal.class);
	public static AudioPlayerManager playerManager;

	public static void main(String[] args) {
		playerManager = new DefaultAudioPlayerManager();
		playerManager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
		playerManager.registerSourceManager(new YoutubeAudioSourceManager());

		long testServerId = Long.parseLong(System.getenv("TEST_SERVER_ID"));

		final String token = System.getenv("TOKEN");
		DiscordApi api = new DiscordApiBuilder()
				.setToken(token)
				.setWaitForServersOnStartup(false)
				.addIntents(Intent.MESSAGE_CONTENT)
				.login().join();

		SlashCommand.with("p", "play",
						Collections.singletonList(SlashCommandOption.createStringOption("query", "The song to play", true)))
				.createGlobal(api)
				.join();
		commands.put("p", new PlayCommand());

		SlashCommand.with("stop", "stop")
				.createGlobal(api)
				.join();
		commands.put("stop", new StopCommand());

		SlashCommand.with("skip", "skip")
				.createGlobal(api)
				.join();
		commands.put("skip", new SkipCommand());

		api.addInteractionCreateListener(event -> {
			Optional<SlashCommandInteraction> interaction = event.getSlashCommandInteraction();
			interaction.ifPresent(slashCommandInteraction -> {
						Command command = commands.get(slashCommandInteraction.getFullCommandName());
						if (command != null) command.execute(event.getInteraction());
						else DEFAULT_LOGGER.info("Invalid command was passed!" + slashCommandInteraction.getFullCommandName());
					}
			);
		});
	}
}