package net.badlion.potpvp.inventories.party;

import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.helpers.PartyHelper;
import net.badlion.potpvp.managers.PotPvPPlayerManager;
import net.badlion.potpvp.matchmaking.Match;
import net.badlion.potpvp.states.matchmaking.GameState;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class PartyPlayerInventoriesInventory {

	// Items
	private static ItemStack yourPartyItem;
	private static ItemStack enemyPartyItem;

	private static ItemStack nextPageItem;
	private static ItemStack prevPageItem;

	private static ItemStack placeHolderItem;

	// Screen handlers
	private static SelectPartyScreenHandler selectPartyScreenHandler;
	private static SelectPlayerScreenHandler selectPlayerScreenHandler;

	private static Map<Group, SmellyInventory> cachedViewPartyPlayerInventoriesInventory = new HashMap<>();

	public static void initialize() {
		// Items
		PartyPlayerInventoriesInventory.yourPartyItem = ItemStackUtil.createItem(Material.PISTON_BASE, ChatColor.GREEN + "Your Party");

		PartyPlayerInventoriesInventory.enemyPartyItem = ItemStackUtil.createItem(Material.PISTON_BASE, ChatColor.GREEN + "Enemy Party");

		PartyPlayerInventoriesInventory.nextPageItem = ItemStackUtil.createItem(Material.BOOK_AND_QUILL, ChatColor.GREEN + "Next Page");

		PartyPlayerInventoriesInventory.prevPageItem = ItemStackUtil.createItem(Material.BOOK_AND_QUILL, ChatColor.GREEN + "Previous Page");

		PartyPlayerInventoriesInventory.placeHolderItem = ItemStackUtil.createItem(Material.PISTON_BASE, ChatColor.LIGHT_PURPLE + "Click on head to view");

		// Screen handlers
		PartyPlayerInventoriesInventory.selectPartyScreenHandler = new SelectPartyScreenHandler();
		PartyPlayerInventoriesInventory.selectPlayerScreenHandler = new SelectPlayerScreenHandler();
	}

	/**
	 * Opens view party player inventories inventory
	 * for player. If the inventory has not been
	 * opened by another player in their party, it is
	 * created. After the creation, the inventory is
	 * cached in an attempt to save processing power
	 * in exchange for a bit of memory
	 *
	 * @param player - Player opening inventory
	 * @param group  - Player's group
	 */
	public static void openViewPartyPlayerInventoriesInventory(Player player, Group group) {
		SmellyInventory smellyInventory = PartyPlayerInventoriesInventory.cachedViewPartyPlayerInventoriesInventory.get(group);
		if (smellyInventory != null) {
			BukkitUtil.openInventory(player, smellyInventory.getMainInventory());
			return;
		}

		// Get enemy party
		Match match = GroupStateMachine.regularMatchState.getMatchFromGroup(group);
		Group enemyParty = match.getGroup1() == group ? match.getGroup2() : match.getGroup1();

		// Create new ViewPartyPlayerInventoriesInventory
		smellyInventory = PartyPlayerInventoriesInventory.createViewPartyPlayerInventoriesInventory(group, enemyParty);

		PotPvPPlayerManager.addDebug(player, "Open view party player inventories inventory");

		// Open inventory for player
	 	BukkitUtil.openInventory(player, smellyInventory.getMainInventory());
	}

	private static SmellyInventory createViewPartyPlayerInventoriesInventory(Group yourParty, Group enemyParty) {
		// Create smelly inventory
		SmellyInventory smellyInventory = new SmellyInventory(PartyPlayerInventoriesInventory.selectPartyScreenHandler, 9,
				ChatColor.BOLD + ChatColor.AQUA.toString() + "Choose Party");

		// Create main inventory
		if (yourParty != enemyParty) {
			smellyInventory.getMainInventory().addItem(PartyPlayerInventoriesInventory.yourPartyItem, PartyPlayerInventoriesInventory.enemyPartyItem);
		} else {
			smellyInventory.getMainInventory().addItem(PartyPlayerInventoriesInventory.yourPartyItem); // Matches with only one group
		}

		// Find out # of rows needed for inventory
		int size = yourParty.players().size();
		int count = 0;
		int rowsNeeded = (int) Math.floor(size / 9D);
		if (size % 9 != 0) rowsNeeded++;
		if (rowsNeeded > 5) rowsNeeded = 5;
		int inventorySize = 9 * (rowsNeeded + 1);

		// Create own players inventory
		boolean firstPage = true;
		Inventory yourPartyInventory = smellyInventory.createInventory(smellyInventory.getFakeHolder(), PartyPlayerInventoriesInventory.selectPlayerScreenHandler,
				0, inventorySize, ChatColor.BOLD + ChatColor.AQUA.toString() + "Choose Player");

		// Placeholder items
		PartyPlayerInventoriesInventory.fillPlaceholderItems(yourPartyInventory);

		// Add all heads
		yourPartyInventory.addItem(PartyPlayerInventoriesInventory.getSkullForPlayer(yourParty.getLeader(), true));
		for (Player player : yourParty.players()) {
			//Bukkit.getLogger().info("Is player leader? (" + player.getName() + "): " + (player != yourParty.getLeader()));
			if (player != yourParty.getLeader()) {
				// Is this player alive in the game?
				if (player.getGameMode() == GameMode.CREATIVE) {
					continue;
				}

				// Is the current inventory full?
				if (yourPartyInventory.firstEmpty() == -1) {
					// Change placeholder items into navigation items of last inventory
					if (!firstPage) {
						yourPartyInventory.setItem(inventorySize - 8, PartyPlayerInventoriesInventory.prevPageItem); // 46
					}

					yourPartyInventory.setItem(inventorySize - 3, PartyPlayerInventoriesInventory.nextPageItem); // 51

					// Create a new inventory
					int newSize = size - count;
					rowsNeeded = (int) Math.floor(newSize / 9D);
					if (newSize % 9 != 0) rowsNeeded++;
					if (rowsNeeded > 5) rowsNeeded = 5;
					inventorySize = 9 * (rowsNeeded + 1);
					yourPartyInventory = smellyInventory.createInventory((SmellyInventory.FakeHolder) yourPartyInventory.getHolder(),
							PartyPlayerInventoriesInventory.selectPlayerScreenHandler, 51,
							inventorySize, ChatColor.BOLD + ChatColor.AQUA.toString() + "Choose Player");

					// Placeholder items
					PartyPlayerInventoriesInventory.fillPlaceholderItems(yourPartyInventory);

					firstPage = false;
				}

				yourPartyInventory.addItem(PartyPlayerInventoriesInventory.getSkullForPlayer(player, false));
			}

			count++;
		}

		count = 0;

		// Create enemy players inventory
		if (yourParty != enemyParty) { // Matches with two groups
			Inventory enemyPartyInventory = smellyInventory.createInventory(smellyInventory.getFakeHolder(), PartyPlayerInventoriesInventory.selectPlayerScreenHandler,
					1, inventorySize, ChatColor.BOLD + ChatColor.AQUA.toString() + "Choose Player");

			// Placeholder items
			PartyPlayerInventoriesInventory.fillPlaceholderItems(enemyPartyInventory);

			// Add all heads
			enemyPartyInventory.addItem(PartyPlayerInventoriesInventory.getSkullForPlayer(enemyParty.getLeader(), true));
			for (Player player : enemyParty.players()) {
				//Bukkit.getLogger().info("Is player leader? (" + player.getName() + "): " + (player != enemyParty.getLeader()));
				if (player != enemyParty.getLeader()) {
					// Is this player alive in the game?
					if (player.getGameMode() == GameMode.CREATIVE) {
						continue;
					}

					// Is the current inventory full?
					if (enemyPartyInventory.firstEmpty() == -1) {
						// Change placeholder items into navigation items of last inventory
						if (!firstPage) {
							enemyPartyInventory.setItem(inventorySize - 8, PartyPlayerInventoriesInventory.prevPageItem); // 46
						}
						enemyPartyInventory.setItem(inventorySize - 3, PartyPlayerInventoriesInventory.nextPageItem); // 51

						// Create a new inventory
						int newSize = size - count;
						rowsNeeded = (int) Math.floor(newSize / 9D);
						if (newSize % 9 != 0) rowsNeeded++;
						if (rowsNeeded > 5) rowsNeeded = 5;
						inventorySize = 9 * (rowsNeeded + 1);
						enemyPartyInventory = smellyInventory.createInventory((SmellyInventory.FakeHolder) enemyPartyInventory.getHolder(),
								PartyPlayerInventoriesInventory.selectPlayerScreenHandler, 51,
								inventorySize, ChatColor.BOLD + ChatColor.AQUA.toString() + "Choose Player");

						// Placeholder items
						PartyPlayerInventoriesInventory.fillPlaceholderItems(enemyPartyInventory);

						firstPage = false;
					}

					enemyPartyInventory.addItem(PartyPlayerInventoriesInventory.getSkullForPlayer(player, false));

					count++;
				}
			}
		}

		// Cache this in case it gets accessed again
		PartyPlayerInventoriesInventory.cachedViewPartyPlayerInventoriesInventory.put(yourParty, smellyInventory);

		return smellyInventory;
	}

	public static void cleanUpCachedInventories(Group... groups) {
		for (Group group : groups) {
			PartyPlayerInventoriesInventory.cachedViewPartyPlayerInventoriesInventory.remove(group);
		}
	}

	private static void fillPlaceholderItems(Inventory inventory) {
		int size = inventory.getSize();

		inventory.setItem(size - 9, PartyPlayerInventoriesInventory.placeHolderItem);
		inventory.setItem(size - 8, PartyPlayerInventoriesInventory.placeHolderItem); // 46
		inventory.setItem(size - 7, PartyPlayerInventoriesInventory.placeHolderItem);
		inventory.setItem(size - 6, PartyPlayerInventoriesInventory.placeHolderItem);
		inventory.setItem(size - 5, PartyPlayerInventoriesInventory.placeHolderItem);
		inventory.setItem(size - 4, PartyPlayerInventoriesInventory.placeHolderItem);
		inventory.setItem(size - 3, PartyPlayerInventoriesInventory.placeHolderItem); // 51
		inventory.setItem(size - 2, PartyPlayerInventoriesInventory.placeHolderItem);
	}

	private static ItemStack getSkullForPlayer(Player player, boolean leader) {
		return ItemStackUtil.createItem(Material.SKULL_ITEM, (short) 3, ChatColor.GREEN + player.getName() + (leader ? " (Leader)" : ""));
	}

	private static void openPlayerInventory(SmellyInventory smellyInventory, Player player, ItemStack item) {
		// Who is the player?
		String name = item.getItemMeta().getDisplayName().substring(2);

		Player viewed = PotPvP.getInstance().getServer().getPlayerExact(name);

		if (viewed == null) {
			player.sendMessage(ChatColor.RED + name + " has logged off!");

			BukkitUtil.closeInventory(player);
			return;
		}

		// Are they alive?
		Group group = PotPvP.getInstance().getPlayerGroup(viewed);
		ItemStack[] armorContents = ((Match) GameState.getGroupGame(group)).getGroupArmor().get(viewed.getUniqueId().toString());
		if (armorContents == null) {
			// Open up their inventory
			BukkitUtil.openInventory(player, PartyHelper.createPlayerInventory(smellyInventory, viewed));
		} else {
			// Open up their inventory
			BukkitUtil.openInventory(player, PartyHelper.createPlayerInventory(smellyInventory, viewed, armorContents,
					((Match) GameState.getGroupGame(group)).getGroupItems().get(viewed.getUniqueId().toString()),
				    ((Match) GameState.getGroupGame(group)).getGroupExtraItems().get(viewed.getUniqueId().toString())));
		}
	}

	public static void handlePlayerDeath(Player player, Group group) {
		SmellyInventory smellyInventory = PartyPlayerInventoriesInventory.cachedViewPartyPlayerInventoriesInventory.get(group);

		if (smellyInventory == null) return;

		for (ItemStack itemStack : smellyInventory.getFakeHolder().getSubInventory(0).getContents()) {
			if (itemStack != null && itemStack.getType() == Material.SKULL_ITEM) {
				if (itemStack.getItemMeta().getDisplayName().contains(player.getName())) {
					smellyInventory.getFakeHolder().getSubInventory(0).remove(itemStack);
				}
			}
		}
	}

	private static class SelectPartyScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			switch (slot) {
				case 0: // Your Party item
					// Create inventories real-time since each player has their own inventory

					BukkitUtil.openInventory(player, fakeHolder.getSubInventory(0));
					break;
				case 1: // Enemy Party item
					BukkitUtil.openInventory(player, fakeHolder.getSubInventory(1));
					break;
			}
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}

	private static class SelectPlayerScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			if (!item.getType().equals(Material.PISTON_BASE)) {
				if (slot == 46) { // Previous page
					BukkitUtil.openInventory(player, fakeHolder.getParentInventory());
				} else if (slot == 51) { // Next page
					BukkitUtil.openInventory(player, fakeHolder.getSubInventory(51));
				} else { // Player head
					PartyPlayerInventoriesInventory.openPlayerInventory(fakeHolder.getSmellyInventory(), player, item);
				}
			}
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}

}
