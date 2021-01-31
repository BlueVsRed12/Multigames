package xyz.rainbowpunk.multigames.utilities;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class PlayerNotifier {
    private Set<Player> receivers;

    private String formattingPrefix;

    public PlayerNotifier() {
        receivers = new HashSet<>();

        formattingPrefix = "";
    }

    public PlayerNotifier(Set<Player> players) {
        receivers = players;

        formattingPrefix = "";
    }

    public void addReceivers(Set<Player> players) {
        receivers.addAll(players);
    }

    public void setFormattingPrefix(String formattingPrefix) {
        this.formattingPrefix = formattingPrefix + " ";
    }

    public void sendChatMessage(String message, Set<Player> receivers) {
        receivers.forEach(player -> player.sendMessage(Utilities.colorText(formattingPrefix + message)));
    }

    public void sendChatMessage(String message) {
        sendChatMessage(message, receivers);
    }

    public void sendActionBar(String message, Set<Player> receivers) {
        receivers.forEach(player -> player.sendActionBar(Utilities.colorText(formattingPrefix + message)));
    }

    public void sendActionBar(String message) {
        sendActionBar(message, receivers);
    }

    public void sendTitle(String title, String subtitle, int time, Set<Player> receivers) {
        receivers.forEach(player -> player.sendTitle(
                Utilities.colorText(formattingPrefix + title),
                Utilities.colorText(formattingPrefix + subtitle),
                0, time, time / 4)
        );
    }

    public void sendTitle(String title, String subtitle, int time) {
        sendTitle(title, subtitle, time, receivers);
    }
}
