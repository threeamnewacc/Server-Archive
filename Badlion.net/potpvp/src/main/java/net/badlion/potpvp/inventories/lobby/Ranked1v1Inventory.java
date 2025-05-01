package net.badlion.potpvp.inventories.lobby;

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

public class Ranked1v1Inventory {

	private static SmellyInventory smellyInventory;

	public static void initialize() {
		SmellyInventory smellyInventory = new SmellyInventory(new Ranked1v1InventoryScreenHandler(), 27,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "Ranked 1v1 - Select kit");

		// Fill with the kit rule set items
		KitHelper.fillMatchMakingQueueInventory(smellyInventory.getMainInventory(), Ladder.LadderType.OneVsOneRanked);

		Ranked1v1Inventory.smellyInventory = smellyInventory;
	}

	public static void openRanked1v1Inventory(Player player) {
		PotPvPPlayerManager.addDebug(player, "Open ranked 1v1 inventory");

		BukkitUtil.openInventory(player, Ranked1v1Inventory.smellyInventory.getMainInventory());
	}

	public static void updateRanked1v1Inventory() {
		Inventory ranked1v1Inventory = Ranked1v1Inventory.smellyInventory.getMainInventory();
		ranked1v1Inventory.clear();
		for (Ladder ladder : Ladder.getLadderMap(Ladder.LadderType.OneVsOneRanked).values()) {
			ItemStack item = KitRuleSet.getKitRuleSetItem(ladder.getKitRuleSet());
			ItemMeta itemMeta = item.getItemMeta();
			List<String> itemLore = new ArrayList<>();
			itemLore.add(ChatColor.BLUE + "In game: " + ladder.getKitRuleSet().getLadderPopulations().get(Ladder.LadderType.OneVsOneRanked));
			itemLore.add(ChatColor.BLUE + "In queue: " + ladder.getMatchMakingService().getNumberInQueue());
			itemLore.add("");
			itemLore.add(ChatColor.YELLOW + "Middle click to preview kit");
			itemMeta.setLore(itemLore);
			item.setItemMeta(itemMeta);

			ranked1v1Inventory.addItem(item);
		}
		ranked1v1Inventory.setItem(26, SmellyInventory.getCloseInventoryItem());
	}

	private static class Ranked1v1InventoryScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			Group group = PotPvP.getInstance().getPlayerGroup(player);
			if (event.getClick().equals(ClickType.MIDDLE)) {
				// Preview kit
				KitHelper.openKitPreviewInventory(fakeHolder.getSmellyInventory(), event.getView().getTopInventory(), player, item);
			} else {
				KitRuleSet kitRuleSet = KitRuleSet.getKitRuleSet(item);

				group.getLeader().performCommand("ranked " + PotPvP.getInstance().getCmdSignsPlugin().generateHash() + " " + kitRuleSet.getName());

				BukkitUtil.closeInventory(player);
			}
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}
}