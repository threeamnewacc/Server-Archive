package net.badlion.arenapvp.inventory;

import net.badlion.arenacommon.kits.Kit;
import net.badlion.arenacommon.kits.KitCommon;
import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class KitPreviewInventory {

	public static void openKitPreviewInventory(final Player sender, final KitRuleSet kitRuleSet, final SmellyInventory smellyInventory, Inventory currentInventory) {
		// Figure out how big our inventory needs to be
		SmellyInventory.FakeHolder fakeHolder;
		String name = ChatColor.AQUA + ChatColor.BOLD.toString() + kitRuleSet.getName() + " Kit Preview";
		if (name.length() > 32) name = name.substring(0, 32);

		final boolean hasSubMenu;
		final SmellyInventory newSmellyInventory;
		if (smellyInventory != null && currentInventory != null) {
			newSmellyInventory = new SmellyInventory(new KitPreviewScreenHandler(kitRuleSet), 54, name);
			newSmellyInventory.getFakeHolder().setParentInventory(smellyInventory.getMainInventory());
			newSmellyInventory.getMainInventory().setItem(53, SmellyInventory.getBackInventoryItem());
			hasSubMenu = true;
			KitPreviewInventory.fillPreviewInventory(sender, kitRuleSet, newSmellyInventory.getMainInventory(), -1, hasSubMenu);
			BukkitUtil.openInventory(sender, newSmellyInventory.getMainInventory());
		} else {
			hasSubMenu = false;
			newSmellyInventory = new SmellyInventory(new KitPreviewScreenHandler(kitRuleSet), 54, name);
			KitPreviewInventory.fillPreviewInventory(sender, kitRuleSet, newSmellyInventory.getMainInventory(), -1, hasSubMenu);
			BukkitUtil.openInventory(sender, newSmellyInventory.getMainInventory());
		}

		KitPreviewInventory.fillPreviewInventory(sender, kitRuleSet, newSmellyInventory.getMainInventory(), -1, hasSubMenu);
	}

	private static void fillPreviewInventory(Player player, KitRuleSet kitRuleSet, Inventory inventory, int kitId, boolean hasSubMenu) {
		// Create kit info item
		//Load Default Kit
		inventory.clear();
		if (kitId == -1) {
			KitCommon.fillInventoryWithContents(inventory, kitRuleSet.getDefaultArmorKit(), kitRuleSet.getDefaultInventoryKit());
		} else {
			Kit kit = KitCommon.getKit(player, player.getUniqueId().toString(), kitRuleSet.getName(), kitId);
			if (kit != null) {
				KitCommon.fillInventoryWithContents(inventory, kit.getArmorItems(), kit.getInventoryItems());
			} else {
				KitCommon.fillInventoryWithContents(inventory, kitRuleSet.getDefaultArmorKit(), kitRuleSet.getDefaultInventoryKit());
			}
		}
		ItemStack defaultItem = ItemStackUtil.createItem(Material.ENCHANTED_BOOK, 1, ChatColor.GREEN + "Preview default kit", (String[]) (new String[]{null, null}));
		inventory.setItem(45, defaultItem);
		for (int i = 0; i < 5; i++) {
			Kit kit = KitCommon.getKit(player, player.getUniqueId().toString(), kitRuleSet.getName(), i);
			if (kit != null) {
				ItemStack item = ItemStackUtil.createItem(Material.ENCHANTED_BOOK, 1, ChatColor.GREEN + "Preview kit " + ChatColor.GOLD + (kit.getId() + 1), (String[]) (new String[]{null, null}));
				inventory.setItem(47 + i, item);
			}
		}
		if(hasSubMenu) {
			inventory.setItem(53, SmellyInventory.getBackInventoryItem());
		}else{
			inventory.setItem(53, SmellyInventory.getCloseInventoryItem());
		}
	}

	private static class KitPreviewScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		private final KitRuleSet kitRuleSet;

		public KitPreviewScreenHandler(KitRuleSet kitRuleSet) {
			this.kitRuleSet = kitRuleSet;
		}

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			if (event.getRawSlot() == 45) {
				fillPreviewInventory(player, kitRuleSet, fakeHolder.getSmellyInventory().getMainInventory(), -1, fakeHolder.getParentInventory() != null);
				return;
			}
			if (event.getRawSlot() >= 45 && event.getRawSlot() <= 51) {
				int kitId = (event.getRawSlot() - 51) + 4;
				Kit kit = KitCommon.getKit(player, player.getUniqueId().toString(), kitRuleSet.getName(), kitId);
				if (kit != null) {
					fillPreviewInventory(player, kitRuleSet, fakeHolder.getSmellyInventory().getMainInventory(), kitId, fakeHolder.getParentInventory() != null);
				} else {
					player.sendFormattedMessage("{0}Could not load kit.", ChatColor.RED);
				}
			}
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {
		}

	}
}
