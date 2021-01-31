package xyz.rainbowpunk.multigames.challenges.parkourchallenge;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import xyz.rainbowpunk.multigames.Multigames;

import java.util.Set;

class PlayerQuitListener implements Listener {
    private final ParkourChallenge challenge;
    private final Multigames plugin;

    private final Set<Player> competingPlayers;
    private final Team redTeam;
    private final Team blueTeam;

    PlayerQuitListener(ParkourChallenge challenge, Multigames plugin) {
        this.challenge = challenge;
        this.plugin = plugin;

        competingPlayers = challenge.getCompetingPlayers();
        redTeam = challenge.getRedTeam();
        blueTeam = challenge.getBlueTeam();
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Bukkit.broadcastMessage(player.getDisplayName() + " has been removed from the game!");

        competingPlayers.remove(player);
        redTeam.getMembers().remove(player);
        blueTeam.getMembers().remove(player);
    }
}