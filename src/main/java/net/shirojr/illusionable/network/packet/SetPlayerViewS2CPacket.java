package net.shirojr.illusionable.network.packet;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.shirojr.illusionable.Illusionable;
import net.shirojr.illusionable.cca.implementation.PlayerViewComponent;
import net.shirojr.illusionable.command.util.View;

import java.util.Collection;

public record SetPlayerViewS2CPacket(View view, boolean force) implements FabricPacket {
    public static final PacketType<SetPlayerViewS2CPacket> TYPE =
            PacketType.create(Illusionable.getId("set_player_view"), SetPlayerViewS2CPacket::read);

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }

    private static SetPlayerViewS2CPacket read(PacketByteBuf buf) {
        return new SetPlayerViewS2CPacket(View.fromPacketByteBuf(buf), buf.readBoolean());
    }

    @Override
    public void write(PacketByteBuf buf) {
        this.view.toPacketByteBuf(buf);
        buf.writeBoolean(this.force);
    }

    public void send(Collection<ServerPlayerEntity> targets, boolean lock) {
        for (ServerPlayerEntity target : targets) {
            ServerPlayNetworking.send(target, this);
            PlayerViewComponent component = PlayerViewComponent.get(target);
            component.setLocked(lock);
        }
    }
}
