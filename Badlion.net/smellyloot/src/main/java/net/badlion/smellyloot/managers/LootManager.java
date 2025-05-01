package net.badlion.smellyloot.managers;

import net.badlion.smellyloot.SmellyLoot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LootManager {

	/**
	 * Drops items from a loot table
	 * at a certain location
	 *
	 * @param lootTable - Name of the loot table
	 * @param numberOfItems - Number of items to choose
	 * @param location - Location to drop the items
	 *
	 * @return - List of all items dropped
	 */
	public static List<ItemStack> dropLoot(String lootTable, int numberOfItems, Location location) {
		if (!LootManager.validate(lootTable)) return null;

		World world = location.getWorld();

		List<ItemStack> droppedItems = SmellyLoot.getInstance().getItemsToDrop(lootTable, numberOfItems);
		for (ItemStack item : droppedItems) {
			world.dropItemNaturally(location, item);
		}

		return droppedItems;
	}

	/**
	 * Inserts items from a loot table
	 * into a chest
	 *
	 * @param lootTable - Name of the loot table
	 * @param numberOfItems - Number of items to choose
	 * @param location - Chest block location
	 *
	 * @return - List of all items inserted into chest
	 */
	public static List<ItemStack> dropLootChest(String lootTable, int numberOfItems, Location location) {
		if (!LootManager.validate(lootTable)) return null;

		// Create chest
		Block block = location.getBlock();
		if (block.getType() != Material.CHEST) {
			block.setType(Material.CHEST);
		}

		Chest chest = (Chest) block.getState();

		List<ItemStack> droppedItems = SmellyLoot.getInstance().getItemsToDrop(lootTable, numberOfItems);
        //Bukkit.getLogger().info("size " + droppedItems);
		for (ItemStack item : droppedItems) {
            //Bukkit.getLogger().info("Adding item " + item);
			chest.getBlockInventory().addItem(item);
		}

		return droppedItems;
	}

	/**
	 * Give loot items to player and
	 * drop on ground if inventory full
	 *
	 * @param lootTable - Name of the loot table
	 * @param numberOfItems - Number of items to choose
	 * @param player - Player
	 *
	 * @return - List of all items inserted into chest
	 */
	public static List<ItemStack> dropLootPlayer(String lootTable, int numberOfItems, Player player) {
		if (!LootManager.validate(lootTable)) return null;

		List<ItemStack> droppedItems = SmellyLoot.getInstance().getItemsToDrop(lootTable, numberOfItems);
		List<ItemStack> droppedItemsCopy = new ArrayList<>(droppedItems);

		// Try to add as many items as possible to their inventory
		Inventory inventory = player.getInventory();
		for (ItemStack item : droppedItems) {
			if (inventory.firstEmpty() != -1) {
				inventory.addItem(item);
				droppedItemsCopy.remove(item);
			}
		}

		// Drop the rest of the items on the floor
		World world = player.getWorld();
		for (ItemStack item : droppedItemsCopy) {
			world.dropItemNaturally(player.getLocation(), item);
		}

		return droppedItems;
	}

	/**
	 * Drop loot for an event
	 * at a specific location
	 *
	 * @param eventName - Name of event
	 * @param location - Location to drop the items
	 *
	 * @return - List of all items inserted into chest
	 */
	public static List<ItemStack> dropEventLoot(String eventName, Location location) {
		List<ItemStack> itemsDropped = new ArrayList<>();

		Map<String, Integer> itemDropAmounts = SmellyLoot.getInstance().getEventDrops().get(eventName);
		for (String lootTable : SmellyLoot.getInstance().getEventDrops().get(eventName).keySet()) {
			if (!LootManager.validate(lootTable)) return null;

			itemsDropped.addAll(LootManager.dropLoot(lootTable, itemDropAmounts.get(lootTable), location));
		}

		return itemsDropped;
	}

	/**
	 * Insert loot for an event
	 * in a chest
	 *
	 * @param eventName - Name of event
	 * @param location - Chest block location
	 *
	 * @return - List of all items inserted into chest
	 */
	public static List<ItemStack> dropEventLootChest(String eventName, Location location) {
		List<ItemStack> itemsDropped = new ArrayList<>();

		Map<String, Integer> itemDropAmounts = SmellyLoot.getInstance().getEventDrops().get(eventName);
        /*Bukkit.getLogger().info("Event Name: " + eventName);

        for (String s : SmellyLoot.getInstance().getEventDrops().keySet()) {
            Bukkit.getLogger().info(s);
        }*/

		for (String lootTable : SmellyLoot.getInstance().getEventDrops().get(eventName).keySet()) {
            Bukkit.getLogger().info("lootTable " + lootTable);
			if (!LootManager.validate(lootTable)) return null;

			itemsDropped.addAll(LootManager.dropLootChest(lootTable, itemDropAmounts.get(lootTable), location));
		}

		return itemsDropped;
	}

	/**
	 * Gives loot items to player and
	 * drops on ground if inventory full
	 *
	 * @param eventName - Name of event
	 * @param player - Player
	 *
	 * @return - List of all items inserted into chest
	 */
	public static List<ItemStack> dropEventLootPlayer(String eventName, Player player) {
		List<ItemStack> itemsDropped = new ArrayList<>();

		Map<String, Integer> itemDropAmounts = SmellyLoot.getInstance().getEventDrops().get(eventName);
		for (String lootTable : SmellyLoot.getInstance().getEventDrops().get(eventName).keySet()) {
			if (!LootManager.validate(lootTable)) return null;

			itemsDropped.addAll(LootManager.dropLootPlayer(lootTable, itemDropAmounts.get(lootTable), player));
		}

		return itemsDropped;
	}

	public static void startDropParty(String dropPartyName, String instanceLabel, boolean dropInitially) {
		if (!LootManager.validate(dropPartyName)) return;

		final SmellyLoot.DropParty dropParty = SmellyLoot.getInstance().getDropParties().get(dropPartyName);
		if (dropParty != null) {
			SmellyLoot.getInstance().getRunningDropParties().put(instanceLabel, SmellyLoot.getInstance().getServer().getScheduler().runTaskTimer(SmellyLoot.getInstance(), new Runnable() {
				@Override
				public void run() {
					for (Location dropLocation : dropParty.getDropLocations()) {
						if (Math.random() <= dropParty.getDropChance() && dropParty.getDropChance() != 0) {
							LootManager.dropLoot(dropParty.getLootTable(), 1, dropLocation);
						}
					}
				}
			}, dropInitially ? 0L : dropParty.getDropInterval(), dropParty.getDropInterval()).getTaskId());
		}
	}

	public static void endDropParty(String instanceLabel) {
		Integer taskId = SmellyLoot.getInstance().getRunningDropParties().get(instanceLabel);
		if (taskId != null) {
			SmellyLoot.getInstance().getServer().getScheduler().cancelTask(taskId);
		}
	}

	private static boolean validate(String name) {
		if (SmellyLoot.getInstance().getLootTablesCommonDrops().get(name) == null && SmellyLoot.getInstance().getEventDrops().get(name) == null
				&& SmellyLoot.getInstance().getDropParties().get(name) == null) {
			SmellyLoot.getInstance().getServer().getLogger().severe("Invalid argument specified for SmellyLoot: " + name);
			SmellyLoot.getInstance().getServer().getLogger().severe("Invalid argument specified for SmellyLoot: " + name);
			SmellyLoot.getInstance().getServer().getLogger().severe("Invalid argument specified for SmellyLoot: " + name);

			return false;
		}

		return true;
	}

}
