package net.badlion.potpvp.inventories.duel;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.exceptions.NoRatingFoundException;
import net.badlion.potpvp.ladders.Ladder;
import net.badlion.potpvp.managers.PotPvPPlayerManager;
import net.badlion.potpvp.managers.RatingManager;
import net.badlion.potpvp.matchmaking.Match;
import net.badlion.potpvp.matchmaking.RedRoverMatch;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RedRoverChooseFighterInventory {

	// Items
	private static ItemStack chooseFirstFighterItem;
	private static ItemStack chooseEnemyFighterItem;

	// Screen handlers
	private static SelectFighterScreenHandler selectFighterScreenHandler;

	// Cached inventories
	private static Map<Group, SmellyInventory> firstFighterInventories = new HashMap<>();
	private static Map<Group, SmellyInventory> enemyFighterInventories = new HashMap<>();


	public static void initialize() {
		// Items
		RedRoverChooseFighterInventory.chooseFirstFighterItem = ItemStackUtil.createItem(Material.BOOK, ChatColor.GREEN + "Choose First Fighter");
		RedRoverChooseFighterInventory.chooseEnemyFighterItem = ItemStackUtil.createItem(Material.BOOK, ChatColor.GREEN + "Choose Enemy Fighter");

		// Screen handlers
		RedRoverChooseFighterInventory.selectFighterScreenHandler = new SelectFighterScreenHandler();
	}

	public static void openSelectFirstFighterInventory(Player player, Group group) {
		SmellyInventory smellyInventory = RedRoverChooseFighterInventory.firstFighterInventories.get(group);
		if (smellyInventory != null) {
			BukkitUtil.openInventory(player, smellyInventory.getMainInventory());
			return;
		}

		smellyInventory = new SmellyInventory(RedRoverChooseFighterInventory.selectFighterScreenHandler, 9,
				ChatColor.BOLD + ChatColor.AQUA.toString() + "Choose First Fighter");

		Match match = GroupStateMachine.regularMatchState.getMatchFromGroup(group);
		Ladder ladder = Ladder.getLadderMap(Ladder.LadderType.OneVsOneRanked).get(match.getKitRuleSet().getName());

		for (Player pl : group.players()) {
			smellyInventory.getMainInventory().addItem(RedRoverChooseFighterInventory.getSkullForPlayer(ladder, pl));
		}

		PotPvPPlayerManager.addDebug(player, "Open red rover select first fighter inventory");

		BukkitUtil.openInventory(player, smellyInventory.getMainInventory());
	}

	public static void openSelectEnemyFighterInventory(Player player, Group group) {
		SmellyInventory smellyInventory = RedRoverChooseFighterInventory.enemyFighterInventories.get(group);
		if (smellyInventory != null) {
			BukkitUtil.openInventory(player, smellyInventory.getMainInventory());
			return;
		}

		smellyInventory = new SmellyInventory(RedRoverChooseFighterInventory.selectFighterScreenHandler, 9,
				ChatColor.BOLD + ChatColor.AQUA.toString() + "Choose Enemy Fighter");

		Match match = GroupStateMachine.regularMatchState.getMatchFromGroup(group);
		Ladder ladder = Ladder.getLadderMap(Ladder.LadderType.OneVsOneRanked).get(match.getKitRuleSet().getName());

		Set<Player> aliveEnemyPlayers = group == match.getGroup1() ? match.getParty2AlivePlayers() : match.getParty1AlivePlayers();

		for (Player pl : aliveEnemyPlayers) {
			smellyInventory.getMainInventory().addItem(RedRoverChooseFighterInventory.getSkullForPlayer(ladder, pl));
		}

		PotPvPPlayerManager.addDebug(player, "Open red rover select enemy fighter inventory");

		BukkitUtil.openInventory(player, smellyInventory.getMainInventory());
	}

	public static void handlePlayerDeath(Player player, Group enemyGroup) {
		SmellyInventory smellyInventory = RedRoverChooseFighterInventory.enemyFighterInventories.get(enemyGroup);

		if (smellyInventory == null) return;

		for (ItemStack itemStack : smellyInventory.getMainInventory().getContents()) {
			if (itemStack.getType() == Material.SKULL_ITEM) {
				if (itemStack.getItemMeta().getDisplayName().contains(player.getName())) {
					smellyInventory.getMainInventory().remove(itemStack);
				}
			}
		}
	}

	public static void cleanUpCachedInventories(Group group1, Group group2) {
		RedRoverChooseFighterInventory.firstFighterInventories.remove(group1);
		RedRoverChooseFighterInventory.firstFighterInventories.remove(group2);
		RedRoverChooseFighterInventory.enemyFighterInventories.remove(group1);
		RedRoverChooseFighterInventory.enemyFighterInventories.remove(group2);
	}

	private static ItemStack getSkullForPlayer(Ladder ladder, Player player) {
		// Is ladder null? (no 1v1 ladder for this kit
		if (ladder != null) {
			int rating;

			try {
				rating = RatingManager.getPlayerRating(player.getUniqueId(), ladder);
			} catch (NoRatingFoundException e) {
				rating = 1400;
			}

			return ItemStackUtil.createItem(Material.SKULL_ITEM, (short) 3,
					ChatColor.GREEN + player.getName(), ChatColor.LIGHT_PURPLE.toString() + rating);
		} else {
			return ItemStackUtil.createItem(Material.SKULL_ITEM, (short) 3,
					ChatColor.GREEN + player.getName(), ChatColor.LIGHT_PURPLE.toString() + "No rating");
		}
	}

	public static ItemStack getChooseFirstFighterItem() {
		return RedRoverChooseFighterInventory.chooseFirstFighterItem;
	}

	public static ItemStack getChooseEnemyFighterItem() {
		return RedRoverChooseFighterInventory.chooseEnemyFighterItem;
	}

	public static class SelectFighterScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			Group group = PotPvP.getInstance().getPlayerGroup(player);
			Player nextFighter = PotPvP.getInstance().getServer().getPlayerExact(item.getItemMeta().getDisplayName().substring(2));

			// Is the player still online?
			if (!Gberry.isPlayerOnline(nextFighter)) {
				player.sendMessage(ChatColor.RED + "That player has left the game, please pick a new player!");
				BukkitUtil.closeInventory(player);
				return;
			}

			RedRoverMatch match = ((RedRoverMatch) GroupStateMachine.regularMatchState.getMatchFromGroup(group));

			if (player == match.getLastFightWinner()) {
				match.setGroupNextFighter(match.getOtherGroup(group), player, nextFighter);
			} else {
				match.setGroupNextFighter(group, player, nextFighter);
			}

			BukkitUtil.closeInventory(player);
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}

}
