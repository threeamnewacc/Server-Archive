package net.badlion.gberry.tasks;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.events.ServerRebootMessageEvent;
import net.badlion.gberry.utils.BukkitUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.joda.time.LocalTime;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ServerRebootTask extends BukkitRunnable {

	private int threshold = 0;
	private boolean loaded = false;

	private Map<LocalTime, Boolean> rebootTimes = new HashMap<>();

	// Add 5-6 hours on top of time
	private Long sixHourRebootTime = System.currentTimeMillis() + Gberry.generateRandomInt(18000000, 21600000);

	public ServerRebootTask() {
		BukkitUtil.runTaskAsync(new Runnable() {
			@Override
			public void run() {
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;

				String query = "SELECT * FROM server_reboot_times WHERE server_name = ?;";

				try {
					connection = Gberry.getConnection();
					ps = connection.prepareStatement(query);

					ps.setString(1, Gberry.serverName.toLowerCase());

					rs = Gberry.executeQuery(connection, ps);

					while (rs.next()) {
						ServerRebootTask.this.rebootTimes.put(LocalTime.parse(rs.getString("reboot_time")).minusSeconds(1),
								rs.getBoolean("reboot_with_players"));
					}

					ServerRebootTask.this.loaded = true;
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					Gberry.closeComponents(rs, ps, connection);
				}
			}
		});
	}

	@Override
	public void run() {
		// Wait until data loads
		if (!this.loaded) return;

		// Make sure server has been up for 5 minutes
		if (Gberry.plugin.getServerUptime() < 300000) return;

		boolean playersOnline = Gberry.plugin.getServer().getOnlinePlayers().size() > 0;

		// No reboot times in database for this server?
		if (this.rebootTimes.isEmpty()) {
			// Has it been 6 hours since last reboot?
			if (System.currentTimeMillis() >= this.sixHourRebootTime) {
				// Are there any players online or has server not rebooted in 24 hours?
				if (!playersOnline || this.threshold == 3) {
					// Call event for special server restart handling
					Gberry.plugin.getServer().getPluginManager().callEvent(new ServerRebootMessageEvent(0));
					Gberry.broadcastMessage(ChatColor.RED + ChatColor.BOLD.toString() + "Server rebooting!");

					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
				} else {
					// Reboot 5-6 hours later then (safety)
					this.sixHourRebootTime += Gberry.generateRandomInt(18000000, 21600000);

					this.threshold++;
				}
			}
			return;
		}

		LocalTime now = LocalTime.now().withSecondOfMinute(0).withMillisOfSecond(0);
		for (LocalTime time : this.rebootTimes.keySet()) {
			// Can we reboot while players are online for this reboot time?
			if (playersOnline && !this.rebootTimes.get(time)) continue;

			if (now.compareTo(time) <= 0 && now.compareTo(time.minusMinutes(5)) >= 0) {
				for (int i = 1; i < 6; i++) {
					if (now.compareTo(time.minusMinutes(i)) >= 0) {
						ServerRebootMessageEvent event = new ServerRebootMessageEvent(i);
						Gberry.plugin.getServer().getPluginManager().callEvent(event);

						if (!event.isCancelled()) {
							if (i == 1) {
								Gberry.broadcastMessage(ChatColor.RED + "Server rebooting in 1 minute.");

								// Start task for 10 second message when there's 1 minute left
								BukkitUtil.runTaskLater(new Runnable() {
									@Override
									public void run() {
										Gberry.broadcastMessage(ChatColor.RED + "Server rebooting in 10 seconds.");
									}
								}, 1000L);

								// Start task for reboot in a minute
								BukkitUtil.runTaskLater(new Runnable() {
									@Override
									public void run() {
										// Call event for special server restart handling
										Gberry.plugin.getServer().getPluginManager().callEvent(new ServerRebootMessageEvent(0));
										Gberry.broadcastMessage(ChatColor.RED + ChatColor.BOLD.toString() + "Server rebooting!");

										Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
									}
								}, 1200L);
							} else {
								Gberry.broadcastMessage(ChatColor.RED + "Server rebooting in " + i + " minutes.");
							}
						}

						Bukkit.getLogger().info("Server rebooting in " + i + " minutes.");
						break;
					}
				}
			}
		}
	}

}
