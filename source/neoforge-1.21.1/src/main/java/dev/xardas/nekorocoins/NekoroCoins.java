package dev.xardas.nekorocoins;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.ArrayList;
import java.util.List;

/** NeoForge 1.21.1: 120 items coin001..coin120 in the shared "Nekoro" creative tab. */
@Mod(Coins.MODID)
public class NekoroCoins {
    /**
     * One tab for the items of all Nekoro mods. Every Nekoro mod with items registers it when no other did yet and
     * adds its items to it, so it exists once whichever of them are installed.
     */
    public static final ResourceKey<CreativeModeTab> NEKORO_TAB =
            ResourceKey.create(Registries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath("nekoro", "nekoro"));

    private static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Coins.MODID);
    private static final List<DeferredItem<CoinItem>> COINS = new ArrayList<>();

    static {
        for (int i = 1; i <= Coins.COUNT; i++) {
            int number = i;
            COINS.add(ITEMS.register(Coins.itemName(i), () -> new CoinItem(number)));
        }
    }

    public NekoroCoins(IEventBus modBus) {
        CoinConfig.init(FMLPaths.CONFIGDIR.get());
        ITEMS.register(modBus);
        modBus.addListener((RegisterEvent event) -> event.register(Registries.CREATIVE_MODE_TAB, helper -> {
            if (BuiltInRegistries.CREATIVE_MODE_TAB.containsKey(NEKORO_TAB.location())) return;
            helper.register(NEKORO_TAB, CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.nekoro"))
                    .icon(() -> COINS.get(Coins.TAB_ICON - 1).toStack())
                    .build());
        }));
        modBus.addListener((BuildCreativeModeTabContentsEvent event) -> {
            if (event.getTabKey() != NEKORO_TAB) return;
            for (int i = 1; i <= Coins.COUNT; i++) {
                if (CoinNames.enabled(i)) event.accept(COINS.get(i - 1));
            }
        });
        modBus.addListener(CoinNetwork::register);
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent event) -> {
            if (event.getEntity() instanceof ServerPlayer player) CoinNetwork.send(player);
        });
    }
}
