package net.badlion.gfactions.listeners;

import net.badlion.gfactions.GFactions;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public class DuplicateItemListener implements Listener {

	private GFactions plugin;

	public DuplicateItemListener(GFactions plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void stopOreDupBug(CraftItemEvent event) {
		if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {
			// Force clear area that it was being crafted in
			ItemStack[] items = event.getInventory().getMatrix();
			ItemStack[] newItems = new ItemStack[items.length];
			event.getInventory().setMatrix(newItems);
		}
	}

}
