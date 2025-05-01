package net.badlion.gfactions.listeners;

import net.badlion.gfactions.GFactions;
import net.badlion.gfactions.tasks.GodAppleAllowTask;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.joda.time.DateTime;
import org.joda.time.Minutes;

public class GodAppleListener implements Listener {

    @EventHandler
    public void godAppleConsumeEvent(PlayerItemConsumeEvent event) { // Disable lava flow
        if (event.getItem().getType().equals(Material.GOLDEN_APPLE) && event.getItem().getDurability() == 1) { // God apple
            Long then = GFactions.plugin.getGodAppleBlacklist().get(event.getPlayer().getUniqueId().toString());
            if (then != null) {
                DateTime now = DateTime.now();
                DateTime thendt = new DateTime(then);
                int minutes = 30 - Minutes.minutesBetween(thendt, now).getMinutes();
                if (minutes == 1) { // Grammar nazi
                    event.getPlayer().sendMessage(ChatColor.YELLOW + "You have already eaten a god apple in the past "
		                    + GFactions.plugin.getGodAppleTimer() + " minutes, you cannot consume another for 1 minute.");
                } else {
                    event.getPlayer().sendMessage(ChatColor.YELLOW + "You have already eaten a god apple in the past "
		                    + GFactions.plugin.getGodAppleTimer() + " minutes, you cannot consume another for " + minutes + " minutes.");
                }
                event.setCancelled(true);
            } else {
                // Allow consumption but add to blacklist
                GFactions.plugin.getGodAppleBlacklist().put(event.getPlayer().getUniqueId().toString(), System.currentTimeMillis());
                event.getPlayer().sendMessage(ChatColor.YELLOW + "You have eaten a god apple, you cannot consume another for 30 minutes.");
                new GodAppleAllowTask(GFactions.plugin, event.getPlayer().getUniqueId().toString())
		                .runTaskLater(GFactions.plugin, GFactions.plugin.getGodAppleTimer() * 60L * 20L);
            }
        }
    }

}
