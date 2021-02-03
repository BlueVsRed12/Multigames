package xyz.rainbowpunk.multigames.stages.parkourchallenge;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

class TrophyMonument {
    private final ParkourChallenge challenge;

    private final Map<Trophy, Location> locations;
    private final Map<Trophy, TrophyStatus> statuses;

    TrophyMonument(ParkourChallenge challenge) {
        this.challenge = challenge;

        locations = new HashMap<>();
        statuses = new HashMap<>();
    }

    boolean isComplete() {
        for (Trophy trophy : Trophy.values()) {
            if (!isTrophyCollected(trophy)) return false;
        }
        return true;
    }

    void setTrophyAnimating(Trophy trophy) {
        statuses.put(trophy, TrophyStatus.ANIMATING);
    }

    void setTrophyCollected(Trophy trophy, Location location) {
        locations.put(trophy, location);
        statuses.put(trophy, TrophyStatus.COLLECTED);
        challenge.checkForWinner();
    }

    void removeTrophy(Trophy trophy) {
        locations.remove(trophy);
        statuses.put(trophy, TrophyStatus.MISSING);
        challenge.checkForWinner();
    }

    Location getTrophyLocation(Trophy trophy) {
        return locations.get(trophy).clone().add(0.5, 0.5, 0.5);
    }

    boolean hasTrophy(Trophy trophy) {
        return getTrophyStatus(trophy) == TrophyStatus.COLLECTED
                || getTrophyStatus(trophy) == TrophyStatus.ANIMATING;
    }

    boolean isTrophyCollected(Trophy trophy) {
        return getTrophyStatus(trophy) == TrophyStatus.COLLECTED;
    }

    private TrophyStatus getTrophyStatus(Trophy trophy) {
        TrophyStatus status = statuses.get(trophy);
        if (status == null) return TrophyStatus.MISSING;
        return status;
    }

    void cleanUp(World world) {
        for (Location location : locations.values()) {
            world.getBlockAt(location).setType(Material.AIR);
        }
    }
}