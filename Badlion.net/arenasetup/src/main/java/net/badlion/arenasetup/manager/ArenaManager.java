package net.badlion.arenasetup.manager;

import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenasetup.ArenaSetup;
import net.badlion.gberry.Gberry;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ArenaManager {


	public static void listAllArenas(Player player) {
		new BukkitRunnable() {
			@Override
			public void run() {
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;

				try {
					String query = "SELECT * FROM build_arenas_s14;";

					connection = Gberry.getConnection();
					ps = connection.prepareStatement(query);

					rs = Gberry.executeQuery(connection, ps);

					int i = 0;
					Map<Integer, Integer> arenaTypeCounts = new HashMap<>();
					while (rs.next()) {
						if (player != null) {
							String arenaName = rs.getString("arena_name");
							String[] typeArray = rs.getString("types").split(",");
							StringBuilder stringBuilder = new StringBuilder();
							for (String type : typeArray) {
								Integer rulesetId = Integer.valueOf(type);
								if (arenaTypeCounts.containsKey(rulesetId)) {
									arenaTypeCounts.put(rulesetId, arenaTypeCounts.get(rulesetId) + 1);
								} else {
									arenaTypeCounts.put(rulesetId, 1);
								}
								stringBuilder.append(ChatColor.YELLOW + KitRuleSet.getKitRuleSet(rulesetId).getName() + ChatColor.GRAY + ", ");
							}
							String types = stringBuilder.substring(0, stringBuilder.length() - 1);

							player.sendMessage(ChatColor.GOLD + "" + ++i + ". " + ChatColor.GREEN + arenaName + ": " + types);
						}
					}
					if (!arenaTypeCounts.isEmpty()) {
						player.sendMessage(ChatColor.AQUA + ChatColor.STRIKETHROUGH.toString() + "----------------------");
						player.sendMessage(ChatColor.GOLD + "Arena Counts For Each Kit:");
						player.sendMessage(ChatColor.AQUA + ChatColor.STRIKETHROUGH.toString() + "----------------------");
						for (Map.Entry<Integer, Integer> entry : arenaTypeCounts.entrySet()) {
							player.sendMessage(ChatColor.GOLD + KitRuleSet.getKitRuleSet(entry.getKey()).getName() + ": " + ArenaManager.colorizeFromInteger(entry.getValue()));
						}
					}

					if (player != null && i == 0) {
						player.sendMessage(ChatColor.RED + "No arenas found! :(");
					}

				} catch (SQLException ex) {
					ex.printStackTrace();
				} finally {
					Gberry.closeComponents(rs, ps, connection);
				}
			}
		}.runTaskAsynchronously(ArenaSetup.getInstance());
	}

	public static String colorizeFromInteger(Integer integer) {
		if (integer < 4) {
			return ChatColor.RED + "" + integer;
		}
		if (integer < 8) {
			return ChatColor.YELLOW + "" + integer;
		}
		return ChatColor.GREEN + "" + integer;
	}


	public static void deleteArena(Player player, String arenaName) {
		new BukkitRunnable() {
			@Override
			public void run() {
				Connection connection = null;
				PreparedStatement ps = null;

				try {
					String query = "DELETE FROM build_arenas_s14 WHERE arena_name = ?;\n";
					query += "DELETE FROM build_warps_s14 WHERE warp_name = ?;\n";
					query += "DELETE FROM build_warps_s14 WHERE warp_name = ?;\n";

					connection = Gberry.getConnection();
					ps = connection.prepareStatement(query);

					ps.setString(1, arenaName);
					ps.setString(2, arenaName + "-1");
					ps.setString(3, arenaName + "-2");

					Gberry.executeUpdate(connection, ps);

					if (player != null) {
						player.sendMessage(ChatColor.GREEN + "Arena " + arenaName + " has been deleted.");
					}
				} catch (SQLException ex) {
					ex.printStackTrace();
				} finally {
					Gberry.closeComponents(ps, connection);
				}
			}
		}.runTaskAsynchronously(ArenaSetup.getInstance());
	}

	public static void addArena(Player player, String arenaName, String types, String warp1, String warp2) {
		new BukkitRunnable() {
			@Override
			public void run() {
				Connection connection = null;
				PreparedStatement ps = null;

				try {
					String query = "UPDATE build_arenas_s14 SET warp_1 = ?, warp_2 = ?, types = ? WHERE arena_name = ?;\n";
					query += "INSERT INTO build_arenas_s14 (arena_name, types, warp_1, warp_2) SELECT ?, ?, ?, ? WHERE NOT EXISTS " +
							"(SELECT 1 FROM build_arenas_s14 WHERE arena_name = ?);";

					connection = Gberry.getConnection();
					ps = connection.prepareStatement(query);

					ps.setString(1, warp1);
					ps.setString(2, warp2);
					ps.setString(3, types);
					ps.setString(4, arenaName);
					ps.setString(5, arenaName);
					ps.setString(6, types);
					ps.setString(7, warp1);
					ps.setString(8, warp2);
					ps.setString(9, arenaName);

					Gberry.executeUpdate(connection, ps);

					if (player != null) {
						player.sendMessage(ChatColor.GREEN + "Arena " + arenaName + " has been added.");
					}
				} catch (SQLException ex) {
					ex.printStackTrace();
				} finally {
					Gberry.closeComponents(ps, connection);
				}
			}
		}.runTaskAsynchronously(ArenaSetup.getInstance());
	}

	public static void addWarp(String warpName, Player player, Location location) {
		new BukkitRunnable() {
			@Override
			public void run() {
				Connection connection = null;
				PreparedStatement ps = null;

				try {
					String query = "UPDATE build_warps_s14 SET x = ?, y = ?, z = ?, yaw = ?, pitch = ? WHERE warp_name = ?;\n";
					query += "INSERT INTO build_warps_s14 (warp_name, x, y, z, yaw, pitch) SELECT ?, ?, ?, ?, ?, ? WHERE NOT EXISTS " +
							"(SELECT 1 FROM build_warps_s14 WHERE warp_name = ?);";

					connection = Gberry.getConnection();

					ps = connection.prepareStatement(query);
					ps.setDouble(1, location.getX());
					ps.setDouble(2, location.getY() + 2);
					ps.setDouble(3, location.getZ());
					ps.setFloat(4, location.getYaw());
					ps.setFloat(5, 0); // Hardcoded pitch to 0
					ps.setString(6, warpName);
					ps.setString(7, warpName);
					ps.setDouble(8, location.getX());
					ps.setDouble(9, location.getY() + 2);
					ps.setDouble(10, location.getZ());
					ps.setFloat(11, location.getYaw());
					ps.setFloat(12, 0); // Hardcoded pitch to 0
					ps.setString(13, warpName);

					Gberry.executeUpdate(connection, ps);

					if (player != null) {
						player.sendMessage(ChatColor.GREEN + "Warp " + warpName + " saved.");
					}
				} catch (SQLException ex) {
					ex.printStackTrace();
				} finally {
					Gberry.closeComponents(ps, connection);
				}
			}
		}.runTaskAsynchronously(ArenaSetup.getInstance());
	}
}
