package net.shirojr.illusionable.init;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.shirojr.illusionable.event.SharedEventObjects;

public interface IllusionableClientEvents {
    static void initializeClient() {
        WorldRenderEvents.AFTER_ENTITIES.register(SharedEventObjects.RENDER_EVENTS);
    }
}
