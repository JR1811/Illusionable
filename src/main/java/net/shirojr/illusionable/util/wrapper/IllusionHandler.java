package net.shirojr.illusionable.util.wrapper;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;

import java.util.HashSet;
import java.util.UUID;
import java.util.function.Consumer;

public interface IllusionHandler {
    boolean illusionable$isIllusion();

    void illusionable$setIllusion(boolean isIllusion);

    HashSet<UUID> illusionable$getPersistentIllusionTargets();

    HashSet<Entity> illusionable$getIllusionTargets();

    void illusionable$modifyIllusionTargets(Consumer<HashSet<UUID>> newList, boolean sendClientUpdate);

    default void illusionable$modifyIllusionTargets(Consumer<HashSet<UUID>> newList) {
        illusionable$modifyIllusionTargets(newList, true);
    }

    void illusionable$clearIllusionTargets();

    void illusionable$updateClients();

    void illusionable$updateTrackedEntityIds(ServerWorld world);
}
