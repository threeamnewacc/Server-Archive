package net.badlion.arenalobby.inventories.lobby;

import net.badlion.arenacommon.ArenaCommon;
import net.badlion.arenalobby.inventories.clan.ClanRanked5v5Inventory;
import net.badlion.arenalobby.inventories.party.Ranked2v2Inventory;
import net.badlion.arenalobby.inventories.party.Ranked3v3Inventory;
import net.badlion.arenalobby.ladders.Ladder;
import net.badlion.arenalobby.managers.LadderManager;
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
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RankedInventory {

	public static SmellyInventory smellyInventory;

	private static Map<ArenaCommon.LadderType, Integer> slotToLadder = new HashMap<>();

	public static void fillRankedInventory() {
		SmellyInventory smellyInventory = new SmellyInventory(new RankedInventoryScreenHandler(), 27,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "Ranked - Select a Queue");

		slotToLadder.clear();

		slotToLadder.put(ArenaCommon.LadderType.RANKED_1V1, 0);
		slotToLadder.put(ArenaCommon.LadderType.RANKED_2V2, 1);
		slotToLadder.put(ArenaCommon.LadderType.RANKED_3V3, 2);
		slotToLadder.put(ArenaCommon.LadderType.RANKED_5V5_CLAN, 8);
		RankedInventory.smellyInventory = smellyInventory;

		RankedInventory.updateRankedInventory();
	}

	public static void openRankedInventory(Player player) {

		BukkitUtil.openInventory(player, RankedInventory.smellyInventory.getMainInventory());
	}


	public static void updateRankedInventory() {
		Inventory rankedInventory = RankedInventory.smellyInventory.getMainInventory();
		rankedInventory.clear();
		for (ArenaCommon.LadderType ladderType : ArenaCommon.LadderType.values()) {
			if (!ladderType.isRanked()) {
				continue;
			}
			if (slotToLadder.get(ladderType) == null) {
				continue;
			}
			ItemStack item;
			switch (ladderType) {
				case RANKED_1V1:
					item = ItemStackUtil.createItem(Material.DIAMOND_SWORD, 1);
					break;
				case RANKED_2V2:
					item = ItemStackUtil.createItem(Material.DIAMOND, 1);
					break;
				case RANKED_3V3:
					item = ItemStackUtil.createItem(Material.DIAMOND_SWORD, 1);
					break;
				case RANKED_5V5_CLAN:
					item = ItemStackUtil.createItem(Material.GOLD_AXE, 1);
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

			rankedInventory.setItem(slotToLadder.get(ladderType), item);
		}
		rankedInventory.setItem(26, SmellyInventory.getCloseInventoryItem());
	}

	private static class RankedInventoryScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			// Kit preview check
			ArenaCommon.LadderType ladderType = null;
			for (Map.Entry<ArenaCommon.LadderType, Integer> entry : slotToLadder.entrySet()) {
				if (entry.getValue() == event.getRawSlot()) {
					ladderType = entry.getKey();
				}
			}

			if (ladderType != null) {
				switch (ladderType) {
					case RANKED_1V1:
						Ranked1v1Inventory.openRanked1v1Inventory(player);
						return;
					case RANKED_2V2:
						Ranked2v2Inventory.openRanked2v2Inventory(player);
						return;
					case RANKED_3V3:
						Ranked3v3Inventory.openRanked3v3Inventory(player);
						return;
					case RANKED_5V5_CLAN:
						ClanRanked5v5Inventory.openRanked5v5Inventory(player);
						return;
				}
			}

			//BukkitUtil.closeInventory(player);
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}

}
