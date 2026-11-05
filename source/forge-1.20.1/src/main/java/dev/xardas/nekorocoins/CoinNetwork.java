package dev.xardas.nekorocoins;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

/** Sends a joining player the names and enabled coins of the server. */
final class CoinNetwork {
    private static final String VERSION = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.tryBuild(Coins.MODID, "names"), () -> VERSION, VERSION::equals, VERSION::equals);

    private CoinNetwork() { }

    static void init() {
        CHANNEL.registerMessage(0, CoinNames.Data.class, CoinNames.Data::write, CoinNames.Data::read, (data, context) -> {
            context.get().enqueueWork(data::apply);
            context.get().setPacketHandled(true);
        });
    }

    static void send(ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), CoinNames.current());
    }
}
