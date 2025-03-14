package net.shirojr.illusionable.init;

import net.shirojr.illusionable.event.CommandRegistrationEvents;
import net.shirojr.illusionable.event.ServerConnectionEvents;

public class IllusionableEvents {
    public static void initializeCommon() {
        CommandRegistrationEvents.register();
        ServerConnectionEvents.register();
    }
}
