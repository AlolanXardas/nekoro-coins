package dev.xardas.nekorocoins;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

/** Shared code for Fabric 1.20.1 and 1.21.1 (Mojang mappings): 120 items coin001..coin120 in the shared "Nekoro" creative tab. */
public class NekoroCoins implements ModInitializer {
    /**
     * One tab for the items of all Nekoro mods. Every Nekoro mod with items registers it when no other did yet and
     * adds its items to it, so it exists once whichever of them are installed.
     */
    public static final ResourceKey<CreativeModeTab> NEKORO_TAB =
            ResourceKey.create(Registries.CREATIVE_MODE_TAB, ResourceLocation.tryBuild("nekoro", "nekoro"));

    @Override
    public void onInitialize() {
        CoinConfig.init(FabricLoader.getInstance().getConfigDir());
        List<Item> coins = new ArrayList<>();
        for (int i = 1; i <= Coins.COUNT; i++) {
            coins.add(Registry.register(BuiltInRegistries.ITEM, id(Coins.itemName(i)), new CoinItem(i)));
        }
        if (!BuiltInRegistries.CREATIVE_MODE_TAB.containsKey(NEKORO_TAB.location())) {
            Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, NEKORO_TAB, FabricItemGroup.builder()
                    .title(Component.translatable("itemGroup.nekoro"))
                    .icon(() -> coins.get(Coins.TAB_ICON - 1).getDefaultInstance())
                    .build());
        }
        ItemGroupEvents.modifyEntriesEvent(NEKORO_TAB).register(entries -> {
            for (int i = 1; i <= Coins.COUNT; i++) {
                if (CoinNames.enabled(i)) entries.accept(coins.get(i - 1));
            }
        });
        CoinNetwork.init();
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> CoinNetwork.send(handler.player));
    }

    /** tryBuild exists in both 1.20.1 and 1.21.1, unlike the constructor (1.20.1) and fromNamespaceAndPath (1.21). */
    private static ResourceLocation id(String path) {
        return ResourceLocation.tryBuild(Coins.MODID, path);
    }
}
