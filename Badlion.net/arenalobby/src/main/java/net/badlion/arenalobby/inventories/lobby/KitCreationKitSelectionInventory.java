package net.badlion.arenalobby.inventories.lobby;

import net.badlion.arenacommon.kits.KitCommon;
import net.badlion.arenacommon.rulesets.CustomRuleSet;
import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenalobby.ArenaLobby;
import net.badlion.arenalobby.Group;
import net.badlion.arenalobby.GroupStateMachine;
import net.badlion.arenalobby.PotPvPPlayer;
import net.badlion.arenalobby.helpers.KitCreationHelper;
import net.badlion.arenalobby.helpers.KitInventoryHelper;
import net.badlion.arenalobby.managers.PotPvPPlayerManager;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.smellyinventory.SmellyInventory;
import net.badlion.statemachine.IllegalStateTransitionException;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;

public class KitCreationKitSelectionInventory {

	private static SmellyInventory smellyInventory;

	private static int eventKitItemSlot = -1;
	private static int customKitItemSlot = -1;

	public static void fillKitCreationSelectionInventory() {
		SmellyInventory smellyInventory = new SmellyInventory(new KitCreationSelectionScreenHandler(), 27,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "Select Kit to Customize");

		// Add all the kit rule set items
		int currentSlot = 0; // Used for custom and event kit sub-inventories
		for (KitRuleSet kitRuleSet : KitRuleSet.getAllKitRuleSets()) {

			if (kitRuleSet.isEnabledInDuels() || kitRuleSet instanceof CustomRuleSet) {
				smellyInventory.getMainInventory().addItem(KitRuleSet.getKitRuleSetItem(kitRuleSet));

				if (kitRuleSet instanceof CustomRuleSet) {

					KitCreationKitSelectionInventory.customKitItemSlot = currentSlot;

					// Create sub-inventory for event kits
					Inventory inventory = smellyInventory.createInventory(smellyInventory.getFakeHolder(), new CustomKitSelectionScreenHandler(),
							currentSlot, 27, ChatColor.AQUA + ChatColor.BOLD.toString() + "Select Custom Kit");

					// Fill with event kit items
					for (int i = 1; i < 21; i++) {
						inventory.addItem(KitInventoryHelper.createCustomKitInventoryItem(i));
					}

				}

				currentSlot++;
			}
		}

		KitCreationKitSelectionInventory.smellyInventory = smellyInventory;
	}

	public static void openKitCreationSelectionInventory(final Player player) {
		final PotPvPPlayer potPvPPlayer = PotPvPPlayerManager.getPotPvPPlayer(player.getUniqueId());

		PotPvPPlayerManager.addDebug(player, "Open kit creation selection inventory");
		BukkitUtil.openInventory(player, KitCreationKitSelectionInventory.smellyInventory.getMainInventory());


		// Load the players custom kits if they try and open up the kit creation menu, their kits should be loaded by the time they click a kit.
		if (!potPvPPlayer.isKitsLoaded()) {
			if (!potPvPPlayer.isLoadingKits()) {
				potPvPPlayer.setLoadingKits(true);
				new BukkitRunnable() {
					@Override
					public void run() {
						try {
							KitCommon.inventories.put(player.getUniqueId(), KitCommon.getAllKitContents(Gberry.getConnection(), player.getUniqueId()));
						} catch (SQLException e) {
							e.printStackTrace();
						}
						new BukkitRunnable() {
							@Override
							public void run() {
								if (player != null && player.isOnline()) {
									potPvPPlayer.setKitsLoaded(true);
									potPvPPlayer.setLoadingKits(false);
								}
							}
						}.runTask(ArenaLobby.getInstance());
					}
				}.runTaskAsynchronously(ArenaLobby.getInstance());

			}
		}
	}

	private static class KitCreationSelectionScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			Group group = ArenaLobby.getInstance().getPlayerGroup(player);

			// Kit preview check
			if (event.getClick().equals(ClickType.MIDDLE)) {
				// Preview kit
				if (slot == KitCreationKitSelectionInventory.customKitItemSlot
						|| slot == KitCreationKitSelectionInventory.eventKitItemSlot) {
					BukkitUtil.openInventory(player, fakeHolder.getSubInventory(slot));
				} else {
					KitInventoryHelper.openKitPreviewInventory(fakeHolder.getSmellyInventory(), event.getView().getTopInventory(), player, item);
				}
			} else {
				PotPvPPlayer potPvPPlayer = PotPvPPlayerManager.getPotPvPPlayer(player.getUniqueId());
				if (!potPvPPlayer.isKitsLoaded()) {
					player.sendFormattedMessage("{0}Please try again in a second, we are still loading your kits.", ChatColor.RED);
					return;
				}
				if (slot == KitCreationKitSelectionInventory.eventKitItemSlot) {
					BukkitUtil.openInventory(player, fakeHolder.getSubInventory(slot));
				} else if (slot == KitCreationKitSelectionInventory.customKitItemSlot) {
					BukkitUtil.openInventory(player, fakeHolder.getSubInventory(slot));
				} else {
					try {
						Gberry.log("KIT", player.getName());
						Gberry.log("KIT", "RAW SLOT " + slot);
						Gberry.log("KIT", "ITEM " + item.toString());
						Gberry.log("KIT", "CURRENT STATE " +
								GroupStateMachine.getInstance().getCurrentState(ArenaLobby.getInstance().getPlayerGroup(player)).getStateName());

						// Moved from KirCreationState's before() because no race conditions, was causing issues with the Kit caches
						player.getInventory().clear();
						player.getInventory().setArmorContents(new ItemStack[4]);

						// Create a kit creator object that handles all the internals
						new KitCreationHelper.KitCreator(player, KitRuleSet.getKitRuleSet(item));

						// Transfer states to kitCreationState
						GroupStateMachine.lobbyState.transition(GroupStateMachine.kitCreationState, group);
					} catch (IllegalStateTransitionException e) {
						ArenaLobby.getInstance().somethingBroke(player, group);
					}
				}
			}
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}

	private static class CustomKitSelectionScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			Group group = ArenaLobby.getInstance().getPlayerGroup(player);

			// Figure out what custom kits we're handling
			KitRuleSet kitRuleSet = KitRuleSet.getKitRuleSet(item);

			if (event.getClick().equals(ClickType.MIDDLE)) {
				// Preview kit
				KitInventoryHelper.openKitPreviewInventory(fakeHolder.getSmellyInventory(), event.getView().getTopInventory(), player, item);
			} else {
				try {
					// Create a kit creator object that handles all the internals
					new KitCreationHelper.KitCreator(player, kitRuleSet, KitInventoryHelper.getCustomKitNumberFromItem(item) - 1);

					// Transfer states to kitCreationState
					GroupStateMachine.lobbyState.transition(GroupStateMachine.kitCreationState, group);
				} catch (IllegalStateTransitionException e) {
					ArenaLobby.getInstance().somethingBroke(player, group);
				}
			}
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}

}