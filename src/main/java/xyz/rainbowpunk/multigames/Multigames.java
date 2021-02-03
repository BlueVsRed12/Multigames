package xyz.rainbowpunk.multigames;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.rainbowpunk.multigames.commands.*;
import xyz.rainbowpunk.multigames.competition.Competition;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Multigames extends JavaPlugin {
    private static Multigames instance;

    private World mainWorld;
    private Competition competition;

    @Override
    public void onEnable() {
        instance = this;
        mainWorld = Bukkit.getWorlds().get(0);
        addCommands();
    }

    private void addCommands() {
        getCommand("color").setExecutor(new ColorCommand(this));
        getCommand("parkour").setExecutor(new ParkourCommand(this));
        getCommand("uniqueitem").setExecutor(new UniqueItemCommand());
        getCommand("mystery").setExecutor(new MysteryCommand(this));
        getCommand("dissolve").setExecutor(new DissolveCommand());
    }

    public World getMainWorld() {
        return mainWorld;
    }

    public Set<UUID> getOnlinePlayerUUIDs() {
        Set<UUID> onlinePlayers = new HashSet<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            onlinePlayers.add(player.getUniqueId());
        }
        return onlinePlayers;
    }

    public Set<Player> getOnlinePlayers() {
        return new HashSet<>(Bukkit.getOnlinePlayers());
    }

    public static Multigames getInstance() {
        return instance;
    }

}
