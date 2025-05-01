package net.badlion.gfactions.listeners;

import net.badlion.gfactions.GFactions;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;

public class VillagerListener implements Listener {

	private GFactions plugin;

	public VillagerListener(GFactions plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerTradeWithVillager(InventoryOpenEvent event) {
		if (event.getInventory().getType() == InventoryType.MERCHANT) {
			if (event.getPlayer() instanceof Player) {
				Player player = (Player) event.getPlayer();
				player.sendMessage(ChatColor.RED + "Cannot interact with villagers.");
			}

			event.setCancelled(true);
		}
	}

}
