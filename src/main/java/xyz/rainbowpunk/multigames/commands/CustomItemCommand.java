package xyz.rainbowpunk.multigames.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.rainbowpunk.multigames.utilities.CustomItems;

import java.util.stream.Collectors;

public class CustomItemCommand implements CommandExecutor {
    private final CustomItems customItems;

    public CustomItemCommand(CustomItems customItems) {
        this.customItems = customItems;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(String.join("\n", customItems.getKeys()));
        }
        if (args.length == 1) {
            if (!(sender instanceof Player)) return true;
            Player player = (Player) sender;
            player.getInventory().addItem(customItems.get(args[0]));
        }
        return true;
    }
}
