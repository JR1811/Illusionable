package net.shirojr.illusionable.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.entity.Attackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.world.World;
import net.shirojr.illusionable.cca.component.IllusionComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Attackable {
    @Shadow
    protected abstract void clearPotionSwirls();

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "canTarget(Lnet/minecraft/entity/LivingEntity;)Z", at = @At("HEAD"), cancellable = true)
    private void avoidTargetingIllusions(LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        IllusionComponent targetIllusionComponent = IllusionComponent.fromEntity(target);
        IllusionComponent selfIllusionComponent = IllusionComponent.fromEntity(self);

        if (selfIllusionComponent.isIllusion() && !selfIllusionComponent.getTargets().contains(target.getUuid())) {
            cir.setReturnValue(false);
            return;
        }
        if (targetIllusionComponent.isIllusion() && !targetIllusionComponent.getTargets().contains(self.getUuid())) {
            cir.setReturnValue(false);
        }
    }

    @WrapOperation(method = "updatePotionVisibility", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/data/DataTracker;set(Lnet/minecraft/entity/data/TrackedData;Ljava/lang/Object;)V"))
    private <T> void disablePotionSwirlsForIllusions(DataTracker instance, TrackedData<T> key, T value, Operation<Void> original) {
        LivingEntity entity = (LivingEntity) (Object) this;
        IllusionComponent illusionComponent = IllusionComponent.fromEntity(entity);
        if (!illusionComponent.isIllusion()) {
            original.call(instance, key, value);
            return;
        }
        clearPotionSwirls();
    }
}
