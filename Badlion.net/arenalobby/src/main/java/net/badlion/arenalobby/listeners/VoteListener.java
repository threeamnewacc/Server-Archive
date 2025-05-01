package net.badlion.arenalobby.listeners;

import net.badlion.arenalobby.ArenaLobby;
import net.badlion.arenalobby.managers.RankedLeftManager;
import net.badlion.arenalobby.managers.VoteManager;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.PlayerRunnable;
import net.badlion.gberry.events.GSyncEvent;
import net.badlion.gberry.utils.BukkitUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

public class VoteListener extends BukkitUtil.Listener {

	public static int NUM_OF_MATCHES_PER_VOTE = 4;

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(final PlayerJoinEvent event) {
		if (!event.getPlayer().hasPermission("badlion.staff") && !VoteManager.hasVoted(event.getPlayer().getUniqueId())) {
			// Nope, haven't voted, send them a nice message
			event.getPlayer().sendFormattedMessage("{0}You haven''t voted today! Vote daily to hide these messages and earn extra ranked matches!", ChatColor.AQUA + ChatColor.BOLD.toString());
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
			final Player player = ArenaLobby.getInstance().getServer().getPlayer(uuid);
			if (!alreadyVoted) {
				VoteListener.sendVoteMessage(player, username);
			}

			if (player != null) {
				player.sendFormattedMessage("{0}You have voted! You earn 4 ranked matches per site you vote on, there are up to 5 sites to vote on daily!", ChatColor.GREEN);
			}
		}
	}


	public static void sendVoteMessage(final Player player, String username) {
		final String msg = ChatColor.AQUA + username + " has voted and earned extra ranked matches! Vote @ http://www.badlion.net daily to hide these messages!";

		Gberry.distributeTask(ArenaLobby.getInstance(), new PlayerRunnable() {
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
			player.sendFormattedMessage("{0}You have voted! You earn 4 ranked matches per site you vote on, there are up to 5 sites to vote on daily!", ChatColor.GREEN);
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
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return 0;
	}

}
