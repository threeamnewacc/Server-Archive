package net.badlion.survivalgames.managers;

import net.badlion.gberry.Gberry;
import net.badlion.survivalgames.Ladder;
import net.badlion.survivalgames.util.BukkitUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RatingManager extends BukkitUtil.Listener {

    public static int DEFAULT_RATING = 1400;

    private static ConcurrentHashMap<UUID, ConcurrentHashMap<Ladder, Integer>> rankedRatings = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<UUID, Integer> globalRatings = new ConcurrentHashMap<>();

    @EventHandler
    public void onPlayerJoin(final AsyncPlayerPreLoginEvent event) {
        // Initialize these for every player
        if (!RatingManager.rankedRatings.containsKey(event.getUniqueId())) {
            ConcurrentHashMap<Ladder, Integer> ratings = new ConcurrentHashMap<>();
            RatingManager.rankedRatings.put(event.getUniqueId(), ratings);
        }

        Gberry.log("RATING", "Adding rating map for " + event.getUniqueId().toString());

        // We get overloaded otherwise
        RatingManager.getDBUserRatings(event.getUniqueId());
    }

    public static void handleGlobalRating(UUID uuid) {
        ConcurrentHashMap<Ladder, Integer> ratings = RatingManager.rankedRatings.get(uuid);

        // Handle global rating
        int totalRating = 0;
        int i = 0;
        for (Integer r : ratings.values()) {
            totalRating += r;
            ++i;
        }

        while (i++ != Ladder.getAllLadders().size()) {
            totalRating += RatingManager.DEFAULT_RATING;
        }

        Gberry.log("RATING", "Total rating for " + uuid + " is " + totalRating);
        int globalRating = totalRating / (Ladder.getAllLadders().size());
        Gberry.log("RATING", "Global rating for " + uuid + " is " + globalRating);
        RatingManager.globalRatings.put(uuid, globalRating);
    }

    public static int getPlayerRating(UUID uuid, Ladder ladder) {
        ConcurrentHashMap<Ladder, Integer> ratings = RatingManager.rankedRatings.get(uuid);
        int rating = RatingManager.getRatingCommon(ratings, ladder);
        Gberry.log("RATING", "Retrieved rating " + rating + " for " + ladder.toString() + " for UUID " + uuid.toString());
        return rating;
    }

	public static int getPlayerGlobalRating(UUID uuid) {
		return RatingManager.globalRatings.get(uuid);
	}

    private static int getRatingCommon(ConcurrentHashMap<Ladder, Integer> ratings, Ladder ladder) {
        if (ratings == null) {
            return RatingManager.DEFAULT_RATING;
        }

        Integer rating = ratings.get(ladder);
        if (rating == null) {
            return RatingManager.DEFAULT_RATING;
        }

        return rating;
    }

    /**
     * Method must be called async
     */
    public static void setGroupRating(final UUID uuid, final Ladder ladder, final int rating, final double winOrLoss) {
        ConcurrentHashMap<Ladder, Integer> ratings = RatingManager.rankedRatings.get(uuid);

        // Update correct map (possible they logged off already)
        if (ratings != null) {
            Gberry.log("RATING", "Adding rating " + rating + " to map for " + ladder.toString() + " for leader " + uuid.toString());
            ratings.put(ladder, rating);

            RatingManager.handleGlobalRating(uuid);
        }

        RatingManager.setDBGroupRating(uuid, 0, RatingManager.globalRatings.get(uuid), winOrLoss);
        RatingManager.setDBGroupRating(uuid, ladder.getLadderId(), rating, winOrLoss);
    }

    public static int fetchAndStoreRating(UUID uuid, Ladder ladder) {
        String query = "SELECT * FROM mcsg_ladder_ratings_s1 WHERE uuid = ? AND lid = ?;";

        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        if (!RatingManager.rankedRatings.containsKey(uuid)) {
            Gberry.log("RATING", "User offline " + uuid + " adding to map");
            RatingManager.rankedRatings.put(uuid, new ConcurrentHashMap<Ladder, Integer>());
        }

        // First get their yolo queue ratings
        try {
            Gberry.log("RATING", "Getting connection");
            connection = Gberry.getConnection();
            Gberry.log("RATING", "Making query");
            ps = connection.prepareStatement(query);
            ps.setString(1, uuid.toString());
            ps.setInt(2, ladder.getLadderId());
            Gberry.log("RATING", "Executing query");
            rs = Gberry.executeQuery(connection, ps);

            if (rs.next()) {
                Gberry.log("RATING", "Found rating " + rs.getInt("rating") + " for " + uuid);
                RatingManager.rankedRatings.get(uuid).put(ladder, rs.getInt("rating"));
                return rs.getInt("rating");
            }
            Gberry.log("RATING", "No result found");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }

        Gberry.log("RATING", "Adding default rating for " + uuid);
        RatingManager.rankedRatings.get(uuid).put(ladder, RatingManager.DEFAULT_RATING);
	    return RatingManager.DEFAULT_RATING;
    }

    public static Map<UUID, Integer> fetchAndStoreRatings(Collection<UUID> uuids, Ladder ladder) {
        String query = "SELECT * FROM mcsg_ladder_ratings_s1 WHERE uuid IN (";

        for (UUID uuid : uuids) {
            query += "?, ";
        }

        query = query.substring(0, query.length() - 2) + ") AND lid = ?;";

        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        for (UUID uuid : uuids) {
            if (!RatingManager.rankedRatings.containsKey(uuid)) {
                Gberry.log("RATING", "User offline " + uuid + " adding to map");
                RatingManager.rankedRatings.put(uuid, new ConcurrentHashMap<Ladder, Integer>());
            }
        }

        Map<UUID, Integer> ratings = new HashMap<>();

        // First get their yolo queue ratings
        try {
            Gberry.log("RATING", "Getting connection");
            connection = Gberry.getConnection();
            Gberry.log("RATING", "Making query");
            ps = connection.prepareStatement(query);

            int i = 1;
            for (UUID uuid : uuids) {
                ps.setString(i++, uuid.toString());
            }

            ps.setInt(i, ladder.getLadderId());
            Gberry.log("RATING", "Executing query");
            rs = Gberry.executeQuery(connection, ps);

            while (rs.next()) {
                Gberry.log("RATING", "Found rating " + rs.getInt("rating") + " for " + rs.getString("uuid"));
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                RatingManager.rankedRatings.get(uuid).put(ladder, rs.getInt("rating"));
                ratings.put(uuid, rs.getInt("rating"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }

        for (UUID uuid : uuids) {
            if (!ratings.containsKey(uuid)) {
                Gberry.log("RATING", "Adding default rating for " + uuid);
                RatingManager.rankedRatings.get(uuid).put(ladder, RatingManager.DEFAULT_RATING);
                ratings.put(uuid, RatingManager.DEFAULT_RATING);
            }
        }

        return ratings;
    }

    public static int getUserRank(int rating, int ladderId) {
        String query = "SELECT COUNT(*) FROM mcsg_ladder_ratings_s1 WHERE lid = ? AND rating > ?;";

        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setInt(1, ladderId);
            ps.setInt(2, rating);
            rs = Gberry.executeQuery(connection, ps);

            if (rs.next()) {
                return rs.getInt(1) + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }

        return -1;
    }

    private static void getDBUserRatings(final UUID uuid) {
        String query = "SELECT * FROM mcsg_ladder_ratings_s1 WHERE uuid = ?;";

        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        // First get their yolo queue ratings
        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, uuid.toString());
            rs = Gberry.executeQuery(connection, ps);

            while (rs.next()) {
                if (rs.getInt("lid") == 0) {
                    continue;
                }

                Gberry.log("RATING", "Adding rating " + rs.getInt("rating") + " for ladder " +
                                             Ladder.getLadder(rs.getInt("lid"), Ladder.LadderType.FFA).toString() +
                                             " with UUID " + uuid.toString());


                RatingManager.rankedRatings.get(uuid).put(
                    Ladder.getLadder(rs.getInt("lid"), Ladder.LadderType.FFA),
                    rs.getInt("rating")
                );
            }

            RatingManager.handleGlobalRating(uuid);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
    }

    private static void setDBGroupRating(UUID uuid, int ladderId, int rating, double winOrLoss) {
        String sql = null, sql2 = null;
        sql = "UPDATE mcsg_ladder_ratings_s1 SET rating = ?, wins = wins + 1 WHERE lid = ? AND uuid = ?;\n";
        sql += "INSERT INTO mcsg_ladder_ratings_s1 (lid, uuid, rating, wins, losses) SELECT ?, ?, ?, ?, ? WHERE NOT EXISTS " +
                       "(SELECT 1 FROM mcsg_ladder_ratings_s1 WHERE lid = ? AND uuid = ?);";
        sql2 = "UPDATE mcsg_ladder_ratings_s1 SET rating = ?, losses = losses + 1 WHERE lid = ? AND uuid = ?;\n";
        sql2 += "INSERT INTO mcsg_ladder_ratings_s1 (lid, uuid, rating, wins, losses) SELECT ?, ?, ?, ?, ? WHERE NOT EXISTS " +
                        "(SELECT 1 FROM mcsg_ladder_ratings_s1 WHERE lid = ? AND uuid = ?);";

        Connection connection = null;
        PreparedStatement ps = null;

        try {
            // Update player rating or create a new one if newbie
            connection = Gberry.getConnection();
            ps = null;
            if (winOrLoss == 1) {
                ps = connection.prepareStatement(sql);
            } else {
                ps = connection.prepareStatement(sql2);
            }

            ps.setInt(1, rating);
            ps.setInt(2, ladderId);

            int index = 3;
            Gberry.log("RATING", "Setting rating " + rating + " for " + ladderId + " for UUID " + uuid.toString());
            ps.setString(index++, uuid.toString());

            ps.setInt(index++, ladderId);

            ps.setString(index++, uuid.toString());

            ps.setInt(index++, rating);
            if (winOrLoss == 1) {
                ps.setInt(index++, 1);
                ps.setInt(index++, 0);
            } else {
                ps.setInt(index++, 0);
                ps.setInt(index++, 1);
            }

            ps.setInt(index++, ladderId);

            ps.setString(index, uuid.toString());

            Gberry.executeUpdate(connection, ps);

            Gberry.log("RATING", "Rating committed");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
    }

}
