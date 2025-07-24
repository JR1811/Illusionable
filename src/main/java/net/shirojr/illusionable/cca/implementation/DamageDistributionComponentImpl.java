package net.shirojr.illusionable.cca.implementation;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.shirojr.illusionable.cca.IllusionableComponents;
import net.shirojr.illusionable.cca.component.DamageDistributionComponent;
import net.shirojr.illusionable.init.IllusionableDamageTypes;
import net.shirojr.illusionable.init.IllusionableGameRules;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class DamageDistributionComponentImpl implements DamageDistributionComponent, AutoSyncedComponent {
    private final LivingEntity provider;

    private final Deque<UUID> linkedTargets;
    private long duration;
    private double range;
    private float multiplier;

    @Nullable
    private UUID aggressor;

    public DamageDistributionComponentImpl(LivingEntity provider) {
        this.provider = provider;
        this.linkedTargets = new ArrayDeque<>();
        this.range = 32;
        this.duration = 0;

        this.aggressor = null;
        this.multiplier = 0.8f;
    }

    @Override
    public Set<UUID> getLinkedTargets() {
        return Set.copyOf(linkedTargets);
    }

    @Override
    public void modifyLinkedEntities(Consumer<Deque<UUID>> consumer, boolean shouldSync) {
        consumer.accept(this.linkedTargets);
        if (shouldSync) {
            this.sync();
        }
    }

    @Override
    public boolean isEmpty() {
        return this.linkedTargets.isEmpty();
    }

    @Override
    public double getRange() {
        return range;
    }

    @Override
    public void setRange(double range, boolean shouldSync) {
        this.range = Math.max(0, range);
        if (shouldSync) {
            sync();
        }
    }

    @Override
    public long getDuration() {
        return this.duration;
    }

    /**
     * @param duration if <code>-1</code> duration is infinite
     */
    @Override
    public void setDuration(long duration, boolean shouldSync) {
        this.duration = duration == -1 ? -1 : Math.max(0, duration);
        if (shouldSync) {
            sync();
        }
    }

    @Override
    @Nullable
    public UUID getAggressor() {
        return aggressor;
    }

    @Override
    public void setAggressor(@Nullable UUID aggressor, boolean shouldSync) {
        this.aggressor = aggressor;
        if (shouldSync) {
            sync();
        }
    }

    @Override
    public float getAppliedDamageMultiplier() {
        return multiplier;
    }

    @Override
    public void setAppliedDamageMultiplier(float multiplier) {
        this.multiplier = multiplier;
    }

    @Override
    public float distributeDamage(ServerWorld world, DamageSource damageSource, float incomingDamage) {
        float damageLeft = incomingDamage;
        if (isEmpty() || damageSource.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return damageLeft;
        }
        if (damageSource.isOf(IllusionableDamageTypes.LINKED_DAMAGE.get()) && !world.getGameRules().getBoolean(IllusionableGameRules.LINKED_DAMAGE_CHAIN)) {
            return damageLeft;
        }
        Iterator<UUID> iterator = linkedTargets.iterator();
        while (iterator.hasNext()) {
            if (damageLeft <= 0) break;
            Entity entity = world.getEntity(iterator.next());
            if (!(entity instanceof LivingEntity livingEntity) || livingEntity.isDead()) {
                iterator.remove();
                continue;
            }
            if (livingEntity.isInvulnerableTo(damageSource)) {
                continue;
            }
            float damage = Math.min(damageLeft, livingEntity.getHealth());
            boolean wasDamaged = livingEntity.damage(IllusionableDamageTypes.of(world, IllusionableDamageTypes.LINKED_DAMAGE.get()),
                    damage * getAppliedDamageMultiplier());
            if (!wasDamaged) continue;
            livingEntity.playSound(SoundEvents.ENTITY_ALLAY_DEATH, 1.0f, 0.8f);
            damageLeft = Math.max(0, damageLeft - damage);
            if (livingEntity.isDead()) {
                iterator.remove();
            }
        }
        sync();
        return damageLeft;
    }

    @Override
    public void start(List<LivingEntity> targets, long duration, double range) {
        modifyLinkedEntities(uuids -> {
            uuids.clear();
            uuids.addAll(targets.stream().map(Entity::getUuid).toList());
        }, false);
        setDuration(duration, false);
        setRange(range, false);
        sync();
    }

    @Override
    public void stop() {
        setDuration(0, false);
        modifyLinkedEntities(Collection::clear, false);
        sync();
    }

    @Override
    public void sync() {
        IllusionableComponents.DAMAGE_DISTRIBUTION.sync(provider);
        if (!(provider.getWorld() instanceof ServerWorld world)) return;
        for (UUID linkedTargetUuid : getLinkedTargets()) {
            if (!(world.getEntity(linkedTargetUuid) instanceof LivingEntity target)) continue;
            DamageDistributionComponent targetComponent = DamageDistributionComponent.fromEntity(target);
            targetComponent.setAggressor(provider.getUuid(), true);
        }
    }

    @Override
    public void readFromNbt(NbtCompound nbt) {
        if (nbt.contains("duration")) {
            setDuration(nbt.getLong("duration"), false);
        }
        if (nbt.contains("range")) {
            setRange(nbt.getDouble("range"), false);
        }
        if (nbt.contains("damageTraders")) {
            NbtList damageTradeNbtList = nbt.getList("damageTraders", NbtElement.STRING_TYPE);
            for (NbtElement nbtElement : damageTradeNbtList) {
                UUID entry = UUID.fromString(nbtElement.asString());
                this.linkedTargets.addLast(entry);
            }
        }
        setAggressor(nbt.contains("aggressor") ? nbt.getUuid("aggressor") : null, false);
        this.sync();
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
        nbt.putLong("duration", getDuration());
        nbt.putDouble("range", getRange());
        NbtList damageTradeNbtList = new NbtList();
        for (UUID linkedEntity : this.getLinkedTargets()) {
            damageTradeNbtList.add(NbtString.of(linkedEntity.toString()));
        }
        nbt.put("damageTraders", damageTradeNbtList);
        UUID aggressorUuid = getAggressor();
        if (aggressorUuid == null) nbt.remove("aggressor");
        else nbt.putUuid("aggressor", aggressorUuid);
    }

    @Override
    public void serverTick() {
        if (!(provider.getWorld() instanceof ServerWorld world) || isEmpty()) return;
        HashSet<UUID> toBeRemoved = new HashSet<>();
        if (getDuration() == 0) {
            toBeRemoved.addAll(getLinkedTargets());
        } else {
            for (UUID linkedEntityUuid : getLinkedTargets()) {
                if (!(world.getEntity(linkedEntityUuid) instanceof LivingEntity target)) {
                    toBeRemoved.add(linkedEntityUuid);
                    continue;
                }
                if (provider.squaredDistanceTo(target) > range * range) {
                    toBeRemoved.add(linkedEntityUuid);
                }
            }
        }
        if (toBeRemoved.isEmpty()) return;
        modifyLinkedEntities(uuids -> uuids.removeIf(toBeRemoved::contains), true);
        if (getDuration() > 0) {
            setDuration(getDuration() - 1, false);
        }
    }
}
