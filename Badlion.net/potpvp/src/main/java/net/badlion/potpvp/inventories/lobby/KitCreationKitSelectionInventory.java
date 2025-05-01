package net.badlion.potpvp.inventories.lobby;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.helpers.KitCreationHelper;
import net.badlion.potpvp.helpers.KitHelper;
import net.badlion.potpvp.managers.PotPvPPlayerManager;
import net.badlion.potpvp.rulesets.CustomRuleSet;
import net.badlion.potpvp.rulesets.EventRuleSet;
import net.badlion.potpvp.rulesets.KitRuleSet;
import net.badlion.potpvp.rulesets.TDMRuleSet;
import net.badlion.smellyinventory.SmellyInventory;
import net.badlion.statemachine.IllegalStateTransitionException;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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
			if (kitRuleSet.isEnabledInDuels() || kitRuleSet instanceof CustomRuleSet || kitRuleSet instanceof TDMRuleSet) {
				smellyInventory.getMainInventory().addItem(KitRuleSet.getKitRuleSetItem(kitRuleSet));

				if (kitRuleSet instanceof CustomRuleSet) {
					if (kitRuleSet instanceof EventRuleSet) {
						KitCreationKitSelectionInventory.eventKitItemSlot = currentSlot;

						// Create sub-inventory for event kits
						Inventory inventory = smellyInventory.createInventory(smellyInventory.getFakeHolder(), new CustomKitSelectionScreenHandler(),
								currentSlot, 27, ChatColor.AQUA + ChatColor.BOLD.toString() + "Select Event Kit");

						// Fill with event kit items
						for (int i = 1; i < 21; i++) {
							inventory.addItem(KitHelper.createEventKitInventoryItem(i));
						}
					} else {
						KitCreationKitSelectionInventory.customKitItemSlot = currentSlot;

						// Create sub-inventory for event kits
						Inventory inventory = smellyInventory.createInventory(smellyInventory.getFakeHolder(), new CustomKitSelectionScreenHandler(),
								currentSlot, 27, ChatColor.AQUA + ChatColor.BOLD.toString() + "Select Custom Kit");

						// Fill with event kit items
						for (int i = 1; i < 21; i++) {
							inventory.addItem(KitHelper.createCustomKitInventoryItem(i));
						}
					}
				}

				currentSlot++;
			}
		}

		KitCreationKitSelectionInventory.smellyInventory = smellyInventory;
	}

	public static void openKitCreationSelectionInventory(Player player) {
		PotPvPPlayerManager.addDebug(player, "Open kit creation selection inventory");

		BukkitUtil.openInventory(player, KitCreationKitSelectionInventory.smellyInventory.getMainInventory());
	}

	private static class KitCreationSelectionScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			Group group = PotPvP.getInstance().getPlayerGroup(player);

			// Kit preview check
			if (event.getClick().equals(ClickType.MIDDLE)) {
				// Preview kit
				if (slot == KitCreationKitSelectionInventory.customKitItemSlot
						 || slot == KitCreationKitSelectionInventory.eventKitItemSlot) {
					BukkitUtil.openInventory(player, fakeHolder.getSubInventory(slot));
				} else {
					KitHelper.openKitPreviewInventory(fakeHolder.getSmellyInventory(), event.getView().getTopInventory(), player, item);
				}
			} else {
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
								GroupStateMachine.getInstance().getCurrentState(PotPvP.getInstance().getPlayerGroup(player)).getStateName());

                        // Moved from KirCreationState's before() because no race conditions, was causing issues with the Kit caches
                        player.getInventory().clear();
                        player.getInventory().setArmorContents(new ItemStack[4]);

						// Create a kit creator object that handles all the internals
						new KitCreationHelper.KitCreator(player, KitRuleSet.getKitRuleSet(item));

						// Transfer states to kitCreationState
						GroupStateMachine.lobbyState.transition(GroupStateMachine.kitCreationState, group);
					} catch (IllegalStateTransitionException e) {
						PotPvP.getInstance().somethingBroke(player, group);
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
			Group group = PotPvP.getInstance().getPlayerGroup(player);

			// Figure out what custom kits we're handling
			KitRuleSet kitRuleSet = KitRuleSet.getKitRuleSet(item);

			if (event.getClick().equals(ClickType.MIDDLE)) {
				// Preview kit
				KitHelper.openKitPreviewInventory(fakeHolder.getSmellyInventory(), event.getView().getTopInventory(), player, item);
			} else {
				try {
					// Create a kit creator object that handles all the internals
					new KitCreationHelper.KitCreator(player, kitRuleSet, KitHelper.getCustomKitNumberFromItem(item));

					// Transfer states to kitCreationState
					GroupStateMachine.lobbyState.transition(GroupStateMachine.kitCreationState, group);
				} catch (IllegalStateTransitionException e) {
					PotPvP.getInstance().somethingBroke(player, group);
				}
			}
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}

}