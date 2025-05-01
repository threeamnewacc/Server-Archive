package net.badlion.essentials.managers;

import net.badlion.essentials.EssentialsPlugin;
import net.badlion.gberry.Gberry;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WarpManager {

    public static List<String> getAllWarps() {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<String> warps = new ArrayList<>();

        String query = "SELECT * FROM " + EssentialsPlugin.getPrefix() + "_essential_warps;";

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);

            rs = Gberry.executeQuery(connection, ps);

            while (rs.next()) {
                warps.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(rs, ps, connection);
        }

        return warps;
    }

    public static Location getWarp(String name) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT * FROM " + EssentialsPlugin.getPrefix() + "_essential_warps WHERE name = ?";

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, name);

            rs = Gberry.executeQuery(connection, ps);

            if (rs.next()) {
                World world = Bukkit.getWorld(rs.getString("world"));
                if (world == null) {
                    return null;
                }

                return new Location(world, rs.getDouble("x"), rs.getDouble("y"), rs.getDouble("z"), (float) rs.getDouble("yaw"), (float) rs.getDouble("pitch"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(rs, ps, connection);
        }

        return null;
    }

    public static boolean doesWarpExist(String name) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT * FROM " + EssentialsPlugin.getPrefix() + "_essential_warps WHERE name = ?";

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, name);

            rs = Gberry.executeQuery(connection, ps);

            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(rs, ps, connection);
        }

        return false;
    }

    public static void deleteWarp(String name) {
        Connection connection = null;
        PreparedStatement ps = null;

        String query = "DELETE FROM " + EssentialsPlugin.getPrefix() + "_essential_warps WHERE name = ?";

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, name);

            Gberry.executeUpdate(connection, ps);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(ps, connection);
        }
    }

    public static void updateWarp(String name, Location location) {
        Connection connection = null;
        PreparedStatement ps = null;

        String query = "UPDATE " + EssentialsPlugin.getPrefix() + "_essential_warps SET x = ?, y = ?, z = ?, yaw = ?, pitch = ?, world = ? WHERE name = ?;\n";
        query += "INSERT INTO " + EssentialsPlugin.getPrefix() + "_essential_warps (name, x, y, z, yaw, pitch, world) SELECT ?, ?, ?, ?, ?, ?, ? WHERE NOT EXISTS " +
                         "(SELECT 1 FROM " + EssentialsPlugin.getPrefix() + "_essential_warps WHERE name = ?);";

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setDouble(1, location.getX());
            ps.setDouble(2, location.getY());
            ps.setDouble(3, location.getZ());
            ps.setDouble(4, location.getYaw());
            ps.setDouble(5, location.getPitch());
            ps.setString(6, location.getWorld().getName());
            ps.setString(7, name);
            ps.setString(8, name);
            ps.setDouble(9, location.getX());
            ps.setDouble(10, location.getY());
            ps.setDouble(11, location.getZ());
            ps.setDouble(12, location.getYaw());
            ps.setDouble(13, location.getPitch());
            ps.setString(14, location.getWorld().getName());
            ps.setString(15, name);

            Gberry.executeUpdate(connection, ps);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(ps, connection);
        }
    }

}
