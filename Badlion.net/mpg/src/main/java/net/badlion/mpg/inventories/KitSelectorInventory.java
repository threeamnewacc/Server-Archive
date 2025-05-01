package net.badlion.mpg.inventories;

import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.mpg.kits.MPGKit;
import net.badlion.mpg.managers.MPGKitManager;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class KitSelectorInventory {

	private static SmellyInventory smellyInventory;

	public static void initialize() {
		KitSelectorInventory.smellyInventory = new SmellyInventory(new KitSelectorInventoryScreenHandler(), 54,
															  ChatColor.BOLD + ChatColor.AQUA.toString() + "Select MPGKit");

		ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(ChatColor.GREEN + "MPGKit 1");
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.YELLOW + "Load MPGKit 1");
		itemMeta.setLore(lore);

		KitSelectorInventory.smellyInventory.getMainInventory().addItem(item);
	}

	public static void openKitSelection(Player player) {
		BukkitUtil.openInventory(player, KitSelectorInventory.smellyInventory.getMainInventory());
	}

	private static class KitSelectorInventoryScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			String[] parts = item.getItemMeta().getDisplayName().split(" ");
			int kitNumber = Integer.parseInt(parts[1]);
			MPGKit kit = MPGKitManager.getKit(player, kitNumber);
			MPGPlayerManager.getMPGPlayer(player.getUniqueId()).setKit(kit);
			player.sendMessage(ChatColor.GREEN + "MPGKit #" + kitNumber + " has been selected.");
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}

}
