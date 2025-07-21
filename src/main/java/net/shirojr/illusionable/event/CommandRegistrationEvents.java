package net.shirojr.illusionable.event;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.shirojr.illusionable.command.DamageDistributionCommand;
import net.shirojr.illusionable.command.IllusionCommand;

public class CommandRegistrationEvents {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            IllusionCommand.register(dispatcher, registryAccess, environment);
            DamageDistributionCommand.register(dispatcher, registryAccess, environment);
        });
    }
}
