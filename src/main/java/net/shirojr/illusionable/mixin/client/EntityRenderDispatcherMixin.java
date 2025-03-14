package net.shirojr.illusionable.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.world.WorldView;
import net.shirojr.illusionable.IllusionableClient;
import net.shirojr.illusionable.util.wrapper.IllusionHandler;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;renderHitbox(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/entity/Entity;FFFF)V"))
    private void renderIllusionHitbox(MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta, float red, float green, float blue, Operation<Void> original) {
        if (canRender(entity, true)) {
            original.call(matrices, vertices, entity, tickDelta, red, green, blue);
        }
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/entity/EntityRenderDispatcher;renderFire(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/entity/Entity;Lorg/joml/Quaternionf;)V"))
    private void renderIllusionFire(EntityRenderDispatcher instance, MatrixStack matrices, VertexConsumerProvider vertexConsumers, Entity entity, Quaternionf rotation, Operation<Void> original) {
        if (canRender(entity, false)) {
            original.call(instance, matrices, vertexConsumers, entity, rotation);
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
        if (!(entity instanceof IllusionHandler illusionable)) return true;
        if (!illusionable.illusionable$isIllusion()) return true;
        if (IllusionableClient.ILLUSIONS_CACHE.contains(entity)) return true;
        return considerPermissionLevel && client.player.hasPermissionLevel(2);
    }
}
