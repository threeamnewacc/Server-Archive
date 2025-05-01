package net.badlion.gfactions.managers;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import net.badlion.gfactions.GFactions;
import net.badlion.gberry.Gberry;

import java.sql.*;

public class FactionManager {

	private GFactions plugin;

	public FactionManager(GFactions plugin) {
		this.plugin = plugin;
	}

	public static void insertOrUpdateNewFaction(Faction faction) {
		//String query = "INSERT INTO faction_faction (faction_id, faction_tag, faction_description, money, leader_uuid, create_time) VALUES " +
		//					   "(?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE faction_tag = ?, faction_description = ?, leader_uuid = ?;";
		String query = "UPDATE " + GFactions.PREFIX + "_faction_faction SET faction_tag = ?, faction_description = ?, leader_uuid = ? WHERE faction_id = ?;\n";
		query += "INSERT INTO " + GFactions.PREFIX + "_faction_faction (faction_id, faction_tag, faction_description, money, leader_uuid, create_time, " +
				"kills, deaths, kdr, towers, koths, manhunts, bloodbowls, dragons) " +
				"SELECT ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? WHERE NOT EXISTS " +
						 "(SELECT 1 FROM " + GFactions.PREFIX + "_faction_faction WHERE faction_id = ?);";

		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);

			ps.setString(1, faction.getTag());
			ps.setString(2, faction.getDescription());
			ps.setString(3, faction.getFPlayerLeader().getId());
			ps.setInt(4, Integer.parseInt(faction.getId()));
			ps.setInt(5, Integer.parseInt(faction.getId()));
			ps.setString(6, faction.getTag());
			ps.setString(7, faction.getDescription());
			ps.setDouble(8, 0);
			ps.setString(9, faction.getFPlayerLeader().getId());
			ps.setTimestamp(10, new Timestamp(new java.util.Date().getTime()));
			ps.setInt(11, 0);
			ps.setInt(12, 0);
			ps.setDouble(13, 0);
			ps.setInt(14, 0);
			ps.setInt(15, 0);
			ps.setInt(16, 0);
			ps.setInt(17, 0);
			ps.setInt(18, 0);
			ps.setInt(19, Integer.parseInt(faction.getId()));


			Gberry.executeUpdate(connection, ps);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}
	}

	public static void insertOrUpdatePlayerIntoFaction(Faction faction, FPlayer fPlayer) {
		//String query = "INSERT INTO faction_faction_player (uuid, faction_id, role) VALUES (?, ?, ?) " +
		//					   "ON DUPLICATE KEY UPDATE role = ?;";
		String query = "UPDATE " + GFactions.PREFIX + "_faction_faction_player SET role = ? WHERE uuid = ?;\n";
		query += "INSERT INTO " + GFactions.PREFIX + "_faction_faction_player (uuid, faction_id, role) SELECT ?, ?, ? WHERE NOT EXISTS " +
						 "(SELECT 1 FROM " + GFactions.PREFIX + "_faction_faction_player WHERE uuid = ?);";

		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, fPlayer.getRole().name());
			ps.setString(2, fPlayer.getId());
			ps.setString(3, fPlayer.getId());
			ps.setInt(4, Integer.parseInt(faction.getId()));
			ps.setString(5, fPlayer.getRole().name());
			ps.setString(6, fPlayer.getId());

			Gberry.executeUpdate(connection, ps);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}
	}

	public static void deletePlayerFromFaction(Faction faction, FPlayer fPlayer) {
		String query = "DELETE FROM " + GFactions.PREFIX + "_faction_faction_player WHERE uuid = ? AND faction_id = ?;";

		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, fPlayer.getId());
			ps.setInt(2, Integer.parseInt(faction.getId()));

			Gberry.executeUpdate(connection, ps);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}
	}

	public static void insertOrUpdateCurrentRelationship(Faction fromFaction, Faction toFaction) {
		String query = "INSERT INTO " + GFactions.PREFIX + "_faction_faction_relation (faction_from, faction_to, relationship, relation_change_time) VALUES (?, ?, ?, ?);";

		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setInt(1, Integer.parseInt(fromFaction.getId()));
			ps.setInt(2,  Integer.parseInt(toFaction.getId()));
			ps.setString(3, fromFaction.getRelationTo(toFaction).name());
			ps.setTimestamp(4, new Timestamp(new java.util.Date().getTime()));

			Gberry.executeUpdate(connection, ps);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}
	}

	public static void getAndDeleteCurrentRelationship(Faction fromFaction, Faction toFaction) {
		String query = "SELECT * FROM " + GFactions.PREFIX + "_faction_faction_relation WHERE faction_from = ? AND faction_to = ?;";

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setInt(1, Integer.parseInt(fromFaction.getId()));
			ps.setInt(2, Integer.parseInt(toFaction.getId()));

			rs = Gberry.executeQuery(connection, ps);
			if (rs.next()) {
				String relationship = rs.getString("relationship");
				Timestamp timestamp = rs.getTimestamp("relation_change_time");

				query = "DELETE FROM " + GFactions.PREFIX + "_faction_faction_relation WHERE faction_from = ? AND faction_to = ?;";
				PreparedStatement ps2 = connection.prepareStatement(query);
				ps2.setInt(1, Integer.parseInt(fromFaction.getId()));
				ps2.setInt(2, Integer.parseInt(toFaction.getId()));

				Gberry.executeUpdate(connection, ps2);
				ps2.close();

				// Insert into history record
				FactionManager.insertRelationshipIntoHistory(fromFaction.getId(), toFaction.getId(), relationship, timestamp);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}
	}

	public static void insertRelationshipIntoHistory(String fromFaction, String toFaction, String relationship, Timestamp timeChanged) {
		String query = "INSERT INTO " + GFactions.PREFIX + "_faction_faction_relation_history (faction_from, faction_to, relationship, relation_change_time) VALUES (?, ?, ?, ?);";

		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setInt(1,  Integer.parseInt(fromFaction));
			ps.setInt(2,  Integer.parseInt(toFaction));
			ps.setString(3, relationship);
			ps.setTimestamp(4, timeChanged);

			Gberry.executeUpdate(connection, ps);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}
	}

	public static void insertIntoFactionHistory(Faction faction, String type, String uuid, Timestamp historyTime) {
		String query = "INSERT INTO " + GFactions.PREFIX + "_faction_faction_member_history (faction_id, type, uuid, history_time) VALUES (?, ?, ?, ?);";

		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setInt(1, Integer.parseInt(faction.getId()));
			ps.setString(2, type);
			ps.setString(3, uuid);
			ps.setTimestamp(4, historyTime);

			Gberry.executeUpdate(connection, ps);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}
	}

	public static void deleteFactionHistory(Faction faction) {
		String query = "DELETE FROM " + GFactions.PREFIX + "_faction_faction_member_history WHERE faction_id = ?;";

		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setInt(1, Integer.parseInt(faction.getId()));

			Gberry.executeUpdate(connection, ps);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}
	}

	public static void deleteFaction(Faction faction) {
		String query = "DELETE FROM " + GFactions.PREFIX + "_faction_faction WHERE faction_id = ?;";

		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setInt(1, Integer.parseInt(faction.getId()));

			Gberry.executeUpdate(connection, ps);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}
	}

	public static void deleteFactionRelationHistory(Faction faction) {
		String query = "DELETE FROM " + GFactions.PREFIX + "_faction_faction_relation_history WHERE faction_from = ? OR faction_to = ?;";

		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setInt(1, Integer.parseInt(faction.getId()));
			ps.setInt(2, Integer.parseInt(faction.getId()));

			Gberry.executeUpdate(connection, ps);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}
	}

	public static void deleteFactionPlayers(Faction faction) {
		String query = "DELETE FROM " + GFactions.PREFIX + "_faction_faction_player WHERE faction_id = ?;";

		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setInt(1, Integer.parseInt(faction.getId()));

			Gberry.executeUpdate(connection, ps);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}
	}

	public static void deleteFactionRelation(Faction faction) {
		String query = "DELETE FROM " + GFactions.PREFIX + "_faction_faction_relation WHERE faction_from = ? OR faction_to = ?;";

		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setInt(1, Integer.parseInt(faction.getId()));
			ps.setInt(2, Integer.parseInt(faction.getId()));

			Gberry.executeUpdate(connection, ps);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}
	}

    public static void addKillDeathToFaction(String stat, Faction faction) {
        String query = "SELECT * FROM " + GFactions.PREFIX + "_faction_faction WHERE faction_id = ?;";

        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setInt(1, Integer.parseInt(faction.getId()));

            rs = Gberry.executeQuery(connection, ps);
            if (rs.next()) {
                int kills = rs.getInt("kills");
                int deaths = rs.getInt("deaths");

                ps.close();

                if (stat.equals("kills")) {
                    kills += 1;
                } else {
                    deaths += 1;
                }

                double kdr = kills;
                if (deaths != 0) {
                    kdr /= deaths;
                }

                query = "UPDATE " + GFactions.PREFIX + "_faction_faction SET " + stat + " = ?, kdr = ? WHERE faction_id = ?;";
                ps = connection.prepareStatement(query);
                ps.setInt(1, (stat.equals("kills") ? kills : deaths));
                ps.setDouble(2, kdr);
                ps.setInt(3, Integer.parseInt(faction.getId()));

                Gberry.executeUpdate(connection, ps);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
    }

    public static void addStatToFaction(String stat, Faction faction) {
        String query = "UPDATE " + GFactions.PREFIX + "_faction_faction SET " + stat + " = " + stat + " + 1 WHERE faction_id = ?;";

        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setInt(1, Integer.parseInt(faction.getId()));

            Gberry.executeUpdate(connection, ps);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
    }

}