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

public class ArenaBoosterInventory {

	private static SmellyInventory smellyInventory;

	public static void initializeArena() {
		ArenaBoosterInventory.smellyInventory = new SmellyInventory(new ArenaBoosterInventoryHandler(), 9,
				ChatColor.BOLD + ChatColor.AQUA.toString() + "Arena Boosters");
	}

	public static void openArenaBoosterInventory(Player player) {
		BukkitUtil.openInventory(player, ArenaBoosterInventory.smellyInventory.getMainInventory());
	}

	private static class ArenaBoosterInventoryHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, final Player player, InventoryClickEvent event, ItemStack item, int slot) {
			if (item.getType() == Material.BOOK) {
				if (Gberry.ARENA_BOOSTER_ACTIVE) {
					player.sendMessage(ChatColor.RED + "There is already a booster active on Arena.");
					event.setCancelled(true);
				} else {
					Gberry.ARENA_BOOSTER_ACTIVE = true;
					player.sendMessage("Your Arena booster has been activated!");
					removeArenaBooster();
					new BukkitRunnable() {
						@Override
						public void run() {
							Gberry.ARENA_BOOSTER_ACTIVE = false;
							player.sendMessage("Your Arena booster has expired.");
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

	public static void addArenaBooster() {
		ArenaBoosterInventory.smellyInventory.getMainInventory().addItem(ItemStackUtil.createItem(Material.BOOK, 1));
	}

	public static void removeArenaBooster() {
		ArenaBoosterInventory.smellyInventory.getMainInventory().getItem(0).setAmount(ArenaBoosterInventory.smellyInventory.getMainInventory().getItem(0).getAmount() - 1);
	}
}