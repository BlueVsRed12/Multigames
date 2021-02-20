package xyz.rainbowpunk.multigames.stages.parkourchallenge;


import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

enum Trophy {
    OAK("Oak"),
    SPRUCE("Spruce"),
    BIRCH("Birch"),
    JUNGLE("Jungle"),
    ACACIA("Acacia"),
    DARK_OAK("Dark Oak");

    private final String name;
    private final String displayFriendlyName;

    Trophy(String displayFriendlyName) {
        name = this.name();
        this.displayFriendlyName = displayFriendlyName;
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
        return Material.getMaterial(name + "_WOOD");
    }

    public Material getStairs() {
        return Material.getMaterial(name + "_STAIRS");
    }

    public String getCustomItemKey() {
        return name + "_trophy";
    }

    public String getDisplayFriendlyName() {
        return displayFriendlyName;
    }
}