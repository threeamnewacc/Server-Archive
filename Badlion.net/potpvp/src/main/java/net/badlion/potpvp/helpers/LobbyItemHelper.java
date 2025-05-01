package net.badlion.potpvp.helpers;

import net.badlion.gberry.utils.ItemStackUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class LobbyItemHelper {

	// Hotbar items
	private static ItemStack kitCreationItem;
	private static ItemStack ranked1v1Item;
	private static ItemStack createPartyItem;
	private static ItemStack unranked1v1Item;
	private static ItemStack ffaItem;
	private static ItemStack eventsItem;
	private static ItemStack tdmItem;
	private static ItemStack spectatorToggleOnItem;
	private static ItemStack settingsItem;

	public static void initialize() {
		// Create all of the lobby items
		LobbyItemHelper.kitCreationItem = ItemStackUtil.createItem(Material.BOOK, ChatColor.GREEN + "Kit Creation");

		LobbyItemHelper.ranked1v1Item = ItemStackUtil.createItem(Material.DIAMOND_SWORD, ChatColor.GREEN + "Ranked 1v1");

		LobbyItemHelper.createPartyItem = ItemStackUtil.createItem(Material.NAME_TAG, ChatColor.GREEN + "Create Party");

		LobbyItemHelper.unranked1v1Item = ItemStackUtil.createItem(Material.IRON_SWORD, ChatColor.GREEN + "Unranked");

		LobbyItemHelper.ffaItem = ItemStackUtil.createItem(Material.GOLD_AXE, ChatColor.GREEN + "FFA");

		LobbyItemHelper.eventsItem = ItemStackUtil.createItem(Material.NETHER_STAR, ChatColor.GREEN + "Events");

		LobbyItemHelper.tdmItem = ItemStackUtil.createItem(Material.BOW, ChatColor.GREEN + "TDM");

		LobbyItemHelper.spectatorToggleOnItem = ItemStackUtil.createItem(Material.REDSTONE_TORCH_ON, ChatColor.GREEN + "Spectator Toggle On");

		LobbyItemHelper.settingsItem = ItemStackUtil.createItem(Material.WATCH, ChatColor.GREEN + "Settings");
	}

	public static ItemStack getKitCreationItem() {
		return kitCreationItem;
	}

	public static ItemStack getRanked1v1Item() {
		return ranked1v1Item;
	}

	public static ItemStack getCreatePartyItem() {
		return createPartyItem;
	}

	public static ItemStack getUnranked1v1Item() {
		return unranked1v1Item;
	}

	public static ItemStack getFFAItem() {
		return ffaItem;
	}

	public static ItemStack getEventsItem() {
		return eventsItem;
	}

	public static ItemStack getTDMItem() {
		return tdmItem;
	}

	public static ItemStack getSpectatorToggleOnItem() {
		return spectatorToggleOnItem;
	}

	public static ItemStack getSettingsItem() {
		return settingsItem;
	}

}
