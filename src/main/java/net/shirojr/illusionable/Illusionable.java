package net.shirojr.illusionable;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import net.shirojr.illusionable.init.*;
import net.shirojr.illusionable.network.IllusionableC2SNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Illusionable implements ModInitializer {
    public static final String MOD_ID = "illusionable";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        IllusionableItems.initialize();
        IllusionableItemGroups.initialize();
        IllusionableStatusEffects.initialize();
        IllusionableDamageTypes.initialize();
        IllusionableGameRules.initialize();
        IllusionableCommonEvents.initializeCommon();
        IllusionableC2SNetworking.initialize();
        IllusionableArgumentTypes.initialize();

        LOGGER.info("I was crazy once...");
    }

    public static Identifier getId(String path) {
        return Identifier.of(MOD_ID, path);
    }
}