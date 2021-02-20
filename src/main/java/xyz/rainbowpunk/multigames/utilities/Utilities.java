package xyz.rainbowpunk.multigames.utilities;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class Utilities {
    public static void spawnParticleLine(World world, Particle particle, int count, Location start, Location end) {
        start = start.clone();
        end = end.clone();
        Vector direction = end.subtract(start).toVector();
        direction.multiply(1.0 / count);
        for (int i = 0; i < count; i++) {
            world.spawnParticle(particle, start, 1, 0, 0, 0, 0);
            start.add(direction);
        }
    }

    public static String convertToTimeFormat(int seconds) {
        if (Math.abs(seconds) < 60) return String.format("%01d", seconds % 60);
        return String.format("%01d:%02d", seconds / 60, seconds % 60);
    }

    public static String colorText(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String resourceToString(String path) {
        InputStream input = Utilities.class.getResourceAsStream(path);
        return new BufferedReader(new InputStreamReader(input)).lines().collect(Collectors.joining("\n"));
    }
}
