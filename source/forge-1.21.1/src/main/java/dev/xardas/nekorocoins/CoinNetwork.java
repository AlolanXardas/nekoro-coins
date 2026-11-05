package dev.xardas.nekorocoins;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

/** Sends a joining player the names and enabled coins of the server. */
final class CoinNetwork {
    private static final SimpleChannel CHANNEL = ChannelBuilder.named(ResourceLocation.tryBuild(Coins.MODID, "names"))
            .networkProtocolVersion(1).simpleChannel();

    private CoinNetwork() { }

    static void init() {
        CHANNEL.messageBuilder(CoinNames.Data.class)
                .encoder(CoinNames.Data::write)
                .decoder(CoinNames.Data::read)
                .consumerMainThread((data, context) -> data.apply())
                .add();
    }

    static void send(ServerPlayer player) {
        CHANNEL.send(CoinNames.current(), PacketDistributor.PLAYER.with(player));
    }
}
