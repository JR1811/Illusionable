package net.shirojr.illusionable.network.packet;

import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.shirojr.illusionable.Illusionable;
import net.shirojr.illusionable.gamerule.Holder;
import net.shirojr.illusionable.init.IllusionableGameRules;

public record UpdateGameruleS2CPacket(String id, Object newValue) implements FabricPacket {
    public static final PacketType<UpdateGameruleS2CPacket> TYPE = PacketType.create(
            Illusionable.getId("gamerule_update"),
            UpdateGameruleS2CPacket::read
    );

    private static UpdateGameruleS2CPacket read(PacketByteBuf buf) {
        String id = buf.readString();
        Holder<?> holder = IllusionableGameRules.ALL.get(id);
        if (holder == null) throw new DecoderException("[Read] Unknown gamerule id: " + id);
        return new UpdateGameruleS2CPacket(id, holder.fromPacket(buf));
    }

    @Override
    public void write(PacketByteBuf buf) {
        Holder<?> holder = IllusionableGameRules.ALL.get(id);
        if (holder == null) throw new EncoderException("[Write] Unknown gamerule id: " + id);
        buf.writeString(id);
        writeCasted(buf, holder, newValue);
    }

    @SuppressWarnings("unchecked")
    private static <T> void writeCasted(PacketByteBuf buf, Holder<T> holder, Object value) {
        holder.toPacket(buf, (T) value);
    }

    @Override
    public PacketType<?> getType() {
        return TYPE;
    }
}
