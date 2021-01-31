package xyz.rainbowpunk.multigames.commands;

import xyz.rainbowpunk.multigames.Multigames;
import xyz.rainbowpunk.multigames.challenges.ParkourChallenge;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ParkourCommand implements CommandExecutor {
    private Multigames plugin;

    private ParkourChallenge challenge;

    public ParkourCommand(Multigames plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("No! This is not how you're supposed to play the game!");
            return true;
        }
        if (args[0].equalsIgnoreCase("start")) {
            if (challenge != null) {
                sender.sendMessage("There's a game already ongoing!");
            } else {
                challenge = new ParkourChallenge(plugin);
            }
        }
        if (args[0].equalsIgnoreCase("stop")) {
            if (challenge == null) {
                sender.sendMessage("There are no games currently ongoing!");
            } else {
                challenge.cleanUp();
                challenge = null;
            }
        }

        return true;
    }
}
