package xyz.rainbowpunk.multigames.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.rainbowpunk.multigames.utilities.specialeffects.SpecialEffects;

public class DissolveCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        SpecialEffects.dissolveInventory(player);
        return true;
    }
}
