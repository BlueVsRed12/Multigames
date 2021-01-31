package xyz.rainbowpunk.multigames.commands;

import xyz.rainbowpunk.multigames.Multigames;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ColorCommand implements CommandExecutor {
    private Multigames plugin;

    public ColorCommand(Multigames plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            String string = args[i];
            stringBuilder.append(string);
            if (i != args.length-1) stringBuilder.append(' ');
        }
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', stringBuilder.toString()));
        return true;
    }
}
