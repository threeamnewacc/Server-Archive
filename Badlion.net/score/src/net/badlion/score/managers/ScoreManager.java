package net.badlion.score.managers;

import net.badlion.gberry.Gberry;
import net.badlion.score.ScorePlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Score;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ScoreManager {

    public static enum SCORE_REASON {}

    private static Map<Integer, GroupScore> groupScores = new ConcurrentHashMap<>();

    public static void initialize() {
        ScoreManager.loadAllScoreCacheInformation();
    }

    /**
     * Load all of the information about all groups and all of the contributions from users
     * into a cache for use on the plugin side
     */
    private static void loadAllScoreCacheInformation() {
        String query = "SELECT * FROM " + ScorePlugin.getInstance().getTag() + "_score;";

        Connection connection = null;
        Connection connection2 = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = Gberry.getConnection();
            connection2 = Gberry.getConnection();
            ps = connection.prepareStatement(query);

            rs = Gberry.executeQuery(connection, ps);
            while (rs.next()) {
                int groupId = rs.getInt("group_id");
                GroupScore groupScore = new GroupScore(groupId, rs.getInt("score"));
                ScoreManager.groupScores.put(groupId, groupScore);

                PreparedStatement ps2 = null;
                ResultSet rs2 = null;

                try {
                    query = "SELECT * FROM " + ScorePlugin.getInstance().getTag() + "_score_users WHERE group_id = ?;";
                    ps2 = connection.prepareStatement(query);
                    ps2.setInt(1, groupId);

                    rs2 = Gberry.executeQuery(connection, ps);

                    while (rs2.next()) {
                        groupScore.addScore(rs.getString("uuid"), rs.getInt("score"));
                    }
                } finally {
                    if (rs2 != null) { try { rs2.close(); } catch (SQLException e) { e.printStackTrace(); } }
                    if (ps2 != null) { try { ps2.close(); } catch (SQLException e) { e.printStackTrace(); } }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (connection2 != null) { try { connection2.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
    }

    /**
     * Add score to a group from a particular player for a particular group
     *
     * @param groupId group id
     * @param uuid uuid in string format
     * @param score score
     * @param reason SCORE_REASON
     */
    public static void addScoreToFactionFromPlayer(final int groupId, final String uuid, final int score, final SCORE_REASON reason) {
        ScoreManager.groupScores.get(groupId).addScore(uuid, score);

        ScorePlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(ScorePlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                String query = "UPDATE " + ScorePlugin.getInstance().getTag() + "_score_users SET score = score + ? WHERE group_id = ? AND uuid = ?;\n";
                query += "INSERT INTO " + ScorePlugin.getInstance().getTag() + "_score_users (group_id, uuid, score) SELECT ?, ?, ? "
                                 + "WHERE NOT EXISTS (SELECT 1 FROM " + ScorePlugin.getInstance().getTag() + "_score_users WHERE group_id = ? AND uuid = ?);";

                Connection connection = null;
                PreparedStatement ps = null;

                try {
                    connection = Gberry.getConnection();
                    ps = connection.prepareStatement(query);
                    ps.setInt(1, groupId);
                    ps.setString(2, uuid);
                    ps.setInt(3, groupId);
                    ps.setString(4, uuid);
                    ps.setInt(5, score);
                    ps.setInt(6, groupId);
                    ps.setString(7, uuid);

                    Gberry.executeUpdate(connection, ps);

                    if (ps != null) {
                        ps.close();
                    }

                    query = "INSERT INTO " + ScorePlugin.getInstance().getTag() + "_score_user_history (uuid, group_id, score, achieved_time, reason, active) VALUES (?, ?, ?, ?, ?, ?);";
                    ps = connection.prepareStatement(query);
                    ps.setString(1, uuid);
                    ps.setInt(2, groupId);
                    ps.setInt(3, score);
                    ps.setTimestamp(4, new Timestamp(new DateTime(DateTimeZone.UTC).getMillis()));
                    ps.setInt(5, reason.ordinal());
                    ps.setString(6, "a");

                    Gberry.executeUpdate(connection, ps);

                    if (ps != null) {
                        ps.close();
                    }

                    query = "UPDATE " + ScorePlugin.getInstance().getTag() + "_scores SET score = score + ? WHERE group_id = ?";
                    ps = connection.prepareStatement(query);
                    ps.setInt(1, score);
                    ps.setInt(2, groupId);

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
    
    public static void rollback(final Player player, final int numOfMinutesToRollBack, final int groupId) {
        ScorePlugin.getInstance().getServer().getScheduler().runTaskAsynchronously(ScorePlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                String query = "SELECT * FROM " + ScorePlugin.getInstance().getTag() + "_score_user_history WHERE group_id = ? AND achieved_time >= ? AND active = 'a';\n";

                Connection connection = null;
                PreparedStatement ps = null;
                ResultSet rs = null;

                try {
                    // Findall of the information for the group id that we want to roll back
                    connection = Gberry.getConnection();
                    ps = connection.prepareStatement(query);
                    ps.setInt(1, groupId);

                    DateTime dateTime = new DateTime(DateTimeZone.UTC);
                    dateTime = dateTime.minusMinutes(numOfMinutesToRollBack);

                    ps.setTimestamp(2, new Timestamp(dateTime.getMillis()));
                    rs = Gberry.executeQuery(connection, ps);

                    int totalScoreToRemove = 0;
                    Map<String, Integer> userScoresToRemove = new HashMap<>();
                    List<Integer> idsToSetToInactive = new ArrayList<>();
                    while (rs.next()) {
                        idsToSetToInactive.add(rs.getInt(ScorePlugin.getInstance().getTag() + "_score_user_history_id"));
                        String uuid = rs.getString("uuid");
                        int score = rs.getInt("score");

                        totalScoreToRemove += score;
                        if (!userScoresToRemove.containsKey(uuid)) {
                            userScoresToRemove.put(uuid, 0);
                        }

                        userScoresToRemove.put(uuid, userScoresToRemove.get(uuid) + score);
                    }

                    PreparedStatement ps2 = null;
                    ResultSet rs2 = null;

                    int rollbackId = -1;

                    // Make roll back entry
                    try {
                        query = "INSERT INTO " + ScorePlugin.getInstance().getTag() + "_score_rollback (uuid, group_id, num_of_minutes_reverted, rollback_time) VALUES (?, ?, ?, ?);";
                        ps2 = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
                        ps2.setString(1, player.getUniqueId().toString());
                        ps2.setInt(2, groupId);
                        ps2.setInt(3, numOfMinutesToRollBack);
                        ps2.setTimestamp(4, new Timestamp(new DateTime(DateTimeZone.UTC).getMillis()));

                        Gberry.executeUpdate(connection, ps);
                        rs2 = ps.getGeneratedKeys();
                        if (rs.next()) {
                            rollbackId = rs.getInt(1);
                        }

                    } finally {
                        if (rs2 != null) { try { rs2.close(); } catch (SQLException e) { e.printStackTrace(); } }
                        if (ps2 != null) { try { ps2.close(); } catch (SQLException e) { e.printStackTrace(); } }
                    }

                    if (rollbackId == -1) {
                        player.sendMessage(ChatColor.RED + "Unable to create rollback ID");
                        return;
                    }

                    // Go through each record, set to inactive and insert an entry for un-rollback
                    if (idsToSetToInactive.size() > 0) {
                        for (Integer id : idsToSetToInactive) {
                            try {
                                query = "UPDATE " + ScorePlugin.getInstance().getTag() + "_score_user_history_id SET active = 'i' WHERE " + ScorePlugin.getInstance().getTag() + "_score_user_history_id = ?;";
                                ps2 = connection.prepareStatement(query);
                                ps2.setInt(1, id);

                                Gberry.executeUpdate(connection, ps);
                            } finally {
                                if (ps2 != null) { try { ps2.close(); } catch (SQLException e) { e.printStackTrace(); } }
                            }

                            try {
                                query = "INSERT INTO " + ScorePlugin.getInstance().getTag() + "_score_rollbacks (" + ScorePlugin.getInstance().getTag() + "_score_rollback_id, " + ScorePlugin.getInstance().getTag() + "_score_user_history_id) VALUES (?, ?);";
                                ps2 = connection.prepareStatement(query);
                                ps2.setInt(1, rollbackId);
                                ps2.setInt(2, id);

                                Gberry.executeUpdate(connection, ps);
                            } finally {
                                if (ps2 != null) { try { ps2.close(); } catch (SQLException e) { e.printStackTrace(); } }
                            }
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + "Nothing to roll back.");
                        return;
                    }

                    // Fix each user's contribution to the group score
                    for (String uuid : userScoresToRemove.keySet()) {
                        try {
                            query = "UPDATE " + ScorePlugin.getInstance().getTag() + "_score_users SET score = score - ? WHERE uuid = ?;";
                            ps2 = connection.prepareStatement(query);
                            ps2.setInt(1, userScoresToRemove.get(uuid));
                            ps2.setString(2, uuid);

                            Gberry.executeUpdate(connection, ps);
                        } finally {
                            if (ps2 != null) { try { ps2.close(); } catch (SQLException e) { e.printStackTrace(); } }
                        }
                    }

                    // Lastly go and fix the group's score
                    try {
                        query = "UPDATE " + ScorePlugin.getInstance().getTag() + "_scores SET score = score - ? WHERE group_id = ?;";
                        ps2 = connection.prepareStatement(query);
                        ps2.setInt(1, totalScoreToRemove);
                        ps2.setInt(2, groupId);

                        Gberry.executeUpdate(connection, ps);
                    } finally {
                        if (ps2 != null) { try { ps2.close(); } catch (SQLException e) { e.printStackTrace(); } }
                    }
                } catch (SQLException e) {
                    player.sendMessage(ChatColor.RED + "Something broke.");
                    e.printStackTrace();
                } finally {
                    if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
                    if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
                    if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
                }
            }
        });
    }

    public static void unrollback(final Player player, final int rollbackId) {

    }

    public static class GroupScore {

        private int groupId;
        private int score;
        private ConcurrentHashMap<String, Integer> userScore = new ConcurrentHashMap<>();

        public GroupScore(int groupId, int score) {
            this.groupId = groupId;
            this.score = score;
        }

        public int getGroupId() {
            return groupId;
        }

        public int getScore() {
            return score;
        }

        public void addScore(String uuid, int score) {
            this.score += score;

            if (!this.userScore.containsKey(uuid)) {
                this.userScore.put(uuid, 0);
            }

            this.userScore.put(uuid, this.userScore.get(uuid) + score);
        }

    }

}
