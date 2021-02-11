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

    public Material getConcrete() {
        return getMaterial("CONCRETE");
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
        return location != null ? fromBlock(location.getBlock()) : null;
    }

    public static MultiColor fromBlock(Block block) {
        return block != null ? fromMaterial(block.getType()) : null;
    }

    public static MultiColor fromMaterial(Material material) {
        if (material == null) return null;
        for (MultiColor color : MultiColor.values()) {
            if (material.toString().startsWith(color.toString())) return color;
        }
        return null;
    }
}
