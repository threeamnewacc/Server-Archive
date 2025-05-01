package net.badlion.potionpvptablist.listeners;


import net.badlion.potionpvptablist.PotionTabList;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.bukkitevents.PlayerRatingChangeEvent;
import net.badlion.potpvp.bukkitevents.RankedLeftChangeEvent;
import net.badlion.potpvp.bukkitevents.RatingRetrievedEvent;
import net.badlion.potpvp.exceptions.NoRatingFoundException;
import net.badlion.potpvp.ladders.Ladder;
import net.badlion.potpvp.managers.RatingManager;
import net.badlion.tablist.TabList;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CustomEventListener implements Listener {

	@EventHandler
	public void ratingRetrievedEvent(RatingRetrievedEvent event) {
		// Create the tab list
        // 3/1/15 Here lies the double player count bug. Two tab lists are created, 3 seconds apart 2 sets of packets are sent, the first never being flushed
        // It just so happens that a lot of the packets are identical so they flush later on properly.
		Player player = Bukkit.getPlayer(event.getUuid());
		if (player != null) {
			if (PotionTabList.getInstance().getPotionTabListManager().getTabList(player) == null) {
				PotionTabList.getInstance().getPotionTabListManager().createTabList(player, event.getRatings());
			}
		}
	}

	@EventHandler
	public void rankedLeftChangeEvent(RankedLeftChangeEvent event) {
        if (event.getPlayer().hasPermission("badlion.donator")) {
            return;
        }

		TabList tabList = PotionTabList.getInstance().getPotionTabListManager().getTabList(event.getPlayer());

		if (tabList != null) {
			if (event.getRankedLeft() > 0) {
				tabList.setPosition(60, "§9 " + event.getRankedLeft(), true);
			} else {
				tabList.setPosition(60, "§9 None", true);
			}

			// Update tab list
			tabList.update(false);
		}
	}

	/*@EventHandler
	public void donatorRankChangeEvent(DonatorRankChangeEvent event) {
		Player player = event.getPlayer();

		// What if they donate while offline?
		if (player != null && player.isOnline()) {
			TabList tabList = PotionTabList.getInstance().getPotionTabListManager().getTabList(player);

			if (tabList != null) {
				if (player.hasPermission("badlion.kittrial")) {
					tabList.setPosition(40, "§9 Staff Member", true);
				} else if (player.hasPermission("badlion.famous")) {
					tabList.setPosition(40, "§9 Famous", true);
				} else if (player.hasPermission("badlion.donatorplus")) {
					tabList.setPosition(40, "§9 Donator §l+", true);
				} else if (player.hasPermission("badlion.donator")) {
					tabList.setPosition(40, "§9 Donator", true);
				} else {
					tabList.setPosition(40, "§9 N/A", true);
				}

				// Update tab list
				tabList.update(false);
			}
		}
	}*/

	@EventHandler
	public void playerEloChangeEvent(PlayerRatingChangeEvent event) {
		TabList tabList = PotionTabList.getInstance().getPotionTabListManager().getTabList(event.getPlayer());

		try {
			if (tabList != null) {
				if (Bukkit.getSpigotJarVersion() == Server.SERVER_VERSION.V1_9) {
					switch (event.getLadder()) {
						case "Archer":
							tabList.setPosition(3, "§b " + event.getNewRating(), true);
							break;
						case "BuildUHC":
							tabList.setPosition(5, "§b " + event.getNewRating() + " ", true);
							break;
						case "IronBuildUHC":
							tabList.setPosition(7, "§b " + event.getNewRating() + "  ", true);
							break;
						case "Diamond":
							tabList.setPosition(9, "§b " + event.getNewRating() + "   ", true);
							break;
						case "GApple":
							tabList.setPosition(11, "§b " + event.getNewRating() + "    ", true);
							break;
						case "Iron":
							tabList.setPosition(13, "§b " + event.getNewRating() + "     ", true);
							break;
						case "Kohi":
							tabList.setPosition(15, "§b " + event.getNewRating() + "      ", true);
							break;
						case "SG":
							tabList.setPosition(17, "§b " + event.getNewRating() + "       ", true);
							break;
					}
				} else {
					switch (event.getLadder()) {
						case "Vanilla":
							tabList.setPosition(3, "§b " + event.getNewRating(), true);
							break;
						case "NoDebuff":
							tabList.setPosition(5, "§b " + event.getNewRating() + " ", true);
							break;
						case "IronSoup":
							tabList.setPosition(7, "§b " + event.getNewRating() + "  ", true);
							break;
						case "SkyWars":
							tabList.setPosition(9, "§b " + event.getNewRating() + " - "
									+ RatingManager.getGroupRating(PotPvP.getInstance().getPlayerGroup(event.getPlayer()),
									Ladder.getLadder("IronBuildUHC", Ladder.LadderType.OneVsOneRanked)), true);
							break;
						case "IronBuildUHC":
							tabList.setPosition(9, "§b " + RatingManager.getGroupRating(PotPvP.getInstance().getPlayerGroup(event.getPlayer()),
									Ladder.getLadder("SkyWars", Ladder.LadderType.OneVsOneRanked)) + " - " + event.getNewRating(), true);
							break;
						case "Kohi":
							tabList.setPosition(11, "§b " + event.getNewRating() + " - "
															+ RatingManager.getGroupRating(PotPvP.getInstance().getPlayerGroup(event.getPlayer()),
																						   Ladder.getLadder("AdvancedUHC", Ladder.LadderType.OneVsOneRanked)) + " ", true);
							break;
						case "AdvancedUHC":
							tabList.setPosition(11, "§b " + RatingManager.getGroupRating(PotPvP.getInstance().getPlayerGroup(event.getPlayer()),
																						 Ladder.getLadder("Kohi", Ladder.LadderType.OneVsOneRanked)) + " - " + event.getNewRating() + " ", true);
							break;
						case "SG":
							tabList.setPosition(13, "§b " + event.getNewRating() + " - "
															+ RatingManager.getGroupRating(PotPvP.getInstance().getPlayerGroup(event.getPlayer()),
																						   Ladder.getLadder("Archer", Ladder.LadderType.OneVsOneRanked)) + "  ", true);
							break;
						case "Archer":
							tabList.setPosition(13, "§b " + RatingManager.getGroupRating(PotPvP.getInstance().getPlayerGroup(event.getPlayer()),
																						 Ladder.getLadder("SG", Ladder.LadderType.OneVsOneRanked)) + " - " + event.getNewRating() + "  ", true);
							break;
						case "UHC":
							tabList.setPosition(15, "§b " + event.getNewRating() + " - "
															+ RatingManager.getGroupRating(PotPvP.getInstance().getPlayerGroup(event.getPlayer()),
																						   Ladder.getLadder("BuildUHC", Ladder.LadderType.OneVsOneRanked))  + (char) 0x26c7, true);
							break;
						case "BuildUHC":
							tabList.setPosition(15, "§b " + RatingManager.getGroupRating(PotPvP.getInstance().getPlayerGroup(event.getPlayer()),
																						 Ladder.getLadder("UHC", Ladder.LadderType.OneVsOneRanked)) + " - " + event.getNewRating()  + (char) 0x26c7, true);
							break;
						case "Horse":
							tabList.setPosition(17, "§b " + event.getNewRating() + " - "
															+ RatingManager.getGroupRating(PotPvP.getInstance().getPlayerGroup(event.getPlayer()),
																						   Ladder.getLadder("GApple", Ladder.LadderType.OneVsOneRanked)) + (char) 0x26c7 + (char) 0x26c7, true);
							break;
						case "GApple":
							tabList.setPosition(17, "§b " + RatingManager.getGroupRating(PotPvP.getInstance().getPlayerGroup(event.getPlayer()),
																						 Ladder.getLadder("Horse", Ladder.LadderType.OneVsOneRanked)) + " - " + event.getNewRating() + (char) 0x26c7 + (char) 0x26c7, true);
							break;
						case "Iron":
							tabList.setPosition(19, "§b " + event.getNewRating() + " - "
															+ RatingManager.getGroupRating(PotPvP.getInstance().getPlayerGroup(event.getPlayer()),
																						   Ladder.getLadder("Diamond", Ladder.LadderType.OneVsOneRanked)) + (char) 0x1f427, true);
							break;
						case "Diamond":
							tabList.setPosition(19, "§b " + RatingManager.getGroupRating(PotPvP.getInstance().getPlayerGroup(event.getPlayer()),
																						 Ladder.getLadder("Iron", Ladder.LadderType.OneVsOneRanked)) + " - " + event.getNewRating() + (char) 0x1f427 + (char) 0x1f427, true);
							break;
					}
				}

				// Update
				tabList.update(false);
			}
		} catch (NoRatingFoundException e) {
			// Pass
		}
	}

}
