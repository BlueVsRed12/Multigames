package xyz.rainbowpunk.multigames.challenges;

import xyz.rainbowpunk.multigames.utilities.PlayerNotifier;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashSet;
import java.util.Set;

public class PushTheButtonChallenge {
    private Set<Player> teamRed;
    private Set<Player> teamBlue;

    private PlayerNotifier notifier;

    public PushTheButtonChallenge(Set<Player> players) {
        teamRed = new HashSet<>();
        teamBlue = new HashSet<>();

        int c = 0;
        for (Player player : players) {
            if (c >= players.size() / 2)
                teamBlue.add(player);
            else
                teamRed.add(player);
            c++;
        }

        notifier = new PlayerNotifier();
        notifier.addReceivers(teamBlue);
        notifier.addReceivers(teamRed);

        start();
    }

    public void start() {
        notifier.sendTitle("Button challenge started!", "", 100);
    }
}

class PlayerLeaveListener implements Listener {

}

class ButtonPressListener implements Listener {
    private final Material VERIFICATION_BLOCK;

    ButtonPressListener(Material verificationBlock) {
        VERIFICATION_BLOCK = verificationBlock;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock != null && clickedBlock.getType() == Material.BIRCH_BUTTON) {
            buttonPressed(clickedBlock);
        }
    }

    public void buttonPressed(Block clickedBlock) {
        if (isValidButton(clickedBlock)) return;

    }

    private boolean isValidButton(Block block) {
        Block verificationBlock = block.getRelative(0, -3, 0);
        return verificationBlock.getType() == VERIFICATION_BLOCK;
    }
}
