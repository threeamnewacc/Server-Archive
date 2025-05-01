package net.badlion.potpvp.inventories.party;

import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.helpers.PartyHelper;
import net.badlion.potpvp.managers.PotPvPPlayerManager;
import net.badlion.potpvp.states.matchmaking.GameState;
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
				ChatColor.BLUE + "Split your party in two", ChatColor.BLUE + "and fight in a red rover matcha"));

		PartyEventsInventory.smellyInventory = smellyInventory;
	}

	public static void openPartyEventsInventory(Player player) {
		PotPvPPlayerManager.addDebug(player, "Open party events inventory");

		BukkitUtil.openInventory(player, PartyEventsInventory.smellyInventory.getMainInventory());
	}

	private static class PartyEventsInventoryScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			Group group = PotPvP.getInstance().getPlayerGroup(player);
			if (group.hasDeadPlayers()) {
				player.sendMessage(ChatColor.RED + "All players must be alive in your party to start a party event.");
				player.sendMessage(ChatColor.YELLOW + "Dead players: " + group.getDeadPlayerString());

				BukkitUtil.closeInventory(player);
				return;
			}

			switch (slot) {
				case 0: // Party Fight
					if (GroupStateMachine.getInstance().getCurrentState(group) == GroupStateMachine.partyState
							&& GameState.getGroupGame(group) == null) {
						if (group.players().size() < PartyHelper.MIN_PARTY_FIGHT_PLAYERS) {
							player.sendMessage(ChatColor.RED + "You do not have enough players in your party (" + PartyHelper.MIN_PARTY_FIGHT_PLAYERS + " required).");

							BukkitUtil.closeInventory(player);
						} else if (!player.hasPermission("badlion.famous") && !player.hasPermission("badlion.staff")
								&& group.players().size() > PartyHelper.MAX_PARTY_FIGHT_PLAYERS) {
							// Allow famous players to bypass the max party fight players limit
							player.sendMessage(ChatColor.RED + "You can only have a maximum of " + PartyHelper.MAX_PARTY_FIGHT_PLAYERS + " players in your party.");

							BukkitUtil.closeInventory(player);
						} else {
							// Open kit selection inventory
							PartyFightChooseKitInventory.openPartyFightChooseKitInventory(player);
						}
					} else {
						player.sendMessage(ChatColor.RED + "Must be in the lobby to use this command.");

						BukkitUtil.closeInventory(player);
					}
					break;
				case 1: // Party FFA
					if (GroupStateMachine.getInstance().getCurrentState(group) == GroupStateMachine.partyState
							&& GameState.getGroupGame(group) == null) {
						if (group.players().size() < PartyHelper.MIN_PARTY_FFA_PLAYERS) {
							player.sendMessage(ChatColor.RED + "You do not have enough players in your party (" + PartyHelper.MIN_PARTY_FFA_PLAYERS + " required).");

							BukkitUtil.closeInventory(player);
						} else if (group.players().size() > PartyHelper.MAX_PARTY_FFA_PLAYERS) {
							player.sendMessage(ChatColor.RED + "You can only have a maximum of " + PartyHelper.MAX_PARTY_FFA_PLAYERS + " players in your party.");

							BukkitUtil.closeInventory(player);
						} else {
							// Open kit selection inventory
							PartyFFAChooseKitInventory.openPartyFFAChooseKitInventory(player);
						}
					} else {
						player.sendMessage(ChatColor.RED + "Must be in the lobby to use this command.");

						BukkitUtil.closeInventory(player);
					}
					break;
				case 2: // Red Rover
					if (GroupStateMachine.getInstance().getCurrentState(group) == GroupStateMachine.partyState
							&& GameState.getGroupGame(group) == null) {
						if (group.players().size() < PartyHelper.MIN_RED_ROVER_PLAYERS) {
							player.sendMessage(ChatColor.RED + "You do not have enough players in your party.");

							BukkitUtil.closeInventory(player);
						} else {
							// Open kit selection inventory
							RedRoverChooseKitInventory.openRedRoverChooseKitInventory(player);
						}
					} else {
						player.sendMessage(ChatColor.RED + "Must be in the lobby to use this command.");

						BukkitUtil.closeInventory(player);
					}
					break;
			}
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}

}