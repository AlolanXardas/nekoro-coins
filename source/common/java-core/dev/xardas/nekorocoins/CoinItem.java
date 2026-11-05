package dev.xardas.nekorocoins;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/** A plain item whose name can come from coins.json. */
public final class CoinItem extends Item {
    private final int number;

    public CoinItem(int number) {
        super(new Item.Properties());
        this.number = number;
    }

    @Override
    public Component getName(ItemStack stack) {
        String name = CoinNames.name(number);
        return name != null ? Component.literal(name) : super.getName(stack);
    }
}
