package xyz.rainbowpunk.multigames.challenges.parkourchallenge;


import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;
import xyz.rainbowpunk.multigames.Multigames;

import java.util.HashSet;
import java.util.Set;

class PlayerMoveListener implements Listener {
    private final ParkourChallenge challenge;
    private final Multigames plugin;
    private final BukkitScheduler scheduler;

    private final Team redTeam;
    private final Team blueTeam;

    private final PotionEffect speedEffect = new PotionEffect(
            PotionEffectType.SPEED,
            10,
            4,
            false,
            false,
            false
    );

    private final int LAUNCH_COOLDOWN = 8;

    private final Vector upwardLaunch = new Vector(0, 3, 0);
    private final Vector blueForward = new Vector(-1, 0, -1);
    private final Vector redForward = new Vector(1, 0, 1);

    private final Set<Player> launchedPlayers = new HashSet<>();

    PlayerMoveListener(ParkourChallenge challenge, Multigames plugin) {
        this.challenge = challenge;
        this.plugin = plugin;
        scheduler = Bukkit.getScheduler();

        redTeam = challenge.getRedTeam();
        blueTeam = challenge.getBlueTeam();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Material materialStoodOn = player.getLocation().add(0, -1, 0).getBlock().getType();
        Set<Player> redMembers = redTeam.getMembers();
        Set<Player> blueMembers = blueTeam.getMembers();
        if (challenge.isTeamSelectingPeriod()) {
            if (!redMembers.contains(player) && materialStoodOn.equals(Material.RED_WOOL)) {
                blueMembers.remove(player);
                redMembers.add(player);
                player.sendMessage("Joined red team!");
            }
            if (!blueMembers.contains(player) && materialStoodOn.equals(Material.BLUE_WOOL)) {
                redMembers.remove(player);
                blueMembers.add(player);
                player.sendMessage("Joined blue team!");
            }
        }
        if (challenge.isPlayingPeriod()) {
            Material materialBelowGround = player.getLocation().add(0, -2, 0).getBlock().getType();
            if (materialStoodOn.equals(Material.WHITE_CONCRETE)) {
                player.addPotionEffect(speedEffect);
            }
            if (materialBelowGround.equals(Material.BLUE_GLAZED_TERRACOTTA) && !launchedPlayers.contains(player)) {
                launchedPlayers.add(player);
                player.setVelocity(upwardLaunch);
                scheduler.runTaskLater(plugin, () -> {
                    player.setVelocity(blueForward);
                    launchedPlayers.remove(player);
                }, LAUNCH_COOLDOWN);
            }
            if (materialBelowGround.equals(Material.RED_GLAZED_TERRACOTTA) && !launchedPlayers.contains(player)) {
                launchedPlayers.add(player);
                player.setVelocity(upwardLaunch);
                scheduler.runTaskLater(plugin, () -> {
                    player.setVelocity(redForward);
                    launchedPlayers.remove(player);
                }, LAUNCH_COOLDOWN);
            }
        }
    }
}