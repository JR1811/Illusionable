package net.shirojr.illusionable.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.shirojr.illusionable.IllusionableClient;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
    @Shadow
    @Final
    protected EntityRenderDispatcher dispatcher;

    @WrapOperation(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I"))
    private int renderObfuscatedLabel(TextRenderer instance, Text text, float x, float y, int color, boolean shadow,
                                      Matrix4f matrix, VertexConsumerProvider vertexConsumers, TextRenderer.TextLayerType layerType,
                                      int backgroundColor, int light, Operation<Integer> original, @Local(argsOnly = true) Entity entity) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) {
            return original.call(instance, text, x, y, color, shadow, matrix, vertexConsumers, layerType, backgroundColor, light);
        }
        boolean isObfuscated = Optional.ofNullable(IllusionableClient.OBFUSCATED_CACHE.get(entity.getUuid())).orElse(false);
        MutableText newText = text.copy();
        if (isObfuscated) {
            if (!client.player.hasPermissionLevel(2) || !dispatcher.shouldRenderHitboxes()) {
                newText.setStyle(text.getStyle().withFormatting(Formatting.OBFUSCATED));
            }
        }
        return original.call(instance, newText, x, y, color, shadow, matrix, vertexConsumers, layerType, backgroundColor, light);
    }
}
