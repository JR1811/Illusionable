package net.shirojr.illusionable.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundEvent;
import net.minecraft.world.World;
import net.shirojr.illusionable.cca.component.IllusionComponent;
import net.shirojr.illusionable.init.IllusionableGameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow
    public abstract World getWorld();

    @WrapOperation(method = "spawnSprintingParticles", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)V"))
    private void avoidSprintingParticlesForIllusion(World instance, ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Operation<Void> original) {
        Entity entity = (Entity) (Object) this;
        if (!isIllusion(entity)) {
            original.call(instance, parameters, x, y, z, velocityX, velocityY, velocityZ);
        }
    }

    @WrapOperation(method = "playStepSound", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;playSound(Lnet/minecraft/sound/SoundEvent;FF)V"))
    private void avoidStepSound(Entity instance, SoundEvent sound, float volume, float pitch, Operation<Void> original) {
        if (!isIllusion(instance)) {
            original.call(instance, sound, volume, pitch);
        }
    }

    @Inject(method = "playCombinationStepSounds", at = @At("HEAD"), cancellable = true)
    private void avoidIllusionCombinedStepSounds(BlockState primaryState, BlockState secondaryState, CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if (isIllusion(entity)) {
            ci.cancel();
        }
    }

    @Inject(method = "pushAwayFrom", at = @At(value = "HEAD"), cancellable = true)
    private void preventIllusionPushing(Entity other, CallbackInfo ci) {
        if (IllusionableGameRules.ILLUSIONS_PUSH_ENTITIES.get(getWorld())) {
            return;
        }

        if ((Entity) (Object) this instanceof LivingEntity livingSelf) {
            IllusionComponent selfComponent = IllusionComponent.fromEntity(livingSelf);
            if (selfComponent.isIllusion()) {
                ci.cancel();
            }
        } else if (other instanceof LivingEntity livingOther) {
            IllusionComponent otherComponent = IllusionComponent.fromEntity(livingOther);
            if (otherComponent.isIllusion()) {
                ci.cancel();
            }
        }
    }

    @Unique
    private boolean isIllusion(Entity entity) {
        return entity instanceof LivingEntity livingEntity && IllusionComponent.fromEntity(livingEntity).isIllusion();
    }
}
