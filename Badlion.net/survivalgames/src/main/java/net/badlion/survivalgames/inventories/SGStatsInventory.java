package net.badlion.survivalgames.inventories;

import net.badlion.gberry.managers.UserDataManager;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.gberry.utils.RatingUtil;
import net.badlion.ministats.MiniStatsPlayer;
import net.badlion.mpg.inventories.StatsInventory;
import net.badlion.smellyinventory.SmellyInventory;
import net.badlion.survivalgames.SGMiniStatsPlayer;
import net.badlion.survivalgames.SurvivalGames;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.util.UUID;

public class SGStatsInventory extends StatsInventory {

	@Override
	public void openPlayerStatsInventory(Player player, UUID uuid, String username, MiniStatsPlayer miniStatsPlayer) {
		SGMiniStatsPlayer sgMiniStatsPlayer = (SGMiniStatsPlayer) miniStatsPlayer;

		JSONObject sgSettings = UserDataManager.getUserData(uuid).getSGSettings();
		boolean ratingVisible = (boolean) sgSettings.get("rating_visibility");
		boolean statsVisible = (boolean) sgSettings.get("stats_visibility");

		SmellyInventory smellyInventory = new SmellyInventory(SmellyInventory.getEmptySmellyInventoryHandler(), 54,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "Stats for " + username);

		if (ratingVisible) {
			String division = RatingUtil.getDivisionFromRating(SurvivalGames.getInstance().getSGGame().getPlayerRating(uuid));

			// Has the player not finished their placement matches to show their rating?
			if (sgMiniStatsPlayer.getWins() + sgMiniStatsPlayer.getLosses() < RatingUtil.SG_PLACEMENT_MATCHES) {
				division = ChatColor.WHITE + "[Unranked]";
			}

			smellyInventory.getMainInventory().addItem(
					ItemStackUtil.createItem(Material.NETHER_STAR, ChatColor.GREEN + "Rating: " + division));
		} else {
			smellyInventory.getMainInventory().addItem(
					ItemStackUtil.createItem(Material.NETHER_STAR, ChatColor.GREEN + "Rating: " + ChatColor.WHITE + ChatColor.ITALIC + "Hidden"));
		}

		if (statsVisible) {
			smellyInventory.getMainInventory().addItem(
					ItemStackUtil.createItem(Material.BEACON, ChatColor.GREEN + "" + sgMiniStatsPlayer.getWins() + " wins"));

			smellyInventory.getMainInventory().addItem(
					ItemStackUtil.createItem(Material.DIAMOND_SWORD, ChatColor.GREEN + "" + sgMiniStatsPlayer.getKills() + " kills"));

			smellyInventory.getMainInventory().addItem(
					ItemStackUtil.createItem(Material.IRON_SWORD, ChatColor.GREEN + "" + SGStatsInventory.df.format(sgMiniStatsPlayer.getDamageDealt() / 2) + " hearts dealt"));

			smellyInventory.getMainInventory().addItem(
					ItemStackUtil.createItem(Material.GOLD_SWORD, ChatColor.GREEN + "Highest Kill Streak: " + sgMiniStatsPlayer.getHighestKillStreak()));

			smellyInventory.getMainInventory().addItem(
					ItemStackUtil.createItem(Material.WATCH, ChatColor.GREEN + "Time Played: " + SGStatsInventory.df.format(sgMiniStatsPlayer.getTimePlayed() / 3600D) + " hours"));

			smellyInventory.getMainInventory().addItem(
					ItemStackUtil.createItem(Material.STONE_SWORD, ChatColor.GREEN + "Sword Swings: " + miniStatsPlayer.getSwordSwings()));

			smellyInventory.getMainInventory().addItem(
					ItemStackUtil.createItem(Material.STONE_SWORD, ChatColor.GREEN + "Sword Blocks: " + miniStatsPlayer.getSwordBlocks()));

			smellyInventory.getMainInventory().addItem(
					ItemStackUtil.createItem(Material.GOLD_SWORD, ChatColor.GREEN + "Sword Hits: " + miniStatsPlayer.getSwordHits()));

			smellyInventory.getMainInventory().addItem(
					ItemStackUtil.createItem(Material.WOOD_SWORD, ChatColor.GREEN + "Hit Accuracy: " + StatsInventory.df.format(miniStatsPlayer.getSwordAccuracy() * 100) + "%"));

			smellyInventory.getMainInventory().addItem(
					ItemStackUtil.createItem(Material.ARROW, ChatColor.GREEN + "Arrows Shot: " + miniStatsPlayer.getArrowsShot()));

			smellyInventory.getMainInventory().addItem(
					ItemStackUtil.createItem(Material.ARROW, ChatColor.GREEN + "Arrows Hit: " + miniStatsPlayer.getArrowsHit()));

			smellyInventory.getMainInventory().addItem(
					ItemStackUtil.createItem(Material.BOW, ChatColor.GREEN + "Bow Accuracy: " + SGStatsInventory.df.format(sgMiniStatsPlayer.getArrowAccuracy() * 100) + "%"));

			smellyInventory.getMainInventory().addItem(
					ItemStackUtil.createItem(Material.BOW, ChatColor.GREEN + "Bow Punches: " + sgMiniStatsPlayer.getBowPunches()));

			smellyInventory.getMainInventory().addItem(
					ItemStackUtil.createItem(Material.CHEST, ChatColor.GREEN + "Tier 1 Chests Opened: " + sgMiniStatsPlayer.getNumberOfTierChestsOpened(1)));

			smellyInventory.getMainInventory().addItem(
					ItemStackUtil.createItem(Material.CHEST, ChatColor.GREEN + "Tier 2 Chests Opened: " + sgMiniStatsPlayer.getNumberOfTierChestsOpened(2)));

			smellyInventory.getMainInventory().addItem(
					ItemStackUtil.createItem(Material.ENDER_CHEST, ChatColor.GREEN + "Supply Drops Opened: " + sgMiniStatsPlayer.getNumberOfSupplyDropsOpened()));
		} else {
			smellyInventory.getMainInventory().addItem(
					ItemStackUtil.createItem(Material.IRON_DOOR, ChatColor.GREEN + "" + ChatColor.ITALIC + "Stats Are Hidden"));
		}

		BukkitUtil.openInventory(player, smellyInventory.getMainInventory());
	}

}
