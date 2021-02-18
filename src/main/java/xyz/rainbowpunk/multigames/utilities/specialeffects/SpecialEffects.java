package xyz.rainbowpunk.multigames.utilities.specialeffects;

import org.bukkit.entity.Player;

public class SpecialEffects {
    public static void dissolveInventory(Player player) {
        DissolveInventory.perform(player);
    }
}
