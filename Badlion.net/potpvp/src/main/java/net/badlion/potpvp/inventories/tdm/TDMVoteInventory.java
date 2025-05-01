package net.badlion.potpvp.inventories.tdm;

import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.managers.PotPvPPlayerManager;
import net.badlion.potpvp.rulesets.KitRuleSet;
import net.badlion.potpvp.states.matchmaking.GameState;
import net.badlion.potpvp.tdm.TDMGame;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TDMVoteInventory {

	private static SmellyInventory.SmellyInventoryHandler screenHandler;

	private static ItemStack voteItem;

	private static Map<TDMGame, SmellyInventory> votingInventories = new HashMap<>();

	public static void initialize() {
		TDMVoteInventory.screenHandler = new TDMVoteInventoryScreenHandler();

		TDMVoteInventory.voteItem = ItemStackUtil.createItem(Material.BOOK, ChatColor.GREEN + "Vote For Next Kit");
	}

	public static void createTDMVoteInventory(TDMGame tdmGame) {
		SmellyInventory smellyInventory = new SmellyInventory(TDMVoteInventory.screenHandler, 9,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "Vote For Next Kit");

		for (KitRuleSet kitRuleSet : TDMGame.getKitRuleSets()) {
			if (kitRuleSet != tdmGame.getKitRuleSet()) {
				// Create kit item
				ItemStack item = kitRuleSet.getKitItem().clone();
				ItemMeta itemMeta = item.getItemMeta();

				List<String> lore = new ArrayList<>();
				lore.add(ChatColor.YELLOW + "Votes: 0");

				itemMeta.setLore(lore);
				item.setItemMeta(itemMeta);

				smellyInventory.getMainInventory().addItem(item);
			}
		}

		TDMVoteInventory.votingInventories.put(tdmGame, smellyInventory);
	}

	public static void openTDMVoteInventory(TDMGame tdmGame, Player player) {
		PotPvPPlayerManager.addDebug(player, "Open tdm vote inventory");

		BukkitUtil.openInventory(player, TDMVoteInventory.votingInventories.get(tdmGame).getMainInventory());
	}

	public static ItemStack getVoteItem() {
		return voteItem;
	}

	private static class TDMVoteInventoryScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			Group group = PotPvP.getInstance().getPlayerGroup(player);
			TDMGame tdmGame = (TDMGame) GameState.getGroupGame(group);

			// Don't let them vote if new kit was chosen and TDM is counting down
			if (tdmGame.isCountingDown()) {
				player.sendMessage(ChatColor.RED + "You can no longer vote, the match is starting soon!");
				BukkitUtil.closeInventory(player);
				return;
			}

			// Check donator permission
			if (!player.hasPermission("badlion.lion")) {
				player.sendMessage(ChatColor.RED + "Only lion donators can vote for kits!");

				BukkitUtil.closeInventory(player);
				return;
			}

			// Get kit from item
			KitRuleSet kitRuleSet = KitRuleSet.getKitRuleSet(item);

			// Add vote
			KitRuleSet oldVote = tdmGame.addVote(player, kitRuleSet);

			if (oldVote != null) {
				if (oldVote == kitRuleSet) {
					player.sendMessage(ChatColor.YELLOW + "You have already voted for " + oldVote.getName() + "!");
					BukkitUtil.closeInventory(player);
					return;
				}

				// Remove a vote from old kit item's lore
				SmellyInventory smellyInventory = TDMVoteInventory.votingInventories.get(tdmGame);

				// Figure out which item it is
				for (ItemStack item2 : smellyInventory.getMainInventory().getContents()) {
					if (item2 == null || item2.getType() == Material.AIR) continue;

					KitRuleSet kitRuleSet2 = KitRuleSet.getKitRuleSet(item2);
					if (oldVote == kitRuleSet2) {
						int votes = Integer.valueOf(item2.getItemMeta().getLore().get(0).split(" ")[1]) - 1;
						List<String> lore = new ArrayList<>();
						lore.add(ChatColor.YELLOW + "Votes: " + votes);

						ItemMeta itemMeta = item2.getItemMeta();
						itemMeta.setLore(lore);
						item2.setItemMeta(itemMeta);

						break;
					}
				}

				player.sendMessage(ChatColor.YELLOW + "You have changed your vote from " + oldVote.getName()
						+ " to " + kitRuleSet.getName() + "!");
			} else {
				player.sendMessage(ChatColor.YELLOW + "You have voted for " + kitRuleSet.getName() + "!");
			}

			// Add vote to kit item's lore
			int votes = Integer.valueOf(item.getItemMeta().getLore().get(0).split(" ")[1]) + 1;
			List<String> lore = new ArrayList<>();
			lore.add(ChatColor.YELLOW + "Votes: " + votes);

			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.setLore(lore);
			item.setItemMeta(itemMeta);

			BukkitUtil.closeInventory(player);
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {
		}

	}

}