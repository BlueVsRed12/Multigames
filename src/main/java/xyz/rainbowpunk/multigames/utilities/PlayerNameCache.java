package xyz.rainbowpunk.multigames.utilities;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.rainbowpunk.multigames.Multigames;

import java.util.*;

public class PlayerNameCache {
    private final Multigames plugin;
    private final Listener playerJoinListener;
    private final Map<UUID, String> nameMap;

    public PlayerNameCache(Multigames plugin) {
        this(plugin, new HashSet<>());
    }

    public PlayerNameCache(Multigames plugin, Set<Player> onlinePlayers) {
        this.plugin = plugin;

        playerJoinListener = new Listener() {
            @EventHandler
            public void onPlayerJoin(PlayerJoinEvent event) {
                cachePlayer(event.getPlayer());
            }
        };
        plugin.registerListener(playerJoinListener);

        nameMap = new HashMap<>();
        for (Player player : onlinePlayers) cachePlayer(player);
    }

    public String getPlayerName(UUID uuid) {
        return nameMap.get(uuid);
    }

    private void cachePlayer(Player player) {
        UUID uuid = player.getUniqueId();
        String name = player.getName();
        nameMap.put(uuid, name);
    }

    public void cleanUp() {
        plugin.unregisterListener(playerJoinListener);
    }
}
