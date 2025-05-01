package net.badlion.bungeelobby;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.badlion.bungeelobby.commands.BIPCommand;
import net.badlion.bungeelobby.commands.ReloadMOTDsCommand;
import net.badlion.bungeelobby.commands.ManagerServerCommand;
import net.badlion.bungeelobby.commands.SendPlayersToMCPCommand;
import net.badlion.bungeelobby.commands.SendToAllCommand;
import net.badlion.bungeelobby.commands.StatusCommand;
import net.badlion.bungeelobby.listeners.MCPListener;
import net.badlion.bungeelobby.listeners.PlayerLoginListener;
import net.badlion.bungeelobby.managers.MCPManager;
import net.badlion.bungeelobby.tasks.MCPProxyPingTask;
import net.badlion.bungeelobby.tasks.RebootTimeTask;
import net.badlion.common.GetCommon;
import net.badlion.common.libraries.HTTPCommon;
import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

// Referrence for more advanced program: http://forums.bukkit.org/threads/serializing-itemmeta-and-all-your-wildest-dreams.137325/

public class BungeeLobby extends Plugin implements Listener {
	
	private String url;
	private String user;
	private String pass;
	private String db;
	private String fullURL;
	//private FileConfiguration fileConfiguration;
	//public static BoneCP connectionPool;
	public static HikariConfig hikariConfig;
	public static HikariDataSource hikariDataSource;

	public static HikariConfig slowHikariConfig;
	public static HikariDataSource slowHikariDataSource;

    public static BungeeConfig config;
    public static BungeeConfig motdsConfig;
    public static BungeeLobby plugin;

	public static String BUNGEE_NAME;

    public static String archAPIURL = "";
    public static String archAPIKey = "";
    public static String archAPIUUID = "";

	public static boolean cloudflareEnabled = true;
	public static String cloudflareKey = "";
	public static String cloudflareEmail = "";
	public static int delete_record = 1;
	public static String cloudflareIP = "";
	public static String cloudflareName = "";
	public static int oddDay = 0;
	public static int timeForReboot = 0;

	public static Runnable timeTask;

	public static DateTime restartTime;

    public static ScheduledTask MOTDTask;

	public static String mcpURL = "";
	public static String mcpKey = "";
	public static int mcpTimeout = 2000;

	public static String motd = "";
	public static ServerPing.Players players = new ServerPing.Players(0, 0, new ServerPing.PlayerInfo[0]);

    public BungeeLobby() {
        BungeeLobby.plugin = this;
    }

	public static BungeeLobby getInstance() {
		return BungeeLobby.plugin;
	}

	public static boolean DEBUG = false;

	@Override
	public void onEnable() {
        BungeeLobby.config = new BungeeConfig(this);
        BungeeLobby.motdsConfig = new BungeeConfig(this, "motds.yml");

        BungeeLobby.archAPIURL = BungeeLobby.config.getConfig().getString("archapi.url");
        BungeeLobby.archAPIUUID = UUID.randomUUID().toString();
        BungeeLobby.archAPIKey = BungeeLobby.config.getConfig().getString("archapi.key");

		BungeeLobby.cloudflareEnabled = BungeeLobby.config.getConfig().getBoolean("cloudflare.enabled", true);
		BungeeLobby.cloudflareKey = BungeeLobby.config.getConfig().getString("cloudflare.key");
		BungeeLobby.cloudflareEmail = BungeeLobby.config.getConfig().getString("cloudflare.email");
		BungeeLobby.delete_record = BungeeLobby.config.getConfig().getInt("cloudflare.delete");
		BungeeLobby.cloudflareIP = BungeeLobby.config.getConfig().getString("cloudflare.ip");
		BungeeLobby.cloudflareName = BungeeLobby.config.getConfig().getString("cloudflare.name");
		BungeeLobby.oddDay = BungeeLobby.config.getConfig().getInt("cloudflare.odd");
		BungeeLobby.timeForReboot = BungeeLobby.config.getConfig().getInt("cloudflare.time");

		BungeeLobby.DEBUG = BungeeLobby.config.getConfig().getBoolean("debug", false);
		BungeeLobby.BUNGEE_NAME = BungeeLobby.config.getConfig().getString("name");

		BungeeLobby.mcpURL = BungeeLobby.config.getConfig().getString("mcp.url");
		BungeeLobby.mcpKey = BungeeLobby.config.getConfig().getString("mcp.key");
		BungeeLobby.mcpTimeout = BungeeLobby.config.getConfig().getInt("mcp.time_out", 2000);

		// TODO: Remove legacy system once we have moved everything away from ArchyPi
        BungeeCord.getInstance().getScheduler().schedule(BungeeLobby.plugin, new Runnable() {
			@Override
			public void run() {
				JSONObject jsonObject = new JSONObject();
				Map<String, Integer> serversMap = new HashMap<>();
				Map<String, String> playersMap = new HashMap<>();
				for (Map.Entry<String, ServerInfo> servers : BungeeCord.getInstance().getServersCopy().entrySet()) {
					serversMap.put(servers.getKey(), servers.getValue().getPlayers().size());
				}

				for (ProxiedPlayer proxiedPlayer : BungeeCord.getInstance().getPlayers()) {
					if (proxiedPlayer.getServer() == null) {
						continue;
					}

					playersMap.put(proxiedPlayer.getUniqueId().toString(), proxiedPlayer.getServer().getInfo().getName());
				}

				jsonObject.put("servers", serversMap);
				jsonObject.put("players", playersMap);

				try {
					BungeeLobby.executeArchAPIPUTRequest(BungeeLobby.archAPIURL + "UpdatePlayerCounts", jsonObject);
				} catch (HTTPRequestFailException e) {
					// Do nothing
				}
			}
		}, 5, 5, TimeUnit.SECONDS);

		// Connect to DB
		this.initializeDB();

		if (BungeeLobby.cloudflareEnabled) {
			restartTime = new DateTime(DateTimeZone.UTC);

			long oddVSeven = restartTime.getMillis() / 1000 / 86400;

			restartTime = restartTime.withHourOfDay(timeForReboot);
			restartTime = restartTime.withMinuteOfHour(0);
			restartTime = restartTime.withSecondOfMinute(0);

			if (oddVSeven % 2 == oddDay) {
				DateTime currentTime = new DateTime(DateTimeZone.UTC);
				if (currentTime.isAfter(restartTime)) {
					restartTime = restartTime.plusHours(48);
				}
			} else {
				restartTime = restartTime.plusHours(24);

			}

			ProxyServer.getInstance().getLogger().info("Next reboot scheduled for - " + restartTime.toString());

			try {
				String rec_id = getRecID(BungeeLobby.cloudflareIP);
				if (rec_id.equals("-2")) {
					BungeeCord.getInstance().stop();
				} else if (rec_id.equals("-1")) {
					String urlString = "https://www.cloudflare.com/api_json.html?a=rec_new&z=badlion.net&ttl=1&type=A"
											   + "&tkn=" + BungeeLobby.cloudflareKey
											   + "&email=" + BungeeLobby.cloudflareEmail
											   + "&content=" + BungeeLobby.cloudflareIP
											   + "&name=" + BungeeLobby.cloudflareName;
					JSONObject json = new JSONObject();

					JSONObject response = HTTPCommon.executePOSTRequest(urlString, json, 60000);
					if (response == null) {
						BungeeCord.getInstance().stop();
					} else if (response.containsKey("result")) {
						if (!((String) response.get("result")).equals("success")) {
							BungeeCord.getInstance().stop();
						}
					}
				}
			} catch (HTTPRequestFailException e) {
				BungeeCord.getInstance().stop();
			}

			BungeeLobby.timeTask = new RebootTimeTask(this);
			BungeeLobby.plugin.getProxy().getScheduler().schedule(this, timeTask, 10, 10, TimeUnit.SECONDS);
		}

		// Listeners
		ProxyServer.getInstance().getPluginManager().registerListener(this, new MCPListener());
		ProxyServer.getInstance().getPluginManager().registerListener(this, new PlayerLoginListener());

		// Commands
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new BIPCommand());
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new ManagerServerCommand());
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new ReloadMOTDsCommand());
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new SendPlayersToMCPCommand());
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new SendToAllCommand());
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new StatusCommand());

		// MCP Boot Sequence
		JSONObject response = MCPManager.bootMCP();
		if (response == null || !response.equals(MCPManager.successResponse)) {
			BungeeLobby.getInstance().getLogger().info("ERROR WHEN CONTACTING MCP ON BOOT");

			// Shut down at the start
			BungeeCord.getInstance().stop();

			return;
		}

		// Kick off keep alive task
		MCPManager.startKeepAlive();

		ProxyServer.getInstance().getScheduler().schedule(this, new MCPProxyPingTask(), 0, 15, TimeUnit.SECONDS);
	}

	@Override
	public void onDisable() {
		if (BungeeLobby.MOTDTask != null) {
			BungeeLobby.MOTDTask.cancel();
		}

		// MCP Shutdown sequence
		JSONObject response = MCPManager.shutdownMCP();
		if (response == null || !response.equals(MCPManager.successResponse)) {
			BungeeLobby.getInstance().getLogger().info("ERROR WHEN SHUTTING DOWN CONTACTING MCP");
		}
	}

    public static JSONObject executeArchAPIGETRequest(String urlString) throws HTTPRequestFailException {
        urlString += "/" + BungeeLobby.archAPIUUID + "/" + BungeeLobby.archAPIKey;
        return HTTPCommon.executeGETRequest(urlString);
    }

    public static JSONObject executeArchAPIPUTRequest(String urlString, JSONObject json) throws HTTPRequestFailException {
        urlString += "/" + BungeeLobby.archAPIUUID + "/" + BungeeLobby.archAPIKey;
        return HTTPCommon.executePUTRequest(urlString, json);
    }

    public static JSONObject executeArchAPIPUTWithoutUUIDRequest(String urlString, JSONObject json) throws HTTPRequestFailException {
        urlString += "/" + BungeeLobby.archAPIKey;
        return HTTPCommon.executePUTRequest(urlString, json);
    }

    public static long toLongIP(byte[] bytes) {
        long val = 0;
        for (int i = 0; i < bytes.length; i++) {
            val <<= 8;
            val |= bytes[i] & 0xff;
        }
        return val;
    }
	
	public void initializeDB() {
		url = BungeeLobby.config.getConfig().getString("lobby.db_url");
		user = BungeeLobby.config.getConfig().getString("lobby.db_user");
		pass = BungeeLobby.config.getConfig().getString("lobby.db_pass");
		db = BungeeLobby.config.getConfig().getString("lobby.db_db");
		fullURL = "jdbc:postgresql://" + url + "/" + db;
		
		if (url == null || user == null || pass == null) {
			this.getLogger().severe("no db login found.");
			return;
		}

		this.getLogger().info("XXXXXXXXX LOADING DB CONNECTION.");

		// Run ASYNC to stop bungeelobby from crying
		BungeeCord.getInstance().getScheduler().runAsync(this, new Runnable() {
			@Override
			public void run() {
				hikariConfig = new HikariConfig();
				hikariConfig.setJdbcUrl(fullURL);
				hikariConfig.setUsername(user);
				hikariConfig.setPassword(pass);
				hikariConfig.setConnectionTimeout(2 * 1000);
				hikariConfig.setIdleTimeout(60 * 1000);
				hikariConfig.setMaxLifetime(300 * 1000);
				hikariConfig.setMinimumIdle(1);
				hikariConfig.setMaximumPoolSize(40);

				try {
					hikariDataSource = new HikariDataSource(hikariConfig);
				} catch (Exception e) {
					e.printStackTrace();
					BungeeCord.getInstance().stop();
				}

				slowHikariConfig = new HikariConfig();
				slowHikariConfig.setJdbcUrl("jdbc:postgresql://" + GetCommon.getIpForSlowDB() + "/badlion_slow");
				slowHikariConfig.setUsername("badlion_slow");
				slowHikariConfig.setPassword("9g4BnhNPti8iSr1u");
				slowHikariConfig.setConnectionTimeout(2 * 1000);
				slowHikariConfig.setIdleTimeout(60 * 1000);
				slowHikariConfig.setMaxLifetime(300 * 1000);
				slowHikariConfig.setMinimumIdle(1);
				slowHikariConfig.setMaximumPoolSize(40);

				try {
					slowHikariDataSource = new HikariDataSource(slowHikariConfig);
				} catch (Exception e) {
					e.printStackTrace();
					BungeeCord.getInstance().stop();
				}
			}
		});
	}



	public static Connection getConnection() throws SQLException {
		return hikariDataSource.getConnection();
	}

	public static Connection getSlowConnection() throws SQLException {
		return slowHikariDataSource.getConnection();
	}

	public static void closeComponents(ResultSet rs, PreparedStatement ps) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void closeComponents(ResultSet rs, PreparedStatement ps, Connection connection) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void closeComponents(PreparedStatement ps, Connection connection) {
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void closeComponents(PreparedStatement ps) {
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void closeComponents(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public String getRecID(String content) throws HTTPRequestFailException {
		String urlString = "https://www.cloudflare.com/api_json.html?a=rec_load_all&z=badlion.net&tkn=" +
				BungeeLobby.cloudflareKey + "&email=" + BungeeLobby.cloudflareEmail;
		JSONObject json = new JSONObject();

		JSONObject response = HTTPCommon.executePOSTRequest(urlString, json, 60000);
		if (response != null) {
			if (response.containsKey("response")) {
				JSONObject records = (JSONObject) response.get("response");
				if (records.containsKey("recs")) {
					records = (JSONObject) records.get("recs");
					if (records.containsKey("objs")) {
						JSONArray array = (JSONArray) records.get("objs");

						for (JSONObject record : (List<JSONObject>) array) {
							if (((String) record.get("content")).equals(content)) {
								return (String) record.get("rec_id");
							}
						}
					} else {
						return "-2";
					}
				} else {
					return "-2";
				}
			} else {
				return "-2";
			}
		} else {
			return "-2";
		}
		return "-1";
	}

	public static int getJSONInteger(JSONObject jsonObject, String key) {
		return getObjectInteger(jsonObject.get(key));
	}

	public static int getObjectInteger(Object object) {
		if (object instanceof Integer) {
			return (int) object;
		} else if (object instanceof Long) {
			return (int) ((long) object);
		}

		throw new RuntimeException("Invalid integer given " + object);
	}

}
