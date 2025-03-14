package net.shirojr.illusionable.event;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.shirojr.illusionable.command.IllusionCommand;

public class CommandRegistrationEvents {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
            IllusionCommand.register(commandDispatcher, commandRegistryAccess, registrationEnvironment);
        });
    }
}
