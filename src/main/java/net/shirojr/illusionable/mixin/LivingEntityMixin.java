package net.shirojr.illusionable.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Attackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shirojr.illusionable.init.IllusionableStatusEffects;
import net.shirojr.illusionable.init.IllusionableTrackedData;
import net.shirojr.illusionable.network.packet.IllusionsCacheUpdatePacket;
import net.shirojr.illusionable.network.packet.ObfuscatedCacheUpdatePacket;
import net.shirojr.illusionable.util.wrapper.IllusionHandler;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;
import java.util.function.Consumer;

@Debug(export = true)
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Attackable, IllusionHandler {
    @Shadow
    protected abstract void clearPotionSwirls();

    @Shadow
    protected abstract void updatePotionSwirls();

    @Shadow
    protected abstract void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition);

    @Shadow
    public abstract Map<RegistryEntry<StatusEffect>, StatusEffectInstance> getActiveStatusEffects();

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Unique
    private final HashSet<UUID> illusionTargetsPersistence = new HashSet<>();

    @Unique
    private final HashSet<Integer> illusionTargetsRuntime = new HashSet<>();

    @Unique
    private boolean isIllusion = false;

    @Inject(method = "canTarget(Lnet/minecraft/entity/LivingEntity;)Z", at = @At("HEAD"), cancellable = true)
    private void avoidTargetingIllusions(LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self instanceof IllusionHandler illusionSelf && illusionSelf.illusionable$isIllusion()) {
            if (!illusionSelf.illusionable$getIllusionTargets().contains(target)) {
                cir.setReturnValue(false);
                return;
            }
        }
        if (target instanceof IllusionHandler illusionTarget && illusionTarget.illusionable$isIllusion()) {
            if (!illusionTarget.illusionable$getIllusionTargets().contains(self)) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "onStatusEffectRemoved", at = @At("TAIL"))
    private void cleanUpObfuscation(StatusEffectInstance effect, CallbackInfo ci) {
        syncObfuscation(false);
    }

    @Inject(method = "onDeath", at = @At("TAIL"))
    private void OnDeathObfuscatedSync(DamageSource damageSource, CallbackInfo ci) {
        syncObfuscation(this.getActiveStatusEffects().containsKey(IllusionableStatusEffects.OBFUSCATED));
    }

    @WrapOperation(method = "updatePotionVisibility", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;updatePotionSwirls()V"))
    private void disablePotionSwirlsForIllusions(LivingEntity instance, Operation<Void> original) {
        if (!(instance instanceof IllusionHandler illusionable) || !illusionable.illusionable$isIllusion()) {
            original.call(instance);
            return;
        }
        clearPotionSwirls();
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    private void readCustomDataNbt(NbtCompound nbt, CallbackInfo ci) {
        this.illusionable$modifyIllusionTargets(uuids -> {
            uuids.clear();
            NbtList nbtList = nbt.getList("IllusionTargets", NbtElement.STRING_TYPE);
            for (int i = 0; i < nbtList.size(); i++) {
                uuids.add(UUID.fromString(nbtList.getString(i)));
            }
        }, false);
        this.illusionable$setIllusion(nbt.getBoolean("IsIllusion"));
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    private void writeCustomDataNbt(NbtCompound nbt, CallbackInfo ci) {
        NbtList nbtList = new NbtList();
        for (UUID entry : illusionable$getPersistentIllusionTargets()) {
            nbtList.add(NbtString.of(entry.toString()));
        }
        nbt.put("IllusionTargets", nbtList);
        nbt.putBoolean("IsIllusion", illusionable$isIllusion());
    }


    @Unique
    private void syncObfuscation(boolean isObfuscated) {
        if (this.getWorld().isClient() || this.getServer() == null) return;
        new ObfuscatedCacheUpdatePacket(this.getUuid(), isObfuscated).sendPacket(PlayerLookup.all(this.getServer()));
    }


    @Override
    public HashSet<UUID> illusionable$getPersistentIllusionTargets() {
        return new HashSet<>(this.illusionTargetsPersistence);
    }

    @Override
    public HashSet<Entity> illusionable$getIllusionTargets() {
        return IllusionableTrackedData.resolveEntityIds(getWorld(), this.illusionTargetsRuntime);
    }

    @Override
    public void illusionable$modifyIllusionTargets(Consumer<HashSet<UUID>> newList, boolean sendClientUpdate) {
        newList.accept(this.illusionTargetsPersistence);
        if (!sendClientUpdate || !(getWorld() instanceof ServerWorld serverWorld)) return;
        this.illusionable$updateTrackedEntityIds(serverWorld);
    }

    @Override
    public void illusionable$clearIllusionTargets() {
        this.illusionTargetsPersistence.clear();
        if (!(getWorld() instanceof ServerWorld serverWorld)) return;
        this.illusionable$updateTrackedEntityIds(serverWorld);
    }

    @Override
    public boolean illusionable$isIllusion() {
        return this.isIllusion;
    }

    @Override
    public void illusionable$setIllusion(boolean isIllusion) {
        this.isIllusion = isIllusion;
        this.illusionable$updateClients();
        if (!getWorld().isClient()) {
            if (this.isIllusion) {
                clearPotionSwirls();
            } else {
                updatePotionSwirls();
            }
        }
    }

    @Override
    public void illusionable$updateClients() {
        if (this.getWorld().isClient()) return;
        List<ServerPlayerEntity> targets = new ArrayList<>(PlayerLookup.tracking(this));
        if ((LivingEntity) (Object) this instanceof ServerPlayerEntity self) targets.add(self);
        targets.forEach(player -> {
            boolean isTarget = this.illusionable$getIllusionTargets().contains(player);
            boolean isValidIllusion = this.illusionable$isIllusion();
            new IllusionsCacheUpdatePacket(this.getId(), isTarget, isValidIllusion).sendPacket(player);
        });
    }

    @Override
    public void illusionable$updateTrackedEntityIds(ServerWorld world) {
        List<Integer> entityIds = new ArrayList<>();
        for (UUID uuid : this.illusionTargetsPersistence) {
            Entity entity = world.getEntity(uuid);
            if (entity != null) {
                entityIds.add(entity.getId());
            }
        }
        this.illusionTargetsRuntime.clear();
        this.illusionTargetsRuntime.addAll(entityIds);
        this.illusionable$updateClients();
    }
}
