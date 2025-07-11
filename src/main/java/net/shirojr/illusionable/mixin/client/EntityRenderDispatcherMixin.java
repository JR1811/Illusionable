package net.shirojr.illusionable.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.WorldView;
import net.shirojr.illusionable.cca.component.IllusionComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;renderHitbox(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/entity/Entity;F)V"))
    private void renderIllusionHitbox(MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta, Operation<Void> original) {
        if (canRender(entity, true)) {
            original.call(matrices, vertices, entity, tickDelta);
        }
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;renderFire(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/entity/Entity;)V"))
    private void renderIllusionFire(EntityRenderDispatcher instance, MatrixStack matrices, VertexConsumerProvider vertexConsumers, Entity entity, Operation<Void> original) {
        if (canRender(entity, false)) {
            original.call(instance, matrices, vertexConsumers, entity);
        }
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;renderShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/entity/Entity;FFLnet/minecraft/world/WorldView;F)V"))
    private void renderIllusionShadow(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Entity entity, float opacity, float tickDelta, WorldView world, float radius, Operation<Void> original) {
        if (canRender(entity, false)) {
            original.call(matrices, vertexConsumers, entity, opacity, tickDelta, world, radius);
        }
    }

    @Unique
    private static boolean canRender(Entity entity, boolean considerPermissionLevel) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return true;
        if (!(entity instanceof LivingEntity livingEntity)) return true;
        IllusionComponent illusionComponent = IllusionComponent.fromEntity(livingEntity);
        if (!illusionComponent.isIllusion()) return true;
        if (illusionComponent.getTargets().contains(client.player.getUuid())) return true;
        return considerPermissionLevel && client.player.hasPermissionLevel(2);
    }
}
