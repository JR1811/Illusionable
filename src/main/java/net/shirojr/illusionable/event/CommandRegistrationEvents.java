package net.shirojr.illusionable.event;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.shirojr.illusionable.command.IllusionCommand;

public class CommandRegistrationEvents {
    public static void register() {
        CommandRegistrationCallback.EVENT.register(IllusionCommand::register);
    }
}
