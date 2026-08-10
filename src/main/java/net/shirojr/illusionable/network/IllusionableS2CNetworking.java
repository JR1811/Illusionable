package net.shirojr.illusionable.network;

import io.netty.handler.codec.DecoderException;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.Perspective;
import net.shirojr.illusionable.command.util.View;
import net.shirojr.illusionable.gamerule.Holder;
import net.shirojr.illusionable.init.IllusionableGameRules;
import net.shirojr.illusionable.network.packet.SetPlayerViewS2CPacket;
import net.shirojr.illusionable.network.packet.UpdateGameruleS2CPacket;
import net.shirojr.illusionable.util.mixin.LockedViewOptions;

public class IllusionableS2CNetworking {
    static {
        ClientPlayNetworking.registerGlobalReceiver(UpdateGameruleS2CPacket.TYPE, IllusionableS2CNetworking::updateGamerule);
        ClientPlayNetworking.registerGlobalReceiver(SetPlayerViewS2CPacket.TYPE, IllusionableS2CNetworking::setPlayerView);
    }

    private static void updateGamerule(UpdateGameruleS2CPacket packet, ClientPlayerEntity player, PacketSender responseSender) {
        MinecraftClient.getInstance().execute(() -> {
            String id = packet.id();
            Holder<?> holder = IllusionableGameRules.ALL.get(id);
            if (holder == null) throw new DecoderException("[Packet Received] Unknown gamerule id: " + id);
            updateCastedGamerule(holder, packet.newValue());
        });
    }

    private static void setPlayerView(SetPlayerViewS2CPacket packet, ClientPlayerEntity player, PacketSender responseSender) {
        View view = packet.view();
        boolean force = packet.force();
        MinecraftClient client = MinecraftClient.getInstance();
        client.execute(() -> {
            if (force) {
                if (client.options instanceof LockedViewOptions lockedViewOptions) {
                    lockedViewOptions.illusionable$forceSetPerspective(view);
                }
            } else {
                switch (view) {
                    case FIRST_PERSON -> client.options.setPerspective(Perspective.FIRST_PERSON);
                    case THIRD_PERSON_BACK -> client.options.setPerspective(Perspective.THIRD_PERSON_BACK);
                    case THIRD_PERSON_FRONT -> client.options.setPerspective(Perspective.THIRD_PERSON_FRONT);
                }
            }
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
