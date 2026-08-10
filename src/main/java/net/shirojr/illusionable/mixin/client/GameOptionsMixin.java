package net.shirojr.illusionable.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Perspective;
import net.minecraft.text.Text;
import net.shirojr.illusionable.cca.implementation.PlayerViewComponent;
import net.shirojr.illusionable.command.util.View;
import net.shirojr.illusionable.util.mixin.LockedViewOptions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameOptions.class)
public abstract class GameOptionsMixin implements LockedViewOptions {
    @Shadow
    protected MinecraftClient client;

    @Shadow
    private Perspective perspective;

    @Inject(method = "setPerspective", at = @At("HEAD"), cancellable = true)
    private void preventForLockedView(Perspective perspective, CallbackInfo ci) {
        if (client.player != null && PlayerViewComponent.get(client.player).isLocked()) {
            client.player.sendMessage(Text.translatable("chat.illusionable.hint.view_locked"), true);
            ci.cancel();
        }
    }

    @Override
    public void illusionable$forceSetPerspective(View view) {
        perspective = switch (view) {
            case FIRST_PERSON -> Perspective.FIRST_PERSON;
            case THIRD_PERSON_FRONT -> Perspective.THIRD_PERSON_FRONT;
            case THIRD_PERSON_BACK -> Perspective.THIRD_PERSON_BACK;
        };
    }
}
