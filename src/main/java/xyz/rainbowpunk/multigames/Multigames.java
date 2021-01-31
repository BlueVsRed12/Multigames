package xyz.rainbowpunk.multigames;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import xyz.rainbowpunk.multigames.commands.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Multigames extends JavaPlugin {
    private static Multigames instance;

    private ProtocolManager protocolManager;
    private PacketAdapter playerJoins;
    private World mainWorld;

    @Override
    public void onEnable() {
        instance = this;

        protocolManager = ProtocolLibrary.getProtocolManager();
        addPacketListeners();
        mainWorld = Bukkit.getWorlds().get(0);

        addCommands();
    }

    private void addPacketListeners() {
        playerJoins = new PacketAdapter(this, ListenerPriority.NORMAL,
                PacketType.Login.Client.START) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Bukkit.broadcastMessage(event.getPlayer().getName() + " has attempted to log in!");
            }
        };
        protocolManager.addPacketListener(playerJoins);
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

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }

    public static Multigames getInstance() {
        return instance;
    }

}
