package xyz.rainbowpunk.multigames.stages.parkourchallenge;


import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import xyz.rainbowpunk.multigames.utilities.UniqueItem;

enum Trophy {
    OAK("Oak"),
    SPRUCE("Spruce"),
    BIRCH("Birch"),
    JUNGLE("Jungle"),
    ACACIA("Acacia"),
    DARK_OAK("Dark Oak");

    private final String nameID;
    private final String displayFriendlyName;
    private final ItemStack itemStack;

    Trophy(String displayFriendlyName) {
        nameID = this.name();
        this.displayFriendlyName = displayFriendlyName;
        itemStack = UniqueItem.getItemStack(nameID + "_TROPHY");
    }

    public static Trophy getTrophy(Material searchedMaterial) {
        for (Trophy trophy : Trophy.values()) {
            if (trophy.getWood().equals(searchedMaterial)) return trophy;
        }
        return null;
    }

    public static Trophy getTrophy(Block block) {
        if (block == null) return null;
        return getTrophy(block.getType());
    }

    public Material getWood() {
        return Material.getMaterial(nameID + "_WOOD");
    }

    public Material getStairs() {
        return Material.getMaterial(nameID + "_STAIRS");
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public String getDisplayFriendlyName() {
        return displayFriendlyName;
    }
}