package net.badlion.potpvp.inventories.spectator;

import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.potpvp.managers.PotPvPPlayerManager;
import net.badlion.potpvp.tdm.TDMGame;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SpectateTDMInventory {

	private static SmellyInventory smellyInventory;

	public static void initialize() {
		SmellyInventory smellyInventory = new SmellyInventory(new SpectateTDMInventoryScreenHandler(), 18,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "Spectate TDM");

		// Fill with tdm items
		for (TDMGame tdmGame : TDMGame.getTDMGames()) {
			smellyInventory.getMainInventory().addItem(tdmGame.getTDMItem());
		}

		SpectateTDMInventory.smellyInventory = smellyInventory;
	}

	public static void openSpectateTDMInventory(Player player) {
		PotPvPPlayerManager.addDebug(player, "Open spectate tdm inventory");

		BukkitUtil.openInventory(player, SpectateTDMInventory.smellyInventory.getMainInventory());
	}

	public static void updateSpectateTDMInventory() {
		Inventory inventory = SpectateTDMInventory.smellyInventory.getMainInventory();
		inventory.clear();

		// Fill with tdm items
		for (TDMGame tdmGame : TDMGame.getTDMGames()) {
			inventory.addItem(tdmGame.getTDMItem());
		}

		inventory.setItem(17, SmellyInventory.getCloseInventoryItem());
	}

	private static class SpectateTDMInventoryScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			TDMGame tdmGame = TDMGame.getTDMGame(item);
			if (tdmGame != null) {
				// Teleport them to the TDM game
				player.teleport(tdmGame.getSpawn());
			}
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}
	
}
