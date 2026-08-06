package net.shirojr.illusionable.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.VertexSorter;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.shirojr.illusionable.Illusionable;
import net.shirojr.illusionable.cca.component.IllusionComponent;
import net.shirojr.illusionable.cca.implementation.CensoredComponent;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Objects;

public class IllusionableRenderEvents implements WorldRenderEvents.AfterEntities, WorldRenderEvents.Last {
    public static final Identifier EYE_ICON = Illusionable.getId("textures/misc/eye.png");
    public static final Identifier EYE_CLOSED_ICON = Illusionable.getId("textures/misc/eye_closed.png");

    @Override
    public void afterEntities(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;
        ClientPlayerEntity player = client.player;
        if (player == null) return;
        ClientWorld world = client.world;
        if (world == null) return;

        this.handleIllusionRendering(context, world, player);
    }

    @Override
    public void onLast(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;
        ClientPlayerEntity player = client.player;
        if (player == null) return;
        ClientWorld world = client.world;
        if (world == null) return;

        this.handleCensorRendering(context, client, world);
    }

    private void handleIllusionRendering(WorldRenderContext context, ClientWorld world, ClientPlayerEntity player) {
        IllusionComponent selfComponent = IllusionComponent.fromEntity(player);
        if (!selfComponent.isIllusion() || !selfComponent.getIconRendering().showIcons()) return;

        double radius = selfComponent.getIconRendering().getRadius();
        List<LivingEntity> targetsInRange = world.getEntitiesByClass(LivingEntity.class, player.getBoundingBox().expand(radius + 1),
                other -> !other.equals(player) && player.squaredDistanceTo(other) <= radius * radius);

        for (LivingEntity target : targetsInRange) {
            boolean isVisible = selfComponent.isTargeting(target);
            double scale = selfComponent.getIconRendering().getScale();
            this.renderIllusionEyeOnTarget(context, target, isVisible, scale);
        }
    }

    private void renderIllusionEyeOnTarget(WorldRenderContext context, LivingEntity target, boolean isVisible, double scale) {
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

    private void handleCensorRendering(WorldRenderContext context, MinecraftClient client, ClientWorld world) {
        CensoredComponent component = CensoredComponent.get(client.player);
        if (component.isEmpty()) return;
        Camera camera = context.camera();
        Vec3d camPos = camera.getPos();
        Matrix4f viewRotationMatrix = new Matrix4f(context.matrixStack().peek().getPositionMatrix());
        Matrix4f projectionMatrix = new Matrix4f(context.projectionMatrix());
        Matrix4f viewProjectionMatrix = new Matrix4f(projectionMatrix).mul(viewRotationMatrix);
        float maxDistance = 128;

        for (Entity entity : world.getEntities()) {
            if (!(entity instanceof LivingEntity target)) continue;
            if (!component.isCensored(entity)) continue;
            if (Objects.equals(target, client.player) && !camera.isThirdPerson()) continue;
            if (target.squaredDistanceTo(camPos) >= maxDistance * maxDistance) continue;
            Vec3d lerpPos = new Vec3d(
                    MathHelper.lerp(client.getTickDelta(), target.lastRenderX, target.getX()),
                    MathHelper.lerp(client.getTickDelta(), target.lastRenderY, target.getY()),
                    MathHelper.lerp(client.getTickDelta(), target.lastRenderZ, target.getZ())
            );
            Vec3d currentPos = target.getPos();
            Vec3d interpolatedPos = lerpPos.subtract(currentPos);
            Box boundingBox = target.getBoundingBox().offset(interpolatedPos).expand(component.getOrDefault(entity, 1f));

            float[] rectangle = boundingBoxToNdc(boundingBox, viewProjectionMatrix, camPos);
            if (rectangle == null) continue;
            this.drawNdcRectangle(rectangle[0], rectangle[1], rectangle[2], rectangle[3], rectangle[4], false);
        }
    }

    private float[] boundingBoxToNdc(Box box, Matrix4f viewProjection, Vec3d camPos) {
        float minX = Float.POSITIVE_INFINITY, minY = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY, maxY = Float.NEGATIVE_INFINITY;
        float closestZ = Float.POSITIVE_INFINITY;
        boolean anyVisible = false;

        for (int i = 0; i < 8; i++) {
            double x = (i & 1) == 0 ? box.minX : box.maxX;
            double y = (i & 2) == 0 ? box.minY : box.maxY;
            double z = (i & 4) == 0 ? box.minZ : box.maxZ;

            Vector4f clipSpace = new Vector4f(
                    (float) (x - camPos.x),
                    (float) (y - camPos.y),
                    (float) (z - camPos.z),
                    1.0f
            );
            viewProjection.transform(clipSpace);
            if (clipSpace.w() <= 1.0E-5f) {
                continue;
            }
            anyVisible = true;
            float ndcX = clipSpace.x() / clipSpace.w();
            float ndcY = clipSpace.y() / clipSpace.w();
            float ndcZ = clipSpace.z() / clipSpace.w();

            minX = Math.min(minX, ndcX);
            maxX = Math.max(maxX, ndcX);
            minY = Math.min(minY, ndcY);
            maxY = Math.max(maxY, ndcY);

            closestZ = Math.min(closestZ, ndcZ);
        }
        if (!anyVisible) return null;
        minX = MathHelper.clamp(minX, -1f, 1f);
        maxX = MathHelper.clamp(maxX, -1f, 1f);
        minY = MathHelper.clamp(minY, -1f, 1f);
        maxY = MathHelper.clamp(maxY, -1f, 1f);
        closestZ = MathHelper.clamp(closestZ - 0.0005f, -1f, 1f);
        return new float[]{minX, minY, maxX, maxY, closestZ};
    }

    @SuppressWarnings("SameParameterValue")
    private void drawNdcRectangle(float minX, float minY, float maxX, float maxY, float closesZ, boolean ignoreOcclusion) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        if (ignoreOcclusion) {
            RenderSystem.disableDepthTest();
        } else {
            RenderSystem.enableDepthTest();
            RenderSystem.depthFunc(GL11.GL_LEQUAL);
        }
        RenderSystem.depthMask(false);

        Matrix4f previousProjection = new Matrix4f(RenderSystem.getProjectionMatrix());
        MatrixStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.push();
        modelViewStack.loadIdentity();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setProjectionMatrix(new Matrix4f(), VertexSorter.BY_Z);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(minX, maxY, closesZ).color(0, 0, 0, 255).next();
        buffer.vertex(maxX, maxY, closesZ).color(0, 0, 0, 255).next();
        buffer.vertex(maxX, minY, closesZ).color(0, 0, 0, 255).next();
        buffer.vertex(minX, minY, closesZ).color(0, 0, 0, 255).next();
        tessellator.draw();

        modelViewStack.pop();
        RenderSystem.applyModelViewMatrix();
        RenderSystem.setProjectionMatrix(previousProjection, VertexSorter.BY_DISTANCE);

        RenderSystem.depthMask(true);
        RenderSystem.disableBlend();
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();
    }
}
