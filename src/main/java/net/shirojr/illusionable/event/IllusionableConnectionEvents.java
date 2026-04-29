package net.shirojr.illusionable.event;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.shirojr.illusionable.cca.component.IllusionComponent;
import net.shirojr.illusionable.cca.util.IllusionStateCallback;
import net.shirojr.illusionable.init.IllusionableGameRules;
import net.shirojr.illusionable.network.packet.UpdateGameruleS2CPacket;

public class IllusionableConnectionEvents implements ServerPlayConnectionEvents.Join, ServerPlayConnectionEvents.Disconnect {
    @Override
    public void onPlayReady(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        if (handler.player instanceof IllusionStateCallback listener) {
            IllusionComponent component = IllusionComponent.fromEntity(handler.player);
            component.getListeners().add(listener);
        }

        IllusionableGameRules.ALL.values().forEach(holder -> ServerPlayNetworking.send(handler.player,
                new UpdateGameruleS2CPacket(holder.getId(), holder.get(handler.getPlayer().getWorld())))
        );
    }

    @Override
    public void onPlayDisconnect(ServerPlayNetworkHandler handler, MinecraftServer server) {
        // remove self listening
        if (!(handler.player instanceof IllusionStateCallback listener)) return;
        IllusionComponent component = IllusionComponent.fromEntity(handler.player);
        component.getListeners().remove(listener);
    }
}
