package net.badlion.potpvp.inventories.party;

import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.helpers.KitHelper;
import net.badlion.potpvp.helpers.PartyHelper;
import net.badlion.smellyinventory.SmellyInventory;
import net.badlion.potpvp.managers.PotPvPPlayerManager;
import net.badlion.potpvp.rulesets.EventRuleSet;
import net.badlion.potpvp.rulesets.KitRuleSet;
import net.badlion.potpvp.rulesets.SkyWarsRuleSet;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PartyFightChooseKitInventory {

	private static SmellyInventory smellyInventory;

	private static int eventKitItemSlot = -1;

	public static void initialize() {
		// Smelly inventory
		SmellyInventory smellyInventory = new SmellyInventory(new PartyFightChooseKitScreenHandler(), 27,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "Choose Kit");

		int currentSlot = 0;
		for (KitRuleSet kitRuleSet : KitRuleSet.getAllKitRuleSets()) {
			if (kitRuleSet.getClass() != KitRuleSet.customRuleSet.getClass() && !(kitRuleSet instanceof SkyWarsRuleSet)) {
				// Save the event kit item slot for future use
				if (PartyFightChooseKitInventory.eventKitItemSlot == -1
						&& kitRuleSet.getClass() == KitRuleSet.eventRuleSet.getClass()) {
					PartyFightChooseKitInventory.eventKitItemSlot = currentSlot;
				}

				if (kitRuleSet.isEnabledInDuels()) {
					smellyInventory.getMainInventory().addItem(KitRuleSet.getKitRuleSetItem(kitRuleSet));
					currentSlot++;
				}
			}
		}

		PartyFightChooseKitInventory.smellyInventory = smellyInventory;
	}

	public static void openPartyFightChooseKitInventory(final Player player) {
		// Select event kit inventory
		final Inventory selectEventKitInventory = PartyFightChooseKitInventory.smellyInventory.createInventory(PartyFightChooseKitInventory.smellyInventory.getFakeHolder(),
				new PartyFightChooseEventKitInventoryScreenHandler(), PartyFightChooseKitInventory.eventKitItemSlot,
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

						PotPvPPlayerManager.addDebug(player, "Open party fight choose kit inventory");

						BukkitUtil.openInventory(player, PartyFightChooseKitInventory.smellyInventory.getMainInventory());
					}
				});
			}
		});
	}

	private static class PartyFightChooseKitScreenHandler implements SmellyInventory.SmellyInventoryHandler {

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
			BukkitUtil.closeInventory(player);

			if (group.players().size() > 1) {
				PartyHelper.startPartyFight(group, kitRuleSet, null, null);
			} else {
				player.sendMessage(ChatColor.RED + "Not enough players in your party.");
			}
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

				PartyHelper.startPartyFight(group, KitRuleSet.eventRuleSet, contents.get(0), contents.get(1));

				BukkitUtil.closeInventory(player);
			}
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}

}
