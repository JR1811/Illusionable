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
    private IconRendering iconRendering;

    public IllusionComponentImpl(LivingEntity entity) {
        this.entity = entity;
        this.isIllusion = false;
        this.targets = new HashSet<>();
        this.iconRendering = new IconRendering(true, 0.25, 30);
    }

    @Override
    public LivingEntity getEntity() {
        return this.entity;
    }

    @Override
    public boolean isIllusion() {
        return this.isIllusion;
    }

    @Override
    public void setIllusionState(boolean isIllusion, boolean sync) {
        this.isIllusion = isIllusion;
        if (!this.entity.getWorld().isClient()) {
            this.entity.setSilent(isIllusion);
        }
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

    @Override
    public boolean isTargeting(LivingEntity other) {
        return this.targets.contains(other.getUuid());
    }

    public IconRendering modifyIconRendering(Consumer<IconRendering> iconRendering, boolean shouldSync) {
        iconRendering.accept(this.iconRendering);
        if (shouldSync) this.sync();
        return this.iconRendering;
    }

    public IconRendering getIconRendering() {
        return iconRendering;
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

        IconRendering iconRendering = IconRendering.fromNbt(nbt);
        if (iconRendering != null) this.iconRendering = iconRendering;
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
        nbt.putBoolean("isIllusion", this.isIllusion);
        NbtList targetListNbt = new NbtList();
        for (UUID uuidEntry : this.targets) {
            targetListNbt.add(NbtString.of(uuidEntry.toString()));
        }
        nbt.put("illusionTargets", targetListNbt);

        this.iconRendering.toNbt(nbt);
    }
}
