package net.shirojr.illusionable.init;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;

public interface IllusionableGameRules {
    GameRules.Key<GameRules.BooleanRule> LINKED_DAMAGE_CHAIN =
            GameRuleRegistry.register("infiniteDamageChain", GameRules.Category.MOBS, GameRuleFactory.createBooleanRule(false));

    static void initialize() {
        // static initialisation
    }
}
