package net.badlion.sglobby.managers;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.RatingUtil;
import net.badlion.ministats.managers.DatabaseManager;
import net.badlion.sglobby.FakeSGMiniStatsPlayer;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RatingManager implements Listener {

	private static Map<UUID, Integer> ratings = new ConcurrentHashMap<>();

	private static Map<UUID, FakeSGMiniStatsPlayer> miniStats = new ConcurrentHashMap<>();

	@EventHandler
	public void onAsyncPlayerPreLoginEvent(final AsyncPlayerPreLoginEvent event) {
		String query = "SELECT * FROM sg_ladder_ratings_s2 WHERE uuid = ? AND gamemode = ?;";

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			connection = Gberry.getConnection();

			// Grab ratings from database
			ps = connection.prepareStatement(query);

			// TODO: HARDCODED CLASSIC GAMEMODE HERE, IF WE ADD MORE GAMEMODES IN THE FUTURE
			// TODO: THEN IMPLEMENT THIS TO CACHE ALL RATINGS FOR ALL GAMEMODES

			ps.setString(1, event.getUniqueId().toString());
			ps.setString(2, "classic");

			rs = Gberry.executeQuery(connection, ps);

			if (rs.next()) {
				RatingManager.ratings.put(event.getUniqueId(), rs.getInt("rating"));
			} else {
				RatingManager.ratings.put(event.getUniqueId(), RatingUtil.DEFAULT_RATING);

				// Don't leak connections!!!
				Gberry.closeComponents(rs, ps);

				query = "INSERT INTO sg_ladder_ratings_s2 (gamemode, uuid, rating, wins, losses) VALUES (?, ?, ?, ?, ?);";

				ps = connection.prepareStatement(query);

				ps.setString(1, "classic");
				ps.setString(2, event.getUniqueId().toString());
				ps.setInt(3, RatingUtil.DEFAULT_RATING);
				ps.setInt(4, 0);
				ps.setInt(5, 0);

				Gberry.executeUpdate(connection, ps);
			}

			// Cache ministats
			RatingManager.miniStats.put(event.getUniqueId(), (FakeSGMiniStatsPlayer) DatabaseManager.getPlayerStats(connection, event.getUniqueId()));
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(rs, ps, connection);
		}
	}

	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		int n = RatingManager.getPlayerMiniStats(event.getPlayer().getUniqueId()).getNumberOfGamesPlayed();
		if (n < RatingUtil.SG_PLACEMENT_MATCHES) {
			event.getPlayer().sendMessage(ChatColor.DARK_AQUA + ChatColor.BOLD.toString() + "You are currently unranked, you need to play "
					+ (RatingUtil.SG_PLACEMENT_MATCHES - n) + " more matches to get a rank!");
		}
	}

	public static int getPlayerRating(UUID uuid) {
		// TODO: HARDCODED CLASSIC GAMEMODE HERE, IF WE ADD MORE GAMEMODES IN THE FUTURE
		// TODO: THEN IMPLEMENT THIS TO CACHE ALL RATINGS FOR ALL GAMEMODES

		return RatingManager.ratings.get(uuid);
	}

	public static FakeSGMiniStatsPlayer getPlayerMiniStats(UUID uuid) {
		return RatingManager.miniStats.get(uuid);
	}

}
