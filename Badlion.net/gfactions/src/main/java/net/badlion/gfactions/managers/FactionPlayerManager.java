package net.badlion.gfactions.managers;

import net.badlion.gfactions.FactionPlayer;
import net.badlion.gfactions.GFactions;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class FactionPlayerManager implements Listener {

    private static Map<UUID, FactionPlayer> players = new HashMap<>();
    private static Set<FactionPlayer> playerSet = new HashSet<>();

    public static void initialize() {
        new FactionScoreboardUpdate().runTaskTimer(GFactions.plugin, 20, 20);
    }

    public static FactionPlayer addPlayer(UUID uuid) {
        FactionPlayer factionPlayer = new FactionPlayer(uuid);
        FactionPlayerManager.players.put(uuid, factionPlayer);
        FactionPlayerManager.playerSet.add(factionPlayer);
        return factionPlayer;
    }

    public static FactionPlayer getPlayer(UUID uuid) {
        return FactionPlayerManager.players.get(uuid);
    }

    public static void removePlayer(UUID uuid) {
        FactionPlayer factionPlayer = FactionPlayerManager.players.remove(uuid);
        FactionPlayerManager.playerSet.remove(factionPlayer);
    }

    @EventHandler(priority=EventPriority.FIRST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        FactionPlayerManager.addPlayer(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority=EventPriority.LAST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        FactionPlayerManager.removePlayer(event.getPlayer().getUniqueId());
    }

    public static class FactionScoreboardUpdate extends BukkitRunnable {

        @Override
        public void run() {
            final List<FactionPlayer> plyrs = new ArrayList<>();
            plyrs.addAll(FactionPlayerManager.playerSet);

            final int size = players.size();
            final int diff = (int) Math.ceil((double) players.size() / 20D);

            new BukkitRunnable() {
                private int start = 0;
                private int end = size;

                public void run() {
                    if (start >= end) {
                        this.cancel();
                        return;
                    }

                    int localEnd = start + diff;
                    for (int i = start; i < localEnd; i++, start++) {
                        // Bail out
                        if (i >= end) {
                            this.cancel();
                            return;
                        }

                        plyrs.get(i).updateScoreboard();
                    }
                }
            }.runTaskTimer(GFactions.plugin, 0, 1);
        }

    }
}
