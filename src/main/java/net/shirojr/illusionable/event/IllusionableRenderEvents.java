package net.shirojr.illusionable.event;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.shirojr.illusionable.Illusionable;
import net.shirojr.illusionable.cca.component.IllusionComponent;
import org.joml.Matrix4f;

import java.util.List;

public class IllusionableRenderEvents implements WorldRenderEvents.AfterEntities {
    public static final Identifier EYE_ICON = Illusionable.getId("textures/misc/eye.png");
    public static final Identifier EYE_CLOSED_ICON = Illusionable.getId("textures/misc/eye_closed.png");

    @Override
    public void afterEntities(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;
        ClientPlayerEntity player = client.player;
        if (player == null) return;
        IllusionComponent selfComponent = IllusionComponent.fromEntity(player);
        if (!selfComponent.isIllusion() || !selfComponent.getIconRendering().showIcons()) return;
        ClientWorld world = client.world;
        if (world == null) return;

        double radius = selfComponent.getIconRendering().getRadius();
        List<LivingEntity> targetsInRange = world.getEntitiesByClass(LivingEntity.class, player.getBoundingBox().expand(radius + 1),
                other -> !other.equals(player) && player.squaredDistanceTo(other) <= radius * radius);

        for (LivingEntity target : targetsInRange) {
            boolean isVisible = selfComponent.isTarget(target);
            double scale = selfComponent.getIconRendering().getScale();
            this.renderIllusionEyeOnTarget(context, player, target, isVisible, scale);
        }
    }

    private void renderIllusionEyeOnTarget(WorldRenderContext context, ClientPlayerEntity player,
                                           LivingEntity target, boolean isVisible, double scale) {
        VertexConsumerProvider vertexConsumers = context.consumers();
        if (vertexConsumers == null) return;

        Identifier icon = isVisible ? EYE_ICON : EYE_CLOSED_ICON;
        MatrixStack matrices = context.matrixStack();
        RenderLayer renderLayer = RenderLayer.getEntityTranslucent(icon);
        VertexConsumer buffer = vertexConsumers.getBuffer(renderLayer);

        Camera camera = context.camera();
        float tickDelta = context.tickDelta();
        double heightPos = target.getHeight() + 0.5;

        Vec3d localPos = new Vec3d(
                MathHelper.lerp(tickDelta, target.lastRenderX, target.getX()),
                MathHelper.lerp(tickDelta, target.lastRenderY, target.getY()),
                MathHelper.lerp(tickDelta, target.lastRenderZ, target.getZ())
        ).subtract(camera.getPos()).add(0, heightPos, 0);

        matrices.push();

        matrices.translate(localPos.x, localPos.y, localPos.z);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(camera.getYaw()));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
        matrices.scale((float) scale, (float) scale, (float) scale);

        Matrix4f matrix = matrices.peek().getPositionMatrix();

        float vertexPos = 0.5f;
        buffer.vertex(matrix, -vertexPos, vertexPos, 0)
                .color(255, 255, 255, 255)
                .texture(0, 0)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                .normal(0, 0, 1)
                .next();
        buffer.vertex(matrix, -vertexPos, -vertexPos, 0)
                .color(255, 255, 255, 255)
                .texture(0, 1)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                .normal(0, 0, 1)
                .next();
        buffer.vertex(matrix, vertexPos, -vertexPos, 0)
                .color(255, 255, 255, 255)
                .texture(1, 1)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                .normal(0, 0, 1)
                .next();
        buffer.vertex(matrix, vertexPos, vertexPos, 0)
                .color(255, 255, 255, 255)
                .texture(1, 0)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                .normal(0, 0, 1)
                .next();

        matrices.pop();
    }
}
