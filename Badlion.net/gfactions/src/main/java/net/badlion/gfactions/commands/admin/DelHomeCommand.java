package net.badlion.gfactions.commands.admin;

import net.badlion.gfactions.GFactions;
import net.badlion.gfactions.bukkitevents.HomeChangeEvent;
import net.badlion.gberry.Gberry;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class DelHomeCommand {

    public static boolean execute(final Player player, final String args[]) {

        GFactions.plugin.getServer().getScheduler().runTaskAsynchronously(GFactions.plugin, new Runnable() {
            @Override
            public void run() {
                // Just list all their current homes
                String query = "SELECT * FROM " + GFactions.PREFIX + "_homes WHERE uuid = ? AND home = ?;";

                Connection connection = null;
                PreparedStatement ps = null;
                ResultSet rs = null;

                try {
                    connection = Gberry.getConnection();
                    ps = connection.prepareStatement(query);

                    final UUID uuid = Gberry.getOfflineUUID(args[0]);
                    if (uuid == null) {
                        player.sendMessage(ChatColor.RED + "Could not locate UUID.");
                        return;
                    }

                    ps.setString(1, uuid.toString());
                    ps.setString(2, args[1]);
                    rs = Gberry.executeQuery(connection, ps);

                    if (rs.next()) {
                        query = "DELETE FROM faction_homes WHERE uuid = ? AND home = ?;";
                        ps = connection.prepareStatement(query);
                        ps.setString(1, uuid.toString());
                        ps.setString(2, args[1]);
                        Gberry.executeUpdate(connection, ps);
                        player.sendMessage(ChatColor.GOLD + "Home " + ChatColor.RED + args[1] + ChatColor.GOLD + " has been deleted for " + args[0]);

                        // Fire off the HomeChangeEvent
                        GFactions.plugin.getServer().getScheduler().runTask(GFactions.plugin, new Runnable() {
                            @Override
                            public void run() {
                                Player p = GFactions.plugin.getServer().getPlayer(uuid);
                                if (p != null) {
                                    HomeChangeEvent event = new HomeChangeEvent(p);
                                    GFactions.plugin.getServer().getPluginManager().callEvent(event);
                                }
                            }
                        });
                    } else {
                        player.sendMessage(ChatColor.DARK_RED + "Home " + ChatColor.RED + args[1] + ChatColor.DARK_RED + " doesn't exist!");
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
