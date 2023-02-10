package com.afkfish.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.interaction.Interaction;
import org.javacord.api.interaction.callback.InteractionOriginalResponseUpdater;

import java.util.concurrent.CompletableFuture;

import static com.afkfish.Opal.players;

public class NpCommand extends ServerCommand {
	@Override
	public void execute(Interaction interaction, CompletableFuture<InteractionOriginalResponseUpdater> response, Server server, EmbedBuilder embed) {
		AudioTrack track = players.get(server.getId()).getPlayingTrack();
		long progress = track.getPosition()/track.getDuration();

		embed.setTitle(track.getInfo().title);
		embed.setUrl(track.getInfo().uri);
		embed.setDescription(getStatusEmoji(track)
				+ " " + progressBar(progress)
				+ " `[" + formatTime(track.getPosition()) + "/" + formatTime(track.getDuration()) + "]`"
		);

		response.thenAccept(updater -> updater.addEmbed(embed).update());
	}

	private static String progressBar(long progress) {
		StringBuilder bar = new StringBuilder();
		for (int i = 0; i < 12; i++) {
			if (i == (int)(progress*12)) {
				bar.append("\\uD83D\\uDD18");
			} else {
				bar.append("▬");
			}
		}
		return bar.toString();
	}

	private static String formatTime(long duration)
	{
		if(duration == Long.MAX_VALUE)
			return "LIVE";
		long seconds = Math.round(duration/1000.0);
		long hours = seconds/(60*60);
		seconds %= 60*60;
		long minutes = seconds/60;
		seconds %= 60;
		return (hours>0 ? hours+":" : "") + (minutes<10 ? "0"+minutes : minutes) + ":" + (seconds<10 ? "0"+seconds : seconds);
	}

	private static String getStatusEmoji(AudioTrack track) {
		return switch (track.getState()) {
			case PLAYING -> "▶️";
			case INACTIVE -> "⏸️";
			case FINISHED -> "⏹️";
			default -> "";
		};
	}
}
