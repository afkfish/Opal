package com.afkfish;

import com.afkfish.commands.Command;
import com.afkfish.commands.PlayCommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.intent.Intent;
import org.javacord.api.interaction.SlashCommand;
import org.javacord.api.interaction.SlashCommandInteraction;

import java.util.HashMap;
import java.util.Optional;

public class Opal {
	private static final HashMap<String, Command> commands = new HashMap<>();
	private static final Logger DEFAULT_LOGGER = LogManager.getLogger(Opal.class);

	public static void main(String[] args) {
		final String token = System.getenv("token");
		DiscordApi api = new DiscordApiBuilder()
				.setToken(token)
				.addIntents(Intent.MESSAGE_CONTENT)
				.login().join();

		SlashCommand play = SlashCommand.with("p", "play")
				.createForServer(api, 940575531567546369L)
				.join();

		api.addInteractionCreateListener(event -> {
			Optional<SlashCommandInteraction> interaction = event.getSlashCommandInteraction();
			interaction.ifPresent(slashCommandInteraction -> {
						Command command = commands.get(slashCommandInteraction.getFullCommandName());
						if (command != null) command.execute(event.getInteraction());
						else DEFAULT_LOGGER.info("Invalid command was passed!" + slashCommandInteraction.getFullCommandName());
					}
			);
		});

		commands.put("p", new PlayCommand());
	}
}