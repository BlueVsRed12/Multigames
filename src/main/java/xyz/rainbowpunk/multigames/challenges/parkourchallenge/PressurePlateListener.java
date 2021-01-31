package xyz.rainbowpunk.multigames.challenges.parkourchallenge;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import xyz.rainbowpunk.multigames.Multigames;

class PressurePlateListener implements Listener {
    private final ParkourChallenge challenge;
    private final Multigames plugin;

    PressurePlateListener(ParkourChallenge challenge, Multigames plugin) {
        this.challenge = challenge;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Block clickedBlock = e.getClickedBlock();

        // When the player steps on a trophy pressure plate during a match.
        if (
                challenge.isPlayingPeriod()
                        && e.getAction().equals(Action.PHYSICAL)
                        && clickedBlock != null
                        && clickedBlock.getType() == Material.HEAVY_WEIGHTED_PRESSURE_PLATE) {
            if (challenge.pickupTrophy(player, clickedBlock.getLocation().add(0, -1, 0).getBlock()))
                e.setCancelled(true);
        }
    }
}