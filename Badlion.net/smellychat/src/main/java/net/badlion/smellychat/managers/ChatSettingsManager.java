package net.badlion.smellychat.managers;

import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.events.AsyncPlayerJoinEvent;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.CompressionUtil;
import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.gpermissions.GPermissions;
import net.badlion.gpermissions.GUser;
import net.badlion.gpermissions.Group;
import net.badlion.smellychat.SmellyChat;
import net.badlion.smellychat.tasks.RecentPMRemoveTask;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChatSettingsManager implements Listener {

	private static Map<UUID, ChatSettings> chatSettings = new ConcurrentHashMap<>();

	public static void initialize() {
		SettingsInventory.initialize();
	}

	public static ChatSettings getChatSettings(Player player) {
		ChatSettings chatSettings = ChatSettingsManager.chatSettings.get(player.getUniqueId());

		if (chatSettings != null)
			return chatSettings;

		return ChatSettingsManager.chatSettings.put(player.getUniqueId(), new ChatSettings(player));
	}

	private static ChatSettings removeChatSettings(Player player) {
		return ChatSettingsManager.chatSettings.remove(player.getUniqueId());
	}

	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		// Load ChatSettings object
		ChatSettings chatSettings = ChatSettingsManager.getChatSettings(event.getPlayer());

		// LEGACY SETTINGS CHECK
		// Enable PMs if this is a staff member who has disabled them
		if (event.getPlayer().hasPermission("badlion.staff") && !event.getPlayer().hasPermission("badlion.admin")
				&& !event.getPlayer().hasPermission("badlion.developer")) {
			chatSettings.setSetting(Setting.PRIVATE_MESSAGES, true);
		}
	}

	@EventHandler
	public void onPlayerAsyncJoin(final AsyncPlayerJoinEvent event) {
		ChatSettings chatSettings = ChatSettingsManager.chatSettings.get(event.getUuid());
		if (chatSettings != null) {
			chatSettings.fetchChatSettings(event.getConnection());
		}
	}

	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		// Remove ChatSettings object
		ChatSettingsManager.removeChatSettings(event.getPlayer());
	}

	public enum SettingType {BOOLEAN, CHAT_COLOR}

	public enum Setting {
		GLOBAL_CHAT("Global Chat", SettingType.BOOLEAN, null, Material.SIGN, true),
		PRIVATE_MESSAGES("Private Messages", SettingType.BOOLEAN, null, Material.BOOK_AND_QUILL, true),
		GLOBAL_CHAT_COLOR("Global Chat Color", SettingType.CHAT_COLOR, "badlion.lion", Material.PAINTING, ChatColor.WHITE);

		private String name;
		private SettingType settingType;
		private String permission;
		private Material material;
		private Object value;

		private List<ItemStack> items = new ArrayList<>();

		Setting(String name, SettingType settingType, String permission, Material material, Object value) {
			this.name = name;
			this.settingType = settingType;
			this.permission = permission;
			this.material = material;
			this.value = value;

			if (settingType == SettingType.CHAT_COLOR) {
				this.items.add(SettingsInventory.getDisplayNameColorItem("White", (short) 0));
				this.items.add(SettingsInventory.getDisplayNameColorItem("Yellow", (short) 4));
				this.items.add(SettingsInventory.getDisplayNameColorItem("Light Purple", (short) 2));
				this.items.add(SettingsInventory.getDisplayNameColorItem("Aqua", (short) 3));
				this.items.add(SettingsInventory.getDisplayNameColorItem("Green", (short) 5));
				this.items.add(SettingsInventory.getDisplayNameColorItem("Blue", (short) 3));
				this.items.add(SettingsInventory.getDisplayNameColorItem("Gold", (short) 1));
				this.items.add(SettingsInventory.getDisplayNameColorItem("Gray", (short) 8));
				this.items.add(SettingsInventory.getDisplayNameColorItem("Dark Gray", (short) 7));
				this.items.add(SettingsInventory.getDisplayNameColorItem("Dark Purple", (short) 10));
				this.items.add(SettingsInventory.getDisplayNameColorItem("Dark Aqua", (short) 9));
				this.items.add(SettingsInventory.getDisplayNameColorItem("Dark Green", (short) 13));
				this.items.add(SettingsInventory.getDisplayNameColorItem("Dark Blue", (short) 11));
				this.items.add(SettingsInventory.getDisplayNameColorItem("Black", (short) 15));
			}
		}

		public String getName() {
			return this.name;
		}

		public SettingType getSettingType() {
			return settingType;
		}

		public String getPermission() {
			return permission;
		}

		public Material getMaterial() {
			return material;
		}

		public Object getValue() {
			return this.value;
		}

		public List<ItemStack> getItems() {
			return items;
		}

	}

	public static class ChatSettings {

		private Player player;

		private String groupPrefix = "";

		// Channels
		private String activeChannel = "G";

		// PM
		private UUID lastPMSource;
		private ArrayList<Player> recentPMs = new ArrayList<>();

		// Settings
		private Map<UUID, Boolean> friendsList = new ConcurrentHashMap<>(); // Used as set
		private Map<UUID, Boolean> ignoredList = new ConcurrentHashMap<>(); // Used as set
		private Map<UUID, ChatColor> markedPlayers = new ConcurrentHashMap<>();
		private Map<Setting, Object> settings = new ConcurrentHashMap<>();

		public ChatSettings(Player player) {
			this.player = player;

			// Load default values for now in case database takes too long to load
			for (Setting setting : Setting.values()) {
				ChatSettings.this.settings.put(setting, setting.getValue());
			}

			ChatSettingsManager.chatSettings.put(player.getUniqueId(), this);
		}

		private void saveFriendsListAndIgnoredList() {
			SmellyChat.getInstance().getServer().getScheduler().runTaskAsynchronously(SmellyChat.getInstance(), new Runnable() {
				@Override
				public void run() {
					String query = "UPDATE smelly_chat_settings SET friends = ?, ignored = ? WHERE uuid = ?;";

					Connection connection = null;
					PreparedStatement ps = null;

					try {
						connection = Gberry.getConnection();
						ps = connection.prepareStatement(query);

						ps.setString(1, ChatSettings.this.serializeSet(ChatSettings.this.friendsList));
						ps.setString(2, ChatSettings.this.serializeSet(ChatSettings.this.ignoredList));
						ps.setString(3, ChatSettings.this.player.getUniqueId().toString());

						Gberry.executeUpdate(connection, ps);
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						Gberry.closeComponents(ps, connection);
					}
				}
			});
		}

		public void saveMarkedPlayers() {
			SmellyChat.getInstance().getServer().getScheduler().runTaskAsynchronously(SmellyChat.getInstance(), new Runnable() {
				@Override
				public void run() {
					String query = "UPDATE smelly_chat_settings SET marked_players = ? WHERE uuid = ?;";

					Connection connection = null;
					PreparedStatement ps = null;

					try {
						connection = Gberry.getConnection();
						ps = connection.prepareStatement(query);

						ps.setBytes(1, ChatSettings.this.serializeMarkedPlayers());
						ps.setString(2, ChatSettings.this.player.getUniqueId().toString());

						Gberry.executeUpdate(connection, ps);
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						Gberry.closeComponents(ps, connection);
					}
				}
			});
		}

		/**
		 * Needs to be called async
		 */
		private void fetchChatSettings(Connection connection) {
			String query = "SELECT * FROM smelly_chat_settings WHERE uuid = ?;";

			PreparedStatement ps = null;
			ResultSet rs = null;

			try {
				ps = connection.prepareStatement(query);

				ps.setString(1, ChatSettings.this.player.getUniqueId().toString());
				rs = Gberry.executeQuery(connection, ps);

				if (rs.next()) {
					ChatSettings.this.deserializeFriends(rs.getString("friends"));
					ChatSettings.this.deserializeIgnored(rs.getString("ignored"));
					ChatSettings.this.deserializeMarkedPlayers(rs.getBytes("marked_players"));
					ChatSettings.this.deserializeSettings(rs.getBytes("settings"));

					// TODO: SAFE CHECKING PERMISSIONS ASYNC?
					this.updateGroupPrefix();
				} else {
					// Load default settings
					for (Setting setting : Setting.values()) {
						ChatSettings.this.settings.put(setting, setting.getValue());
					}

					String query2 = "INSERT INTO smelly_chat_settings (uuid, settings) VALUES (?, ?);";

					ps = connection.prepareStatement(query2);

					ps.setString(1, ChatSettings.this.player.getUniqueId().toString());
					ps.setBytes(2, ChatSettings.this.serializeSettings());

					Gberry.executeUpdate(connection, ps);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				Gberry.closeComponents(rs, ps);
			}
		}

		public void updateGroupPrefix() {
			// Get GPermissions group
			GUser user = GPermissions.plugin.getUser(this.player.getUniqueId().toString());

			// Are they an admin/senior/manager or mod/trial on current server?
			if (this.player.hasPermission("badlion.admin") || this.player.hasPermission("badlion.kitsrmod")
					|| this.player.hasPermission("badlion.sgsrmod") || this.player.hasPermission("badlion.uhcsrhost")
					|| this.player.hasPermission("badlion.manager") || this.player.hasPermission("badlion.jrdev")
					|| this.player.hasPermission("badlion.kohisenior") || this.player.hasPermission("badlion.fsenior")) {
				this.groupPrefix = ChatColor.RESET + user.getGroup().getPrefix();
			} else if (this.player.hasPermission(SmellyChat.getInstance().getReportMessagePermission())) {
				// Figure out which group prefix we need to show

				if (user.getGroup().getPermissions().containsKey(SmellyChat.getInstance().getReportMessagePermission())) {
					this.groupPrefix = ChatColor.RESET + user.getGroup().getPrefix();
				} else {
					for (Group group : user.getSubgroups()) {
						if (group.getPermissions().containsKey(SmellyChat.getInstance().getReportMessagePermission())) {
							this.groupPrefix = ChatColor.RESET + group.getPrefix();

							break;
						}
					}
				}
			} else if (this.player.hasPermission("badlion.kitmod") || this.player.hasPermission("badlion.sgmod")
					|| this.player.hasPermission("badlion.uhchost") || this.player.hasPermission("badlion.kohimod")
					|| this.player.hasPermission("badlion.fmod")) { // Are they a mod on a server they have no power on?
				this.groupPrefix = ChatColor.DARK_GREEN + "[ChatMod]";
			} else if (this.player.hasPermission("badlion.kittrial") || this.player.hasPermission("badlion.sgtrial")
					|| this.player.hasPermission("badlion.uhctrial") || this.player.hasPermission("badlion.kohitrial")) { // Are they a trial on a server they have no power on?
				this.groupPrefix = "";
			} else { // Non staff
				// Players with no permission/group records have no GUser
				if (user != null && user.getGroup() != null) {
					this.groupPrefix = ChatColor.RESET + user.getGroup().getPrefix();
				}
			}
		}

		private String serializeSet(Map<UUID, Boolean> map) {
			StringBuilder sb = new StringBuilder();

			if (!map.isEmpty()) {
				for (UUID uuid : map.keySet()) {
					sb.append(uuid);
					sb.append(",");
				}

				String str = sb.toString();
				return str.substring(0, str.length() - 1);
			} else {
				return "";
			}
		}

		private void deserializeFriends(String str) {
			if (str != null && !str.isEmpty()) {
				String[] array = str.split(",");
				for (String s : array) {
					try {
						this.friendsList.put(UUID.fromString(s), true);
					} catch (IllegalArgumentException e) {
						// Sweep it under the rug boys
					}
				}
			}
		}

		private void deserializeIgnored(String str) {
			if (str != null) {
				String[] array = str.split(",");
				for (String s : array) {
					try {
						this.ignoredList.put(UUID.fromString(s), true);
					} catch (IllegalArgumentException e) {
						// Sweep it under the rug boys
					}
				}
			}
		}

		private byte[] serializeMarkedPlayers() {
			StringBuilder sb = new StringBuilder();
			for (UUID uuid : this.markedPlayers.keySet()) {
				sb.append(uuid);
				sb.append(":");
				sb.append(this.markedPlayers.get(uuid).name());
				sb.append(",");
			}

			try {
				return CompressionUtil.compress(sb.toString());
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		private void deserializeMarkedPlayers(byte[] bytes) {
			try {
				for (String str : CompressionUtil.decompress(bytes).split(",")) {
					if (!str.isEmpty()) {
						String[] array2 = str.split(":");

						this.markedPlayers.put(UUID.fromString(array2[0]), ChatColor.valueOf(array2[1]));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private byte[] serializeSettings() {
			StringBuilder sb = new StringBuilder();
			for (Setting setting : this.settings.keySet()) {
				sb.append(setting.name());
				sb.append(":");
				sb.append(this.settings.get(setting));
				sb.append(",");
			}

			try {
				return CompressionUtil.compress(sb.toString());
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		private void deserializeSettings(byte[] bytes) {
			try {
				for (String str : CompressionUtil.decompress(bytes).split(",")) {
					if (!str.isEmpty()) {
						try {
							String[] array2 = str.split(":");
							Setting setting = Setting.valueOf(array2[0]);
							if (setting.getValue() instanceof Boolean) {
								this.settings.put(setting, Boolean.valueOf(array2[1]));
							} else if (setting.getValue() instanceof ChatColor) {
								this.settings.put(setting, ChatColor.valueOf(array2[1]));
							}
						} catch (IllegalArgumentException e) {
							// They have a setting that we removed, don't save in cache so when we sync db, we get rid of it
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public String getGroupPrefix() {
			// Don't return prefix if player is disguised
			if (this.player.isDisguised()) return "";

			return this.groupPrefix;
		}

		public boolean isActiveChannel(String identifier) {
			return this.activeChannel.equalsIgnoreCase(identifier);
		}

		public String getActiveChannel() {
			return this.activeChannel;
		}

		public void setActiveChannel(String identifier) {
			this.activeChannel = identifier;
		}

		public void addRecentPM(Player sender) {
			this.recentPMs.add(sender);
			new RecentPMRemoveTask(sender, this).runTaskLater(SmellyChat.getInstance(), 40L);
		}

		public void removeRecentPM(Player player) {
			this.recentPMs.remove(player);
		}

		public boolean isSpammingPMs(Player player) {
			return this.recentPMs.contains(player);
		}

		public UUID getLastPMSource() {
			return this.lastPMSource;
		}

		public void setLastPMSource(UUID uuid) {
			this.lastPMSource = uuid;
		}

		public boolean isFriendsWith(Player player) {
			return this.isFriendsWith(player.getUniqueId());
		}

		public boolean isFriendsWith(UUID uuid) {
			return this.friendsList.containsKey(uuid);
		}

		public boolean addToFriendsList(Player player) {
			return this.addToFriendsList(player.getUniqueId());
		}

		public boolean addToFriendsList(UUID uuid) {
			if (this.friendsList.put(uuid, true) == null) {
				// Sync database
				this.saveFriendsListAndIgnoredList();

				return true;
			}

			return false;
		}

		public boolean removeFromFriendsList(Player player) {
			return this.removeFromFriendsList(player.getUniqueId());
		}

		public boolean removeFromFriendsList(UUID uuid) {
			if (this.friendsList.remove(uuid) != null) {
				// Sync database
				this.saveFriendsListAndIgnoredList();

				return true;
			}

			return false;
		}

		public Set<UUID> getFriendsList() {
			return friendsList.keySet();
		}

		public boolean isIgnoring(Player player) {
			return this.isIgnoring(player.getUniqueId());
		}

		public boolean isIgnoring(UUID uuid) {
			return this.ignoredList.containsKey(uuid);
		}

		public boolean addToIgnoredList(Player player) {
			return this.addToIgnoredList(player.getUniqueId());
		}

		public boolean addToIgnoredList(final UUID uuid) {
			if (this.ignoredList.put(uuid, true) == null) {
				// Sync database
				this.saveFriendsListAndIgnoredList();

				// Sync with MCP
				BukkitUtil.runTaskAsync(new Runnable() {
					@Override
					public void run() {
						JSONObject payload = new JSONObject();

						payload.put("uuid", ChatSettings.this.player.getUniqueId().toString());

						payload.put("type", "add_ignore");
						payload.put("data", uuid.toString());

						try {
							Gberry.contactMCP("player-chat-settings-change", payload);

						} catch (HTTPRequestFailException e) {
							Gberry.plugin.getLogger().warning(e.getResponseCode() + ": " + e.getResponse());
							e.printStackTrace();
						}
					}
				});

				return true;
			}

			return false;
		}

		public boolean removeFromIgnoredList(Player player) {
			return this.removeFromIgnoredList(player.getUniqueId());
		}

		public boolean removeFromIgnoredList(final UUID uuid) {
			if (this.ignoredList.remove(uuid) != null) {
				// Sync database
				this.saveFriendsListAndIgnoredList();

				// Sync with MCP
				BukkitUtil.runTaskAsync(new Runnable() {
					@Override
					public void run() {
						JSONObject payload = new JSONObject();

						payload.put("uuid", ChatSettings.this.player.getUniqueId().toString());

						payload.put("type", "remove_ignore");
						payload.put("data", uuid.toString());

						try {
							Gberry.contactMCP("player-chat-settings-change", payload);

						} catch (HTTPRequestFailException e) {
							Gberry.plugin.getLogger().warning(e.getResponseCode() + ": " + e.getResponse());
							e.printStackTrace();
						}
					}
				});

				return true;
			}

			return false;
		}

		public Set<UUID> getIgnoredList() {
			return ignoredList.keySet();
		}

		public void addMarkedPlayer(final UUID uuid, final ChatColor color) {
			this.markedPlayers.put(uuid, color);

			// Sync database
			this.saveMarkedPlayers();

			// Sync with MCP
			BukkitUtil.runTaskAsync(new Runnable() {
				@Override
				public void run() {
					JSONObject payload = new JSONObject();

					payload.put("uuid", ChatSettings.this.player.getUniqueId().toString());

					payload.put("type", "add_marked_player");

					List<String> data = new ArrayList<>();
					data.add(uuid.toString());

					// Only send the color code
					data.add(color.toString().substring(1));

					payload.put("data", data);

					try {
						Gberry.contactMCP("player-chat-settings-change", payload);

					} catch (HTTPRequestFailException e) {
						Gberry.plugin.getLogger().warning(e.getResponseCode() + ": " + e.getResponse());
						e.printStackTrace();
					}
				}
			});
		}


		public boolean removeMarkedPlayer(final UUID uuid) {
			if (this.markedPlayers.remove(uuid) != null) {
				// Sync database
				this.saveMarkedPlayers();

				// Sync with MCP
				BukkitUtil.runTaskAsync(new Runnable() {
					@Override
					public void run() {
						JSONObject payload = new JSONObject();

						payload.put("uuid", ChatSettings.this.player.getUniqueId().toString());

						payload.put("type", "remove_marked_player");
						payload.put("data", uuid.toString());

						try {
							Gberry.contactMCP("player-chat-settings-change", payload);

						} catch (HTTPRequestFailException e) {
							Gberry.plugin.getLogger().warning(e.getResponseCode() + ": " + e.getResponse());
							e.printStackTrace();
						}
					}
				});

				return true;
			}

			return false;
		}

		public ChatColor getMarkedPlayerColor(Player player) {
			// Check permission - KEEP AT TOP FOR OPTIMAL PERFORMANCE
			if (!this.player.hasPermission("badlion.lion")) {
				return null;
			}

			// Check if player is disguised
			// We only do this check in this method
			if (player.isDisguised()) {
				return null;
			}

			return this.getMarkedPlayerColor(player.getUniqueId());
		}

		/**
		 * WARNING: Does not check for disguised names
		 */
		public ChatColor getMarkedPlayerColor(UUID uuid) {
			return this.markedPlayers.get(uuid);
		}

		public Map<UUID, ChatColor> getMarkedPlayers() {
			return markedPlayers;
		}

		public Object getSetting(Setting setting) {
			return this.settings.get(setting);
		}

		public void setSetting(final Setting setting, final Object value) {
			this.settings.put(setting, value);

			// Sync with MCP
			BukkitUtil.runTaskAsync(new Runnable() {
				@Override
				public void run() {
					JSONObject payload = new JSONObject();

					payload.put("uuid", ChatSettings.this.player.getUniqueId().toString());

					if (setting == Setting.PRIVATE_MESSAGES) {
						payload.put("type", "pms_enabled");

						payload.put("data", value);
					} else if (setting == Setting.GLOBAL_CHAT) {
						payload.put("type", "global_chat_enabled");

						payload.put("data", value);
					} else if (setting == Setting.GLOBAL_CHAT_COLOR) {
						payload.put("type", "global_chat_color");

						// Only send the color code
						payload.put("data", value.toString().substring(1));
						System.out.println(value.toString().substring(1));
					}

					try {
						Gberry.contactMCP("player-chat-settings-change", payload);

					} catch (HTTPRequestFailException e) {
						Gberry.plugin.getLogger().warning(e.getResponseCode() + ": " + e.getResponse());
						e.printStackTrace();
					}
				}
			});

			SmellyChat.getInstance().getServer().getScheduler().runTaskAsynchronously(SmellyChat.getInstance(), new Runnable() {
				@Override
				public void run() {
					String query = "UPDATE smelly_chat_settings SET settings = ? WHERE uuid = ?;";

					Connection connection = null;
					PreparedStatement ps = null;

					try {
						connection = Gberry.getConnection();
						ps = connection.prepareStatement(query);

						ps.setBytes(1, ChatSettings.this.serializeSettings());
						ps.setString(2, ChatSettings.this.player.getUniqueId().toString());

						Gberry.executeUpdate(connection, ps);
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						Gberry.closeComponents(ps, connection);
					}
				}
			});
		}

	}

	public static class SettingsInventory {

		private static SettingsScreenHandler settingsScreenHandler;
		private static SettingsChangeScreenHandler settingsChangeScreenHandler;

		private static Map<ItemStack, String> settingItems = new HashMap<>();

		public static void initialize() {
			// Create the screen handlers
			SettingsInventory.settingsScreenHandler = new SettingsScreenHandler();
			SettingsInventory.settingsChangeScreenHandler = new SettingsChangeScreenHandler();

			// Create setting items
			for (Setting setting : Setting.values()) {
				SettingsInventory.settingItems.put(
						ItemStackUtil.createItem(setting.getMaterial(), ChatColor.GREEN + "Manage " + setting.getName()),
						setting.getPermission());
			}
		}

		private static ItemStack getDisplayNameColorItem(String colorName, short woolColor) {
			return ItemStackUtil.createItem(Material.WOOL, woolColor, ChatColor.GREEN + "Choose " + colorName);
		}

		public static void openSettingsInventory(Player player) {
			// Create smelly inventory
			SmellyInventory smellyInventory = new SmellyInventory(SettingsInventory.settingsScreenHandler, 18,
					ChatColor.BOLD + ChatColor.AQUA.toString() + "Chat Settings");

			ChatSettings chatSettings = ChatSettingsManager.getChatSettings(player);

			// Add the items to the inventory
			for (ItemStack settingItem : SettingsInventory.settingItems.keySet()) {
				String permission = SettingsInventory.settingItems.get(settingItem);

				// Only display this setting if player has permission to change the setting
				if (permission == null || player.hasPermission(permission)) {
					// Which setting does this item represent?
					Setting setting = Setting.valueOf(settingItem.getItemMeta().getDisplayName().substring(9).replaceAll(" ", "_").toUpperCase());

					// Clone the item and show current setting in the lore
					ItemStack item = settingItem.clone();
					ItemMeta itemMeta = item.getItemMeta();
					List<String> lore = new ArrayList<>();
					if (setting.getValue() instanceof Boolean) {
						if ((boolean) chatSettings.getSetting(setting)) {
							lore.add(ChatColor.YELLOW + "Currently Enabled");
						} else {
							lore.add(ChatColor.YELLOW + "Currently Disabled");
						}
					} else if (setting.getValue() instanceof ChatColor) {
						lore.add(ChatColor.YELLOW + "Current Color: " + ((ChatColor) chatSettings.getSetting(setting)).name().replaceAll("_", " ").toLowerCase());
					}
					itemMeta.setLore(lore);
					item.setItemMeta(itemMeta);

					smellyInventory.getMainInventory().addItem(item);
				}
			}

			// Open the inventory
			BukkitUtil.openInventory(player, smellyInventory.getMainInventory());
		}

		private static class SettingsScreenHandler implements SmellyInventory.SmellyInventoryHandler {

			@Override
			public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
				// Which setting does this item represent?
				Setting setting = Setting.valueOf(item.getItemMeta().getDisplayName().substring(9).replaceAll(" ", "_").toUpperCase());

				// Don't allow staff members to disable PMs
				if (setting == Setting.PRIVATE_MESSAGES && player.hasPermission("badlion.staff") && !player.hasPermission("badlion.admin")
						&& !player.hasPermission("badlion.developer")) {
					player.sendMessage(ChatColor.RED + "You cannot disable PMs!");
					return;
				}

				// If setting is a boolean, toggle it
				if (setting.getSettingType() == SettingType.BOOLEAN) {
					ChatSettings chatSettings = ChatSettingsManager.getChatSettings(player);

					boolean newValue = !(boolean) chatSettings.getSetting(setting);
					chatSettings.setSetting(setting, newValue);

					if (newValue) {
						player.sendMessage(ChatColor.YELLOW + "You have enabled " + setting.getName() + ".");
					} else {
						player.sendMessage(ChatColor.YELLOW + "You have disabled " + setting.getName() + ".");
					}


					BukkitUtil.closeInventory(player);
					return;
				}

				// Create sub inventory
				Inventory inventory = fakeHolder.getSmellyInventory().createInventory(fakeHolder, SettingsInventory.settingsChangeScreenHandler, 0, 18,
						ChatColor.BOLD + ChatColor.AQUA.toString() + ChatColor.stripColor(item.getItemMeta().getDisplayName()));

				// Add the items for this setting
				for (ItemStack item2 : setting.getItems()) {
					inventory.addItem(item2);
				}

				// Open inventory
				BukkitUtil.openInventory(player, inventory);
			}

			@Override
			public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

			}

		}

		private static class SettingsChangeScreenHandler implements SmellyInventory.SmellyInventoryHandler {

			@Override
			public void handleInventoryClickEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryClickEvent event, ItemStack item, int slot) {
				ChatSettings chatSettings = ChatSettingsManager.getChatSettings(player);

				// Which setting does this item represent?
				Setting setting = Setting.valueOf(event.getInventory().getName().substring(11).replaceAll(" ", "_").toUpperCase());

				/*if (setting.getSettingType() == SettingType.BOOLEAN) {
					if (slot == 0) { // Enable
						boolean currentlyEnabled = (boolean) chatSettings.getSetting(setting);

						if (currentlyEnabled) {
							player.sendMessage(ChatColor.YELLOW + setting.getName() + " are already enabled.");
						} else {
							chatSettings.setSetting(setting, true);
							player.sendMessage(ChatColor.YELLOW + "You have enabled " + setting.getName() + ".");
						}
					} else { // Disable
						boolean currentlyEnabled = (boolean) chatSettings.getSetting(setting);

						if (currentlyEnabled) {
							chatSettings.setSetting(setting, false);
							player.sendMessage(ChatColor.YELLOW + "You have disabled " + setting.getName() + ".");
						} else {
							player.sendMessage(ChatColor.YELLOW + setting.getName() + " are already disabled.");
						}
					}
				} else */
				if (setting.getSettingType() == SettingType.CHAT_COLOR) {
					// Which color does this item represent?
					ChatColor color = ChatColor.valueOf(item.getItemMeta().getDisplayName().substring(9).replaceAll(" ", "_").toUpperCase());
					ChatColor currentColor = (ChatColor) chatSettings.getSetting(setting);

					if (color != currentColor) {
						chatSettings.setSetting(setting, color);
						player.sendMessage(ChatColor.YELLOW + "You have set the color of " + setting.getName() + " to "
								+ color + item.getItemMeta().getDisplayName().substring(9) + ".");
					} else {
						player.sendMessage(ChatColor.YELLOW + "The color of " + setting.getName() + " is already set to "
								+ color + item.getItemMeta().getDisplayName().substring(9) + ".");
					}
				}

				BukkitUtil.closeInventory(player);
			}

			@Override
			public void handleInventoryCloseEvent(SmellyInventory.FakeHolder fakeHolder, Player player, InventoryCloseEvent event) {

			}

		}

	}

}
