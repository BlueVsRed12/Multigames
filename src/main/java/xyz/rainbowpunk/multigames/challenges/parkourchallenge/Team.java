package xyz.rainbowpunk.multigames.challenges.parkourchallenge;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

class Team {
    private final ParkourChallenge challenge;

    private final String name;

    private final Location waitingSpawn;

    private final Set<Player> members;

    private final TrophyMonument trophyMonument;

    Team(ParkourChallenge challenge, String teamName, Location waitingSpawn) {
        this.challenge = challenge;

        name = teamName;

        this.waitingSpawn = waitingSpawn;

        members = new HashSet<>();

        trophyMonument = new TrophyMonument(challenge);
    }

    Set<Player> getMembers() {
        return members;
    }

    Location getSpawn() {
        return waitingSpawn.clone();
    }

    TrophyMonument getTrophyMonument() {
        return trophyMonument;
    }

    void cleanUp(World world) {
        trophyMonument.cleanUp(world);
    }

    String getName() {
        return name;
    }
}