package net.shirojr.illusionable.init;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.shirojr.illusionable.event.IllusionableRenderEvents;

public interface IllusionableClientEvents {
    IllusionableRenderEvents RENDER_EVENTS = new IllusionableRenderEvents();

    static void initializeClient() {
        WorldRenderEvents.AFTER_ENTITIES.register(RENDER_EVENTS);
        WorldRenderEvents.LAST.register(RENDER_EVENTS);
    }
}
