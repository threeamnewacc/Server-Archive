package net.badlion.sgrankedmatchmaker.listeners;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import net.badlion.gberry.Gberry;
import net.badlion.sgrankedmatchmaker.SGRankedMatchMaker;
import net.badlion.sgrankedmatchmaker.managers.RankedLeftManager;
import net.badlion.gberry.utils.BukkitUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.joda.time.DateTime;

import java.sql.*;
import java.util.Date;
import java.util.HashSet;
import java.util.UUID;

public class VoteListener implements Listener {

    public static int NUM_OF_MATCHES_PER_VOTE = 1;
	public static final HashSet<String> peopleWhoVotedToday = new HashSet<>();

	@EventHandler(priority=EventPriority.MONITOR)
	public void onPlayerJoin(final PlayerJoinEvent event) {
		// Join message in here
		event.getPlayer().sendMessage(ChatColor.GREEN + "Welcome to the SG Ranked Matchmaking Lobby, please wait to be put into a match!");
        event.getPlayer().sendMessage(ChatColor.YELLOW + "PLEASE NOTE! If you leave after you are assigned a match you will get an automatic loss.");

		BukkitUtil.runTaskAsync(new Runnable() {
			@Override
			public void run() {
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;

				String query = "SELECT COUNT(*) FROM potion_vote_records WHERE uuid = ? AND vote_date >= ?;";

				try {
					connection = Gberry.getConnection();
					ps = connection.prepareStatement(query);
					ps.setString(1, event.getPlayer().getName());

					// In theory this should get us the first second of the day of the month
					DateTime date = new DateTime();
					date = date.minusSeconds(86400); // 1 day
					ps.setTimestamp(2, new Timestamp(date.toDate().getTime()));

					rs = Gberry.executeQuery(connection, ps);

					if (rs.next() && rs.getInt(1) != 0) {
						return;
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

				// Nope, haven't voted, send them a nice message
				if (!event.getPlayer().hasPermission("badlion.donator")) {
					event.getPlayer().sendMessage(ChatColor.AQUA + "" + ChatColor.BOLD + "You haven't voted today! Vote daily to hide these messages and earn up to 5 extra ranked matches!");
				}
			}
		});
	}

	@EventHandler
	public void onVotifierEvent(VotifierEvent event) {
		final Vote vote = event.getVote();

		// Always run, ignore errors
		BukkitUtil.runTaskAsync(new Runnable() {

			@Override
			public void run() {
				String query = "INSERT INTO sg_vote_records (uuid, vote_date) VALUES (?, ?);";

				Connection connection = null;
				PreparedStatement ps = null;

				// Try to get the UUID, if we can't find it FUCK IT
				final UUID uuid = Gberry.getOfflineUUID(vote.getUsername());
				if (uuid == null) {
					return;
				}

				try {
					// player1
					connection = Gberry.getConnection();
					ps = connection.prepareStatement(query);
					ps.setString(1, uuid.toString());
					ps.setTimestamp(2, new Timestamp(new Date().getTime()));

					Gberry.executeUpdate(connection, ps);
				} catch (SQLException e) {
					// Who cares, it won't work, move on with our FUCKING LIVES
					//e.printStackTrace();
				} finally {
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

				BukkitUtil.runTask(new Runnable() {
					@Override
					public void run() {
						RankedLeftManager.removeNumberOfMatches(uuid, VoteListener.NUM_OF_MATCHES_PER_VOTE);
						SGRankedMatchMaker.getInstance().getServer().dispatchCommand(SGRankedMatchMaker.getInstance().getServer().getConsoleSender(),
								"extrasgrankedmatches " + uuid.toString());
					}
				});
			}

		});

		// Now spam the fuck outta everyone who hasn't
		final String msg = ChatColor.AQUA + vote.getUsername() + " has voted and earned extra ranked matches! " +
				"Vote @ http://www.badlion.net daily to hide these messages!";

		synchronized (VoteListener.peopleWhoVotedToday) {
			// Don't shoot the same message off for the same person voting
			if (!VoteListener.peopleWhoVotedToday.contains(vote.getUsername())) {
				for (Player p : SGRankedMatchMaker.getInstance().getServer().getOnlinePlayers()) {
					if (!VoteListener.peopleWhoVotedToday.contains(p.getName()) && !p.getName().equals(vote.getUsername())) {
						p.sendMessage(msg);
					}
				}
			}
		}

		// Add to our record so we don't spam them
		synchronized (VoteListener.peopleWhoVotedToday) {
			VoteListener.peopleWhoVotedToday.add(vote.getUsername());
		}

		Player player = SGRankedMatchMaker.getInstance().getServer().getPlayer(vote.getUsername());
		if (player != null) {
			player.sendMessage(ChatColor.GREEN + "You have earned 1 ranked match for voting, there are up to 5 sites to vote on daily!");
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
			DateTime date = new DateTime();
			date = date.minusSeconds(date.getSecondOfDay());
			date = date.minusDays(date.getDayOfMonth() - 1);
			ps.setTimestamp(2, new Timestamp(date.toDate().getTime()));

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
