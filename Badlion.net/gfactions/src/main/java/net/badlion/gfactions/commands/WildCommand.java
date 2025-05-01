package net.badlion.gfactions.commands;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Faction;
import net.badlion.gfactions.GFactions;
import net.badlion.gfactions.tasks.WarpTask;
import net.badlion.gberry.Gberry;
import net.badlion.gguard.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.joda.time.DateTime;

import java.sql.*;

public class WildCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            final String uuid = player.getUniqueId().toString();

            // Are we combat tagged?
            if (GFactions.plugin.isInCombat(player)) {
                player.sendMessage(ChatColor.RED + "Cannot use /wild when in combat.");
                return true;
            }

            // Can only use wild in spawn
            ProtectedRegion region = GFactions.plugin.getgGuardPlugin().getProtectedRegion(player.getLocation(),
                    GFactions.plugin.getgGuardPlugin().getProtectedRegions());
            if (region != null && !region.getRegionName().equals("spawn")) {
                player.sendMessage(ChatColor.RED + "Cannot use /wild when not in spawn.");
                return true;
            }

            // Check if they used the command in the past 2 hours
            GFactions.plugin.getServer().getScheduler().runTaskAsynchronously(GFactions.plugin, new Runnable() {
                @Override
                public void run() {
                    String timeLeft = WildCommand.this.usedCommandRecently(uuid);
                    if (timeLeft != null) {
                        player.sendMessage(ChatColor.RED + "You cannot use this command for another " + timeLeft);
                    } else {
                        GFactions.plugin.getServer().getScheduler().runTask(GFactions.plugin, new Runnable() {
                            @Override
                            public void run() {
                                // Get a good location
                                Location loc = null;
                                while (loc == null) {
                                    loc = WildCommand.this.randomWildernessLocation();
                                }

                                // Use the warp task cuz if we write our own class, that's more lines of code = more possible bugs
                                player.sendMessage(ChatColor.GOLD + "Teleportation will commence in " + ChatColor.RED + "7 seconds"
                                        + ChatColor.GOLD + ". Don't move.");
                                new WarpTask(GFactions.plugin, player, player.getLocation(), loc, false).runTaskTimer(GFactions.plugin, 0L, 5L);

                                // Sneaky bugger eh thought ye could get around this eh?
                                GFactions.plugin.getServer().getScheduler().runTaskAsynchronously(GFactions.plugin, new Runnable() {
                                    @Override
                                    public void run() {
                                        WildCommand.this.recordCommandUse(uuid);
                                    }
                                });
                            }
                        });
                    }
                }
            });
        }
        return true;
    }

    private Location randomWildernessLocation() {
        World w = GFactions.plugin.getSpawnLocation().getWorld();

        int x = GFactions.plugin.generateRandomInt(512, 2000);
        int z = GFactions.plugin.generateRandomInt(512, 2000);
        int y = w.getHighestBlockYAt(x, z);

        Location loc = new Location(w, x, y, z);

        // Don't spawn in water/lava
        Material material = loc.add(0, -1, 0).getBlock().getType();
        if (material == Material.STATIONARY_WATER || material == Material.WATER
                    || material == Material.LAVA || material == Material.STATIONARY_LAVA) {
            return null;
        }

        Faction faction = Board.getFactionAt(loc);

        if (faction.getId().equals("0")) {
            return loc.add(0, 4, 0); // Up a bit more
        }

        return null;
    }

    private void recordCommandUse(String uuid) {
        Connection connection = null;
        PreparedStatement ps = null;

        //String query = "INSERT INTO faction_wild_command_records (uuid, \"time\") VALUES (?, ?) " +
        //					   "ON DUPLICATE KEY UPDATE \"time\" = ?;";
        String query = "UPDATE faction_wild_command_records SET \"time\" = ? WHERE uuid = ?;\n";
        query += "INSERT INTO faction_wild_command_records (uuid, \"time\") SELECT ?, ? WHERE NOT EXISTS " +
                         "(SELECT 1 FROM faction_wild_command_records WHERE uuid = ?);";

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);

            Timestamp ts = new Timestamp(DateTime.now().toDate().getTime());

            ps.setTimestamp(1, ts);
            ps.setString(2, uuid);
            ps.setString(3, uuid);
            ps.setTimestamp(4, ts);
            ps.setString(5, uuid);

            Gberry.executeUpdate(connection, ps);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
    }

    private String usedCommandRecently(String uuid) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT * FROM faction_wild_command_records WHERE uuid = ?;";

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, uuid);

            rs = Gberry.executeQuery(connection, ps);

            // Check if any of the dates are from within 2 hours ago
            if (rs.next()) {
                Timestamp time = rs.getTimestamp("time");
                DateTime recordDate = new DateTime(time);
                DateTime nextUse = recordDate.plusHours(2);

                DateTime now = DateTime.now();

                if (now.isAfter(nextUse)) {
                    return null;
                } else {
                    // Get total difference in minutes
                    int minutes = nextUse.getMinuteOfDay() - now.getMinuteOfDay();

                    // Got at least an hour left?
                    if (minutes - 60 >= 0) {
                        // There is at least an hour left so remove 60 minutes since we use getMinuteOfDay()
                        minutes = minutes - 60;

                        // Grammar nazi
                        if (minutes != 0) {
                            return "1 hour and " + minutes + " minutes.";
                        } else {
                            return "1 hour.";
                        }
                    } else {
                        // Grammar nazi
                        if (minutes != 1) {
                            return minutes + " minutes.";
                        } else {
                            return "1 minute.";
                        }
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }

        return null;
    }

}