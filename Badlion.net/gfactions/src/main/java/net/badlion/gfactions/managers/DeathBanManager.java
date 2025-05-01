package net.badlion.gfactions.managers;

import net.badlion.gberry.Gberry;
import net.badlion.gfactions.DeathBanConfig;
import net.badlion.gfactions.GFactions;
import net.badlion.gberry.utils.BukkitUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.sql.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DeathBanManager {

    private static DeathBanConfig config;

    private static Map<UUID, Integer> livesMap = new ConcurrentHashMap<>();
    private static Map<UUID, Timestamp> deathBannedPlayers = new ConcurrentHashMap<>();
    private static Map<UUID, Integer> heartShards = new ConcurrentHashMap<>();

    private static Map<UUID, Long> lastJoinTime = new ConcurrentHashMap<>();

	public static void putLastJoinTime(UUID uuid) {
		DeathBanManager.lastJoinTime.put(uuid, System.currentTimeMillis());
	}

	public static void removeLastJoinTime(UUID uuid) {
		DeathBanManager.lastJoinTime.remove(uuid);
	}

    public static Long getLastJoinTime(UUID uuid) {
        return DeathBanManager.lastJoinTime.get(uuid);
    }

    public static void initialize() {
        DeathBanManager.config = new DeathBanConfig("deathban");

        // Load DB Caches
        DeathBanManager.loadDeathBannedPlayers();
        DeathBanManager.loadLives();
        DeathBanManager.loadHeartShards();
    }

    private static void loadDeathBannedPlayers() {
        String query = "SELECT * FROM " + GFactions.PREFIX + "_death_bans WHERE unban_time > ?;";

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setTimestamp(1, new Timestamp(new DateTime(DateTimeZone.UTC).getMillis()));
            rs = Gberry.executeQuery(connection, ps);

            while (rs.next()) {
                Timestamp ts = rs.getTimestamp("unban_time");
                DeathBanManager.deathBannedPlayers.put(UUID.fromString(rs.getString("uuid")), ts);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
    }

    private static void loadLives() {
        String query = "SELECT * FROM " + GFactions.PREFIX + "_num_of_lives;";

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            rs = Gberry.executeQuery(connection, ps);

            while (rs.next()) {
                DeathBanManager.livesMap.put(UUID.fromString(rs.getString("uuid")), rs.getInt("num_of_lives"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
    }

    private static void loadHeartShards() {
        String query = "SELECT * FROM " + GFactions.PREFIX + "_heart_shards;";

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            rs = Gberry.executeQuery(connection, ps);

            while (rs.next()) {
                DeathBanManager.livesMap.put(UUID.fromString(rs.getString("uuid")), rs.getInt("num_of_shards"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
    }

    public static boolean isDeathBanned(UUID uuid) {
        Timestamp ts = DeathBanManager.deathBannedPlayers.get(uuid);
        if (ts == null) {
            return false;
        }

        if (ts.getTime() < System.currentTimeMillis()) {
            DeathBanManager.deathBannedPlayers.remove(uuid);
            return false;
        }

        return true;
    }

    public static Timestamp getDeathBanTime(UUID uuid) {
        return DeathBanManager.deathBannedPlayers.get(uuid);
    }

    public static void deathbanPlayer(final Player player) {
        int numOfMinsBanned = DeathBanManager.config.isSpecialDeathBanEnabled()
                                      ? DeathBanManager.config.getSpecialDeathBanInMinutes()
                                      : DeathBanManager.config.getDeathBanTimeInMinutes();

        if (player.hasPermission("badlion.lion")) {
            numOfMinsBanned -= numOfMinsBanned * DeathBanManager.config.getLionTimeOffPercentage();
        } else if (player.hasPermission("badlion.donatorplus")) {
            numOfMinsBanned -= numOfMinsBanned * DeathBanManager.config.getDonatorPlusTimeOffPercentage();
        } else if (player.hasPermission("badlion.donator")) {
            numOfMinsBanned -= numOfMinsBanned * DeathBanManager.config.getDonatorTimeOffPercentage();
        }

        long numOfMillisBanned = numOfMinsBanned * 60 * 1000;
        final Timestamp ts = new Timestamp(new DateTime(DateTimeZone.UTC).getMillis() + numOfMillisBanned);
        DeathBanManager.deathBannedPlayers.put(player.getUniqueId(), ts);

        BukkitUtil.runTaskAsync(new Runnable() {
            @Override
            public void run() {
                Bukkit.getLogger().info("deathban async");

                Connection connection = null;
                PreparedStatement ps = null;

                try {
                    connection = Gberry.getConnection();
                    Bukkit.getLogger().info("deathban permissions");

                    String query = "UPDATE " + GFactions.PREFIX + "_death_bans SET unban_time = ? WHERE uuid = ?;\n";
                    query += "INSERT INTO " + GFactions.PREFIX + "_death_bans (uuid, unban_time) SELECT ?, ? WHERE NOT EXISTS " +
                                     "(SELECT 1 FROM " + GFactions.PREFIX + "_death_bans WHERE uuid = ?);";

                    ps = connection.prepareStatement(query);

                    ps.setTimestamp(1, ts);
                    ps.setString(2, player.getUniqueId().toString());
                    ps.setString(3, player.getUniqueId().toString());
                    ps.setTimestamp(4, ts);
                    ps.setString(5, player.getUniqueId().toString());

                    Bukkit.getLogger().info("deathban pre-update");
                    Gberry.executeUpdate(connection, ps);
                    Bukkit.getLogger().info("deathban post-update");

                    query = "UPDATE " + GFactions.PREFIX + "_num_of_death_bans SET num_of_bans = num_of_bans + 1 WHERE uuid = ?;\n";
                    query += "INSERT INTO " + GFactions.PREFIX + "_num_of_death_bans (uuid, num_of_bans) SELECT ?, 1 WHERE NOT EXISTS " +
                                     "(SELECT 1 FROM " + GFactions.PREFIX + "_num_of_death_bans WHERE uuid = ?);";

                    ps.close();
                    ps = connection.prepareStatement(query);
                    ps.setString(1, player.getUniqueId().toString());
                    ps.setString(2, player.getUniqueId().toString());
                    ps.setString(3, player.getUniqueId().toString());

                    Gberry.executeUpdate(connection, ps);
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
                    if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
                }
            }
        });

        // Increasing deathban
        /*
        BukkitUtil.runTaskAsync(new Runnable() {
            @Override
            public void run() {
                Bukkit.getLogger().info("deathban async");
                String query = "SELECT * FROM " + GFactions.PREFIX + "_num_of_death_bans WHERE uuid = ?;";

                Connection connection = null;
                PreparedStatement ps = null;
                ResultSet rs = null;

                try {
                    int numOfDeathBans = 0;

                    connection = Gberry.getConnection();
                    Bukkit.getLogger().info("deathban connection");
                    ps = connection.prepareStatement(query);
                    ps.setString(1, player.getUniqueId().toString());
                    Bukkit.getLogger().info("deathban pre-query");
                    rs = Gberry.executeQuery(connection, ps);
                    Bukkit.getLogger().info("deathban post-query");

                    if (rs.next()) {
                        Bukkit.getLogger().info("deathban num_of_bans");
                        numOfDeathBans = rs.getInt("num_of_bans");
                    }

                    int numOfMinsBanned = DeathBanManager.config.isSpecialDeathBanEnabled()
                                                  ? DeathBanManager.config.getSpecialDeathBanInMinutes()
                                                  : DeathBanManager.config.getDeathBanTimeInMinutes();

                    // Add on the # of deaths they have times our multiplier
                    numOfMinsBanned += DeathBanManager.config.getExtraMinutesPerDeath() * numOfDeathBans;

                    // Too high?
                    if (numOfMinsBanned > DeathBanManager.config.getMaxDeathBanLengthInMinutes()) {
                        numOfMinsBanned = DeathBanManager.config.getMaxDeathBanLengthInMinutes();
                    }
                    Bukkit.getLogger().info("deathban math");

                    // Take off for donator
                    if (player.hasPermission("badlion.lion")) {
                        numOfMinsBanned -= numOfMinsBanned * DeathBanManager.config.getLionTimeOffPercentage();
                    } else if (player.hasPermission("badlion.donatorplus")) {
                        numOfMinsBanned -= numOfMinsBanned * DeathBanManager.config.getDonatorPlusTimeOffPercentage();
                    } else if (player.hasPermission("badlion.donator")) {
                        numOfMinsBanned -= numOfMinsBanned * DeathBanManager.config.getDonatorTimeOffPercentage();
                    }

                    long numOfMillisBanned = numOfMinsBanned * 60 * 1000;
                    Bukkit.getLogger().info("deathban permissions");

                    // Not sure why this is here...but it should not be called ASYNC
                    //GFactions.plugin.getServer().getPluginManager().callEvent(new DeathBanEvent(player, numOfMillisBanned));

                    query = "UPDATE " + GFactions.PREFIX + "_death_bans SET unban_time = ? WHERE uuid = ?;\n";
                    query += "INSERT INTO " + GFactions.PREFIX + "_death_bans (uuid, unban_time) SELECT ?, ? WHERE NOT EXISTS " +
                                     "(SELECT 1 FROM " + GFactions.PREFIX + "_death_bans WHERE uuid = ?);";

                    ps.close();
                    ps = connection.prepareStatement(query);
                    Timestamp ts = new Timestamp(new DateTime(DateTimeZone.UTC).getMillis() + numOfMillisBanned);
                    ps.setTimestamp(1, ts);
                    ps.setString(2, player.getUniqueId().toString());
                    ps.setString(3, player.getUniqueId().toString());
                    ps.setTimestamp(4, ts);
                    ps.setString(5, player.getUniqueId().toString());

                    Bukkit.getLogger().info("deathban pre-update");
                    Gberry.executeUpdate(connection, ps);
                    Bukkit.getLogger().info("deathban post-update");

                    // Add to local cache
                    DeathBanManager.deathBannedPlayers.put(player.getUniqueId(), ts);

                    Bukkit.getLogger().info("deathban added to map");

                    query = "UPDATE " + GFactions.PREFIX + "_num_of_death_bans SET num_of_bans = num_of_bans + 1 WHERE uuid = ?;\n";
                    query += "INSERT INTO " + GFactions.PREFIX + "_num_of_death_bans (uuid, num_of_bans) SELECT ?, 1 WHERE NOT EXISTS " +
                                     "(SELECT 1 FROM " + GFactions.PREFIX + "_num_of_death_bans WHERE uuid = ?);";

                    ps.close();
                    ps = connection.prepareStatement(query);
                    ps.setString(1, player.getUniqueId().toString());
                    ps.setString(2, player.getUniqueId().toString());
                    ps.setString(3, player.getUniqueId().toString());

                    Gberry.executeUpdate(connection, ps);
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
                    if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
                    if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
                }
            }
        });
         */
    }

    public static void unDeathBanPlayer(final UUID uuid) {
        Timestamp ts = DeathBanManager.deathBannedPlayers.remove(uuid);
        DeathBanManager.removeLives(uuid, 1);

        if (ts != null) {
            BukkitUtil.runTaskAsync(new Runnable() {
                @Override
                public void run() {
                    Connection connection = null;
                    PreparedStatement ps = null;

                    String query = "DELETE FROM " + GFactions.PREFIX + "_death_bans WHERE uuid = ?";

                    try {
                        connection = Gberry.getConnection();
                        ps = connection.prepareStatement(query);
                        ps.setString(1, uuid.toString());

                        Gberry.executeUpdate(connection, ps);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
                        if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
                    }
                }
            });
        }
    }

    public static int getNumOfLives(UUID uuid) {
        if (DeathBanManager.livesMap.containsKey(uuid)) {
            return DeathBanManager.livesMap.get(uuid);
        }

        return 0;
    }

    public static void addLives(final UUID uuid, int numOfLives) {
        int numOfCurrentLives = 0;

        if (DeathBanManager.livesMap.containsKey(uuid)) {
            numOfCurrentLives = DeathBanManager.livesMap.get(uuid);
        }

        final int newLives = numOfCurrentLives + numOfLives;
        DeathBanManager.livesMap.put(uuid, newLives);

        // Sync DB
        DeathBanManager.syncLivesDB(uuid, newLives);
    }

    public static void removeLives(final UUID uuid, int numOfLives) {
        int numOfCurrentLives = 0;

        if (DeathBanManager.livesMap.containsKey(uuid)) {
            numOfCurrentLives = DeathBanManager.livesMap.get(uuid);
        }

        int tmpLives = numOfCurrentLives - numOfLives;
        if (tmpLives < 0) {
            tmpLives = 0;
        }

        final int newLives = tmpLives;
        DeathBanManager.livesMap.put(uuid, newLives);

        // Sync DB
        DeathBanManager.syncLivesDB(uuid, newLives);
    }

    private static void syncLivesDB(final UUID uuid, final int newLives) {
        BukkitUtil.runTaskAsync(new Runnable() {
            @Override
            public void run() {
                String query = "UPDATE " + GFactions.PREFIX + "_num_of_lives SET num_of_lives = ? WHERE uuid = ?;\n";
                query += "INSERT INTO " + GFactions.PREFIX + "_num_of_lives (uuid, num_of_lives) SELECT ?, ? WHERE NOT EXISTS " +
                                 "(SELECT 1 FROM " + GFactions.PREFIX + "_num_of_lives WHERE uuid = ?);";

                Connection connection = null;
                PreparedStatement ps = null;

                try {
                    connection = Gberry.getConnection();
                    ps = connection.prepareStatement(query);
                    ps.setInt(1, newLives);
                    ps.setString(2, uuid.toString());
                    ps.setString(3, uuid.toString());
                    ps.setInt(4, newLives);
                    ps.setString(5, uuid.toString());

                    Gberry.executeUpdate(connection, ps);
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
                    if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
                }
            }
        });
    }

    public static int getHeartShards(UUID uuid) {
        if (!DeathBanManager.heartShards.containsKey(uuid)) {
            return 0;
        }

        return DeathBanManager.heartShards.get(uuid);
    }

    public static void addHeartShards(UUID uuid, int num) {
        int numOfShards = 0;

        if (DeathBanManager.heartShards.containsKey(uuid)) {
            numOfShards = DeathBanManager.getHeartShards(uuid);
        }

        numOfShards += num;

        if (numOfShards >= DeathBanManager.config.getHeartShardPieces()) {
            numOfShards -= DeathBanManager.config.getHeartShardPieces();

            DeathBanManager.addLives(uuid, 1);
        }

        // Store in cache and db
        DeathBanManager.heartShards.put(uuid, numOfShards);
        DeathBanManager.syncHeartShardsDB(uuid, numOfShards);
    }

    private static void syncHeartShardsDB(final UUID uuid, final int newShards) {
        BukkitUtil.runTaskAsync(new Runnable() {
            @Override
            public void run() {
                String query = "UPDATE " + GFactions.PREFIX + "_heart_shards SET num_of_shards = ? WHERE uuid = ?;\n";
                query += "INSERT INTO " + GFactions.PREFIX + "_heart_shards (uuid, num_of_shards) SELECT ?, ? WHERE NOT EXISTS " +
                                 "(SELECT 1 FROM " + GFactions.PREFIX + "_heart_shards WHERE uuid = ?);";

                Connection connection = null;
                PreparedStatement ps = null;

                try {
                    connection = Gberry.getConnection();
                    ps = connection.prepareStatement(query);
                    ps.setInt(1, newShards);
                    ps.setString(2, uuid.toString());
                    ps.setString(3, uuid.toString());
                    ps.setInt(4, newShards);
                    ps.setString(5, uuid.toString());

                    Gberry.executeUpdate(connection, ps);
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
                    if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
                }
            }
        });
    }

    public static String calculateTimeRemaining(double remaining) {
        remaining /= 1000;

        String rem = "";
        if (remaining >= 3600) {
            rem += (int)remaining / 3600 + "h ";
        }

        remaining %= 3600;
        if (remaining >= 60) {
            rem += (int)remaining / 60 + "m ";
        }

        remaining %= 60;
        rem += (int)remaining + "s";

        return rem;
    }

    public static DeathBanConfig getConfig() {
        return config;
    }

}
