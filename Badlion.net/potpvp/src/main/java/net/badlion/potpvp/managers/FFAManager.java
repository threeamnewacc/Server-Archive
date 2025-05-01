package net.badlion.potpvp.managers;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.events.AsyncDelayedPlayerJoinEvent;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.PotPvPPlayer;
import net.badlion.potpvp.ffaworlds.FFAWorld;
import net.badlion.potpvp.rulesets.KitRuleSet;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FFAManager extends BukkitUtil.Listener {

    public static boolean cancelQuit = false;

    private static ConcurrentHashMap<UUID, ConcurrentHashMap<KitRuleSet, FFAStats>> ffaStats = new ConcurrentHashMap<>();

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        // Initialize these for every player
        ConcurrentHashMap<KitRuleSet, FFAStats> stats = new ConcurrentHashMap<>();
        FFAManager.ffaStats.put(event.getPlayer().getUniqueId(), stats);

        Gberry.log("FFA", "Adding FFA Map " + event.getPlayer().getName() + " (" + event.getPlayer().getUniqueId().toString() + ")");
    }

    @EventHandler
    public void onPlayerAsyncJoinDelayed(final AsyncDelayedPlayerJoinEvent event) {
        FFAManager.getFFAStatsFromDB(event.getConnection(), event.getUuid());

        event.getRunnables().add(new Runnable() {
	        public void run() {
		        PotPvPPlayer potPvPPlayer = PotPvPPlayerManager.getPotPvPPlayer(event.getUuid());
		        if (potPvPPlayer != null) {
			        potPvPPlayer.setFFALoaded(true);
		        }
	        }
        });
    }

	@EventHandler(priority = EventPriority.LAST, ignoreCancelled = true)
	public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
		if (event.getMessage().equalsIgnoreCase("/stop") && event.getPlayer().isOp()) {
			FFAManager.cancelQuit = true;

			FFAManager.flushAllFFAStats();
		}
	}

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Stop double stuff
        if (FFAManager.cancelQuit) {
            return;
        }

	    final ConcurrentHashMap<KitRuleSet, FFAStats> stats = FFAManager.ffaStats.remove(event.getPlayer().getUniqueId());

        PotPvP.getInstance().getServer().getScheduler().runTaskAsynchronously(PotPvP.getInstance(), new Runnable() {
            @Override
            public void run() {
                Connection connection = null;
                try {
                    connection = Gberry.getConnection();

                    for (FFAStats ffaStats : stats.values()) {
                        FFAManager.flushFFAStats(ffaStats, connection);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
                }
            }
        });
    }

    /**
     * Flush all stats to database so we don't lose them
     */
    public static void flushAllFFAStats() {
	    final List<FFAStats> ffaStats = new ArrayList<>();
        for (ConcurrentHashMap<KitRuleSet, FFAStats> stats : FFAManager.ffaStats.values()) {
            ffaStats.addAll(stats.values());
        }

	    final int perGroup = (int) Math.ceil(ffaStats.size() / 50D);
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            final int tmpI = i;
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    Connection connection = null;

                    try {
                        connection = Gberry.getConnection();

                        for (int j = 0; j < perGroup; j++) {
                            if (tmpI * perGroup + j >= ffaStats.size()) {
                                return;
                            }

	                        FFAManager.flushFFAStats(ffaStats.get(tmpI * perGroup + j), connection);
                        }
                    } catch (SQLException e) {
	                    e.printStackTrace();
                    } finally {
                        if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
                    }
                }
            });

            t.start();
            threads.add(t);
        }

        for (int i = 0; i < 50; i++) {
            try {
                threads.get(i).join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static FFAStats addKill(UUID uuid, KitRuleSet kitRuleSet) {
	    FFAStats ffaStats = FFAManager.ffaStats.get(uuid).get(kitRuleSet);
	    ffaStats.addKill();

	    return ffaStats;
    }

    public static FFAStats addDeath(UUID uuid, KitRuleSet kitRuleSet) {
        FFAStats ffaStats = FFAManager.ffaStats.get(uuid).get(kitRuleSet);
	    ffaStats.addDeath();

	    return ffaStats;
    }

    public static FFAStats getFFAStats(UUID uuid, KitRuleSet kitRuleSet) {
        return FFAManager.ffaStats.get(uuid).get(kitRuleSet);
    }

    public static void getFFAStatsFromDB(Connection connection, UUID uuid) {
        String query = "SELECT * FROM potion_pvp_ffa_s11" + PotPvP.getInstance().getDBExtra() + " WHERE uuid = ?;";

        PreparedStatement ps = null;
        ResultSet rs = null;

        ConcurrentHashMap<KitRuleSet, FFAStats> totalStats = FFAManager.ffaStats.get(uuid);
        if (totalStats == null) {
            Gberry.log("FFA", "Found nothing for " + uuid.toString());
            return;
        }

        try {
            ps = connection.prepareStatement(query);
            ps.setString(1, uuid.toString());

            rs = Gberry.executeQuery(connection, ps);

            while (rs.next()) {
                Gberry.log("FFA", "Found record in db for " + uuid.toString() + " for ruleset + " + rs.getString("ruleset"));
                FFAStats stats = new FFAStats(uuid, KitRuleSet.getKitRuleSet(rs.getString("ruleset")), rs.getInt("kills"), rs.getInt("deaths"), rs.getInt("max_kill_streak"));

	            KitRuleSet kitRuleSet = KitRuleSet.getKitRuleSet(rs.getString("ruleset"));

	            // Safety check
	            if (kitRuleSet == null) {
		            PotPvP.getInstance().getLogger().severe("KIT NOT FOUND FOR " + uuid + " FFA STATS: " + rs.getString("ruleset"));
	            } else {
		            // Race condition avoided (hopefully lol)
		            totalStats.put(KitRuleSet.getKitRuleSet(rs.getString("ruleset")), stats);
	            }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
	        Gberry.closeComponents(rs, ps);
        }

        // Technically this can cause a CME...but this stuff gets initialized so we don't care, only reads
        List<KitRuleSet> kitRuleSets = KitRuleSet.getAllKitRuleSets();
        for (KitRuleSet kitRuleSet : kitRuleSets) {
            FFAWorld ffaWorld = FFAWorld.getFFAWorld(kitRuleSet);
            FFAStats ffaStats = FFAManager.ffaStats.get(uuid).get(kitRuleSet);
            if (ffaStats == null && ffaWorld != null) {
                Gberry.log("FFA", "Putting default ffastats for " + uuid.toString() + " with ruleset " + kitRuleSet.getName());
                totalStats.put(kitRuleSet, new FFAStats(uuid, kitRuleSet, 0, 0, 0));
            }
        }
    }

    public static void flushFFAStats(FFAStats ffaStats, Connection connection) {
        String query = "UPDATE potion_pvp_ffa_s11" + PotPvP.getInstance().getDBExtra() + " SET kills = ?, deaths = ?, max_kill_streak = ? WHERE uuid = ? AND ruleset = ?;";
        query += "INSERT INTO potion_pvp_ffa_s11" + PotPvP.getInstance().getDBExtra() + " (uuid, ruleset, kills, deaths, max_kill_streak) SELECT ?, ?, ?, ?, ? WHERE NOT EXISTS " +
                "(SELECT 1 FROM potion_pvp_ffa_s11" + PotPvP.getInstance().getDBExtra() + " WHERE uuid = ? AND ruleset = ?);";

        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(query);

            ps.setInt(1, ffaStats.getTotalKills());
            ps.setInt(2, ffaStats.getTotalDeaths());
            ps.setInt(3, ffaStats.getMaxKillStreak());
            ps.setString(4, ffaStats.getUUID().toString());
            ps.setString(5, ffaStats.getKitRuleSet().getName());
            ps.setString(6, ffaStats.getUUID().toString());
            ps.setString(7, ffaStats.getKitRuleSet().getName());
            ps.setInt(8, ffaStats.getTotalKills());
            ps.setInt(9, ffaStats.getTotalDeaths());
            ps.setInt(10, ffaStats.getMaxKillStreak());
            ps.setString(11, ffaStats.getUUID().toString());
            ps.setString(12, ffaStats.getKitRuleSet().getName());

            Gberry.executeUpdate(connection, ps);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
	        Gberry.closeComponents(ps);
        }
    }

    public static class FFAStats {

        private UUID player;
        private KitRuleSet kitRuleSet;

	    private int kills = 0;
	    private int totalKills;

	    private int deaths = 0;
	    private int totalDeaths;

	    private int killstreak = 0;
	    private int maxKillStreak;

        public FFAStats(UUID player, KitRuleSet kitRuleSet, int kills, int deaths, int maxKillStreak) {
            this.player = player;
            this.kitRuleSet = kitRuleSet;

	        this.totalKills = kills;
	        this.totalDeaths = deaths;
	        this.maxKillStreak = maxKillStreak;
        }

	    public void addKill() {
		    this.kills++;
		    this.totalKills++;
		    this.killstreak++;

		    if (this.killstreak > this.maxKillStreak) {
			    this.maxKillStreak = this.killstreak;
		    }
	    }

	    public void addDeath() {
		    this.deaths++;
		    this.totalDeaths++;

		    // Reset killstreak
		    this.killstreak = 0;
	    }

        public UUID getUUID() {
            return player;
        }

        public KitRuleSet getKitRuleSet() {
            return kitRuleSet;
        }

	    public int getKills() {
		    return kills;
	    }

	    public void setKills(int kills) {
		    this.kills = kills;
	    }

	    public int getTotalKills() {
		    return totalKills;
	    }

	    public int getDeaths() {
		    return deaths;
	    }

	    public void setDeaths(int deaths) {
		    this.deaths = deaths;
	    }

	    public int getTotalDeaths() {
		    return totalDeaths;
	    }

	    public int getKillstreak() {
		    return killstreak;
	    }

	    public int getMaxKillStreak() {
		    return maxKillStreak;
	    }

    }

}
