package dev.xardas.nekorocoins;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Shared code for Fabric 1.20.1 and 1.21.1 (Mojang mappings): 120 plain items
 * coin001..coin120 and a creative tab. The IDs must never change.
 */
public class NekoroCoins implements ModInitializer {
    public static final String MODID = "nekorocoins";
    public static final int COIN_COUNT = 120;
    /** Creative tab icon: coin111 (Amethyst Cat Coin), same as in earlier versions. */
    private static final int TAB_ICON = 111;

    @Override
    public void onInitialize() {
        List<Item> coins = new ArrayList<>();
        for (int i = 1; i <= COIN_COUNT; i++) {
            coins.add(Registry.register(BuiltInRegistries.ITEM, id(String.format("coin%03d", i)), new Item(new Item.Properties())));
        }
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, id("nekoro_coins_tab"), FabricItemGroup.builder()
                .title(Component.translatable("itemGroup.nekorocoins.nekoro_coins_tab"))
                .icon(() -> coins.get(TAB_ICON - 1).getDefaultInstance())
                .displayItems((parameters, output) -> coins.forEach(output::accept))
                .build());
    }

    /** tryBuild exists in both 1.20.1 and 1.21.1, unlike the constructor (1.20.1) and fromNamespaceAndPath (1.21). */
    private static ResourceLocation id(String path) {
        return ResourceLocation.tryBuild(MODID, path);
    }
}
