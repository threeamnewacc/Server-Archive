package net.badlion.arenatablist.lobby;

import net.badlion.arenacommon.ArenaCommon;
import net.badlion.arenalobby.ArenaLobby;
import net.badlion.arenalobby.ladders.Ladder;
import net.badlion.arenalobby.managers.LadderManager;
import net.badlion.arenalobby.managers.RankedLeftManager;
import net.badlion.arenalobby.managers.RatingManager;
import net.badlion.arenatablist.ArenaTabList;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.RatingUtil;
import net.badlion.tablist.TabList;
import net.badlion.tablist.TabListManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Map;

public class ArenaLobbyTabListManager extends TabListManager {

	public ArenaLobbyTabListManager() {
		super(ArenaTabList.getInstance());
	}

	public void createTabList(Player player, double globalRating, Map<Ladder, Double> ratings) {
		TabList tabList = new TabList(player, 1, 60);

		tabList.setPosition(1, "§6§lBadlion PvP", false);
		tabList.setPosition(21, "§6§lBadlion PvP ", false);
		tabList.setPosition(41, "§6§lBadlion PvP" + (char) 0x26c7, false);

		String division = RatingUtil.getDivisionFromRating(globalRating);
		String divisionColor = RatingUtil.getDivisionColorFromRating(globalRating);

		tabList.setPosition(2, divisionColor + ChatColor.STRIKETHROUGH + "----------  ", false);
		tabList.setPosition(22, divisionColor + ChatColor.STRIKETHROUGH + "---------  -", false);
		tabList.setPosition(42, divisionColor + ChatColor.STRIKETHROUGH + "--------  --", false);

		int rankedLeft = RankedLeftManager.getNumberOfRankedMatchesLeft(player);
		int unrankedLeft = RankedLeftManager.getNumberOfUnRankedMatchesLeft(player);

		tabList.setPosition(3, "§cRanked Left:", false);
		if (player.hasPermission(ArenaLobby.getUnlimitedRankedPermission())) {
			tabList.setPosition(4, "§9 Unlimited", false);
		} else if (rankedLeft > 0) {
			tabList.setPosition(4, "§9 " + rankedLeft + " ", false);
		} else {
			tabList.setPosition(4, "§9 None", false);
		}

		tabList.setPosition(6, "§cUnranked Left:", false);
		if (player.hasPermission(ArenaLobby.getUnlimitedRankedPermission())) {
			tabList.setPosition(7, "§9 Unlimited ", false);
		} else if (unrankedLeft > 0) {
			tabList.setPosition(7, "§9 " + unrankedLeft + "  ", false);
		} else {
			tabList.setPosition(7, "§9 None ", false);
		}

		// Get total unranked matches
		int totalUnrankedWins = RatingManager.getTotalUnrankedWins(player.getUniqueId());

		// Have they won 10 unranked matches?
		if (!player.hasPermission("badlion.donator") && totalUnrankedWins < RatingUtil.ARENA_UNRANKED_WINS_NEEDED_FOR_RANKED) {
			tabList.setPosition(23, "§6Unranked Wins:", false);
			tabList.setPosition(24, "§b " + totalUnrankedWins, false);
		} else {
			tabList.setPosition(23, "§6Ranked Played:", false);
			tabList.setPosition(24, "§b " + RatingManager.getTotalRankedMatchesPlayed(player.getUniqueId()), false);
		}

		tabList.setPosition(26, "§6Global Rank:", false);
		tabList.setPosition(27, divisionColor + " " + division, false);

		tabList.setPosition(43, "§cServer:", false);
		if (Gberry.serverName.toLowerCase().contains("lobby")) {
			tabList.setPosition(44, "§9 Lobby " + Gberry.plugin.getServerNumber(), false);
		} else {
			tabList.setPosition(44, "§9 Arena " + Gberry.plugin.getServerNumber(), false);
		}

		tabList.setPosition(46, "§cDonation Info:", false);
		if (player.hasPermission("badlion.staff")) {
			tabList.setPosition(47, "§9 Staff Member", false);
		} else if (player.hasPermission("badlion.famousplus")) {
			tabList.setPosition(47, "§9 Famous §l+", false);
		} else if (player.hasPermission("badlion.famous")) {
			tabList.setPosition(47, "§9 Famous", false);
		} else if (player.hasPermission("badlion.lion")) {
			tabList.setPosition(47, "§9 Lion", false);
		} else if (player.hasPermission("badlion.donatorplus")) {
			tabList.setPosition(47, "§9 Donator §l+", false);
		} else if (player.hasPermission("badlion.donator")) {
			tabList.setPosition(47, "§9 Donator", false);
		} else {
			tabList.setPosition(47, "§9 N/A", false);
		}

		tabList.setPosition(8, ChatColor.GREEN.toString() + ChatColor.STRIKETHROUGH + "----------- ", false);
		tabList.setPosition(28, "§a -- Ratings --", false);
		tabList.setPosition(48, ChatColor.GREEN.toString() + ChatColor.STRIKETHROUGH + "---------- -", false);

		Double rating;

		int row = 9;
		int index = 0;

		Ladder ladder = LadderManager.getLadder("Vanilla", ArenaCommon.LadderType.RANKED_1V1);
		rating = ratings.get(ladder);
		this.addLadderToTab(tabList, row, ladder, rating, player, index);

		row++;
		index++;

		ladder = LadderManager.getLadder("NoDebuff", ArenaCommon.LadderType.RANKED_1V1);
		rating = ratings.get(ladder);
		this.addLadderToTab(tabList, row, ladder, rating, player, index);

		row++;
		index++;

		ladder = LadderManager.getLadder("IronSoup", ArenaCommon.LadderType.RANKED_1V1);
		rating = ratings.get(ladder);
		this.addLadderToTab(tabList, row, ladder, rating, player, index);

		row++;
		index++;

		ladder = LadderManager.getLadder("Debuff", ArenaCommon.LadderType.RANKED_1V1);
		rating = ratings.get(ladder);
		this.addLadderToTab(tabList, row, ladder, rating, player, index);

		row++;
		index++;

		ladder = LadderManager.getLadder("SG", ArenaCommon.LadderType.RANKED_1V1);
		rating = ratings.get(ladder);
		this.addLadderToTab(tabList, row, ladder, rating, player, index);

		row++;
		index++;

		ladder = LadderManager.getLadder("Archer", ArenaCommon.LadderType.RANKED_1V1);
		rating = ratings.get(ladder);
		this.addLadderToTab(tabList, row, ladder, rating, player, index);

		row++;
		index++;

		ladder = LadderManager.getLadder("UHC", ArenaCommon.LadderType.RANKED_1V1);
		rating = ratings.get(ladder);
		this.addLadderToTab(tabList, row, ladder, rating, player, index);

		row++;
		index++;

		ladder = LadderManager.getLadder("BuildUHC", ArenaCommon.LadderType.RANKED_1V1);
		rating = ratings.get(ladder);
		this.addLadderToTab(tabList, row, ladder, rating, player, index);

		row++;
		index++;

		ladder = LadderManager.getLadder("GApple", ArenaCommon.LadderType.RANKED_1V1);
		rating = ratings.get(ladder);
		this.addLadderToTab(tabList, row, ladder, rating, player, index);

		row++;
		index++;

		ladder = LadderManager.getLadder("Iron", ArenaCommon.LadderType.RANKED_1V1);
		rating = ratings.get(ladder);
		this.addLadderToTab(tabList, row, ladder, rating, player, index);

		row++;
		index++;

		ladder = LadderManager.getLadder("Diamond", ArenaCommon.LadderType.RANKED_1V1);
		rating = ratings.get(ladder);
		this.addLadderToTab(tabList, row, ladder, rating, player, index);

		row++;
		index++;

		ladder = LadderManager.getLadder("Horse", ArenaCommon.LadderType.RANKED_1V1);
		rating = ratings.get(ladder);
		this.addLadderToTab(tabList, row, ladder, rating, player, index);

		// Send initial tab list packets
		tabList.updateInitial();

		// We want to do this at the very end because we want to set everything up first
		this.tabLists.put(player, tabList);
	}

	public void updateTablist(Player player, double globalRating, Map<Ladder, Double> ratings) {
		TabList tabList = ArenaLobbyTabListManager.getInstance().getTabList(player);

		String division = RatingUtil.getDivisionFromRating(globalRating);
		String divisionColor = RatingUtil.getDivisionColorFromRating(globalRating);

		tabList.setPosition(2, divisionColor + ChatColor.STRIKETHROUGH + "----------  ", false);
		tabList.setPosition(22, divisionColor + ChatColor.STRIKETHROUGH + "---------  -", false);
		tabList.setPosition(42, divisionColor + ChatColor.STRIKETHROUGH + "--------  --", false);

		int rankedLeft = RankedLeftManager.getNumberOfRankedMatchesLeft(player);
		int unrankedLeft = RankedLeftManager.getNumberOfUnRankedMatchesLeft(player);

		if (player.hasPermission(ArenaLobby.getUnlimitedRankedPermission())) {
			tabList.setPosition(4, "§9 Unlimited", false);
		} else if (rankedLeft > 0) {
			tabList.setPosition(4, "§9 " + rankedLeft + " ", false);
		} else {
			tabList.setPosition(4, "§9 None", false);
		}

		if (player.hasPermission(ArenaLobby.getUnlimitedRankedPermission())) {
			tabList.setPosition(7, "§9 Unlimited ", false);
		} else if (unrankedLeft > 0) {
			tabList.setPosition(7, "§9 " + unrankedLeft + "  ", false);
		} else {
			tabList.setPosition(7, "§9 None ", false);
		}

		// Get total unranked matches
		int totalUnrankedWins = RatingManager.getTotalUnrankedWins(player.getUniqueId());

		// Have they won 10 unranked matches?
		if (!player.hasPermission("badlion.donator") && totalUnrankedWins < RatingUtil.ARENA_UNRANKED_WINS_NEEDED_FOR_RANKED) {
			tabList.setPosition(23, "§6Unranked Wins:", false);
			tabList.setPosition(24, "§b " + totalUnrankedWins, false);
		} else {
			tabList.setPosition(23, "§6Ranked Played:", false);
			tabList.setPosition(24, "§b " + RatingManager.getTotalRankedMatchesPlayed(player.getUniqueId()), false);
		}

		tabList.setPosition(27, divisionColor + " " + division, false);

		Double rating;

		int row = 9;
		int index = 0;

		Ladder ladder = LadderManager.getLadder("Vanilla", ArenaCommon.LadderType.RANKED_1V1);
		rating = ratings.get(ladder);
		this.addLadderToTab(tabList, row, ladder, rating, player, index);

		row++;
		index++;

		ladder = LadderManager.getLadder("NoDebuff", ArenaCommon.LadderType.RANKED_1V1);
		rating = ratings.get(ladder);
		this.addLadderToTab(tabList, row, ladder, rating, player, index);

		row++;
		index++;

		ladder = LadderManager.getLadder("IronSoup", ArenaCommon.LadderType.RANKED_1V1);
		rating = ratings.get(ladder);
		this.addLadderToTab(tabList, row, ladder, rating, player, index);

		row++;
		index++;

		ladder = LadderManager.getLadder("Debuff", ArenaCommon.LadderType.RANKED_1V1);
		rating = ratings.get(ladder);
		this.addLadderToTab(tabList, row, ladder, rating, player, index);

		row++;
		index++;

		ladder = LadderManager.getLadder("SG", ArenaCommon.LadderType.RANKED_1V1);
		rating = ratings.get(ladder);
		this.addLadderToTab(tabList, row, ladder, rating, player, index);

		row++;
		index++;

		ladder = LadderManager.getLadder("Archer", ArenaCommon.LadderType.RANKED_1V1);
		rating = ratings.get(ladder);
		this.addLadderToTab(tabList, row, ladder, rating, player, index);

		row++;
		index++;

		ladder = LadderManager.getLadder("UHC", ArenaCommon.LadderType.RANKED_1V1);
		rating = ratings.get(ladder);
		this.addLadderToTab(tabList, row, ladder, rating, player, index);

		row++;
		index++;

		ladder = LadderManager.getLadder("BuildUHC", ArenaCommon.LadderType.RANKED_1V1);
		rating = ratings.get(ladder);
		this.addLadderToTab(tabList, row, ladder, rating, player, index);

		row++;
		index++;

		ladder = LadderManager.getLadder("GApple", ArenaCommon.LadderType.RANKED_1V1);
		rating = ratings.get(ladder);
		this.addLadderToTab(tabList, row, ladder, rating, player, index);

		row++;
		index++;

		ladder = LadderManager.getLadder("Iron", ArenaCommon.LadderType.RANKED_1V1);
		rating = ratings.get(ladder);
		this.addLadderToTab(tabList, row, ladder, rating, player, index);

		row++;
		index++;

		ladder = LadderManager.getLadder("Diamond", ArenaCommon.LadderType.RANKED_1V1);
		rating = ratings.get(ladder);
		this.addLadderToTab(tabList, row, ladder, rating, player, index);

		row++;
		index++;

		ladder = LadderManager.getLadder("Horse", ArenaCommon.LadderType.RANKED_1V1);
		rating = ratings.get(ladder);
		this.addLadderToTab(tabList, row, ladder, rating, player, index);
		// Send tab list packets
		tabList.update(true);
	}


	public String getHiddenString(int index) {
		char hidden1 = (char) 0x1f427;
		char hidden2 = (char) 0x26c7;
		char hidden3 = (char) 0x26c8;
		char hidden4 = ' ';

		switch (index) {
			case 0:
				return hidden1 + "";
			case 1:
				return hidden2 + "";
			case 2:
				return hidden3 + "";
			case 3:
				return hidden4 + "";
			case 4:
				return hidden1 + "" + hidden1;
			case 5:
				return hidden1 + "" + hidden2;
			case 6:
				return hidden1 + "" + hidden3;
			case 7:
				return hidden1 + "" + hidden4;
			case 8:
				return hidden2 + "" + hidden1;
			case 9:
				return hidden2 + "" + hidden2;
			case 10:
				return hidden2 + "" + hidden3;
			case 11:
				return hidden2 + "" + hidden4;
			case 12:
				return hidden3 + "" + hidden1;
			case 13:
				return hidden3 + "" + hidden2;
			case 14:
				return hidden3 + "" + hidden3;
			case 15:
				return hidden3 + "" + hidden4;
			case 16:
				return hidden4 + "" + hidden1;
			case 17:
				return hidden4 + "" + hidden2;
			case 18:
				return hidden4 + "" + hidden3;
			case 19:
				return hidden4 + "" + hidden4;
			default:
				return "";
		}
	}

	public void addLadderToTab(TabList tabList, int row, Ladder ladder, double rating, Player player, int index) {
		tabList.setPosition(row, "§6" + ladder.getKitRuleSet().getName(), false);
		if (RatingManager.getMatchesPlayed(player.getUniqueId(), ladder) < RatingUtil.ARENA_PLACEMENT_MATCHES) {
			rating = -1;
		}
		int points = RatingUtil.Rank.getPoints(rating);
		RatingUtil.Rank rank = RatingUtil.Rank.getRankByElo(rating);
		tabList.setPosition(row + 20, rank.getChatColor() + rank.getName() + this.getHiddenString(index), false);
		tabList.setPosition(row + 40, RatingUtil.Rank.getChatColorFromPoints(points).toString() + RatingUtil.Rank.getPoints(rating) + ChatColor.GRAY + "/1000" + this.getHiddenString(index), false);
	}

}
