package xyz.rainbowpunk.multigames.utilities;

import com.google.gson.*;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class CustomItems {
    private final List<String> keys;
    private final Map<String, ItemStack> itemMap;

    public CustomItems(String json) {
        keys = new LinkedList<>();
        itemMap = new HashMap<>();
        loadItems(json);
    }

    public List<String> getKeys() {
        return new LinkedList<>(keys);
    }

    public ItemStack get(String key) {
        return itemMap.get(key.toLowerCase());
    }

    private void loadItems(String json) {
        JsonElement root = new JsonParser().parse(json);
        JsonArray itemArray = root.getAsJsonArray();
        for (JsonElement item : itemArray) loadItem(item.getAsJsonObject());
    }

    private void loadItem(JsonObject json) {
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

        // This is really bad, because:
        // 1- if this is an armor piece, it'll have protection I by default
        // 2- if there are other enchantments already existing on this item,
        //        those will be hidden by the HIDE_ENCHANTS flag
        // todo: implement this better
        JsonPrimitive isSparkleJson = json.getAsJsonPrimitive("sparkle");
        if (isSparkleJson != null && isSparkleJson.getAsBoolean()) {
            meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        item.setItemMeta(meta);

        keys.add(key);
        itemMap.put(key, item);
    }
}