package net.badlion.archmoney.commands;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.badlion.archmoney.ArchMoney;
import net.badlion.gberry.Gberry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TopCommand implements CommandExecutor{
	
	private ArchMoney plugin;
	
	public TopCommand(ArchMoney plugin){
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, final String[] args) {
		if (sender instanceof Player){
			final Player player = (Player) sender;
			
			Bukkit.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
				public void run(){
					String query = "SELECT * FROM " + plugin.getServerName() + "_money_balances WHERE uuid NOT LIKE '~faction_%' ORDER BY balance DESC LIMIT 10;";
					Connection connection = null;
					PreparedStatement ps = null;
					ResultSet rs = null;
					
					try {
						connection = Gberry.getConnection();
						ps = connection.prepareStatement(query);
						rs = Gberry.executeQuery(connection, ps);
						
						player.sendMessage(ChatColor.RED + "---Top 10 Balances---");
						
						while (rs.next()){
							player.sendMessage(ChatColor.BLUE + "" + Gberry.getUsernameFromUUID(rs.getString("uuid")) + ChatColor.RED + " - " + ChatColor.YELLOW +  rs.getInt("balance"));
						}

                        player.sendMessage(ChatColor.RED + "--------------");
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {		
						if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
						if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
						if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
					}
				}
			});
		}
		return true;
	}

}
