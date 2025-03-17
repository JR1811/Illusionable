package net.shirojr.illusionable.network.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.shirojr.illusionable.Illusionable;
import net.shirojr.illusionable.IllusionableClient;
import net.shirojr.illusionable.util.wrapper.IllusionHandler;

import java.util.Collection;

public record IllusionsCacheUpdatePacket(int entityNetworkId, boolean isTarget,
                                         boolean isValidIllusion) implements CustomPayload {
    public static final Id<IllusionsCacheUpdatePacket> IDENTIFIER = new Id<>(Illusionable.getId("update_illusions_cache"));

    public static final PacketCodec<RegistryByteBuf, IllusionsCacheUpdatePacket> CODEC = PacketCodec.tuple(
            PacketCodecs.VAR_INT, IllusionsCacheUpdatePacket::entityNetworkId,
            PacketCodecs.BOOL, IllusionsCacheUpdatePacket::isTarget,
            PacketCodecs.BOOL, IllusionsCacheUpdatePacket::isValidIllusion,
            IllusionsCacheUpdatePacket::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return IDENTIFIER;
    }

    public void sendPacket(ServerPlayerEntity player) {
        if (player.networkHandler == null) return;
        ServerPlayNetworking.send(player, this);
    }

    public void sendPacket(Collection<ServerPlayerEntity> players) {
        players.forEach(this::sendPacket);
    }

    public void handlePacket(ClientPlayNetworking.Context context) {
        ClientWorld world = context.player().clientWorld;
        if (world == null || !(world.getEntityById(entityNetworkId) instanceof IllusionHandler illusionable)) return;
        illusionable.illusionable$setIllusion(isValidIllusion);
        if (isTarget) {
            IllusionableClient.ILLUSIONS_CACHE.add(world.getEntityById(entityNetworkId()));
        } else {
            IllusionableClient.ILLUSIONS_CACHE.remove(world.getEntityById(entityNetworkId()));
        }
    }
}
