package xyz.rainbowpunk.multigames;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {
    private Material material;
    private String name;
    private boolean isSparkle;
    private List<String> lore;

    public ItemBuilder(Material material) {
        this.material = material;
        name = null;
        isSparkle = false;
        lore = new ArrayList<>();
    }

    public ItemBuilder(Material material, String formattedName, boolean isSparkle) {
        this.material = material;
        name = ChatColor.translateAlternateColorCodes('&', formattedName);
        this.isSparkle = isSparkle;
        lore = new ArrayList<>();
    }

    public ItemBuilder addLore(String[] lines) {
        for (String line : lines) {
            lore.add(ChatColor.translateAlternateColorCodes('&', line));
        }
        return this;
    }

    public ItemBuilder addLoreLine(String line) {
        lore.add(ChatColor.translateAlternateColorCodes('&', line));
        return this;
    }

    public ItemStack getItemStack(int count) {
        ItemStack item = new ItemStack(material, count);
        if (isSparkle) item.addUnsafeEnchantment(Enchantment.LUCK, 1);

        ItemMeta meta = item.getItemMeta();
        if (name != null) meta.setDisplayName(name);
        if (isSparkle) meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        if (!lore.isEmpty()) meta.setLore(lore);

        item.setItemMeta(meta);
        return item;
    }
}
