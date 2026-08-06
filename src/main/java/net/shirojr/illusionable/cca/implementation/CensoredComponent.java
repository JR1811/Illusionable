package net.shirojr.illusionable.cca.implementation;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.shirojr.illusionable.Illusionable;
import net.shirojr.illusionable.cca.IllusionableComponents;
import net.shirojr.illusionable.util.constant.IllusionableNbtKeys;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public class CensoredComponent implements Component, AutoSyncedComponent {
    public static final Identifier KEY = Illusionable.getId("censored");
    private final PlayerEntity player;

    private final Object2FloatOpenHashMap<UUID> scaledCensor;

    public CensoredComponent(PlayerEntity player) {
        this.player = player;
        this.scaledCensor = new Object2FloatOpenHashMap<>();
    }

    public static CensoredComponent get(PlayerEntity player) {
        return IllusionableComponents.CENSOR.get(player);
    }

    public boolean isCensored(Entity entity) {
        return this.scaledCensor.containsKey(entity.getUuid());
    }

    public boolean isEmpty() {
        return this.scaledCensor.isEmpty();
    }

    public void clear() {
        this.scaledCensor.clear();
        this.sync();
    }

    public void add(Collection<? extends Entity> targets, float scale) {
        targets.forEach(target -> {
            if (scale <= 0) {
                this.scaledCensor.removeFloat(target.getUuid());
            } else {
                this.scaledCensor.put(target.getUuid(), scale);
            }
        });
        this.sync();
    }

    @Nullable
    public Float get(Entity entity) {
        if (!this.scaledCensor.containsKey(entity.getUuid())) return null;
        return this.scaledCensor.getFloat(entity.getUuid());
    }

    public float getOrDefault(Entity entity, float defaultScale) {
        return this.scaledCensor.getOrDefault(entity.getUuid(), defaultScale);
    }

    public void set(Entity entity, float scale) {
        if (scale <= 0) {
            this.scaledCensor.removeFloat(entity.getUuid());
        } else {
            this.scaledCensor.put(entity.getUuid(), scale);
        }
        this.sync();
    }

    public void remove(Collection<? extends Entity> entities) {
        for (Entity entity : entities) {
            if (!this.scaledCensor.containsKey(entity.getUuid())) continue;
            this.set(entity, 0f);
        }
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        if (tag.contains(IllusionableNbtKeys.CENSORED)) {
            this.scaledCensor.clear();
            NbtCompound censoredNbt = tag.getCompound(IllusionableNbtKeys.CENSORED);
            for (String entryKey : censoredNbt.getKeys()) {
                UUID targetUuid = UUID.fromString(entryKey);
                float scale = censoredNbt.getFloat(entryKey);
                this.scaledCensor.put(targetUuid, scale);
            }
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        NbtCompound censoredNbt = new NbtCompound();
        this.scaledCensor.forEach((targetUuid, scale) ->
                censoredNbt.putFloat(targetUuid.toString(), scale)
        );
        tag.put(IllusionableNbtKeys.CENSORED, censoredNbt);
    }

    @SuppressWarnings("unused")
    public static void onRespawn(CensoredComponent from, CensoredComponent to, boolean lossless, boolean keepInventory, boolean sameCharacter) {
        to.scaledCensor.putAll(from.scaledCensor);
        to.sync();
    }

    public void sync() {
        if (!(this.player.getWorld() instanceof ServerWorld)) return;
        IllusionableComponents.CENSOR.sync(this.player);
    }
}
