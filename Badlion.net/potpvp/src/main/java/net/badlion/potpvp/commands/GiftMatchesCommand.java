package net.badlion.potpvp.commands;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.managers.RankedLeftManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GiftMatchesCommand extends GCommandExecutor {

    public static int FREE_NUM_OF_MATCHES_TO_GIVE_FOR_DONATOR_PLUS = 20;
    public static int FREE_NUM_OF_MATCHES_TO_GIVE_FOR_LION = 40;

    private Set<UUID> gifting = new HashSet<>();
    private static ConcurrentHashMap<UUID, Boolean> givenMatches = new ConcurrentHashMap<>();

    public GiftMatchesCommand() {
        super(1); // 1 args minimum
    }

    @Override
    public void onGroupCommand(Command command, String label, final String[] args) {
        final Player pl = PotPvP.getInstance().getServer().getPlayer(args[0]);
        if (pl == null) {
            this.player.sendMessage(ChatColor.RED + "Player is not online, could not give extra ranked matches.");
            return;
        }

        if (this.gifting.contains(this.player.getUniqueId())) {
            this.player.sendMessage(ChatColor.RED + "You are already attempting to gift matches to someone!");
            return;
        }

	    if (pl == this.player) {
		    this.player.sendMessage(ChatColor.RED + "You can't gift matches to yourself!");
		    return;
	    }

        this.gifting.add(this.player.getUniqueId());

	    BukkitUtil.runTaskAsync(new Runnable() {
            @Override
            public void run() {
                if (!GiftMatchesCommand.givenMatches.containsKey(GiftMatchesCommand.this.player.getUniqueId())) {
                    // We might not have loaded their stuff, try again
                    if (GiftMatchesCommand.this.checkGiftMatchesUsedToday(GiftMatchesCommand.this.player.getUniqueId())) {
                        GiftMatchesCommand.this.player.sendMessage(ChatColor.RED + "You have already given out your extra ranked matches today.");
	                    GiftMatchesCommand.this.gifting.remove(GiftMatchesCommand.this.player.getUniqueId());
	                    return;
                    }
                } else if (GiftMatchesCommand.givenMatches.get(GiftMatchesCommand.this.player.getUniqueId())) {
                    GiftMatchesCommand.this.player.sendMessage(ChatColor.RED + "You have already given out your extra ranked matches today.");
	                GiftMatchesCommand.this.gifting.remove(GiftMatchesCommand.this.player.getUniqueId());
                    return;
                }

                // Add matches to other player, no need remove from current player because they're donator & we record /giftmatches use
                BukkitUtil.runTask(new Runnable() {
                    @Override
                    public void run() {
                        final boolean registered = GiftMatchesCommand.this.player.hasPermission("badlion.registered");

                        // Check player again because they could have logged off by now
                        if (!Gberry.isPlayerOnline(pl)) {
                            GiftMatchesCommand.this.player.sendMessage(ChatColor.RED + "Player is not online, could not give extra ranked matches.");
	                        GiftMatchesCommand.this.gifting.remove(GiftMatchesCommand.this.player.getUniqueId());
                            return;
                        }

                        int amt = GiftMatchesCommand.FREE_NUM_OF_MATCHES_TO_GIVE_FOR_DONATOR_PLUS;
                        if (GiftMatchesCommand.this.player.hasPermission("badlion.lion")) {
                            amt = GiftMatchesCommand.FREE_NUM_OF_MATCHES_TO_GIVE_FOR_LION;
                        }

                        if (registered) {
                            amt += 10;
                        }

                        RankedLeftManager.addRankedMatches(pl, amt, true);

                        final int finalAmt = amt;

                        // Record /giftmatches use
                        BukkitUtil.runTaskAsync(new Runnable() {
                            @Override
                            public void run() {
                                GiftMatchesCommand.this.recordGiftMatchesUse(GiftMatchesCommand.this.player.getUniqueId());

                                GiftMatchesCommand.givenMatches.put(GiftMatchesCommand.this.player.getUniqueId(), true);

                                // Fuck the players, remove them from set async
                                GiftMatchesCommand.this.gifting.remove(GiftMatchesCommand.this.player.getUniqueId());

                                pl.sendMessage(ChatColor.GREEN + "You have been given " + finalAmt + " extra ranked matches by " + GiftMatchesCommand.this.player.getName());

                                GiftMatchesCommand.this.player.sendMessage(ChatColor.GREEN + "You have given your extra ranked matches for the day to " + pl.getName());
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public void usage(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Command usage: /giftmatches [name]");
        sender.sendMessage(ChatColor.YELLOW + "This will give the specified player 20 extra ranked matches :)");
    }

    public boolean checkGiftMatchesUsedToday(UUID uuid) {
        String query = "SELECT * FROM potion_gifted_matches WHERE uuid = ? AND day = ?;";

        Connection connection = null;
        ResultSet rs = null;
        PreparedStatement ps = null;

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, uuid.toString());
            ps.setTimestamp(2, new Timestamp(new DateTime(DateTimeZone.UTC).getMillis()));

            rs = Gberry.executeQuery(connection, ps);

	        // Add to our map
            boolean val = rs.next();
	        GiftMatchesCommand.givenMatches.put(uuid, val);

            return val;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }

        return false;
    }

    private void recordGiftMatchesUse(UUID uuid) {
        String query = "INSERT INTO potion_gifted_matches (uuid, day) VALUES (?, ?);";

        Connection connection = null;
        PreparedStatement ps = null;

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, uuid.toString());
            ps.setTimestamp(2, new Timestamp(new DateTime(DateTimeZone.UTC).getMillis()));

            Gberry.executeUpdate(connection, ps);
        } catch (SQLException e) {

        } finally {
            if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
    }

}
