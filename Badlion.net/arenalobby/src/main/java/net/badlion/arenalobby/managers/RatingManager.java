package net.badlion.arenalobby.managers;

import net.badlion.arenacommon.ArenaCommon;
import net.badlion.arenalobby.ArenaLobby;
import net.badlion.arenalobby.Group;
import net.badlion.arenalobby.PotPvPPlayer;
import net.badlion.arenalobby.bukkitevents.RatingRetrievedEvent;
import net.badlion.arenalobby.exceptions.NoRatingFoundException;
import net.badlion.arenalobby.ladders.Ladder;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.events.AsyncPlayerJoinEvent;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.Pair;
import net.badlion.gberry.utils.RatingUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

public class RatingManager extends BukkitUtil.Listener {

	public static double DEFAULT_RATING = -1.0;

	private static Map<UUID, ConcurrentHashMap<Ladder, Double>> rankedRatings = new ConcurrentHashMap<>();
	private static Map<UUID, Integer> totalMatchesPlayed = new ConcurrentHashMap<>();

	private static Map<UUID, ConcurrentHashMap<Ladder, Integer>> rankedMatchesPlayed = new ConcurrentHashMap<>();
	private static Map<UUID, ConcurrentHashMap<Ladder, Integer>> unrankedMatchesWon = new ConcurrentHashMap<>();
	private static Map<RatingPair, ConcurrentHashMap<Ladder, Double>> rankedPartyRatings = new ConcurrentHashMap<>();
	private static ConcurrentHashMap<UUID, ConcurrentLinkedQueue<RatingPair>> playerToRatingPairs = new ConcurrentHashMap<>();
	private static Map<UUID, Double> globalRatings = new ConcurrentHashMap<>();

	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent event) {
		// Initialize these for every player
		ConcurrentHashMap<Ladder, Double> ratings = new ConcurrentHashMap<>();
		RatingManager.rankedRatings.put(event.getPlayer().getUniqueId(), ratings);

		ConcurrentHashMap<Ladder, Integer> rankedMatchesPlayed = new ConcurrentHashMap<>();
		RatingManager.rankedMatchesPlayed.put(event.getPlayer().getUniqueId(), rankedMatchesPlayed);

		ConcurrentHashMap<Ladder, Integer> unrankedMatchesWon = new ConcurrentHashMap<>();
		RatingManager.unrankedMatchesWon.put(event.getPlayer().getUniqueId(), unrankedMatchesWon);

		ConcurrentLinkedQueue<RatingPair> ratingPairs = new ConcurrentLinkedQueue<>();
		RatingManager.playerToRatingPairs.put(event.getPlayer().getUniqueId(), ratingPairs);

		Gberry.log("RATING", "Adding rating map for " + event.getPlayer().getName() + " (" + event.getPlayer().getUniqueId().toString() + ")");
	}

	@EventHandler
	public void onPlayerAsyncJoin(final AsyncPlayerJoinEvent event) {
		Runnable runnable = RatingManager.getDBUserRatings(event.getConnection(), event.getUuid());

		event.getRunnables().add(new Runnable() {
			public void run() {
				PotPvPPlayer potPvPPlayer = PotPvPPlayerManager.getPotPvPPlayer(event.getUuid());
				if (potPvPPlayer != null) {
					potPvPPlayer.setRatingsLoaded(true);
				}
			}
		});

		if (runnable != null) {
			event.getRunnables().add(runnable);
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerQuit(PlayerQuitEvent event) {
		// Race condition with setDBGroupRating() for ladder 0
		//final Group group = ArenaLobby.getInstance().getPlayerGroup(event.getPlayer());
		//RatingManager.rankedRatings.remove(group.getLeader().getUniqueId());
		//RatingManager.globalRatings.remove(group.getLeader().getUniqueId());

		RatingManager.rankedRatings.remove(event.getPlayer().getUniqueId());

		RatingManager.totalMatchesPlayed.remove(event.getPlayer().getUniqueId());

		RatingManager.rankedMatchesPlayed.remove(event.getPlayer().getUniqueId());

		RatingManager.unrankedMatchesWon.remove(event.getPlayer().getUniqueId());

		RatingManager.globalRatings.remove(event.getPlayer().getUniqueId());


		ConcurrentLinkedQueue<RatingPair> ratingPairs = RatingManager.playerToRatingPairs.remove(event.getPlayer().getUniqueId());
		for (RatingPair ratingPair : ratingPairs) {
			boolean foundSomeone = false;
			for (UUID uuid : ratingPair.uuids()) {
				if (uuid.equals(event.getPlayer().getUniqueId())) {
					continue;
				}
				Player p = ArenaLobby.getInstance().getServer().getPlayer(uuid);
				if (p != null) {
					foundSomeone = true;
				}
			}

			if (!foundSomeone) {
				RatingManager.rankedPartyRatings.remove(ratingPair);
			}
		}

	}

	public static Pair<Ladder, RatingUtil.Rank> getHighestRank(Player player) {
		if (rankedRatings.get(player.getUniqueId()) != null) {
			Map<Ladder, Double> ladderIntegerMap = rankedRatings.get(player.getUniqueId());
			Map.Entry<Ladder, Double> highestRating = null;
			for (Map.Entry<Ladder, Double> entry : ladderIntegerMap.entrySet()) {
				if (RatingManager.getMatchesPlayed(player.getUniqueId(), entry.getKey()) < RatingUtil.ARENA_PLACEMENT_MATCHES)
					continue;

				if (highestRating != null) {
					if (entry.getValue() > highestRating.getValue()) {
						highestRating = entry;
					}
				} else {
					highestRating = entry;
				}
			}
			if (highestRating != null) {
				return new Pair<>(highestRating.getKey(), RatingUtil.Rank.getRankByElo(highestRating.getValue()));
			}
		}
		return null;
	}

	public static void updateGlobalRating(UUID uuid) {
		ConcurrentHashMap<Ladder, Double> ratings = RatingManager.rankedRatings.get(uuid);

		// Handle global rating
		int totalRating = 0;
		int i = 0;
		for (double r : ratings.values()) {
			totalRating += r;
			i++;
		}

		// -2 for custom and event?
		while (i++ != Ladder.globalRankedLadders) {
			totalRating += RatingManager.DEFAULT_RATING;
		}

		Gberry.log("RATING", "Total rating for " + uuid + " is " + totalRating);
		double globalRating = totalRating / Ladder.globalRankedLadders;
		Gberry.log("RATING", "Global rating for " + uuid + " is " + globalRating);
		RatingManager.globalRatings.put(uuid, globalRating);
	}

	public static double getGroupRating(Group group, Ladder ladder) throws NoRatingFoundException {
		List<Player> players = group.sortedPlayers();
		if (ladder.getLadderType().equals(ArenaCommon.LadderType.RANKED_1V1)) {
			if (RatingManager.getMatchesPlayed(players.get(0).getUniqueId(), ladder) < RatingUtil.ARENA_PLACEMENT_MATCHES) {
				return -1;
			}
		}
		if (players.size() == 1) {
			return RatingManager.getPlayerRating(players.get(0).getUniqueId(), ladder);
		} else if (players.size() != 2 && players.size() != 3 && players.size() != 5) {
			throw new RuntimeException("Asking for rating when a group has more than 2 or 3 or 5 players");
		}

		return RatingManager.getPartyRating(players, ladder);
	}

	/**
	 * MUST CALL THIS ASYNC
	 */
	public static double getDBPlayerGlobalRating(final UUID uuid) {
		if (getTotalRankedMatchesPlayed(uuid) < RatingUtil.ARENA_PLACEMENT_MATCHES) {
			return -1;
		}
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		String query = "SELECT * FROM ladder_ratings_s14 WHERE uuid = ? AND lid = ?;";

		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);

			ps.setString(1, uuid.toString());
			ps.setInt(2, 0);

			rs = Gberry.executeQuery(connection, ps);

			// Get their ratings
			while (rs.next()) {
				return rs.getDouble("mu");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(rs, ps, connection);
		}

		return -1;
	}

	/**
	 * MUST CALL THIS ASYNC
	 */
	public static double getDBPartyGlobalRating(Group group) {
		RatingPair ratingPair = RatingManager.getRatingPair(group.players());

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		String query = "SELECT * FROM ladder_ratings_party_" + group.players().size() + "_s14 WHERE player1 = ? AND player2 = ? AND lid = ?;";

		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);

			ps.setString(1, ratingPair.uuids().get(0).toString());
			ps.setString(2, ratingPair.uuids().get(1).toString());
			ps.setInt(3, 0);

			rs = Gberry.executeQuery(connection, ps);

			// Get their ratings
			while (rs.next()) {
				return rs.getDouble("mu");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(rs, ps, connection);
		}

		return RatingManager.DEFAULT_RATING;
	}

	private static RatingPair getRatingPair(List<Player> players) {
		UUID[] uuids = new UUID[players.size()];
		for (int i = 0; i < uuids.length; i++) {
			uuids[i] = players.get(i).getUniqueId();
		}

		return new RatingPair(uuids);
	}

	public static void deleteRating(UUID uuid, Ladder ladder) {
		ConcurrentHashMap<Ladder, Double> ratings = RatingManager.rankedRatings.get(uuid);
		ratings.remove(ladder);

		// Add default rating
		ratings.put(ladder, RatingManager.DEFAULT_RATING);
	}

	public static double getPlayerRating(UUID uuid, Ladder ladder) throws NoRatingFoundException {
		ConcurrentHashMap<Ladder, Double> ratings = RatingManager.rankedRatings.get(uuid);
		double rating = RatingManager.getRatingCommon(ratings, ladder);
		Gberry.log("RATING", "Retrieved rating " + rating + " for " + ladder.toString() + " for UUID " + uuid.toString());
		return rating;
	}

	public static double getPartyRating(List<Player> players, Ladder ladder) throws NoRatingFoundException {
		RatingPair ratingPair = RatingManager.getRatingPair(players);

		ConcurrentHashMap<Ladder, Double> ratings = RatingManager.rankedPartyRatings.get(ratingPair);
		return RatingManager.getRatingCommon(ratings, ladder);
	}

	/**
	 * Needs to be called ASYNC
	 */
	private static Runnable getDBUserRatings(Connection connection, final UUID uuid) {
		String query = "SELECT * FROM ladder_ratings_s14 WHERE uuid = ?;";

		ResultSet rs = null;
		PreparedStatement ps = null;

		double globalRating = RatingManager.DEFAULT_RATING;
		final Map<Ladder, Double> ratings = RatingManager.rankedRatings.get(uuid);

		if (ratings == null) return null;

		int totalRankedMatchesPlayed = 0;
		int totalMatches = 0;

		// First get their yolo queue ratings
		try {
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
				int unrankedWins = rs.getInt("unranked_wins");
				int unrankedLosses = rs.getInt("unranked_losses");

				totalMatches += rankedWins + rankedLosses + unrankedWins + unrankedLosses;

				// Skip ratings if they have not played ranked matches yet or if the ranked ladder doesn't exist
				if (rankedWins + rankedLosses == 0) {
					continue;
				}
				if (LadderManager.getLadder(rs.getInt("lid"), ArenaCommon.LadderType.RANKED_1V1) == null) {
					continue;
				}

				Gberry.log("RATING", "Adding rating " + rs.getDouble("mu") + " for ladder " +
						LadderManager.getLadder(rs.getInt("lid"), ArenaCommon.LadderType.RANKED_1V1).toString() +
						" with UUID " + uuid.toString());

				ratings.put(LadderManager.getLadder(rs.getInt("lid"), ArenaCommon.LadderType.RANKED_1V1), rs.getDouble("mu"));


				totalRankedMatchesPlayed += rankedWins;
				totalRankedMatchesPlayed += rankedLosses;

				RatingManager.rankedMatchesPlayed.get(uuid).put(LadderManager.getLadder(rs.getInt("lid"), ArenaCommon.LadderType.RANKED_1V1), rankedWins + rankedLosses);
				RatingManager.unrankedMatchesWon.get(uuid).put(LadderManager.getLadder(rs.getInt("lid"), ArenaCommon.LadderType.UNRANKED_1V1), unrankedWins);
			}

			// Go through and add default ratings
			for (Ladder ladder : LadderManager.getLadderMap(ArenaCommon.LadderType.RANKED_1V1).values()) {
				if (ratings.get(ladder) == null) {
					ratings.put(ladder, RatingManager.DEFAULT_RATING);
				}
			}

			// Next get all of their 2v2 ratings
			query = "SELECT * FROM ladder_ratings_party_2_s14 WHERE player1 = ? OR player2 = ?;";

			ps = connection.prepareStatement(query);

			ps.setString(1, uuid.toString());
			ps.setString(2, uuid.toString());

			rs = Gberry.executeQuery(connection, ps);

			// Store their ratings
			while (rs.next()) {
				if (rs.getInt("lid") == 0) {
					continue;
				}

				RatingPair ratingPair = new RatingPair(UUID.fromString(rs.getString("player1")), UUID.fromString(rs.getString("player2")));

				// Make a new rating party entry if we don't have one already
				ConcurrentHashMap<Ladder, Double> map = RatingManager.rankedPartyRatings.get(ratingPair);
				if (map == null) {
					map = new ConcurrentHashMap<>();
					RatingManager.rankedPartyRatings.put(ratingPair, map);
				}

				Gberry.log("RATING", "Adding rating " + rs.getDouble("mu") + " for ladder " +
						LadderManager.getLadder(rs.getInt("lid"), ArenaCommon.LadderType.RANKED_2V2).toString() +
						" with UUIDs " + rs.getString("player1") + " AND " + rs.getString("player2"));

				map.put(LadderManager.getLadder(rs.getInt("lid"), ArenaCommon.LadderType.RANKED_2V2), rs.getDouble("mu"));

				// Store the rating pair for this UUID if it isn't already stored
				ConcurrentLinkedQueue<RatingPair> ratingPairs = RatingManager.playerToRatingPairs.get(uuid);
				if (ratingPairs != null) {
					if (!ratingPairs.contains(ratingPair)) {
						ratingPairs.add(ratingPair);
					}
				}
			}

			// Next get all of their 3v3 ratings
			query = "SELECT * FROM ladder_ratings_party_3_s14 WHERE player1 = ? OR player2 = ? OR player3 = ?;";

			ps = connection.prepareStatement(query);

			ps.setString(1, uuid.toString());
			ps.setString(2, uuid.toString());
			ps.setString(3, uuid.toString());

			rs = Gberry.executeQuery(connection, ps);

			// Store their ratings
			while (rs.next()) {
				RatingPair ratingPair = new RatingPair(UUID.fromString(rs.getString("player1")), UUID.fromString(rs.getString("player2")),
						UUID.fromString(rs.getString("player3")));

				// Make a new rating party entry if we don't have one already
				ConcurrentHashMap<Ladder, Double> map = RatingManager.rankedPartyRatings.get(ratingPair);
				if (map == null) {
					map = new ConcurrentHashMap<>();
					RatingManager.rankedPartyRatings.put(ratingPair, map);
				}

				Bukkit.getLogger().log(Level.INFO, "Ladder DEBUG: " + rs.getInt("lid"));
				Gberry.log("RATING", "Adding rating " + rs.getDouble("mu") + " for ladder " +
						LadderManager.getLadder(rs.getInt("lid"), ArenaCommon.LadderType.RANKED_3V3).toString() +
						" with UUIDs " + rs.getString("player1") + " AND " + rs.getString("player2") + " AND " + rs.getString("player3"));

				map.put(LadderManager.getLadder(rs.getInt("lid"), ArenaCommon.LadderType.RANKED_3V3), rs.getDouble("mu"));

				// Store the rating pair for this UUID if it isn't already stored
				ConcurrentLinkedQueue<RatingPair> ratingPairs = RatingManager.playerToRatingPairs.get(uuid);
				if (ratingPairs != null) {
					if (!ratingPairs.contains(ratingPair)) {
						ratingPairs.add(ratingPair);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(rs, ps);
		}

		final double globalRatingFinal = totalRankedMatchesPlayed < RatingUtil.ARENA_PLACEMENT_MATCHES ? -1 : globalRating;

		RatingManager.totalMatchesPlayed.put(uuid, totalMatches);


		return new Runnable() {
			@Override
			public void run() {
				ArenaLobby.getInstance().getServer().getPluginManager().callEvent(
						new RatingRetrievedEvent(uuid, globalRatingFinal, (ConcurrentHashMap<Ladder, Double>) ratings));
			}
		};
	}

	public static ConcurrentHashMap<Ladder, Integer> getMatchesPlayed(UUID playerId) {
		return RatingManager.rankedMatchesPlayed.get(playerId);
	}

	public static int getMatchesPlayed(UUID playerId, Ladder ladder) {
		ConcurrentHashMap<Ladder, Integer> map = RatingManager.rankedMatchesPlayed.get(playerId);
		if (map.containsKey(ladder)) {
			return map.get(ladder);
		} else {
			return 0;
		}
	}


	public static int getTotalMatchesPlayed(UUID playerId) {
		if (RatingManager.totalMatchesPlayed.containsKey(playerId)) {
			return RatingManager.totalMatchesPlayed.get(playerId);
		} else {
			return 0;
		}
	}

	public static int getTotalRankedMatchesPlayed(UUID playerId) {
		ConcurrentHashMap<Ladder, Integer> map = RatingManager.rankedMatchesPlayed.get(playerId);

		if (map != null) {
			int total = 0;
			for (Integer amount : map.values()) {
				total += amount;
			}

			return total;
		} else {
			return 0;
		}
	}

	public static int getTotalUnrankedWins(UUID playerId) {
		ConcurrentHashMap<Ladder, Integer> map = RatingManager.unrankedMatchesWon.get(playerId);

		if (map != null) {
			int total = 0;
			for (Integer amount : map.values()) {
				total += amount;
			}

			return total;
		} else {
			return 0;
		}
	}

	private static double getRatingCommon(ConcurrentHashMap<Ladder, Double> ratings, Ladder ladder) throws NoRatingFoundException {
		if (ratings == null) {
			throw new NoRatingFoundException();
		}

		Double rating = ratings.get(ladder);
		if (rating == null) {
			throw new NoRatingFoundException();
		}

		return rating;
	}

	private static class RatingPair {

		private List<UUID> uuidsSorted = new ArrayList<>();

		public RatingPair(UUID... uuids) {
			for (UUID uuid : uuids) {
				this.uuidsSorted.add(uuid);
			}

			Collections.sort(this.uuidsSorted);
		}

		public List<UUID> uuids() {
			return Collections.unmodifiableList(this.uuidsSorted);
		}

		@Override
		public boolean equals(Object other) {
			if (other == null) {
				return false;
			} else if (!(other instanceof RatingPair)) {
				return false;
			}

			RatingPair ratingPair = (RatingPair) other;

			for (UUID otherUUID : ratingPair.uuids()) {
				if (!this.uuidsSorted.contains(otherUUID)) {
					return false;
				}
			}

			List<UUID> otherUUIDs = ratingPair.uuids();
			for (UUID uuid : this.uuidsSorted) {
				if (!otherUUIDs.contains(uuid)) {
					return false;
				}
			}

			return true;
		}

		@Override
		public int hashCode() {
			int hashcode = this.uuidsSorted.get(0).hashCode();
			for (int i = 1; i < this.uuidsSorted.size(); i++) {
				hashcode = hashcode ^ this.uuidsSorted.get(i).hashCode();
			}

			return hashcode;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			for (UUID uuid : this.uuidsSorted) {
				builder.append(uuid.toString());
				builder.append(", ");
			}

			return builder.substring(0, builder.length() - 2);
		}

	}

}
