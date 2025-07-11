package net.shirojr.illusionable.init;

import net.shirojr.illusionable.event.CommandRegistrationEvents;

public class IllusionableEvents {
    public static void initializeCommon() {
        CommandRegistrationEvents.register();
    }
}
