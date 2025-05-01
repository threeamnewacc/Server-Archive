package net.badlion.potpvp.helpers;

import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MatchmakingHelper {

	private static ItemStack currentQueueItem;
	private static ItemStack currentKitItem;
	private static ItemStack leaveQueueItem;

	public static void initialize() {
		MatchmakingHelper.currentQueueItem = ItemStackUtil.createItem(Material.PAPER, ChatColor.GREEN + "Current Queue");

		MatchmakingHelper.currentKitItem = ItemStackUtil.createItem(Material.BOOK, ChatColor.GREEN + "Current Kit");

		MatchmakingHelper.leaveQueueItem = ItemStackUtil.createItem(Material.REDSTONE, ChatColor.GREEN + "Leave Queue");
	}

	public static ItemStack getCurrentQueueItem() {
		return currentQueueItem;
	}

	public static ItemStack getCurrentKitItem() {
		return currentKitItem;
	}

	public static ItemStack getLeaveQueueItem() {
		return leaveQueueItem;
	}

}
