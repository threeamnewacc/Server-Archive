package net.badlion.gfactions.commands;

import net.badlion.gfactions.GFactions;
import net.badlion.gberry.Gberry;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.sql.*;

public class SeenCommand implements CommandExecutor {

	private GFactions plugin;

	public SeenCommand(GFactions plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(final CommandSender sender, Command command, String label, final String[] args) {
		// Safety checks
		if (args.length < 1) {
			return false;
		}

		if (args[0].length() > 16) {
			return false;
		}

		this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
			@Override
			public void run() {
				String query = "SELECT last_login FROM player_ips WHERE username = ? AND server = '" + Gberry.serverName + "' ORDER BY last_login LIMIT 1;";

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;

				try {
					connection = Gberry.getConnection();
					ps = connection.prepareStatement(query);
					ps.setString(1, args[0]);
					rs = Gberry.executeQuery(connection, ps);

					if (rs.next()) {
						Timestamp timestamp = rs.getTimestamp("last_login");
						sender.sendMessage(ChatColor.GREEN + args[0]+ ChatColor.GOLD + " was last seen on " + ChatColor.RED + timestamp.toString());
					} else {
						sender.sendMessage(ChatColor.RED + "Could not find " + args[0]);
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
