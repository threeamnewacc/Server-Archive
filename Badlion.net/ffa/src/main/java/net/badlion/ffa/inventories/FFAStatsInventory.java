package net.badlion.ffa.inventories;

import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.ministats.MiniStatsPlayer;
import net.badlion.mpg.inventories.StatsInventory;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FFAStatsInventory extends StatsInventory {

	@Override
	public void openPlayerStatsInventory(Player player, UUID uuid, String username, MiniStatsPlayer miniStatsPlayer) {
		SmellyInventory smellyInventory = new SmellyInventory(SmellyInventory.getEmptySmellyInventoryHandler(), 54,
				ChatColor.AQUA + ChatColor.BOLD.toString() + "Stats for " + username);

		smellyInventory.getMainInventory().addItem(
				ItemStackUtil.createItem(Material.DIAMOND_SWORD, ChatColor.GREEN + "" + miniStatsPlayer.getKills() + " kills"));

		smellyInventory.getMainInventory().addItem(
				ItemStackUtil.createItem(Material.SKULL, ChatColor.GREEN + "" + miniStatsPlayer.getDeaths() + " deaths"));

		smellyInventory.getMainInventory().addItem(
				ItemStackUtil.createItem(Material.DIAMOND_HOE, ChatColor.GREEN + "KDR: " + miniStatsPlayer.getKdr()));

		smellyInventory.getMainInventory().addItem(
				ItemStackUtil.createItem(Material.IRON_SWORD, ChatColor.GREEN + "" + StatsInventory.df.format(miniStatsPlayer.getDamageDealt() / 2) + " hearts dealt"));

		smellyInventory.getMainInventory().addItem(
				ItemStackUtil.createItem(Material.IRON_AXE, ChatColor.GREEN + "" + StatsInventory.df.format(miniStatsPlayer.getDamageTaken() / 2) + " hearts taken"));

		smellyInventory.getMainInventory().addItem(
				ItemStackUtil.createItem(Material.GOLD_SWORD, ChatColor.GREEN + "Highest Kill Streak: " + miniStatsPlayer.getHighestKillStreak()));

		smellyInventory.getMainInventory().addItem(
				ItemStackUtil.createItem(Material.WATCH, ChatColor.GREEN + "Time Played: " + StatsInventory.df.format(miniStatsPlayer.getTimePlayed() / 3600D) + " hours"));

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
				ItemStackUtil.createItem(Material.BOW, ChatColor.GREEN + "Bow Accuracy: " + StatsInventory.df.format(miniStatsPlayer.getArrowAccuracy() * 100) + "%"));

		smellyInventory.getMainInventory().addItem(
				ItemStackUtil.createItem(Material.BOW, ChatColor.GREEN + "Bow Punches: " + miniStatsPlayer.getBowPunches()));

		BukkitUtil.openInventory(player, smellyInventory.getMainInventory());
	}

}
