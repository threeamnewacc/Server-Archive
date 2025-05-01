package net.badlion.potpvp.inventories.party;

import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.ladders.Ladder;
import net.badlion.potpvp.managers.PotPvPPlayerManager;
import net.badlion.potpvp.rulesets.KitRuleSet;
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
import java.util.List;

public class Ranked5v5Inventory {

	private static SmellyInventory smellyInventory;

	public static void initialize() {
		Ranked5v5Inventory.smellyInventory = new SmellyInventory(new Ranked5v5InventoryScreenHandler(), 27,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "Ranked 5v5 - Select kit");

		// Fill with the kit rule set items
		Ranked5v5Inventory.updateRanked5v5Inventory();
	}

	public static void openRanked5v5Inventory(Player player) {
		PotPvPPlayerManager.addDebug(player, "Open ranked 5v5 inventory");

		BukkitUtil.openInventory(player, Ranked5v5Inventory.smellyInventory.getMainInventory());
	}

	public static void updateRanked5v5Inventory() {
		Inventory ranked5v5Inventory = Ranked5v5Inventory.smellyInventory.getMainInventory();
		ranked5v5Inventory.clear();

		int inGame = 0;
		for (KitRuleSet kitRuleSet : Ladder.getLadder(KitRuleSet.SG_BUILD_UHC_LADDER_NAME, Ladder.LadderType.FiveVsFiveRanked).getAllKitRuleSets()) {
			inGame += kitRuleSet.getLadderPopulations().get(Ladder.LadderType.FiveVsFiveRanked);
		}

		ItemStack item = ItemStackUtil.createItem(Material.NETHER_STAR, ChatColor.GREEN + "SG/BuildUHC");
		ItemMeta itemMeta = item.getItemMeta();
		List<String> itemLore = new ArrayList<>();
		itemLore.add(ChatColor.BLUE + "In game: " + inGame);
		itemLore.add(ChatColor.BLUE + "In queue: " + Ladder.getLadder(KitRuleSet.SG_BUILD_UHC_LADDER_NAME, Ladder.LadderType.FiveVsFiveRanked).getMatchMakingService().getNumberInQueue());
		itemMeta.setLore(itemLore);
		item.setItemMeta(itemMeta);

		ranked5v5Inventory.addItem(item);

		ranked5v5Inventory.setItem(26, SmellyInventory.getCloseInventoryItem());
	}

	private static class Ranked5v5InventoryScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			Group group = PotPvP.getInstance().getPlayerGroup(player);

			// Party leader executes command
			group.getLeader().performCommand("rankedteam " + PotPvP.getInstance().getCmdSignsPlugin().generateHash() + " " + KitRuleSet.SG_BUILD_UHC_LADDER_NAME + " 5");

			BukkitUtil.closeInventory(player);
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}

}