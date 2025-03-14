package net.shirojr.illusionable.util.wrapper;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public interface IllusionHandler {
    boolean illusionable$isIllusion();

    void illusionable$setIllusion(boolean isIllusion);

    List<UUID> illusionable$getPersistentIllusionTargets();

    List<Entity> illusionable$getIllusionTargets();

    void illusionable$modifyIllusionTargets(Consumer<List<UUID>> newList, boolean sendClientUpdate);

    default void illusionable$modifyIllusionTargets(Consumer<List<UUID>> newList) {
        illusionable$modifyIllusionTargets(newList, true);
    }

    void illusionable$clearIllusionTargets();

    void illusionable$updateClients();

    void illusionable$updateTrackedEntityIds(ServerWorld world);
}
