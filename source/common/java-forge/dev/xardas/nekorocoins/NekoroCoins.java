package dev.xardas.nekorocoins;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Shared code for Forge 1.20.1 and 1.21.1: 120 plain items coin001..coin120
 * and a creative tab. The IDs must never change, worlds and modpacks rely on them.
 */
@Mod(NekoroCoins.MODID)
public class NekoroCoins {
    public static final String MODID = "nekorocoins";
    public static final int COIN_COUNT = 120;
    /** Creative tab icon: coin111 (Amethyst Cat Coin), same as in earlier versions. */
    private static final int TAB_ICON = 111;

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    private static final List<RegistryObject<Item>> COINS = new ArrayList<>();

    static {
        for (int i = 1; i <= COIN_COUNT; i++) {
            COINS.add(ITEMS.register(String.format("coin%03d", i), () -> new Item(new Item.Properties())));
        }
        TABS.register("nekoro_coins_tab", () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.nekorocoins.nekoro_coins_tab"))
                .icon(() -> COINS.get(TAB_ICON - 1).get().getDefaultInstance())
                .displayItems((parameters, output) -> COINS.forEach(coin -> output.accept(coin.get())))
                .build());
    }

    @SuppressWarnings("removal") // get() works on both 47.x and 52.x; the context constructor is missing in some 47.x builds
    public NekoroCoins() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modBus);
        TABS.register(modBus);
    }
}
