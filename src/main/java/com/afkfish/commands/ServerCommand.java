package com.afkfish.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.Interaction;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class ServerCommand {
    private static final Logger LOGGER = LogManager.getLogger(ServerCommand.class);
    public abstract void execute(Interaction interaction, CompletableFuture<InteractionOriginalResponseUpdater> response, Server server, EmbedBuilder embed);

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public void validate(Interaction interaction) {
        CompletableFuture<InteractionOriginalResponseUpdater> response = interaction.respondLater();
        Optional<Server> server = interaction.getServer();

        EmbedBuilder embed = new EmbedBuilder();

        if (server.isEmpty()) {
            LOGGER.warn("ServerCommand was called in a DM");
            embed.setTitle("Error");
            embed.setDescription("This command can only be used in a server.");
            response.thenAccept(updater -> updater.addEmbed(embed).update());
            return;
        }

        LOGGER.debug("Command {} called by {}", interaction.asSlashCommandInteraction().get().getCommandName(), interaction.getUser().getDiscriminatedName());
        this.execute(interaction, response, server.get(), embed);
    }
}
