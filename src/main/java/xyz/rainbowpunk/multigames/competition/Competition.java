package xyz.rainbowpunk.multigames.competition;

import org.bukkit.entity.Player;
import xyz.rainbowpunk.multigames.Multigames;
import xyz.rainbowpunk.multigames.utilities.MultiColor;

import java.util.*;
import java.util.stream.Collectors;

public class Competition {
    private final Multigames plugin;

    /**
     * All competitors that are competing, stored as UUIDs.
     */
    private Set<UUID> competitorUUIDs;
    /**
     * Competitors are only put into the color map if they are assigned a color; i.e., after the player select stage.
     */
    private Map<UUID, MultiColor> colorMap;

    public Competition(Multigames plugin) {
        this.plugin = plugin;
        initializeCompetitors();
    }
    // initialization
    private void initializeCompetitors() {
        competitorUUIDs = new HashSet<>();
        colorMap = new HashMap<>();
    }

    // accessors & mutators
    public void addCompetitor(Player player) {
        addCompetitor(player.getUniqueId());
    }

    public void addCompetitor(UUID competitor) {
        competitorUUIDs.add(competitor);
    }

    public UUID getCompetitor(MultiColor color) {
        return colorMap.entrySet().stream()
                .filter(entry -> entry.getValue().equals(color))
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public MultiColor getCompetitorColor(UUID competitor) {
        return colorMap.get(competitor);
    }

    public void setCompetitorColor(UUID competitor, MultiColor color) {
        if (!isCompeting(competitor)) return;

        if (color == null) removeCompetitorColor(competitor);
        else colorMap.put(competitor, color);
    }

    public void removeCompetitorColor(UUID competitor) {
        colorMap.remove(competitor);
    }

    public boolean isCompeting(UUID competitor) {
        return competitorUUIDs.contains(competitor);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        List<UUID> competitors = competitorUUIDs.stream()
                .filter(e -> getCompetitorColor(e) != null)
                .sorted(Comparator.comparing(this::getCompetitorColor))
                .collect(Collectors.toCollection(LinkedList::new));
        competitors.addAll(competitorUUIDs.stream()
                .filter(e -> getCompetitorColor(e) == null)
                .sorted(Comparator.comparing(plugin::getPlayerName))
                .collect(Collectors.toList()));

        for (UUID uuid : competitors) {
            MultiColor color = getCompetitorColor(uuid);
            builder.append(plugin.getPlayerName(uuid))
                    .append(" | ")
                    .append(color == null ? "UNASSIGNED" : color)
                    .append('\n');
        }

        return builder.toString();
    }
}
