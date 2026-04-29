package net.shirojr.illusionable.network;

import io.netty.handler.codec.DecoderException;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.shirojr.illusionable.gamerule.Holder;
import net.shirojr.illusionable.init.IllusionableGameRules;
import net.shirojr.illusionable.network.packet.UpdateGameruleS2CPacket;

public class IllusionableS2CNetworking {
    static {
        ClientPlayNetworking.registerGlobalReceiver(UpdateGameruleS2CPacket.TYPE, IllusionableS2CNetworking::updateGamerule);
    }

    private static void updateGamerule(UpdateGameruleS2CPacket packet, ClientPlayerEntity player, PacketSender responseSender) {
        MinecraftClient.getInstance().execute(() -> {
            String id = packet.id();
            Holder<?> holder = IllusionableGameRules.ALL.get(id);
            if (holder == null) throw new DecoderException("[Packet Received] Unknown gamerule id: " + id);
            updateCastedGamerule(holder, packet.newValue());
        });
    }

    @SuppressWarnings("unchecked")
    private static <T> void updateCastedGamerule(Holder<T> holder, Object value) {
        holder.update((T) value);
    }

    public static void initialize() {
        // static initialisation
    }
}
