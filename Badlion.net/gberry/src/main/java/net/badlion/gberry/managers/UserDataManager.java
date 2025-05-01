package net.badlion.gberry.managers;

import net.badlion.gberry.GMap;
import net.badlion.gberry.GMapManager;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.events.FinishedUserDataEvent;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.Pair;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.postgresql.util.PGobject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UserDataManager implements GMap<UserDataManager.UserData>, Listener {

	private static Map<UUID, UserData> userMap = new ConcurrentHashMap<>();
	private static ConcurrentLinkedQueue<UserDataUpdate> pendingUpdates = new ConcurrentLinkedQueue<>();

	public static void initialize() {
		// For debugging
		new BukkitRunnable() {
			@Override
			public void run() {
				UserDataManager.executeUpdates();
			}
		}.runTaskTimerAsynchronously(Gberry.plugin, 40, 40);

		UserDataManager userDataManager = new UserDataManager();
		Bukkit.getServer().getPluginManager().registerEvents(userDataManager, Gberry.plugin);
		GMapManager.getInstance().register(userDataManager);
	}

	@EventHandler
	public void onAsyncPlayerPreLoginEvent(final AsyncPlayerPreLoginEvent event) {
		UserData userData = UserDataManager.getUserDataFromDB(event.getUniqueId());

		// Error
		if (userData == null) {
			event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
			event.setKickMessage("Error loading data. If this issue persists contact support@badlion.net");
		} else {
			UserDataManager.userMap.put(event.getUniqueId(), userData);
		}

		// Call the user data event
		BukkitUtil.runTask(new Runnable() {
			@Override
			public void run() {
				Gberry.plugin.getServer().getPluginManager().callEvent(new FinishedUserDataEvent(event.getUniqueId()));
			}
		});
	}

	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		UserDataManager.userMap.remove(event.getPlayer().getUniqueId());
	}

	public static UserData getUserData(Player player) {
		return UserDataManager.getUserData(player.getUniqueId());
	}

	public static UserData getUserData(UUID uuid) {
		return UserDataManager.userMap.get(uuid);
	}

	private static void addPendingUpdate(UserDataUpdate userDataUpdate) {
		UserDataManager.pendingUpdates.add(userDataUpdate);
	}

	@Override
	public String getName() {
		return "user_data";
	}

	@Override
	public Map<UUID, UserData> getMap() {
		return UserDataManager.userMap;
	}

	/**
	 * Call ASYNC
	 */
	public static UserData getUserDataFromDB(UUID uuid) {
		String query = "SELECT * FROM user_data WHERE uuid = ?;";

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		UserData userData = null;

		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);

			ps.setString(1, uuid.toString());
			rs = Gberry.executeQuery(connection, ps);

			if (rs.next()) {
				JSONParser parser = new JSONParser();
				JSONObject cosmetics = new JSONObject();
				JSONObject cases = new JSONObject();
				JSONObject sgSettings = new JSONObject();
				JSONObject disguiseSettings = new JSONObject();
				JSONObject chatSettings = new JSONObject();
				JSONObject arenaSettings = new JSONObject();

				try {
					// Parse all our json
					cosmetics = ((JSONObject) parser.parse(rs.getString("cosmetics")));
					cases = ((JSONObject) parser.parse(rs.getString("cases")));
					sgSettings = ((JSONObject) parser.parse(rs.getString("sg_settings")));
					disguiseSettings = ((JSONObject) parser.parse(rs.getString("disguise_settings")));
					chatSettings = ((JSONObject) parser.parse(rs.getString("chat_settings")));
					arenaSettings = (JSONObject) parser.parse(rs.getString("arena_settings"));
				} catch (ParseException e) {
					e.printStackTrace();
				}

				userData = new UserData(uuid, rs.getInt("currency"), rs.getBoolean("player_visibility"), rs.getBoolean("lobby_flight"), cosmetics, cases, sgSettings, disguiseSettings, chatSettings, arenaSettings);
			} else {
				String query2 = "INSERT INTO user_data (uuid, currency, player_visibility, cosmetics, cases, sg_settings, disguise_settings, chat_settings, arena_settings) VALUES (?, 0, true, '{}', '{}', '{}', '{}', '{}', '{}');";
				ps = connection.prepareStatement(query2);

				ps.setString(1, uuid.toString());
				Gberry.executeUpdate(connection, ps);

				userData = new UserData(uuid, 0, true, false, new JSONObject(), new JSONObject(), new JSONObject(), new JSONObject(), new JSONObject(), new JSONObject());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(rs, ps, connection);
		}

		return userData;
	}

	private static void executeUpdates() {
		if (UserDataManager.pendingUpdates.size() == 0) {
			return;
		}

		// Create pairs of data for queries
		List<UserDataUpdate> queries = new ArrayList<>();
		Iterator<UserDataUpdate> iterator = UserDataManager.pendingUpdates.iterator();
		while (iterator.hasNext()) {
			queries.add(iterator.next());
			iterator.remove();
		}

		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = Gberry.getConnection();

			for (UserDataUpdate userDataUpdate : queries) {
				// Generate a query
				int i = 1;
				List<Object> objects = new ArrayList<>();
				StringBuilder builder = new StringBuilder();
				builder.append("UPDATE user_data SET ");

				boolean flag = false;
				for (Pair<String, Object> pair : userDataUpdate.getData()) {
					if (flag) {
						builder.append(", ");
					}

					// Build Query
					builder.append(pair.getA());
					builder.append(" = ?");

					// Store for later
					objects.add(pair.getB());
					flag = true;
				}

				builder.append(" WHERE uuid = ?;");

				// Add the params to the query
				//Bukkit.getLogger().info(builder.toString());
				ps = connection.prepareStatement(builder.toString());
				for (Object o : objects) {
					ps.setObject(i++, o);
				}

				ps.setString(i, userDataUpdate.getUserData().getUUID().toString());

				Gberry.executeUpdate(connection, ps);

				// Did a player's disguised settings change?
				for (Pair<String, Object> pair : userDataUpdate.getData()) {
					if (pair.getA().equals("disguise_settings")) {
						// Update the player's chat prefix in MCP
						Gberry.sendChatPlayerPrefixChange(userDataUpdate.getUserData().getUUID());
						break;
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(ps, connection);
		}
	}

	public static class UserData {

		private UUID uuid;

		private int currency;
		private boolean playerVisibility;
		private boolean lobbyFlight;

		private JSONObject cases;
		private JSONObject cosmetics;

		private JSONObject sgSettings;

		private JSONObject disguiseSettings;

		private JSONObject chatSettings;

		private JSONObject arenaSettings;

		public UserData(UUID uuid, int currency, boolean playerVisibility, boolean lobbyFlight, JSONObject cosmetics,
		                JSONObject cases, JSONObject sgSettings, JSONObject disguiseSettings,
		                JSONObject chatSettings, JSONObject arenaSettings) {
			this.uuid = uuid;

			this.currency = currency;
			this.playerVisibility = playerVisibility;
			this.lobbyFlight = lobbyFlight;

			this.cases = cases;
			this.cosmetics = cosmetics;

			this.sgSettings = sgSettings;

			this.disguiseSettings = disguiseSettings;

			this.chatSettings = chatSettings;

			this.arenaSettings = arenaSettings;

			// Insert default values
			if (!sgSettings.containsKey("rating_visibility")) {
				sgSettings.put("rating_visibility", true);
			}

			if (!sgSettings.containsKey("stats_visibility")) {
				sgSettings.put("stats_visibility", true);
			}

			if (!disguiseSettings.containsKey("is_disguised")) {
				disguiseSettings.put("is_disguised", false);
			}
		}

		public UUID getUUID() {
			return this.uuid;
		}

		public boolean arePlayersVisible() {
			return playerVisibility;
		}

		public void setArePlayersVisible(boolean playerVisibility) {
			this.playerVisibility = playerVisibility;

			new UserDataUpdate(this, Pair.of("player_visibility", (Object) this.playerVisibility)).queue();
		}

		public boolean isLobbyFlight() {
			return this.lobbyFlight;
		}

		public void setLobbyFlight(boolean lobbyFlight) {
			this.lobbyFlight = lobbyFlight;

			new UserDataUpdate(this, Pair.of("lobby_flight", (Object) this.lobbyFlight)).queue();
		}

		public int getCurrency() {
			return this.currency;
		}

		public void addCurrency(int num) {
			this.currency += num;

			new UserDataUpdate(this, Pair.of("currency", (Object) this.currency)).queue();
		}

		public JSONObject getCases() {
			return cases;
		}

		public void setCases(JSONObject cases, boolean update) {
			this.cases = cases;

			PGobject jsonWrapper = new PGobject();
			jsonWrapper.setType("json");

			try {
				jsonWrapper.setValue(cases.toJSONString());
			} catch (SQLException e) {
				e.printStackTrace();
				return;
			}

			if (update) {
				new UserDataUpdate(this, Pair.of("cases", (Object) jsonWrapper)).queue();
			}
		}

		public JSONObject getCosmetics() {
			return this.cosmetics;
		}

		public void setCosmetics(JSONObject cosmetics, boolean update) {
			this.cosmetics = cosmetics;

			if (update) {
				PGobject jsonWrapper = new PGobject();
				jsonWrapper.setType("json");

				try {
					jsonWrapper.setValue(cosmetics.toJSONString());
				} catch (SQLException e) {
					e.printStackTrace();
					return;
				}

				new UserDataUpdate(this, Pair.of("cosmetics", (Object) jsonWrapper)).queue();
			}
		}

		public JSONObject getSGSettings() {
			return this.sgSettings;
		}

		public void setSGSettings(JSONObject sgSettings, boolean update) {
			this.sgSettings = sgSettings;

			if (update) {
				PGobject jsonWrapper = new PGobject();
				jsonWrapper.setType("json");

				try {
					jsonWrapper.setValue(sgSettings.toJSONString());
				} catch (SQLException e) {
					e.printStackTrace();
					return;
				}

				new UserDataUpdate(this, Pair.of("sg_settings", (Object) jsonWrapper)).queue();
			}
		}

		public JSONObject getDisguiseSettings() {
			return this.disguiseSettings;
		}

		public void setDisguiseSettings(JSONObject disguiseSettings, boolean update) {
			this.disguiseSettings = disguiseSettings;

			if (update) {
				PGobject jsonWrapper = new PGobject();
				jsonWrapper.setType("json");

				try {
					jsonWrapper.setValue(disguiseSettings.toJSONString());
				} catch (SQLException e) {
					e.printStackTrace();
					return;
				}

				new UserDataUpdate(this, Pair.of("disguise_settings", (Object) jsonWrapper)).queue();
			}
		}

		public JSONObject getChatSettings() {
			return this.chatSettings;
		}

		public void setChatSettings(JSONObject chatSettings, boolean update) {
			this.chatSettings = chatSettings;

			if (update) {
				PGobject jsonWrapper = new PGobject();
				jsonWrapper.setType("json");

				try {
					jsonWrapper.setValue(chatSettings.toJSONString());
				} catch (SQLException e) {
					e.printStackTrace();
					return;
				}

				new UserDataUpdate(this, Pair.of("chat_settings", (Object) jsonWrapper)).queue();
			}
		}

		public JSONObject getArenaSettings() {
			return this.arenaSettings;
		}

		public void setArenaSettings(JSONObject arenaSettings, boolean update) {
			this.arenaSettings = arenaSettings;

			if (update) {
				PGobject jsonWrapper = new PGobject();
				jsonWrapper.setType("json");

				try {
					jsonWrapper.setValue(arenaSettings.toJSONString());
				} catch (SQLException e) {
					e.printStackTrace();
					return;
				}

				new UserDataUpdate(this, Pair.of("arena_settings", (Object) jsonWrapper)).queue();
			}
		}

	}

	private static class UserDataUpdate {

		private UserData userData;

		private List<Pair<String, Object>> data = new ArrayList<>();

		@SafeVarargs
		public UserDataUpdate(UserData userData, Pair<String, Object>... data) {
			this.userData = userData;

			Collections.addAll(this.data, data);
		}

		public void queue() {
			UserDataManager.addPendingUpdate(this);
		}

		public UserData getUserData() {
			return userData;
		}

		public List<Pair<String, Object>> getData() {
			return data;
		}
	}

}
