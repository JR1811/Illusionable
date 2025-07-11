package net.shirojr.illusionable.init;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.shirojr.illusionable.Illusionable;
import net.shirojr.illusionable.effect.ObfuscatedStatusEffect;

public interface IllusionableStatusEffects {
    RegistryEntry<StatusEffect> OBFUSCATED = register("obfuscated", new ObfuscatedStatusEffect(
            StatusEffectCategory.BENEFICIAL, 0xbdbdbd)
    );

    @SuppressWarnings("SameParameterValue")
    private static RegistryEntry<StatusEffect> register(String name, StatusEffect entry) {
        return Registry.registerReference(Registries.STATUS_EFFECT, Illusionable.getId(name), entry);
    }

    static void initialize() {
        // static initialisation
    }
}
