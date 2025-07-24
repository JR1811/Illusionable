package net.shirojr.illusionable.init;

import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.shirojr.illusionable.Illusionable;

public interface IllusionableParticleTypes {
    DefaultParticleType GLYPHS = register("glyphs", true);

    @SuppressWarnings("SameParameterValue")
    private static DefaultParticleType register(String name, boolean alwaysShow) {
        return Registry.register(Registries.PARTICLE_TYPE, Illusionable.getId(name), FabricParticleTypes.simple(alwaysShow));
    }

    static void initialize() {
        // static initialisation
    }
}
