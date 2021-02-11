package xyz.rainbowpunk.multigames.utilities;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public enum MultiColor {
    BROWN,
    RED,
    ORANGE,
    YELLOW,
    LIME,
    GREEN,
    CYAN,
    LIGHT_BLUE,
    BLUE,
    PURPLE,
    MAGENTA,
    PINK,
    WHITE,
    LIGHT_GRAY,
    GRAY,
    BLACK;

    private final String materialString;
    MultiColor() {
        materialString = name().toUpperCase();
    }

    public Material getStainedGlass() {
         return getMaterial("STAINED_GLASS");
    }

    public Material getWool() {
        return getMaterial("WOOL");
    }

    private Material getMaterial(String material) {
        return Material.getMaterial(materialString + "_" + material);
    }

    public static MultiColor fromLocation(Location location) {
        return fromBlock(location.getBlock());
    }

    public static MultiColor fromBlock(Block block) {
        return fromMaterial(block.getType());
    }

    public static MultiColor fromMaterial(Material material) {
        for (MultiColor color : MultiColor.values()) {
            if (material.toString().startsWith(color.toString())) return color;
        }
        return null;
    }
}
