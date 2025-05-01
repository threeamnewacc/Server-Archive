package net.badlion.sgrankedmatchmaker.managers;

import net.badlion.gberry.Gberry;
import net.badlion.sgrankedmatchmaker.SGRankedMatchMaker;
import net.badlion.sgrankedmatchmaker.bukkitevents.RankedLeftChangeEvent;
import net.badlion.gberry.utils.BukkitUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RankedLeftManager implements Listener {

	public static int DEFAULT_MAX_NUM_OF_RANKED_MATCHES_PER_DAY = 5;
	private static ConcurrentHashMap<UUID, Integer> matchesToday = new ConcurrentHashMap<>();

	@EventHandler(priority = EventPriority.LAST)
	public void playerLoginEvent(final PlayerLoginEvent event) {
		BukkitUtil.runTaskAsync(new Runnable() {
			@Override
			public void run() {
				final int numOfMatchesToday = RankedLeftManager.getNumOfPlayerRankedMatchesToday(event.getPlayer().getUniqueId());
				RankedLeftManager.addToMatchesTodayMap(event.getPlayer().getUniqueId(), numOfMatchesToday);

				/*BukkitUtil.runTask(new Runnable() {
					@Override
					public void run() {
						if (!event.getPlayer().hasPermission("SurvivalGames.unlimitedranked") && numOfMatchesToday >= RankedLeftManager.DEFAULT_MAX_NUM_OF_RANKED_MATCHES_PER_DAY) {
							event.getPlayer().kickPlayer("Out of ranked matches. You can get more by donating or voting at http://www.badlion.net/");
						}
					}
				});*/

			}
		});
	}

	public static Integer getNumberOfRankedMatchesToday(UUID uuid) {
		return RankedLeftManager.matchesToday.get(uuid);
	}

	public static void updateOrInsertNumOfGames(final Player player) {
		final int rankedLeft = RankedLeftManager.getNumberOfRankedMatchesToday(player.getUniqueId());
		if (RankedLeftManager.matchesToday.containsKey(player.getUniqueId())) {
			RankedLeftManager.matchesToday.put(player.getUniqueId(), rankedLeft + 1);
		} else {
			RankedLeftManager.matchesToday.put(player.getUniqueId(), 1);
		}

		RankedLeftManager.updateOrInsertNumOfGamesDB(player);

		BukkitUtil.runTask(new Runnable() {
			@Override
			public void run() {
				// Fire off an event for our wonderful le SmerryPrengruin
				RankedLeftChangeEvent event = new RankedLeftChangeEvent(player, RankedLeftManager.DEFAULT_MAX_NUM_OF_RANKED_MATCHES_PER_DAY - rankedLeft - 1,
						rankedLeft < RankedLeftManager.DEFAULT_MAX_NUM_OF_RANKED_MATCHES_PER_DAY);
				SGRankedMatchMaker.getInstance().getServer().getPluginManager().callEvent(event);
			}
		});
	}

	public static void removeNumberOfMatches(UUID uuid, int num) {
		if (RankedLeftManager.matchesToday.containsKey(uuid)) {
			RankedLeftManager.matchesToday.put(uuid, RankedLeftManager.matchesToday.get(uuid) - num);
		} else {
			RankedLeftManager.matchesToday.put(uuid, -num);
		}
	}

	public static int getNumOfPlayerRankedMatchesToday(UUID uuid) {
		int num = 0;

		Connection connection = null;
		ResultSet rs = null;
		PreparedStatement ps = null;

		try {
			connection = Gberry.getConnection();
			String query = "SELECT * FROM sg_user_num_of_ranked WHERE uuid = ? AND day = ?;";
			ps = connection.prepareStatement(query);
			ps.setString(1, uuid.toString());
			Date today = new Date();
			ps.setDate(2, new java.sql.Date(today.getTime()));
			rs = Gberry.executeQuery(connection, ps);

			if (rs.next()) {
				return rs.getInt("num_of_matches");
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}

		return num;
	}

	private static void updateOrInsertNumOfGamesDB(Player player) {
		String query = "UPDATE sg_user_num_of_ranked SET num_of_matches = num_of_matches + 1 WHERE uuid = ? AND day = ?;\n";
		query += "INSERT INTO sg_user_num_of_ranked (uuid, num_of_matches, day) SELECT ?, ?, ? WHERE NOT EXISTS " +
				"(SELECT 1 FROM sg_user_num_of_ranked WHERE uuid = ? AND day = ?);";

		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			Date today = new Date();

			ps.setString(1, player.getUniqueId().toString());
			ps.setDate(2, new java.sql.Date(today.getTime()));
			ps.setString(3, player.getUniqueId().toString());
			ps.setInt(4, 1); // start off with 1 match, increments are handled automatically
			ps.setDate(5, new java.sql.Date(today.getTime()));
			ps.setString(6, player.getUniqueId().toString());
			ps.setDate(7, new java.sql.Date(today.getTime()));

			Gberry.executeUpdate(connection, ps);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}
	}

	public static void addToMatchesTodayMap(UUID uuid, int matches) {
		RankedLeftManager.matchesToday.put(uuid, matches);
	}

}
