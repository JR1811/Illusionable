package net.shirojr.illusionable.cca.component;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.shirojr.illusionable.Illusionable;
import net.shirojr.illusionable.cca.IllusionableComponents;
import org.jetbrains.annotations.Nullable;

import java.util.Deque;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public interface DamageDistributionComponent extends Component, ServerTickingComponent {
    Identifier KEY = Illusionable.getId("damage_distribution");

    static DamageDistributionComponent fromEntity(LivingEntity entity) {
        return IllusionableComponents.DAMAGE_DISTRIBUTION.get(entity);
    }

    Set<UUID> getLinkedTargets();

    void modifyLinkedEntities(Consumer<Deque<UUID>> consumer, boolean shouldSync);

    boolean isEmpty();

    double getRange();

    void setRange(double range, boolean shouldSync);

    long getDuration();

    void setDuration(long duration, boolean shouldSync);

    @Nullable UUID getAggressor();

    void setAggressor(@Nullable UUID aggressor, boolean shouldSync);

    float getAppliedDamageMultiplier();

    void setAppliedDamageMultiplier(float multiplier);

    /**
     * Distributes damage to all linked entities
     *
     * @param damageSource   incoming damage type
     * @param incomingDamage incoming damage
     * @return left-over damage if linked entities weren't enough
     */
    float distributeDamage(ServerWorld world, DamageSource damageSource, float incomingDamage);

    void start(List<LivingEntity> targets, long duration, double range);

    @SuppressWarnings("unused")
    void stop();

    void sync();
}
