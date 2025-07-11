package net.shirojr.illusionable.effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.shirojr.illusionable.cca.component.ObfuscationComponent;

public class ObfuscatedStatusEffect extends StatusEffect {
    public ObfuscatedStatusEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void onApplied(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onApplied(entity, attributes, amplifier);
        ObfuscationComponent obfuscationComponent = ObfuscationComponent.fromEntity(entity);
        obfuscationComponent.setObfuscated(entity, true, true);
    }

    @Override
    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        super.onRemoved(entity, attributes, amplifier);
        ObfuscationComponent obfuscationComponent = ObfuscationComponent.fromEntity(entity);
        obfuscationComponent.setObfuscated(entity, false, true);
    }

}
