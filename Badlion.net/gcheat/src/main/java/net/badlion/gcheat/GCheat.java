package net.badlion.gcheat;

import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.badlion.gberry.Gberry;
import net.badlion.gcheat.listeners.AutoClickerListener;
import net.badlion.gcheat.listeners.BlockGlitchListener;
import net.badlion.gcheat.listeners.BungeeCordListener;
import net.badlion.gcheat.listeners.FoodFixListener;
import net.badlion.gcheat.listeners.GSyncListener;
import net.badlion.gcheat.listeners.InventoryTweakListener;
import net.badlion.gcheat.listeners.MovementListener;
import net.badlion.gcheat.listeners.ReducedKnockbackListener;
import net.badlion.gcheat.listeners.RegenListener;
import net.badlion.gcheat.listeners.SwingListener;
import net.badlion.gcheat.listeners.VapeListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.GCheatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GCheat extends JavaPlugin implements Listener {

	public static GCheat plugin;

    public static final Random random = new Random();

	// Logger stuff
	private Logger chatLogger;
	private Marker marker;
    private boolean logMessages = false;
    private String serverName;

    private Queue<GCheatRecord> records = new ConcurrentLinkedQueue<>();
    private Queue<SwingListener.SwingTracker> swingRecords = new ConcurrentLinkedQueue<>();

    public static Set<String> disabledPlayers = new HashSet<>();
    public static Set<String> ignoredPlayers = new HashSet<>();
    public static Set<UUID> bannedUUIDS = new HashSet<>(); // Stop double bans

	@Override
	public void onEnable() {
        GCheat.plugin = this;

        this.saveDefaultConfig();
        this.logMessages = this.getConfig().getBoolean("log-messages", false);
        this.serverName = Gberry.serverName;

		// Setup log4j2 logger
		this.chatLogger = LogManager.getLogger(GCheat.class.getName());
		this.marker = MarkerManager.getMarker("GCHEAT");

		this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getPluginManager().registerEvents(new AutoClickerListener(this), this);
        this.getServer().getPluginManager().registerEvents(new GSyncListener(), this);
        this.getServer().getPluginManager().registerEvents(new InventoryTweakListener(this), this);
        this.getServer().getPluginManager().registerEvents(new FoodFixListener(), this);
        this.getServer().getPluginManager().registerEvents(new MovementListener(), this);
        this.getServer().getPluginManager().registerEvents(new RegenListener(), this);
        this.getServer().getPluginManager().registerEvents(new SwingListener(), this);
        this.getServer().getPluginManager().registerEvents(new BlockGlitchListener(), this);
        this.getServer().getPluginManager().registerEvents(new ReducedKnockbackListener(), this);
        //this.getServer().getPluginManager().registerEvents(new NoFallListener(), this);

        BungeeCordListener bungeeCordListener = new BungeeCordListener();
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", bungeeCordListener);
        this.getServer().getPluginManager().registerEvents(bungeeCordListener, this);

        VapeListener vapeListener = new VapeListener();
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "LOLIMAHCKER", vapeListener);
        this.getServer().getPluginManager().registerEvents(vapeListener, this);

        new BukkitRunnable() {

            @Override
            public void run() {
                GCheat.this.insertGCheatRecords();
                GCheat.this.insertGCheatSwingRecords();
            }

        }.runTaskTimerAsynchronously(this, 20 * 5, 20 * 5);

        this.getCommand("dgcheat").setExecutor(new DisableGCheatCommand());

	}

	@Override
	public void onDisable() {
        this.insertGCheatRecords();
	}

    @EventHandler(ignoreCancelled=true)
    public void gCheatLogEventNew(final GCheatEvent event) {
        if (!disabledPlayers.contains(event.getPlayer().getName().toLowerCase())) {
	        Gberry.plugin.getServer().getScheduler().runTask(Gberry.plugin, new Runnable() {
		        @Override
		        public void run() {
			        Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "ad " + event.getMsg());
		        }
	        });
            if (ignoredPlayers.contains(event.getPlayer().getName().toLowerCase())) {
                return;
            }

            //if (event.getLevel().ordinal() >= GCheatEvent.Level.ADMIN.ordinal()) {
            //} else if (event.getLevel().ordinal() >= GCheatEvent.Level.MOD.ordinal()) {
            //    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "mc " + event.getMsg());
            //}

            // Automatic Ban handler
            this.handleAutomaticBans(event);

            this.logMessage(event.getPlayer(), event.getMsg());
        } else {
            Bukkit.getLogger().info(event.getMsg());
        }
    }

    private static Map<UUID, List<Long>> killAuraViolationsTypeC = new HashMap<>();

    private static Map<UUID, Long> ignoreHI = new HashMap<>();
    private static Map<UUID, List<Long>> killAuraViolationsTypeHShort = new HashMap<>();
    private static Map<UUID, List<Long>> killAuraViolationsTypeHLong = new HashMap<>();
    private static Map<UUID, List<Long>> killAuraViolationsTypeIShort = new HashMap<>();
    private static Map<UUID, List<Long>> killAuraViolationsTypeILong = new HashMap<>();
    private static Map<UUID, List<Long>> killAuraViolationsTypeS = new HashMap<>();
    private static Map<UUID, List<Long>> killAuraViolationsTypeT8 = new HashMap<>();
    private static Map<UUID, List<Long>> killAuraViolationsTypeT9 = new HashMap<>();
    private static Map<UUID, List<Long>> criticalsTypeB = new HashMap<>();
    private static Map<UUID, List<Long>> badPacketsTypeA = new HashMap<>();

    public void handleAutomaticBans(final GCheatEvent event) {
        if (event.getType() == GCheatEvent.Type.KILL_AURA) {
            if (event.getMsg().contains("Type C")) {
                int lvl = Integer.parseInt(event.getMsg().substring(event.getMsg().length() - 1));
                if (lvl <= 7) {
                    List<Long> times = killAuraViolationsTypeC.get(event.getPlayer().getUniqueId());
                    if (times == null) {
                        times = new ArrayList<>();
                        killAuraViolationsTypeC.put(event.getPlayer().getUniqueId(), times);
                    }

                    int violations = 0;
                    Long currentTime = System.currentTimeMillis();
                    times.add(currentTime);
                    for (Iterator<Long> iterator = times.iterator(); iterator.hasNext();) {
                        Long time = iterator.next();
                        if (time + 60 * 5 * 1000 >= currentTime) {
                            ++violations;
                        } else {
                            iterator.remove();
                        }
                    }

                    if (((lvl == 1 || (lvl >= 4 && lvl <= 7)) && violations == 5) || ((lvl == 2 || lvl == 3) && violations == 10)) {
                        if (event.getPlayer().getName().equals("GCheat")) {
                            return;
                        }

                        if (Bukkit.getSpigotJarVersion() == Server.SERVER_VERSION.V1_9) return; // Tmp
                        if (!GCheat.bannedUUIDS.contains(event.getPlayer().getUniqueId())) {
                            GCheat.bannedUUIDS.add(event.getPlayer().getUniqueId());
	                        Gberry.plugin.getServer().getScheduler().runTask(Gberry.plugin, new Runnable() {
		                        @Override
		                        public void run() {
			                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban " + event.getPlayer().getName() + " [GCheat] Unfair Advantage");
		                        }
	                        });
                        }
                    }
                }
            } else if (event.getMsg().contains("Type E2")) {
                GCheat.delayedBan(event.getPlayer());
            } else if (event.getMsg().contains("Type H")) {
                // Old style of messages
                if (event.getMsg().contains("Type H (Experimental) 1") || event.getMsg().contains("Type H (Experimental) 2")) {
                    Long ts = ignoreHI.get(event.getPlayer().getUniqueId());
                    if (ts == null || ts + 5000 < System.currentTimeMillis()) {
                        if (!GCheat.handleTimeDetectionWithSkips(killAuraViolationsTypeHShort, event.getPlayer().getUniqueId(), event.getPlayer().getName(), 60 * 10 * 1000, 10, event.getMsg())) {
                            GCheat.handleTimeDetectionWithSkips(killAuraViolationsTypeHLong, event.getPlayer().getUniqueId(), event.getPlayer().getName(), 60 * 60 * 1000, 30, event.getMsg());
                        }
                    }
                } else if (event.getMsg().contains("Type H | ")) { // New style of messages
                    String[] parts = event.getMsg().split(" \\| ");
                    parts = parts[1].split(" ");
                    for (String part : parts) {
                        int val = Integer.parseInt(part.replace(" ", ""));

                        // Verify they aren't lagging
                        if (val > 2) {
                            killAuraViolationsTypeHShort.remove(event.getPlayer().getUniqueId());
                            killAuraViolationsTypeHLong.remove(event.getPlayer().getUniqueId());
                            ignoreHI.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
                        } else {
                            // Check that they weren't lagging recently
                            Long ts = ignoreHI.get(event.getPlayer().getUniqueId());
                            if (ts == null || ts + 5000 < System.currentTimeMillis()) {
                                if (!GCheat.handleTimeDetectionWithSkips(killAuraViolationsTypeHShort, event.getPlayer().getUniqueId(), event.getPlayer().getName(), 60 * 10 * 1000, 10, event.getMsg())) {
                                    GCheat.handleTimeDetectionWithSkips(killAuraViolationsTypeHLong, event.getPlayer().getUniqueId(), event.getPlayer().getName(), 60 * 60 * 1000, 30, event.getMsg());
                                }
                            }
                        }
                    }
                } else {
                    killAuraViolationsTypeHShort.remove(event.getPlayer().getUniqueId());
                    killAuraViolationsTypeHLong.remove(event.getPlayer().getUniqueId());
                    ignoreHI.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
                }
            } else if (event.getMsg().contains("Type I")) {
                if (event.getMsg().contains("Type I (Experimental) 1") || event.getMsg().contains("Type I (Experimental) 2")) {
                    Long ts = ignoreHI.get(event.getPlayer().getUniqueId());
                    if (ts == null || ts + 5000 < System.currentTimeMillis()) {
                        if (!GCheat.handleTimeDetectionWithSkips(killAuraViolationsTypeIShort, event.getPlayer().getUniqueId(), event.getPlayer().getName(), 60 * 10 * 1000, 10, event.getMsg())) {
                            GCheat.handleTimeDetectionWithSkips(killAuraViolationsTypeILong, event.getPlayer().getUniqueId(), event.getPlayer().getName(), 60 * 60 * 1000, 30, event.getMsg());
                        }
                    }
                } else if (event.getMsg().contains(("Type I | "))) { // New style of messages
                    String[] parts = event.getMsg().split(" \\| ");
                    parts = parts[1].split(" ");
                    for (String part : parts) {
                        int val = Integer.parseInt(part.replace(" ", ""));

                        // Verify they aren't lagging
                        if (val > 2) {
                            killAuraViolationsTypeIShort.remove(event.getPlayer().getUniqueId());
                            killAuraViolationsTypeILong.remove(event.getPlayer().getUniqueId());
                            ignoreHI.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
                        } else {
                            // Check that they weren't lagging recently
                            Long ts = ignoreHI.get(event.getPlayer().getUniqueId());
                            if (ts == null || ts + 5000 < System.currentTimeMillis()) {
                                if (!GCheat.handleTimeDetectionWithSkips(killAuraViolationsTypeIShort, event.getPlayer().getUniqueId(), event.getPlayer().getName(), 60 * 10 * 1000, 10, event.getMsg())) {
                                    GCheat.handleTimeDetectionWithSkips(killAuraViolationsTypeILong, event.getPlayer().getUniqueId(), event.getPlayer().getName(), 60 * 60 * 1000, 30, event.getMsg());
                                }
                            }
                        }
                    }
                } else {
                    killAuraViolationsTypeIShort.remove(event.getPlayer().getUniqueId());
                    killAuraViolationsTypeILong.remove(event.getPlayer().getUniqueId());
                    ignoreHI.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
                }
            } else if (event.getMsg().contains("Type J") || event.getMsg().contains("Type K")) {
                // Remove logs
                long currentTime = System.currentTimeMillis() / 1000;
                GCheat.removeLogs(killAuraViolationsTypeHShort, event.getPlayer().getUniqueId(), currentTime);
                GCheat.removeLogs(killAuraViolationsTypeHLong, event.getPlayer().getUniqueId(), currentTime);
                GCheat.removeLogs(killAuraViolationsTypeIShort, event.getPlayer().getUniqueId(), currentTime);
                GCheat.removeLogs(killAuraViolationsTypeILong, event.getPlayer().getUniqueId(), currentTime);
            } else if (event.getMsg().contains("Type U")) {
                GCheat.handleTimeDetection(killAuraViolationsTypeS, event.getPlayer().getUniqueId(), event.getPlayer().getName(), 60 * 1000, 3);
            } else if (event.getMsg().contains("Type T (8)")) {
                GCheat.handleTimeDetection(killAuraViolationsTypeT8, event.getPlayer().getUniqueId(), event.getPlayer().getName(), 10 * 60 * 1000, 10);
            } else if (event.getMsg().contains("Type T (9)")) {
                GCheat.handleTimeDetection(killAuraViolationsTypeT9, event.getPlayer().getUniqueId(), event.getPlayer().getName(), 10 * 60 * 1000, 5);
            }
        } else if (event.getType() == GCheatEvent.Type.REGEN) {
            if (event.getMsg().contains("Regen Type B")) {
                GCheat.delayedBan(event.getPlayer());
            }
        } else if (event.getType() == GCheatEvent.Type.CRIT) {
            if (event.getMsg().contains("Criticals Type B")) {
                GCheat.handleTimeDetection(criticalsTypeB, event.getPlayer().getUniqueId(), event.getPlayer().getName(), 60000, 10);
            }
        } else if (event.getType() == GCheatEvent.Type.UNKNOWN) {
            if (event.getMsg().contains("Bad Packets Type A")) {
                if (event.getMsg().endsWith("(PacketPlayInArmAnimation)") || event.getMsg().endsWith("(PacketPlayInEntityAction)") || event.getMsg().endsWith("(PacketPlayInUseEntity)")) {
                    GCheat.handleTimeDetection(badPacketsTypeA, event.getPlayer().getUniqueId(), event.getPlayer().getName(), 60 * 1000, 2);
                }
            }
        }
    }


	public void logMessage(Player player, String message) {
        message = "[MC-" + player.getVersion() + "]" + message;
        if (this.logMessages) {
		    this.chatLogger.info(this.marker, message);
        } else {
            this.records.add(new GCheatRecord(player, message));
        }
	}

    public class GCheatRecord {

        private Player player;
        private String msg;
        private long ts;

        public GCheatRecord(Player player, String msg) {
            this.player = player;
            this.msg = msg;
            this.ts = new DateTime(DateTimeZone.UTC).getMillis();
        }

        public Player getPlayer() {
            return player;
        }

        public String getMsg() {
            return msg;
        }

        public long getTs() {
            return ts;
        }
    }

    public void insertGCheatRecords() {
        StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO gcheat_logs (server, msg, username, uuid, log_time) VALUES ");

	    List<GCheatRecord> records = new ArrayList<>();
        Iterator<GCheatRecord> iterator = this.records.iterator();
        while (iterator.hasNext()) {
            records.add(iterator.next());
            iterator.remove();

	        builder.append("(?, ?, ?, ?, ?), ");
        }

        if (records.size() == 0) {
            return;
        }

        String query = builder.substring(0, builder.length() - 2) + ";";

        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);

            int i = 1;

            for (GCheatRecord record : records) {
                ps.setString(i++, this.serverName);
                ps.setString(i++, record.getMsg());
                ps.setString(i++, record.getPlayer().getName());
                ps.setString(i++, record.getPlayer().getUniqueId().toString());
                ps.setTimestamp(i++, new Timestamp(record.getTs()));
            }

            Gberry.executeUpdate(connection, ps);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(ps, connection);
        }
    }

	public void insertGCheatSwingRecords() {
		// POSTGRES
		StringBuilder builder = new StringBuilder();
		// 9.5 builder.append("INSERT INTO gcheat_swing_logs (server, uuid, swings, hits, swing_hit_percentage) VALUES ");

		List<SwingListener.SwingTracker> swingRecords = new ArrayList<>();
		Iterator<SwingListener.SwingTracker> iterator = this.swingRecords.iterator();
		while (iterator.hasNext()) {
			swingRecords.add(iterator.next());
			iterator.remove();

			// 9.5 builder.append("(?, ?, ?, ?, ?) ON CONFLICT (server, uuid) DO UPDATE SET swings = swings + ?, hits = hits + ?, swing_hit_percentage = ? ");

			// TODO: Division by zero error 'swing_hit_percentage = hits * 100.0 / swings', can't do in single query
			builder.append("UPDATE gcheat_swing_logs SET swings = swings + ?, hits = hits + ? WHERE server = ? AND uuid = ?;");
			builder.append("UPDATE gcheat_swing_logs SET swing_hit_percentage = hits * 100.0 / swings WHERE server = ? AND uuid = ? AND swings != 0;");
			builder.append("INSERT INTO gcheat_swing_logs (server, uuid, swings, hits, swing_hit_percentage) SELECT ?, ?, ?, ?, ? WHERE NOT EXISTS (SELECT 1 FROM gcheat_swing_logs WHERE server = ? AND uuid = ?);");
		}

		if (swingRecords.size() == 0) {
			return;
		}

		String query = builder.toString();

		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);

			int i = 1;

			for (SwingListener.SwingTracker swingTracker : swingRecords) {
				ps.setInt(i++, swingTracker.getSwings());
				ps.setInt(i++, swingTracker.getHits());
				ps.setString(i++, Gberry.serverType.getInternalName());
				ps.setString(i++, swingTracker.getUniqueId().toString());

				ps.setString(i++, Gberry.serverType.getInternalName());
				ps.setString(i++, swingTracker.getUniqueId().toString());

				ps.setString(i++, Gberry.serverType.getInternalName());
				ps.setString(i++, swingTracker.getUniqueId().toString());
				ps.setInt(i++, swingTracker.getSwings());
				ps.setInt(i++, swingTracker.getHits());
				ps.setDouble(i++, swingTracker.getSwingHitPercentage());

				ps.setString(i++, Gberry.serverType.getInternalName());
				ps.setString(i++, swingTracker.getUniqueId().toString());
			}

			Gberry.executeUpdate(connection, ps);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(ps, connection);
		}

		// COUCHDB
		JSONObject matchDataBulk = new JSONObject();
		JSONArray matchDataDocs = new JSONArray();

		for (SwingListener.SwingTracker swingTracker : swingRecords) {
			JSONObject matchData = new JSONObject();
			matchData.put("swings", swingTracker.getSwings());
			matchData.put("hits", swingTracker.getHits());
			matchData.put("swing_hit_percentage", swingTracker.getSwingHitPercentage());

			// Add custom data from the match
			if (swingTracker.getData() != null) {
				for (String key : swingTracker.getData().keySet()) {
					matchData.put(key, swingTracker.getData().get(key));
				}
			}

			matchDataDocs.add(matchData);
		}

		matchDataBulk.put("docs", matchDataDocs);

		// We don't care about the result in this case (not modifying further)
		try {
			Gberry.executeCouchDBBulkPostQuery(matchDataBulk, "gcheat");
		} catch (HTTPRequestFailException e) {
			GCheat.plugin.getLogger().info(matchDataBulk.toJSONString());
			GCheat.plugin.getLogger().info("Failed HTTP match-player request with error " + e.getResponseCode());
			GCheat.plugin.getLogger().info(e.getResponse());
		}
	}

    public static boolean handleTimeDetection(Map<UUID, List<Long>> map, UUID uuid, String name, int length, int violationCount) {
        return handleTimeDetection(map, uuid, name, length, violationCount, " [GCheat] Unfair Advantage");
    }

    public static boolean handleTimeDetection(Map<UUID, List<Long>> map, UUID uuid, final String name, int length, int violationCount, final String banReason) {
        List<Long> times = map.get(uuid);
        if (times == null) {
            times = new ArrayList<>();
            times.add(System.currentTimeMillis());
            map.put(uuid, times);
        } else {
            long currentTime = System.currentTimeMillis();
            int violations = 0;
            times.add(currentTime);

            for (Iterator<Long> iterator = times.iterator(); iterator.hasNext();) {
                Long time = iterator.next();
                if (time + length >= currentTime) {
                    ++violations;
                } else {
                    iterator.remove();
                }
            }

            if (Bukkit.getSpigotJarVersion() == Server.SERVER_VERSION.V1_9) return false; // Tmp

            // They hit 5 of the exact same packets in 3 seconds...seems very fishy
            if (violations >= violationCount) {
                if (!disabledPlayers.contains(name.toLowerCase())) {
                    if (!GCheat.bannedUUIDS.contains(uuid)) {
                        GCheat.bannedUUIDS.add(uuid);
	                    Gberry.plugin.getServer().getScheduler().runTask(Gberry.plugin, new Runnable() {
		                    @Override
		                    public void run() {
			                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban " + name + banReason);
		                    }
	                    });
                    }
                }
                return true;
            }
        }

        return false;
    }

    public static void removeLogs(Map<UUID, List<Long>> map, UUID uuid, long time) {
        List<Long> times = map.get(uuid);
        if (times != null) {
            times.remove(time);
        }
    }

    // Skip timestamps that we already have
    public static boolean handleTimeDetectionWithSkips(Map<UUID, List<Long>> map, UUID uuid, final String name, int length, int violationCount, String msg) {
        List<Long> times = map.get(uuid);
        if (times == null) {
            times = new ArrayList<>();
            times.add(System.currentTimeMillis() / 1000);
            map.put(uuid, times);
        } else {
            long currentTime = System.currentTimeMillis() / 1000;

            // Don't allow duplicates
            if (times.contains(currentTime)) {
                return false;
            }

            int violations = 0;
            times.add(currentTime);

            for (Iterator<Long> iterator = times.iterator(); iterator.hasNext();) {
                Long time = iterator.next();
                if (time + (length / 1000) >= currentTime) {
                    ++violations;
                } else {
                    iterator.remove();
                }
            }

            if (Bukkit.getSpigotJarVersion() == Server.SERVER_VERSION.V1_9) return false; // Tmp

            // They hit 5 of the exact same packets in 3 seconds...seems very fishy
            if (violations >= violationCount) {
                if (!disabledPlayers.contains(name.toLowerCase())) {
                    if (!GCheat.bannedUUIDS.contains(uuid)) {
                        GCheat.bannedUUIDS.add(uuid);
	                    Gberry.plugin.getServer().getScheduler().runTask(Gberry.plugin, new Runnable() {
		                    @Override
		                    public void run() {
			                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban " + name + " [GCheat] Unfair Advantage");
		                    }
	                    });
                    }
                }
                return true;
            }
        }

        return false;
    }

    public static boolean isAntiCheatActivated() {
	    double[] recentTPS = GCheat.plugin.getServer().getRecentTps();

	    int offSet = recentTPS.length == 4 ? 1 : 0;

        return Bukkit.getGCheatActivation() && Gberry.getTPS(Gberry.Benchmark.ONE_MINUTE) > 19.0;
    }

	public void addSwingRecord(SwingListener.SwingTracker swingTracker) {
		this.swingRecords.add(swingTracker);
	}

	public static void delayedBan(final Player player) {
        Gberry.plugin.getServer().getScheduler().runTaskLater(Gberry.plugin, new Runnable() {
            @Override
            public void run() {
                if (GCheat.bannedUUIDS.add(player.getUniqueId())) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban " + player.getName() + " [GCheat] Unfair Advantage");
                }
            }
        }, 100 + random.nextInt(200));
    }
}
