package net.badlion.arenavotes.listener;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import net.badlion.arenavotes.manager.VoteManager;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VoteListener implements Listener {

	public static int NUM_OF_MATCHES_PER_VOTE = 4;

	@EventHandler
	public void onVotifierEvent(VotifierEvent event) {
		final Vote vote = event.getVote();

		// Listen for a players vote, insert it into the db, add matches for the player and send out gsync event to update the arena lobbies

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

				// This will only ever get called from the bllobby1 since that is the only place this plugin is running from
				List<String> args = new ArrayList<>();
				args.add("Voting");
				args.add(uuid.toString());
				args.add(vote.getUsername());
				args.add(VoteListener.NUM_OF_MATCHES_PER_VOTE + "");
				Gberry.sendGSyncEvent(args);

				Gberry.log("VOTE", vote.getUsername() + " voted, adding matches.");

				// Add ranked matches for player to all the arena lobbies
				BukkitUtil.runTask(new Runnable() {
					@Override
					public void run() {
						VoteManager.addRankedMatchesAndSync(uuid, VoteListener.NUM_OF_MATCHES_PER_VOTE);
					}
				});
			}
		});
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
