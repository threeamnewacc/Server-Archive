package net.badlion.colors;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.events.AsyncDelayedPlayerJoinEvent;
import net.badlion.gberry.events.AsyncPlayerJoinEvent;
import net.badlion.gpermissions.GPermissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerAsyncJoin(final AsyncPlayerJoinEvent event) {
        if (GPermissions.plugin.userHasPermission(event.getUuid().toString(), "ColorName.changecolor")) {
            ResultSet rs = null;
            PreparedStatement ps = null;

            try {
                String query = "SELECT * FROM " + ColorName.getInstance().getServerName() + "_colors WHERE uuid = ?;";
                Connection connection = event.getConnection();
                ps = connection.prepareStatement(query);
                ps.setString(1, event.getUuid().toString());
                rs = Gberry.executeQuery(connection, ps);

                if (rs.next()) {
                    final String color = rs.getString("color");

                    // Call our change event
                    event.getRunnables().add(new Runnable() {
                        @Override
                        public void run() {
                            Player player = Bukkit.getPlayer(event.getUuid());
                            if (player != null) {
                                player.setDisplayName(ChatColor.valueOf(color) + player.getName() + ChatColor.WHITE);
                            }

                            ColorName.getInstance().getServer().getPluginManager().callEvent(new ColorChangeEvent(event.getUuid()));
                        }
                    });
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                Gberry.closeComponents(rs, ps);
            }
        }
    }

}
