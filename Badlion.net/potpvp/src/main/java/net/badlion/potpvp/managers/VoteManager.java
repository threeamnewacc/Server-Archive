package net.badlion.potpvp.managers;

import net.badlion.gberry.Gberry;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.sql.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class VoteManager {

    private static Map<UUID, Boolean> hasVoted = new ConcurrentHashMap<>();

    public static void initialize() {
        DateTime date = new DateTime(DateTimeZone.UTC);
        date = date.minusSeconds(86400); // 1 day

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT DISTINCT uuid FROM potion_vote_records WHERE vote_date >= ?;";

        try {
            connection = Gberry.getUnsafeConnection();
            ps = connection.prepareStatement(query);
            ps.setTimestamp(1, new Timestamp(date.getMillis()));

            rs = Gberry.executeQuery(connection, ps);

            while (rs.next()) {
                VoteManager.hasVoted.put(UUID.fromString(rs.getString("uuid")), true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
    }

    public static boolean hasVoted(UUID uuid) {
        return VoteManager.hasVoted.containsKey(uuid);
    }

    public static void addVoted(UUID uuid) {
        VoteManager.hasVoted.put(uuid, true);
    }

}
