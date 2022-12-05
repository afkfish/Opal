package com.afkfish.commands;

import org.javacord.api.interaction.Interaction;

public interface Command {
    void execute(Interaction interaction);
}
