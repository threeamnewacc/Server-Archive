package net.badlion.gfactions.events.koth;

import net.badlion.gberry.Gberry;
import net.badlion.gfactions.GFactions;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class KOTHRemoveProtTask extends BukkitRunnable {

    private GFactions plugin;

    public KOTHRemoveProtTask(GFactions plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        if (this.plugin.getKoth() != null) {
            HashSet<Player> playersToRemove = new HashSet<Player>();
            Set<Map.Entry<String, Long>> entries = this.plugin.getMapNameToJoinTime().entrySet();
            for (Map.Entry<String, Long> entry : entries) {
                final Player player = this.plugin.getServer().getPlayerExact(entry.getKey());
                if (player != null) {
                    if (Gberry.isLocationInBetween(this.plugin.getKoth().getArenaLocation1(), this.plugin.getKoth().getArenaLocation2(), player.getLocation())) {
                        playersToRemove.add(player);
                    }
                }
            }

            // No concurrent modification exceptions
            for (final Player player : playersToRemove) {
                // Their PVP protection is over, time to remove from the system
                this.plugin.getMapNameToPvPTimeRemaining().remove(player.getUniqueId().toString());
                this.plugin.getMapNameToJoinTime().remove(player.getUniqueId().toString());

                this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

                    @Override
                    public void run() {
                        // Purge from DB
                        plugin.removeProtection(player);
                    }
                });

                player.sendMessage(ChatColor.RED + "Entered KOTH Zone, lost PVP Protection.");
            }
        }
    }
}
