package net.shirojr.illusionable.effect;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.shirojr.illusionable.network.packet.ObfuscatedCacheUpdatePacket;

public class ObfuscatedStatusEffect extends StatusEffect {
    public ObfuscatedStatusEffect(StatusEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void onApplied(LivingEntity entity, int amplifier) {
        super.onApplied(entity, amplifier);
        if (entity.getWorld().isClient() || entity.getServer() == null) return;
        new ObfuscatedCacheUpdatePacket(entity.getUuid(), true).sendPacket(PlayerLookup.all(entity.getServer()));
    }
}
