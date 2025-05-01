package net.badlion.potpvp.inventories.party;

import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.helpers.KitHelper;
import net.badlion.smellyinventory.SmellyInventory;
import net.badlion.potpvp.ladders.Ladder;
import net.badlion.potpvp.managers.PotPvPPlayerManager;
import net.badlion.potpvp.rulesets.KitRuleSet;
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
		SmellyInventory smellyInventory = new SmellyInventory(new Ranked2v2InventoryScreenHandler(), 27,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "Ranked 2v2 - Select kit");

		// Fill with the kit rule set items
		KitHelper.fillMatchMakingQueueInventory(smellyInventory.getMainInventory(), Ladder.LadderType.TwoVsTwoRanked);

		Ranked2v2Inventory.smellyInventory = smellyInventory;
	}

	public static void openRanked2v2Inventory(Player player) {
		PotPvPPlayerManager.addDebug(player, "Open ranked 2v2 inventory");

		BukkitUtil.openInventory(player, Ranked2v2Inventory.smellyInventory.getMainInventory());
	}

	public static void updateRanked2v2Inventory() {
		Inventory ranked2v2Inventory = Ranked2v2Inventory.smellyInventory.getMainInventory();
		ranked2v2Inventory.clear();
		for (Ladder ladder : Ladder.getLadderMap(Ladder.LadderType.TwoVsTwoRanked).values()) {
			ItemStack item = KitRuleSet.getKitRuleSetItem(ladder.getKitRuleSet());
			ItemMeta itemMeta = item.getItemMeta();
			List<String> itemLore = new ArrayList<>();
			itemLore.add(ChatColor.BLUE + "In game: " + ladder.getKitRuleSet().getLadderPopulations().get(Ladder.LadderType.TwoVsTwoRanked));
			itemLore.add(ChatColor.BLUE + "In queue: " + ladder.getMatchMakingService().getNumberInQueue());
			itemLore.add("");
			itemLore.add(ChatColor.YELLOW + "Middle click to preview kit");
			itemMeta.setLore(itemLore);
			item.setItemMeta(itemMeta);

			ranked2v2Inventory.addItem(item);
		}
		ranked2v2Inventory.setItem(26, SmellyInventory.getCloseInventoryItem());
	}

	private static class Ranked2v2InventoryScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			Group group = PotPvP.getInstance().getPlayerGroup(player);
			if (event.getClick().equals(ClickType.MIDDLE)) {
				// Preview kit
				KitHelper.openKitPreviewInventory(fakeHolder.getSmellyInventory(), event.getView().getTopInventory(), player, item);
			} else {
				KitRuleSet kitRuleSet = KitRuleSet.getKitRuleSet(item);

				// Party leader executes command
				group.getLeader().performCommand("rankedteam " + PotPvP.getInstance().getCmdSignsPlugin().generateHash() + " " + kitRuleSet.getName() + " 2");

				BukkitUtil.closeInventory(player);
			}
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}

}