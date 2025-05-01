package net.badlion.potionpvptablist;

import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.ladders.Ladder;
import net.badlion.potpvp.managers.RankedLeftManager;
import net.badlion.potpvp.managers.RatingManager;
import net.badlion.tablist.TabList;
import net.badlion.tablist.TabListManager;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PotionTabListManager extends TabListManager {

	public PotionTabListManager() {
		super(PotionTabList.getInstance());
	}

	public void createTabList(Player player, Map<Ladder, Integer> ratings) {
		TabList tabList = new TabList(player, 1, 60);

		// Set up strings
		Integer rating, rating2;
		tabList.setPosition(1, "§cRatings:", false);

		if (Bukkit.getSpigotJarVersion() == Server.SERVER_VERSION.V1_9) {
			tabList.setPosition(2, "§6Archer", false);
			rating = ratings.get(Ladder.getLadder("Archer", Ladder.LadderType.OneVsOneRanked));
			if (rating == null) rating = RatingManager.DEFAULT_RATING;
			tabList.setPosition(3, "§b " + rating, false);

			tabList.setPosition(4, "§6BuildUHC", false);
			rating = ratings.get(Ladder.getLadder("BuildUHC", Ladder.LadderType.OneVsOneRanked));
			if (rating == null) rating = RatingManager.DEFAULT_RATING;
			tabList.setPosition(5, "§b " + rating + " ", false);

			tabList.setPosition(6, "§6IronBuildUHC", false);
			rating = ratings.get(Ladder.getLadder("IronBuildUHC", Ladder.LadderType.OneVsOneRanked));
			if (rating == null) rating = RatingManager.DEFAULT_RATING;
			tabList.setPosition(7, "§b " + rating + "  ", false);

			tabList.setPosition(8, "§6Diamond", false);
			rating = ratings.get(Ladder.getLadder("Diamond", Ladder.LadderType.OneVsOneRanked));
			if (rating == null) rating = RatingManager.DEFAULT_RATING;
			tabList.setPosition(9, "§b " + rating + "   ", false);

			tabList.setPosition(10, "§6GApple", false);
			rating = ratings.get(Ladder.getLadder("GApple", Ladder.LadderType.OneVsOneRanked));
			if (rating == null) rating = RatingManager.DEFAULT_RATING;
			tabList.setPosition(11, "§b " + rating + "    ", false);

			tabList.setPosition(12, "§6Iron", false);
			rating = ratings.get(Ladder.getLadder("Iron", Ladder.LadderType.OneVsOneRanked));
			if (rating == null) rating = RatingManager.DEFAULT_RATING;
			tabList.setPosition(13, "§b " + rating + "     ", false);

			tabList.setPosition(14, "§6Kohi", false);
			rating = ratings.get(Ladder.getLadder("Kohi", Ladder.LadderType.OneVsOneRanked));
			if (rating == null) rating = RatingManager.DEFAULT_RATING;
			tabList.setPosition(15, "§b " + rating + "      ", false);

			tabList.setPosition(16, "§6SG", false);
			rating = ratings.get(Ladder.getLadder("SG", Ladder.LadderType.OneVsOneRanked));
			if (rating == null) rating = RatingManager.DEFAULT_RATING;
			tabList.setPosition(17, "§b " + rating  + "       ", false);
		} else {
			tabList.setPosition(2, "§6Vanilla", false);
			rating = ratings.get(Ladder.getLadder("Vanilla", Ladder.LadderType.OneVsOneRanked));
			if (rating == null) rating = RatingManager.DEFAULT_RATING;
			tabList.setPosition(3, "§b " + rating, false);

			tabList.setPosition(4, "§6No Debuff", false);
			rating = ratings.get(Ladder.getLadder("NoDebuff", Ladder.LadderType.OneVsOneRanked));
			if (rating == null) rating = RatingManager.DEFAULT_RATING;
			tabList.setPosition(5, "§b " + rating + " ", false);

			tabList.setPosition(6, "§6Iron Soup", false);
			rating = ratings.get(Ladder.getLadder("IronSoup", Ladder.LadderType.OneVsOneRanked));
			if (rating == null) rating = RatingManager.DEFAULT_RATING;
			tabList.setPosition(7, "§b " + rating + "  ", false);

			tabList.setPosition(8, "§6SW - IronBuild", false);
			rating = ratings.get(Ladder.getLadder("SkyWars", Ladder.LadderType.OneVsOneRanked));
			if (rating == null) rating = RatingManager.DEFAULT_RATING;
			rating2 = ratings.get(Ladder.getLadder("IronBuildUHC", Ladder.LadderType.OneVsOneRanked));
			if (rating2 == null) rating2 = RatingManager.DEFAULT_RATING;
			tabList.setPosition(9, "§b " + rating + " - " + rating2, false);

			tabList.setPosition(10, "§6Kohi - AdvUHC", false);
			rating = ratings.get(Ladder.getLadder("Kohi", Ladder.LadderType.OneVsOneRanked));
			if (rating == null) rating = RatingManager.DEFAULT_RATING;
			rating2 = ratings.get(Ladder.getLadder("AdvancedUHC", Ladder.LadderType.OneVsOneRanked));
			if (rating2 == null) rating2 = RatingManager.DEFAULT_RATING;
			tabList.setPosition(11, "§b " + rating + " - " + rating2 + " ", false);

			tabList.setPosition(12, "§6SG - Archer", false);
			rating = ratings.get(Ladder.getLadder("SG", Ladder.LadderType.OneVsOneRanked));
			if (rating == null) rating = RatingManager.DEFAULT_RATING;
			rating2 = ratings.get(Ladder.getLadder("Archer", Ladder.LadderType.OneVsOneRanked));
			if (rating2 == null) rating2 = RatingManager.DEFAULT_RATING;
			tabList.setPosition(13, "§b " + rating + " - " + rating2 + "  ", false);

			tabList.setPosition(14, "§6UHC - BuildUHC", false);
			rating = ratings.get(Ladder.getLadder("UHC", Ladder.LadderType.OneVsOneRanked));
			if (rating == null) rating = RatingManager.DEFAULT_RATING;
			rating2 = ratings.get(Ladder.getLadder("BuildUHC", Ladder.LadderType.OneVsOneRanked));
			if (rating2 == null) rating2 = RatingManager.DEFAULT_RATING;
			tabList.setPosition(15, "§b " + rating + " - " + rating2 + (char) 0x26c7, false);

			tabList.setPosition(16, "§6Horse - GApple", false);
			rating = ratings.get(Ladder.getLadder("Horse", Ladder.LadderType.OneVsOneRanked));
			if (rating == null) rating = RatingManager.DEFAULT_RATING;
			rating2 = ratings.get(Ladder.getLadder("GApple", Ladder.LadderType.OneVsOneRanked));
			if (rating2 == null) rating2 = RatingManager.DEFAULT_RATING;
			tabList.setPosition(17, "§b " + rating + " - " + rating2 + (char) 0x26c7 + (char) 0x26c7, false);

			tabList.setPosition(18, "§6Iron - Diamond", false);
			rating = ratings.get(Ladder.getLadder("Iron", Ladder.LadderType.OneVsOneRanked));
			if (rating == null) rating = RatingManager.DEFAULT_RATING;
			rating2 = ratings.get(Ladder.getLadder("Diamond", Ladder.LadderType.OneVsOneRanked));
			if (rating2 == null) rating2 = RatingManager.DEFAULT_RATING;
			tabList.setPosition(19, "§b " + rating + " - " + rating2 + (char) 0x1f427, false);
		}

		tabList.setPosition(21, "§cServer Info:", false);
		tabList.setPosition(22, "§9Players - " + PotionTabList.getInstance().getServer().getOnlinePlayers().size(), false);

		tabList.setPosition(24, "§9na.badlion.net", false);
        tabList.setPosition(25, "naw.badlion.net", false);
        tabList.setPosition(26, "§9eu.badlion.net", false);
        tabList.setPosition(27, "au.badlion.net", false);

		tabList.setPosition(30, "§6§lBadlion PVP", false);
		tabList.setPosition(31, "§6§lBadlion PVP ", false);

		tabList.setPosition(39, "§cDonation Info:", false);
		if (player.hasPermission("badlion.kittrial")) {
			tabList.setPosition(40, "§9 Staff Member", false);
		} else if (player.hasPermission("badlion.famousplus")) {
			tabList.setPosition(40, "§9 Famous §l+", false);
		} else if (player.hasPermission("badlion.famous")) {
			tabList.setPosition(40, "§9 Famous", false);
		} else if (player.hasPermission("badlion.lion")) {
			tabList.setPosition(40, "§9 Lion", false);
		} else if (player.hasPermission("badlion.donatorplus")) {
			tabList.setPosition(40, "§9 Donator §l+", false);
		} else if (player.hasPermission("badlion.donator")) {
			tabList.setPosition(40, "§9 Donator", false);
		} else {
			tabList.setPosition(40, "§9 N/A", false);
		}

		tabList.setPosition(41, "§cStaff Online:", false);
		List<String> staff = Gberry.plugin.getListCommandHandler().getStaff();
		for (int x = 0; x < 17; x++) {
			String name = "";

			if (x < staff.size()) {
				name = staff.get(x);
			}

			tabList.setPosition(42 + x, name, false);
		}

		int rankedLeft = RankedLeftManager.getNumberOfRankedMatchesLeft(player);

		tabList.setPosition(59, "§cRanked Left:", false);
		if (player.hasPermission(PotPvP.getUnlimitedRankedPermission())) {
			tabList.setPosition(60, "§9 Unlimited", false);
		} else if (rankedLeft > 0) {
			tabList.setPosition(60, "§9 " + rankedLeft, false);
		} else {
			tabList.setPosition(60, "§9 None", false);
		}

		// Send initial tab list packets
		tabList.updateInitial();

		// We want to do this at the very end because we want to set everything up first
		this.tabLists.put(player, tabList);
	}

	public void updateStaffList() {
		List<String> staff = Gberry.plugin.getListCommandHandler().getStaff();

		Map<Integer, String> tabChanges = new HashMap<>();

		// Update player count too to fix some random race condition
		tabChanges.put(22, "§9Players - " + Bukkit.getOnlinePlayers().size());

		for (int x = 0; x < 16; x++) {
			String name = "";

			if (x < staff.size()) {
				name = staff.get(x);

				//System.out.println(staff.size() + x);
			}

			tabChanges.put(42 + x, name);
		}

		PotionTabListManager.this.setAllTabListPositions(tabChanges, true);
	}

}
