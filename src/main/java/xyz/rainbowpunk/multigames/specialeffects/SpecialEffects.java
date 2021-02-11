package xyz.rainbowpunk.multigames.specialeffects;

import org.bukkit.entity.Player;

public class SpecialEffects {
    /**
     * Deletes the player's inventory in a very exaggerated manner.
     */
    public static void dissolveInventory(Player player) {
        DissolveInventory.perform(player);
    }
}
