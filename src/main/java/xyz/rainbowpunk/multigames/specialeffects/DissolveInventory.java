package xyz.rainbowpunk.multigames.specialeffects;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.rainbowpunk.multigames.Multigames;

import java.util.Collections;
import java.util.Stack;

class DissolveInventory {
    private final Plugin plugin;

    private final Player player;
    private final Inventory inventory;
    private final Stack<Integer> untouchedSlots;

    private final static int INITIAL_STACK_DISSOLVE_DELAY = 15;
    private final static int MIN_STACK_DISSOLVE_DELAY = 1;
    private final static float INITIAL_SLOT_DISSOLVE_DELAY = 7.0f;
    private final static float MIN_SLOT_DISSOLVE_DELAY = 2.0f;

    private int itemsDissolved;

    //todo: take damage upon interacting with dissolving inventory
    //todo: disable inventory interactions during dissolve
    private DissolveInventory(Player player) {
        //todo: making this private because i want to use an interface later when there's more special effects
        plugin = Multigames.getInstance();

        this.player = player;
        inventory = player.getInventory();
        untouchedSlots = new Stack<>();
        ItemStack[] items = inventory.getContents();
        for (int i = 0; i < inventory.getSize(); i++) {
            if (items[i] != null) untouchedSlots.push(i);
        }
        Collections.shuffle(untouchedSlots);
        dissolveInventory();

        itemsDissolved = 0;
    }

    private void dissolveInventory() {
        dissolveInventory(INITIAL_SLOT_DISSOLVE_DELAY);
    }

    private void dissolveInventory(float delay) {
        if (untouchedSlots.isEmpty()) {
            player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 0.3f, 1f);
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                dissolveSlot(untouchedSlots.pop());
                dissolveInventory(Math.max(delay - 0.3f, MIN_SLOT_DISSOLVE_DELAY));
            }
        }.runTaskLater(plugin, (int) delay);
    }

    private void dissolveSlot(int slot) {
        dissolveSlot(slot, INITIAL_STACK_DISSOLVE_DELAY);
    }

    private void dissolveSlot(int slot, int delay) {
        if (isSlotEmpty(slot)) {
            player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 0.2f, 1f);
            return;
        }

        new BukkitRunnable() {
            public void run() {
                decrementSlot(slot);
                attemptSoundEffect();

                dissolveSlot(slot, Math.max(delay - 1, MIN_STACK_DISSOLVE_DELAY));
            }
        }.runTaskLater(plugin, delay);
    }

    private void decrementSlot(int slot) {
        ItemStack stack = inventory.getItem(slot);
        assert stack != null;
        stack.setAmount(stack.getAmount() - 1);
        inventory.setItem(slot, stack);

        itemsDissolved++;
    }

    private void attemptSoundEffect() {
        if (itemsDissolved == 1) player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, .2f, 1f);
        if (itemsDissolved == 10) player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, .2f, 1f);
        if (itemsDissolved % 100 == 0) player.playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR, .2f, 1f);
    }

    private boolean isSlotEmpty(int slot) {
        return inventory.getItem(slot) == null;
    }

    public static void perform(Player player) {
        new DissolveInventory(player);
    }
}
