package net.shirojr.illusionable.init;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

public interface IllusionableParticles {
    static void initialize() {
        ParticleFactoryRegistry.getInstance().register(IllusionableParticleTypes.GLYPHS, );
    }
}
