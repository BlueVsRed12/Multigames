package xyz.rainbowpunk.multigames.utilities;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.Random;

/**
 * A cuboid selection of blocks.
 */
public class Selection {
    private final Random random;

    private final World world;
    private final int x1, y1, z1, x2, y2, z2;

    /**
     * Creates a cuboid selection from the coordinates 1 to 2, inclusive.
     */
    public Selection(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
        random = new Random();
        this.world = world;
        this.x1 = Math.min(x1, x2); this.x2 = Math.max(x1, x2);
        this.y1 = Math.min(y1, y2); this.y2 = Math.max(y1, y2);
        this.z1 = Math.min(z1, z2); this.z2 = Math.max(z1, z2);
    }

    public Location getRandomLocation() {
        return new Location(world, random(x1, x2), random(y1, y2), random(z1, z2));
    }

    /**
     * @return Returns a random number between min and max, inclusive.
     */
    private int random(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }
}
