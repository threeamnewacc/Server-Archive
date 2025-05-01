package net.badlion.survivalgames.inventories;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SelectionChestInventory {

	private static ItemStack selectionChestItem;

	private static List<ItemStack> maxTierTwoItems = new ArrayList<>();

    public static void initialize() {
	    SelectionChestInventory.selectionChestItem = ItemStackUtil.createItem(Material.ENDER_CHEST, ChatColor.GREEN + "Selection Chest",
			    ChatColor.YELLOW + "Right click to open an inventory",
			    ChatColor.YELLOW + "of T2 items. You can choose only",
			    ChatColor.YELLOW + "one item that to get.");

	    // Add all max tier 2 items
	    SelectionChestInventory.maxTierTwoItems.add(new ItemStack(Material.DIAMOND));
	    SelectionChestInventory.maxTierTwoItems.add(new ItemStack(Material.IRON_AXE));
	    SelectionChestInventory.maxTierTwoItems.add(new ItemStack(Material.STONE_SWORD));
	    SelectionChestInventory.maxTierTwoItems.add(new ItemStack(Material.IRON_HELMET));
	    SelectionChestInventory.maxTierTwoItems.add(new ItemStack(Material.IRON_CHESTPLATE));
	    SelectionChestInventory.maxTierTwoItems.add(new ItemStack(Material.IRON_LEGGINGS));
	    SelectionChestInventory.maxTierTwoItems.add(new ItemStack(Material.IRON_BOOTS));
	    SelectionChestInventory.maxTierTwoItems.add(new ItemStack(Material.CHAINMAIL_HELMET));
	    SelectionChestInventory.maxTierTwoItems.add(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
	    SelectionChestInventory.maxTierTwoItems.add(new ItemStack(Material.CHAINMAIL_LEGGINGS));
	    SelectionChestInventory.maxTierTwoItems.add(new ItemStack(Material.CHAINMAIL_BOOTS));
	    SelectionChestInventory.maxTierTwoItems.add(new ItemStack(Material.GOLD_HELMET));
	    SelectionChestInventory.maxTierTwoItems.add(new ItemStack(Material.GOLD_CHESTPLATE));
	    SelectionChestInventory.maxTierTwoItems.add(new ItemStack(Material.GOLD_LEGGINGS));
	    SelectionChestInventory.maxTierTwoItems.add(new ItemStack(Material.GOLD_BOOTS));
	    SelectionChestInventory.maxTierTwoItems.add(new ItemStack(Material.IRON_INGOT, 2));
	    SelectionChestInventory.maxTierTwoItems.add(new ItemStack(Material.BOW));
	    SelectionChestInventory.maxTierTwoItems.add(new ItemStack(Material.ARROW, 3));
	    SelectionChestInventory.maxTierTwoItems.add(new ItemStack(Material.WEB, 2));
	    SelectionChestInventory.maxTierTwoItems.add(new ItemStack(Material.FLINT_AND_STEEL));
	    SelectionChestInventory.maxTierTwoItems.add(new ItemStack(Material.STICK, 2));
	    SelectionChestInventory.maxTierTwoItems.add(new ItemStack(Material.BAKED_POTATO, 2));
	    SelectionChestInventory.maxTierTwoItems.add(new ItemStack(Material.GOLDEN_CARROT, 3));
	    SelectionChestInventory.maxTierTwoItems.add(new ItemStack(Material.GRILLED_PORK, 2));
    }

	public static ItemStack getSelectionChestItem() {
		return SelectionChestInventory.selectionChestItem;
	}

	public static void openSelectionChestInventory(Player player) {
		SmellyInventory smellyInventory = new SmellyInventory(new SelectionChestInventoryScreenHandler(), 54,
				ChatColor.BOLD + ChatColor.AQUA.toString() + "Selection Chest");

		// Clear inventory to get rid the close inventory item
		smellyInventory.getMainInventory().clear();

		// Fill chest with the max T2 items
		for (ItemStack itemStack : SelectionChestInventory.maxTierTwoItems) {
			smellyInventory.getMainInventory().addItem(itemStack);
		}

		BukkitUtil.openInventory(player, smellyInventory.getMainInventory());
	}

	private static class SelectionChestInventoryScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			// Remove selection chest from player's inventory
			for (int i = 0; i < player.getInventory().getSize(); i++) {
				ItemStack inventoryItem = player.getInventory().getItem(i);
				if (inventoryItem != null && inventoryItem.getType() == SelectionChestInventory.getSelectionChestItem().getType()) {
					int itemAmount = inventoryItem.getAmount();
					if (itemAmount > 1) {
						inventoryItem.setAmount(itemAmount - 1);
					} else {
						player.getInventory().setItem(i, null);
					}

					break;
				}
			}

			// Remove item from selection chest inventory
			fakeHolder.getInventory().setItem(slot, null);

			// Drop item at player's feet
			player.getWorld().dropItemNaturally(player.getLocation(), item);

			// Play sound
			player.getWorld().playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "ITEM_BREAK", "ENTITY_ITEM_BREAK"), 1.4F, 0.4F);

			// Close inventory
			BukkitUtil.closeInventory(player);
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}

}
