package net.badlion.arenalobby.inventories.party;

import net.badlion.arenacommon.ArenaCommon;
import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenalobby.ArenaLobby;
import net.badlion.arenalobby.Group;
import net.badlion.arenalobby.helpers.KitInventoryHelper;
import net.badlion.arenalobby.helpers.RankedInventoryHelper;
import net.badlion.arenalobby.inventories.clan.ClanRanked5v5Inventory;
import net.badlion.arenalobby.inventories.lobby.Ranked1v1Inventory;
import net.badlion.arenalobby.inventories.lobby.RankedInventory;
import net.badlion.arenalobby.ladders.Ladder;
import net.badlion.arenalobby.managers.LadderManager;
import net.badlion.arenalobby.managers.PotPvPPlayerManager;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Ranked2v2Inventory {

	private static SmellyInventory smellyInventory;

	public static void initialize() {
		SmellyInventory smellyInventory = new SmellyInventory(new Ranked2v2InventoryScreenHandler(), 36,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "Ranked 2v2 - Select kit");
		smellyInventory.getFakeHolder().setParentInventory(RankedInventory.smellyInventory.getMainInventory());

		// Fill with the kit rule set items
		KitInventoryHelper.fillMatchMakingQueueInventory(smellyInventory.getMainInventory(), ArenaCommon.LadderType.RANKED_2V2);

		Ranked2v2Inventory.smellyInventory = smellyInventory;
		Ranked2v2Inventory.updateRanked2v2Inventory();
	}

	public static void openRanked2v2Inventory(Player player) {
		PotPvPPlayerManager.addDebug(player, "Open ranked 2v2 inventory");

		BukkitUtil.openInventory(player, Ranked2v2Inventory.smellyInventory.getMainInventory());
	}

	public static void updateRanked2v2Inventory() {
		Inventory ranked2v2Inventory = Ranked2v2Inventory.smellyInventory.getMainInventory();
		ranked2v2Inventory.clear();
		for (Ladder ladder : LadderManager.getLadderMap(ArenaCommon.LadderType.RANKED_2V2).values()) {
			ItemStack item = KitRuleSet.getKitRuleSetItem(ladder.getKitRuleSet());
			int total = ladder.getInGame() + ladder.getInQueue();
			item.setAmount((total <= 0) ? 1 : (total > 64) ? 64 : total);
			ItemMeta itemMeta = item.getItemMeta();
			List<String> itemLore = new ArrayList<>();
			itemLore.add(ChatColor.BLUE + "In game: " + ladder.getInGame());
			itemLore.add(ChatColor.BLUE + "In queue: " + ladder.getInQueue());
			itemLore.add("");
			itemLore.add(ChatColor.YELLOW + "Middle click to preview kit");
			itemMeta.setLore(itemLore);
			item.setItemMeta(itemMeta);

			ranked2v2Inventory.addItem(item);
		}
		RankedInventoryHelper.addRankedQueueInventories(ranked2v2Inventory, ArenaCommon.LadderType.RANKED_2V2);
		ranked2v2Inventory.setItem(35, SmellyInventory.getCloseInventoryItem());
	}

	private static class Ranked2v2InventoryScreenHandler implements SmellyInventory.SmellyInventoryHandler {

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
							//Ranked2v2Inventory.openRanked2v2Inventory(player);
							break;
						case 32:
							Ranked3v3Inventory.openRanked3v3Inventory(player);
							break;
						case 34:
							ClanRanked5v5Inventory.openRanked5v5Inventory(player);
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

				LadderManager.joinLadderQueue(group, player, kitRuleSet.getName(), ArenaCommon.LadderType.RANKED_2V2);

				BukkitUtil.closeInventory(player);
			}
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}

}