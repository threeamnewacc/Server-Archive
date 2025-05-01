package net.badlion.gfactions.listeners;

import net.badlion.gberry.Gberry;
import net.badlion.gfactions.GFactions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.*;

public class RegisteredListener implements Listener {

    private GFactions plugin;

    public RegisteredListener(GFactions plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        if (!player.hasPermission("GFactions.kit.member")) {
            // Check to see if they have registered
            this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

                @Override
                public void run() {
                    String query = "SELECT * FROM users WHERE uuid = ? AND minecraft_verified = 1 AND active = 1;";

                    Connection connection = null;
                    PreparedStatement ps = null;
                    ResultSet rs = null;

                    try {
                        // player1
                        connection = Gberry.getConnection();
                        ps = connection.prepareStatement(query);
                        ps.setString(1, player.getUniqueId().toString());
                        rs = Gberry.executeQuery(connection, ps);

                        if (rs.next()) {
                            plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "user " + player.getName() + " setgroup member");
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
        }
    }
}
