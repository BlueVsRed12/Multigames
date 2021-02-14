package xyz.rainbowpunk.multigames.competition;

import xyz.rainbowpunk.multigames.Multigames;
import xyz.rainbowpunk.multigames.utilities.MultiColor;

import java.util.*;
import java.util.stream.Collectors;

public class Competition {
    private final Multigames plugin;

    private Set<UUID> competitors;
    private Map<UUID, MultiColor> colorMap;
    private Map<MultiColor, UUID> competitorMap;

    public Competition(Multigames plugin, Set<UUID> competitors) {
        this.plugin = plugin;
        initializeCompetitorTracking(competitors);
    }

    // initialization
    private void initializeCompetitorTracking(Set<UUID> competitors) {
        this.competitors = competitors;

        colorMap = new HashMap<>();
        competitorMap = new HashMap<>();
        for (UUID competitor : competitors) setCompetitorColor(competitor, null);
    }

    // accessors & mutators
    public UUID getCompetitor(MultiColor color) {
        return competitorMap.get(color);
    }

    public MultiColor getCompetitorColor(UUID competitor) {
        return colorMap.get(competitor);
    }

    public void setCompetitorColor(UUID competitor, MultiColor color) {
        if (!isCompeting(competitor)) return;
        colorMap.put(competitor, color);
        competitorMap.put(color, competitor);
    }

    public boolean isCompeting(UUID competitor) {
        return (competitors.contains(competitor));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        Map<MultiColor, String> stringColorMap = competitorMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> plugin.getPlayerName(e.getValue())));
        TreeMap<MultiColor, String> sortedStringColorMap = new TreeMap<>(Comparator.comparing(MultiColor::toString));
        sortedStringColorMap.putAll(stringColorMap);
        for (Map.Entry<MultiColor, String> entry : sortedStringColorMap.entrySet()) {
            builder
                    .append(entry.getValue())
                    .append(" | ")
                    .append(entry.getKey())
                    .append("\n");
        }

        return builder.toString();
    }
}
