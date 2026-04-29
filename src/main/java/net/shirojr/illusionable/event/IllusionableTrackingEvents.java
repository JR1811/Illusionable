package net.shirojr.illusionable.event;

import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.shirojr.illusionable.cca.component.IllusionComponent;
import net.shirojr.illusionable.cca.util.IllusionStateCallback;

public class IllusionableTrackingEvents implements EntityTrackingEvents.StartTracking, EntityTrackingEvents.StopTracking {
    @Override
    public void onStartTracking(Entity trackedEntity, ServerPlayerEntity player) {
        if (!(trackedEntity instanceof LivingEntity livingEntity)) return;
        IllusionComponent component = IllusionComponent.fromEntity(livingEntity);
        if (!(player instanceof IllusionStateCallback listener)) return;
        component.getListeners().add(listener);
    }

    @Override
    public void onStopTracking(Entity trackedEntity, ServerPlayerEntity player) {
        if (!(trackedEntity instanceof LivingEntity livingEntity)) return;
        IllusionComponent component = IllusionComponent.fromEntity(livingEntity);
        if (!(player instanceof IllusionStateCallback listener)) return;
        component.getListeners().remove(listener);
    }
}
