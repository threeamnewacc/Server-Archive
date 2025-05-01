package net.badlion.arenapvp.manager;

import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.RatingUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;


public class RatingManager implements Listener {

	private static Map<UUID, ConcurrentHashMap<Integer, Double>> rankedRatings = new ConcurrentHashMap<>();

	private static Map<UUID, Double> globalRatings = new ConcurrentHashMap<>();

	// Store a list of the active 1v1 ranked ladders for the /stats inventory since arena lobby is the one that registers those.
	public static Set<KitRuleSet> rankedKitLadders = new HashSet<>();

	static {
		rankedKitLadders.add(KitRuleSet.archerRuleSet);
		rankedKitLadders.add(KitRuleSet.buildUHCRuleSet);
		rankedKitLadders.add(KitRuleSet.diamondOCNRuleSet);
		rankedKitLadders.add(KitRuleSet.godAppleRuleSet);
		rankedKitLadders.add(KitRuleSet.ironOCNRuleSet);
		rankedKitLadders.add(KitRuleSet.ironSoupRuleSet);
		rankedKitLadders.add(KitRuleSet.kohiRuleSet);
		rankedKitLadders.add(KitRuleSet.noDebuffRuleSet);
		rankedKitLadders.add(KitRuleSet.sgRuleSet);
		rankedKitLadders.add(KitRuleSet.uhcRuleSet);
		rankedKitLadders.add(KitRuleSet.vanillaRuleSet);
		rankedKitLadders.add(KitRuleSet.horseRuleSet);
	}


	public static double DEFAULT_RATING = -1.0;


	public static Map<Integer, Double> getRatings(Player player) {
		if (RatingManager.rankedRatings.get(player.getUniqueId()) != null) {
			return RatingManager.rankedRatings.get(player.getUniqueId());
		}
		return null;
	}

	public static Double getGlobalRating(Player player) {
		if (globalRatings.containsKey(player.getUniqueId())) {
			return globalRatings.get(player.getUniqueId());
		}
		return RatingManager.DEFAULT_RATING;
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		RatingManager.rankedRatings.remove(player.getUniqueId());
		RatingManager.globalRatings.remove(player.getUniqueId());
	}


	/**
	 * Needs to be called ASYNC
	 */
	public static void getAllDBUserRatings(final UUID uuid) {
		Bukkit.getLogger().log(Level.INFO, "Fetching All ratings for " + uuid.toString());
		String query = "SELECT * FROM ladder_ratings_s14 WHERE uuid = ?;";

		ResultSet rs = null;
		PreparedStatement ps = null;
		Connection connection = null;

		double globalRating = -1;

		if (RatingManager.rankedRatings.get(uuid) == null) {
			RatingManager.rankedRatings.put(uuid, new ConcurrentHashMap<>());
		}

		final Map<Integer, Double> ratings = RatingManager.rankedRatings.get(uuid);

		for (KitRuleSet kitRuleSet : rankedKitLadders) {
			ratings.put(kitRuleSet.getId(), RatingManager.DEFAULT_RATING);
		}

		int totalRankedMatchesPlayed = 0;

		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, uuid.toString());
			rs = Gberry.executeQuery(connection, ps);

			// Store their ratings
			while (rs.next()) {
				if (rs.getInt("lid") == 0) {
					globalRating = rs.getDouble("mu");
					continue;
				}

				int rankedWins = rs.getInt("ranked_wins");
				int rankedLosses = rs.getInt("ranked_losses");


				// Skip ratings if they have not played ranked matches yet or if the ranked ladder doesn't exist
				if (rankedWins + rankedLosses == 0) {
					continue;
				}
				boolean activeLadder = false;
				for (KitRuleSet kitRuleSet : rankedKitLadders) {
					if (kitRuleSet.getId() == rs.getInt("lid")) {
						activeLadder = true;
						break;
					}
				}

				if (!activeLadder) {
					continue;
				}

				Gberry.log("RATING", "Adding rating " + rs.getDouble("mu") + " for ladder " + KitRuleSet.getKitRuleSet(rs.getInt("lid")) + " with UUID " + uuid.toString() + " wins: " + rankedWins + " loss: " + rankedLosses);

				if (rankedWins + rankedLosses < RatingUtil.ARENA_PLACEMENT_MATCHES) {
					ratings.put(rs.getInt("lid"), RatingManager.DEFAULT_RATING);
				} else {
					ratings.put(rs.getInt("lid"), rs.getDouble("mu"));
				}

				totalRankedMatchesPlayed += rankedWins;
				totalRankedMatchesPlayed += rankedLosses;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(rs, ps, connection);
		}

		final double globalRatingFinal = totalRankedMatchesPlayed < RatingUtil.ARENA_PLACEMENT_MATCHES ? RatingManager.DEFAULT_RATING : globalRating;

		RatingManager.globalRatings.put(uuid, globalRatingFinal);
	}


	/**
	 * Needs to be called ASYNC
	 */
	public static Double getDBUserRatings(Connection connection, final UUID uuid, int ladderId) {
		String query = "SELECT * FROM ladder_ratings_s14 WHERE uuid = ? AND lid = ?;";

		ResultSet rs = null;
		PreparedStatement ps = null;

		// First get their yolo queue ratings
		try {
			ps = connection.prepareStatement(query);
			ps.setString(1, uuid.toString());
			ps.setInt(2, ladderId);
			rs = Gberry.executeQuery(connection, ps);

			// Store their ratings
			while (rs.next()) {
				if (rs.getInt("lid") == 0) {
					continue;
				}
				int wins = rs.getInt("ranked_wins");
				int losses = rs.getInt("ranked_losses");
				if (wins + losses < RatingUtil.ARENA_PLACEMENT_MATCHES) {
					return RatingManager.DEFAULT_RATING;
				}
				return rs.getDouble("mu");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(rs, ps, connection);
		}
		return RatingManager.DEFAULT_RATING;
	}

	public static void addMatchPlayedForRegion(UUID uuid, Connection connection) {
		String query = "UPDATE matches_played_region_s14 SET matches = matches + (?) WHERE uuid = ? AND region = ?;\n";
		query += "INSERT INTO matches_played_region_s14 (uuid, region, matches) SELECT ?, ?, ? WHERE NOT EXISTS " +
				"(SELECT 1 FROM matches_played_region_s14 WHERE uuid = ? AND region = ?);";
		PreparedStatement ps = null;

		try {
			ps = connection.prepareStatement(query);

			ps.setInt(1, 1);
			ps.setString(2, uuid.toString());
			ps.setString(3, Gberry.serverRegion.toString().toLowerCase());

			ps.setString(4, uuid.toString());
			ps.setString(5, Gberry.serverRegion.toString().toLowerCase());
			ps.setInt(6, 1);

			ps.setString(7, uuid.toString());
			ps.setString(8, Gberry.serverRegion.toString().toLowerCase());
			Gberry.executeUpdate(connection, ps);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(ps);
		}
	}
}
