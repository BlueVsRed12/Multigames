package me.bluevsred12.multigames;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import me.bluevsred12.multigames.commands.ColorCommand;
import me.bluevsred12.multigames.commands.MysteryCommand;
import me.bluevsred12.multigames.commands.ParkourCommand;
import me.bluevsred12.multigames.commands.UniqueItemCommand;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Multigames extends JavaPlugin {
    private ProtocolManager protocolManager;

    private PacketAdapter playerJoins;

    private World mainWorld;

    @Override
    public void onEnable() {
        protocolManager = ProtocolLibrary.getProtocolManager();
        mainWorld = Bukkit.getWorlds().get(0);
        addPacketListeners();
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
        try {
            getCommand("color").setExecutor(new ColorCommand(this));
            getCommand("parkour").setExecutor(new ParkourCommand(this));
            getCommand("uniqueitem").setExecutor(new UniqueItemCommand());
            getCommand("mystery").setExecutor(new MysteryCommand(this));
        }
        catch(Exception E) { System.out.println(E); }
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

}
