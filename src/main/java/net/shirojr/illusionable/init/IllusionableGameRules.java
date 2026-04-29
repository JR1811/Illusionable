package net.shirojr.illusionable.init;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.GameRules;
import net.shirojr.illusionable.gamerule.Holder;
import net.shirojr.illusionable.gamerule.sync.BooleanRuleHolder;
import net.shirojr.illusionable.network.packet.UpdateGameruleS2CPacket;

import java.util.HashMap;
import java.util.function.Function;

public interface IllusionableGameRules {
    HashMap<String, Holder<?>> ALL = new HashMap<>();

    BooleanRuleHolder LINKED_DAMAGE_CHAIN = register(
            "infiniteDamageChain", id -> new BooleanRuleHolder(
                    id, GameRuleRegistry.register(id, GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false,
                            (server, booleanRule) -> PlayerLookup.all(server).forEach(player ->
                                    ServerPlayNetworking.send(player, new UpdateGameruleS2CPacket(id, booleanRule.get())))
                    )
            ))
    );

    BooleanRuleHolder ILLUSIONS_PUSH_ENTITIES = register(
            "illusionsPushEntities", id -> new BooleanRuleHolder(
                    id, GameRuleRegistry.register(id, GameRules.Category.MISC, GameRuleFactory.createBooleanRule(false,
                            (server, booleanRule) -> PlayerLookup.all(server).forEach(player ->
                                    ServerPlayNetworking.send(player, new UpdateGameruleS2CPacket(id, booleanRule.get())))
                    )
            ))
    );

    private static <T extends Holder<?>> T register(String id, Function<String, T> factory) {
        T entry = factory.apply(id);
        ALL.put(id, entry);
        return entry;
    }

    static void initialize() {
        // static initialisation
    }
}
