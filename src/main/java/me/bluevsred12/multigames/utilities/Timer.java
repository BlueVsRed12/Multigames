package me.bluevsred12.multigames.utilities;

import me.bluevsred12.multigames.Multigames;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Set;

public class Timer {
    private static final int TICKS_PER_SECOND = 20;

    private final Multigames plugin;
    private final Set<Player> players;
    private final Runnable runnable;

    private final BossBar bossBar;
    private final String title;
    private BukkitTask task;

    private Sound tickingSound;
    private float tickingPitch;
    private final Sound endingTickingSound;
    private final float endingTickingPitch;
    private final int endingPeriod;

    private final int TOTAL_TIME;
    private int secondsPassed;

    public Timer(TimerBuilder builder) {
        plugin = builder.plugin;
        players = builder.players;
        runnable = builder.runnable;

        title = builder.title;
        bossBar = Bukkit.createBossBar(
                title,
                builder.color,
                builder.style
        );
        task = null;

        tickingSound = builder.tickingSound;
        tickingPitch = builder.tickingPitch;
        endingTickingSound = builder.endingTickingSound;
        endingTickingPitch = builder.endingTickingPitch;
        endingPeriod = builder.endingPeriod;

        TOTAL_TIME = builder.timeToComplete;
        secondsPassed = 0;
    }

    public void start() {
        initializeBossBar();

        for (Player player : players) {
            bossBar.addPlayer(player);
        }

        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (getRemainingSeconds() > 1) {
                    incrementTimer();
                    playTickingSound();
                    updateBossBarProgress();
                } else {
                    runRunnable();
                    cleanUp();
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    private void runRunnable() {
        if (runnable == null) return;
        runnable.run();
    }

    public void playTickingSound() {
        if (endingTickingSound != null && getRemainingSeconds() <= endingPeriod) {
            players.forEach(
                    player -> player.playSound(
                            player.getLocation(),
                            endingTickingSound,
                            1f,
                            endingTickingPitch)
            );
        } else if (tickingSound != null) {
            players.forEach(
                    player -> player.playSound(
                            player.getLocation(),
                            tickingSound,
                            1f,
                            tickingPitch)
            );
        }
    }

    private void incrementTimer() {
        secondsPassed++;
    }

    private void initializeBossBar() {
        updateBossBarProgress();
        bossBar.setVisible(true);
    }

    private void updateBossBarProgress() {
        bossBar.setTitle(
                title + " : "
                + Utilities.convertToTimeFormat(TOTAL_TIME - secondsPassed));
        bossBar.setProgress(Math.max(0, 1 - (double) secondsPassed / TOTAL_TIME));
    }

    public int getSecondsPassed() {
        return secondsPassed;
    }

    public int getRemainingSeconds() {
        return TOTAL_TIME - secondsPassed;
    }

    public String getTitle() {
        return title;
    }

    public void cleanUp() {
        bossBar.removeAll();
        if (task != null) task.cancel();
    }

    public static class TimerBuilder {
        private final Multigames plugin;
        private final Set<Player> players;
        private final String title;
        private final int timeToComplete;

        private Sound tickingSound;
        private float tickingPitch;
        private Sound endingTickingSound;
        private float endingTickingPitch;
        private int endingPeriod;

        private Runnable runnable;
        private BarColor color;
        private BarStyle style;

        public TimerBuilder(Multigames plugin, Set<Player> players, int timeToComplete, String title) {
            this.plugin = plugin;
            this.players = players;
            this.timeToComplete = timeToComplete;
            this.title = Utilities.colorText(title);
            tickingSound = null;
            tickingPitch = 1f;
            endingTickingSound = null;
            endingTickingPitch = 1f;
            endingPeriod = 0;
            runnable = null;
            color = BarColor.WHITE;
            style = BarStyle.SOLID;
        }

        public TimerBuilder setTickingSound(Sound sound, float pitch) {
            tickingSound = sound;
            tickingPitch = pitch;
            return this;
        }

        public TimerBuilder setEndingTickingSound(Sound sound, float pitch, int endingPeriod) {
            endingTickingSound = sound;
            endingTickingPitch = pitch;
            this.endingPeriod = endingPeriod;
            return this;
        }

        public TimerBuilder setRunnable(Runnable runnable) {
            this.runnable = runnable;
            return this;
        }

        public TimerBuilder setBarColor(BarColor color) {
            this.color = color;
            return this;
        }

        public TimerBuilder setBarStyle(BarStyle style) {
            this.style = style;
            return this;
        }

        public Timer build() {
            return new Timer(this);
        }
    }
}
