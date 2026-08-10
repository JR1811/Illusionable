package net.shirojr.illusionable.command.util;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.StringIdentifiable;

import java.util.Locale;

public enum View implements StringIdentifiable {
    FIRST_PERSON, THIRD_PERSON_BACK, THIRD_PERSON_FRONT;

    public static final com.mojang.serialization.Codec<View> CODEC = StringIdentifiable.createCodec(View::values);

    @Override
    public String asString() {
        return this.name().toLowerCase(Locale.ROOT);
    }

    public void toPacketByteBuf(PacketByteBuf buf) {
        buf.writeString(this.asString());
    }

    public static View fromPacketByteBuf(PacketByteBuf buf) {
        String search = buf.readString();
        for (View entry : View.values()) {
            if (search.equals(entry.asString())) return entry;
        }
        throw new IllegalStateException("View entry not found in enum: " + search);
    }
}