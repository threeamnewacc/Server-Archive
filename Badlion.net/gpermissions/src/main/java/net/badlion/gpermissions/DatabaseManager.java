package net.badlion.gpermissions;

import net.badlion.gberry.Gberry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class DatabaseManager {

	private GPermissions plugin;

	public DatabaseManager(GPermissions plugin) {
		this.plugin = plugin;
	}

	public static GUser getUser(String uuid) {
		String query = "SELECT * FROM " + GPermissions.plugin.getTablePrefix() + "_gperms_users WHERE uuid = ?;";

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, uuid);
			rs = Gberry.executeQuery(connection, ps);

			if (rs.next()) {
				return new GUser(rs.getString("uuid"), rs.getString("group"), rs.getString("prefix"), null);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(rs, ps, connection);
		}

		return null;
	}

	public static void updateUser(String uuid, String groupName, String prefix) {
		String query = "UPDATE " + GPermissions.plugin.getTablePrefix() + "_gperms_users SET \"group\" = ?, prefix = ? WHERE uuid = ?;\n";
		query += "INSERT INTO " + GPermissions.plugin.getTablePrefix() + "_gperms_users (uuid, \"group\", prefix) SELECT ?, ?, ? WHERE NOT EXISTS " +
						 "(SELECT 1 FROM " + GPermissions.plugin.getTablePrefix() + "_gperms_users WHERE uuid = ?);";

		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);

			ps.setString(1, groupName);
			ps.setString(2, prefix);
			ps.setString(3, uuid);
			ps.setString(4, uuid);
			ps.setString(5, groupName);
			ps.setString(6, prefix);
			ps.setString(7, uuid);

			Gberry.executeUpdate(connection, ps);

			// Update the player's chat prefix in MCP
			Gberry.sendChatPlayerPrefixChange(uuid);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(ps, connection);
		}
	}

	public static Group getGroup(String groupName) {
		String query = "SELECT * FROM " + GPermissions.plugin.getTablePrefix() + "_gperms_groups WHERE group_name = ?;";

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, groupName);
			rs = Gberry.executeQuery(connection, ps);

			if (rs.next()) {
				return new Group(groupName, rs.getString("prefix"), new HashMap<String, Boolean>());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(rs, ps, connection);
		}

		return null;
	}

	public static void insertUpdateGroup(String groupName, String prefix) {
		String query = "UPDATE " + GPermissions.plugin.getTablePrefix() + "_gperms_groups SET prefix = ? WHERE group_name = ?;\n";
		query += "INSERT INTO " + GPermissions.plugin.getTablePrefix() + "_gperms_groups (group_name, prefix) SELECT ?, ? WHERE NOT EXISTS " +
						 "(SELECT 1 FROM " + GPermissions.plugin.getTablePrefix() + "_gperms_groups WHERE group_name = ?);";

		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, prefix);
			ps.setString(2, groupName);
			ps.setString(3, groupName);
			ps.setString(4, prefix);
			ps.setString(5, groupName);

			Gberry.executeUpdate(connection, ps);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(ps, connection);
		}
	}

	public static void insertGroupPermissions(String groupName, String permission) {
		String query = "INSERT INTO " + GPermissions.plugin.getTablePrefix() + "_gperms_group_permissions (group_name, permission_name) VALUES (?, ?);";

		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, groupName);
			ps.setString(2, permission);

			Gberry.executeUpdate(connection, ps);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(ps, connection);
		}
	}

	public static void deleteGroupPermissions(String groupName, String permission) {
		String query = "DELETE FROM " + GPermissions.plugin.getTablePrefix() + "_gperms_group_permissions WHERE group_name = ? AND permission_name = ?;";

		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, groupName);
			ps.setString(2, permission);

			Gberry.executeUpdate(connection, ps);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(ps, connection);
		}
	}

	public static void insertUserPermissions(String uuid, String permission) {
		String query = "INSERT INTO " + GPermissions.plugin.getTablePrefix() + "_gperms_user_permissions (uuid, permission_name) VALUES (?, ?);";

		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, uuid);
			ps.setString(2, permission);

			Gberry.executeUpdate(connection, ps);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(ps, connection);
		}
	}

	public static void deleteUserPermissions(String uuid, String permission) {
		String query = "DELETE FROM " + GPermissions.plugin.getTablePrefix() + "_gperms_user_permissions WHERE uuid = ? AND permission_name = ?;";

		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, uuid);
			ps.setString(2, permission);

			Gberry.executeUpdate(connection, ps);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(ps, connection);
		}
	}

	public static HashSet<Group> getAllGroups() {
		String query = "SELECT * FROM " + GPermissions.plugin.getTablePrefix() + "_gperms_groups;";

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		HashSet<Group> groups = new HashSet<Group>();

		try {
			connection = Gberry.getUnsafeConnection();
			ps = connection.prepareStatement(query);
			rs = Gberry.executeQuery(connection, ps);

			while (rs.next()) {
				Group group = new Group(rs.getString("group_name"), rs.getString("prefix"), null);
				groups.add(group);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(rs, ps, connection);
		}

		return groups;
	}

	public static Map<String, Set<String>> getAllGroupPermissions() {
		String query = "SELECT * FROM " + GPermissions.plugin.getTablePrefix() + "_gperms_group_permissions;";

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Map<String, Set<String>> permissions = new HashMap<>();

		try {
			connection = Gberry.getUnsafeConnection();
			ps = connection.prepareStatement(query);
			rs = Gberry.executeQuery(connection, ps);

			while (rs.next()) {
				if (!permissions.containsKey(rs.getString("group_name"))) {
					permissions.put(rs.getString("group_name"), new HashSet<String>());
				}
				permissions.get(rs.getString("group_name")).add(rs.getString("permission_name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(rs, ps, connection);
		}

		return permissions;
	}

	public static HashSet<String> getGroupPermissions(String groupName) {
		String query = "SELECT * FROM " + GPermissions.plugin.getTablePrefix() + "_gperms_group_permissions WHERE group_name = ?;";

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		HashSet<String> permissions = new HashSet<String>();

		try {
			connection = Gberry.getUnsafeConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, groupName);
			rs = Gberry.executeQuery(connection, ps);

			while (rs.next()) {
				permissions.add(rs.getString("permission_name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(rs, ps, connection);
		}

		return permissions;
	}

	public static void addRemoveSubGroup(UUID uuid, String groupName, String type) {
		String query = null;

		if (type.equals("addgroup")) {
			query = "INSERT INTO " + GPermissions.plugin.getTablePrefix() + "_gperms_user_subgroups (uuid, group_name) VALUES (?, ?);";
		} else {
			query = "DELETE FROM " + GPermissions.plugin.getTablePrefix() + "_gperms_user_subgroups WHERE uuid = ? AND group_name = ?;";
		}

		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);

			ps.setString(1, uuid.toString());
			ps.setString(2, groupName);

			Gberry.executeUpdate(connection, ps);

			// Update the player's chat prefix in MCP
			Gberry.sendChatPlayerPrefixChange(uuid);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(ps, connection);
		}
	}

	public static HashSet<String> getUserPermissions(String uuid) {
		String query = "SELECT * FROM " + GPermissions.plugin.getTablePrefix() + "_gperms_user_permissions WHERE uuid = ?;";

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		HashSet<String> permissions = new HashSet<String>();

		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, uuid);
			rs = Gberry.executeQuery(connection, ps);

			while (rs.next()) {
				permissions.add(rs.getString("permission_name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(rs, ps, connection);
		}

		return permissions;
	}

	public static HashSet<String> getUserSubgroups(String uuid) {
		String query = "SELECT * FROM " + GPermissions.plugin.getTablePrefix() + "_gperms_user_subgroups WHERE uuid = ?;";

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		HashSet<String> subgroups = new HashSet<>();

		try {
			connection = Gberry.getUnsafeConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, uuid);
			rs = Gberry.executeQuery(connection, ps);

			while (rs.next()) {
				subgroups.add(rs.getString("group_name"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(rs, ps, connection);
		}

		return subgroups;
	}

}
