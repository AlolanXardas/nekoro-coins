package dev.xardas.nekorocoins;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.List;

/**
 * NeoForge 1.21.1: 120 plain items coin001..coin120 and a creative tab.
 * The IDs must never change, worlds and modpacks rely on them.
 */
@Mod(NekoroCoins.MODID)
public class NekoroCoins {
    public static final String MODID = "nekorocoins";
    public static final int COIN_COUNT = 120;
    /** Creative tab icon: coin111 (Amethyst Cat Coin), same as in earlier versions. */
    private static final int TAB_ICON = 111;

    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    private static final List<DeferredItem<Item>> COINS = new ArrayList<>();

    static {
        for (int i = 1; i <= COIN_COUNT; i++) {
            COINS.add(ITEMS.registerSimpleItem(String.format("coin%03d", i)));
        }
        TABS.register("nekoro_coins_tab", () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.nekorocoins.nekoro_coins_tab"))
                .icon(() -> COINS.get(TAB_ICON - 1).toStack())
                .displayItems((parameters, output) -> COINS.forEach(output::accept))
                .build());
    }

    public NekoroCoins(IEventBus modBus) {
        ITEMS.register(modBus);
        TABS.register(modBus);
    }
}
