package me.bluevsred12.multigames;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public enum UniqueItem {
    PECULIAR_BOOK(
            Material.ENCHANTED_BOOK,
            "&3&l&o&n&k[ &3&k&nThe &lMetabook&3&l&o&n&k ]",
            "&7&oA pecu&kl&7&oiar book with seemingly no purpose."),
    OAK_TROPHY(
            Material.OAK_WOOD,
            "Oak Trophy",
            NameStyle.WHITE_ITALICS,
            "&7&oA strange number is carved into this trophy's side.",
            "&0&l&8[137]"
    ),
    SPRUCE_TROPHY(
            Material.SPRUCE_WOOD,
            "Spruce Trophy",
            NameStyle.WHITE_ITALICS
    ),
    BIRCH_TROPHY(
            Material.BIRCH_WOOD,
            "Birch Trophy",
            NameStyle.WHITE_ITALICS
    ),
    JUNGLE_TROPHY(
            Material.JUNGLE_WOOD,
            "Jungle Trophy",
            NameStyle.WHITE_ITALICS
    ),
    ACACIA_TROPHY(
            Material.ACACIA_WOOD,
            "Acacia Trophy",
            NameStyle.WHITE_ITALICS
    ),
    DARK_OAK_TROPHY(
            Material.DARK_OAK_WOOD,
            "Dark Oak Trophy",
            NameStyle.WHITE_ITALICS
    );

    private ItemStack itemStack;
    UniqueItem(Material material, String name, NameStyle style, String... lore) {
        initializeItemStack(material, name, style, true, lore);
    }
    UniqueItem(Material material, String name, NameStyle nameStyle) {
        initializeItemStack(material, name, nameStyle, true, (String[]) null);
    }
    UniqueItem(Material material, String name, String... lore) {
        initializeItemStack(material, name, true, lore);
    }
    UniqueItem(Material material, String name, boolean isSparkle) {
        initializeItemStack(material, name, isSparkle, (String[]) null);
    }
    UniqueItem(Material material, String name) {
        initializeItemStack(material, name, true, (String[]) null);
    }

    private void initializeItemStack(Material material, String name, NameStyle nameStyle, boolean isSparkle, String... lore) {
        if (nameStyle != null) {
            StringBuilder nameBuilder = new StringBuilder();
            if (nameStyle == NameStyle.WHITE_ITALICS) nameBuilder
                    .append("&f&o[ &f")
                    .append(name)
                    .append("&f&o ]");
            name = nameBuilder.toString();
        }
        ItemBuilder itemBuilder = new ItemBuilder(material, name, isSparkle);
        if (lore != null) itemBuilder.addLore(lore);
        itemStack = itemBuilder.getItemStack(1);
    }

    private void initializeItemStack(Material material, String name, boolean isSparkle, String... lore) {
        initializeItemStack(material, name, null, isSparkle, lore);
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public static UniqueItem getUniqueItem(String searchedItem) {
        for (UniqueItem uniqueItem : UniqueItem.values()) {
            if (uniqueItem.name().equalsIgnoreCase(searchedItem))
                return uniqueItem;
        }
        return null;
    }

    public static ItemStack getItemStack(String searchedItem) {
        UniqueItem uniqueItem = getUniqueItem(searchedItem);
        if (uniqueItem == null) return null;
        return uniqueItem.getItemStack();
    }
}

enum NameStyle {
    WHITE_ITALICS
}
