package xyz.rainbowpunk.multigames.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.rainbowpunk.multigames.competition.Competition;

public class CompetitionCommand implements CommandExecutor {
    private final Competition competition;

    public CompetitionCommand(Competition competition) {
        this.competition = competition;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) sendStatus(sender);
        if (args.length == 1) {
            if (args[0].equals("status")) sendStatus(sender);
            if (args[0].equals("join")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("You must be a player to run this command!");
                    return true;
                }
                Player player = (Player) sender;
                competition.addCompetitor(player);
                System.out.println(competition);
            }
        }
        return true;
    }

    private void sendStatus(CommandSender sender) {
        sender.sendMessage(competition.toString());
    }
}
