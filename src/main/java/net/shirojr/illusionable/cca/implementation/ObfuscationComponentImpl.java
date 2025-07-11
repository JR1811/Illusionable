package net.shirojr.illusionable.cca.implementation;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.shirojr.illusionable.cca.IllusionableComponents;
import net.shirojr.illusionable.cca.component.ObfuscationComponent;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class ObfuscationComponentImpl implements ObfuscationComponent, AutoSyncedComponent {
    @Nullable
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final MinecraftServer server;
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final Scoreboard provider;

    private final HashMap<UUID, Boolean> obfuscationData;

    public ObfuscationComponentImpl(Scoreboard provider, @Nullable MinecraftServer server) {
        this.server = server;
        this.provider = provider;
        this.obfuscationData = new HashMap<>();
    }

    @Override
    public Map<UUID, Boolean> getObfuscationData() {
        return Collections.unmodifiableMap(this.obfuscationData);
    }

    @Override
    public void modifyObfuscationData(Consumer<HashMap<UUID, Boolean>> consumer, boolean sync) {
        consumer.accept(this.obfuscationData);
        if (sync) {
            IllusionableComponents.OBFUSCATION_DATA.sync(provider);
        }
    }

    @Override
    public boolean isObfuscated(UUID uuid) {
        return this.obfuscationData.getOrDefault(uuid, false);
    }

    @Override
    public void setObfuscated(UUID uuid, boolean obfuscated, boolean sync) {
        modifyObfuscationData(data -> data.compute(uuid, (entryUuid, entryObfuscated) -> {
                    entryObfuscated = obfuscated;
                    return entryObfuscated;
                }), sync
        );
    }

    @Override
    public void readFromNbt(NbtCompound nbt) {
        this.modifyObfuscationData(data -> {
            data.clear();
            for (NbtElement nbtElement : nbt.getList("obfuscatedData", NbtElement.COMPOUND_TYPE)) {
                NbtCompound entryNbt = (NbtCompound) nbtElement;
                data.put(entryNbt.getUuid("uuid"), entryNbt.getBoolean("obfuscated"));
            }
        }, false);
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
        NbtList dataNbt = new NbtList();
        for (var entry : this.obfuscationData.entrySet()) {
            NbtCompound entryNbt = new NbtCompound();
            entryNbt.putUuid("uuid", entry.getKey());
            entryNbt.putBoolean("obfuscated", entry.getValue());
            dataNbt.add(entryNbt);
        }
        nbt.put("obfuscatedData", dataNbt);
    }
}
