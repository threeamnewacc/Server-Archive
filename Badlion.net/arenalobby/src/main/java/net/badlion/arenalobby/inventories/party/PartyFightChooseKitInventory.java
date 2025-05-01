package net.badlion.arenalobby.inventories.party;

import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenacommon.rulesets.SkyWarsRuleSet;
import net.badlion.arenalobby.ArenaLobby;
import net.badlion.arenalobby.Group;
import net.badlion.arenalobby.helpers.PartyHelper;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class PartyFightChooseKitInventory {

	private static SmellyInventory smellyInventory;


	private static int eventKitItemSlot = -1;

	public static void initialize() {
		// Smelly inventory
		SmellyInventory smellyInventory = new SmellyInventory(new PartyFightChooseKitScreenHandler(), 27,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "Choose Kit");
		smellyInventory.getFakeHolder().setParentInventory(PartyEventsInventory.getSmellyInventory().getMainInventory());

		int currentSlot = 0;
		for (KitRuleSet kitRuleSet : KitRuleSet.getAllKitRuleSets()) {
			if (kitRuleSet.getClass() != KitRuleSet.customRuleSet.getClass() && !(kitRuleSet instanceof SkyWarsRuleSet)) {
				if (kitRuleSet.isEnabledInDuels()) {
					smellyInventory.getMainInventory().addItem(KitRuleSet.getKitRuleSetItem(kitRuleSet));
					currentSlot++;
				}
			}
		}

		PartyFightChooseKitInventory.smellyInventory = smellyInventory;
	}

	public static void openPartyFightChooseKitInventory(final Player player) {
		BukkitUtil.openInventory(player, PartyFightChooseKitInventory.smellyInventory.getMainInventory());
	}

	public static void openPartyFightFriendlyFireInventory(final Player player, KitRuleSet kitRuleSet) {
		SmellyInventory friendlyFire = new SmellyInventory(new PartyFightChooseFriendlyFireInventoryScreenHandler(kitRuleSet), 9,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "Enable Friendly Fire?");
		smellyInventory.getFakeHolder().setParentInventory(PartyFightChooseKitInventory.smellyInventory.getMainInventory());

		ItemStack enable = ItemStackUtil.createItem(Material.INK_SACK, 1, (short) 10, ChatColor.GREEN + "Enable Friendly Fire");
		ItemStack disable = ItemStackUtil.createItem(Material.INK_SACK, 1, (short) 14, ChatColor.RED + "Disable Friendly Fire");

		friendlyFire.getMainInventory().setItem(3, enable);
		friendlyFire.getMainInventory().setItem(5, disable);

		BukkitUtil.openInventory(player, friendlyFire.getMainInventory());
	}


	private static class PartyFightChooseKitScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			KitRuleSet kitRuleSet = KitRuleSet.getKitRuleSet(item);
			openPartyFightFriendlyFireInventory(player, kitRuleSet);
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}

	private static class PartyFightChooseFriendlyFireInventoryScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		KitRuleSet kitRuleSet;

		public PartyFightChooseFriendlyFireInventoryScreenHandler(KitRuleSet kitRuleSet) {
			this.kitRuleSet = kitRuleSet;
		}

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			Group group = ArenaLobby.getInstance().getPlayerGroup(player);

			switch (slot) {
				case 3:
					PartyHelper.startPartyEvent(group, this.kitRuleSet, "team", true);
					break;
				case 5:
					PartyHelper.startPartyEvent(group, this.kitRuleSet, "team", false);
					break;
			}

			BukkitUtil.closeInventory(player);
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}

}
