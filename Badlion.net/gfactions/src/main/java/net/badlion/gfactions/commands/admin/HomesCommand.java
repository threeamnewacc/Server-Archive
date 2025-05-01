package net.badlion.gfactions.commands.admin;

import net.badlion.gberry.Gberry;
import net.badlion.gfactions.GFactions;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class HomesCommand {

	public static boolean execute(final Player player, final String args[]) {
		if (args.length == 0) {
			return false;
		}

		GFactions.plugin.getServer().getScheduler().runTaskAsynchronously(GFactions.plugin, new Runnable() {

			@Override
			public void run() {
				// Just list all their current homes
				String query = "SELECT * FROM " + GFactions.PREFIX + "_homes WHERE uuid = ?;";

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;

				try {
					final UUID uuid = Gberry.getOfflineUUID(args[0]);
					if (uuid == null) {
						player.sendMessage(ChatColor.RED + "Could not locate UUID.");
						return;
					}

					connection = Gberry.getConnection();
					ps = connection.prepareStatement(query);
					ps.setString(1, uuid.toString());
					rs = Gberry.executeQuery(connection, ps);

					int count = 0;
					while (rs.next()) {
						if (count == 0) { // Send this message once
							player.sendMessage(ChatColor.GREEN + args[0] + ChatColor.GOLD + " has the following home(s):");
						}
						player.sendMessage(ChatColor.DARK_GREEN + rs.getString("home") + ChatColor.GOLD + " - " + ChatColor.DARK_AQUA + rs.getInt("x")
								+ ChatColor.GOLD + ", " + ChatColor.DARK_AQUA + rs.getInt("y")
								+ ChatColor.GOLD + ", " + ChatColor.DARK_AQUA + rs.getInt("z") + ChatColor.GOLD + " in world " + ChatColor.DARK_AQUA + rs.getString("world"));
						count++;
					}

					if (count == 0) {
						player.sendMessage(ChatColor.GOLD + "This user has no homes.");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
					if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
					if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
				}

			}

		});

		return true;
	}

}
