package xyz.rainbowpunk.multigames.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import xyz.rainbowpunk.multigames.Multigames;
import xyz.rainbowpunk.multigames.stages.playerselect.PlayerSelectStage;

public class StageCommand implements CommandExecutor {
    private Multigames plugin;
    private PlayerSelectStage playerSelectStage;

    public StageCommand(Multigames plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) return true;
        if (args[0].equalsIgnoreCase("playerselect")) {
            if (args[1].equalsIgnoreCase("start")) {
                if (playerSelectStage != null) {
                    sender.sendMessage("This stage is already running!");
                    return true;
                }
                playerSelectStage = new PlayerSelectStage(plugin);
                playerSelectStage.start();
            }
            if (args[1].equalsIgnoreCase("stop")) {
                if (playerSelectStage == null) {
                    sender.sendMessage("This stage is not running!");
                    return true;
                }
                playerSelectStage.end();
                playerSelectStage = null;
            }
        }
        return true;
    }
}
