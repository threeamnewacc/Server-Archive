package net.badlion.arenalobby.inventories.party;

import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenacommon.rulesets.SkyWarsRuleSet;
import net.badlion.arenalobby.ArenaLobby;
import net.badlion.arenalobby.Group;
import net.badlion.arenalobby.helpers.PartyHelper;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class RedRoverChooseKitInventory {

	private static SmellyInventory smellyInventory;
	
	public static void initialize() {
		// Smelly inventory
		SmellyInventory smellyInventory = new SmellyInventory(new RedRoverChooseKitScreenHandler(), 27,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "Choose Kit");
		smellyInventory.getFakeHolder().setParentInventory(PartyEventsInventory.getSmellyInventory().getMainInventory());

		for (KitRuleSet kitRuleSet : KitRuleSet.getAllKitRuleSets()) {

			if (kitRuleSet.getClass() != KitRuleSet.customRuleSet.getClass() && !(kitRuleSet instanceof SkyWarsRuleSet)) {
				if (kitRuleSet.isEnabledInDuels()) {
					smellyInventory.getMainInventory().addItem(KitRuleSet.getKitRuleSetItem(kitRuleSet));
				}
			}
		}

		RedRoverChooseKitInventory.smellyInventory = smellyInventory;
	}

	public static void openRedRoverChooseKitInventory(final Player player) {
		BukkitUtil.openInventory(player, RedRoverChooseKitInventory.smellyInventory.getMainInventory());
	}

	private static class RedRoverChooseKitScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			Group group = ArenaLobby.getInstance().getPlayerGroup(player);
			KitRuleSet kitRuleSet = KitRuleSet.getKitRuleSet(item);

			// Always close the inventory no matter what
			PartyHelper.startPartyEvent(group, kitRuleSet, "rrb");
			BukkitUtil.closeInventory(player);
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}

}
