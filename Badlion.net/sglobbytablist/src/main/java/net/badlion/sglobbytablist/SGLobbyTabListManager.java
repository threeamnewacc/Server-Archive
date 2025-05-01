package net.badlion.sglobbytablist;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.managers.UserDataManager;
import net.badlion.gberry.utils.RatingUtil;
import net.badlion.sglobby.FakeSGMiniStatsPlayer;
import net.badlion.sglobby.managers.RatingManager;
import net.badlion.tablist.TabList;
import net.badlion.tablist.TabListManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

public class SGLobbyTabListManager extends TabListManager {

	public SGLobbyTabListManager() {
		super(SGLobbyTabList.getInstance());
	}

	public void createTabList(Player player) {
		TabList tabList = new TabList(player, 1, 60);

		JSONObject sgSettings = UserDataManager.getUserData(player).getSGSettings();
		FakeSGMiniStatsPlayer stats = RatingManager.getPlayerMiniStats(player.getUniqueId());

		tabList.setPosition(1, "§6§lBadlion SG", false);
		tabList.setPosition(21, "§6§lBadlion SG ", false);
		tabList.setPosition(41, "§6§lBadlion SG  ", false);

		int rating = RatingManager.getPlayerRating(player.getUniqueId());
		int points = RatingUtil.Rank.getPoints(rating);
		String division = RatingUtil.getDivisionFromRating(rating);
		String divisionColor = RatingUtil.getDivisionColorFromRating(rating);

		// Are they hiding their rating?
		if ((boolean) sgSettings.get("rating_visibility")) {
			// Has the player not finished their placement matches to show their rating?
			if (stats.getNumberOfGamesPlayed() < RatingUtil.SG_PLACEMENT_MATCHES) {
				points = -1;

				division = ChatColor.WHITE + "[Unranked]";
				divisionColor = ChatColor.WHITE.toString();
			}
		} else {
			points = -2;

			division = "§oHidden";
			divisionColor = ChatColor.WHITE.toString();
		}

		tabList.setPosition(2, divisionColor + ChatColor.STRIKETHROUGH + "----------  ", false);
		tabList.setPosition(22, divisionColor + ChatColor.STRIKETHROUGH + "---------  -", false);
		tabList.setPosition(42, divisionColor + ChatColor.STRIKETHROUGH + "--------  --", false);

		String gamesWon = "§oHidden";
		String totalKills = "§oHidden";
		String highestKillStreak = "§oHidden";

		// Are they showing their stats?
		if ((boolean) sgSettings.get("stats_visibility")) {
			totalKills = stats.getKills() + "";
			gamesWon = stats.getWins() + "";
			highestKillStreak = stats.getHighestKillStreak() + "";
		}

		tabList.setPosition(4, "§6Games Won:", false);
		tabList.setPosition(5, "§b " + gamesWon + " ", false);

		tabList.setPosition(24, "§6Total Kills:", false);
		tabList.setPosition(25, "§b " + totalKills + "  ", false);

		tabList.setPosition(44, "§cServer:", false);
		tabList.setPosition(45, "§9 SG Lobby " + Gberry.plugin.getServerNumber(), false);

		tabList.setPosition(7, "§6#1 Killstreak:", false);
		tabList.setPosition(8, "§b " + highestKillStreak + " ", false);

		tabList.setPosition(27, "§cRank:", false);
		tabList.setPosition(28, " " + division, false);

		tabList.setPosition(30, "§cPoints:", false);
		if (points == -1) { // Unranked
			tabList.setPosition(31, " " + ChatColor.WHITE + "N/A", false);
		} else if (points == -2) { // Hidden
			tabList.setPosition(31, ChatColor.WHITE + " §oHidden", false);
		} else {
			tabList.setPosition(31, "  " + RatingUtil.Rank.getChatColorFromPoints(points).toString() + RatingUtil.Rank.getPoints(rating) + ChatColor.GRAY + "/1000", false);
		}

		tabList.setPosition(47, "§cDonation Info:", false);
		if (player.hasPermission("badlion.sgtrial")) {
			tabList.setPosition(48, "§9 Staff Member", false);
		} else if (player.hasPermission("badlion.famousplus")) {
			tabList.setPosition(48, "§9 Famous §l+", false);
		} else if (player.hasPermission("badlion.famous")) {
			tabList.setPosition(48, "§9 Famous", false);
		} else if (player.hasPermission("badlion.lion")) {
			tabList.setPosition(48, "§9 Lion", false);
		} else if (player.hasPermission("badlion.donatorplus")) {
			tabList.setPosition(48, "§9 Donator §l+", false);
		} else if (player.hasPermission("badlion.donator")) {
			tabList.setPosition(48, "§9 Donator", false);
		} else {
			tabList.setPosition(48, "§9 N/A", false);
		}

		// Send initial tab list packets
		tabList.updateInitial();

		// We want to do this at the very end because we want to set everything up first
		this.tabLists.put(player, tabList);
	}

}
