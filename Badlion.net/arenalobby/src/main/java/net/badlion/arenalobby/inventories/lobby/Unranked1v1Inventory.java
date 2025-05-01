package net.badlion.arenalobby.inventories.lobby;

import net.badlion.arenacommon.ArenaCommon;
import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenalobby.ArenaLobby;
import net.badlion.arenalobby.Group;
import net.badlion.arenalobby.helpers.KitInventoryHelper;
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

public class Unranked1v1Inventory {

	public static List<Ladder> unlimitedUnrankedLadders = new ArrayList<>();

	private static SmellyInventory smellyInventory;

	public static void initialize() {
		Unranked1v1Inventory.smellyInventory = new SmellyInventory(new Unranked1v1InventoryScreenHandler(), 45,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "Unranked 1v1 - Select kit");

		Unranked1v1Inventory.updateUnranked1v1Inventory();
	}

	public static void openUnranked1v1Inventory(Player player) {
		PotPvPPlayerManager.addDebug(player, "Open unranked 1v1 inventory");

		BukkitUtil.openInventory(player, Unranked1v1Inventory.smellyInventory.getMainInventory());
	}

	public static void updateUnranked1v1Inventory() {
		Inventory unranked1v1Inventory = Unranked1v1Inventory.smellyInventory.getMainInventory();
		unranked1v1Inventory.clear();

		int i = 0;
		int j = 18;

		for (Ladder ladder : LadderManager.getLadderMap(ArenaCommon.LadderType.UNRANKED_1V1).values()) {
			ItemStack item = KitRuleSet.getKitRuleSetItem(ladder.getKitRuleSet());
			int total = ladder.getInGame() + ladder.getInQueue();
			item.setAmount((total <= 0) ? 1 : (total > 64) ? 64 : total);
			ItemMeta itemMeta = item.getItemMeta();
			List<String> itemLore = new ArrayList<>();
			itemLore.add(ChatColor.BLUE + "In game: " + ladder.getInGame());
			itemLore.add(ChatColor.BLUE + "In queue: " + ladder.getInQueue());
			itemLore.add("");
			itemLore.add(ChatColor.YELLOW + "Middle click to preview kit");
			itemLore.add("");
			if (Unranked1v1Inventory.unlimitedUnrankedLadders.contains(ladder)) {
				itemLore.add(ChatColor.AQUA + "Unlimited Unranked Matches");
			} else {
				itemLore.add(ChatColor.RED + "Limited Unranked Matches");
			}
			itemMeta.setLore(itemLore);
			item.setItemMeta(itemMeta);

			if (Unranked1v1Inventory.unlimitedUnrankedLadders.contains(ladder)) {
				unranked1v1Inventory.setItem(i++, item);
			} else {
				unranked1v1Inventory.setItem(j++, item);
			}
		}

		unranked1v1Inventory.setItem(unranked1v1Inventory.getSize() - 1, SmellyInventory.getCloseInventoryItem());
	}

	private static class Unranked1v1InventoryScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			Group group = ArenaLobby.getInstance().getPlayerGroup(player);
			// Kit preview check
			if (event.getClick().equals(ClickType.MIDDLE)) {
				KitInventoryHelper.openKitPreviewInventory(fakeHolder.getSmellyInventory(), event.getView().getTopInventory(), player, item);
			} else {
				KitRuleSet kitRuleSet = KitRuleSet.getKitRuleSet(item);

				LadderManager.joinLadderQueue(group, player, kitRuleSet.getName(), ArenaCommon.LadderType.UNRANKED_1V1);

				BukkitUtil.closeInventory(player);
			}
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}

}