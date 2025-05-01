package net.badlion.potpvp.inventories.duel;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.helpers.DuelHelper;
import net.badlion.potpvp.managers.PotPvPPlayerManager;
import net.badlion.potpvp.rulesets.EventRuleSet;
import net.badlion.potpvp.rulesets.KitRuleSet;
import net.badlion.potpvp.rulesets.SkyWarsRuleSet;
import net.badlion.smellyinventory.SmellyInventory;
import net.badlion.statemachine.IllegalStateTransitionException;
import net.badlion.statemachine.State;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class DuelChooseKitInventory {

	private static SmellyInventory duelInventory;
	private static SmellyInventory duelPartyInventory;
	private static SmellyInventory rrPartyInventory;

	private static Set<UUID> switchingInventories = new HashSet<>();

	public static void fillDuelChooseKitInventories() {
		// Duel inventory
		SmellyInventory smellyInventory = new SmellyInventory(new DuelChooseKitScreenHandler(), 27,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "Duel - Choose Kit");

		for (KitRuleSet kitRuleSet : KitRuleSet.getAllKitRuleSets()) {
			// Don't allow duels with event kits
			if (kitRuleSet instanceof EventRuleSet) continue;

			if (kitRuleSet.isEnabledInDuels()) {
				smellyInventory.getMainInventory().addItem(kitRuleSet.getKitItem());
			}
		}

		DuelChooseKitInventory.duelInventory = smellyInventory;

		// Duel party inventory
		smellyInventory = new SmellyInventory(new DuelChooseKitScreenHandler(), 27,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "Duel - Choose Kit");

		for (KitRuleSet kitRuleSet : KitRuleSet.getAllKitRuleSets()) {
			// Don't allow duels with event kits
			if (kitRuleSet instanceof EventRuleSet) continue;

			if (kitRuleSet.isEnabledInDuels()) {
				smellyInventory.getMainInventory().addItem(kitRuleSet.getKitItem());
			}
		}

		smellyInventory.getMainInventory().setItem(18, ItemStackUtil.createItem(Material.EMERALD_BLOCK, ChatColor.GREEN + "Switch to RR"));

		DuelChooseKitInventory.duelPartyInventory = smellyInventory;

		// RR party inventory
		smellyInventory = new SmellyInventory(new DuelChooseKitScreenHandler(), 27,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "RR - Choose Kit");

		for (KitRuleSet kitRuleSet : KitRuleSet.getAllKitRuleSets()) {
			// Don't allow duels with event kits
			if (kitRuleSet instanceof EventRuleSet) continue;

			if (kitRuleSet.isEnabledInDuels()) {
				smellyInventory.getMainInventory().addItem(kitRuleSet.getKitItem());
			}
		}

		smellyInventory.getMainInventory().setItem(18, ItemStackUtil.createItem(Material.REDSTONE_BLOCK, ChatColor.GREEN + "Switch to Duel"));

		DuelChooseKitInventory.rrPartyInventory = smellyInventory;
	}

	public static void openDuelChooseKitInventory(Player player) {
		Group group = PotPvP.getInstance().getPlayerGroup(player);

		PotPvPPlayerManager.addDebug(player, "Open duel choose kit inventory");

		if (group.isParty()) {
			BukkitUtil.openInventory(player, DuelChooseKitInventory.duelPartyInventory.getMainInventory());
		} else {
			BukkitUtil.openInventory(player, DuelChooseKitInventory.duelInventory.getMainInventory());
		}
	}

	private static class DuelChooseKitScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			Group group = PotPvP.getInstance().getPlayerGroup(player);
			DuelHelper.DuelCreator duelCreator = GroupStateMachine.duelRequestState.getDuelCreator(group);

			// Are they switching between duels/RR?
			// Note: Impossible to be the 1v1 inventory
			if (slot == 18) {
				// Open correct inventory
				if (duelCreator.isRedRover()) {
					BukkitUtil.openInventory(player, DuelChooseKitInventory.duelPartyInventory.getMainInventory());
				} else {
					// Check if they have enough players in their party
					if (group.players().size() < 2) {
						player.sendMessage(ChatColor.RED + "You need one more person in your party to play Red Rover!");
						return;
					}

					BukkitUtil.openInventory(player, DuelChooseKitInventory.rrPartyInventory.getMainInventory());
				}

				duelCreator.setRedRover(!duelCreator.isRedRover());

				DuelChooseKitInventory.switchingInventories.add(player.getUniqueId());
				return;
			}

			// 8/7/2015 I think I fixed this - Smelly
			// Somehow people are getting here...just close the inventory
			if (duelCreator == null) {
				// Make sure this always logs
				Bukkit.getLogger().severe("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ DUEL CREATOR NULL IN DUELCHOOSEKITINVENTORY, state: "
						+ GroupStateMachine.getInstance().getCurrentState(group));
				player.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + "A bug has occurred! Please make a bug report on the forums with details of what you did to get this message!");

				List<String> lines = GroupStateMachine.getInstance().debugTransitionsForElement(group);
				for (String line : lines) {
					Gberry.log("LAG", line);
				}

				BukkitUtil.closeInventory(player);
				return;
			}

			// Check party invites here again because of sneaky fuckers
			// Is the player trying to invite someone to their party?
			if (GroupStateMachine.partyRequestState.containsInvitingPlayer(player)) {
				BukkitUtil.closeInventory(player);
				GroupStateMachine.duelRequestState.removeDuelCreator(group);
				player.sendMessage(ChatColor.RED + "Wait until the person you're inviting to your party accepts/declines.");
				return;
			}

			// Is the other player trying to invite their party?
			if (GroupStateMachine.partyRequestState.containsInvitingPlayer(duelCreator.getReceiver().getLeader())) {
				BukkitUtil.closeInventory(player);
				GroupStateMachine.duelRequestState.removeDuelCreator(group);
				player.sendMessage(ChatColor.RED + "Other player is currently inviting someone to their party.");
				return;
			}

			KitRuleSet kitRuleSet = KitRuleSet.getKitRuleSet(item);

			if (kitRuleSet == null) {
				Gberry.log("DUEL", "null kitruleset detected, backing out");
				PotPvPPlayerManager.addDebug(player, "Null kitruleset for duel detected, backing out");
				BukkitUtil.closeInventory(player);
				GroupStateMachine.duelRequestState.removeDuelCreator(group);
				return;
			}

			// Red Rover check
			if (duelCreator.isRedRover() && kitRuleSet instanceof SkyWarsRuleSet) {
				PotPvPPlayerManager.addDebug(player, "You cannot use the SkyWars kit for Red Rover!");

				player.sendMessage(ChatColor.RED + "You cannot use the SkyWars kit for Red Rover!");
				GroupStateMachine.duelRequestState.removeDuelCreator(group);
				BukkitUtil.closeInventory(player);
				return;
			}

			// Save the kit rule set they want to duel with
			duelCreator.setKitRuleSet(kitRuleSet);

			// Check to see if they can both move
			Group receiver = duelCreator.getReceiver();
			State<Group> currentState = GroupStateMachine.getInstance().getCurrentState(group);
			State<Group> otherState = GroupStateMachine.getInstance().getCurrentState(receiver);

			// They logged off
			if (otherState == null) {
				PotPvPPlayerManager.addDebug(player, "Opponent logged off. Cancelling duel.");

				player.sendMessage(ChatColor.RED + "Opponent logged off. Cancelling duel.");
				BukkitUtil.closeInventory(player);
				GroupStateMachine.duelRequestState.removeDuelCreator(group);
				return;
			}

			if (currentState.isStateTransitionValid(GroupStateMachine.duelRequestState)) {
				if (otherState.isStateTransitionValid(GroupStateMachine.duelRequestState)) {
					try {
						// Close inventory for player
						BukkitUtil.closeInventory(player);

						// Open duel inventory for group receiving request
						DuelRequestInventory.openDuelRequestInventory(player, group, receiver, duelCreator.isRedRover());

						duelCreator.startDuelTimeoutTask();

						currentState.transition(GroupStateMachine.duelRequestState, group);
						currentState.transition(GroupStateMachine.duelRequestState, receiver);

						// Ok now send them the message
						if (group.isParty()) {
							PotPvPPlayerManager.addDebug(player, "Sent party duel request");

							group.sendMessageWithoutLeader(ChatColor.BLUE + "Duel request sent to " + receiver.getLeader().getName() + " by " + player.getName());
							group.sendLeaderMessage(ChatColor.BLUE + "Duel request sent to " + receiver.getLeader().getName());
							receiver.sendMessageWithoutLeader(ChatColor.BLUE + "Duel request from " + player.getName() + ". Wait for leader to accept/deny.");
						} else {
							PotPvPPlayerManager.addDebug(player, "Sent duel request");

							group.sendMessage(ChatColor.BLUE + "Duel request sent to " + receiver.getLeader().getName());
						}

						// Cleanup
						DuelChooseKitInventory.switchingInventories.remove(player.getUniqueId());

						return;
					} catch (IllegalStateTransitionException e) {
						// Something weird happened
						PotPvP.getInstance().somethingBroke(player, group, receiver);
					}
				} else {
					player.sendMessage(ChatColor.RED + "Player cannot accept a duel request at the moment because " + otherState.description());
				}
			} else {
				player.sendMessage(ChatColor.RED + "You cannot send a duel request at the moment because " + currentState.description());
			}

			PotPvPPlayerManager.addDebug(player, "Could not send duel request");

			// Cleanup
			BukkitUtil.closeInventory(player);
			GroupStateMachine.duelRequestState.removeDuelCreator(group);
			DuelChooseKitInventory.switchingInventories.remove(player.getUniqueId());
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {
			Group group = PotPvP.getInstance().getPlayerGroup(player);
			Gberry.log("DUEL", "Duel choose kit inventory closed, attempting to remove duel creator");

			// Don't leak memory - if kit isn't selected, they closed inventory themselves
			DuelHelper.DuelCreator duelCreator = GroupStateMachine.duelRequestState.getDuelCreator(group);
			if (duelCreator != null && duelCreator.getKitRuleSet() == null
					&& !DuelChooseKitInventory.switchingInventories.remove(player.getUniqueId())) {
				PotPvPPlayerManager.addDebug(player, "ESC Closed duel choose kit inventory");

				GroupStateMachine.duelRequestState.removeDuelCreator(group);
			}
		}

	}

}
