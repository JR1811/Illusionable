package net.shirojr.illusionable;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import net.shirojr.illusionable.init.*;
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
        IllusionableParticleTypes.initialize();
        IllusionableGameRules.initialize();
        IllusionableEvents.initializeCommon();
        LOGGER.info("I was crazy once...");
    }

    public static Identifier getId(String path) {
        return Identifier.of(MOD_ID, path);
    }
}