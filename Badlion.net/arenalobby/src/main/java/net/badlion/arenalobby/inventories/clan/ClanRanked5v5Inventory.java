package net.badlion.arenalobby.inventories.clan;

import net.badlion.arenacommon.ArenaCommon;
import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenalobby.ArenaLobby;
import net.badlion.arenalobby.Group;
import net.badlion.arenalobby.helpers.KitInventoryHelper;
import net.badlion.arenalobby.helpers.RankedInventoryHelper;
import net.badlion.arenalobby.inventories.lobby.Ranked1v1Inventory;
import net.badlion.arenalobby.inventories.lobby.RankedInventory;
import net.badlion.arenalobby.inventories.party.Ranked2v2Inventory;
import net.badlion.arenalobby.inventories.party.Ranked3v3Inventory;
import net.badlion.arenalobby.ladders.Ladder;
import net.badlion.arenalobby.managers.LadderManager;
import net.badlion.arenalobby.managers.PotPvPPlayerManager;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ClanRanked5v5Inventory {

	private static SmellyInventory smellyInventory;

	public static void initialize() {
		ClanRanked5v5Inventory.smellyInventory = new SmellyInventory(new Ranked5v5InventoryScreenHandler(), 36,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "Ranked 5v5 - Select kit");
		ClanRanked5v5Inventory.smellyInventory.getFakeHolder().setParentInventory(RankedInventory.smellyInventory.getMainInventory());

		// Fill with the kit rule set items
		ClanRanked5v5Inventory.updateRanked5v5Inventory();
	}

	public static void openRanked5v5Inventory(Player player) {
		PotPvPPlayerManager.addDebug(player, "Open ranked 5v5 inventory");

		BukkitUtil.openInventory(player, ClanRanked5v5Inventory.smellyInventory.getMainInventory());
	}

	public static void updateRanked5v5Inventory() {
		Inventory ranked5v5Inventory = ClanRanked5v5Inventory.smellyInventory.getMainInventory();
		ranked5v5Inventory.clear();
		for (Ladder ladder : LadderManager.getLadderMap(ArenaCommon.LadderType.RANKED_5V5_CLAN).values()) {
			/*ItemStack item = KitRuleSet.getKitRuleSetItem(ladder.getKitRuleSet());
			ItemMeta itemMeta = item.getItemMeta();
			List<String> itemLore = new ArrayList<>();
			itemLore.add(ChatColor.BLUE + "In game: " + ladder.getInGame());
			itemLore.add(ChatColor.BLUE + "In queue: " + ladder.getInQueue());
			itemLore.add("");
			itemLore.add(ChatColor.YELLOW + "Middle click to preview kit");
			itemMeta.setLore(itemLore);
			item.setItemMeta(itemMeta);*/

			ItemStack item = new ItemStack(Material.EMERALD);
			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.setDisplayName(ChatColor.GOLD + "Clan 5v5 Ladder");
			List<String> itemLore = new ArrayList<>();
			itemLore.add(ChatColor.BLUE + "In game: " + ladder.getInGame());
			itemLore.add(ChatColor.BLUE + "In queue: " + ladder.getInQueue());
			itemLore.add("");
			itemLore.add(ChatColor.YELLOW + "Middle click to preview kit");
			itemMeta.setLore(itemLore);
			item.setItemMeta(itemMeta);

			ranked5v5Inventory.addItem(item);
		}
		RankedInventoryHelper.addRankedQueueInventories(ranked5v5Inventory, ArenaCommon.LadderType.RANKED_5V5_CLAN);

		ranked5v5Inventory.setItem(35, SmellyInventory.getCloseInventoryItem());
	}

	private static class Ranked5v5InventoryScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			Group group = ArenaLobby.getInstance().getPlayerGroup(player);
			if (item != null) {
				if (slot >= 28 && slot <= 35) {
					switch (slot) {
						case 28:
							Ranked1v1Inventory.openRanked1v1Inventory(player);
							break;
						case 30:
							Ranked2v2Inventory.openRanked2v2Inventory(player);
							break;
						case 32:
							Ranked3v3Inventory.openRanked3v3Inventory(player);
							break;
						case 34:
							//ClanRanked5v5Inventory.openRanked5v5Inventory(player);
							break;
					}
					return;
				}
			}
			if (event.getClick().equals(ClickType.MIDDLE)) {
				// Preview kit
				KitInventoryHelper.openKitPreviewInventory(fakeHolder.getSmellyInventory(), event.getView().getTopInventory(), player, item);
			} else {
				KitRuleSet kitRuleSet = KitRuleSet.getKitRuleSet(item);
				LadderManager.joinLadderQueue(group, player, kitRuleSet.getName(), ArenaCommon.LadderType.RANKED_5V5_CLAN);

				BukkitUtil.closeInventory(player);
			}
			BukkitUtil.closeInventory(player);
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}
}
