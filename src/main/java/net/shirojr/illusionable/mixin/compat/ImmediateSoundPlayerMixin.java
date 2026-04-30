package net.shirojr.illusionable.mixin.compat;

import eu.ha3.presencefootsteps.sound.Options;
import eu.ha3.presencefootsteps.sound.player.ImmediateSoundPlayer;
import net.minecraft.entity.LivingEntity;
import net.shirojr.illusionable.cca.component.IllusionComponent;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// compat for Presence-Footsteps mod
@Debug(export = true)
@Mixin(value = ImmediateSoundPlayer.class)
public class ImmediateSoundPlayerMixin {
    @Inject(method = "playSound", at = @At("HEAD"), remap = false, cancellable = true)
    private void blockStepSoundForIllusion(LivingEntity location, String soundName, float volume, float pitch, Options options, CallbackInfo ci) {
        IllusionComponent component = IllusionComponent.fromEntity(location);
        if (component.isIllusion()) {
            ci.cancel();
        }
    }
}
