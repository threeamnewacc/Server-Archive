package net.badlion.potpvp.inventories.party;

import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.helpers.KitHelper;
import net.badlion.potpvp.helpers.PartyHelper;
import net.badlion.potpvp.rulesets.EventRuleSet;
import net.badlion.potpvp.rulesets.KitRuleSet;
import net.badlion.potpvp.rulesets.SkyWarsRuleSet;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class RedRoverChooseKitInventory {

	private static SmellyInventory smellyInventory;

	private static int eventKitItemSlot = -1;

	public static void initialize() {
		// Smelly inventory
		SmellyInventory smellyInventory = new SmellyInventory(new RedRoverChooseKitScreenHandler(), 27,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "Choose Kit");

		int currentSlot = 0;
		for (KitRuleSet kitRuleSet : KitRuleSet.getAllKitRuleSets()) {
			if (kitRuleSet.getClass() != KitRuleSet.customRuleSet.getClass() && !(kitRuleSet instanceof SkyWarsRuleSet)) {
				// Save the event kit item slot for future use
				if (RedRoverChooseKitInventory.eventKitItemSlot == -1
						&& kitRuleSet.getClass() == KitRuleSet.eventRuleSet.getClass()) {
					RedRoverChooseKitInventory.eventKitItemSlot = currentSlot;
				}

				if (kitRuleSet.isEnabledInDuels()) {
					smellyInventory.getMainInventory().addItem(KitRuleSet.getKitRuleSetItem(kitRuleSet));
					currentSlot++;
				}
			}
		}

		RedRoverChooseKitInventory.smellyInventory = smellyInventory;
	}

	public static void openRedRoverChooseKitInventory(final Player player) {
		// Select event kit inventory
		final Inventory selectEventKitInventory = RedRoverChooseKitInventory.smellyInventory.createInventory(RedRoverChooseKitInventory.smellyInventory.getFakeHolder(),
																											 new PartyFightChooseEventKitInventoryScreenHandler(), RedRoverChooseKitInventory.eventKitItemSlot,
																											 27, ChatColor.AQUA + ChatColor.BOLD.toString() + "Select Event Kit");

		BukkitUtil.runTaskAsync(new Runnable() {
			@Override
			public void run() {
				final List<Integer> eventKitNumbers = KitHelper.getSavedCustomKitNumbers(player, "Event");
				BukkitUtil.runTask(new Runnable() {
					@Override
					public void run() {
						for (Integer eventKitNumber : eventKitNumbers) {
							selectEventKitInventory.addItem(KitHelper.createEventKitInventoryItem(eventKitNumber));
						}

						BukkitUtil.openInventory(player, RedRoverChooseKitInventory.smellyInventory.getMainInventory());
					}
				});
			}
		});

		BukkitUtil.openInventory(player, RedRoverChooseKitInventory.smellyInventory.getMainInventory());
	}

	private static class RedRoverChooseKitScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			Group group = PotPvP.getInstance().getPlayerGroup(player);
			KitRuleSet kitRuleSet = KitRuleSet.getKitRuleSet(item);

			// Event rule set?
			if (kitRuleSet instanceof EventRuleSet) {
				BukkitUtil.openInventory(player, fakeHolder.getSubInventory(slot));
				return;
			}

			// Always close the inventory no matter what
			PartyHelper.startPartyRedRover(group, kitRuleSet, null, null);
			BukkitUtil.closeInventory(player);
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}

	private static class PartyFightChooseEventKitInventoryScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			Group group = PotPvP.getInstance().getPlayerGroup(player);
			if (event.getClick().equals(ClickType.MIDDLE)) { // Preview kit
				KitHelper.openKitPreviewInventory(fakeHolder.getSmellyInventory(), event.getView().getTopInventory(), player, item);
			} else {
				final List<ItemStack[]> contents = KitHelper.getKit(player, KitRuleSet.eventRuleSet, KitHelper.getCustomKitNumberFromItem(item));

				// Edge case
				PartyHelper.startPartyRedRover(group, KitRuleSet.eventRuleSet, contents.get(0), contents.get(1));
				BukkitUtil.closeInventory(player);
			}
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}

}
