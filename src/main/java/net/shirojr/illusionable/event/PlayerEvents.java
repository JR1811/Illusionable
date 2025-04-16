package net.shirojr.illusionable.event;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.server.network.ServerPlayerEntity;
import net.shirojr.illusionable.init.IllusionableStatusEffects;
import net.shirojr.illusionable.network.packet.ObfuscatedCacheUpdatePacket;
import net.shirojr.illusionable.util.wrapper.IllusionHandler;

public class PlayerEvents {
    public static void register() {
        ServerPlayerEvents.AFTER_RESPAWN.register(PlayerEvents::copyData);
    }

    private static void copyData(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        if (!(oldPlayer instanceof IllusionHandler oldIllusion) || !(newPlayer instanceof IllusionHandler newIllusion)) {
            return;
        }
        if (newPlayer.getServer() == null) {
            return;
        }

        new ObfuscatedCacheUpdatePacket(oldPlayer.getUuid(), false).sendPacket(PlayerLookup.all(newPlayer.getServer()));
        new ObfuscatedCacheUpdatePacket(oldPlayer.getUuid(), newPlayer.hasStatusEffect(IllusionableStatusEffects.OBFUSCATED)).sendPacket(PlayerLookup.all(newPlayer.getServer()));

        newIllusion.illusionable$setIllusion(oldIllusion.illusionable$isIllusion());
        newIllusion.illusionable$clearIllusionTargets();
        newIllusion.illusionable$modifyIllusionTargets(uuids ->
                uuids.addAll(oldIllusion.illusionable$getPersistentIllusionTargets()), true
        );
    }
}
