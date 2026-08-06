package net.shirojr.illusionable.init;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.shirojr.illusionable.command.CensoredCommand;
import net.shirojr.illusionable.command.DamageDistributionCommand;
import net.shirojr.illusionable.command.IllusionCommand;
import net.shirojr.illusionable.event.IllusionableConnectionEvents;
import net.shirojr.illusionable.event.IllusionableTrackingEvents;

public interface IllusionableCommonEvents {
    IllusionableTrackingEvents TRACKING_EVENTS = new IllusionableTrackingEvents();
    IllusionableConnectionEvents CONNECTION_EVENTS = new IllusionableConnectionEvents();

    static void initializeCommon() {
        CommandRegistrationCallback.EVENT.register(new IllusionCommand());
        CommandRegistrationCallback.EVENT.register(new DamageDistributionCommand());
        CommandRegistrationCallback.EVENT.register(new CensoredCommand());
        EntityTrackingEvents.START_TRACKING.register(TRACKING_EVENTS);
        EntityTrackingEvents.STOP_TRACKING.register(TRACKING_EVENTS);
        ServerPlayConnectionEvents.JOIN.register(CONNECTION_EVENTS);
        ServerPlayConnectionEvents.DISCONNECT.register(CONNECTION_EVENTS);
    }
}
