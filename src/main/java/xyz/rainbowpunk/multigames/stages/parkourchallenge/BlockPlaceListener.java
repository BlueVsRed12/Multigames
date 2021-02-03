package xyz.rainbowpunk.multigames.stages.parkourchallenge;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.rainbowpunk.multigames.Multigames;



class BlockPlaceListener implements Listener {
    private final ParkourChallenge challenge;
    private final Multigames plugin;

    BlockPlaceListener(ParkourChallenge challenge, Multigames plugin) {
        this.challenge = challenge;
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        trophyPlacement(event);
    }

    private void trophyPlacement(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block placedBlock = event.getBlockPlaced();
        Trophy trophy = Trophy.getTrophy(placedBlock);

        TrophyPlacementType placementType = challenge.determineTrophyPlacementType(player, placedBlock);
        if (placementType == TrophyPlacementType.INVALID
                || placementType == TrophyPlacementType.MISMATCHED_PODIUM
                || placementType == TrophyPlacementType.PODIUMS_CLOGGED)
            event.setBuild(false);
        if (placementType == TrophyPlacementType.PODIUM_COLLECT) {
            challenge.placeTrophy(player, trophy);
            challenge.animateTrophyPlacement(player, trophy, placedBlock);
        }
        if (placementType == TrophyPlacementType.SHATTER) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    challenge.animateTrophyShatter(player, trophy, placedBlock);
                    challenge.shatterTrophy(player, trophy);
                }
            }.runTaskLater(plugin, 5);
        }
    }
}
