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
    private final static int PLAYER_SHARD_HEIGHT = ColorPlatform.SHARD_HEIGHT + 1;
    private final static double DETECTION_FUZZINESS = 0.5;

    private final PlayerSelectStage stage;
    private final Map<UUID, MultiColor> previouslyStoodMap;

    public PlayerSelectListener(PlayerSelectStage stage) {
        this.stage = stage;
        previouslyStoodMap = new HashMap<>();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer(); UUID uuid = player.getUniqueId();

        Block standingOn = getPlayerIndicatorBlock(player);
        MultiColor now = MultiColor.fromBlock(standingOn);
        MultiColor last = previouslyStoodMap.get(uuid);

        if (now == last) return; // continue only if the player moved on / off a platform
        if (last != null) stepOff(last, uuid);
        double yPos = player.getLocation().getY();
        double yVelocity = Math.abs(player.getVelocity().getY());
        boolean isOnGround = yPos < PLAYER_SHARD_HEIGHT + DETECTION_FUZZINESS
                && yVelocity < 0.1;

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

        // todo: this can be improved so hard
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
