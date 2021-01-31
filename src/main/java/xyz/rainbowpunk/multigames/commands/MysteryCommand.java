package xyz.rainbowpunk.multigames.commands;

import xyz.rainbowpunk.multigames.Multigames;
import xyz.rainbowpunk.multigames.utilities.Utilities;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MysteryCommand implements CommandExecutor {
    private Multigames plugin;

    private Map<String, Test> runnableTests;

    public MysteryCommand(Multigames plugin) {
        this.plugin = plugin;
        runnableTests = new HashMap<>();

        initializeTests();
    }

    private void initializeTests() {
        runnableTests.put("particles", this::testParticles);
        runnableTests.put("titles", this::testTitles);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("Available tests:");
            sendAvailableTests(sender);
            return true;
        }

        String searchedTest = args[0];
        String[] testArguments = Arrays.copyOfRange(args, 1, args.length);

        Test test = runnableTests.get(searchedTest);

        if (test != null) test.run(sender, testArguments);
        else sender.sendMessage(searchedTest + " is not a testable argument!");

        return true;
    }

    private void sendAvailableTests(CommandSender sender) {
        for (String testName : runnableTests.keySet()) {
            sender.sendMessage(testName);
        }
    }

    private void testTitles(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("No! This is not how you're supposed to play the game!");
            return;
        }
        Player player = (Player) sender;

        for (int i = 0; i < args.length; i++) {
            args[i] = Utilities.colorText(args[i]);
        }

        String combinedArguments = String.join(" ", args);
        args = combinedArguments.split("(\\\\\\\\)"); // separates between "\\"s

        String actionBar = (args.length >= 1 && !args[0].equals("NULL")) ? args[0] : "";
        String title = (args.length >= 2 && !args[1].equals("NULL")) ? args[1] : "";
        String subtitle = (args.length >= 3 && !args[2].equals("NULL")) ? args[2] : "";
        int time = (args.length >= 4 && Integer.getInteger(args[3]) != null) ? Integer.parseInt(args[3]) : 60;

        player.sendActionBar(actionBar);
        player.sendTitle(title, subtitle, 0, time, time/3);
    }

    private void testParticles(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("No! This is not how you're supposed to play the game!");
            return;
        }
        Player player = (Player) sender;
        Utilities.spawnParticleLine(
                plugin.getMainWorld(),
                Particle.CLOUD,
                20,
                player.getLocation(),
                player.getLocation().add(0, 10, 0)
        );
        player.sendMessage("Poof!");
    }
}

interface Test {
    void run(CommandSender sender, String[] args);
}