package net.shirojr.illusionable.cca.component;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.entity.LivingEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.Identifier;
import net.shirojr.illusionable.Illusionable;
import net.shirojr.illusionable.cca.IllusionableComponents;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public interface ObfuscationComponent extends Component {
    Identifier KEY = Illusionable.getId("obfuscation");

    static ObfuscationComponent fromEntity(LivingEntity entity) {
        return fromProvider(entity.getWorld().getScoreboard());
    }

    static ObfuscationComponent fromProvider(Scoreboard scoreboard) {
        return IllusionableComponents.OBFUSCATION_DATA.get(scoreboard);
    }

    @SuppressWarnings("unused")
    Map<UUID, Boolean> getObfuscationData();

    void modifyObfuscationData(Consumer<HashMap<UUID, Boolean>> consumer, boolean sync);

    boolean isObfuscated(UUID uuid);

    @SuppressWarnings("unused")
    default boolean isObfuscated(LivingEntity entity) {
        return isObfuscated(entity.getUuid());
    }

    void setObfuscated(UUID uuid, boolean obfuscated, boolean sync);

    default void setObfuscated(LivingEntity entity, boolean obfuscated, boolean sync) {
        setObfuscated(entity.getUuid(), obfuscated, sync);
    }
}
