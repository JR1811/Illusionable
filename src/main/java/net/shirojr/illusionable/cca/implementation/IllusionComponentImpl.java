package net.shirojr.illusionable.cca.implementation;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.shirojr.illusionable.cca.IllusionableComponents;
import net.shirojr.illusionable.cca.component.IllusionComponent;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class IllusionComponentImpl implements IllusionComponent, AutoSyncedComponent {
    private final LivingEntity entity;
    private boolean isIllusion;
    private final HashSet<UUID> targets;

    public IllusionComponentImpl(LivingEntity entity) {
        this.entity = entity;
        this.isIllusion = false;
        this.targets = new HashSet<>();
    }

    @Override
    public LivingEntity getEntity() {
        return entity;
    }

    @Override
    public boolean isIllusion() {
        return isIllusion;
    }

    @Override
    public void setIllusionState(boolean isIllusion, boolean sync) {
        this.isIllusion = isIllusion;
        if (sync) {
            IllusionableComponents.ILLUSION_DATA.sync(this.entity);
        }
    }

    @Override
    public Set<UUID> getTargets() {
        return Collections.unmodifiableSet(this.targets);
    }

    @Override
    public void modifyTargets(Consumer<HashSet<UUID>> consumer, boolean sync) {
        consumer.accept(this.targets);
        if (sync) {
            IllusionableComponents.ILLUSION_DATA.sync(this.entity);
        }
    }

    @SuppressWarnings("unused")
    public boolean isTarget(LivingEntity other) {
        return this.targets.contains(other.getUuid());
    }

    @Override
    public void readFromNbt(NbtCompound nbt) {
        setIllusionState(nbt.getBoolean("isIllusion"), false);
        modifyTargets(targets -> {
            targets.clear();
            NbtList illusionTargets = nbt.getList("illusionTargets", NbtElement.STRING_TYPE);
            for (NbtElement nbtElement : illusionTargets) {
                targets.add(UUID.fromString(nbtElement.asString()));
            }
        }, false);
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
        nbt.putBoolean("isIllusion", this.isIllusion);
        NbtList targetListNbt = new NbtList();
        for (UUID uuidEntry : this.targets) {
            targetListNbt.add(NbtString.of(uuidEntry.toString()));
        }
        nbt.put("illusionTargets", targetListNbt);
    }
}
