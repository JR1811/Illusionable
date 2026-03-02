package net.shirojr.illusionable.network;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Blocks;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class IllusionableC2SNetworking {
    static {
        ServerPlayNetworking.registerGlobalReceiver(NetworkIdentifiers.PLACE_BLOCK_DEBUG, (minecraftServer, serverPlayerEntity, serverPlayNetworkHandler, packetByteBuf, packetSender) -> IllusionableC2SNetworking.onBlockPlaceDebug(minecraftServer, serverPlayerEntity, serverPlayNetworkHandler, packetByteBuf, packetSender));
    }

    private static void onBlockPlaceDebug(MinecraftServer server, ServerPlayerEntity player,
                                          ServerPlayNetworkHandler networkHandler, PacketByteBuf buf,
                                          PacketSender packetSender) {
        BlockPos pos = buf.readBlockPos();

        server.execute(() -> {
            if (!FabricLoader.getInstance().isDevelopmentEnvironment()) return;
            player.getWorld().setBlockState(pos, Blocks.DIAMOND_BLOCK.getDefaultState());
        });
    }


    public static void initialize() {
        // static initialisation
    }
}
