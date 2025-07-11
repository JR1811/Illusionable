package net.shirojr.illusionable.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import net.shirojr.illusionable.cca.component.IllusionComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixin {
    @WrapOperation(method = "spawnSprintingParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"))
    private void avoidSprintingParticlesForIllusion(World instance, ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Operation<Void> original) {
        Entity entity = (Entity) (Object) this;
        boolean isIllusion = entity instanceof LivingEntity livingEntity && IllusionComponent.fromEntity(livingEntity).isIllusion();
        if (!isIllusion) {
            original.call(instance, parameters, x, y, z, velocityX, velocityY, velocityZ);
        }
    }

    @WrapOperation(method = "playStepSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;playSound(Lnet/minecraft/sound/SoundEvent;FF)V"))
    private void avoidStepSound(Entity instance, SoundEvent sound, float volume, float pitch, Operation<Void> original) {
        boolean isIllusion = instance instanceof LivingEntity livingEntity && IllusionComponent.fromEntity(livingEntity).isIllusion();
        if (!isIllusion) {
            original.call(instance, sound, volume, pitch);
        }
    }

    @Inject(method = "isSilent", at = @At("HEAD"), cancellable = true)
    private void avoidIllusionSound(CallbackInfoReturnable<Boolean> cir) {
        Entity entity = (Entity) (Object) this;
        boolean isIllusion = entity instanceof LivingEntity livingEntity && IllusionComponent.fromEntity(livingEntity).isIllusion();
        if (isIllusion) {
            cir.setReturnValue(true);
        }
    }
}
