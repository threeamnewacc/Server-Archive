package net.badlion.gberry.utils;

import net.badlion.gberry.Gberry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseUtil {

    public static void insertDB(String query, Object... objects) {
        Connection connection = null;

        try {
            connection = Gberry.getConnection();
            insertDB(query, connection, objects);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(connection);
        }
    }

    public static void insertDB(String query, Connection connection, Object... objects) {
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(query);
            int i = 1;

            for (Object object : objects) {
                ps.setObject(i++, object);
            }

            Gberry.executeUpdate(connection, ps);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(ps);
        }
    }

    public static void execute(String query, Object... objects) {
        try {
            execute(query, Gberry.getConnection(), objects);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void execute(final String query, final Connection connection, Object... objects) {
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(query);
            int i = 1;

            for (Object object : objects) {
                ps.setObject(i++, object);
            }

            Gberry.executeUpdate(connection, ps);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(connection);
        }
    }

    public static String getDBString(String query, String stringToGet, Object... objects) {
        Connection connection = null;

        try {
            connection = Gberry.getConnection();

            return getDBString(query, connection, stringToGet, objects);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(connection);
        }

        return null;
    }

    public static String getDBString(String query, Connection connection, String stringToGet, Object... objects) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = connection.prepareStatement(query);
            int i = 1;

            for (Object object : objects) {
                ps.setObject(i++, object);
            }

            rs = Gberry.executeQuery(connection, ps);

            if (rs.next()) {
                return rs.getString(stringToGet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(rs, ps);
        }

        return null;
    }

    public static int getDBInt(String query, String intToGet, Object... objects) {
        Connection connection = null;

        try {
            connection = Gberry.getConnection();
            return getDBInt(query, connection, intToGet, objects);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(connection);
        }

        return -1;
    }

    public static int getDBInt(String query, Connection connection, String intToGet, Object... objects) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = connection.prepareStatement(query);
            int i = 1;

            for (Object object : objects) {
                ps.setObject(i++, object);
            }

            rs = Gberry.executeQuery(connection, ps);

            if (rs.next()) {
                return rs.getInt(intToGet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(rs, ps);
        }

        return -1;
    }

    public static boolean getDBBoolean(String query, String booleanToGet, Object... objects) {
        Connection connection = null;

        try {
            connection = Gberry.getConnection();
            return getDBBoolean(query, connection, booleanToGet, objects);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(connection);
        }

        return false;
    }

    public static boolean getDBBoolean(String query, Connection connection, String booleanToGet, Object... objects) {
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = connection.prepareStatement(query);
            int i = 1;

            for (Object object : objects) {
                ps.setObject(i++, object);
            }

            rs = Gberry.executeQuery(connection, ps);

            if (rs.next()) {
                return rs.getBoolean(booleanToGet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(rs, ps);
        }

        return false;
    }

}
