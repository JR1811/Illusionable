package net.shirojr.illusionable.cca;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.scoreboard.ScoreboardComponentInitializer;
import net.minecraft.entity.LivingEntity;
import net.shirojr.illusionable.cca.component.IllusionComponent;
import net.shirojr.illusionable.cca.component.ObfuscationComponent;
import net.shirojr.illusionable.cca.implementation.IllusionComponentImpl;
import net.shirojr.illusionable.cca.implementation.ObfuscationComponentImpl;

public class IllusionableComponents implements EntityComponentInitializer, ScoreboardComponentInitializer {
    public static final ComponentKey<IllusionComponent> ILLUSION_DATA = ComponentRegistry.getOrCreate(IllusionComponent.KEY, IllusionComponent.class);
    public static final ComponentKey<ObfuscationComponent> OBFUSCATION_DATA = ComponentRegistry.getOrCreate(ObfuscationComponent.KEY, ObfuscationComponent.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerFor(LivingEntity.class, ILLUSION_DATA, IllusionComponentImpl::new);
    }

    @Override
    public void registerScoreboardComponentFactories(ScoreboardComponentFactoryRegistry registry) {
        registry.registerScoreboardComponent(OBFUSCATION_DATA, ObfuscationComponentImpl::new);
    }
}
