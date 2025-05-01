package net.badlion.arenalobby.inventories.party;

import net.badlion.arenalobby.managers.PotPvPPlayerManager;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class PartyEventsInventory {

	private static SmellyInventory smellyInventory;

	public static void initialize() {
		SmellyInventory smellyInventory = new SmellyInventory(new PartyEventsInventoryScreenHandler(), 27,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "Party Events");

		// Party Fight item
		smellyInventory.getMainInventory().addItem(ItemStackUtil.createItem(Material.DIAMOND_AXE, ChatColor.AQUA + "Party Fight",
				ChatColor.BLUE + "Split your party in two", ChatColor.BLUE + "and fight it out in an arena"));

		// Party FFA item
		smellyInventory.getMainInventory().addItem(ItemStackUtil.createItem(Material.IRON_SWORD, ChatColor.AQUA + "Party FFA",
				ChatColor.BLUE + "Have an FFA between all", ChatColor.BLUE + "the members in your party"));

		// Red Rover item
		smellyInventory.getMainInventory().addItem(ItemStackUtil.createItem(Material.REDSTONE_BLOCK, ChatColor.AQUA + "Red Rover",
				ChatColor.BLUE + "Choose two captains and", ChatColor.BLUE + "let them pick players for their teams."));

		// Tournament item
		smellyInventory.getMainInventory().addItem(ItemStackUtil.createItem(Material.GOLD_INGOT, ChatColor.AQUA + "Tournament",
				ChatColor.BLUE + "Host a 1v1 tournament", ChatColor.BLUE + "with members in your party"));

		PartyEventsInventory.smellyInventory = smellyInventory;
	}

	public static void openPartyEventsInventory(Player player) {
		PotPvPPlayerManager.addDebug(player, "Open party events inventory");

		BukkitUtil.openInventory(player, PartyEventsInventory.smellyInventory.getMainInventory());
	}

	private static class PartyEventsInventoryScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {

			switch (slot) {
				case 0: // Party Fight
					PartyFightChooseKitInventory.openPartyFightChooseKitInventory(player);
					break;
				case 1: // Party FFA
					PartyFFAChooseKitInventory.openPartyFFAChooseKitInventory(player);
					break;
				case 2: // Red Rover
					RedRoverChooseKitInventory.openRedRoverChooseKitInventory(player);
					break;
				case 3: // Tournament
					PartyTournamentChooseKitInventory.openPartyTournamentChooseKitInventory(player);
					break;
			}
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}

	public static SmellyInventory getSmellyInventory() {
		return smellyInventory;
	}
}