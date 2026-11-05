package dev.xardas.nekorocoins;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.stream.JsonReader;

/** Reads config/Nekoro/Coins/coins.json, which is written on first start with the names from the language file. */
public final class CoinConfig {
    /** One coin. An empty name means the language file, an empty recipe item means no recipe. */
    public record Coin(String name, boolean enabled, String recipeItem, int recipeCount, int resultCount) { }

    private static final String HEADER = """
            // Nekoro Coins. One line for each coin; the ids coin001 to coin120 never change.
            // name: what the coin is called. Empty uses the language file. On a server everyone sees the server's names.
            // enabled: false takes the coin out of the creative tab and removes its recipe. Coins players already have stay.
            // recipe_item: what the coin is crafted from: an item ("minecraft:gold_ingot") or a tag ("#minecraft:planks"). Empty for no recipe.
            // recipe_count: how many of recipe_item go into the crafting grid, 1 to 9.
            // result_count: how many coins the recipe gives, 1 to 64.
            """;

    private static Path file = Path.of("config", "Nekoro", "Coins", "coins.json");
    private static Coin[] coins = new Coin[0];

    private CoinConfig() { }

    /** Called by the loader entry point with its config folder. */
    public static void init(Path configDir) {
        file = configDir.resolve("Nekoro").resolve("Coins").resolve("coins.json");
        load();
    }

    /** Reads the file, or writes it when it is missing. A coin that cannot be read keeps its default. */
    public static void load() {
        JsonObject names = languageFile();
        JsonObject root = read(names);
        Coin[] loaded = new Coin[Coins.COUNT];
        for (int i = 1; i <= Coins.COUNT; i++) {
            String id = Coins.itemName(i);
            String name = defaultName(names, i);
            try {
                JsonObject o = root.has(id) ? root.getAsJsonObject(id) : new JsonObject();
                loaded[i - 1] = new Coin(
                        o.has("name") ? o.get("name").getAsString() : name,
                        !o.has("enabled") || o.get("enabled").getAsBoolean(),
                        o.has("recipe_item") ? o.get("recipe_item").getAsString().trim() : "",
                        o.has("recipe_count") ? clamp(o.get("recipe_count").getAsInt(), 1, 9) : 1,
                        o.has("result_count") ? clamp(o.get("result_count").getAsInt(), 1, 64) : 1);
            } catch (RuntimeException e) {
                Coins.LOGGER.warn("Could not read {} in coins.json, using its defaults", id, e);
                loaded[i - 1] = new Coin(name, true, "", 1, 1);
            }
        }
        coins = loaded;
        CoinNames.fromConfig();
    }

    public static Coin coin(int number) {
        return coins[number - 1];
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static JsonObject read(JsonObject names) {
        String defaults = defaults(names);
        try {
            if (Files.notExists(file)) {
                Files.createDirectories(file.getParent());
                Files.writeString(file, defaults, StandardCharsets.UTF_8);
            }
            try (Reader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                return parse(reader);
            }
        } catch (IOException | RuntimeException e) {
            Coins.LOGGER.error("Could not read {}, using the defaults", file, e);
            return parse(new StringReader(defaults));
        }
    }

    private static JsonObject parse(Reader reader) {
        JsonReader json = new JsonReader(reader);
        json.setLenient(true);
        return JsonParser.parseReader(json).getAsJsonObject();
    }

    private static String defaults(JsonObject names) {
        StringBuilder out = new StringBuilder(HEADER).append("{\n");
        for (int i = 1; i <= Coins.COUNT; i++) {
            out.append("  \"").append(Coins.itemName(i)).append("\": { \"name\": ").append(new JsonPrimitive(defaultName(names, i)))
                    .append(", \"enabled\": true, \"recipe_item\": \"\", \"recipe_count\": 1, \"result_count\": 1 }")
                    .append(i < Coins.COUNT ? ",\n" : "\n");
        }
        return out.append("}\n").toString();
    }

    private static String defaultName(JsonObject names, int number) {
        String key = "item." + Coins.MODID + "." + Coins.itemName(number);
        return names.has(key) ? names.get(key).getAsString() : "";
    }

    private static JsonObject languageFile() {
        String path = "/assets/" + Coins.MODID + "/lang/en_us.json";
        try (InputStream in = CoinConfig.class.getResourceAsStream(path)) {
            if (in == null) throw new IOException(path + " is missing from the jar");
            return parse(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (IOException | RuntimeException e) {
            Coins.LOGGER.error("Could not read the default coin names", e);
            return new JsonObject();
        }
    }
}
