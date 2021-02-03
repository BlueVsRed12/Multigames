package xyz.rainbowpunk.multigames.commands;

import xyz.rainbowpunk.multigames.utilities.UniqueItem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class UniqueItemCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You must run this command as a player!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length > 1) {
            sender.sendMessage("Too many arguments!");
            return true;
        }

        if (args.length == 0) {
            // Send the player a list of all UniqueItem's
            for (UniqueItem uniqueItem : UniqueItem.values()) {
                StringBuilder message = new StringBuilder();

                ItemStack item = uniqueItem.getItemStack();
                String displayName = item.getI18NDisplayName();
                if (displayName != null) message.append(displayName);
                else message.append(item.getType().toString());

                message.append(" (").append(uniqueItem.toString()).append(")");
                player.sendMessage(message.toString());
            }
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("everything!")) {
                for (UniqueItem uniqueItem : UniqueItem.values()) {
                    player.getWorld().dropItem(
                            player.getLocation(),
                            uniqueItem.getItemStack()
                    );
                }
                return true;
            }

            String searchedItem = args[0].toUpperCase();

            UniqueItem uniqueItem = UniqueItem.getUniqueItem(searchedItem);
            if (uniqueItem == null) {
                player.sendMessage(searchedItem + " does not exist!");
                return true;
            }

            player.getInventory().addItem(uniqueItem.getItemStack());
            player.sendMessage("Obtained " + uniqueItem + "!");

            return true;
        }
        return true;
    }
}
