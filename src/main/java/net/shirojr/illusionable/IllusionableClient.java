package net.shirojr.illusionable;

import net.fabricmc.api.ClientModInitializer;
import net.shirojr.illusionable.init.IllusionableClientEvents;
import net.shirojr.illusionable.network.IllusionableS2CNetworking;

public class IllusionableClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        IllusionableClientEvents.initializeClient();
        IllusionableS2CNetworking.initialize();
    }
}
