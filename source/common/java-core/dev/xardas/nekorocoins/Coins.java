package dev.xardas.nekorocoins;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

/** What every loader shares. The ids coin001..coin120 must never change, worlds and modpacks rely on them. */
public final class Coins {
    public static final String MODID = "nekorocoins";
    public static final int COUNT = 120;
    /** Creative tab icon: coin111 (Amethyst Cat Coin), same as in earlier versions. */
    public static final int TAB_ICON = 111;
    public static final Logger LOGGER = LogUtils.getLogger();

    private Coins() { }

    public static String itemName(int number) {
        return String.format("coin%03d", number);
    }
}
