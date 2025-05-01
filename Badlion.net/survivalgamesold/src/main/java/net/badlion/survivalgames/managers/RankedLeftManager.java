package net.badlion.survivalgames.managers;

import net.badlion.gberry.Gberry;
import org.bukkit.event.Listener;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class RankedLeftManager implements Listener {

	public static int DEFAULT_MAX_NUM_OF_RANKED_MATCHES_PER_DAY = 5;

	public static int getNumOfPlayerRankedMatchesToday(UUID uuid) {
		int num = 0;

		Connection connection = null;
		ResultSet rs = null;
		PreparedStatement ps = null;

		try {
			connection = Gberry.getConnection();
			String query = "SELECT * FROM sg_user_num_of_ranked WHERE uuid = ? AND day = ?;";
			ps = connection.prepareStatement(query);
			ps.setString(1, uuid.toString());
			java.util.Date today = new java.util.Date();
			ps.setDate(2, new java.sql.Date(today.getTime()));
			rs = Gberry.executeQuery(connection, ps);

			if (rs.next()) {
				return rs.getInt("num_of_matches");
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}

		return num;
	}

}
