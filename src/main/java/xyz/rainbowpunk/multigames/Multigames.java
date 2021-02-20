package xyz.rainbowpunk.multigames;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.rainbowpunk.multigames.commands.*;
import xyz.rainbowpunk.multigames.competition.Competition;
import xyz.rainbowpunk.multigames.utilities.CustomItems;
import xyz.rainbowpunk.multigames.utilities.PlayerNameCache;
import xyz.rainbowpunk.multigames.utilities.Utilities;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Multigames extends JavaPlugin {
    private static Multigames instance;

    private World mainWorld;
    private Competition competition;
    private PlayerNameCache playerNameCache;
    private CustomItems customItems;

    // initialization
    @Override
    public void onEnable() {
        instance = this;

        mainWorld = Bukkit.getWorlds().get(0);
        competition = new Competition(this);
        playerNameCache = new PlayerNameCache(this, getOnlinePlayers());
        customItems = new CustomItems(Utilities.resourceToString("/customitems.json"));

        addCommands();
    }

    @Override
    public void onDisable() {
        playerNameCache.cleanUp();
    }

    private void addCommands() {
        getCommand("color").setExecutor(new ColorCommand(this));
        getCommand("parkour").setExecutor(new ParkourCommand(this));
        getCommand("mystery").setExecutor(new MysteryCommand(this));
        getCommand("dissolve").setExecutor(new DissolveCommand());
        getCommand("stage").setExecutor(new StageCommand(this));
        getCommand("competition").setExecutor(new CompetitionCommand(competition));
    }

    // accessors and mutators
    public Competition getCompetition() {
        return competition;
    }

    public CustomItems getCustomItems() {
        return customItems;
    }

    public String getPlayerName(UUID uuid) {
        return playerNameCache.getPlayerName(uuid);
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

    public void registerListener(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    public void unregisterListener(Listener listener) {
        HandlerList.unregisterAll(listener);
    }
}
