package net.shirojr.illusionable.gamerule;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.World;

public interface Holder<T> {
    String getId();

    T get(World world);

    T fromPacket(PacketByteBuf buf);

    void toPacket(PacketByteBuf buf, T value);

    void update(T value);
}