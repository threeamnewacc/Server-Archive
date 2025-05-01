package net.badlion.uhcmeetup.inventories;

import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.ministats.MiniStatsPlayer;
import net.badlion.mpg.inventories.StatsInventory;
import net.badlion.smellyinventory.SmellyInventory;
import net.badlion.uhcmeetup.UHCMeetupMiniStatsPlayer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UHCMeetupStatsInventory extends StatsInventory {

	@Override                                               // TODO: ADD GOLDEN HEAD/APPLE STUFF
	public void openPlayerStatsInventory(Player player, UUID uuid, String username, MiniStatsPlayer miniStatsPlayer) {
		UHCMeetupMiniStatsPlayer uhcMeetupMiniStatsPlayer = (UHCMeetupMiniStatsPlayer) miniStatsPlayer;

		SmellyInventory smellyInventory = new SmellyInventory(SmellyInventory.getEmptySmellyInventoryHandler(), 54,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "Stats for " + username);

		smellyInventory.getMainInventory().addItem(
				ItemStackUtil.createItem(Material.DIAMOND_SWORD, ChatColor.GREEN + "" + uhcMeetupMiniStatsPlayer.getKills() + " kills"));

		smellyInventory.getMainInventory().addItem(
				ItemStackUtil.createItem(Material.SKULL, ChatColor.GREEN + "" + uhcMeetupMiniStatsPlayer.getDeaths() + " deaths"));

		smellyInventory.getMainInventory().addItem(
				ItemStackUtil.createItem(Material.DIAMOND_HOE, ChatColor.GREEN + "KDR: " + uhcMeetupMiniStatsPlayer.getKdr()));

		smellyInventory.getMainInventory().addItem(
				ItemStackUtil.createItem(Material.IRON_SWORD, ChatColor.GREEN + "" + StatsInventory.df.format(uhcMeetupMiniStatsPlayer.getDamageDealt() / 2) + " hearts dealt"));

		smellyInventory.getMainInventory().addItem(
				ItemStackUtil.createItem(Material.IRON_AXE, ChatColor.GREEN + "" + StatsInventory.df.format(uhcMeetupMiniStatsPlayer.getDamageTaken() / 2) + " hearts taken"));

		smellyInventory.getMainInventory().addItem(
				ItemStackUtil.createItem(Material.GOLD_SWORD, ChatColor.GREEN + "Highest Kill Streak: " + uhcMeetupMiniStatsPlayer.getHighestKillStreak()));

		smellyInventory.getMainInventory().addItem(
				ItemStackUtil.createItem(Material.WATCH, ChatColor.GREEN + "Time Played: " + StatsInventory.df.format(uhcMeetupMiniStatsPlayer.getTimePlayed() / 3600D) + " hours"));

		smellyInventory.getMainInventory().addItem(
				ItemStackUtil.createItem(Material.STONE_SWORD, ChatColor.GREEN + "Sword Swings: " + uhcMeetupMiniStatsPlayer.getSwordSwings()));

		smellyInventory.getMainInventory().addItem(
				ItemStackUtil.createItem(Material.STONE_SWORD, ChatColor.GREEN + "Sword Blocks: " + uhcMeetupMiniStatsPlayer.getSwordBlocks()));

		smellyInventory.getMainInventory().addItem(
				ItemStackUtil.createItem(Material.GOLD_SWORD, ChatColor.GREEN + "Sword Hits: " + uhcMeetupMiniStatsPlayer.getSwordHits()));

		smellyInventory.getMainInventory().addItem(
				ItemStackUtil.createItem(Material.WOOD_SWORD, ChatColor.GREEN + "Hit Accuracy: " + StatsInventory.df.format(uhcMeetupMiniStatsPlayer.getSwordAccuracy() * 100) + "%"));

		smellyInventory.getMainInventory().addItem(
				ItemStackUtil.createItem(Material.ARROW, ChatColor.GREEN + "Arrows Shot: " + uhcMeetupMiniStatsPlayer.getArrowsShot()));

		smellyInventory.getMainInventory().addItem(
				ItemStackUtil.createItem(Material.ARROW, ChatColor.GREEN + "Arrows Hit: " + uhcMeetupMiniStatsPlayer.getArrowsHit()));

		smellyInventory.getMainInventory().addItem(
				ItemStackUtil.createItem(Material.BOW, ChatColor.GREEN + "Bow Accuracy: " + StatsInventory.df.format(uhcMeetupMiniStatsPlayer.getArrowAccuracy() * 100) + "%"));

		smellyInventory.getMainInventory().addItem(
				ItemStackUtil.createItem(Material.BOW, ChatColor.GREEN + "Bow Punches: " + uhcMeetupMiniStatsPlayer.getBowPunches()));

		smellyInventory.getMainInventory().addItem(
				ItemStackUtil.createItem(Material.GOLDEN_APPLE, ChatColor.GREEN + "Absorption Hearts: " + uhcMeetupMiniStatsPlayer.getAbsorptionHearts()));

		smellyInventory.getMainInventory().addItem(
				ItemStackUtil.createItem(Material.GOLDEN_APPLE, ChatColor.GREEN + "Golden Apples Eaten: " + uhcMeetupMiniStatsPlayer.getGoldenApplesEaten()));

		smellyInventory.getMainInventory().addItem(
				ItemStackUtil.createItem(Material.GOLDEN_APPLE, ChatColor.GREEN + "Golden Heads Eaten: " + uhcMeetupMiniStatsPlayer.getGoldenHeadsEaten()));

		BukkitUtil.openInventory(player, smellyInventory.getMainInventory());
	}

}
