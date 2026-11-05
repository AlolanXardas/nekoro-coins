package dev.xardas.nekorocoins;

import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

/** The recipes of the coins are written from coins.json each time recipes are loaded, as if they came from a data pack. */
public final class CoinRecipes {
    private CoinRecipes() { }

    public static void add(Map<ResourceLocation, JsonElement> recipes) {
        CoinConfig.load();
        for (int i = 1; i <= Coins.COUNT; i++) {
            CoinConfig.Coin coin = CoinConfig.coin(i);
            if (!coin.enabled() || coin.recipeItem().isEmpty()) continue;
            JsonObject ingredient = ingredient(coin.recipeItem());
            if (ingredient == null) {
                Coins.LOGGER.warn("Unknown recipe_item \"{}\" of {} in coins.json, no recipe", coin.recipeItem(), Coins.itemName(i));
                continue;
            }
            ResourceLocation id = ResourceLocation.tryBuild(Coins.MODID, Coins.itemName(i));
            recipes.put(id, recipe(id, ingredient, coin));
        }
    }

    /** {"item": ...} or {"tag": ...}, or null for an item the game does not have. */
    private static JsonObject ingredient(String text) {
        boolean tag = text.startsWith("#");
        ResourceLocation id = ResourceLocation.tryParse(tag ? text.substring(1) : text);
        if (id == null || (!tag && !BuiltInRegistries.ITEM.containsKey(id))) return null;
        JsonObject out = new JsonObject();
        out.addProperty(tag ? "tag" : "item", id.toString());
        return out;
    }

    private static JsonObject recipe(ResourceLocation id, JsonObject ingredient, CoinConfig.Coin coin) {
        JsonObject recipe = new JsonObject();
        recipe.addProperty("type", "minecraft:crafting_shapeless");
        recipe.addProperty("category", "misc");
        JsonArray ingredients = new JsonArray();
        for (int i = 0; i < coin.recipeCount(); i++) ingredients.add(ingredient);
        recipe.add("ingredients", ingredients);
        JsonObject result = new JsonObject();
        // 1.20.1 reads "item", 1.21 reads "id"; each ignores the other
        result.addProperty("item", id.toString());
        result.addProperty("id", id.toString());
        result.addProperty("count", coin.resultCount());
        recipe.add("result", result);
        return recipe;
    }
}
