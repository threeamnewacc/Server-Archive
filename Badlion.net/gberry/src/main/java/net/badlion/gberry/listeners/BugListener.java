package net.badlion.gberry.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BugListener implements Listener {

    public static final int THRESHOLD = 500;
    private final Map<UUID, Long> lastEatTime = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.lastEatTime.put(event.getPlayer().getUniqueId(), 0L);
    }


    @EventHandler
    public void onPlayerEat(PlayerItemConsumeEvent event) {
        long lastEatTime = this.lastEatTime.get(event.getPlayer().getUniqueId());

        long ts = System.currentTimeMillis();
        if (lastEatTime + THRESHOLD > ts) {
            event.setCancelled(true);
            return;
        }

        this.lastEatTime.put(event.getPlayer().getUniqueId(), ts);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.lastEatTime.remove(event.getPlayer().getUniqueId());
    }


}
