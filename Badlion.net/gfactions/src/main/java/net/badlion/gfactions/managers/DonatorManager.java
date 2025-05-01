package net.badlion.gfactions.managers;

import net.badlion.gfactions.GFactions;
import net.badlion.gberry.Gberry;
import org.joda.time.DateTime;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class DonatorManager {
	
	private GFactions plugin;
	private Map<String, Double> boostMap;
	
	public DonatorManager(GFactions plugin) {
		this.plugin = plugin;
		this.boostMap = new HashMap<String, Double>();
        //this.initializeXPBoosts();
	}

    public boolean hasReservedSlot(String uuid) {
        String query = "SELECT * FROM " + GFactions.PREFIX + "_reserved_slot WHERE uuid = ?;";

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, uuid);
            rs = Gberry.executeQuery(connection, ps);

            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }

        return false;
    }

    public void addReservedSlot(String uuid) {
        String query = "INSERT INTO " + GFactions.PREFIX + "_reserved_slot (uuid) VALUES (?);";

        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, uuid);

            Gberry.executeUpdate(connection, ps);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
    }

    public long getLastKitLoadTime(String kit, String uuid) {
        String query = "SELECT * FROM " + GFactions.PREFIX + "_kit_load_times WHERE uuid = ? AND kit = ?;";

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, uuid);
            ps.setString(2, kit);
            rs = Gberry.executeQuery(connection, ps);

            if (rs.next()) {
                return rs.getInt("last_load_time");
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

    public void updateKitLoadTime(String kit, String uuid) {
        //String query = "INSERT INTO faction_kit_load_times (uuid, kit, last_load_time) VALUES (?, ?, ?) " +
        //        "ON DUPLICATE KEY UPDATE last_load_time = ?;";
		String query = "UPDATE " + GFactions.PREFIX + "_kit_load_times SET last_load_time = ? WHERE uuid = ? AND kit = ?;\n";
		query += "INSERT INTO " + GFactions.PREFIX + "_kit_load_times (uuid, kit, last_load_time) SELECT ?, ?, ? WHERE NOT EXISTS " +
						 "(SELECT 1 FROM " + GFactions.PREFIX + "_kit_load_times WHERE uuid = ? AND kit = ?);";


        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);

			int lastUsedTime = (int) (System.currentTimeMillis() / 1000);

			ps.setInt(1, lastUsedTime);
			ps.setString(2, uuid);
			ps.setString(3, kit);
			ps.setString(4, uuid);
            ps.setString(5, kit);
            ps.setInt(6, lastUsedTime);
			ps.setString(7, uuid);
			ps.setString(8, kit);

            Gberry.executeUpdate(connection, ps);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
    }

    public void initializeXPBoosts() {
        String query = "SELECT * FROM " + GFactions.PREFIX + "_xp_boost WHERE expiration_date > ?;";

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setTimestamp(1, new Timestamp((new DateTime().toDate().getTime())));
            rs = Gberry.executeQuery(connection, ps);

            while (rs.next()) {
                this.boostMap.put(rs.getString("uuid"), rs.getDouble("multiplier"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }

    }

    public void addXPBoost(String uuid, double mult, int days) {
        /*String query = "INSERT INTO faction_xp_boost (uuid, multiplier, expiration_date) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE multiplier = ?, expiration_date = ?;";

        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, uuid);
            ps.setDouble(2, mult);

            DateTime datetime = new DateTime();
            datetime.plusDays(days);
            ps.setTimestamp(3, new Timestamp(datetime.toDate().getTime()));

            ps.setDouble(4, mult);
            ps.setTimestamp(5, new Timestamp(datetime.toDate().getTime()));

            Gberry.executeUpdate(connection, ps);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }*/
    }

    public void removeXPBoost(String uuid) {
        String query = "DELETE FROM " + GFactions.PREFIX + "_xp_boost WHERE uuid = ?";

        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, uuid);
            Gberry.executeUpdate(connection, ps);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
    }

	public GFactions getPlugin() {
		return plugin;
	}

	public void setPlugin(GFactions plugin) {
		this.plugin = plugin;
	}

	public Map<String, Double> getBoostMap() {
		return boostMap;
	}

	public void setBoostMap(Map<String, Double> boostMap) {
		this.boostMap = boostMap;
	}
	
	

}
