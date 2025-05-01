package net.badlion.playerlogger.listeners;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;

import net.badlion.gberry.Gberry;
import net.badlion.playerlogger.PlayerLogger;

public class PlayerDeathListener implements Listener {
	
	private PlayerLogger plugin;
	
	public PlayerDeathListener(PlayerLogger plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onPlayerDeath(final PlayerDeathEvent event) {
		final Player player = event.getEntity();
		if (player != null) {
			// ur banned
			this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
	  			
				public void run() {
					String query = "INSERT INTO death_logger (uuid, server, killer, killer_weapon, death_reason, death_x, death_y, " +
							"death_z, death_time, user_pot_effects, killer_pot_effects) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,?);";

					Connection connection = null;
					PreparedStatement ps = null;
					
					try {
						// player1
						connection = Gberry.getConnection();
						ps = connection.prepareStatement(query);
			
						java.util.Date today = new java.util.Date();
						
						ps.setString(1, player.getUniqueId().toString());
						ps.setString(2, plugin.getServerString());
						
						// Get Killer or we set nothing
						Player killer = player.getKiller();
						if (killer == null) {
							ps.setString(3, "N/A");
							ps.setString(4, "N/A");
						} else {
							ps.setString(3, killer.getUniqueId().toString());
							ps.setString(4, killer.getItemInHand().getType().name().toLowerCase().replaceAll("_", " "));
						}
						
						ps.setString(5, event.getDeathMessage() == null ? "none" : event.getDeathMessage());
						
						Location location = player.getLocation();
						ps.setInt(6, location.getBlockX());
						ps.setInt(7, location.getBlockY());
						ps.setInt(8, location.getBlockZ());
						
						ps.setTimestamp(9,  new java.sql.Timestamp(today.getTime()));
						
						StringBuilder userPots = new StringBuilder();
						StringBuilder killerPots = new StringBuilder();
						
						for (PotionEffect potionEffect : player.getActivePotionEffects()) {
							userPots.append(potionEffect.getType().getName());
							userPots.append(",");
						}
						
						if (killer != null) {
							for (PotionEffect potionEffect : killer.getActivePotionEffects()) {
								killerPots.append(potionEffect.getType().getName());
								killerPots.append(",");
							}
						}
							
						ps.setString(10, userPots.toString());
						ps.setString(11, killerPots.toString());
						
						Gberry.executeUpdate(connection, ps);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
						if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
					}
				}
			});
		}
	}
	
}
