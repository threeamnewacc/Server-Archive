package net.badlion.potpvp.managers;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.events.AsyncDelayedPlayerJoinEvent;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.PotPvPPlayer;
import net.badlion.potpvp.bukkitevents.TDMCounterFilledEvent;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TDMManager extends BukkitUtil.Listener {

    public static boolean cancelQuit = false;

    private static ConcurrentHashMap<UUID, TDMStats> tdmStats = new ConcurrentHashMap<>();

    @EventHandler
    public void onPlayerAsyncJoinDelayed(final AsyncDelayedPlayerJoinEvent event) {
        TDMManager.getTDMStatsFromDB(event.getConnection(), event.getUuid());

        event.getRunnables().add(new Runnable() {
	        public void run() {
		        PotPvPPlayer potPvPPlayer = PotPvPPlayerManager.getPotPvPPlayer(event.getUuid());
		        if (potPvPPlayer != null) {
			        potPvPPlayer.setTDMLoaded(true);
		        }
	        }
        });
    }

	@EventHandler(priority = EventPriority.LAST, ignoreCancelled = true)
	public void onPlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
		if (event.getMessage().equalsIgnoreCase("/stop") && event.getPlayer().isOp()) {
			TDMManager.cancelQuit = true;

			TDMManager.flushAllTDMStats();
		}
	}

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Stop double stuff
        if (TDMManager.cancelQuit) {
            return;
        }

	    final TDMStats stats = TDMManager.tdmStats.remove(event.getPlayer().getUniqueId());

	    // TODO: FIX MEMORY LEAK
	    if (stats == null) return;

        PotPvP.getInstance().getServer().getScheduler().runTaskAsynchronously(PotPvP.getInstance(), new Runnable() {
            @Override
            public void run() {
                Connection connection = null;
                try {
                    connection = Gberry.getConnection();

	                TDMManager.flushTDMStats(stats, connection);
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
    public static void flushAllTDMStats() {
	    final List<TDMStats> tdmStats = new ArrayList<>();
        for (TDMStats stats : TDMManager.tdmStats.values()) {
            tdmStats.add(stats);
        }

	    final int perGroup = (int) Math.ceil(tdmStats.size() / 50D);
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
                            if (tmpI * perGroup + j >= tdmStats.size()) {
                                return;
                            }

	                        TDMManager.flushTDMStats(tdmStats.get(tmpI * perGroup + j), connection);
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

    public static TDMStats addKill(UUID uuid) {
	    TDMStats tdmStats = TDMManager.tdmStats.get(uuid);

	    tdmStats.addKill();

	    return tdmStats;
    }

    public static TDMStats addDeath(UUID uuid) {
        TDMStats tdmStats = TDMManager.tdmStats.get(uuid);

	    tdmStats.addDeath();

	    return tdmStats;
    }

	public static TDMStats resetCurrentStats(UUID uuid) {
		TDMStats tdmStats = TDMManager.tdmStats.get(uuid);

		tdmStats.setKills(0);
		tdmStats.setDeaths(0);
		tdmStats.setAssists(0);
		tdmStats.setKillstreak(0);
		tdmStats.setCounter(0);

		return tdmStats;
	}

    public static TDMStats getTDMStats(UUID uuid) {
        return TDMManager.tdmStats.get(uuid);
    }

    public static void getTDMStatsFromDB(Connection connection, UUID uuid) {
        String query = "SELECT * FROM potion_tdm_stats_s12 WHERE uuid = ?;";

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = connection.prepareStatement(query);
            ps.setString(1, uuid.toString());

            rs = Gberry.executeQuery(connection, ps);

            if (rs.next()) {
                Gberry.log("TDM", "Found record in db for " + uuid.toString());
                TDMStats stats = new TDMStats(uuid, rs.getInt("kills"), rs.getInt("assists"), rs.getInt("deaths"), rs.getInt("max_kill_streak"));

	            TDMManager.tdmStats.put(uuid, stats);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(rs, ps);
        }

        // Technically this can cause a CME...but this stuff gets initialized so we don't care, only reads
	    TDMStats tdmStats = TDMManager.tdmStats.get(uuid);
	    if (tdmStats == null) {
		    Gberry.log("TDM", "Putting default tdmstats for " + uuid.toString());
		    TDMManager.tdmStats.put(uuid, new TDMStats(uuid, 0, 0, 0, 0));
	    }
    }

    public static void flushTDMStats(TDMStats tdmStats, Connection connection) {
        String query = "UPDATE potion_tdm_stats_s12 SET kills = ?, assists = ?, deaths = ?, max_kill_streak = ? WHERE uuid = ?;";
        query += "INSERT INTO potion_tdm_stats_s12 (uuid, kills, assists, deaths, max_kill_streak) SELECT ?, ?, ?, ?, ? WHERE NOT EXISTS " +
                "(SELECT 1 FROM potion_tdm_stats_s12 WHERE uuid = ?);";

        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(query);

            ps.setInt(1, tdmStats.getTotalKills());
            ps.setInt(2, tdmStats.getTotalAssists());
            ps.setInt(3, tdmStats.getTotalDeaths());
            ps.setInt(4, tdmStats.getMaxKillStreak());
            ps.setString(5, tdmStats.getUUID().toString());
            ps.setString(6, tdmStats.getUUID().toString());
            ps.setInt(7, tdmStats.getTotalKills());
            ps.setInt(8, tdmStats.getTotalAssists());
            ps.setInt(9, tdmStats.getTotalDeaths());
            ps.setInt(10, tdmStats.getMaxKillStreak());
            ps.setString(11, tdmStats.getUUID().toString());

            Gberry.executeUpdate(connection, ps);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
	        Gberry.closeComponents(ps);
        }
    }

    public static class TDMStats {

        private UUID uuid;

        private int kills = 0;
	    private int totalKills;

        private int deaths = 0;
        private int totalDeaths;

        private int assists = 0;
        private int totalAssists;

        private double counter = 0;

	    private int killstreak = 0;
        private int maxKillStreak;

        private Map<UUID, TDMDamager> damageMap = new HashMap<>();

        public TDMStats(UUID uuid, int kills, int assists, int deaths, int maxKillStreak) {
            this.uuid = uuid;

            this.totalKills = kills;
            this.totalAssists = assists;
            this.totalDeaths = deaths;
            this.maxKillStreak = maxKillStreak;
        }

        public void addDamage(UUID attacker, double dmg) {
            TDMDamager tdmDamager = this.damageMap.get(attacker);
            if (tdmDamager == null) {
                tdmDamager = new TDMDamager(attacker, dmg);
                this.damageMap.put(attacker, tdmDamager);
            } else {
                tdmDamager.addDamage(dmg);
            }
        }

        public void addKill() {
            this.kills++;
	        this.totalKills++;
            this.killstreak++;

            if (this.killstreak > this.maxKillStreak) {
                this.maxKillStreak = this.killstreak;
            }
        }

        public void addAssist() {
            this.assists++;
            this.totalAssists++;
        }

        public void addDeath() {
            this.deaths++;
	        this.totalDeaths++;

            // Reset killstreak
            this.killstreak = 0;
        }

	    public UUID getUUID() {
		    return uuid;
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

        public int getAssists() {
            return assists;
        }

	    public void setAssists(int assists) {
		    this.assists = assists;
	    }

	    public int getTotalAssists() {
            return totalAssists;
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

	    public void setKillstreak(int killstreak) {
		    this.killstreak = killstreak;
	    }

	    public int getMaxKillStreak() {
            return maxKillStreak;
        }

        public Collection<TDMDamager> getDamagers() {
            return this.damageMap.values();
        }

        public void clearDamage() {
            this.damageMap.clear();
        }

        public void addCounter(double amt) {
            this.counter += (amt * 5); // 5 because 20 hearts -> 100

            // Check to see if they are alive and exist
            Player player = PotPvP.getInstance().getServer().getPlayer(this.uuid);
            if (player != null) {
                Group group = PotPvP.getInstance().getPlayerGroup(player);
                if (GroupStateMachine.getInstance().getCurrentState(group) == GroupStateMachine.tdmState) {
                    if (!player.isDead() && player.getGameMode() == GameMode.SURVIVAL) {
                        if (this.counter >= 100) {
                            this.counter -= 100;

                            // Event
                            PotPvP.getInstance().getServer().getPluginManager().callEvent(new TDMCounterFilledEvent(this.uuid));
                        }
                    }
                }
            }
        }

	    public int getCounter() {
		    return (int) counter;
	    }

	    public void setCounter(double counter) {
		    this.counter = counter;
	    }
    }

    public static class TDMDamager {

        private UUID uuid;
        private double damage;
        private long lastDamageTime;

        public TDMDamager(UUID uuid, double damage) {
            this.uuid = uuid;
            this.damage = damage;
            this.lastDamageTime = System.currentTimeMillis();
        }

        public UUID getUuid() {
            return uuid;
        }

        public double getDamage() {
            return damage;
        }

        public void addDamage(double damage) {
            this.damage += damage;
            this.lastDamageTime = System.currentTimeMillis();
        }

        public long getLastDamageTime() {
            return lastDamageTime;
        }

    }

}
