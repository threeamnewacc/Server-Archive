package net.badlion.gfactions.listeners;

import net.badlion.gfactions.GFactions;
import net.badlion.gguard.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPostPortalEvent;

public class SpawnTagListener implements Listener {

    private GFactions plugin;

    public SpawnTagListener(GFactions plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority= EventPriority.MONITOR)
    public void onTeleport(PlayerPostPortalEvent event) {
        Player player = event.getPlayer();

        if (this.plugin.isInCombat(player)) {
			// Hack
			if (event.getFrom().getWorld().getName().equals("world_the_end")) {
				this.plugin.removeCombatTagged(player);
			}

            ProtectedRegion region = this.plugin.getgGuardPlugin().getProtectedRegion(player.getLocation(), this.plugin.getgGuardPlugin().getProtectedRegions());
            if (region != null && region.getRegionName().equals("spawn")) {
				if (!event.getFrom().getWorld().getName().equals("world_the_end")) {
					event.setCancelled(true);
					player.sendMessage(ChatColor.RED + "Cannot enter spawn when combat tagged.  Use /ct to see how much time you have left.");
				}
            }
        }

    }
}
