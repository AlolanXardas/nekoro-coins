package dev.xardas.nekorocoins;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/** Sends a joining player the names and enabled coins of the server. */
final class CoinNetwork {
    static final ResourceLocation ID = ResourceLocation.tryBuild(Coins.MODID, "names");

    private CoinNetwork() { }

    static void init() { }

    static void send(ServerPlayer player) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        CoinNames.current().write(buf);
        ServerPlayNetworking.send(player, ID, buf);
    }
}
