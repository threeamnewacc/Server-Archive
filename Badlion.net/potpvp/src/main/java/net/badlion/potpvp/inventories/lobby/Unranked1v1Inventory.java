package net.badlion.potpvp.inventories.lobby;

import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.helpers.KitHelper;
import net.badlion.potpvp.ladders.Ladder;
import net.badlion.potpvp.managers.PotPvPPlayerManager;
import net.badlion.potpvp.rulesets.KitRuleSet;
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

	private static SmellyInventory smellyInventory;

	public static void fillUnranked1v1Inventory() {
		SmellyInventory smellyInventory = new SmellyInventory(new Unranked1v1InventoryScreenHandler(), 27,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "Unranked 1v1 - Select kit");

		// Fill with the kit rule set items
		KitHelper.fillMatchMakingQueueInventory(smellyInventory.getMainInventory(),
				Ladder.LadderType.OneVsOneUnranked);

		Unranked1v1Inventory.smellyInventory = smellyInventory;
	}

	public static void openUnranked1v1Inventory(Player player) {
		PotPvPPlayerManager.addDebug(player, "Open unranked 1v1 inventory");

		BukkitUtil.openInventory(player, Unranked1v1Inventory.smellyInventory.getMainInventory());
	}

	public static void updateUnranked1v1Inventory() {
		Inventory unranked1v1Inventory = Unranked1v1Inventory.smellyInventory.getMainInventory();
		unranked1v1Inventory.clear();
		for (Ladder ladder : Ladder.getLadderMap(Ladder.LadderType.OneVsOneUnranked).values()) {
			ItemStack item = KitRuleSet.getKitRuleSetItem(ladder.getKitRuleSet());
			ItemMeta itemMeta = item.getItemMeta();
			List<String> itemLore = new ArrayList<>();
			itemLore.add(ChatColor.BLUE + "In game: " + ladder.getKitRuleSet().getLadderPopulations().get(Ladder.LadderType.OneVsOneUnranked));
			itemLore.add(ChatColor.BLUE + "In queue: " + ladder.getMatchMakingService().getNumberInQueue());
			itemLore.add("");
			itemLore.add(ChatColor.YELLOW + "Middle click to preview kit");
			itemMeta.setLore(itemLore);
			item.setItemMeta(itemMeta);

			unranked1v1Inventory.addItem(item);
		}
		unranked1v1Inventory.setItem(26, SmellyInventory.getCloseInventoryItem());
	}

	private static class Unranked1v1InventoryScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			Group group = PotPvP.getInstance().getPlayerGroup(player);
			// Kit preview check
			if (event.getClick().equals(ClickType.MIDDLE)) {
				KitHelper.openKitPreviewInventory(fakeHolder.getSmellyInventory(), event.getView().getTopInventory(), player, item);
			} else {
				KitRuleSet kitRuleSet = KitRuleSet.getKitRuleSet(item);

				group.getLeader().performCommand("unranked " + PotPvP.getInstance().getCmdSignsPlugin().generateHash() + " " + kitRuleSet.getName());

				BukkitUtil.closeInventory(player);
			}
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}

}