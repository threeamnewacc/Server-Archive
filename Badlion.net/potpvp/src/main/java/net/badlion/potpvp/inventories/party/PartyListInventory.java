package net.badlion.potpvp.inventories.party;

import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.managers.PotPvPPlayerManager;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PartyListInventory {

	private static boolean firstPage;
	                                                      // TODO: REMOVE PAGES IF NOT BEING USED?
	private static SmellyInventory smellyInventory;

	private static PartyListScreenHandler partyListScreenHandler;

	private static ItemStack nextPageItem;
	private static ItemStack prevPageItem;

	private static ItemStack placeHolderItem;

	private static Map<Group, ItemStack> partyListingItems = new HashMap<>();

	public static void initialize() {
		// Items
		PartyListInventory.nextPageItem = ItemStackUtil.createItem(Material.BOOK_AND_QUILL, ChatColor.GREEN + "Next Page");

		PartyListInventory.prevPageItem = ItemStackUtil.createItem(Material.BOOK_AND_QUILL, ChatColor.GREEN + "Previous Page");

		PartyListInventory.placeHolderItem = ItemStackUtil.createItem(Material.PISTON_BASE, ChatColor.LIGHT_PURPLE + "Click on head to duel party");

		// Screen handler
		PartyListInventory.partyListScreenHandler = new PartyListScreenHandler();

		// Smelly inventory
		SmellyInventory smellyInventory = new SmellyInventory(PartyListInventory.partyListScreenHandler, 54,
				ChatColor.BOLD + ChatColor.AQUA.toString() + "Party List");

		PartyListInventory.fillPlaceholderItems(smellyInventory.getMainInventory());

		PartyListInventory.smellyInventory = smellyInventory;
	}

	private static ItemStack getSkullForParty(Group group) {
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);

		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

		skullMeta.setDisplayName(ChatColor.GREEN + group.getLeader().getName() + "'s Party (" + group.players().size() + ")");

		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.YELLOW + group.getLeader().getName());
		for (Player player : group.players()) {
			if (player != group.getLeader()) {
				lore.add(ChatColor.YELLOW + player.getName());
			}
		}

		skullMeta.setLore(lore);
		//skullMeta.setOwner(group.getLeader().getName());
		skull.setItemMeta(skullMeta);
		return skull;
	}

	private static void fillPlaceholderItems(Inventory inventory) {
		int size = inventory.getSize();

		inventory.setItem(size - 9, PartyListInventory.placeHolderItem);
		inventory.setItem(size - 8, PartyListInventory.placeHolderItem); // 46
		inventory.setItem(size - 7, PartyListInventory.placeHolderItem);
		inventory.setItem(size - 6, PartyListInventory.placeHolderItem);
		inventory.setItem(size - 5, PartyListInventory.placeHolderItem);
		inventory.setItem(size - 4, PartyListInventory.placeHolderItem);
		inventory.setItem(size - 3, PartyListInventory.placeHolderItem); // 51
		inventory.setItem(size - 2, PartyListInventory.placeHolderItem);
	}

	public static void openPartyListInventory(Player player) {
		PotPvPPlayerManager.addDebug(player, "Open party list inventory");

		BukkitUtil.openInventory(player, PartyListInventory.smellyInventory.getMainInventory());
	}

	/**
	 * Called whenever a party is:
	 * created.
	 *
	 * @param group - Party
	 */
	public static void updatePartyListing(Group group) {
		ItemStack item = PartyListInventory.getSkullForParty(group);

		// Remove the old item
		PartyListInventory.removePartyListing(group);

		Inventory currentInventory = PartyListInventory.smellyInventory.getMainInventory();

	  	if (currentInventory.firstEmpty() != -1) {
		    currentInventory.addItem(item);
	    } else {
		    // Change placeholder items into navigation items of last inventory
		    currentInventory.setItem(51, PartyListInventory.nextPageItem);

	        // Create a new inventory
		    Inventory newInventory = smellyInventory.createInventory(
				    (SmellyInventory.FakeHolder) currentInventory.getHolder(), PartyListInventory.partyListScreenHandler,
				    51, 54, ChatColor.BOLD + ChatColor.AQUA.toString() + "Party List");

		    // Placeholder items
		    PartyListInventory.fillPlaceholderItems(newInventory);

		    if (!PartyListInventory.firstPage) {
			    newInventory.setItem(46, PartyListInventory.prevPageItem);
		    }

		    newInventory.addItem(item);

		    PartyListInventory.firstPage = false;
	    }

		PartyListInventory.partyListingItems.put(group, item);
	}

	/**
	 * Called whenever a party is
	 * in lobby state or waiting
	 * for a match.
	 *
	 * @param group - Party
	 */
	public static void removePartyListing(Group group) {
		ItemStack old = PartyListInventory.partyListingItems.remove(group);
		if (old != null) {
			Inventory inv = PartyListInventory.smellyInventory.getMainInventory();
			inv.remove(old);
			while (((SmellyInventory.FakeHolder) inv.getHolder()).hasSubInventories()) {
				inv = ((SmellyInventory.FakeHolder) inv.getHolder()).getSubInventory(51);
				inv.remove(old);
			}
		}
	}

	private static void duelPlayerHead(Player player, ItemStack item) {
		// Find the party leader
		String name = item.getItemMeta().getDisplayName().substring(2);
		String[] strings = name.split("'s P");
		name = strings[0];

		Player viewed = PotPvP.getInstance().getServer().getPlayerExact(name);

		if (viewed == null) {
			player.sendMessage(ChatColor.RED + name + "'s party does not exist anymore!");

			BukkitUtil.closeInventory(player);
		}

		// Send them a duel request
		player.performCommand("duel " + name);
	}

	private static class PartyListScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			if (!item.getType().equals(Material.PISTON_BASE)) {
				if (slot == 46) { // Previous page
					// TODO: HOTFIX FOR PAGINATION SYSTEM
					//BukkitUtil.openInventory(player, fakeHolder.getParentInventory());
				} else if (slot == 51) { // Next page
					BukkitUtil.openInventory(player, fakeHolder.getSubInventory(51));
				} else if (item.getType().equals(Material.SKULL_ITEM)) { // Party head
					PartyListInventory.duelPlayerHead(player, item);
				}
			}
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}

}
