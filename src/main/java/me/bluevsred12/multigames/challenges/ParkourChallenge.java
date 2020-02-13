package me.bluevsred12.multigames.challenges;

import me.bluevsred12.multigames.Multigames;
import me.bluevsred12.multigames.UniqueItem;
import me.bluevsred12.multigames.utilities.Timer;
import me.bluevsred12.multigames.utilities.Utilities;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ParkourChallenge {
    private final Multigames plugin;

    private final BukkitScheduler scheduler;

    private final World world;

    private final Set<Player> competingPlayers;
    private final Team redTeam;
    private final Team blueTeam;

    private final Location teamSelectionLocation;

    private static final Material PODIUM_VERIFICATION_TYPE = Material.BLACK_GLAZED_TERRACOTTA;

    private final Set<ChallengeState> ongoingStates;
    private enum ChallengeState {
        TEAM_SELECTION,
        WAITING_PERIOD,
        ENDGAME, PLAYING_PERIOD
    }

    private Set<Listener> listeners;
    private enum ListenerType {
        PRESSURE_PLATE,
        PLAYER_QUIT,
        PLAYER_MOVE,
        BLOCK_PLACE
    }

    public ParkourChallenge(Multigames plugin) {
        this.plugin = plugin;

        scheduler = Bukkit.getScheduler();

        world = plugin.getMainWorld();

        competingPlayers = plugin.getOnlinePlayers();
        redTeam = new Team("Red", new Location(world, 61.0, 178, -316.0));
        blueTeam = new Team("Blue", new Location(world, 75.0, 178, -301.0));

        teamSelectionLocation = new Location(world, 68, 194, -308);

        ongoingStates = new HashSet<>();

        initializeListeners();

        startTeamSelectingPeriod();
    }

    private void initializeListeners() {
        listeners = new HashSet<>();

        listeners.add(new PlayerQuitListener(this, plugin));
        listeners.add(new PlayerMoveListener(this, plugin));
        listeners.add(new BlockPlaceListener(this, plugin));
        listeners.add(new PressurePlateListener(this, plugin));

        for (Listener listener : listeners) {
            plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }

    // game states
    private void startTeamSelectingPeriod() {
        ongoingStates.add(ChallengeState.TEAM_SELECTION);

        for (Player player : competingPlayers) {
            player.teleport(teamSelectionLocation);
        }

        Timer timer = new Timer(
                plugin,
                "Team selection period",
                competingPlayers,
                this::startWaitingPeriod,
                12);
        timer.start();
    }

    private void startWaitingPeriod() {
        ongoingStates.clear();
        ongoingStates.add(ChallengeState.WAITING_PERIOD);

        for (Player player : competingPlayers) {
            if (redTeam.getMembers().contains(player)) player.teleport(redTeam.getSpawn());
            else if (blueTeam.getMembers().contains(player)) player.teleport(blueTeam.getSpawn());
            else {
                competingPlayers.remove(player);
                Bukkit.broadcastMessage(player.getDisplayName() + " did not select a team and will now be spectating!");
            }
        }

        Timer timer = new Timer(
                plugin,
                "Getting ready...",
                competingPlayers,
                this::startPlayingPeriod,
                6);
        timer.start();
    }

    private void startPlayingPeriod() {
        ongoingStates.clear();
        ongoingStates.add(ChallengeState.PLAYING_PERIOD);

        for (Player player : competingPlayers) {
            player.teleport(player.getLocation().add(0, -3, 0));
        }
        Bukkit.broadcastMessage("The game has begun!");


        Timer timer = new Timer(
                plugin,
                "Time remaining",
                competingPlayers,
                this::startEndgamePeriod,
                45
        );
        timer.start();
    }

    private void startEndgamePeriod() {
        ongoingStates.add(ChallengeState.ENDGAME);
        Bukkit.broadcastMessage("The endgame has begun! Trophy shattering has been DISABLED!");
    }

    // playing period methods
    protected TrophyPlacementType determineTrophyPlacementType(Player player, Block placedBlock) {
        Block blockAdjacent = placedBlock.getRelative(0, -1, 0);
        Block blockTwiceBelow = placedBlock.getRelative(0, -3, 0);
        Trophy trophy = Trophy.getTrophy(placedBlock.getType());

        // Was the block a trophy?
        if (trophy == null)
            return TrophyPlacementType.INVALID;

        // Was this placed on top of a podium? (Check verification block below the podium)
        if (blockTwiceBelow.getType() != PODIUM_VERIFICATION_TYPE)
            return TrophyPlacementType.INVALID;

        // Was this placed on the right type of stairs?
        if (trophy.getStairs() != blockAdjacent.getType()) {
            player.spawnParticle(Particle.BARRIER, placedBlock.getLocation().add(0.5, 0.5,0.5), 1);
            player.playSound(placedBlock.getLocation(), Sound.BLOCK_BAMBOO_FALL, 1f, 1f);
            return TrophyPlacementType.MISMATCHED_PODIUM;
        }

        // Is this player part of a team?
        Team team = getPlayerTeam(player);
        if (team == null)
            return TrophyPlacementType.INVALID;

        TrophyTracker podium = team.getTrophyTracker();
        TrophyTracker oppositePodium = getOppositeTeam(team).getTrophyTracker();

        // Are we missing this trophy, or does the enemy have a trophy we can shatter?
        if (!podium.hasTrophy(trophy)) {
            return TrophyPlacementType.PODIUM_COLLECT;
        } else if (isEndgamePeriod()) {
            player.sendMessage("You cannot shatter enemy trophies during the endgame period!");
            return TrophyPlacementType.INVALID;
        } else if (oppositePodium.isTrophyCollected(trophy)) {
            return TrophyPlacementType.SHATTER;
        } else {
            player.sendMessage("You must wait until you lose your existing trophy, or when the enemy gains one!");
            return TrophyPlacementType.PODIUMS_CLOGGED;
        }
    }

    protected void placeTrophy(Player player, Trophy trophy) {
        String playerName = player.getDisplayName();
        String trophyName = trophy.getDisplayFriendlyName();
        Team team = getPlayerTeam(player);
        TrophyTracker tracker = team.getTrophyTracker();

        Bukkit.broadcastMessage(
                playerName + " has placed the "
                + trophyName + " trophy on "
                + team.getName() + " team's podium!");

        tracker.setTrophyAnimating(trophy);
    }

    protected void shatterTrophy(Player player, Trophy trophy) {
        String playerName = player.getDisplayName();
        String trophyName = trophy.getDisplayFriendlyName();
        Team oppositeTeam = getOppositeTeam(player);

        oppositeTeam.getTrophyTracker().removeTrophy(trophy);
        Bukkit.broadcastMessage(
                playerName + " has shattered the "
                        + oppositeTeam.getName() + " team's "
                        + trophyName + " trophy!");
    }

    protected void animateTrophyPlacement(Player player, Trophy trophy, Block placedBlock) {
        Team team = getPlayerTeam(player);
        TrophyTracker trophyTracker = team.getTrophyTracker();

        new BukkitRunnable() {
            private int step = 0;
            private Block currentBlock = placedBlock;
            private final Location startLocation = placedBlock.getLocation().add(0.5, 0.5, 0.5);
            private final Location targetLocation = startLocation.clone().add(0, 3, 0);

            @Override
            public void run() {
                if (step != 0) {
                    currentBlock.getRelative(BlockFace.UP).setType(currentBlock.getType());
                    currentBlock.setType(Material.AIR);
                    currentBlock = currentBlock.getRelative(BlockFace.UP);
                    Utilities.spawnParticleLine(world, Particle.CLOUD, 5, startLocation, targetLocation);
                }
                if (step == 0) {
                    world.playSound(
                            player.getLocation(),
                            Sound.BLOCK_NOTE_BLOCK_PLING,
                            SoundCategory.BLOCKS,
                            1f,
                            1f
                    );
                } else if (step == 1 || step == 2) {
                    world.playSound(
                            currentBlock.getLocation(),
                            Sound.ITEM_FLINTANDSTEEL_USE,
                            1f,
                            1f
                    );
                } else if (step >= 3) {
                    world.playSound(
                            currentBlock.getLocation(),
                            Sound.ENTITY_BLAZE_HURT,
                            1f,
                            0f
                    );
                    trophyTracker.setTrophyCollected(trophy, currentBlock.getLocation());
                    this.cancel();
                }
                step++;
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    protected void animateTrophyShatter(Player player, Trophy trophy, Block placedBlock) {
        Team oppositeTeam = getOppositeTeam(player);
        Location opposingTrophyLocation = oppositeTeam.getTrophyTracker().getTrophyLocation(trophy);
        Location trophyLocation = placedBlock.getLocation();

        world.getBlockAt(opposingTrophyLocation).setType(Material.AIR);
        world.getBlockAt(trophyLocation).setType(Material.AIR);
        Utilities.spawnParticleLine(world, Particle.FLAME, 10, trophyLocation, opposingTrophyLocation);
        world.spawnParticle(Particle.BLOCK_CRACK, trophyLocation, 8, 0.2, 0.2, 0.2,
                trophy.getWood().createBlockData());
        world.spawnParticle(Particle.BLOCK_CRACK, opposingTrophyLocation, 8, 0.2, 0.2, 0.2,
                trophy.getWood().createBlockData());
        world.playSound(opposingTrophyLocation, Sound.BLOCK_GLASS_BREAK, 50f, 0f);
    }

    protected boolean pickupTrophy(Player player, Block woodBlock) {
        Trophy trophy = Trophy.getTrophy(woodBlock.getType());
        Block pressurePlate = woodBlock.getRelative(0,  1, 0);
        if (trophy == null) return false;

        player.getInventory().addItem(trophy.getItemStack());
        Bukkit.broadcastMessage(player.getDisplayName() + " has collected the " + trophy.getDisplayFriendlyName() + " trophy!");

        pressurePlate.setType(Material.AIR);
        woodBlock.setType(Material.AIR);
        player.playSound(woodBlock.getLocation(), Sound.ITEM_TRIDENT_HIT_GROUND, SoundCategory.BLOCKS, 1f, 0f);
        player.spawnParticle(
                Particle.CRIT,
                woodBlock.getLocation().add(0.5, 0.5, 0.5),
                10, 0.3, 0.3, 0.3, 0.2);

        scheduler.runTaskLater(plugin, () -> {
            woodBlock.setType(trophy.getWood());
            pressurePlate.setType(Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
        }, 60);
        return true;
    }

    // utility
    protected boolean isTeamSelectingPeriod() {
        return ongoingStates.contains(ChallengeState.TEAM_SELECTION);
    }

    protected boolean isPlayingPeriod() {
        return ongoingStates.contains(ChallengeState.PLAYING_PERIOD);
    }

    protected boolean isEndgamePeriod() {
        return ongoingStates.contains(ChallengeState.ENDGAME);
    }

    protected Set<Player> getCompetingPlayers() {
        return competingPlayers;
    }

    private Team getOppositeTeam(Team team) {
        if (team == redTeam) return blueTeam;
        else return redTeam;
    }

    private Team getOppositeTeam(Player player) {
        return getOppositeTeam(getPlayerTeam(player));
    }

    private Team getPlayerTeam(Player player) {
        if (redTeam.getMembers().contains(player)) return redTeam;
        if (blueTeam.getMembers().contains(player)) return blueTeam;
        return null;
    }

    protected Team getRedTeam() {
        return redTeam;
    }

    protected Team getBlueTeam() {
        return blueTeam;
    }

    public void cleanUp() {
        for (Listener listener : listeners) {
            HandlerList.unregisterAll(listener);
        }
        redTeam.cleanUp(world);
        blueTeam.cleanUp(world);
    }
}

class PlayerQuitListener implements Listener {
    private final ParkourChallenge challenge;
    private final Multigames plugin;

    private final Set<Player> competingPlayers;
    private final Team redTeam;
    private final Team blueTeam;

    public PlayerQuitListener(ParkourChallenge challenge, Multigames plugin) {
        this.challenge = challenge;
        this.plugin = plugin;

        competingPlayers = challenge.getCompetingPlayers();
        redTeam = challenge.getRedTeam();
        blueTeam = challenge.getBlueTeam();
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Bukkit.broadcastMessage(player.getDisplayName() + " has been removed from the game!");

        competingPlayers.remove(player);
        redTeam.getMembers().remove(player);
        blueTeam.getMembers().remove(player);
    }
}

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

class BlockPlaceListener implements Listener {
    private final ParkourChallenge challenge;
    private final Multigames plugin;

    BlockPlaceListener(ParkourChallenge challenge, Multigames plugin) {
        this.challenge = challenge;
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        trophyPlacement(event);
    }

    private void trophyPlacement(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block placedBlock = event.getBlockPlaced();
        Trophy trophy = Trophy.getTrophy(placedBlock);

        TrophyPlacementType placementType = challenge.determineTrophyPlacementType(player, placedBlock);
        if (placementType == TrophyPlacementType.INVALID
                || placementType == TrophyPlacementType.MISMATCHED_PODIUM
                || placementType == TrophyPlacementType.PODIUMS_CLOGGED)
            event.setBuild(false);
        if (placementType == TrophyPlacementType.PODIUM_COLLECT) {
            challenge.placeTrophy(player, trophy);
            challenge.animateTrophyPlacement(player, trophy, placedBlock);
        }
        if (placementType == TrophyPlacementType.SHATTER) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    challenge.shatterTrophy(player, trophy);
                    challenge.animateTrophyShatter(player, trophy, placedBlock);
                }
            }.runTaskLater(plugin, 5);
        }
    }
}

class PressurePlateListener implements Listener {
    private final ParkourChallenge challenge;
    private final Multigames plugin;

    public PressurePlateListener(ParkourChallenge challenge, Multigames plugin) {
        this.challenge = challenge;
        this.plugin = plugin;
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Block clickedBlock = e.getClickedBlock();

        // When the player steps on a trophy pressure plate during a match.
        if (
                challenge.isPlayingPeriod()
                        && e.getAction().equals(Action.PHYSICAL)
                        && clickedBlock != null
                        && clickedBlock.getType() == Material.HEAVY_WEIGHTED_PRESSURE_PLATE) {
            if (challenge.pickupTrophy(player, clickedBlock.getLocation().add(0, -1, 0).getBlock()))
                e.setCancelled(true);
        }
    }
}

class Team {
    private final String name;

    private final Location waitingSpawn;

    private final Set<Player> members;

    private final TrophyTracker trophyTracker;

    Team(String teamName, Location waitingSpawn) {
        name = teamName;

        this.waitingSpawn = waitingSpawn;

        members = new HashSet<>();

        trophyTracker = new TrophyTracker();
    }

    Set<Player> getMembers() {
        return members;
    }

    Location getSpawn() {
        return waitingSpawn.clone();
    }

    public TrophyTracker getTrophyTracker() {
        return trophyTracker;
    }

    public void cleanUp(World world) {
        trophyTracker.cleanUp(world);
    }

    public String getName() {
        return name;
    }
}

class TrophyTracker {
    private final Map<Trophy, Location> locations;
    private final Map<Trophy, TrophyStatus> statuses;

    TrophyTracker() {
        locations = new HashMap<>();
        statuses = new HashMap<>();
    }

    public void setTrophyAnimating(Trophy trophy) {
        statuses.put(trophy, TrophyStatus.ANIMATING);
    }

    public void setTrophyCollected(Trophy trophy, Location location) {
        locations.put(trophy, location);
        statuses.put(trophy, TrophyStatus.COLLECTED);
    }

    public TrophyStatus getTrophyStatus(Trophy trophy) {
        TrophyStatus status = statuses.get(trophy);
        if (status == null) return TrophyStatus.MISSING;
        return status;
    }

    public void removeTrophy(Trophy trophy) {
        locations.remove(trophy);
        statuses.put(trophy, TrophyStatus.MISSING);
    }

    public Location getTrophyLocation(Trophy trophy) {
        return locations.get(trophy).clone().add(0.5, 0.5, 0.5);
    }

    public boolean hasTrophy(Trophy trophy) {
        return getTrophyStatus(trophy) == TrophyStatus.COLLECTED
                || getTrophyStatus(trophy) == TrophyStatus.ANIMATING;
    }

    public boolean isTrophyCollected(Trophy trophy) {
        return getTrophyStatus(trophy) == TrophyStatus.COLLECTED;
    }

    public void cleanUp(World world) {
        for (Location location : locations.values()) {
            world.getBlockAt(location).setType(Material.AIR);
        }
    }
}

enum Trophy {
    OAK("Oak"),
    SPRUCE("Spruce"),
    BIRCH("Birch"),
    JUNGLE("Jungle"),
    ACACIA("Acacia"),
    DARK_OAK("Dark Oak")
    ;

    private final String nameID;
    private final String displayFriendlyName;
    private final ItemStack itemStack;

    Trophy(String displayFriendlyName) {
        nameID = this.name();
        this.displayFriendlyName = displayFriendlyName;
        itemStack = UniqueItem.getItemStack(nameID + "_TROPHY");
    }

    public static Trophy getTrophy(Material searchedMaterial) {
        for (Trophy trophy : Trophy.values()) {
            if (trophy.getWood().equals(searchedMaterial)) return trophy;
        }
        return null;
    }

    public static Trophy getTrophy(Block block) {
        if (block == null) return null;
        return getTrophy(block.getType());
    }

    public Material getWood() {
        return Material.getMaterial(nameID + "_WOOD");
    }

    public Material getStairs() {
        return Material.getMaterial(nameID + "_STAIRS");
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public String getDisplayFriendlyName() {
        return displayFriendlyName;
    }
}

enum TrophyPlacementType {
    INVALID,
    MISMATCHED_PODIUM,
    PODIUMS_CLOGGED,
    PODIUM_COLLECT,
    SHATTER
}

enum TrophyStatus {
    MISSING,
    ANIMATING,
    COLLECTED
}