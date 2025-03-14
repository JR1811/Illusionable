package net.shirojr.illusionable.init;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.shirojr.illusionable.network.packet.ObfuscatedCacheInitPacket;
import net.shirojr.illusionable.network.packet.ObfuscatedCacheUpdatePacket;
import net.shirojr.illusionable.network.packet.IllusionsCacheUpdatePacket;

public class IllusionableNetworkingS2C {
    static {
        ClientPlayNetworking.registerGlobalReceiver(IllusionsCacheUpdatePacket.IDENTIFIER, IllusionsCacheUpdatePacket::handlePacket);
        ClientPlayNetworking.registerGlobalReceiver(ObfuscatedCacheInitPacket.IDENTIFIER, ObfuscatedCacheInitPacket::handlePacket);
        ClientPlayNetworking.registerGlobalReceiver(ObfuscatedCacheUpdatePacket.IDENTIFIER, ObfuscatedCacheUpdatePacket::handlePacket);
    }

    public static void initialize() {
        // static initialisation
    }
}
