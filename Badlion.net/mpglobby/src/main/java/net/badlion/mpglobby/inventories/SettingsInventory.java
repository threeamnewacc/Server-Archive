package net.badlion.mpglobby.inventories;

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

public abstract class SettingsInventory {

	private static SettingsInventory instance;

	private ItemStack openSettingsInventoryItem;

	private ItemStack chatSettingsItem;
	private ItemStack displayNameColorItem;

	public SettingsInventory() {
		this.openSettingsInventoryItem = ItemStackUtil.createItem(Material.WATCH, ChatColor.GREEN + "Settings");

		this.chatSettingsItem = ItemStackUtil.createItem(Material.SIGN, ChatColor.GREEN + "Chat Settings", ChatColor.YELLOW + "Click to configure your", ChatColor.YELLOW + "chat settings!");
		this.displayNameColorItem = ItemStackUtil.createItem(Material.CLAY, ChatColor.GREEN + "Display Name Color", ChatColor.YELLOW + "Click to change the", ChatColor.YELLOW + "color of your name!");

		SettingsInventory.instance = this;
	}

	public static SettingsInventory getInstance() {
		return SettingsInventory.instance;
	}

	public ItemStack getOpenSettingsInventoryItem() {
		return this.openSettingsInventoryItem;
	}

	public void openSettingsInventory(Player player) {
		// Create smelly inventory
		SmellyInventory smellyInventory = new SmellyInventory(new SettingsInventoryScreenHandler(), 18,
				ChatColor.BOLD + ChatColor.AQUA.toString() + "Settings");

		this.fillSettingsInventory(player, smellyInventory);

		smellyInventory.getMainInventory().setItem(4, this.chatSettingsItem);

		smellyInventory.getMainInventory().setItem(8, this.displayNameColorItem);

		BukkitUtil.openInventory(player, smellyInventory.getMainInventory());
	}

	protected abstract void fillSettingsInventory(Player player, SmellyInventory smellyInventory);

	private ItemStack getDisplayNameColorItem(Player player, String prefix, ChatColor color, String colorName, short woolColor) {
		return ItemStackUtil.createItem(Material.WOOL, woolColor,
				ChatColor.GREEN + "Choose " + colorName, ChatColor.YELLOW + "Change display name", ChatColor.YELLOW + "color to " + color + colorName,
				"", prefix + color + player.getName());
	}

	protected class SettingsInventoryScreenHandler implements SmellyInventory.SmellyInventoryHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			switch (slot) {
				case 4: // Chat settings
					player.performCommand("ch settings");
					break;
				case 8:
					if (player.hasPermission("ColorName.changecolor")) {
						Inventory displayNameColorInventory = fakeHolder.getSmellyInventory().createInventory(fakeHolder,
								new DisplayNameColorScreenHandler(), 8, 18, ChatColor.BOLD + ChatColor.AQUA.toString() + "Display Name Color Options");

						String playerPrefix = GPermissions.plugin.getUserGroup(player.getUniqueId()).getPrefix().replace("&", "§");
						displayNameColorInventory.addItem(SettingsInventory.this.getDisplayNameColorItem(player, playerPrefix, ChatColor.WHITE, "White", (short) 0),
								SettingsInventory.this.getDisplayNameColorItem(player, playerPrefix, ChatColor.YELLOW, "Yellow", (short) 4),
								SettingsInventory.this.getDisplayNameColorItem(player, playerPrefix, ChatColor.LIGHT_PURPLE, "Light Purple", (short) 2),
								SettingsInventory.this.getDisplayNameColorItem(player, playerPrefix, ChatColor.AQUA, "Aqua", (short) 3),
								SettingsInventory.this.getDisplayNameColorItem(player, playerPrefix, ChatColor.GREEN, "Green", (short) 5),
								SettingsInventory.this.getDisplayNameColorItem(player, playerPrefix, ChatColor.BLUE, "Blue", (short) 3),
								SettingsInventory.this.getDisplayNameColorItem(player, playerPrefix, ChatColor.DARK_GRAY, "Dark Gray", (short) 7),
								SettingsInventory.this.getDisplayNameColorItem(player, playerPrefix, ChatColor.GRAY, "Gray", (short) 8),
								SettingsInventory.this.getDisplayNameColorItem(player, playerPrefix, ChatColor.GOLD, "Gold", (short) 1),
								SettingsInventory.this.getDisplayNameColorItem(player, playerPrefix, ChatColor.DARK_PURPLE, "Dark Purple", (short) 10),
								SettingsInventory.this.getDisplayNameColorItem(player, playerPrefix, ChatColor.DARK_AQUA, "Dark Aqua", (short) 9),
								SettingsInventory.this.getDisplayNameColorItem(player, playerPrefix, ChatColor.DARK_GREEN, "Dark Green", (short) 13),
								SettingsInventory.this.getDisplayNameColorItem(player, playerPrefix, ChatColor.RED, "Red", (short) 14));

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

	private class DisplayNameColorScreenHandler implements SmellyInventory.SmellyInventoryHandler {

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
