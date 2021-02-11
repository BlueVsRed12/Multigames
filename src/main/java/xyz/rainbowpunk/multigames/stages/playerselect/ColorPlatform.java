package xyz.rainbowpunk.multigames.stages.playerselect;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import xyz.rainbowpunk.multigames.utilities.MultiColor;

import java.util.HashSet;
import java.util.Set;

import static org.bukkit.block.BlockFace.*;
import static org.bukkit.block.BlockFace.NORTH;

class ColorPlatform {
    private static final BlockFace[] ADJACENT_FACES = {NORTH, SOUTH, EAST, WEST};

    public static final int INDICATOR_HEIGHT = 0;
    public static final int SHARD_HEIGHT = 76; //todo: this value is nearly definitely wrong
    public static final int MAX_RECURSE = 750;

    private MultiColor color;
    private Material verificationMaterial;
    private Material fillMaterial;
    private Set<Block> edgeBlocks;

    public ColorPlatform(World world, int seedX, int seedY) {
        Block seed = new Location(world, seedX, INDICATOR_HEIGHT, seedY).getBlock();
        initializeColor(seed);
        initializeEdgeBlocks(seed);
    }

    private void initializeColor(Block seed) {
        color = MultiColor.fromBlock(seed);
        verificationMaterial = color.getStainedGlass();
        fillMaterial = color.getConcrete();
    }

    private void initializeEdgeBlocks(Block seed) {
        edgeBlocks = new HashSet<>();
        recurse(new HashSet<>(), seed);
    }

    private void recurse(HashSet<Block> visited, Block block) {
        if (visited.contains(block) || visited.size() > MAX_RECURSE) return;
        visited.add(block);
        if (block.getType() != verificationMaterial) return;
        if (adjacentToAir(block)) edgeBlocks.add(getShardRelativeBlock(block));
        for (BlockFace face : ADJACENT_FACES) recurse(visited, block.getRelative(face));
    }

    private boolean adjacentToAir(Block block) {
        for (BlockFace face : ADJACENT_FACES)
            if (block.getRelative(face).getType() == Material.AIR) return true;
        return false;
    }

    static Block getShardRelativeBlock(Block block) {
        Location location = block.getLocation();
        location.setY(SHARD_HEIGHT);
        return location.getBlock();
    }

    public void turnOn() {
        for (Block block : edgeBlocks) block.setType(fillMaterial);
    }

    public void turnOff() {
        for (Block block : edgeBlocks) block.setType(verificationMaterial);
    }

    public MultiColor getColor() {
        return color;
    }
}
