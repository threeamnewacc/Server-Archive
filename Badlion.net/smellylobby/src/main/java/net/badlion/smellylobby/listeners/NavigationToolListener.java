package net.badlion.smellylobby.listeners;

import net.badlion.smellylobby.helpers.NavigationInventoryHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class NavigationToolListener implements Listener {

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if (event.getItem() != null && event.getItem().getType().equals(Material.WATCH)
				&& (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
			event.setCancelled(true);

			// Open up the server inventory
			NavigationInventoryHelper.openNavigationInventory(event.getPlayer());
		}
	}

}
