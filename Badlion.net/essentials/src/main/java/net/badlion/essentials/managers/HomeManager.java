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

public class HomeManager {

    public static List<String> getAllHomes(UUID uuid) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<String> homes = new ArrayList<>();

        String query = "SELECT * FROM " + EssentialsPlugin.getPrefix() + "_homes WHERE uuid = ?";

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, uuid.toString());

            rs = Gberry.executeQuery(connection, ps);

            while (rs.next()) {
                homes.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(rs, ps, connection);
        }

        return homes;
    }

    public static Location getHome(UUID uuid, String name) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT * FROM " + EssentialsPlugin.getPrefix() + "_homes WHERE uuid = ? AND name = ?";

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, uuid.toString());
            ps.setString(2, name);

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

    public static boolean doesHomeExist(UUID uuid, String name) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT * FROM " + EssentialsPlugin.getPrefix() + "_homes WHERE uuid = ? AND name = ?";

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, uuid.toString());
            ps.setString(2, name);

            rs = Gberry.executeQuery(connection, ps);

            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(rs, ps, connection);
        }

        return false;
    }

    public static void deleteHome(UUID uuid, String name) {
        Connection connection = null;
        PreparedStatement ps = null;

        String query = "DELETE FROM " + EssentialsPlugin.getPrefix() + "_homes WHERE uuid = ? AND name = ?";

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, uuid.toString());
            ps.setString(2, name);

            Gberry.executeUpdate(connection, ps);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(ps, connection);
        }
    }

    public static void updateHome(UUID uuid, String name, Location location) {
        Connection connection = null;
        PreparedStatement ps = null;

        String query = "UPDATE " + EssentialsPlugin.getPrefix() + "_homes SET x = ?, y = ?, z = ?, yaw = ?, pitch = ?, world = ? WHERE uuid = ? AND name = ?;\n";
        query += "INSERT INTO " + EssentialsPlugin.getPrefix() + "_homes (uuid, name, x, y, z, yaw, pitch, world) SELECT ?, ?, ?, ?, ?, ?, ?, ? WHERE NOT EXISTS " +
                         "(SELECT 1 FROM " + EssentialsPlugin.getPrefix() + "_homes WHERE uuid = ? AND name = ?);";

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setDouble(1, location.getX());
            ps.setDouble(2, location.getY());
            ps.setDouble(3, location.getZ());
            ps.setDouble(4, location.getYaw());
            ps.setDouble(5, location.getPitch());
            ps.setString(6, location.getWorld().getName());
            ps.setString(7, uuid.toString());
            ps.setString(8, name);
            ps.setString(9, uuid.toString());
            ps.setString(10, name);
            ps.setDouble(11, location.getX());
            ps.setDouble(12, location.getY());
            ps.setDouble(13, location.getZ());
            ps.setDouble(14, location.getYaw());
            ps.setDouble(15, location.getPitch());
            ps.setString(16, location.getWorld().getName());
            ps.setString(17, uuid.toString());
            ps.setString(18, name);

            Gberry.executeUpdate(connection, ps);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(ps, connection);
        }
    }

}
