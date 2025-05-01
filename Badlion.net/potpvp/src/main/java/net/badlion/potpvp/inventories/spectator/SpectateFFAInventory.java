package net.badlion.potpvp.inventories.spectator;

import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.potpvp.ffaworlds.FFAWorld;
import net.badlion.potpvp.managers.PotPvPPlayerManager;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SpectateFFAInventory {

	private static SmellyInventory smellyInventory;

	public static void initialize() {
		SmellyInventory smellyInventory = new SmellyInventory(new SpectateFFAInventoryScreenHandler(), 18,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "Spectate FFA");

		// Fill with ffa items
		for (FFAWorld ffaWorld : FFAWorld.getFfaWorlds().values()) {
			smellyInventory.getMainInventory().addItem(ffaWorld.getFFAItem());
		}

		SpectateFFAInventory.smellyInventory = smellyInventory;
	}

	public static void openSpectateFFAInventory(Player player) {
		PotPvPPlayerManager.addDebug(player, "Open spectate ffa inventory");

		BukkitUtil.openInventory(player, SpectateFFAInventory.smellyInventory.getMainInventory());
	}

	public static void updateSpectateFFAInventory() {
		Inventory inventory = SpectateFFAInventory.smellyInventory.getMainInventory();
		inventory.clear();

		// Fill with ffa items
		for (FFAWorld ffaWorld : FFAWorld.getFfaWorlds().values()) {
			inventory.addItem(ffaWorld.getFFAItem());
		}

		inventory.setItem(17, SmellyInventory.getCloseInventoryItem());
	}

	private static class SpectateFFAInventoryScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			FFAWorld ffaWorld = FFAWorld.getFFAWorld(item);
			if (ffaWorld != null) {
				// Teleport them to the FFA world
				player.teleport(ffaWorld.getSpawn());
			}
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}
	
}
