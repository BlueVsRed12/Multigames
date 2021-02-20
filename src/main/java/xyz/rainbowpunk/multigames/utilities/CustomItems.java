package xyz.rainbowpunk.multigames.utilities;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomItems {
    private final Map<String, ItemStack> itemMap;

    public CustomItems(String json) {
        itemMap = new HashMap<>();
        loadItems(json);
    }

    public ItemStack get(String key) {
        return itemMap.get(key.toLowerCase());
    }

    private void loadItems(String json) {
        JsonElement root = new JsonParser().parse(json);
        JsonArray itemArray = root.getAsJsonArray();
        for (JsonElement item : itemArray) loadItems(item.getAsJsonObject());
    }

    private void loadItems(JsonObject json) {
        String key = json.getAsJsonPrimitive("key").getAsString();

        Material material = Material.matchMaterial(json.getAsJsonPrimitive("item").getAsString());
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        String title = json.getAsJsonPrimitive("title").getAsString();
        title = Utilities.colorText(title); // make sure minecraft colors work
        meta.setDisplayName(title);

        if (json.has("lore")) {
            List<String> lore = new ArrayList<>();
            JsonArray loreArray = json.getAsJsonArray("lore");
            for (JsonElement loreLine : loreArray) lore.add(Utilities.colorText(loreLine.getAsString()));
            meta.setLore(lore);
        }

        item.setItemMeta(meta);

        itemMap.put(key, item);
    }
}
