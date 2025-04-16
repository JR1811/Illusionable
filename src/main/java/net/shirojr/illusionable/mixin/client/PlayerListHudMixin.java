package net.shirojr.illusionable.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.shirojr.illusionable.Illusionable;
import net.shirojr.illusionable.IllusionableClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {
    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/PlayerSkinDrawer;draw(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/util/Identifier;IIIZZ)V"))
    private void renderObfuscatedSkin(DrawContext context, Identifier texture, int x, int y, int size, boolean hatVisible,
                                      boolean upsideDown, Operation<Void> original, @Local GameProfile playerProfile) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.player.isSpectator()) {
            original.call(context, texture, x, y, size, hatVisible, upsideDown);
            return;
        }
        if (client.player.hasPermissionLevel(2) && client.getEntityRenderDispatcher().shouldRenderHitboxes()) {
            original.call(context, texture, x, y, size, hatVisible, upsideDown);
            return;
        }
        boolean isObfuscated = IllusionableClient.OBFUSCATED_CACHE.contains(playerProfile.getId());
        if (!isObfuscated) {
            original.call(context, texture, x, y, size, hatVisible, upsideDown);
            return;
        }
        RenderSystem.setShaderTexture(0, Illusionable.getId("textures/entity/obfuscated_skin.png"));
    }

    @WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;getPlayerName(Lnet/minecraft/client/network/PlayerListEntry;)Lnet/minecraft/text/Text;"))
    private Text renderObfuscatedName(PlayerListHud instance, PlayerListEntry entry, Operation<Text> original) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.player.isSpectator()) {
            return original.call(instance, entry);
        }
        if (client.player.hasPermissionLevel(2) && client.getEntityRenderDispatcher().shouldRenderHitboxes()) {
            return original.call(instance, entry);
        }
        boolean isObfuscated = IllusionableClient.OBFUSCATED_CACHE.contains(entry.getProfile().getId());
        if (!isObfuscated) {
            return original.call(instance, entry);
        }
        MutableText name = original.call(instance, entry).copy();
        return name.setStyle(name.getStyle().withFormatting(Formatting.OBFUSCATED));
    }

    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/ClientConnection;isEncrypted()Z"))
    private boolean enabledForTestEnv(boolean original) {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) return true;
        return original;
    }
}
