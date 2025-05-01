package net.badlion.gberry.inventories;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class SGBoosterInventory {

	private static SmellyInventory smellyInventory;

	public static void initializeSG() {
		SGBoosterInventory.smellyInventory = new SmellyInventory(new SGBoosterInventoryHandler(), 9,
				ChatColor.BOLD + ChatColor.AQUA.toString() + "SG Boosters");
	}

	public static void openSGBoosterInventory(Player player) {
		BukkitUtil.openInventory(player, SGBoosterInventory.smellyInventory.getMainInventory());
	}

	private static class SGBoosterInventoryHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, final Player player, InventoryClickEvent event, ItemStack item, int slot) {
			if (item.getType() == Material.BOOK) {
				if (Gberry.SG_BOOSTER_ACTIVE) {
					player.sendMessage(ChatColor.RED + "There is already a booster active on SG.");
					event.setCancelled(true);
				} else {
					Gberry.SG_BOOSTER_ACTIVE = true;
					player.sendMessage("Your SG booster has been activated!");
					removeSGBooster();
					new BukkitRunnable() {
						@Override
						public void run() {
							Gberry.SG_BOOSTER_ACTIVE = false;
							player.sendMessage("Your SG booster has expired.");
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

	public static void addSGBooster() {
		SGBoosterInventory.smellyInventory.getMainInventory().addItem(ItemStackUtil.createItem(Material.BOOK, 1));
	}

	public static void removeSGBooster() {
		SGBoosterInventory.smellyInventory.getMainInventory().getItem(0).setAmount(SGBoosterInventory.smellyInventory.getMainInventory().getItem(0).getAmount() - 1);
	}
}