package net.badlion.potpvp.inventories.spectator;

import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.potpvp.events.Event;
import net.badlion.potpvp.managers.PotPvPPlayerManager;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SpectateEventInventory {

	private static SmellyInventory smellyInventory;

	public static void initialize() {
		SpectateEventInventory.smellyInventory = new SmellyInventory(new SpectateEventInventoryScreenHandler(), 18,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "Spectate Event");
	}

	public static Inventory getMainSpectateEventInventory() {
		return SpectateEventInventory.smellyInventory.getMainInventory();
	}

	public static void openSpectateEventInventory(Player player) {
		PotPvPPlayerManager.addDebug(player, "Open spectate event inventory");

		BukkitUtil.openInventory(player, SpectateEventInventory.smellyInventory.getMainInventory());
	}

	private static class SpectateEventInventoryScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			Event event2 = Event.getEventForItem(item);
			if (event2 != null) {
				player.teleport(event2.getArena().getWarp1());
			}
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}
	
}
