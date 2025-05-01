package net.badlion.potpvp.managers;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.events.AsyncPlayerJoinEvent;
import net.badlion.gpermissions.GPermissions;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.potpvp.PotPvP;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DonatorManager extends BukkitUtil.Listener {

    private static int DONATOR_EVENT_WAIT_TIME = 1000 * 60 * 60 * 6; // 6 hours
    private static int DONATOR_PLUS_EVENT_WAIT_TIME = 1000 * 60 * 60; // 1 hour
    private static int LION_EVENT_WAIT_TIME = 1000 * 60 * 30; // 30 minutes

    private static Map<UUID, Long> lastDonatorEventTimes = new ConcurrentHashMap<>();

    public static boolean canStartEvent(Player player) {
        // Is player a donator?
        if (!player.hasPermission("badlion.donator")) {
            player.sendMessage(ChatColor.RED + "Only donators can start events.");
            player.sendMessage(ChatColor.GREEN + "Become a donator at http://store.badlion.net/ and help support the server.");
            return false;
        }

        // Is player a staff member?
        if (player.hasPermission("badlion.staff")) {
            return true;
        }

        if (!DonatorManager.lastDonatorEventTimes.containsKey(player.getUniqueId())) {
            DonatorManager.lastDonatorEventTimes.put(player.getUniqueId(), 0L);
        }

        if (player.hasPermission("badlion.lion")) {
            return DonatorManager.lastDonatorEventTimes.get(player.getUniqueId()) + DonatorManager.LION_EVENT_WAIT_TIME < System.currentTimeMillis();
        } else if (player.hasPermission("badlion.donatorplus")) {
            return DonatorManager.lastDonatorEventTimes.get(player.getUniqueId()) + DonatorManager.DONATOR_PLUS_EVENT_WAIT_TIME < System.currentTimeMillis();
        } else {
            return DonatorManager.lastDonatorEventTimes.get(player.getUniqueId()) + DonatorManager.DONATOR_EVENT_WAIT_TIME < System.currentTimeMillis();
        }
    }

    public static Long getTimeRemaining(Player player) {
        if (player.hasPermission("badlion.lion")) {
            return DonatorManager.lastDonatorEventTimes.get(player.getUniqueId()) + DonatorManager.LION_EVENT_WAIT_TIME - System.currentTimeMillis();
        } else if (player.hasPermission("badlion.donatorplus")) {
            return DonatorManager.lastDonatorEventTimes.get(player.getUniqueId()) + DonatorManager.DONATOR_PLUS_EVENT_WAIT_TIME - System.currentTimeMillis();
        } else {
            return DonatorManager.lastDonatorEventTimes.get(player.getUniqueId()) + DonatorManager.DONATOR_EVENT_WAIT_TIME - System.currentTimeMillis();
        }
    }

    public static void setEventTime(final UUID uuid, final Long time) {
        DonatorManager.lastDonatorEventTimes.put(uuid, time);

        PotPvP.getInstance().getServer().getScheduler().runTaskAsynchronously(PotPvP.getInstance(), new Runnable() {
            @Override
            public void run() {
                DonatorManager.setLastEventTime(uuid, time);
            }
        });
    }

    @EventHandler
    public void onPlayerAsyncJoin(final AsyncPlayerJoinEvent event) {
        if (GPermissions.plugin.userHasPermission(event.getUuid().toString(), "badlion.donator")) {
            // Initialize these for every player
            DonatorManager.getLastEventTime(event.getConnection(), event.getUuid());
        }
    }

    @EventHandler(priority=EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent event) {
        try {
            DonatorManager.lastDonatorEventTimes.remove(event.getPlayer().getUniqueId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getLastEventTime(Connection connection, UUID uuid) {
        String query = "SELECT * FROM potion_donator_events WHERE uuid = ?;";

        ResultSet rs = null;
        PreparedStatement ps = null;

        // First get their yolo queue ratings
        try {
            ps = connection.prepareStatement(query);
            ps.setString(1, uuid.toString());
            rs = Gberry.executeQuery(connection, ps);

            // Store their ratings
            if (rs.next()) {
                Gberry.log("DONATOR", "Retrieved event time " + rs.getLong("event_time") + " for " + uuid);
                DonatorManager.lastDonatorEventTimes.put(uuid, rs.getLong("event_time"));
            } else {
                DonatorManager.lastDonatorEventTimes.put(uuid, 0L);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
    }

    private static void setLastEventTime(UUID uuid, Long time) {
        String query = "UPDATE potion_donator_events SET event_time = ? WHERE uuid = ?;\n";
        query += "INSERT INTO potion_donator_events (uuid, event_time) SELECT ?, ? WHERE NOT EXISTS " +
                "(SELECT 1 FROM potion_donator_events WHERE uuid = ?);";

        Connection connection = null;
        PreparedStatement ps = null;

        // First get their yolo queue ratings
        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setLong(1, time);
            ps.setString(2, uuid.toString());
            ps.setString(3, uuid.toString());
            ps.setLong(4, time);
            ps.setString(5, uuid.toString());
            Gberry.executeUpdate(connection, ps);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
    }

}
