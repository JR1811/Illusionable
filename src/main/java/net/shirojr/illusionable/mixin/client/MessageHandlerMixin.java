package net.shirojr.illusionable.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.message.MessageHandler;
import net.minecraft.network.message.MessageType;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.shirojr.illusionable.IllusionableClient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MessageHandler.class)
public class MessageHandlerMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    @ModifyVariable(method = "onChatMessage", at = @At("HEAD"), argsOnly = true)
    private MessageType.Parameters obfuscatePlayerName(MessageType.Parameters params, @Local(argsOnly = true) GameProfile sender) {
        if (client == null || client.player == null) return params;
        var obfuscatedCache = IllusionableClient.OBFUSCATED_CACHE;
        if (!obfuscatedCache.containsKey(sender.getId()) || !obfuscatedCache.get(sender.getId())) return params;
        MutableText modified = params.name().copy();
        modified = modified.formatted(Formatting.OBFUSCATED);
        HoverEvent originalHover = modified.getStyle().getHoverEvent();
        ClickEvent originalClick = modified.getStyle().getClickEvent();
        if (originalHover != null) {
            var entityInformation = originalHover.getValue(HoverEvent.Action.SHOW_ENTITY);
            if (entityInformation != null && entityInformation.name.isPresent()) {
                Text obfuscation;
                if (client.player.hasPermissionLevel(2) || client.player.isSpectator()) {
                    obfuscation = entityInformation.name.get().copy().append(Text.translatable("chat.illusionable.hint.obfuscation", " - [", " ]"));
                } else {
                    obfuscation = entityInformation.name.get().copy().formatted(Formatting.OBFUSCATED);
                    if (originalClick != null) {
                        modified.setStyle(modified.getStyle().withClickEvent(null));
                    }
                }

                HoverEvent.EntityContent content = new HoverEvent.EntityContent(entityInformation.entityType, entityInformation.uuid, obfuscation);
                modified = modified.setStyle(modified.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ENTITY, content)));
            }
        }
        return new MessageType.Parameters(params.type(), modified, params.targetName());
    }
}
