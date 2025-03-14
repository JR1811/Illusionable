package net.shirojr.illusionable.init;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.shirojr.illusionable.network.packet.ObfuscatedCacheInitPacket;
import net.shirojr.illusionable.network.packet.ObfuscatedCacheUpdatePacket;
import net.shirojr.illusionable.network.packet.IllusionsCacheUpdatePacket;

public class IllusionableNetworkPayloads {
    static {
        registerS2C(IllusionsCacheUpdatePacket.IDENTIFIER, IllusionsCacheUpdatePacket.CODEC);
        registerS2C(ObfuscatedCacheInitPacket.IDENTIFIER, ObfuscatedCacheInitPacket.CODEC);
        registerS2C(ObfuscatedCacheUpdatePacket.IDENTIFIER, ObfuscatedCacheUpdatePacket.CODEC);
    }


    private static <T extends CustomPayload> void registerS2C(CustomPayload.Id<T> packetIdentifier, PacketCodec<RegistryByteBuf, T> codec) {
        PayloadTypeRegistry.playS2C().register(packetIdentifier, codec);
    }

    private static <T extends CustomPayload> void registerC2S(CustomPayload.Id<T> packetIdentifier, PacketCodec<RegistryByteBuf, T> codec) {
        PayloadTypeRegistry.playC2S().register(packetIdentifier, codec);
    }

    public static void initialize() {
        // static initialisation
    }
}
