package net.badlion.build.managers;

import net.badlion.gberry.Gberry;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ArenaManager {

    public enum ArenaType {
	    PEARL, NON_PEARL, BUILD_UHC, HORSE, SOUP, ARCHER, LMS, WAR, SLAUGHTER, UHC_MEETUP, INFECTION, KOTH, REDROVER, PARTYFFA, SKYWARS
    }

    public static void addArena(Player player, String arenaName, String types, String warp1, String warp2) {
        Connection connection = null;
        PreparedStatement ps = null;

        try {
            String query = "UPDATE build_arenas SET warp_1 = ?, warp_2 = ?, types = ? WHERE arena_name = ?;\n";
            query += "INSERT INTO build_arenas (arena_name, types, warp_1, warp_2) SELECT ?, ?, ?, ? WHERE NOT EXISTS " +
                    "(SELECT 1 FROM build_arenas WHERE arena_name = ?);";

            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);

            ps.setString(1, warp1);
            ps.setString(2, warp2);
            ps.setString(3, types);
            ps.setString(4, arenaName);
            ps.setString(5, arenaName);
            ps.setString(6, types);
            ps.setString(7, warp1);
            ps.setString(8, warp2);
            ps.setString(9, arenaName);

            Gberry.executeUpdate(connection, ps);

            player.sendMessage(ChatColor.GREEN + "Arena " + arenaName + " has been added/updated.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
    }

    public static void addWarp(String warpName, Player player) {
        Connection con = null;
        PreparedStatement ps = null;

        try {
            Location location = player.getLocation();

            String query = "UPDATE build_warps SET x = ?, y = ?, z = ?, yaw = ?, pitch = ? WHERE warp_name = ?;\n";
            query += "INSERT INTO build_warps (warp_name, x, y, z, yaw, pitch) SELECT ?, ?, ?, ?, ?, ? WHERE NOT EXISTS " +
                    "(SELECT 1 FROM build_warps WHERE warp_name = ?);";

            con = Gberry.getConnection();

            ps = con.prepareStatement(query);
            ps.setDouble(1, location.getX());
            ps.setDouble(2, location.getY() + 2);
            ps.setDouble(3, location.getZ());
            ps.setFloat(4, location.getYaw());
            ps.setFloat(5, location.getPitch());
            ps.setString(6, warpName);
            ps.setString(7, warpName);
            ps.setDouble(8, location.getX());
            ps.setDouble(9, location.getY() + 2);
            ps.setDouble(10, location.getZ());
            ps.setFloat(11, location.getYaw());
            ps.setFloat(12, location.getPitch());
            ps.setString(13, warpName);

            Gberry.executeUpdate(con, ps);

            player.sendMessage(ChatColor.GREEN + "Warp saved/updated.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (con != null) { try { con.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
    }

}
