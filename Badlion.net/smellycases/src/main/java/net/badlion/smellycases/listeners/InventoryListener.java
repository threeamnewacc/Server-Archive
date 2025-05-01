package net.badlion.smellycases.listeners;

import net.badlion.smellycases.managers.CaseManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryListener implements Listener {

	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent event) {
		if (event.getInventory().getHolder() instanceof CaseManager.CaseHolder) {
			event.setCancelled(true);
		}
	}
}
