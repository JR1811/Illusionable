package net.shirojr.illusionable.network.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Uuids;
import net.shirojr.illusionable.Illusionable;
import net.shirojr.illusionable.IllusionableClient;

import java.util.HashMap;
import java.util.UUID;

public record ObfuscatedCacheInitPacket(HashMap<UUID, Boolean> obfuscatedEntityList) implements CustomPayload {
    public static final Id<ObfuscatedCacheInitPacket> IDENTIFIER = new Id<>(Illusionable.getId("obfuscated_entity_cache_init"));

    public static final PacketCodec<RegistryByteBuf, ObfuscatedCacheInitPacket> CODEC = PacketCodec.tuple(
            PacketCodecs.map(HashMap::new, Uuids.PACKET_CODEC, PacketCodecs.BOOL), ObfuscatedCacheInitPacket::obfuscatedEntityList,
            ObfuscatedCacheInitPacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return IDENTIFIER;
    }

    public void sendPacket(ServerPlayerEntity player) {
        if (player.networkHandler == null) return;
        ServerPlayNetworking.send(player, this);
    }

    public void handlePacket(ClientPlayNetworking.Context context) {
        ClientPlayerEntity player = context.player();
        if (player == null) return;
        IllusionableClient.OBFUSCATED_CACHE.putAll(obfuscatedEntityList);
        // IllusionableClient.OBFUSCATED_CACHE.put(player.getUuid(), player.hasStatusEffect(IllusionableStatusEffects.OBFUSCATED));
    }
}
