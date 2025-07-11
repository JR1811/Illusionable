package net.shirojr.illusionable.cca.component;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.shirojr.illusionable.Illusionable;
import net.shirojr.illusionable.cca.IllusionableComponents;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public interface IllusionComponent extends Component {
    Identifier KEY = Illusionable.getId("illusion");

    static IllusionComponent fromEntity(LivingEntity entity) {
        return IllusionableComponents.ILLUSION_DATA.get(entity);
    }

    LivingEntity getEntity();

    boolean isIllusion();

    void setIllusionState(boolean isIllusion, boolean sync);

    Set<UUID> getTargets();

    void modifyTargets(Consumer<HashSet<UUID>> consumer, boolean sync);
}
