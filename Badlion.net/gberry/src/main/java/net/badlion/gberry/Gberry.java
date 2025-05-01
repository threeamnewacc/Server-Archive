package net.badlion.gberry;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.badlion.common.GetCommon;
import net.badlion.common.libraries.HTTPCommon;
import net.badlion.common.libraries.IPCommon;
import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.badlion.gberry.commands.ArmorSeeCommand;
import net.badlion.gberry.commands.AutoMuteCommand;
import net.badlion.gberry.commands.ClearChatCommand;
import net.badlion.gberry.commands.DiscordCommand;
import net.badlion.gberry.commands.ExtractCommand;
import net.badlion.gberry.commands.GetMapCommand;
import net.badlion.gberry.commands.GinieCommand;
import net.badlion.gberry.commands.HackerCommand;
import net.badlion.gberry.commands.InvSeeCommand;
import net.badlion.gberry.commands.ListHideCommand;
import net.badlion.gberry.commands.LoadFiltersCommand;
import net.badlion.gberry.commands.LoadSettingsCommand;
import net.badlion.gberry.commands.LoggerCommand;
import net.badlion.gberry.commands.MaxPlayerCommand;
import net.badlion.gberry.commands.PunishmentsCommand;
import net.badlion.gberry.commands.ServerStatsCommand;
import net.badlion.gberry.commands.SudoCommand;
import net.badlion.gberry.commands.WhereCommand;
import net.badlion.gberry.enchantments.GlowEnchantment;
import net.badlion.gberry.events.SettingsLoadedEvent;
import net.badlion.gberry.listeners.BugListener;
import net.badlion.gberry.listeners.ChatListener;
import net.badlion.gberry.listeners.CommandListener;
import net.badlion.gberry.listeners.CompromisedAccountListener;
import net.badlion.gberry.listeners.InvSeeListener;
import net.badlion.gberry.listeners.PerformanceListener;
import net.badlion.gberry.listeners.PlayerDestroyBlock;
import net.badlion.gberry.listeners.PlayerJoinLeaveListener;
import net.badlion.gberry.listeners.SafetyListener;
import net.badlion.gberry.listeners.SignListener;
import net.badlion.gberry.listeners.WeatherListener;
import net.badlion.gberry.listeners.WebsiteListener;
import net.badlion.gberry.managers.MCPManager;
import net.badlion.gberry.managers.UserDataManager;
import net.badlion.gberry.tasks.BanEveryoneTask;
import net.badlion.gberry.tasks.GSyncTasks;
import net.badlion.gberry.tasks.LimitWorldTask;
import net.badlion.gberry.tasks.ServerRebootTask;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.FireWorkUtil;
import net.badlion.gberry.utils.NameTagUtil;
import net.badlion.gberry.utils.ScoreboardUtil;
import net.badlion.gberry.utils.tinyprotocol.TinyProtocolReferences;
import net.badlion.gspigot.TinyProtocol;
import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.ChatMessage;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.badlion.gberry.Gberry.ServerRegion.NA;


public class Gberry extends JavaPlugin {

	public static boolean ARENA_BOOSTER_ACTIVE = false;
	public static boolean UHC_BOOSTER_ACTIVE = false;
	public static boolean MINIUHC_BOOSTER_ACTIVE = false;
	public static boolean SG_BOOSTER_ACTIVE = false;

	public static final Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().create();

	private static Map<ChatColor, Color> chatColorToColor = new HashMap<>();

	private long serverStartTime = System.currentTimeMillis();

	private String url;
	private String user;
	private String pass;
	private String db;
	private String fullURL;
	private static FileConfiguration fileConfiguration;
	private static HikariConfig hikariConfig;
	private static HikariDataSource hikariDataSource;
	public static Gberry plugin;
	public static boolean factions;
	public static boolean debugDB;
	public static String serverName;
	public static ServerRegion serverRegion;
	public static ServerType serverType;

	public static String couchDBUrl;
	public static String couchDBPort;
	public static String coudhDBDatabase;

	private static String slowDBUrl;
	private static String slowDBUser;
	private static String slowDBPass;
	private static HikariConfig slowHikariConfig;
	private static HikariDataSource slowHikariDataSource;

	private AbstractListCommandHandler listCommandHandler;

	private List<Player> invSeeing = new ArrayList<>();

	// Used to track the currently active Logger flags
	public static Set<String> loggingTags = new HashSet<>();

	private String syncURL;
	private String syncKey;
	private UUID syncUUID;

	public static boolean enableAsyncLoginEvent = false;
	public static boolean enableAsyncDelayedLoginEvent = false;
	public static boolean enableAsyncQuitEvent = false;

	private static boolean disableGSync = false;

	// Hard-code to true for now coz we always use it
	public static boolean useSpigotHack = true;
	public static boolean throwNonAsyncErrors = true;
	public static boolean enableProtocol = true; // Default true coz Gberry wanted it to be, swag
	public static TinyProtocol protocol;
	private boolean allowConnections = false;

	public static int blockLimit = 10000;

	private List<CommandRecord> commandRecords = new ArrayList<>();

	private Map<String, String> globalSettings = new ConcurrentHashMap<>();

	public static Date startupTime;

	public static String mcpURL;
	public static String mcpKey = "IVxbY9cf9e8Bsqp9UpJqQVgiLvWmhi1dPEFpcI1a";
	public static int mcpTimeout = 2000;

	public static boolean gsyncEnabled = true;

	public enum Benchmark {
		FIFTEEN_MINUTE(3),
		FIVE_MINUTE(2),
		ONE_MINUTE(1),
		FIVE_SECOND(0);

		private int index;

		Benchmark(int index) {
			this.index = index;
		}

		public int getIndex() {
			return index;
		}

	}

	public enum ServerRegion {
		NA,
		EU,
		AU,
		SA,
		AS,
		DE,
		BE
	}

	public enum ServerType {
		ARENAPVP("ArenaPvP", "pvp"),
		CTF("CTF", "ctf"),
		FACTIONS("Factions", "factions"),
		FFA("FFA", "ffa"),
		LOBBY("Lobby", "lobby"),
		MINIUHC("Mini UHC", "miniuhc"),
		SG("SG", "sg"),
		SKYWARS("Skywars", "skywars"),
		TDM("TDM", "tdm"),
		TOURNAMENT("Tournament", "tournament"),
		UHC("UHC", "uhc"),
		UHCMEETUP("UHC Meetup", "uhcmeetup"),
		UNKNOWN("UNKNOWN", "unknown");

		private String name;
		private String internalName;

		ServerType(String name, String internalName) {
			this.name = name;
			this.internalName = internalName;
		}

		public String getName() {
			return name;
		}

		public String getInternalName() {
			return internalName;
		}

	}

	public Gberry() {
		Gberry.chatColorToColor.put(ChatColor.BLACK, Color.fromRGB(0, 0, 0));
		Gberry.chatColorToColor.put(ChatColor.DARK_BLUE, Color.fromRGB(0, 0, 170));
		Gberry.chatColorToColor.put(ChatColor.DARK_GREEN, Color.fromRGB(0, 170, 0));
		Gberry.chatColorToColor.put(ChatColor.DARK_AQUA, Color.fromRGB(0, 170, 170));
		Gberry.chatColorToColor.put(ChatColor.DARK_RED, Color.fromRGB(170, 0, 0));
		Gberry.chatColorToColor.put(ChatColor.DARK_PURPLE, Color.fromRGB(170, 0, 170));
		Gberry.chatColorToColor.put(ChatColor.GOLD, Color.fromRGB(255, 170, 0));
		Gberry.chatColorToColor.put(ChatColor.GRAY, Color.fromRGB(170, 170, 170));
		Gberry.chatColorToColor.put(ChatColor.DARK_GRAY, Color.fromRGB(85, 85, 85));
		Gberry.chatColorToColor.put(ChatColor.BLUE, Color.fromRGB(85, 85, 255));
		Gberry.chatColorToColor.put(ChatColor.GREEN, Color.fromRGB(85, 255, 85));
		Gberry.chatColorToColor.put(ChatColor.AQUA, Color.fromRGB(85, 255, 255));
		Gberry.chatColorToColor.put(ChatColor.RED, Color.fromRGB(255, 85, 85));
		Gberry.chatColorToColor.put(ChatColor.LIGHT_PURPLE, Color.fromRGB(255, 85, 255));
		Gberry.chatColorToColor.put(ChatColor.YELLOW, Color.fromRGB(255, 255, 85));
		Gberry.chatColorToColor.put(ChatColor.WHITE, Color.fromRGB(255, 255, 255));
	}

	@SuppressWarnings("unused")
	@Override
	public void onEnable() {
		Gberry.plugin = this;

		useSpigotHack = getConfig().getBoolean("useSpigotHack", true);

		ScoreboardUtil.initialize();

		this.saveDefaultConfig();

		// Debug mode?
		Gberry.debugDB = this.getConfig().getBoolean("debug_db", false);
		Gberry.serverName = this.getConfig().getString("server");
		Gberry.disableGSync = this.getConfig().getBoolean("disable-gsync", false);
		Gberry.blockLimit = this.getConfig().getInt("block-limit", 10000);

		if (Gberry.serverName.equals("none")) {
			Bukkit.getLogger().info("server name not set");
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
		}

		String serverName = Gberry.serverName.toLowerCase();

		// Set server region
		if (serverName.startsWith("eu")) {
			Gberry.serverRegion = ServerRegion.EU;
		} else if (serverName.startsWith("au")) {
			Gberry.serverRegion = ServerRegion.AU;
		} else if (serverName.startsWith("sa")) {
			Gberry.serverRegion = ServerRegion.SA;
		} else if (serverName.startsWith("as")) {
			Gberry.serverRegion = ServerRegion.AS;
		} else if (serverName.startsWith("de")) {
			Gberry.serverRegion = ServerRegion.DE;
		} else if (serverName.startsWith("be")) {
			Gberry.serverRegion = ServerRegion.BE;
		} else {
			Gberry.serverRegion = NA;
		}

		// Set server type
		if (serverName.contains("arena")) {
			Gberry.serverType = ServerType.ARENAPVP;
		} else if (serverName.contains("tournament")) {
			Gberry.serverType = ServerType.TOURNAMENT;
		} else if (serverName.contains("faction")) {
			Gberry.serverType = ServerType.FACTIONS;
		} else if (serverName.contains("mini")) { // Above UHC
			Gberry.serverType = ServerType.MINIUHC;
		} else if (serverName.contains("ffa")) { // Above UHC and SG
			Gberry.serverType = ServerType.FFA;
		} else if (serverName.contains("sg")) {
			Gberry.serverType = ServerType.SG;
		} else if (serverName.contains("uhcmeetup")) { // Above UHC
			Gberry.serverType = ServerType.UHCMEETUP;
		} else if (serverName.contains("uhc")) {
			Gberry.serverType = ServerType.UHC;
		} else if (serverName.contains("lobby")) {
			Gberry.serverType = ServerType.LOBBY;
		} else if (serverName.contains("ctf")) {
			Gberry.serverType = ServerType.CTF;
		} else if (serverName.contains("tdm")) {
			Gberry.serverType = ServerType.TDM;
		} else {
			Gberry.serverType = ServerType.UNKNOWN;
		}

		this.syncURL = "http://" + GetCommon.getIpForDB() + ":10111/";
		this.syncKey = "5mPkwHY9xxLUMVwmCCZK3whzjsWMjyBC";
		this.syncUUID = UUID.randomUUID();

		int mcpPort = this.getConfig().getInt("mcp.port", 9000);
		String mcpIP = this.getConfig().getString("mcp.ip", GetCommon.getIpForDB());
		Gberry.mcpURL = "http://" + mcpIP + ":" + mcpPort + "/";

		// Get DB login info
		File configFile = new File("bukkit.yml");

		if (!configFile.exists()) {
			Bukkit.getLogger().severe("no config file found.");
			return;
		}

		Gberry.fileConfiguration = YamlConfiguration.loadConfiguration(configFile);

		this.url = GetCommon.getIpForDB(); //Gberry.fileConfiguration.getString("db_url");
		this.user = Gberry.fileConfiguration.getString("db_user");
		this.pass = Gberry.fileConfiguration.getString("db_pass");
		this.db = Gberry.fileConfiguration.getString("db_db");
		this.fullURL = "jdbc:postgresql://" + this.url + "/" + this.db;

		Gberry.couchDBUrl = GetCommon.getIpForSlowDB();//this.getConfig().getString("couchdb.url");
		Gberry.couchDBPort = this.getConfig().getString("couchdb.port");
		Gberry.coudhDBDatabase = this.getConfig().getString("couchdb.db");

		Gberry.slowDBUrl = GetCommon.getIpForSlowDB();
		Gberry.slowDBUser = "badlion_slow";
		Gberry.slowDBPass = "9g4BnhNPti8iSr1u";
		String fullSlowUrl = "jdbc:postgresql://" + Gberry.slowDBUrl + "/badlion_slow";

		if (this.url == null || this.user == null || this.pass == null) {
			Bukkit.getLogger().severe("GBERRY - NO DATABASE LOGIN FOUND, STOPPING SERVER");
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
			return;
		}

		Bukkit.getLogger().severe("XXXXXXXXX LOADING DB CONNECTION.");

		Gberry.hikariConfig = new HikariConfig();
		Gberry.hikariConfig.setJdbcUrl(this.fullURL);
		Gberry.hikariConfig.setUsername(this.user);
		Gberry.hikariConfig.setPassword(this.pass);
		Gberry.hikariConfig.setConnectionTimeout(10 * 1000);
		Gberry.hikariConfig.setIdleTimeout(120 * 1000);
		Gberry.hikariConfig.setMaxLifetime(300 * 1000);
		Gberry.hikariConfig.setMinimumIdle(this.getConfig().getInt("db.min-connections"));
		Gberry.hikariConfig.setMaximumPoolSize(this.getConfig().getInt("db.max-connections"));
		Gberry.hikariConfig.setLeakDetectionThreshold(2000);

		try {
			Gberry.hikariDataSource = new HikariDataSource(Gberry.hikariConfig);
		} catch (Exception e) {
			e.printStackTrace();
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
		}

		// Slow DB
		Gberry.slowHikariConfig = new HikariConfig();
		Gberry.slowHikariConfig.setJdbcUrl(fullSlowUrl);
		Gberry.slowHikariConfig.setUsername(Gberry.slowDBUser);
		Gberry.slowHikariConfig.setPassword(Gberry.slowDBPass);
		Gberry.slowHikariConfig.setConnectionTimeout(10 * 1000);
		Gberry.slowHikariConfig.setIdleTimeout(30 * 1000);
		Gberry.slowHikariConfig.setMaxLifetime(300 * 1000);
		Gberry.slowHikariConfig.setMinimumIdle(1);
		Gberry.slowHikariConfig.setMaximumPoolSize(5);

		try {
			Gberry.slowHikariDataSource = new HikariDataSource(Gberry.slowHikariConfig);
		} catch (Exception e) {
			e.printStackTrace();
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
		}

		/*BoneCPConfig config = new BoneCPConfig();
		config.setJdbcUrl(fullURL);
		config.setUsername(user);
		config.setPassword(pass);
		config.setMinConnectionsPerPartition(this.getConfig().getInt("db.min-connections"));
		config.setMaxConnectionsPerPartition(this.getConfig().getInt("db.max-connections"));
		config.setPartitionCount(this.getConfig().getInt("db.partitions"));
        config.setIdleMaxAgeInMinutes(2);
		config.setCloseConnectionWatch(this.debugDB);

		try {
			connectionPool = new BoneCP(config);
		} catch (SQLException e) {
			e.printStackTrace();
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
			return;
		} // setup the connection pool */

		// Set default list command handler
		this.listCommandHandler = new DefaultListCommandHandler();

		// Register BungeeCord
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

		BukkitUtil.initialize(this);
		FireWorkUtil.initialize();

		// Prevent people from joining too soon
		Listener listener = new Listener() {

			@EventHandler(priority = EventPriority.LAST)
			public void onPlayerLogin(PlayerLoginEvent event) {
				if (!Gberry.plugin.allowConnections()) {
					event.setKickMessage("The server is still being setup!");
					event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
				}
			}

		};

		Gberry.plugin.getServer().getPluginManager().registerEvents(listener, Gberry.plugin);

		// Allow connections after 3 seconds for tinyprotocol
		Gberry.plugin.getServer().getScheduler().runTaskLater(Gberry.plugin, new Runnable() {
			@Override
			public void run() {
				Gberry.this.allowConnections = true;
				if (Gberry.protocol != null) {
					Gberry.protocol.setAllowConnections(true);
				}
			}
		}, 60L);

		//this.getServer().getPluginManager().registerEvents(new AprilFoolsListener(), this);
		this.getServer().getPluginManager().registerEvents(new BugListener(), this);
		this.getServer().getPluginManager().registerEvents(new InvSeeListener(this), this);
		this.getServer().getPluginManager().registerEvents(new PlayerJoinLeaveListener(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerDestroyBlock(), this);
		this.getServer().getPluginManager().registerEvents(new ChatListener(this), this);
		this.getServer().getPluginManager().registerEvents(new CommandListener(), this);
		this.getServer().getPluginManager().registerEvents(new CompromisedAccountListener(this), this);
		this.getServer().getPluginManager().registerEvents(new WeatherListener(this), this);
		this.getServer().getPluginManager().registerEvents(new WebsiteListener(), this);
		this.getServer().getPluginManager().registerEvents(new SignListener(), this);
		this.getServer().getPluginManager().registerEvents(new PerformanceListener(), this);
		this.getServer().getPluginManager().registerEvents(new NameTagUtil(), this);

		this.getCommand("discord").setExecutor(new DiscordCommand());
		this.getCommand("automute").setExecutor(new AutoMuteCommand());
		this.getCommand("punishments").setExecutor(new PunishmentsCommand(this));
		this.getCommand("invsee").setExecutor(new InvSeeCommand(this));
		this.getCommand("armorsee").setExecutor(new ArmorSeeCommand(this));
		this.getCommand("clearchat").setExecutor(new ClearChatCommand(this));
		this.getCommand("hacker").setExecutor(new HackerCommand(this));
		this.getCommand("maxplayers").setExecutor(new MaxPlayerCommand(this));
		this.getCommand("ss").setExecutor(new ServerStatsCommand(this));
		this.getCommand("logger").setExecutor(new LoggerCommand());
		this.getCommand("where").setExecutor(new WhereCommand());
		this.getCommand("getmap").setExecutor(new GetMapCommand());
		this.getCommand("ginie").setExecutor(new GinieCommand());
		this.getCommand("sudo").setExecutor(new SudoCommand());
		this.getCommand("loadsettings").setExecutor(new LoadSettingsCommand());
		this.getCommand("loadfilters").setExecutor(new LoadFiltersCommand());
		this.getCommand("extract").setExecutor(new ExtractCommand());
		this.getCommand("listhide").setExecutor(new ListHideCommand());

		Gberry.factions = this.getServer().getPluginManager().getPlugin("Factions") != null;

		// MCP Boot Sequence
		JSONObject response = MCPManager.bootMCP();
		if (response == null || !response.equals(MCPManager.successResponse)) {
			this.getLogger().info("ERROR WHEN CONTACTING MCP ON BOOT");
			this.getLogger().info("Response: " + response);

			// Shut down at the start
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");

			return;
		}

		if (!Gberry.disableGSync) {
			new GSyncTasks().run();
			new GSyncTasks().runTaskTimerAsynchronously(this, 20 * 5, 20 * 5);
		}

		if (this.getConfig().getBoolean("safety", true)) {
			new BanEveryoneTask().runTaskTimer(this, 0, 1);
			this.getServer().getPluginManager().registerEvents(new SafetyListener(), this);
		}

		new GMapManager();

		UserDataManager.initialize();

		// Register BungeeCord
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

		// Custom enchants
		try {
			Field f = Enchantment.class.getDeclaredField("acceptingNew");
			f.setAccessible(true);
			f.set(null, true);
			EnchantmentWrapper.registerEnchantment(new GlowEnchantment());
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (Gberry.enableProtocol) {
			Gberry.protocol = this.getServer().getTinyProtocol(this);
		}

		// Initialize booster inventory
		//BoosterInventory.initialize();

		Gberry.getAllGlobalSettings();

		Gberry.startupTime = new Date();

		// Command logging
		BukkitUtil.runTaskTimerAsync(new Runnable() {
			@Override
			public void run() {
				Gberry.this.insertCommandRecords();
			}
		}, 100L);

		// Kick off keep alive task
		MCPManager.startKeepAlive();

		// Reboot server task
		new ServerRebootTask().runTaskTimer(this, 1200L, 1200L);
		new LimitWorldTask().runTaskTimer(this, 100L, 100L);
	}

	@Override
	public void onDisable() {
		// MCP Shutdown sequence
		JSONObject response = MCPManager.shutdownMCP();
		if (response == null || !response.equals(MCPManager.successResponse)) {
			this.getLogger().info("ERROR WHEN SHUTTING DOWN CONTACTING MCP");
		}
	}

	public static double getTPS(Benchmark benchmark) {
		int offSet = Gberry.plugin.getServer().getRecentTps().length == 4 ? 0 : -1;

		return Gberry.plugin.getServer().getRecentTps()[offSet + benchmark.getIndex()];
	}

	/**
	 * WARNING! CALL THIS ASYNC
	 */
	public static void sendGSyncEvent(List<String> args) {
		if (Gberry.disableGSync) {
			return;
		}

		// Safety
		Gberry.catchNonAsyncThread();

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("sync_event", args);

		MCPManager.contactMCP(MCPManager.MCP_MESSAGE.POST_SYNC_SERVER, jsonObject);
	}

	/**
	 * WARNING! CALL THIS ASYNC
	 */
	public static void sendToAll(List<String> cmds) {
		// Safety
		Gberry.catchNonAsyncThread();

		JSONObject object = new JSONObject();
		object.put("command", cmds.get(0));

		MCPManager.contactMCP(MCPManager.MCP_MESSAGE.SEND_TO_ALL, object);
	}

	public static Connection getUnsafeConnection() throws SQLException {
		try {
			return hikariDataSource.getConnection();
		} catch (SQLException e) {
			try {
				// Try one more time at max (load of kitpvp servers a lot gets overloaded)
				return hikariDataSource.getConnection();
			} catch (SQLTimeoutException e2) {
				// Can we not grab a connection and has the server just rebooted?
				if (Gberry.plugin.getServerUptime() < 60000) {
					e2.printStackTrace();
					Bukkit.getLogger().severe("UNABLE TO GRAB A DATABASE CONNECTION, STOPPING SERVER");
					Bukkit.getLogger().severe("UNABLE TO GRAB A DATABASE CONNECTION, STOPPING SERVER");
					Bukkit.getLogger().severe("UNABLE TO GRAB A DATABASE CONNECTION, STOPPING SERVER");

					Gberry.plugin.getServer().dispatchCommand(Gberry.plugin.getServer().getConsoleSender(), "stop");
				}

				throw e2;
			}
		}
	}

	public static Connection getConnection() throws SQLException {
		// Safety
		Gberry.catchNonAsyncThread();

		try {
			return hikariDataSource.getConnection();
		} catch (SQLException e) {
			try {
				// Try one more time at max (load of kitpvp servers a lot gets overloaded)
				return hikariDataSource.getConnection();
			} catch (SQLTimeoutException e2) {
				// Can we not grab a connection and has the server just rebooted?
				if (Gberry.plugin.getServerUptime() < 60000) {
					e2.printStackTrace();
					Bukkit.getLogger().severe("UNABLE TO GRAB A DATABASE CONNECTION, STOPPING SERVER");
					Bukkit.getLogger().severe("UNABLE TO GRAB A DATABASE CONNECTION, STOPPING SERVER");
					Bukkit.getLogger().severe("UNABLE TO GRAB A DATABASE CONNECTION, STOPPING SERVER");

					BukkitUtil.runTask(new Runnable() {
						@Override
						public void run() {
							Gberry.plugin.getServer().dispatchCommand(Gberry.plugin.getServer().getConsoleSender(), "stop");
						}
					});
				} else {
					e2.printStackTrace();
					// TODO: TEMP
					BukkitUtil.runTask(new Runnable() {
						@Override
						public void run() {
							Gberry.plugin.getServer().dispatchCommand(Gberry.plugin.getServer().getConsoleSender(), "stop");
						}
					});
				}

				throw e2;
			}
		}
	}

	public static Connection getSlowConnection() throws SQLException {
		try {
			return slowHikariDataSource.getConnection();
		} catch (SQLException e) {
			try {
				// Try one more time at max (load of kitpvp servers a lot gets overloaded)
				return slowHikariDataSource.getConnection();
			} catch (SQLTimeoutException e2) {
				// Can we not grab a connection and has the server just rebooted?
				if (Gberry.plugin.getServerUptime() < 60000) {
					e2.printStackTrace();
					Bukkit.getLogger().severe("UNABLE TO GRAB A DATABASE CONNECTION, STOPPING SERVER");
					Bukkit.getLogger().severe("UNABLE TO GRAB A DATABASE CONNECTION, STOPPING SERVER");
					Bukkit.getLogger().severe("UNABLE TO GRAB A DATABASE CONNECTION, STOPPING SERVER");

					BukkitUtil.runTask(new Runnable() {
						@Override
						public void run() {
							Gberry.plugin.getServer().dispatchCommand(Gberry.plugin.getServer().getConsoleSender(), "stop");
						}
					});
				}

				throw e2;
			}
		}
	}

	public static ResultSet executeQuery(Connection connection, PreparedStatement stmt) throws SQLException {
		return stmt.executeQuery();
	}

	public static int executeUpdate(Connection connection, PreparedStatement stmt) throws SQLException {
		return stmt.executeUpdate();
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

	public int getServerNumber() {
		Pattern pattern = Pattern.compile("[0-9]+$");
		Matcher match = pattern.matcher(Gberry.serverName);
		if (match.find()) {
			return Integer.parseInt(match.group());
		}

		return -1;
	}

	public static String formatJSON(String jsonString) {
		JsonParser parser = new JsonParser();
		JsonObject json = parser.parse(jsonString).getAsJsonObject();

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return gson.toJson(json);
	}

	public static Location getCenterOfBlock(Location location) {
		Location newLocation = location.clone();

		newLocation.setX(newLocation.getBlockX() + 0.5);
		newLocation.setZ(newLocation.getBlockZ() + 0.5);

		newLocation.setPitch(0F);

		return newLocation;
	}

	public static Location parseLocation(String locationString) {
		String[] components = locationString.split(",");
		if (components.length == 4) {
			World world = Gberry.plugin.getServer().getWorld(components[3]);

			return new Location(world, Double.parseDouble(components[0]),
					Double.parseDouble(components[1]), Double.parseDouble(components[2]));
		} else if (components.length == 6) {
			World world = Gberry.plugin.getServer().getWorld(components[5]);

			return new Location(world, Double.parseDouble(components[0]),
					Double.parseDouble(components[1]), Double.parseDouble(components[2]),
					Float.parseFloat(components[3]), Float.parseFloat(components[4]));
		} else {
			throw new RuntimeException("Invalid location string");
		}
	}

	public static Location parseLocation(World world, String locationString) {
		String[] components = locationString.split(",");
		if (components.length == 5) {
			return new Location(world, Double.parseDouble(components[0]),
					Double.parseDouble(components[1]), Double.parseDouble(components[2]),
					Float.parseFloat(components[3]), Float.parseFloat(components[4]));
		} else {
			throw new RuntimeException("Invalid location string");
		}
	}

	public static String getLocationString(Location location) {
		return location.getX() + "," + location.getY() + "," + location.getZ() + "," + location.getYaw() + "," + location.getPitch() + "," + location.getWorld().getName();
	}

	public static boolean isLocationInBetween(Location location1, Location location2, Location location) {
		// Do some quick fixes if needed
		if (location1.getX() > location2.getX()) {
			double tmp = location2.getX();
			location2.setX(location1.getX());
			location1.setX(tmp);
		}

		if (location1.getY() > location2.getY()) {
			double tmp = location2.getY();
			location2.setY(location1.getY());
			location1.setY(tmp);
		}

		if (location1.getZ() > location2.getZ()) {
			double tmp = location2.getZ();
			location2.setZ(location1.getZ());
			location1.setZ(tmp);
		}

		if ((location.getX() >= location1.getX() && location.getX() <= location2.getX()) &&
				(location.getY() >= location1.getY() && location.getY() <= location2.getY()) &&
				(location.getZ() >= location1.getZ() && location.getZ() <= location2.getZ())) {
			return true;
		}
		return false;
	}

	public static String toRomanNumeral(int value) {
		if (value < 1) {
			return "";
		} else if (value == 1) {
			return "I";
		} else if (value == 2) {
			return "II";
		} else if (value == 3) {
			return "III";
		} else if (value == 4) {
			return "IV";
		} else if (value == 5) {
			return "V";
		} else if (value == 6) {
			return "VI";
		} else if (value == 7) {
			return "VII";
		} else if (value == 8) {
			return "VIII";
		} else if (value == 9) {
			return "IX";
		} else if (value == 10) {
			return "X";
		}

		return "";
	}

	public static int fromRomanNumeral(String str) {
		if (str.equals("I")) {
			return 1;
		} else if (str.equals("II")) {
			return 2;
		} else if (str.equals("III")) {
			return 3;
		} else if (str.equals("IV")) {
			return 4;
		} else if (str.equals("V")) {
			return 5;
		} else if (str.equals("VI")) {
			return 6;
		} else if (str.equals("VII")) {
			return 7;
		} else if (str.equals("VIII")) {
			return 8;
		} else if (str.equals("IX")) {
			return 9;
		} else if (str.equals("X")) {
			return 10;
		}

		return -1;
	}

	public static void log(String flag, String message) {
		if (Gberry.loggingTags.contains(flag)) {
			Bukkit.getLogger().info("[" + flag + "] " + message);
		}
	}

	/**
	 * Method used to get their timezone,
	 * based off their IP and that IP's last recorded location.
	 *
	 * MUST BE CALLED ASYNC.
	 */
	public static String getTimeZone(Player player) {
		return Gberry.getTimeZone(player.getAddress().getAddress());
	}

	/**
	 * Method used to get their timezone,
	 * based off their IP and that IP's last recorded location.
	 *
	 * MUST BE CALLED ASYNC.
	 */
	public static String getTimeZone(InetAddress inetAddress) {
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		String timeZone = null;

		final long longIp = IPCommon.toLongIP(inetAddress.getAddress());

		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement("SELECT location_timezone FROM maxmind_ips WHERE ip = ?;");
			ps.setLong(1, longIp);

			rs = ps.executeQuery();

			if (rs.next()) {
				timeZone = rs.getString("location_timezone");
			} else {
				timeZone = "EST"; // If it can't find their time, just use EST (NY) time
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(rs, ps, connection);
		}

		return timeZone;
	}

	public static long getOfflineIP(String name) {
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		String query = "SELECT uuid FROM player_uuid_mapping WHERE lower_username = ?;";

		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, name.toLowerCase());

			rs = Gberry.executeQuery(connection, ps);
			UUID uuid = null;
			if (rs.next()) {
				uuid = UUID.fromString(rs.getString("uuid"));
			}

			if (uuid == null) {
				return -1;
			}

			query = "SELECT long_ip FROM player_ips WHERE uuid = ? ORDER BY last_login DESC LIMIT 1;";
			ps = connection.prepareStatement(query);
			ps.setString(1, uuid.toString());

			rs = Gberry.executeQuery(connection, ps);
			if (rs.next()) {
				return rs.getLong("long_ip");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(rs, ps, connection);
		}

		return -1;
	}

	public static long getOfflineIP(UUID uuid) {
		// Try to avoid rate limiter
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		String query = "SELECT long_ip FROM player_ips WHERE uuid = ? ORDER BY last_login DESC LIMIT 1;";

		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, uuid.toString());

			rs = Gberry.executeQuery(connection, ps);
			if (rs.next()) {
				return rs.getLong("long_ip");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(rs, ps, connection);
		}

		return -1;
	}

	/**
	 * Try to get the UUID for a username from Mojang's silly API
	 * WARNING: This function should never be called on the main thread!
	 *
	 * @param name username of the UUID we want
	 * @return UUID of the username (can be null)
	 */
	public static UUID getOfflineUUID(String name) {
		// Try to avoid rate limiter
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		String query = "SELECT uuid FROM player_uuid_mapping WHERE lower_username = ?;";

		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, name.toLowerCase());

			rs = Gberry.executeQuery(connection, ps);
			if (rs.next()) {
				return UUID.fromString(rs.getString("uuid"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(rs, ps, connection);
		}

		// Backup system
		/*GameProfile[] profiles = new GameProfile[1];
		GameProfileLookup gameProfileLookup = new GameProfileLookup(profiles);

		MinecraftServer.getServer().getGameProfileRepository().findProfilesByNames(new String[] { name }, Agent.MINECRAFT, gameProfileLookup);
		if (!MinecraftServer.getServer().getOnlineMode() && profiles[0] == null) {
			UUID uuid = EntityHuman.a(new GameProfile(null, name));
			GameProfile profile = new GameProfile(uuid, name);

			gameProfileLookup.onProfileLookupSucceeded(profile);
		}

		GameProfile profile = profiles[0];
		if (profile != null) {
			return profile.getId(); // This is my UUID I want
		}*/


		return null;
	}

	public static UUID getOfflineUUIDSlow(String name) {
		// Try to avoid rate limiter
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		String query = "SELECT uuid FROM player_ips WHERE lower(username) = ? ORDER BY last_login DESC LIMIT 1;";

		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, name.toLowerCase());

			rs = Gberry.executeQuery(connection, ps);
			if (rs.next()) {
				return UUID.fromString(rs.getString("uuid"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(rs, ps, connection);
		}

		return null;
	}

	/**
	 * Get the latest UUID from our player_ip table cache
	 * WARNING: This function should never be called on the main thread!
	 *
	 * @param uuid uuid of username we wish to retreve
	 * @return latest username for uuid
	 */
	public static String getUsernameFromUUID(UUID uuid) {
		return Gberry.getUsernameFromUUID(uuid.toString());
	}

	/**
	 * Get the latest UUID from our player_ip table cache
	 * WARNING: This function should never be called on the main thread!
	 *
	 * @param uuid uuid of username we wish to retreve
	 * @return latest username for uuid
	 */
	public static String getUsernameFromUUID(String uuid) {
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		String query = "SELECT username FROM player_uuid_mapping WHERE uuid = ?;";

		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, uuid);

			rs = Gberry.executeQuery(connection, ps);
			if (rs.next()) {
				return rs.getString("username");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(rs, ps, connection);
		}

		return "N/A";
	}

	public static String convertChatColors(String msg) {
		return msg.replace("&", "§");
	}

	public static void broadcastMessageNoBalance(String msg) {
		ChatMessage chatMessage = Gberry.plugin.getServer().createChatMessage(msg, false); // False is keepNewLines
		for (Player player : Bukkit.getOnlinePlayers()) {
			chatMessage.sendTo(player);
		}
	}

	public static void broadcastMessage(final String msg) {
		Gberry.broadcastMessage(msg, true);
	}

	public static void broadcastMessage(final String msg, boolean sendIfBlank) {
		// Don't send if we don't want blanks
		if (!sendIfBlank && (msg == null || msg.equals(""))) {
			return;
		}

		final ImmutableList<Player> players = ImmutableList.copyOf(Bukkit.getOnlinePlayers());
		int size = players.size();
		int diff = (int) Math.ceil((double) players.size() / 20D);

		final ChatMessage chatMessage = Gberry.plugin.getServer().createChatMessage(msg, false); // False is keepNewLines

		for (int i = 0, j = 0; i < size; i += diff) {
			// Overshot
			if (i >= size) {
				return;
			}

			// Some shit for the task
			final int start = i;
			final int end = i + diff;
			Bukkit.getServer().getScheduler().runTaskLater(Gberry.plugin, new Runnable() {

				@Override
				public void run() {
					for (int i = start; i < end; ++i) {
						// Overshot
						if (i >= players.size()) {
							return;
						}

						chatMessage.sendTo(players.get(i));
					}
				}

			}, ++j);
		}
	}

	public static void distributeTask(JavaPlugin plugin, final PlayerRunnable runnable) {
		Gberry.distributeTask(plugin, runnable, 20D);
	}

	public static void distributeTask(JavaPlugin plugin, final PlayerRunnable runnable, double ticks) {
		final ImmutableList<Player> players = ImmutableList.copyOf(Bukkit.getOnlinePlayers());
		int size = players.size();
		int diff = (int) Math.ceil((double) players.size() / ticks);

		for (int i = 0, j = 0; i < size; i += diff) {
			// Overshot
			if (i >= size) {
				return;
			}

			// Some shit for the task
			final int start = i;
			final int end = i + diff;
			Bukkit.getServer().getScheduler().runTaskLater(plugin, new Runnable() {

				@Override
				public void run() {
					for (int i = start; i < end; ++i) {
						// Overshot
						if (i >= players.size()) {
							return;
						}

						runnable.run(players.get(i));
					}
				}

			}, ++j);
		}
	}

	public static void safeTeleport(final Player player, final Location location) {
		if (player.getVehicle() != null && player.getVehicle() instanceof Horse) {
			final Horse vehicle = ((Horse) player.getVehicle());
			vehicle.eject();

			// TP the horse
			new BukkitRunnable() {
				public void run() {
					// Add 1 to location to be safe
					vehicle.teleport(location.add(0, 1, 0));
				}
			}.runTaskLater(Gberry.plugin, 1L);

			// Reattach the player to the horse
			new BukkitRunnable() {
				public void run() {
					vehicle.setPassenger(player);
				}
			}.runTaskLater(Gberry.plugin, 2L);
		} else {
			player.setFallDistance(0);
			player.teleport(location);
		}
	}

	/**
	 * Query CouchDB to try and get a specific document based on our design and our view
	 *
	 * @param design design of the document types we are looking for
	 * @param view   view of the type of key we are looking to go by
	 * @param key    the key
	 * @return the return result from the GET (to access data use json.rows[0].doc[column] as an example)
	 */
	public static JSONObject executeCouchDBGetQuery(String design, String view, String key) throws HTTPRequestFailException {
		String url = "http://" + Gberry.couchDBUrl + ":" + Gberry.couchDBPort + "/" + Gberry.coudhDBDatabase + "/_design/" + design + "/_view/" + view + "?key=\"" + key + "\"&include_docs=true";
		return HTTPCommon.executeGETRequest(url);
	}

	/**
	 * Call this to UPDATE information in CouchDB. You must pass the _rev information
	 * to prove to CouchDB that you are properly editing a document
	 *
	 * @param id   uuid of the document
	 * @param json all of document data including the _rev
	 * @return return object from CouchDB
	 */
	public static JSONObject executeCouchDBPutQuery(String id, JSONObject json) throws HTTPRequestFailException {
		String url = "http://" + Gberry.couchDBUrl + ":" + Gberry.couchDBPort + "/" + Gberry.coudhDBDatabase + "/" + id;
		JSONObject jsonObject = HTTPCommon.executePUTRequest(url, json);
		if (jsonObject != null && jsonObject.containsKey("id")) {
			return jsonObject;
		}

		return null;
	}

	/**
	 * Call this to DELETE information in CouchDB. You must pass the _rev information
	 * to prove to CouchDB that you are properly editing a document
	 *
	 * @param id uuid of the document
	 * @return return object from CouchDB
	 */
	public static JSONObject executeCouchDBDeleteQuery(String id, String rev) throws HTTPRequestFailException {
		String url = "http://" + Gberry.couchDBUrl + ":" + Gberry.couchDBPort + "/" + Gberry.coudhDBDatabase + "/" + id + "?rev=" + rev;
		JSONObject jsonObject = HTTPCommon.executeDELETERequest(url);
		if (jsonObject != null && jsonObject.containsKey("id")) {
			return jsonObject;
		}

		return null;
	}

	/**
	 * Call this when we want to INSERT a new document into the database
	 *
	 * @param json json data we want to store
	 * @return CouchDB's response w/ "id" and "rev"
	 */
	public static JSONObject executeCouchDBPostQuery(JSONObject json) throws HTTPRequestFailException {
		String url = "http://" + Gberry.couchDBUrl + ":" + Gberry.couchDBPort + "/" + Gberry.coudhDBDatabase + "/";
		JSONObject jsonObject = HTTPCommon.executePOSTRequest(url, json);

		if (jsonObject != null && jsonObject.containsKey("id")) {
			return jsonObject;
		}

		return null;
	}

	public static boolean isPlayerOnline(Player player) {
		return player != null && player.equals(Gberry.plugin.getServer().getPlayer(player.getUniqueId()));
	}

	/**
	 * Call this when we want to INSERT a new document into the database
	 *
	 * @param json json data we want to store
	 * @return CouchDB's response w/ "id" and "rev"
	 */
	public static JSONArray executeCouchDBBulkPostQuery(JSONObject json) throws HTTPRequestFailException {
		String url = "http://" + Gberry.couchDBUrl + ":" + Gberry.couchDBPort + "/" + Gberry.coudhDBDatabase + "/_bulk_docs";
		return HTTPCommon.executePOSTRequestJSONArray(url, json);
	}

	/**
	 * Call this when we want to INSERT a new document into the database
	 *
	 * @param json json data we want to store
	 * @return CouchDB's response w/ "id" and "rev"
	 */
	public static JSONArray executeCouchDBBulkPostQuery(JSONObject json, String coudhDBDatabase) throws HTTPRequestFailException {
		String url = "http://" + Gberry.couchDBUrl + ":" + Gberry.couchDBPort + "/" + coudhDBDatabase + "/_bulk_docs";
		return HTTPCommon.executePOSTRequestJSONArray(url, json);
	}

	public static void addPlayerPotionEffects(JSONObject json, List<UUID> uuids, String key, Map<String, Collection<PotionEffect>> groupPotionEffects) {
		Map<String, ArrayList<Map<String, Object>>> totalPotionEffects = new HashMap<>();

		for (UUID uuid : uuids) {
			ArrayList<Map<String, Object>> potionEffects = new ArrayList<>();
			for (PotionEffect effect : groupPotionEffects.get(uuid.toString())) {
				Map<String, Object> map = new HashMap<>();
				map.put("name", effect.getType().getName());
				map.put("amplifier", effect.getAmplifier());
				potionEffects.add(map);
			}
			totalPotionEffects.put(uuid.toString(), potionEffects);
		}

		json.put(key, totalPotionEffects);
	}

	public static void addPlayerItems(JSONObject json, List<UUID> uuids, String key, Map<String, ItemStack[]> groupItems) {
		Map<String, ArrayList<Map<String, Object>>> totalArmor = new HashMap<>();
		for (UUID uuid : uuids) {
			ArrayList<Map<String, Object>> playerItems = new ArrayList<>();
			for (ItemStack item : groupItems.get(uuid.toString())) {
				Map<String, Object> map = new HashMap<>();
				map.put("t", item == null ? "n" : item.getType().name());
				map.put("d", item == null ? 0 : item.getDurability());
				map.put("s", item == null ? 0 : item.getAmount());
				if (item != null) {
					Iterator<Map.Entry<Enchantment, Integer>> it = item.getEnchantments().entrySet().iterator();
					Map<String, Integer> enchantments = new HashMap<>();
					while (it.hasNext()) {
						Map.Entry<Enchantment, Integer> pairs = it.next();
						enchantments.put(pairs.getKey().getName(), pairs.getValue());
					}
					if (enchantments.size() > 0) {
						map.put("e", enchantments);
					}
				}
				playerItems.add(map);
			}
			totalArmor.put(uuid.toString(), playerItems);
		}

		json.put(key, totalArmor);
	}

	public static Color getColorFromChatColor(ChatColor chatColor) {
		return Gberry.chatColorToColor.get(chatColor);
	}

	/**
	 * Checks if the given potion is a vial of water.
	 *
	 * @param item the item to check
	 * @return true if it's a water vial
	 */
	public static boolean isWaterPotion(ItemStack item) {
		return (item.getDurability() & 0x3F) == 0;
	}

	/**
	 * Get just the potion effect bits. This is to work around bugs with potion
	 * parsing.
	 *
	 * @param item item
	 * @return new bits
	 */
	public static int getPotionEffectBits(ItemStack item) {
		return item.getDurability() & 0x3F;
	}

	public static void executeCommand(CommandSender sender, String cmd) {
		if (sender instanceof Player) {
			((Player) sender).performCommand(cmd);
		} else {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
		}
	}

	public static void broadcastSound(Sound sound, float volume, float pitch) {
		for (Player pl : Bukkit.getOnlinePlayers()) {
			pl.playSound(pl.getLocation(), sound, volume, pitch);
		}
	}

	/**
	 * Sends a player to a server through MCP. MUST BE CALLED ASYNC!
	 */
	public static void sendToServer(String uuid, String server) {
		Map<String, String> data = new HashMap<>();
		data.put(uuid, server);

		Gberry.sendToServer(data);
	}

	/**
	 * Sends players to a server through MCP. MUST BE CALLED ASYNC!
	 */
	public static void sendToServer(Collection uuids, String server) {
		Map<String, String> data = new HashMap<>();

		for (Object uuid : uuids) {
			data.put(uuid.toString(), server);
		}

		Gberry.sendToServer(data);
	}

	/**
	 * Sends players to servers through MCP. MUST BE CALLED ASYNC!
	 */
	public static void sendToServer(Map<String, String> data) {
		// Safety
		Gberry.catchNonAsyncThread();

		// self.json_body = {'data': [{'uuid': self.USER_1_UUID, 'server': 'test1'}, {'uuid': self.USER_2_UUID, 'server': 'test2'}]}

		JSONObject payload = new JSONObject();

		List<JSONObject> list = new ArrayList<>();
		for (Map.Entry<String, String> entry : data.entrySet()) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("uuid", entry.getKey());
			jsonObject.put("server", entry.getValue());

			list.add(jsonObject);
		}

		payload.put("data", list);

		MCPManager.contactMCP(MCPManager.MCP_MESSAGE.BUKKIT_SEND_TO_SERVER, payload);
	}

	/**
	 * Sends a player to a server using plugin messages. Player must be online.
	 */
	public static void sendToServer(Player player, String server) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);

		try {
			out.writeUTF("Connect");
			out.writeUTF(server);
		} catch (IOException e) {
			e.printStackTrace();
		}

		player.sendPluginMessage(Gberry.plugin, "BungeeCord", b.toByteArray());
	}

	public static String getLineSeparator(ChatColor chatColor) {
		return chatColor + "=====================================================";
	}

	public static void getAllGlobalSettings() {
		new BukkitRunnable() {
			public void run() {
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;

				final Map<String, String> settings = new ConcurrentHashMap<>();

				String query = "SELECT * FROM global_settings;";

				try {
					connection = Gberry.getConnection();
					ps = connection.prepareStatement(query);

					rs = Gberry.executeQuery(connection, ps);

					while (rs.next()) {
						settings.put(rs.getString("setting"), rs.getString("val"));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
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

				// Fire the event
				new BukkitRunnable() {
					public void run() {
						Gberry.plugin.getServer().getPluginManager().callEvent(new SettingsLoadedEvent(settings));
					}
				}.runTask(Gberry.plugin);

				Gberry.plugin.globalSettings = settings;
			}
		}.runTaskAsynchronously(Gberry.plugin);
	}

	/**
	 * ASYNC
	 */
	public static String getGlobalSettingFromDB(String setting) {
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		String query = "SELECT * FROM global_settings WHERE setting = ?;";

		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, setting);

			rs = Gberry.executeQuery(connection, ps);

			if (rs.next()) {
				return rs.getString("val");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
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

		return null;
	}

	public static String getGlobalSetting(String setting) {
		return Gberry.plugin.globalSettings.get(setting);
	}

	public static void catchNonAsyncThread() {
		if (Gberry.plugin.getServer().isPrimaryThread()) {
			Gberry.broadcastMessage(ChatColor.DARK_RED + "Something is very wrong with a plugin. Contact a developer immediately");

			if (Gberry.throwNonAsyncErrors) {
				throw new IllegalStateException("Illegal call on main thread");
			}
		}
	}

	/**
	 * MUST BE CALLED ASYNC
	 */
	public static void sendChatPlayerPrefixChange(UUID uuid) {
		Gberry.sendChatPlayerPrefixChange(uuid.toString());
	}

	/**
	 * MUST BE CALLED ASYNC
	 */
	public static void sendChatPlayerPrefixChange(String uuid) {
		// Fail-safe
		Gberry.catchNonAsyncThread();

		JSONObject payload = new JSONObject();

		payload.put("uuid", uuid);

		try {
			Gberry.contactMCP("player-chat-prefix-change", payload);

		} catch (HTTPRequestFailException e) {
			Gberry.plugin.getLogger().warning(e.getResponseCode() + ": " + e.getResponse());
			e.printStackTrace();
		}
	}

	/**
	 * The numbers are inclusive [0, 3]
	 */
	public static int generateRandomInt(int min, int max) {
		return min + (int) (Math.random() * ((max - min) + 1));
	}

	public static Block getBlockWithinThreshold(World world, int x, int y, int z, int limit) {
		return Gberry.getBlockWithinThreshold(world, x, y, z, limit, new HashSet<Material>());
	}

	public static Block getBlockWithinThreshold(World world, int x, int y, int z, int limit, Set<Material> blacklistedMaterials) {
		int tmpY = y;
		int count = 0;
		Block finalBlock = null;
		do {
			Block block = world.getBlockAt(x, tmpY, z);
			if (block.getType() == null || blacklistedMaterials.contains(block.getType())) {
				continue;
			}

			finalBlock = block;
		} while (tmpY++ <= 255 && count < limit);

		return finalBlock;
	}

	public static ItemStack getGlowItem(ItemStack itemStack) {
		itemStack.addUnsafeEnchantment(new GlowEnchantment(), 0);
		return itemStack;
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

	public static boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					Gberry.deleteDirectory(file);
				} else {
					file.delete();
				}
			}
		}
		return (path.delete());
	}

	public static void writeJarFile(JavaPlugin plugin, String name) {
		JarFile jar = null;
		InputStream stream = null;
		OutputStream outputStream = null;
		try {
			// FUCK BUKKIT
			File pluginFile = TinyProtocolReferences.pluginFile.get(plugin);
			jar = new JarFile(pluginFile);
			JarEntry jarEntry = jar.getJarEntry(name);
			stream = jar.getInputStream(jarEntry);

			// Make sure the data folder exists
			if (!plugin.getDataFolder().exists()) {
				plugin.getDataFolder().mkdir();
			}

			File outputLocation = new File(plugin.getDataFolder(), name);
			outputStream = new FileOutputStream(outputLocation);
			IOUtils.copy(stream, outputStream);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (jar != null) {
				try {
					jar.close();
				} catch (IOException e) {
				}
			}
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
				}
			}
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static void recordCommandUsage(Player player, String message) {
		Gberry.recordCommandUsage(player.getUniqueId(), message);
	}

	public static void recordCommandUsage(UUID uuid, String message) {
		Gberry.plugin.commandRecords.add(new CommandRecord(uuid, message));
	}

	private void insertCommandRecords() {
		StringBuilder builder = new StringBuilder();
		builder.append("INSERT INTO player_command_logs (uuid, log_time, server_name, command) VALUES ");
		List<CommandRecord> records = new ArrayList<>();

		Iterator<CommandRecord> iterator = this.commandRecords.iterator();
		while (iterator.hasNext()) {
			records.add(iterator.next());
			iterator.remove();
		}

		if (records.size() == 0) {
			return;
		}

		for (int i = 0; i < records.size(); i++) {
			builder.append("(?, ?, ?, ?), ");
		}

		String sql = builder.substring(0, builder.length() - 2) + ";";

		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = Gberry.getSlowConnection();
			ps = connection.prepareStatement(sql);

			int i = 1;

			for (CommandRecord record : records) {
				ps.setString(i++, record.getUUID());
				ps.setTimestamp(i++, new Timestamp(record.getTimeStamp()));
				ps.setString(i++, Gberry.serverName);
				ps.setString(i++, record.getMessage());
			}

			Gberry.executeUpdate(connection, ps);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(ps, connection);
		}
	}

	private static class CommandRecord {

		private Long timeStamp = DateTime.now().toDateTime(DateTimeZone.UTC).getMillis();

		private String uuid;
		private String message;

		public CommandRecord(UUID uuid, String message) {
			this.uuid = uuid.toString();
			this.message = message;
		}

		public Long getTimeStamp() {
			return timeStamp;
		}

		public String getUUID() {
			return uuid;
		}

		public String getMessage() {
			return message;
		}

	}

	public static JSONObject contactMCP(String msg, JSONObject data) throws HTTPRequestFailException {
		try {
			JSONObject response = HTTPCommon.executePOSTRequest(Gberry.mcpURL + msg.toLowerCase().replace("_", "-") + "/IVxbY9cf9e8Bsqp9UpJqQVgiLvWmhi1dPEFpcI1a", data, Gberry.mcpTimeout);

			return response;
		} catch (HTTPRequestFailException e) {
			Gberry.plugin.getLogger().info(e.getType().name());
			Gberry.plugin.getLogger().info(e.getResponseCode() + "");
			Gberry.plugin.getLogger().info(e.getResponse());
		}
		return null;
	}

	public static FileConfiguration getFileConfiguration() {
		return fileConfiguration;
	}

	public List<Player> getInvSeeing() {
		return invSeeing;
	}

	public String getSyncURL() {
		return syncURL;
	}

	public String getSyncKey() {
		return syncKey;
	}

	public UUID getSyncUUID() {
		return syncUUID;
	}

	public String getUrl() {
		return url;
	}

	public String getUser() {
		return user;
	}

	public String getPass() {
		return pass;
	}

	public String getDb() {
		return db;
	}

	public String getFullURL() {
		return fullURL;
	}

	public boolean allowConnections() {
		return this.allowConnections;
	}

	public long getServerStartTime() {
		return this.serverStartTime;
	}

	public long getServerUptime() {
		return System.currentTimeMillis() - this.serverStartTime;
	}

	public AbstractListCommandHandler getListCommandHandler() {
		return this.listCommandHandler;
	}

	public void setListCommandHandler(AbstractListCommandHandler listCommandHandler) {
		this.listCommandHandler = listCommandHandler;
	}

}
