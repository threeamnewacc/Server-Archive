package net.badlion.arenalobby.inventories.lobby;

import net.badlion.arenalobby.managers.ArenaSettingsManager;
import net.badlion.arenalobby.managers.PotPvPPlayerManager;
import net.badlion.arenacommon.settings.ArenaSettings;
import net.badlion.cosmetics.inventories.CosmeticsInventory;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.gpermissions.GPermissions;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class SettingsInventory {

	public static SmellyInventory smellyInventory;

	private static ItemStack duelItem;
	private static ItemStack partyItem;
	private static ItemStack duelRequestTypeItem;
	private static ItemStack sidebarEnabledItem;
	private static ItemStack showPlayersInLobbyItem;
	private static ItemStack showColoredHelmetsItem;
	private static ItemStack showTitlesItem;
	private static ItemStack showRankPrefix;


	public static void initialize() {
		// Items
		SettingsInventory.duelItem = ItemStackUtil.createItem(Material.YELLOW_FLOWER, ChatColor.GREEN + "Toggle Accept Duel Requests");

		SettingsInventory.partyItem = ItemStackUtil.createItem(Material.NAME_TAG, ChatColor.GREEN + "Toggle Accept Party Invites");

		SettingsInventory.duelRequestTypeItem = ItemStackUtil.createItem(Material.PAINTING, ChatColor.GREEN + "Toggle Duel Request Type");
		SettingsInventory.sidebarEnabledItem = ItemStackUtil.createItem(Material.ITEM_FRAME, ChatColor.GREEN + "Toggle Sidebar");

		SettingsInventory.showPlayersInLobbyItem = ItemStackUtil.createItem(Material.SKULL_ITEM, (short) 3, ChatColor.GREEN + "Toggle Player Visibility");

		SettingsInventory.showColoredHelmetsItem = ItemStackUtil.createItem(Material.LEATHER_HELMET, ChatColor.GREEN + "Toggle Color Helmets In Spectator");

		SettingsInventory.showTitlesItem = ItemStackUtil.createItem(Material.PAPER, ChatColor.GREEN + "Toggle Titles", "(Only for 1.8+)");

		SettingsInventory.showRankPrefix = ItemStackUtil.createItem(Material.DIAMOND, ChatColor.GREEN + "Toggle Rank Prefix", "(Above your name)");

		ItemStack lobbySelectorItem = ItemStackUtil.createItem(Material.WOOL, ChatColor.GREEN + "Select Arena Lobby");
		ItemStack chatLobbySelectorItem = ItemStackUtil.createItem(Material.SIGN, ChatColor.GREEN + "Select Arena Chat Lobby");
		ItemStack spawnSelectorItem = ItemStackUtil.createItem(Material.DIAMOND_SWORD, ChatColor.GREEN + "Select Spawn Point");

		ItemStack chatSettingsItem = ItemStackUtil.createItem(Material.SIGN, ChatColor.GREEN + "Chat Settings");
		ItemStack arenaSettingsItem = ItemStackUtil.createItem(Material.DIAMOND_CHESTPLATE, ChatColor.GREEN + "Arena Settings");

		ItemStack displayNameColorItem = ItemStackUtil.createItem(Material.CLAY, ChatColor.GREEN + "Display Name Color");

		ItemStack cosmeticsItem = ItemStackUtil.createItem(Material.ENDER_CHEST, ChatColor.GREEN + "Cosmetics");

		// Smelly inventory
		SmellyInventory smellyInventory = new SmellyInventory(new SettingsScreenHandler(), 18,
				ChatColor.BOLD + ChatColor.AQUA.toString() + "Settings");

		smellyInventory.getMainInventory().setItem(0, chatLobbySelectorItem);
		smellyInventory.getMainInventory().setItem(2, spawnSelectorItem);
		smellyInventory.getMainInventory().setItem(3, lobbySelectorItem);
		smellyInventory.getMainInventory().setItem(5, chatSettingsItem);
		smellyInventory.getMainInventory().setItem(6, arenaSettingsItem);
		smellyInventory.getMainInventory().setItem(8, displayNameColorItem);
		smellyInventory.getMainInventory().setItem(9, cosmeticsItem);

		SettingsInventory.smellyInventory = smellyInventory;
	}

	private static ItemStack getDisplayNameColorItem(Player player, String prefix, ChatColor color, String colorName, short woolColor) {
		ItemStack item = ItemStackUtil.createItem(Material.WOOL, woolColor,
				ChatColor.GREEN + "Choose " + colorName, ChatColor.YELLOW + "Change display name", ChatColor.YELLOW + "color to " + color + colorName,
				"", prefix + color + player.getName());
		return item;
	}

	public static void openSettingsInventory(Player player) {
		PotPvPPlayerManager.addDebug(player, "Open settings inventory");

		BukkitUtil.openInventory(player, SettingsInventory.smellyInventory.getMainInventory());
	}

	public static void openArenaSettingsInventory(Player player) {
		PotPvPPlayerManager.addDebug(player, "Open arena settings inventory");

		Inventory inventory = SettingsInventory.smellyInventory.createInventory(SettingsInventory.smellyInventory.getFakeHolder(),
				new ArenaSettingsScreenHandler(), 5, 18, ChatColor.BOLD + ChatColor.AQUA.toString() + "Arena Settings");

		// Arena options
		ArenaSettings settings = ArenaSettingsManager.getSettings(player);

		SettingsInventory.addItemStackToSettingsInventory(inventory, 0, SettingsInventory.duelItem, SettingsInventory.enabledDisabledFromBoolean(settings.isAllowDuelRequests()));
		SettingsInventory.addItemStackToSettingsInventory(inventory, 1, SettingsInventory.partyItem, SettingsInventory.enabledDisabledFromBoolean(settings.isAllowPartyRequests()));


		SettingsInventory.addItemStackToSettingsInventory(inventory, 4, SettingsInventory.duelRequestTypeItem, ChatColor.GOLD + settings.getDuelRequestType().toString());

		SettingsInventory.addItemStackToSettingsInventory(inventory, 7, SettingsInventory.sidebarEnabledItem, SettingsInventory.enabledDisabledFromBoolean(settings.isSidebarEnabled()));
		SettingsInventory.addItemStackToSettingsInventory(inventory, 8, SettingsInventory.showTitlesItem, SettingsInventory.enabledDisabledFromBoolean(settings.showsTitles()));

		SettingsInventory.addItemStackToSettingsInventory(inventory, 9, SettingsInventory.showPlayersInLobbyItem, SettingsInventory.enabledDisabledFromBoolean(settings.showsPlayersInLobby()));
		SettingsInventory.addItemStackToSettingsInventory(inventory, 10, SettingsInventory.showColoredHelmetsItem, SettingsInventory.enabledDisabledFromBoolean(settings.showsColoredHelmInSpec()));

		SettingsInventory.addItemStackToSettingsInventory(inventory, 13, SettingsInventory.showRankPrefix, SettingsInventory.enabledDisabledFromBoolean(settings.isShowRankPrefix()));

		BukkitUtil.openInventory(player, inventory);
	}

	private static void updateArenaSettingsInventory(Inventory inventory, ArenaSettings settings){
		inventory.clear();
		SettingsInventory.addItemStackToSettingsInventory(inventory, 0, SettingsInventory.duelItem, SettingsInventory.enabledDisabledFromBoolean(settings.isAllowDuelRequests()));
		SettingsInventory.addItemStackToSettingsInventory(inventory, 1, SettingsInventory.partyItem, SettingsInventory.enabledDisabledFromBoolean(settings.isAllowPartyRequests()));


		SettingsInventory.addItemStackToSettingsInventory(inventory, 4, SettingsInventory.duelRequestTypeItem, ChatColor.GOLD + settings.getDuelRequestType().toString());

		SettingsInventory.addItemStackToSettingsInventory(inventory, 7, SettingsInventory.sidebarEnabledItem, SettingsInventory.enabledDisabledFromBoolean(settings.isSidebarEnabled()));
		SettingsInventory.addItemStackToSettingsInventory(inventory, 8, SettingsInventory.showTitlesItem, SettingsInventory.enabledDisabledFromBoolean(settings.showsTitles()));

		SettingsInventory.addItemStackToSettingsInventory(inventory, 9, SettingsInventory.showPlayersInLobbyItem, SettingsInventory.enabledDisabledFromBoolean(settings.showsPlayersInLobby()));
		SettingsInventory.addItemStackToSettingsInventory(inventory, 10, SettingsInventory.showColoredHelmetsItem, SettingsInventory.enabledDisabledFromBoolean(settings.showsColoredHelmInSpec()));

		SettingsInventory.addItemStackToSettingsInventory(inventory, 13, SettingsInventory.showRankPrefix, SettingsInventory.enabledDisabledFromBoolean(settings.isShowRankPrefix()));

		inventory.setItem(17, SmellyInventory.getBackInventoryItem());
	}

	private static void addItemStackToSettingsInventory(Inventory inventory, int slot, ItemStack itemStack, String value) {
		ItemStack item = itemStack.clone();
		ItemMeta itemMeta = item.getItemMeta();
		List<String> lore;
		if (itemMeta.hasLore()) {
			lore = itemMeta.getLore();
		} else {
			lore = new ArrayList<>();
		}
		lore.add(" ");
		lore.add(ChatColor.YELLOW + "Currently: " + value);
		itemMeta.setLore(lore);
		item.setItemMeta(itemMeta);

		inventory.setItem(slot, item);
	}


	public static String enabledDisabledFromBoolean(boolean bool) {
		if (bool) {
			return ChatColor.GREEN + "Enabled";
		} else {
			return ChatColor.RED + "Disabled";
		}
	}


	private static class SettingsScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			switch (slot) {
				case 0:
					ChatLobbySelectorInventory.openChatLobbySelectorMenu(player);
					break;
				case 2:
					SpawnSelectorInventory.openSpawnSelectorMenu(player);
					break;
				case 3:
					LobbySelectorInventory.openLobbySelectorMenu(player);
					break;
				case 5: // Chat settings
					player.performCommand("ch settings");
					break;
				case 6: // Arena settings
					SettingsInventory.openArenaSettingsInventory(player);
					break;
				case 8:
					if (player.hasPermission("ColorName.changecolor")) {
						Inventory displayNameColorInventory = smellyInventory.createInventory(smellyInventory.getFakeHolder(),
								new DisplayNameColorScreenHandler(), 8, 18, ChatColor.BOLD + ChatColor.AQUA.toString() + "Display Name Color Options");

						String playerPrefix = GPermissions.plugin.getUserGroup(player.getUniqueId()).getPrefix().replace("&", "§");
						displayNameColorInventory.addItem(SettingsInventory.getDisplayNameColorItem(player, playerPrefix, ChatColor.WHITE, "White", (short) 0),
								SettingsInventory.getDisplayNameColorItem(player, playerPrefix, ChatColor.YELLOW, "Yellow", (short) 4),
								SettingsInventory.getDisplayNameColorItem(player, playerPrefix, ChatColor.LIGHT_PURPLE, "Light Purple", (short) 2),
								SettingsInventory.getDisplayNameColorItem(player, playerPrefix, ChatColor.AQUA, "Aqua", (short) 3),
								SettingsInventory.getDisplayNameColorItem(player, playerPrefix, ChatColor.GREEN, "Green", (short) 5),
								SettingsInventory.getDisplayNameColorItem(player, playerPrefix, ChatColor.BLUE, "Blue", (short) 3),
								SettingsInventory.getDisplayNameColorItem(player, playerPrefix, ChatColor.DARK_GRAY, "Dark Gray", (short) 7),
								SettingsInventory.getDisplayNameColorItem(player, playerPrefix, ChatColor.GRAY, "Gray", (short) 8),
								SettingsInventory.getDisplayNameColorItem(player, playerPrefix, ChatColor.GOLD, "Gold", (short) 1),
								SettingsInventory.getDisplayNameColorItem(player, playerPrefix, ChatColor.DARK_PURPLE, "Dark Purple", (short) 10),
								SettingsInventory.getDisplayNameColorItem(player, playerPrefix, ChatColor.DARK_AQUA, "Dark Aqua", (short) 9),
								SettingsInventory.getDisplayNameColorItem(player, playerPrefix, ChatColor.DARK_GREEN, "Dark Green", (short) 13),
								SettingsInventory.getDisplayNameColorItem(player, playerPrefix, ChatColor.RED, "Red", (short) 14));

						BukkitUtil.openInventory(player, fakeHolder.getSubInventory(slot));
					} else {
						player.sendFormattedMessage("{0}Only donators can change the color of their names.", ChatColor.RED);
						player.sendFormattedMessage("{0}Become a donator at {1} and help support the server.", ChatColor.GREEN, "http://store.badlion.net/");

						BukkitUtil.closeInventory(player);
					}
					break;
				case 9:
					CosmeticsInventory.openCosmeticInventory(player);
					break;
			}
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {
		}

	}

	private static class ArenaSettingsScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			ArenaSettings settings = ArenaSettingsManager.getSettings(player);
			switch (slot){
				case 0:
					// Duels
					settings.setAllowDuelRequests(!settings.isAllowDuelRequests());
					player.sendFormattedMessage("{0}Duel Requests: {1}", ChatColor.YELLOW, SettingsInventory.enabledDisabledFromBoolean(settings.isAllowDuelRequests()));
					break;
				case 1:
					// Party
					settings.setAllowPartyRequests(!settings.isAllowPartyRequests());
					player.sendFormattedMessage("{0}Party Invites: {1}", ChatColor.YELLOW, SettingsInventory.enabledDisabledFromBoolean(settings.isAllowDuelRequests()));
					break;
				case 4:
					// Duel request type
					if(settings.getDuelRequestType().equals(ArenaSettings.DuelRequestType.CHAT)) {
						settings.setDuelRequestType(ArenaSettings.DuelRequestType.INVENTORY);
					}else if (settings.getDuelRequestType().equals(ArenaSettings.DuelRequestType.INVENTORY)){
						settings.setDuelRequestType(ArenaSettings.DuelRequestType.CHAT);
					}
					player.sendFormattedMessage("{0}Duel Request Type: {1}", ChatColor.YELLOW, ChatColor.GOLD + settings.getDuelRequestType().toString());
					break;
				case 7:
					// sidebar
					settings.setSidebarEnabled(!settings.isSidebarEnabled());
					player.sendFormattedMessage("{0}Sidebar In Duels: {1}", ChatColor.YELLOW, SettingsInventory.enabledDisabledFromBoolean(settings.isSidebarEnabled()));
					break;
				case 8:
					// titles
					settings.setShowTitles(!settings.showsTitles());
					player.sendFormattedMessage("{0}Showing Titles (1.8+ Only): {1}", ChatColor.YELLOW, SettingsInventory.enabledDisabledFromBoolean(settings.showsTitles()));
					break;
				case 9:
					// show players
					settings.setShowPlayersInLobby(!settings.showsPlayersInLobby());
					player.sendFormattedMessage("{0}Show Players In The Lobby: {1}", ChatColor.YELLOW, SettingsInventory.enabledDisabledFromBoolean(settings.showsPlayersInLobby()));
					break;
				case 10:
					// show helms
					settings.setShowColoredHelmInSpec(!settings.showsColoredHelmInSpec());
					player.sendFormattedMessage("{0}Show Colored Helmets In Spectator: {1}", ChatColor.YELLOW, SettingsInventory.enabledDisabledFromBoolean(settings.showsColoredHelmInSpec()));
					break;
				case 13:
					settings.setShowRankPrefix(!settings.isShowRankPrefix());
					player.sendFormattedMessage("{0}Show Rank Prefix: {1}", ChatColor.YELLOW, SettingsInventory.enabledDisabledFromBoolean(settings.isShowRankPrefix()));
					break;
			}
			SettingsInventory.updateArenaSettingsInventory(fakeHolder.getInventory(), settings);
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {
			ArenaSettings settings = ArenaSettingsManager.getSettings(player);
			if(settings.hasChanged()) {
				ArenaSettingsManager.saveSettings(player.getUniqueId());
			}
		}

	}

	private static class DisplayNameColorScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			switch (slot) {
				case 0:
					player.performCommand("color White");
					break;
				case 1:
					player.performCommand("color Yellow");
					break;
				case 2:
					player.performCommand("color Light_Purple");
					break;
				case 3:
					player.performCommand("color Aqua");
					break;
				case 4:
					player.performCommand("color Green");
					break;
				case 5:
					player.performCommand("color Blue");
					break;
				case 6:
					player.performCommand("color Dark_Gray");
					break;
				case 7:
					player.performCommand("color Gray");
					break;
				case 8:
					player.performCommand("color Gold");
					break;
				case 9:
					player.performCommand("color Dark_Purple");
					break;
				case 10:
					player.performCommand("color Dark_Aqua");
					break;
				case 11:
					player.performCommand("color Dark_Green");
					break;
				case 12:
					player.performCommand("color Red");
					break;
			}

			BukkitUtil.closeInventory(player);
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}

}
