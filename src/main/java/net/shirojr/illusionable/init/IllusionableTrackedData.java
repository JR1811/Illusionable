package net.shirojr.illusionable.init;

import net.minecraft.entity.Entity;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.List;

public class IllusionableTrackedData {
    public static final TrackedDataHandler<List<Integer>> ENTITY_LIST = TrackedDataHandler.create(PacketCodecs.VAR_INT.collect(PacketCodecs.toList()));

    public static HashSet<Entity> resolveEntityIds(World world, HashSet<Integer> entityIds) {
        HashSet<Entity> entities = new HashSet<>();
        for (int entry : entityIds) {
            entities.add(world.getEntityById(entry));
        }
        return entities;
    }

    public static void initialize() {
        TrackedDataHandlerRegistry.register(ENTITY_LIST);
    }
}
