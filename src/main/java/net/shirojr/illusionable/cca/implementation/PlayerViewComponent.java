package net.shirojr.illusionable.cca.implementation;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.shirojr.illusionable.Illusionable;
import net.shirojr.illusionable.cca.IllusionableComponents;
import net.shirojr.illusionable.command.util.View;
import net.shirojr.illusionable.network.packet.SetPlayerViewS2CPacket;
import net.shirojr.illusionable.util.constant.IllusionableNbtKeys;

import java.util.Set;

public class PlayerViewComponent implements Component, AutoSyncedComponent {
    public static final Identifier KEY = Illusionable.getId("player_view");
    private final PlayerEntity player;

    private boolean locked;

    public PlayerViewComponent(PlayerEntity player) {
        this.player = player;
    }

    public static PlayerViewComponent get(PlayerEntity player) {
        return IllusionableComponents.PLAYER_VIEW.get(player);
    }

    public void setView(View view, boolean force, boolean lock) {
        if (!(this.player instanceof ServerPlayerEntity serverPlayer)) return;
        new SetPlayerViewS2CPacket(view, force).send(Set.of(serverPlayer), lock);
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
        this.sync();
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        if (tag.contains(IllusionableNbtKeys.VIEW_LOCKED)) {
            this.locked = tag.getBoolean(IllusionableNbtKeys.VIEW_LOCKED);
        } else {
            this.locked = false;
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putBoolean(IllusionableNbtKeys.VIEW_LOCKED, this.locked);
    }

    public void sync() {
        if (!(this.player instanceof ServerPlayerEntity)) return;
        IllusionableComponents.PLAYER_VIEW.sync(this.player);
    }

    @SuppressWarnings("unused")
    public static void onRespawn(PlayerViewComponent from, PlayerViewComponent to, boolean lossless,
                                 boolean keepInventory, boolean sameCharacter) {
        to.locked = from.locked;
    }
}
