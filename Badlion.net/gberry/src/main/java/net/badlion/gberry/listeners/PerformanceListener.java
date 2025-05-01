package net.badlion.gberry.listeners;

import com.google.common.base.Joiner;
import net.badlion.gberry.Gberry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.WatchdogEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class PerformanceListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().hasPermission("badlion.admin")) {
            event.getPlayer().performCommand("ss");
        }
    }

    @EventHandler
    public void onWatchDog(final WatchdogEvent event) {
        new BukkitRunnable() {
            public void run() {
                String allTraces = Joiner.on("\n=======================================\n").join(event.getTraces());
                DateTime dateTime = new DateTime(DateTimeZone.UTC);
                String query = "INSERT INTO badlion_performance (server_name, ts, traces) VALUES (?, ?, ?);";

                Connection connection = null;
                PreparedStatement ps = null;

                try {
                    connection = Gberry.getSlowConnection();
                    ps = connection.prepareStatement(query);
                    ps.setString(1, Gberry.serverName);
                    ps.setTimestamp(2, new Timestamp(dateTime.getMillis()));
                    ps.setString(3, allTraces);
                    Gberry.executeUpdate(connection, ps);
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
                    if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
                }
            }
        }.runTaskAsynchronously(Gberry.plugin);
    }

}
