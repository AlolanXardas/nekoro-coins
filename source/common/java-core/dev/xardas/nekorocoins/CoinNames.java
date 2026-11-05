package dev.xardas.nekorocoins;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.FriendlyByteBuf;

/**
 * The names and the enabled coins as this game knows them: from its own coins.json, and on a server from the server,
 * which sends them when a player joins, so everyone sees the same names.
 */
public final class CoinNames {
    private static Data current = new Data(List.of(), List.of());

    private CoinNames() { }

    /** What the server sends. The same format on every loader. */
    public record Data(List<String> names, List<Boolean> enabled) {
        public void write(FriendlyByteBuf buf) {
            buf.writeVarInt(names.size());
            for (int i = 0; i < names.size(); i++) {
                buf.writeUtf(names.get(i));
                buf.writeBoolean(enabled.get(i));
            }
        }

        public static Data read(FriendlyByteBuf buf) {
            int size = buf.readVarInt();
            List<String> names = new ArrayList<>(size);
            List<Boolean> enabled = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                names.add(buf.readUtf());
                enabled.add(buf.readBoolean());
            }
            return new Data(names, enabled);
        }

        public void apply() {
            current = this;
        }
    }

    public static Data current() {
        return current;
    }

    /** After coins.json was read. */
    static void fromConfig() {
        List<String> names = new ArrayList<>();
        List<Boolean> enabled = new ArrayList<>();
        for (int i = 1; i <= Coins.COUNT; i++) {
            names.add(CoinConfig.coin(i).name());
            enabled.add(CoinConfig.coin(i).enabled());
        }
        current = new Data(names, enabled);
    }

    /** The coin's name, or null to use the name from the language file. */
    public static String name(int number) {
        if (number > current.names().size()) return null;
        String name = current.names().get(number - 1);
        return name.isEmpty() ? null : name;
    }

    public static boolean enabled(int number) {
        return number > current.enabled().size() || current.enabled().get(number - 1);
    }
}
