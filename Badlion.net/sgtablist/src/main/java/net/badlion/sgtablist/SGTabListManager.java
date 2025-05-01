package net.badlion.sgtablist;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.managers.UserDataManager;
import net.badlion.gberry.utils.RatingUtil;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGGame;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.survivalgames.SGPlayer;
import net.badlion.survivalgames.SurvivalGames;
import net.badlion.tablist.TabList;
import net.badlion.tablist.TabListManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SGTabListManager extends TabListManager {

	public SGTabListManager() {
		super(SGTabList.getInstance());
	}

	public void createTabList(Player player) {
		SGPlayer sgPlayer = (SGPlayer) MPGPlayerManager.getMPGPlayer(player);
		JSONObject sgSettings = UserDataManager.getUserData(player).getSGSettings();

		TabList tabList = new TabList(player, 1, 60);

		tabList.setPosition(1, "§6§lBadlion SG", false);
		tabList.setPosition(21, "§6§lBadlion SG ", false);
		tabList.setPosition(41, "§6§lBadlion SG  ", false);

		int rating = SurvivalGames.getInstance().getSGGame().getPlayerRating(player.getUniqueId());
		int points = RatingUtil.Rank.getPoints(rating);
		String division = RatingUtil.getDivisionFromRating(rating);
		String divisionColor = RatingUtil.getDivisionColorFromRating(rating);

		// Are they hiding their rating?
		if ((boolean) sgSettings.get("rating_visibility")) {
			// Has the player not finished their placement matches to show their rating?
			if (sgPlayer.getGamesPlayed() < RatingUtil.SG_PLACEMENT_MATCHES) {
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

		String worldName = SurvivalGames.getInstance().getSGGame().getWorld().getGWorld().getNiceWorldName();
		String worldName2 = "";
		if (worldName.length() > 14) {
			worldName2 = worldName.substring(14);
			worldName = worldName.substring(0, 14);
		}

		if (worldName2.length() > 14) {
			worldName2 = worldName2.substring(0, 14);
		}

		tabList.setPosition(3, "§bMap:", false);
		tabList.setPosition(23, "§e" + worldName, false);
		tabList.setPosition(43, "§e" + worldName2, false);

		tabList.setPosition(4, "§bAuthor:", false);

		String authorName = SurvivalGames.getInstance().getSGGame().getWorld().getGWorld().getAuthor();
		String[] names = authorName.split(", ");
		for (int j = 0; j < names.length; j++) {
			// We only have space for 4 names on the tab list
			if (j > 3) break;

			String truncatedName = names[j];

			// Is this the last name we're showing?
			if (j == names.length - 1) {
				// Don't need space for a comma
				if (names[j].length() > 14) {
					truncatedName = names[j].substring(0, 14);
				}
			} else {
				truncatedName += ",";

				if (names[j].length() > 13) {
					truncatedName = names[j].substring(0, 13) + ",";
				}
			}

			// Figure out where this name goes
			switch (j) {
				case 0:
					tabList.setPosition(24, "§e" + truncatedName, false);
					break;
				case 1:
					tabList.setPosition(44, "§e" + truncatedName, false);
					break;
				case 2:
					tabList.setPosition(25, "§e" + truncatedName, false);
					break;
				case 3:
					tabList.setPosition(45, "§e" + truncatedName, false);
					break;
			}
		}

		String gamesWon = "§oHidden";
		String totalKills = "§oHidden";

		// Are they showing their stats?
		if ((boolean) sgSettings.get("stats_visibility")) {
			totalKills = sgPlayer.getTotalKills() + "";
			gamesWon = sgPlayer.getGamesWon() + "";
		}

		tabList.setPosition(6, "§6Games Won:", false);
		tabList.setPosition(7, "§b " + gamesWon + " ", false);

		tabList.setPosition(26, "§6Total Kills:", false);
		tabList.setPosition(27, "§b " + totalKills + "  ", false);

		tabList.setPosition(46, "§cServer:", false);
		tabList.setPosition(47, "§9 SG " + Gberry.plugin.getServerNumber(), false);

		tabList.setPosition(9, "§cRank:", false);
		tabList.setPosition(10, " " + division, false);

		tabList.setPosition(29, "§cPoints:", false);
		if (points == -1) { // Unranked
			tabList.setPosition(30, " " + ChatColor.WHITE + "N/A", false);
		} else if (points == -2) { // Hidden
			tabList.setPosition(30, ChatColor.WHITE + " §oHidden", false);
		} else {
			tabList.setPosition(30, "  " + RatingUtil.Rank.getChatColorFromPoints(points).toString() + RatingUtil.Rank.getPoints(rating) + ChatColor.GRAY + "/1000", false);
		}

		tabList.setPosition(49, "§cDonation Info:", false);
		if (player.hasPermission("badlion.sgtrial")) {
			tabList.setPosition(50, "§9 Staff Member", false);
		} else if (player.hasPermission("badlion.famousplus")) {
			tabList.setPosition(50, "§9 Famous §l+", false);
		} else if (player.hasPermission("badlion.famous")) {
			tabList.setPosition(50, "§9 Famous", false);
		} else if (player.hasPermission("badlion.lion")) {
			tabList.setPosition(50, "§9 Lion", false);
		} else if (player.hasPermission("badlion.donatorplus")) {
			tabList.setPosition(50, "§9 Donator §l+", false);
		} else if (player.hasPermission("badlion.donator")) {
			tabList.setPosition(50, "§9 Donator", false);
		} else {
			tabList.setPosition(50, "§9 N/A", false);
		}

		tabList.setPosition(12, ChatColor.GREEN.toString() + ChatColor.STRIKETHROUGH + "----------- ", false);
		tabList.setPosition(32, "§a --- Alive ---", false);
		tabList.setPosition(52, ChatColor.GREEN.toString() + ChatColor.STRIKETHROUGH + "---------- -", false);

		int counter = 0;

		// Has the game started?
		if (MPG.getInstance().getMPGGame().getGameState().ordinal() > MPGGame.GameState.GAME_COUNTDOWN.ordinal()) {
			// Fill alive players
			for (MPGPlayer mpgPlayer2 : MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.PLAYER)) {
				int j = counter + 13;

				if (counter > 15) j += 24;
				else if (counter > 7) j += 12;

				Player player2 = mpgPlayer2.getPlayer();

				// This means a player logged off before the countdown
				if (player2 == null) continue;

				String name = mpgPlayer2.getPlayer().getDisguisedName();

				if (name.length() > 14) {
					name = name.substring(0, 14);
				}

				// Is this a party game?
				if (MPG.GAME_TYPE == MPG.GameType.PARTY) {
					// Use the player's team color
					tabList.setPosition(j, mpgPlayer2.getTeam().getColor() + name, false);
				} else {
					tabList.setPosition(j, ChatColor.GREEN + name, false);
				}

				counter++;
			}

			// Fill blank
			for (int i = counter; i < 24; i++) {
				int j = i + 13;

				if (i > 15) j += 24;
				else if (i > 7) j += 12;

				tabList.setPosition(j, "", false);
			}
		}

		// Send initial tab list packets
		tabList.updateInitial();

		// We want to do this at the very end because we want to set everything up first
		this.tabLists.put(player, tabList);
	}

	public void updatePlayerList() {
		// Has the game not started?
		if (MPG.getInstance().getMPGGame().getGameState().ordinal() <= MPGGame.GameState.GAME_COUNTDOWN.ordinal()) return;

		Map<Integer, String> tabChanges = new HashMap<>();

		int counter = 0;

		// Fill alive players
		for (MPGPlayer mpgPlayer : MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.PLAYER)) {
			Player player = mpgPlayer.getPlayer();

			if (player == null) {
				continue;
			}

			int j = counter + 13;

			if (counter > 15) j += 24;
			else if (counter > 7) j += 12;

			String name = player.getDisguisedName();

			if (name.length() > 14) {
				name = name.substring(0, 14);
			}

			// Is this a party game?
			if (MPG.GAME_TYPE == MPG.GameType.PARTY) {
				// Use the player's team color
				tabChanges.put(j, mpgPlayer.getTeam().getColor() + name);
			} else {
				tabChanges.put(j, ChatColor.GREEN + name);
			}

			counter++;
		}

		// Fill blank
		for (int i = counter; i < 24; i++) {
			int j = i + 13;

			if (i > 15) j += 24;
			else if (i > 7) j += 12;

			tabChanges.put(j, "");
		}

		this.setAllTabListPositions(tabChanges);
	}

}
