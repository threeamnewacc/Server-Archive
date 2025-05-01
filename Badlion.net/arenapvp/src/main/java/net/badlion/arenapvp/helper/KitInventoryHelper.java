package net.badlion.arenapvp.helper;

import net.badlion.arenacommon.ArenaCommon;
import net.badlion.arenacommon.kits.Kit;
import net.badlion.arenacommon.kits.KitCommon;
import net.badlion.arenacommon.rulesets.CustomRuleSet;
import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenapvp.ArenaPvP;
import net.badlion.arenapvp.inventory.KitPreviewInventory;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class KitInventoryHelper {


	public static int KIT_PREVIEW_LIMIT = 1000;

	private static Map<UUID, Long> lastCustomPreviewTime = new HashMap<>();

	public static ItemStack createCustomKitInventoryItem(int customKitNumber) {
		return ItemStackUtil.createItem(
				Material.WRITTEN_BOOK, ChatColor.GREEN + "Custom Kit - " + customKitNumber, ChatColor.YELLOW + "Middle click to preview kit");
	}


	public static int getCustomKitNumberFromItem(ItemStack item) {
		// Get custom kit number
		int customKitNumber = -1;
		try {
			Gberry.log("KIT", "Splitting " + item.toString());
			String[] strings = item.getItemMeta().getDisplayName().split("- ");
			customKitNumber = Integer.valueOf(strings[1]);
		} catch (NumberFormatException e) {
			Bukkit.getLogger().severe("Custom kit number not found");
		}

		return customKitNumber;
	}

	private static boolean limitPreviews(Player player) {
		Long ts = KitInventoryHelper.lastCustomPreviewTime.get(player.getUniqueId());
		if (ts != null) {
			if (ts + KitInventoryHelper.KIT_PREVIEW_LIMIT > System.currentTimeMillis()) {
				player.sendFormattedMessage("{0}Do not spam kit preview, wait 1 second in between previews", ChatColor.RED);
				return false;
			}
		}

		KitInventoryHelper.lastCustomPreviewTime.put(player.getUniqueId(), System.currentTimeMillis());

		return true;
	}

	public static void openKitPreviewInventory(SmellyInventory smellyInventory, Inventory currentInventory, Player player, ItemStack item) {
		KitRuleSet kitRuleSet = KitRuleSet.getKitRuleSet(item);
		if (kitRuleSet instanceof CustomRuleSet) {
			KitInventoryHelper.openCustomKitPreviewInventory(smellyInventory, currentInventory, player, kitRuleSet, KitInventoryHelper.getCustomKitNumberFromItem(item) -1);
		}
	}

	private static void openCustomKitPreviewInventory(SmellyInventory smellyInventory, Inventory currentInventory, final Player player, final KitRuleSet kitRuleSet, final int customKitNumber) {
		// Prevent spam
		if (!KitInventoryHelper.limitPreviews(player)) {
			return;
		}

		SmellyInventory.FakeHolder fakeHolder = smellyInventory.createFakeHolderForKitPreviews();
		fakeHolder.setParentInventory(currentInventory);

		// Create inventory
		final Inventory inventory = ArenaPvP.getInstance().getServer().createInventory(fakeHolder,
				54, ChatColor.AQUA + ChatColor.BOLD.toString() + kitRuleSet.getName() + " Kit " + customKitNumber + " Preview");

		// Get kit and load one if we have it
		Kit kit = KitCommon.getKit(player, player.getUniqueId().toString(), kitRuleSet.getName(), customKitNumber);
		if (kit != null) {
			KitCommon.fillInventoryWithContents(inventory, kit.getArmorItems(), kit.getInventoryItems());
		} else {
			player.sendFormattedMessage("{0}No kit found to load.", ChatColor.RED);
			return;
		}

		inventory.setItem(53, SmellyInventory.getBackInventoryItem());
		BukkitUtil.openInventory(player, inventory);
	}

}
