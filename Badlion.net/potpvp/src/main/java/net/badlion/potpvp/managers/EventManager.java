package net.badlion.potpvp.managers;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.events.Event;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EventManager extends BukkitUtil.Listener {

    private static Map<UUID, Map<Event.EventType, EventStats>> stats = new ConcurrentHashMap<>();

    /*@EventHandler
    public void onPlayerAsyncJoinDelayed(final AsyncDelayedPlayerJoinEvent event) {
        EventManager.stats.put(event.getUuid(), getStatsFromDB(event.getConnection(), event.getUuid()));

        event.getRunnables().add(new Runnable() {
            public void run() {
                PotPvPPlayer potPvPPlayer = PotPvPPlayerManager.getPotPvPPlayer(event.getUuid());
                if (potPvPPlayer != null) {
                    potPvPPlayer.setEventsLoaded(true);
                }
            }
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        EventManager.stats.remove(event.getPlayer().getUniqueId());
    }

    public static EventStats getStats(Event.EventType eventType, UUID uuid) {
        return EventManager.stats.get(uuid).get(eventType);
    }*/

    public static void updateStats(final Collection<EventStats> eventStats) {
        new BukkitRunnable() {

            @Override
            public void run() {
                Connection connection = null;
                try {
                    connection = Gberry.getConnection();
                    for (EventStats eventStat : eventStats) {
                        EventManager.updateStatsDB(connection, eventStat);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
                }
            }


        }.runTaskAsynchronously(PotPvP.getInstance());
    }

    private static void updateStatsDB(Connection connection, EventStats eventStats) {
        PreparedStatement ps = null;

        try {
            String query = "UPDATE potion_event_stats_s12" + PotPvP.getInstance().getDBExtra() + " SET wins = wins + ?, games = games + ?, kills = kills + ?, deaths = deaths + ? WHERE uuid = ? AND type = ?;";
            query += "INSERT INTO potion_event_stats_s12" + PotPvP.getInstance().getDBExtra() + " (uuid, type, wins, games, rating, kills, deaths) SELECT ?, ?, ?, ?, 1400, ?, ? WHERE NOT EXISTS " +
                             "(SELECT 1 FROM potion_event_stats_s12" + PotPvP.getInstance().getDBExtra() + " WHERE uuid = ? AND type = ?);";

            ps = connection.prepareStatement(query);

            ps.setInt(1, eventStats.getWins());
            ps.setInt(2, eventStats.getGames());
            ps.setInt(3, eventStats.getKills());
            ps.setInt(4, eventStats.getDeaths());
            ps.setString(5, eventStats.getUuid().toString());
            ps.setString(6, eventStats.getEventType().name());
            ps.setString(7, eventStats.getUuid().toString());
            ps.setString(8, eventStats.getEventType().name());
            ps.setInt(9, eventStats.getWins());
            ps.setInt(10, eventStats.getGames());
            ps.setInt(11, eventStats.getKills());
            ps.setInt(12, eventStats.getDeaths());
            ps.setString(13, eventStats.getUuid().toString());
            ps.setString(14, eventStats.getEventType().name());

            Gberry.executeUpdate(connection, ps);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(ps);
        }
    }

    private Map<Event.EventType, EventStats> getStatsFromDB(Connection connection, UUID uuid) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        Map<Event.EventType, EventStats> stats = new HashMap<>();

        try {
            String query = "SELECT * FROM potion_event_stats_s12" + PotPvP.getInstance().getDBExtra() + " WHERE uuid = ?;";

            ps = connection.prepareStatement(query);
            ps.setString(1, uuid.toString());
            rs = Gberry.executeQuery(connection, ps);

            while (rs.next()) {
                EventStats eventStats = new EventStats(Event.EventType.valueOf(rs.getString("type")), uuid,
                                                              rs.getInt("kills"), rs.getInt("deaths"), rs.getInt("wins"),
                                                              rs.getInt("games"), rs.getInt("rating"));
                stats.put(eventStats.getEventType(), eventStats);
            }

            // Insert missing records
            for (Event.EventType eventType : Event.EventType.values()) {
                if (!stats.containsKey(eventType)) {
                    stats.put(eventType, new EventStats(eventType, uuid, 0, 0, 0, 0, 0));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            Gberry.closeComponents(rs, ps);
        }

        return stats;
    }

    public static class EventStats {

        private Event.EventType eventType;
        private UUID uuid;
        private int kills;
        private int deaths;
        private int wins;
        private int games;
        private int rating;

        public EventStats(Event.EventType eventType, UUID uuid, int kills, int deaths, int wins, int games, int rating) {
            this.eventType = eventType;
            this.uuid = uuid;
            this.kills = kills;
            this.deaths = deaths;
            this.wins = wins;
            this.games = games;
            this.rating = rating;
        }

        public Event.EventType getEventType() {
            return eventType;
        }

        public UUID getUuid() {
            return uuid;
        }

        public int getKills() {
            return kills;
        }

        public void addKill() {
            this.kills += 1;
        }

        public void addKills(int number) {
            this.kills += number;
        }

        public int getDeaths() {
            return deaths;
        }

        public void addDeath() {
            this.deaths += 1;
        }

        public void addDeaths(int number) {
            this.deaths += 1;
        }

        public int getWins() {
            return wins;
        }

        public void addWin() {
            this.wins += 1;
        }

        public int getGames() {
            return games;
        }

        public void addGame() {
            this.games += 1;
        }

        public int getRating() {
            return rating;
        }

        public void setRating(int rating) {
            this.rating = rating;
        }
    }

}
