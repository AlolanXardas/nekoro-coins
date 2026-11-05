package dev.xardas.nekorocoins;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

/** Sends a joining player the names and enabled coins of the server. */
final class CoinNetwork {
    record Payload(CoinNames.Data data) implements CustomPacketPayload {
        static final Type<Payload> TYPE = new Type<>(ResourceLocation.tryBuild(Coins.MODID, "names"));
        static final StreamCodec<FriendlyByteBuf, Payload> CODEC =
                StreamCodec.of((buf, payload) -> payload.data().write(buf), buf -> new Payload(CoinNames.Data.read(buf)));

        @Override
        public Type<Payload> type() {
            return TYPE;
        }
    }

    private CoinNetwork() { }

    static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToClient(Payload.TYPE, Payload.CODEC, (payload, context) -> payload.data().apply());
    }

    static void send(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new Payload(CoinNames.current()));
    }
}
