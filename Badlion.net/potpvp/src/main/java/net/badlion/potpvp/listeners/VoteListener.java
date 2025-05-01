package net.badlion.potpvp.listeners;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.PlayerRunnable;
import net.badlion.gberry.events.GSyncEvent;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.managers.RankedLeftManager;
import net.badlion.potpvp.managers.VoteManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VoteListener extends BukkitUtil.Listener {

    public static int NUM_OF_MATCHES_PER_VOTE = 4;

	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerJoin(final PlayerJoinEvent event) {
		if (!event.getPlayer().hasPermission("badlion.staff") && !VoteManager.hasVoted(event.getPlayer().getUniqueId())) {
			// Nope, haven't voted, send them a nice message
			event.getPlayer().sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "You haven't voted today! Vote daily to hide these messages and earn extra ranked matches!");
		}
	}

    @EventHandler
    public void onGSyncVote(GSyncEvent event) {
        if (event.getArgs().size() != 4) {
            return;
        }

        String subChannel = event.getArgs().get(0);
        if (subChannel.equals("Voting")) {
            String uuid = event.getArgs().get(1);
			String username = event.getArgs().get(2);
            Integer amount = Integer.parseInt(event.getArgs().get(3));
            RankedLeftManager.addRankedMatches(UUID.fromString(uuid), amount, false);
			boolean alreadyVoted = VoteManager.hasVoted(UUID.fromString(uuid));
			VoteManager.addVoted(UUID.fromString(uuid));

			// Now spam the fuck outta everyone who hasn't
			final Player player = PotPvP.getInstance().getServer().getPlayer(uuid);
			if (!alreadyVoted) {
				VoteListener.sendVoteMessage(player, username);
			}

            if (player != null) {
                player.sendMessage(ChatColor.GREEN + "You have voted! You earn 4 ranked matches per site you vote on, there are up to 5 sites to vote on daily!");
            }
        }
    }

	@EventHandler
	public void onVotifierEvent(VotifierEvent event) {
		final Vote vote = event.getVote();

		// Always run, ignore errors
		BukkitUtil.runTaskAsync(new Runnable() {

			@Override
			public void run() {
				String query = "INSERT INTO potion_vote_records (uuid, vote_date) VALUES (?, ?);";

				Connection connection = null;
				PreparedStatement ps = null;

				// Try to get the UUID, if we can't find it FUCK IT
				final UUID uuid = Gberry.getOfflineUUID(vote.getUsername());
				if (uuid == null) {
					return;
				}

				VoteManager.addVoted(uuid);

				try {
					connection = Gberry.getConnection();
					ps = connection.prepareStatement(query);
					ps.setString(1, uuid.toString());
					ps.setTimestamp(2, new Timestamp(new DateTime(DateTimeZone.UTC).getMillis()));

					Gberry.executeUpdate(connection, ps);
				} catch (SQLException e) {
					// Who cares, it won't work, move on with our FUCKING LIVES
					//e.printStackTrace();
				} finally {
					Gberry.closeComponents(ps, connection);
				}

				// This will only ever get called on the main na server cuz thats the only one we have registered with votifier
				List<String> args = new ArrayList<>();
				args.add("Voting");
				args.add(uuid.toString());
				args.add(vote.getUsername());
				args.add(VoteListener.NUM_OF_MATCHES_PER_VOTE + "");
				Gberry.sendGSyncEvent(args); // derp...

				// Add ranked matches for player
				BukkitUtil.runTask(new Runnable() {
					@Override
					public void run() {
						RankedLeftManager.addRankedMatches(uuid, VoteListener.NUM_OF_MATCHES_PER_VOTE, true);
					}
				});
			}
		});

		// Now spam the fuck outta everyone who hasn't
		final Player player = PotPvP.getInstance().getServer().getPlayer(vote.getUsername());
		if (player != null && !player.hasPermission("badlion.donator") && !VoteManager.hasVoted(player.getUniqueId())) {
			VoteListener.sendVoteMessage(player, vote.getUsername());
		}
	}

	public static void sendVoteMessage(final Player player, String username) {
		final String msg = ChatColor.AQUA + username + " has voted and earned extra ranked matches! Vote @ http://www.badlion.net daily to hide these messages!";

		Gberry.distributeTask(PotPvP.getInstance(), new PlayerRunnable() {
			@Override
			public void run(Player pl) {
				if (!VoteManager.hasVoted(pl.getUniqueId())) {
					// If we are not the person who voted
					if (player != pl && !pl.hasPermission("badlion.staff")) {
						pl.sendMessage(msg);
					}
				}
			}
		});

		if (player != null) {
			player.sendMessage(ChatColor.GREEN + "You have voted! You earn 4 ranked matches per site you vote on, there are up to 5 sites to vote on daily!");
		}
	}

	public static int getNumOfVotesForThisMonth(String name) {
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		String query = "SELECT COUNT(*) FROM potion_vote_records WHERE uuid = ? AND vote_date >= ?;";

		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, name);

			// In theory this should get us the first second of the day of the month
			DateTime date = new DateTime(DateTimeZone.UTC);
			date = date.minusSeconds(date.getSecondOfDay());
			date = date.minusDays(date.getDayOfMonth() - 1);
			ps.setTimestamp(2, new Timestamp(date.getMillis()));

			rs = Gberry.executeQuery(connection, ps);

			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}

		return 0;
	}

}
