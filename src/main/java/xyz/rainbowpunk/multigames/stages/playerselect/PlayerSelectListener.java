package xyz.rainbowpunk.multigames.stages.playerselect;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;
import xyz.rainbowpunk.multigames.utilities.MultiColor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Picks up events that happen, and calls methods from PlayerSelectStage.
 */
public class PlayerSelectListener implements Listener {
    private final PlayerSelectStage stage;
    private Map<UUID, MultiColor> previouslyStoodMap;

    public PlayerSelectListener(PlayerSelectStage stage) {
        this.stage = stage;
        previouslyStoodMap = new HashMap<>();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        boolean isOnGround = player.getLocation().getY() < 77.5;

        Block standingOn = getPlayerIndicatorBlock(player);
        MultiColor now = MultiColor.fromBlock(standingOn);
        MultiColor last = previouslyStoodMap.get(uuid);

        if (now == last) return; // continue only if the player moved on / off a platform
        if (last != null) stepOff(last, uuid);
        if (now != null && isOnGround) stepOnto(now, uuid);

    }

    private void stepOnto(MultiColor color, UUID uuid) {
        previouslyStoodMap.put(uuid, color);
        stage.stepOnto(color);
    }

    private void stepOff(MultiColor color, UUID uuid) {
        previouslyStoodMap.put(uuid, null);
        stage.stepOff(color);
    }

    private Block getPlayerIndicatorBlock(Player player) {
        Location center = player.getLocation();
        center.setY(ColorPlatform.INDICATOR_HEIGHT);
        if (!isAir(center.getBlock())) return center.getBlock();

        // i apologize for what you're about to witness
        // ok like the thing is, a player can be standing on a block /without/ their center being directly on top of it
        // and it's. i don't know what else to do here.
        Block xy = center.clone().add(0.3, 0.1, 0.3).getBlock(); if (!isAir(xy)) return xy;
        Block x_y = center.clone().add(-0.3, 0.0, 0.3).getBlock(); if (!isAir(x_y)) return x_y;
        Block x_y_ = center.clone().add(-0.3, 0.0, -0.3).getBlock(); if (!isAir(x_y_)) return x_y_;
        Block xy_ = center.clone().add(0.3, 0.0, -0.3).getBlock(); if (!isAir(xy_)) return xy_;
        return null;
    }

    private boolean isAir(Block block) {
        return block.getType() == Material.AIR;
    }
}
