package net.shirojr.illusionable.event;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.shirojr.illusionable.init.IllusionableStatusEffects;
import net.shirojr.illusionable.network.packet.IllusionsCacheUpdatePacket;
import net.shirojr.illusionable.network.packet.ObfuscatedCacheInitPacket;
import net.shirojr.illusionable.network.packet.ObfuscatedCacheUpdatePacket;
import net.shirojr.illusionable.util.wrapper.IllusionHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public class ServerConnectionEvents {
    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerConnectionEvents.handleObfuscation(handler, sender, server);
            ServerConnectionEvents.handleIllusions(handler, sender, server);
        });
    }

    private static void handleIllusions(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        ServerPlayerEntity player = handler.getPlayer();
        if (!(player instanceof IllusionHandler illusionable)) return;
        List<ServerPlayerEntity> players = new ArrayList<>(PlayerLookup.tracking(player));
        players.add(player);
        new IllusionsCacheUpdatePacket(
                handler.getPlayer().getId(),
                illusionable.illusionable$getPersistentIllusionTargets().contains(player.getUuid()),
                illusionable.illusionable$isIllusion()
        ).sendPacket(players);
    }

    private static void handleObfuscation(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        List<ServerPlayerEntity> players = new ArrayList<>(PlayerLookup.all(server));
        players.add(handler.getPlayer());
        new ObfuscatedCacheUpdatePacket(
                handler.getPlayer().getUuid(),
                handler.getPlayer().hasStatusEffect(IllusionableStatusEffects.OBFUSCATED)
        ).sendPacket(players);

        HashMap<UUID, Boolean> obfuscatedEntities = new HashMap<>();
        for (ServerPlayerEntity entry : PlayerLookup.all(server)) {
            obfuscatedEntities.put(entry.getUuid(), entry.hasStatusEffect(IllusionableStatusEffects.OBFUSCATED));
        }
        new ObfuscatedCacheInitPacket(obfuscatedEntities).sendPacket(handler.getPlayer());
    }
}
