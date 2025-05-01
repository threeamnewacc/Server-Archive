package net.badlion.arenavotes.manager;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VoteManager {

	public static void addRankedMatchesAndSync(final UUID uuid, final int amount) {
		// Sync database
		BukkitUtil.runTaskAsync(new Runnable() {
			@Override
			public void run() {
				List<String> args = new ArrayList<>();
				args.add("AddMatches");
				args.add(uuid.toString());
				args.add(amount + "");

				Gberry.sendGSyncEvent(args);

				Connection connection = null;
				try {
					connection = Gberry.getConnection();
					VoteManager.updateRankedMatchesLeftDB(uuid, amount, connection);
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					Gberry.closeComponents(connection);
				}
			}
		});
	}

	private static void updateRankedMatchesLeftDB(UUID uuid, int amount, Connection connection) {
		String query = "UPDATE potion_matches_left_s14 SET num_ranked_left = num_ranked_left + (?) WHERE uuid = ? AND day = ?;";

		PreparedStatement ps = null;

		try {
			ps = connection.prepareStatement(query);

			ps.setInt(1, amount);
			ps.setString(2, uuid.toString());
			ps.setTimestamp(3, new Timestamp(new DateTime(DateTimeZone.UTC).getMillis()));

			Gberry.executeUpdate(connection, ps);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(ps);
		}
	}
}
