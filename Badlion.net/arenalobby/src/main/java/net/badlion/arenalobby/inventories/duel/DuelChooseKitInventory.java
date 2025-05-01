package net.badlion.arenalobby.inventories.duel;

import net.badlion.arenacommon.rulesets.CustomRuleSet;
import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenacommon.rulesets.SkyWarsRuleSet;
import net.badlion.arenalobby.ArenaLobby;
import net.badlion.arenalobby.Group;
import net.badlion.arenalobby.GroupStateMachine;
import net.badlion.arenalobby.helpers.DuelHelper;
import net.badlion.arenalobby.managers.DuelRequestManager;
import net.badlion.arenalobby.managers.PotPvPPlayerManager;
import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.managers.MCPManager;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public class DuelChooseKitInventory {

	private static SmellyInventory duelInventory;
	private static SmellyInventory duelPartyInventory;
	private static SmellyInventory rrPartyInventory;
	private static SmellyInventory bestOfInventory;

	private static Set<UUID> switchingInventories = new HashSet<>();

	public static void fillDuelChooseKitInventories() {
		// Duel inventory
		SmellyInventory smellyInventory = new SmellyInventory(new DuelChooseKitScreenHandler(), 27,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "Duel - Choose Kit");

		for (KitRuleSet kitRuleSet : KitRuleSet.getAllKitRuleSets()) {

			if (kitRuleSet.isEnabledInDuels()) {
				smellyInventory.getMainInventory().addItem(kitRuleSet.getKitItem());
			}
		}

		DuelChooseKitInventory.duelInventory = smellyInventory;

		// Duel party inventory
		smellyInventory = new SmellyInventory(new DuelChooseKitScreenHandler(), 27,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "Duel - Choose Kit");

		for (KitRuleSet kitRuleSet : KitRuleSet.getAllKitRuleSets()) {
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
			if (kitRuleSet instanceof CustomRuleSet) continue;

			if (kitRuleSet.isEnabledInDuels()) {
				smellyInventory.getMainInventory().addItem(kitRuleSet.getKitItem());
			}
		}

		smellyInventory.getMainInventory().setItem(18, ItemStackUtil.createItem(Material.REDSTONE_BLOCK, ChatColor.GREEN + "Switch to Duel"));

		DuelChooseKitInventory.rrPartyInventory = smellyInventory;

		// Best of selector inventory
		smellyInventory = new SmellyInventory(new DuelChooseBestOfScreenHandler(), 9,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "How many matches?");

		ItemStack item = ItemStackUtil.createItem(Material.DIAMOND_SWORD, 1, (short) 0, "Best of 1", (String[]) (new String[]{null, null}));
		smellyInventory.getMainInventory().setItem(0, item);
		item = ItemStackUtil.createItem(Material.DIAMOND_SWORD, 3, (short) 0, "Best of 3", (String[]) (new String[]{null, null}));
		smellyInventory.getMainInventory().setItem(4, item);
		item = ItemStackUtil.createItem(Material.DIAMOND_SWORD, 5, (short) 0, "Best of 5", (String[]) (new String[]{null, null}));
		smellyInventory.getMainInventory().setItem(8, item);

		DuelChooseKitInventory.bestOfInventory = smellyInventory;
	}

	public static void openDuelChooseKitInventory(Player player) {
		Group group = ArenaLobby.getInstance().getPlayerGroup(player);

		PotPvPPlayerManager.addDebug(player, "Open duel choose kit inventory");

		BukkitUtil.openInventory(player, DuelChooseKitInventory.duelInventory.getMainInventory());
	}

	private static class DuelChooseKitScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, final Player player, InventoryClickEvent event, ItemStack item, int slot) {
			final Group group = ArenaLobby.getInstance().getPlayerGroup(player);
			final DuelHelper.DuelCreator duelCreator = DuelRequestManager.getDuelCreator(player.getUniqueId());

			// Are they switching between duels/RR?
			// Note: Impossible to be the 1v1 inventory
			if (slot == 18) {
				// Open correct inventory
				if (duelCreator.isRedRover()) {
					BukkitUtil.openInventory(player, DuelChooseKitInventory.duelPartyInventory.getMainInventory());
				} else {
					// Check if they have enough players in their party
					if (group.players().size() < 2) {
						player.sendFormattedMessage("{0}You need one more person in your party to play Red Rover!", ChatColor.RED);
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
				player.sendFormattedMessage("{0}Could not find player. Try again.", ChatColor.RED);

				List<String> lines = GroupStateMachine.getInstance().debugTransitionsForElement(group);
				for (String line : lines) {
					Gberry.log("LAG", line);
				}

				BukkitUtil.closeInventory(player);
				return;
			}


			KitRuleSet kitRuleSet = KitRuleSet.getKitRuleSet(item);

			if (kitRuleSet == null) {
				Gberry.log("DUEL", "null kitruleset detected, backing out");
				PotPvPPlayerManager.addDebug(player, "Null kitruleset for duel detected, backing out");
				BukkitUtil.closeInventory(player);
				//GroupStateMachine.duelRequestState.removeDuelCreator(group);
				return;
			}

			// Red Rover check
			if (duelCreator.isRedRover() && kitRuleSet instanceof SkyWarsRuleSet) {
				PotPvPPlayerManager.addDebug(player, "You cannot use the SkyWars kit for Red Rover!");
				player.sendFormattedMessage("{0}You cannot use the SkyWars kit for Red Rover!", ChatColor.RED);
				//GroupStateMachine.duelRequestState.removeDuelCreator(group);
				BukkitUtil.closeInventory(player);
				return;
			}

			// Save the kit rule set they want to duel with
			duelCreator.setKitRuleSet(kitRuleSet);

			// Open Best of selector
			BukkitUtil.openInventory(player, DuelChooseKitInventory.bestOfInventory.getMainInventory());


			//GroupStateMachine.duelRequestState.removeDuelCreator(group);
			DuelChooseKitInventory.switchingInventories.remove(player.getUniqueId());
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {
			Group group = ArenaLobby.getInstance().getPlayerGroup(player);
			Gberry.log("DUEL", "Duel choose kit inventory closed, attempting to remove duel creator");

			// Don't leak memory - if kit isn't selected, they closed inventory themselves
			DuelHelper.DuelCreator duelCreator = DuelRequestManager.getDuelCreator(player.getUniqueId());
			if (duelCreator != null && duelCreator.getKitRuleSet() == null
					&& !DuelChooseKitInventory.switchingInventories.remove(player.getUniqueId())) {
				PotPvPPlayerManager.addDebug(player, "ESC Closed duel choose kit inventory");

				//GroupStateMachine.duelRequestState.removeDuelCreator(group);
			}
		}

	}

	private static class DuelChooseBestOfScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, final Player player, final InventoryClickEvent event, ItemStack item, int slot) {
			final Group group = ArenaLobby.getInstance().getPlayerGroup(player);
			final DuelHelper.DuelCreator duelCreator = DuelRequestManager.getDuelCreator(player.getUniqueId());

			if (duelCreator == null) {
				player.sendFormattedMessage("{0}That player is no longer online.", ChatColor.RED);

				BukkitUtil.closeInventory(player);
				return;
			}

			if (duelCreator.getKitRuleSet() == null) {
				Gberry.log("DUEL", "null kitruleset detected, backing out");
				PotPvPPlayerManager.addDebug(player, "Null kitruleset for duel detected, backing out");
				BukkitUtil.closeInventory(player);
				//GroupStateMachine.duelRequestState.removeDuelCreator(group);
				return;
			}

			if (event.getRawSlot() != 0 && event.getRawSlot() != 4 && event.getRawSlot() != 8) {
				return;
			}
			// Close inventory for player
			BukkitUtil.closeInventory(player);

			// Send api request /arena-duel-request/

			new BukkitRunnable() {
				@Override
				public void run() {
					JSONObject data = new JSONObject();
					data.put("sender_uuid", player.getUniqueId().toString());
					data.put("sender_name", player.getName());
					data.put("target_uuid", duelCreator.getReceiverId().toString());
					data.put("ladder", duelCreator.getKitRuleSet().getName());
					data.put("server_name", Gberry.serverName);
					switch (event.getRawSlot()) {
						case 0:
							duelCreator.setBestOf(1);
							// dont send for best of 1
							break;
						case 4:
							duelCreator.setBestOf(3);
							data.put("best_of", "3");
							break;
						case 8:
							duelCreator.setBestOf(5);
							data.put("best_of", "5");
					}
					try {
						JSONObject response = Gberry.contactMCP("arena-duel-request", data);
						ArenaLobby.getInstance().getLogger().log(Level.INFO, "Sending duel request: " + data);
						ArenaLobby.getInstance().getLogger().log(Level.INFO, "Getting duel response " + response);

						if (!response.equals(MCPManager.successResponse)) {
							player.sendMessage(DuelHelper.duelRequestErrorString((String) response.get("error")));
							//error
							return;
						}

						new BukkitRunnable() {
							@Override
							public void run() {
								duelCreator.startDuelTimeoutTask();

								// Ok now send them the message

								PotPvPPlayerManager.addDebug(player, "Sent duel request");
								group.sendMessage(ChatColor.BLUE + "Duel request sent to " + duelCreator.getReceiverName());


								// Cleanup
								DuelChooseKitInventory.switchingInventories.remove(player.getUniqueId());
							}
						}.runTask(ArenaLobby.getInstance());
						return;

					} catch (HTTPRequestFailException e) {
						player.sendMessage(ChatColor.RED + "Unable to send duel request, try again later.");
						e.printStackTrace();
					}

				}
			}.runTaskAsynchronously(ArenaLobby.getInstance());


			PotPvPPlayerManager.addDebug(player, "Could not send duel request");

			// Cleanup
			BukkitUtil.closeInventory(player);
			//GroupStateMachine.duelRequestState.removeDuelCreator(group);
			DuelChooseKitInventory.switchingInventories.remove(player.getUniqueId());
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {
			Group group = ArenaLobby.getInstance().getPlayerGroup(player);
			Gberry.log("DUEL", "Duel choose kit inventory closed, attempting to remove duel creator");

			// Don't leak memory - if kit isn't selected, they closed inventory themselves
			DuelHelper.DuelCreator duelCreator = DuelRequestManager.getDuelCreator(player.getUniqueId());
			if (duelCreator != null && duelCreator.getKitRuleSet() == null
					&& !DuelChooseKitInventory.switchingInventories.remove(player.getUniqueId())) {
				PotPvPPlayerManager.addDebug(player, "ESC Closed duel choose kit inventory");

				//GroupStateMachine.duelRequestState.removeDuelCreator(group);
			}
		}

	}

}
