package net.badlion.arenalobby.helpers;

import net.badlion.arenacommon.ArenaCommon;
import net.badlion.arenalobby.ladders.Ladder;
import net.badlion.arenalobby.managers.LadderManager;
import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class RankedInventoryHelper {

	public static void addRankedQueueInventories(Inventory inventory, ArenaCommon.LadderType activeInventory) {
		for (ArenaCommon.LadderType ladderType : ArenaCommon.LadderType.values()) {
			if (ladderType.equals(ArenaCommon.LadderType.RANKED_5V5_CLAN)) {
				continue;
			}
			if (!ladderType.isRanked()) {
				continue;
			}
			ItemStack item;
			switch (ladderType) {
				case RANKED_1V1:
					item = ItemStackUtil.createItem(activeInventory == ladderType ? Material.DIAMOND : Material.DIAMOND_SWORD, 1);
					break;
				case RANKED_2V2:
					item = ItemStackUtil.createItem(activeInventory == ladderType ? Material.DIAMOND : Material.DIAMOND_SWORD, 2);
					break;
				case RANKED_3V3:
					item = ItemStackUtil.createItem(activeInventory == ladderType ? Material.DIAMOND : Material.DIAMOND_SWORD, 3);
					break;
				case RANKED_5V5_CLAN:
					item = ItemStackUtil.createItem(activeInventory == ladderType ? Material.DIAMOND : Material.GOLD_AXE, 5);
					break;
				default:
					item = ItemStackUtil.createItem(Material.DIAMOND_SWORD, 1);
					break;
			}
			int total = 0;
			for (Ladder ladder : LadderManager.getLadderMap(ladderType).values()) {
				total += ladder.getInGame();
				total += ladder.getInQueue();
			}
			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.setDisplayName(ChatColor.GREEN + ladderType.getNiceName());
			List<String> itemLore = new ArrayList<>();
			itemLore.add(ChatColor.BLUE + "Total Players: " + total);
			itemLore.add("");
			itemMeta.setLore(itemLore);
			item.setItemMeta(itemMeta);
			switch (ladderType) {
				case RANKED_1V1:
					inventory.setItem(28, item);
					break;
				case RANKED_2V2:
					inventory.setItem(30, item);
					break;
				case RANKED_3V3:
					inventory.setItem(32, item);
					break;
				case RANKED_5V5_CLAN:
					inventory.setItem(34, item);
					break;
				default:
					break;
			}
		}
	}
}
