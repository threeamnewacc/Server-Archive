package net.badlion.sglobbytablist.listeners;

import net.badlion.gberry.utils.RatingUtil;
import net.badlion.sglobby.FakeSGMiniStatsPlayer;
import net.badlion.sglobby.bukkitevents.SGSettingsChangeEvent;
import net.badlion.sglobby.managers.RatingManager;
import net.badlion.tablist.TabList;
import net.badlion.tablist.TabListManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CustomEventListener implements Listener {

	@EventHandler
	public void onSGSettingsChangeEvent(SGSettingsChangeEvent event) {
		Player player = event.getPlayer();

		TabList tabList = TabListManager.getInstance().getTabList(player);

		FakeSGMiniStatsPlayer stats = RatingManager.getPlayerMiniStats(player.getUniqueId());

		int rating = RatingManager.getPlayerRating(player.getUniqueId());
		int points = RatingUtil.Rank.getPoints(rating);
		String division = RatingUtil.getDivisionFromRating(rating);
		String divisionColor = RatingUtil.getDivisionColorFromRating(rating);

		// Are they hiding their rating?
		if (event.isRatingVisibile()) {
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

		tabList.setPosition(2, divisionColor + ChatColor.STRIKETHROUGH + "----------  ", true);
		tabList.setPosition(22, divisionColor + ChatColor.STRIKETHROUGH + "---------  -", true);
		tabList.setPosition(42, divisionColor + ChatColor.STRIKETHROUGH + "--------  --", true);

		String gamesWon = "§oHidden";
		String totalKills = "§oHidden";
		String highestKillStreak = "§oHidden";

		// Are they showing their stats?
		if (event.areStatsVisible()) {
			totalKills = stats.getKills() + "";
			gamesWon = stats.getWins() + "";
			highestKillStreak = stats.getHighestKillStreak() + "";
		}

		tabList.setPosition(4, "§6Games Won:", true);
		tabList.setPosition(5, "§b " + gamesWon + " ", true);

		tabList.setPosition(24, "§6Total Kills:", true);
		tabList.setPosition(25, "§b " + totalKills + "  ", true);

		tabList.setPosition(7, "§6#1 Killstreak:", true);
		tabList.setPosition(8, "§b " + highestKillStreak, true);

		tabList.setPosition(27, "§cRank:", true);
		tabList.setPosition(28, " " + division, true);

		tabList.setPosition(30, "§cPoints:", true);
		if (points == -1) { // Unranked
			tabList.setPosition(31, " " + ChatColor.WHITE + "N/A", true);
		} else if (points == -2) { // Hidden
			tabList.setPosition(31, ChatColor.WHITE + " §oHidden", true);
		} else {
			tabList.setPosition(31, RatingUtil.Rank.getChatColorFromPoints(points).toString() + RatingUtil.Rank.getPoints(rating) + ChatColor.GRAY + "/1000", true);
		}

		tabList.update(false);
	}

}
