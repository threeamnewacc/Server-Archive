package net.badlion.arenalobby.inventories.kitcreation;

import net.badlion.arenacommon.kits.Kit;
import net.badlion.arenacommon.kits.KitCommon;
import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.common.libraries.EnumCommon;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class KitMultiSaveLoadInventory {

	public static void openKitMultiSaveLoadInventory(Player sender, KitRuleSet kitRuleSet) {
		// Figure out how big our inventory needs to be

		int inventorySize = 36;

		String name = "Manage " + kitRuleSet.getName() + " kits";

		if (name.length() > 32) name = name.substring(0, 32);

		// Create smelly inventory
		SmellyInventory smellyInventory = new SmellyInventory(new KitSaveLoadScreenHandler(kitRuleSet), inventorySize, name);

		// Fill items
		KitMultiSaveLoadInventory.fillSaveLoadInventory(sender, kitRuleSet, smellyInventory.getMainInventory());

		// Open Inventory
		BukkitUtil.openInventory(sender, smellyInventory.getMainInventory());
	}

	private static void fillSaveLoadInventory(Player player, KitRuleSet kitRuleSet, Inventory inventory) {
		// Create kit info item
		inventory.setItem(35, null);

		for (int i = 0; i < 5; i++) {
			Kit kit = KitCommon.getKit(player, player.getUniqueId().toString(), kitRuleSet.getName(), i);
			if (kit == null) {
				ItemStack item = ItemStackUtil.createItem(Material.CHEST, 1, ChatColor.GREEN + "Save kit " + ChatColor.GOLD + (i + 1), (String[]) (new String[]{null, null}));
				inventory.setItem(i * 2, item);
			} else {
				ItemStack item = ItemStackUtil.createItem(Material.CHEST, 1, ChatColor.GREEN + "Save kit " + ChatColor.GOLD + (i + 1), (String[]) (new String[]{null, null}));
				inventory.setItem(i * 2, item);

				item = ItemStackUtil.createItem(Material.ENCHANTED_BOOK, 1, ChatColor.YELLOW + "Load kit " + ChatColor.GOLD + (kit.getId() + 1), (String[]) (new String[]{null, null}));
				inventory.setItem(i * 2 + 9, item);

				item = ItemStackUtil.createItem(Material.FIRE, 1, ChatColor.RED + "Delete kit " + ChatColor.GOLD + (kit.getId() + 1), (String[]) (new String[]{null, null}));
				inventory.setItem(i * 2 + 27, item);
			}
		}
	}

	private static class KitSaveLoadScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		private final KitRuleSet kitRuleSet;

		public KitSaveLoadScreenHandler(KitRuleSet kitRuleSet) {
			this.kitRuleSet = kitRuleSet;
		}

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			int row = event.getRawSlot() / 9;
			int kit = event.getRawSlot() % 9 / 2;
			switch (row) {
				case 0: // save
					KitCommon.saveKit(player, kitRuleSet, kit);
					player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "ANVIL_USE", "BLOCK_ANVIL_USE"), 1.0F, 1.0F);
					break;
				case 1: // load
					KitCommon.loadKit(player, kitRuleSet, kit);
					break;
				case 3: // delete
					if (KitCommon.getKit(player, player.getUniqueId().toString(), kitRuleSet.getName(), kit) != null) {
						player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "ANVIL_BREAK", "BLOCK_ANVIL_BREAK"), 1.0F, 1.0F);
						KitCommon.deleteKit(player, player.getUniqueId().toString(), kitRuleSet.getName(), kit);
						player.sendFormattedMessage("{0}Deleted {1} kit: {2}", ChatColor.RED, kitRuleSet.getName(), ChatColor.GOLD + String.valueOf((kit + 1)));
					}
					break;
			}
			BukkitUtil.closeInventory(player);
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {
		}

	}
}
