package net.badlion.gberry.inventories;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class UHCBoosterInventory {

	private static SmellyInventory smellyInventory;

	public static void initializeUHC() {
		UHCBoosterInventory.smellyInventory = new SmellyInventory(new UHCBoosterInventoryHandler(), 9,
				ChatColor.BOLD + ChatColor.AQUA.toString() + "UHC Boosters");
	}

	public static void openUHCBoosterInventory(Player player) {
		BukkitUtil.openInventory(player, UHCBoosterInventory.smellyInventory.getMainInventory());
	}

	private static class UHCBoosterInventoryHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, final Player player, InventoryClickEvent event, ItemStack item, int slot) {
			if (item.getType() == Material.BOOK) {
				if (Gberry.UHC_BOOSTER_ACTIVE) {
					player.sendMessage(ChatColor.RED + "There is already a booster active on UHC.");
					event.setCancelled(true);
				} else {
					Gberry.UHC_BOOSTER_ACTIVE = true;
					player.sendMessage("Your UHC booster has been activated!");
					new BukkitRunnable() {
						@Override
						public void run() {
							Gberry.UHC_BOOSTER_ACTIVE = false;
							player.sendMessage("Your UHC booster has expired.");
						}
					}.runTaskLater(Gberry.plugin, 72000);
				}
			}
			BukkitUtil.closeInventory(player);
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}
}