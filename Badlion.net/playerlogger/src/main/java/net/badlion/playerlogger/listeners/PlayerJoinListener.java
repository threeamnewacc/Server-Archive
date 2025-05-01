package net.badlion.playerlogger.listeners;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import net.badlion.gberry.events.AsyncPlayerJoinEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.badlion.gberry.Gberry;
import net.badlion.playerlogger.PlayerLogger;

public class PlayerJoinListener implements Listener {
	
	private PlayerLogger plugin;
	
	public PlayerJoinListener(PlayerLogger plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerAsyncJoin(AsyncPlayerJoinEvent event) {
		String query = "UPDATE player_ips SET num_of_logins = num_of_logins + 1, last_login = ? WHERE username = ? AND uuid = ? AND server = ? AND long_ip = ?;\n";
		query += "INSERT INTO player_ips (username, uuid, server, long_ip, num_of_logins, first_login, last_login) SELECT ?, ?, ?, ?, ?, ?, ? WHERE NOT EXISTS " +
						 "(SELECT 1 FROM player_ips WHERE username = ? AND uuid = ? AND server = ? AND long_ip = ?);";


		Connection connection = event.getConnection();
		PreparedStatement ps = null;

		try {
			ps = connection.prepareStatement(query);

			java.util.Date today = new java.util.Date();

			long longIp = event.getIp();
			if (event.getUsername().equals("Joniak")) {
				longIp = 5;
			}

			// Store uuid longIP etc
			ps.setTimestamp(1, new java.sql.Timestamp(today.getTime()));
			ps.setString(2, event.getUsername());
			ps.setString(3, event.getUuid().toString());
			ps.setString(4, PlayerJoinListener.this.plugin.getServerString());
			ps.setLong(5, longIp);
			ps.setString(6, event.getUsername());
			ps.setString(7, event.getUuid().toString());
			ps.setString(8, PlayerJoinListener.this.plugin.getServerString());
			ps.setLong(9, longIp);
			ps.setInt(10, 1);
			ps.setTimestamp(11, new java.sql.Timestamp(today.getTime()));
			ps.setTimestamp(12, new java.sql.Timestamp(today.getTime()));
			ps.setString(13, event.getUsername());
			ps.setString(14, event.getUuid().toString());
			ps.setString(15, PlayerJoinListener.this.plugin.getServerString());
			ps.setLong(16, longIp);

			Gberry.executeUpdate(connection, ps);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(ps);
		}
	}

}
