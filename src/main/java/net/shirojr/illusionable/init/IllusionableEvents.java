package net.shirojr.illusionable.init;

import net.shirojr.illusionable.event.CommandRegistrationEvents;

public interface IllusionableEvents {
    static void initializeCommon() {
        CommandRegistrationEvents.register();
    }
}
