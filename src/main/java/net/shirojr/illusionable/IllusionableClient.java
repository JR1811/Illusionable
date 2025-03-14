package net.shirojr.illusionable;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.entity.Entity;
import net.shirojr.illusionable.init.IllusionableNetworkingS2C;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

public class IllusionableClient implements ClientModInitializer {
    public static final HashSet<Entity> ILLUSIONS_CACHE = new HashSet<>();
    public static final HashMap<UUID, Boolean> OBFUSCATED_CACHE = new HashMap<>();

    @Override
    public void onInitializeClient() {
        IllusionableNetworkingS2C.initialize();
    }
}
