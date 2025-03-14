package net.shirojr.illusionable.event;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.shirojr.illusionable.init.IllusionableStatusEffects;
import net.shirojr.illusionable.network.packet.ObfuscatedCacheInitPacket;
import net.shirojr.illusionable.network.packet.ObfuscatedCacheUpdatePacket;

import java.util.HashMap;
import java.util.UUID;

public class ServerConnectionEvents {
    @SuppressWarnings("Convert2MethodRef")
    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerConnectionEvents.handleObfuscation(handler, sender, server);
            //ServerConnectionEvents.handleIllusions(handler, sender, server);
        });
    }

    private static void handleObfuscation(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        new ObfuscatedCacheUpdatePacket(
                handler.getPlayer().getUuid(),
                handler.getPlayer().hasStatusEffect(IllusionableStatusEffects.OBFUSCATED)
        ).sendPacket(PlayerLookup.all(server));

        HashMap<UUID, Boolean> obfuscatedEntities = new HashMap<>();
        for (ServerPlayerEntity entry : PlayerLookup.all(server)) {
            obfuscatedEntities.put(entry.getUuid(), entry.hasStatusEffect(IllusionableStatusEffects.OBFUSCATED));
        }
        new ObfuscatedCacheInitPacket(obfuscatedEntities).sendPacket(handler.getPlayer());
    }
}
