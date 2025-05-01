package net.badlion.survivalgames.util;

import net.badlion.gberry.Gberry;
import net.badlion.survivalgames.SurvivalGames;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.concurrent.Callable;

/**
 * Given to me by Aikar, interesting abstraction concepts, not a bad idea
 */
public class BukkitUtil {

    private static final HashMap<String, org.bukkit.event.Listener> _listeners = new HashMap<>();

    public static void openInventory(final Player player, final Inventory inventory) {
        if (inventory == null) {
            Gberry.log("INV", "Inventory is null for " + player.getName());

            try {
                throw new Exception();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return;
        }

        if(player.getOpenInventory() != null) {
            BukkitUtil.runTaskNextTick(new Runnable() {
                @Override
                public void run() {
                    if (player.isOnline()) {
                        player.closeInventory();
                        player.openInventory(inventory);
                    }
                }
            });
        } else {
            player.openInventory(inventory);
        }
    }

    public static void closeInventory(final Player player) {
        if(player.getOpenInventory() != null) {
	        // Remove item on their cursor
	        player.setItemOnCursor(null);

            BukkitUtil.runTaskNextTick(new Runnable() {
                @Override
                public void run() {
                    // Fix race condition created when they log off and are no longer in the state machine
                    if (player.isOnline()) {
                        player.closeInventory();
                    }
                }
            });
        }
    }

    public static void updateInventory(final Player player) {
        BukkitUtil.runTaskNextTick(new Runnable() {
            @Override
            public void run() {
                if (player.isOnline()) {
                    player.updateInventory();
                }
            }
        });
    }

    public static void registerListener(org.bukkit.event.Listener listener) {
        Bukkit.getServer().getPluginManager().registerEvents(listener, SurvivalGames.getInstance());
    }

    public static void registerListener(String name, org.bukkit.event.Listener listener) {
        unregisterListener(name);
        _listeners.put(name, listener);
        registerListener(listener);
    }

    public static void unregisterListener(String name) {
        org.bukkit.event.Listener listener = _listeners.get(name);
        if (listener != null) {
            unregisterListener(listener);
        }
    }

    public static void unregisterListener(org.bukkit.event.Listener listener) {
        HandlerList.unregisterAll(listener);
    }

    public static void unregisterListeners() {
        HandlerList.unregisterAll(SurvivalGames.getInstance());
        _listeners.clear();
    }

    public static BukkitTask runTaskLater(Runnable run, long delay) {
        return Bukkit.getServer().getScheduler().runTaskLater(SurvivalGames.getInstance(), run, delay);
    }

    public static BukkitTask runTaskTimer(Runnable run, long start, long repeat) {
        return Bukkit.getServer().getScheduler().runTaskTimer(SurvivalGames.getInstance(), run, start, repeat);
    }

    public static BukkitTask runTaskTimer(Runnable run, long repeat) {
        return Bukkit.getServer().getScheduler().runTaskTimer(SurvivalGames.getInstance(), run, 0, repeat);
    }

    public static BukkitTask runTaskTimerAsync(Runnable run, long start, long repeat) {
        return Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(SurvivalGames.getInstance(), run, start, repeat);
    }

    public static BukkitTask runTaskTimerAsync(Runnable run, long repeat) {
        return Bukkit.getServer().getScheduler().runTaskTimerAsynchronously(SurvivalGames.getInstance(), run, 0, repeat);
    }


    public static int scheduleTask(Runnable run, long delay) {
        return Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(SurvivalGames.getInstance(), run, delay);
    }

    public static int scheduleAsyncTask(Runnable run, long delay) {
        return Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(SurvivalGames.getInstance(), run, delay);
    }

    public static BukkitTask runTask(Runnable run) {
        if (!SurvivalGames.getInstance().isEnabled()) {
            return null;
        }
        return Bukkit.getServer().getScheduler().runTask(SurvivalGames.getInstance(), run);
    }

    public static <T> T runTaskSync(Callable<T> run) throws Exception {
        return Bukkit.getScheduler().callSyncMethod(SurvivalGames.getInstance(), run).get();
    }

    public static int runTaskNextTick(Runnable run) {
        if (!SurvivalGames.getInstance().isEnabled()) {
            run.run();
            return 0;
        }
        return scheduleTask(run, 1);
    }

    public static void runTaskAsync(Runnable run) {
        if (!SurvivalGames.getInstance().isEnabled()) {
            run.run();
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(SurvivalGames.getInstance(), run);
    }

    public static class Listener implements org.bukkit.event.Listener {
        public Listener() {
            BukkitUtil.registerListener(this);
        }
    }

}