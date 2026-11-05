package dev.xardas.nekorocoins;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class NekoroCoinsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(CoinNetwork.Payload.TYPE, (payload, context) -> payload.data().apply());
    }
}
