package net.badlion.sglobby.inventories;

import net.badlion.common.libraries.EnumCommon;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.managers.UserDataManager;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.mpglobby.MPGLobby;
import net.badlion.mpglobby.inventories.SettingsInventory;
import net.badlion.sglobby.SGLobby;
import net.badlion.sglobby.bukkitevents.SGSettingsChangeEvent;
import net.badlion.sglobby.managers.SGLobbySidebarManager;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class SGSettingsInventory extends SettingsInventory {

	private ItemStack showSGRatingItem;
	private ItemStack hideSGRatingItem;

	private ItemStack showSGStatsItem;
	private ItemStack hideSGStatsItem;

	public SGSettingsInventory() {
		super();

		this.showSGRatingItem = ItemStackUtil.createItem(Material.INK_SACK, (short) 8, ChatColor.GREEN + "Show SG Rating", ChatColor.YELLOW + "Click to show your SG", ChatColor.YELLOW + "rating to the public!");
		this.hideSGRatingItem = ItemStackUtil.createItem(Material.INK_SACK, (short) 10, ChatColor.GREEN + "Hide SG Rating", ChatColor.YELLOW + "Click to hide your SG", ChatColor.YELLOW + "rating from the public!");

		this.showSGStatsItem = ItemStackUtil.createItem(Material.INK_SACK, (short) 8, ChatColor.GREEN + "Show SG Stats", ChatColor.YELLOW + "Click to show your SG", ChatColor.YELLOW + "stats to the public!");
		this.hideSGStatsItem = ItemStackUtil.createItem(Material.INK_SACK, (short) 10, ChatColor.GREEN + "Hide SG Stats", ChatColor.YELLOW + "Click to hide your SG", ChatColor.YELLOW + "stats from the public!");
	}

	@Override
	protected void fillSettingsInventory(Player player, SmellyInventory smellyInventory) {
		// Overwrite the screen handler for the smellyinventory
		smellyInventory.getFakeHolder().setSmellyInventoryHandler(new SGSettingsInventoryScreenHandler());

		// Grab SG settings
		JSONObject sgSettings = UserDataManager.getUserData(player).getSGSettings();

		// Is their sg rating public?
		if ((boolean) sgSettings.get("rating_visibility")) {
			smellyInventory.getMainInventory().setItem(0, this.hideSGRatingItem);
		} else {
			smellyInventory.getMainInventory().setItem(0, this.showSGRatingItem);
		}

		// Are their sg stats public?
		if ((boolean) sgSettings.get("stats_visibility")) {
			smellyInventory.getMainInventory().setItem(1, this.hideSGStatsItem);
		} else {
			smellyInventory.getMainInventory().setItem(1, this.showSGStatsItem);
		}
	}

	private void syncSGSettingsInRatingsTable(final UUID uuid, final JSONObject sgSettings) {
		BukkitUtil.runTaskAsync(new Runnable() {
			@Override
			public void run() {
				String query = "UPDATE sg_ladder_ratings_s2 SET rating_visibility = ?, stats_visibility = ? WHERE uuid = ?;";

				Connection connection = null;
				PreparedStatement ps = null;

				try {
					connection = Gberry.getConnection();
					ps = connection.prepareStatement(query);

					ps.setBoolean(1, (boolean) sgSettings.get("rating_visibility"));
					ps.setBoolean(2, (boolean) sgSettings.get("stats_visibility"));
					ps.setString(3, uuid.toString());

					Gberry.executeUpdate(connection, ps);
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					Gberry.closeComponents(ps, connection);
				}
			}
		});
	}

	private class SGSettingsInventoryScreenHandler extends SettingsInventoryScreenHandler {

		@Override
		public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
			// Check cooldown (hardcodes for chat settings and display name colors)
			if (slot != 4 && slot != 8) {
				if (MPGLobby.getInstance().hasCooldown(player.getUniqueId())) {
					player.sendMessage(ChatColor.RED + "Cannot change settings so quickly!");
					return;
				}
			}

			switch (slot) {
				case 0: // SG rating visibility
					// Grab SG settings
					UserDataManager.UserData userData = UserDataManager.getUserData(player);
					JSONObject sgSettings = userData.getSGSettings();

					boolean current = (boolean) sgSettings.get("rating_visibility");

					// Toggle option and update
					sgSettings.put("rating_visibility", !current);
					userData.setSGSettings(sgSettings, true);

					player.playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "ITEM_BREAK", "ENTITY_ITEM_BREAK"), 1.4F, 0.4F);

					// Send message
					if (current) {
						player.sendMessage(ChatColor.YELLOW + "Your SG rating is no longer visible to the public!");

						fakeHolder.getInventory().setItem(0, SGSettingsInventory.this.showSGRatingItem);
					} else {
						player.sendMessage(ChatColor.YELLOW + "Your SG rating is now visible to the public!");

						fakeHolder.getInventory().setItem(0, SGSettingsInventory.this.hideSGRatingItem);
					}

					// Update sidebar
					SGLobbySidebarManager.updateSidebar(player);

					// Call event for tab list
					SGLobby.getInstance().getServer().getPluginManager().callEvent(new SGSettingsChangeEvent(player, (boolean) sgSettings.get("rating_visibility"), (boolean) sgSettings.get("stats_visibility")));

					// Sync settings in the ratings table
					SGSettingsInventory.this.syncSGSettingsInRatingsTable(player.getUniqueId(), sgSettings);

					break;
				case 1: // SG stats visibility
					// Grab SG settings
					userData = UserDataManager.getUserData(player);
					sgSettings = userData.getSGSettings();

					current = (boolean) sgSettings.get("stats_visibility");

					// Toggle option and update
					sgSettings.put("stats_visibility", !current);
					userData.setSGSettings(sgSettings, true);

					player.getWorld().playSound(player.getLocation(), EnumCommon.getEnumValueOf(Sound.class, "ITEM_BREAK", "ENTITY_ITEM_BREAK"), 1.4F, 0.4F);

					// Send message
					if (current) {
						player.sendMessage(ChatColor.YELLOW + "Your SG stats are no longer visible to the public!");

						fakeHolder.getInventory().setItem(1, SGSettingsInventory.this.showSGStatsItem);
					} else {
						player.sendMessage(ChatColor.YELLOW + "Your SG stats are now visible to the public!");

						fakeHolder.getInventory().setItem(1, SGSettingsInventory.this.hideSGStatsItem);
					}

					// Call event for tab list
					SGLobby.getInstance().getServer().getPluginManager().callEvent(new SGSettingsChangeEvent(player, (boolean) sgSettings.get("rating_visibility"), (boolean) sgSettings.get("stats_visibility")));

					// Sync settings in the ratings table
					SGSettingsInventory.this.syncSGSettingsInRatingsTable(player.getUniqueId(), sgSettings);

					break;
				default:
					super.handleInventoryClickEvent(fakeHolder, player, event, item, slot);
					break;
			}
		}

		@Override
		public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

		}

	}

}
