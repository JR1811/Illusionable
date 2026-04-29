package net.shirojr.illusionable.gamerule.sync;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.shirojr.illusionable.gamerule.Holder;

public class BooleanRuleHolder implements Holder<Boolean> {
    private final GameRules.Key<GameRules.BooleanRule> key;
    private final String id;
    private boolean value;

    public BooleanRuleHolder(String id, GameRules.Key<GameRules.BooleanRule> key) {
        this.id = id;
        this.key = key;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Boolean get(World world) {
        if (world.isClient()) {
            return value;
        } else {
            return world.getGameRules().getBoolean(key);
        }
    }

    @Override
    public Boolean fromPacket(PacketByteBuf buf) {
        return buf.readBoolean();
    }

    @Override
    public void toPacket(PacketByteBuf buf, Boolean value) {
        buf.writeBoolean(value);
    }

    @Override
    public void update(Boolean value) {
        this.value = value;
    }
}
