package net.shirojr.illusionable.init;

import net.minecraft.entity.damage.DamageScaling;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.world.World;
import net.shirojr.illusionable.Illusionable;

import java.util.HashMap;
import java.util.List;

public interface IllusionableDamageTypes {
    HashMap<RegistryKey<DamageType>, DamageTypePair> ALL_DAMAGE_TYPES = new HashMap<>();

    DamageTypePair LINKED_DAMAGE = register(new DamageType("linked_damage", DamageScaling.NEVER, 0.1f),
            List.of(DamageTypeTags.BYPASSES_ARMOR, DamageTypeTags.BYPASSES_SHIELD, DamageTypeTags.BYPASSES_COOLDOWN,
                    DamageTypeTags.WITCH_RESISTANT_TO, DamageTypeTags.AVOIDS_GUARDIAN_THORNS)
    );


    private static DamageTypePair register(DamageType type, List<TagKey<DamageType>> tags) {
        DamageTypePair damageTypePair = new DamageTypePair(type, tags);
        ALL_DAMAGE_TYPES.put(damageTypePair.get(), damageTypePair);
        return damageTypePair;
    }

    static DamageSource of(World world, RegistryKey<DamageType> key) {
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key));
    }

    static void bootstrap(Registerable<DamageType> registerable) {
        for (var entry : ALL_DAMAGE_TYPES.entrySet()) {
            registerable.register(entry.getKey(), entry.getValue().instance);
        }
    }

    record DamageTypePair(DamageType instance, List<TagKey<DamageType>> tags) {
        public RegistryKey<DamageType> get() {
            return RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Illusionable.getId(instance.msgId()));
        }
    }

    static void initialize() {
        // static initialisation
    }
}
