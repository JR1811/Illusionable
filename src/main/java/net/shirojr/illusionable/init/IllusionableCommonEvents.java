package net.shirojr.illusionable.init;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.shirojr.illusionable.command.DamageDistributionCommand;
import net.shirojr.illusionable.command.IllusionCommand;

public interface IllusionableCommonEvents {
    static void initializeCommon() {
        CommandRegistrationCallback.EVENT.register(new IllusionCommand());
        CommandRegistrationCallback.EVENT.register(new DamageDistributionCommand());
    }
}
