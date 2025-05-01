package net.badlion.gberry.inventories;

import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class BoosterInventory {

	private static SmellyInventory smellyInventory;

	public static void initialize() {
		initializeBooster();
		ArenaBoosterInventory.initializeArena();
		MiniUHCBoosterInventory.initializeMiniUHC();
		SGBoosterInventory.initializeSG();
		UHCBoosterInventory.initializeUHC();
	}

	public static void initializeBooster() {
		BoosterInventory.smellyInventory = new SmellyInventory(new BoosterInventoryHandler(), 27,
				ChatColor.BOLD + ChatColor.AQUA.toString() + "Boosters");

		BoosterInventory.smellyInventory.getMainInventory().addItem(ItemStackUtil.createItem(Material.POTION, (short) 8197, ChatColor.GREEN + "Arena"));
		BoosterInventory.smellyInventory.getMainInventory().addItem(ItemStackUtil.createItem(Material.GOLDEN_APPLE, (short) 1, ChatColor.BLUE + "UHC"));
		BoosterInventory.smellyInventory.getMainInventory().addItem(ItemStackUtil.createItem(Material.APPLE, ChatColor.RED + "MiniUHC"));
		BoosterInventory.smellyInventory.getMainInventory().addItem(ItemStackUtil.createItem(Material.ENCHANTED_BOOK, ChatColor.LIGHT_PURPLE + "SG"));
		BoosterInventory.smellyInventory.getMainInventory().addItem(ItemStackUtil.createItem(Material.PAPER, ChatColor.GREEN + "Personal"));
	}

	public static void openBoosterInventory(Player player) {
		BukkitUtil.openInventory(player, BoosterInventory.smellyInventory.getMainInventory());
	}

	private static class BoosterInventoryHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, final Player player, InventoryClickEvent event, ItemStack item, int slot) {
			if (item.getItemMeta().getDisplayName() == ChatColor.GREEN + "Arena") {
				ArenaBoosterInventory.openArenaBoosterInventory(player);
			}
			if (item.getItemMeta().getDisplayName() == ChatColor.BLUE + "UHC") {
				UHCBoosterInventory.openUHCBoosterInventory(player);
			}
			if (item.getItemMeta().getDisplayName() == ChatColor.RED + "MiniUHC") {
				MiniUHCBoosterInventory.openMiniUHCBoosterInventory(player);
			}
			if (item.getItemMeta().getDisplayName() == ChatColor.LIGHT_PURPLE + "SG") {
				SGBoosterInventory.openSGBoosterInventory(player);
			}

			BukkitUtil.closeInventory(player);
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}

}