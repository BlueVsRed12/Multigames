package xyz.rainbowpunk.multigames.stages.playerselect;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.HashSet;
import java.util.Set;

import static org.bukkit.block.BlockFace.*;
import static org.bukkit.block.BlockFace.NORTH;

class ColorPlatform {
    private static final BlockFace[] ADJACENT_FACES = {NORTH, SOUTH, EAST, WEST};

    public static final int INDICATOR_HEIGHT = 0;
    public static final int SHARD_HEIGHT = 76; //todo: this value is nearly definitely wrong
    public static final int MAX_RECURSE = 500;

    private final Material verificationMaterial;
    private Set<Block> edgeBlocks;

    public ColorPlatform(World world, int seedX, int seedY) {
        verificationMaterial = Material.ACACIA_BUTTON;
        initializeEdgeBlocks(new Location(world, seedX, INDICATOR_HEIGHT, seedY));
    }

    private void initializeEdgeBlocks(Location seed) {
        edgeBlocks = new HashSet<>();
        recurse(new HashSet<>(), seed.getBlock());
    }

    private void recurse(HashSet<Block> visited, Block block) {
        if (visited.contains(block) || visited.size() > MAX_RECURSE) return;
        visited.add(block);
        if (block.getType() != verificationMaterial) return;
        edgeBlocks.add(block);
        for (BlockFace face : ADJACENT_FACES) recurse(visited, block.getRelative(face));
    }

    public void turnOn() {
        for (Block block : edgeBlocks) block.setType(Material.SEA_LANTERN);
    }

    public void turnOff() {
        for (Block block : edgeBlocks) block.setType(Material.GLASS);
    }
}
