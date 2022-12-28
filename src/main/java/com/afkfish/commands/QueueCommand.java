package com.afkfish.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.Interaction;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;

import java.util.concurrent.CompletableFuture;

import static com.afkfish.Opal.schedulers;

public class QueueCommand extends ServerCommand {
	@Override
	public void execute(Interaction interaction, CompletableFuture<InteractionOriginalResponseUpdater> response, Server server, EmbedBuilder embed) {
		if (!schedulers.containsKey(server.getId()) || schedulers.get(server.getId()).queue.isEmpty()) {
			embed.addField("Empty", "There are no songs in the queue.");
			response.thenAccept(updater -> updater.addEmbed(embed).update());
			return;
		}

		StringBuilder queue = new StringBuilder();
		for (AudioTrack track: schedulers.get(server.getId()).queue) {
			queue.append(track.getInfo().title).append("\n");
		}
		embed.addField("Queue", queue.toString());
		response.thenAccept(updater -> updater.setContent(queue.toString()).update());
	}
}
