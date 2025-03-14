package net.shirojr.illusionable.mixin;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.entity.Attackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.shirojr.illusionable.init.IllusionableTrackedData;
import net.shirojr.illusionable.network.packet.ObfuscatedCacheUpdatePacket;
import net.shirojr.illusionable.network.packet.IllusionsCacheUpdatePacket;
import net.shirojr.illusionable.util.wrapper.IllusionHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Attackable, IllusionHandler {
    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Unique
    private final List<UUID> illusionTargetsPersistence = new ArrayList<>();

    @Unique
    private static final TrackedData<List<Integer>> ILLUSION_TARGETS_RUNTIME = DataTracker.registerData(LivingEntityMixin.class, IllusionableTrackedData.ENTITY_LIST);
    @Unique
    private static final TrackedData<Boolean> IS_ILLUSION = DataTracker.registerData(LivingEntityMixin.class, TrackedDataHandlerRegistry.BOOLEAN);

    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void initCustomDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(ILLUSION_TARGETS_RUNTIME, new ArrayList<>());
        builder.add(IS_ILLUSION, false);
    }

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
        if (this.getWorld().isClient() || this.getServer() == null) return;
        new ObfuscatedCacheUpdatePacket(this.getUuid(), false).sendPacket(PlayerLookup.all(this.getServer()));
    }

    @Override
    public List<UUID> illusionable$getPersistentIllusionTargets() {
        return Collections.unmodifiableList(this.illusionTargetsPersistence);
    }

    @Override
    public List<Entity> illusionable$getIllusionTargets() {
        return IllusionableTrackedData.resolveEntityIds(getWorld(), dataTracker.get(ILLUSION_TARGETS_RUNTIME));
    }

    @Override
    public void illusionable$modifyIllusionTargets(Consumer<List<UUID>> newList, boolean sendClientUpdate) {
        newList.accept(this.illusionTargetsPersistence);
        if (sendClientUpdate && getWorld() instanceof ServerWorld serverWorld) {
            this.illusionable$updateTrackedEntityIds(serverWorld);
        }
    }

    @Override
    public void illusionable$clearIllusionTargets() {
        this.illusionTargetsPersistence.clear();
        if (getWorld() instanceof ServerWorld serverWorld) {
            this.illusionable$updateTrackedEntityIds(serverWorld);
        }
    }

    @Override
    public boolean illusionable$isIllusion() {
        return dataTracker.get(IS_ILLUSION);
    }

    @Override
    public void illusionable$setIllusion(boolean isIllusion) {
        dataTracker.set(IS_ILLUSION, isIllusion);
        this.illusionable$updateClients();
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

    @Override
    public void illusionable$updateClients() {
        if (this.getWorld().isClient()) return;
        PlayerLookup.tracking(this).forEach(player -> {
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
        this.dataTracker.set(ILLUSION_TARGETS_RUNTIME, entityIds);
        this.illusionable$updateClients();
    }
}
