package net.badlion.gfactions.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.badlion.gberry.Gberry;
import net.badlion.gfactions.GFactions;

public class FunModCommand implements CommandExecutor {

	private GFactions plugin;
	
	public FunModCommand(GFactions plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, final String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			
			if (args.length < 1) {
				player.sendMessage(ChatColor.RED + "Not using the mod commands properly.");
				return true;
			}

			Player target = this.plugin.getServer().getPlayer(args[0]);
			if (target == null) {
				player.sendMessage(ChatColor.RED + "Not a valid player or not online.");
				return true;
			}
			
			if (command.getName().equals("slap")) {
				target.setHealth(10); // 5 hearts
				this.insertModActionIntoDatabase(player.getUniqueId().toString(), target.getUniqueId().toString(), command.getName());
				player.sendMessage(ChatColor.GREEN + "Slapped " + target.getName());
			} else if (command.getName().equals("strike")) {
				target.setHealth(0);
				this.insertModActionIntoDatabase(player.getUniqueId().toString(), target.getUniqueId().toString(), command.getName());
				player.sendMessage(ChatColor.GREEN + "Striked " + target.getName());
			} else if (command.getName().equals("boot")) {
				target.kickPlayer("");
				this.plugin.getPlayersToBeKicked().add(target.getName());
				this.insertModActionIntoDatabase(player.getUniqueId().toString(), target.getUniqueId().toString(), command.getName());
				player.sendMessage(ChatColor.GREEN + "Booted " + target.getName());
			}
		}
		return true;
	}
	
	public void insertModActionIntoDatabase(final String user, final String target, final String action) {
		this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
			
			@Override
			public void run() {
				Connection connection = null;
				PreparedStatement ps = null;
				
				try {
					String query = "INSERT INTO " + GFactions.PREFIX + "_admin_command_log (used_on, used_by, command, time_used) VALUES(?, ?, ?, ?);";
					
					java.util.Date today = new java.util.Date();
					
					connection = Gberry.getConnection();
					ps = connection.prepareStatement(query);
					ps.setString(1, target);
					ps.setString(2, user);
					ps.setString(3,  action);
					ps.setTimestamp(4, new java.sql.Timestamp(today.getTime()));
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

}
