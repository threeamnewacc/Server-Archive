package net.badlion.gfactions.listeners;

import net.badlion.gfactions.GFactions;
import net.badlion.gfactions.SLoot;
import net.badlion.gguard.ProtectedRegion;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerItemsDroppedFromDeathEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class LootStealListener implements Listener {

	private GFactions plugin;

	public LootStealListener(GFactions plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onDeath(PlayerItemsDroppedFromDeathEvent event) {
        Player player = event.getPlayer();
        if (player.getKiller() != null) {
            SLoot.protectLoot(event.getItemsDroppedOnDeath(), player.getKiller(), 15);
        }
	}

	@EventHandler
	public void onPlayerPickupItemFromKill(PlayerPickupItemEvent event) {
		Player looter = event.getPlayer();
        Item loot = event.getItem();
		// We are not the killer, gotta wait 10 sec like a fucking peasant
		if (!SLoot.checkTheirPrivilege(looter, loot)) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
	public void onPlayerLootStealInWarZone(PlayerPickupItemEvent event) {
		ProtectedRegion region = this.plugin.getgGuardPlugin().getProtectedRegion(event.getPlayer().getLocation(), this.plugin.getgGuardPlugin().getProtectedRegions());
		if (region != null && (!region.getRegionName().equals("warzone") && !region.getRegionName().equals("spawn") && !region.getRegionName().toLowerCase().contains("road"))) {
            if (this.plugin.getMapNameToJoinTime().containsKey(event.getPlayer().getUniqueId().toString())) {
                event.setCancelled(true);
            }
        }
	}
}
