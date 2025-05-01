package net.badlion.gfactions.tasks;

import net.badlion.gberry.Gberry;
import net.badlion.gfactions.GFactions;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VoteCheckerTask extends BukkitRunnable {

    private GFactions plugin;

    public VoteCheckerTask(GFactions plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

            @Override
            public void run() {
                String query = "SELECT * FROM " + GFactions.PREFIX + "_vote_records WHERE vote_date = ?;";

                Connection connection = null;
                PreparedStatement ps = null;
                ResultSet rs = null;

                try {
                    // player1
                    connection = Gberry.getConnection();
                    ps = connection.prepareStatement(query);
                    ps.setDate(1, new java.sql.Date(new java.util.Date().getTime()));
                    rs = Gberry.executeQuery(connection, ps);

                    synchronized (plugin.getPeopleWhoVotedToday()) {
                        while (rs.next()) {
                            plugin.getPeopleWhoVotedToday().add(rs.getString("uuid"));
                        }
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
