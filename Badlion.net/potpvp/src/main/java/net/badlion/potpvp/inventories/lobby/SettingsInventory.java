package net.badlion.potpvp.inventories.lobby;

import net.badlion.cosmetics.inventories.CosmeticsInventory;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.gpermissions.GPermissions;
import net.badlion.potpvp.managers.MessageManager;
import net.badlion.potpvp.managers.PotPvPPlayerManager;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class SettingsInventory {

	private static SmellyInventory smellyInventory;

	public static void initialize() {
		// Items
		ItemStack duelNoItem = ItemStackUtil.createItem(Material.YELLOW_FLOWER, ChatColor.GREEN + "Toggle Accept Duel Requests");

		ItemStack partyNoItem = ItemStackUtil.createItem(Material.NAME_TAG, ChatColor.GREEN + "Toggle Accept Party Invites");

		ItemStack chatSettingsItem = ItemStackUtil.createItem(Material.SIGN, ChatColor.GREEN + "Chat Settings");
		ItemStack messageSettingsItem = ItemStackUtil.createItem(Material.COMPASS, ChatColor.GREEN + "Message Settings");

		ItemStack displayNameColorItem = ItemStackUtil.createItem(Material.CLAY, ChatColor.GREEN + "Display Name Color");

		ItemStack cosmeticsItem = ItemStackUtil.createItem(Material.ENDER_CHEST, ChatColor.GREEN + "Cosmetics");

		// Smelly inventory
		SmellyInventory smellyInventory = new SmellyInventory(new SettingsScreenHandler(), 18,
				ChatColor.BOLD + ChatColor.AQUA.toString() + "Settings");

		smellyInventory.getMainInventory().addItem(duelNoItem, partyNoItem);
		smellyInventory.getMainInventory().setItem(4, chatSettingsItem);
		smellyInventory.getMainInventory().setItem(5, messageSettingsItem);
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

	public static void openMessageSettingsInventory(Player player) {
		PotPvPPlayerManager.addDebug(player, "Open message settings inventory");

		Inventory inventory = SettingsInventory.smellyInventory.createInventory(SettingsInventory.smellyInventory.getFakeHolder(),
				new MessageSettingsScreenHandler(), 5, 9, ChatColor.BOLD + ChatColor.AQUA.toString() + "Server Message Settings");

		// Message options
		MessageManager.MessageOptions messageOptions = MessageManager.getMessageOptions(player);
		for (MessageManager.MessageType messageType : MessageManager.MessageType.values()) {
			if (messageType.equals(MessageManager.MessageType.DUEL)
					|| messageType.equals(MessageManager.MessageType.PARTY)) continue;

			ItemStack item = messageType.getItem().clone();

			ItemMeta itemMeta = item.getItemMeta();
			List<String> lore = itemMeta.getLore();

			lore.add("");
			if (messageOptions.getMessageTagBoolean(messageType)) {
				lore.add(ChatColor.YELLOW + "Currently " + ChatColor.GREEN + "Enabled");
			} else {
				lore.add(ChatColor.YELLOW + "Currently " + ChatColor.RED + "Disabled");
			}

			itemMeta.setLore(lore);
			item.setItemMeta(itemMeta);

			inventory.addItem(item);
		}

		BukkitUtil.openInventory(player, inventory);
	}

	private static class SettingsScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			switch (slot) {
			    case 0: // Duel requests
				    MessageManager.MessageOptions messageOptions = MessageManager.getMessageOptions(player);
				    boolean bool = messageOptions.toggleMessageOption(MessageManager.MessageType.DUEL);

				    if (bool) {
					    player.sendMessage(ChatColor.YELLOW + "Now accepting duel requests");
				    } else {
					    player.sendMessage(ChatColor.YELLOW + "No longer accepting duel requests");
				    }

				    BukkitUtil.closeInventory(player);
				    break;
			    case 1: // Party invites
				    messageOptions = MessageManager.getMessageOptions(player);
				    bool = messageOptions.toggleMessageOption(MessageManager.MessageType.PARTY);

				    if (bool) {
					    player.sendMessage(ChatColor.YELLOW + "Now accepting party invites");
				    } else {
					    player.sendMessage(ChatColor.YELLOW + "No longer accepting party invites");
				    }

				    BukkitUtil.closeInventory(player);
				    break;
			    case 4: // Chat settings
				    player.performCommand("ch settings");
				    break;
			    case 5: // Server message settings
				    SettingsInventory.openMessageSettingsInventory(player);
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
                        player.sendMessage(ChatColor.RED + "Only donators can change the color of their names.");
                        player.sendMessage(ChatColor.GREEN + "Become a donator at http://store.badlion.net/ and help support the server.");

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

	private static class MessageSettingsScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			MessageManager.MessageType messageType = MessageManager.MessageType.valueOf(item.getItemMeta().getDisplayName().substring(2).replace(" ", "_").toUpperCase());
			MessageManager.MessageOptions messageOptions = MessageManager.getMessageOptions(player);

			boolean bool = messageOptions.toggleMessageOption(messageType);

			if (bool) {
				player.sendMessage(ChatColor.GREEN + "Enabled " + messageType.getItem().getItemMeta().getDisplayName());
			} else {
				player.sendMessage(ChatColor.RED + "Disabled " + messageType.getItem().getItemMeta().getDisplayName());
			}

			BukkitUtil.closeInventory(player);
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}

	private static class DisplayNameColorScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			switch(slot) {
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
