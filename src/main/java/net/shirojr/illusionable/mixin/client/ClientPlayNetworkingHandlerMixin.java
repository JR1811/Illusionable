package net.shirojr.illusionable.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.shirojr.illusionable.cca.component.IllusionComponent;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Debug(export = true)
@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkingHandlerMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @ModifyExpressionValue(method = "onEntityStatusEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/packet/s2c/play/EntityStatusEffectS2CPacket;shouldShowParticles()Z"))
    private boolean preventIllusionParticles(boolean original, @Local Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) return original;
        IllusionComponent component = IllusionComponent.fromEntity(livingEntity);
        if (!component.isIllusion() || client.player == null || !component.isTargeting(client.player)) return original;

        return false;
    }
}
