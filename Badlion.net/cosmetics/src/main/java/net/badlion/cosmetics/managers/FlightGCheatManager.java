package net.badlion.cosmetics.managers;

import net.badlion.cosmetics.Cosmetics;
import net.badlion.cosmetics.tasks.TurnGCheatBackOnTask;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FlightGCheatManager implements Listener {

    private static final Map<UUID, BukkitTask> mapping = new HashMap<>();

    static {
        Cosmetics.getInstance().getServer().getPluginManager().registerEvents(new FlightGCheatManager(), Cosmetics.getInstance());
    }

    public static void addToMapping(Player player, int ticks) {
        TurnGCheatBackOnTask task = new TurnGCheatBackOnTask(player);

        BukkitTask existingTask = mapping.get(player.getUniqueId());
        if (existingTask != null) {
            existingTask.cancel();
        }

        player.setBypassGCheat(true);

        FlightGCheatManager.mapping.put(player.getUniqueId(), task.runTaskLater(Cosmetics.getInstance(), ticks));
    }

    public static void removeFromMapping(Player player) {
        FlightGCheatManager.mapping.remove(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        FlightGCheatManager.removeFromMapping(event.getPlayer());
    }


}
