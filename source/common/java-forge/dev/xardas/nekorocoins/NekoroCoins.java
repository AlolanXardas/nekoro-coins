package dev.xardas.nekorocoins;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Shared code for Forge 1.20.1 and 1.21.1: 120 items coin001..coin120 and the "Nekoro" creative tab
 * (Forge has no other Nekoro mods with items, so it stays this mod's own).
 */
@Mod(Coins.MODID)
public class NekoroCoins {
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Coins.MODID);
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Coins.MODID);
    private static final List<RegistryObject<Item>> COINS = new ArrayList<>();

    static {
        for (int i = 1; i <= Coins.COUNT; i++) {
            int number = i;
            COINS.add(ITEMS.register(Coins.itemName(i), () -> new CoinItem(number)));
        }
        TABS.register("nekoro_coins_tab", () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.nekoro"))
                .icon(() -> COINS.get(Coins.TAB_ICON - 1).get().getDefaultInstance())
                .displayItems((parameters, output) -> {
                    for (int i = 1; i <= Coins.COUNT; i++) {
                        if (CoinNames.enabled(i)) output.accept(COINS.get(i - 1).get());
                    }
                })
                .build());
    }

    @SuppressWarnings("removal") // get() works on both 47.x and 52.x; the context constructor is missing in some 47.x builds
    public NekoroCoins() {
        CoinConfig.init(FMLPaths.CONFIGDIR.get());
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(modBus);
        TABS.register(modBus);
        CoinNetwork.init();
        MinecraftForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent event) -> {
            if (event.getEntity() instanceof ServerPlayer player) CoinNetwork.send(player);
        });
    }
}
