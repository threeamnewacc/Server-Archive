package net.badlion.potpvp.managers;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.events.AsyncDelayedPlayerJoinEvent;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.PotPvPPlayer;
import net.badlion.potpvp.bukkitevents.RatingRetrievedEvent;
import net.badlion.potpvp.exceptions.NoRatingFoundException;
import net.badlion.potpvp.ladders.Ladder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RatingManager extends BukkitUtil.Listener {

    public static int DEFAULT_RATING = 1400;

    private static Map<UUID, ConcurrentHashMap<Ladder, Integer>> rankedRatings = new ConcurrentHashMap<>();
    private static Map<RatingPair, ConcurrentHashMap<Ladder, Integer>> rankedPartyRatings = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<UUID, ConcurrentLinkedQueue<RatingPair>> playerToRatingPairs = new ConcurrentHashMap<>();
    private static Map<UUID, Integer> globalRatings = new ConcurrentHashMap<>();

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        // Initialize these for every player
        ConcurrentHashMap<Ladder, Integer> ratings = new ConcurrentHashMap<>();
        RatingManager.rankedRatings.put(event.getPlayer().getUniqueId(), ratings);
        ConcurrentLinkedQueue<RatingPair> ratingPairs = new ConcurrentLinkedQueue<>();
        RatingManager.playerToRatingPairs.put(event.getPlayer().getUniqueId(), ratingPairs);

        Gberry.log("RATING", "Adding rating map for " + event.getPlayer().getName() + " (" + event.getPlayer().getUniqueId().toString() + ")");
    }

    @EventHandler
    public void onPlayerAsyncJoinDelayed(final AsyncDelayedPlayerJoinEvent event) {
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

    @EventHandler(priority=EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Race condition with setDBGroupRating() for ladder 0
        //final Group group = PotPvP.getInstance().getPlayerGroup(event.getPlayer());
        //RatingManager.rankedRatings.remove(group.getLeader().getUniqueId());
        //RatingManager.globalRatings.remove(group.getLeader().getUniqueId());

        ConcurrentLinkedQueue<RatingPair> ratingPairs = RatingManager.playerToRatingPairs.remove(event.getPlayer().getUniqueId());
        for (RatingPair ratingPair : ratingPairs) {
            boolean foundSomeone = false;
            for (UUID uuid : ratingPair.uuids()) {
                Player p = PotPvP.getInstance().getServer().getPlayer(uuid);
                if (p != null) {
                    foundSomeone = true;
                }
            }

            if (!foundSomeone) {
                RatingManager.rankedPartyRatings.remove(ratingPair);
            }
        }
    }

    public static void updateGlobalRating(UUID uuid) {
        ConcurrentHashMap<Ladder, Integer> ratings = RatingManager.rankedRatings.get(uuid);

        // Handle global rating
        int totalRating = 0;
        int i = 0;
        for (Integer r : ratings.values()) {
            totalRating += r;
            i++;
        }

        // -2 for custom and event?
        while (i++ != Ladder.globalRankedLadders) {
            totalRating += RatingManager.DEFAULT_RATING;
        }

        Gberry.log("RATING", "Total rating for " + uuid + " is " + totalRating);
        int globalRating = totalRating / Ladder.globalRankedLadders;
        Gberry.log("RATING", "Global rating for " + uuid + " is " + globalRating);
        RatingManager.globalRatings.put(uuid, globalRating);
    }

    private static void handle2GlobalRating(RatingPair ratingPair, Connection connection, Group group, double winOrLoss) {
        if (ratingPair == null) {
            throw new RuntimeException("Why do we have something null here");
        }

        ResultSet rs = null;
        PreparedStatement ps = null;

        String query = "SELECT * FROM ladder_ratings_party_2_s12" + PotPvP.getInstance().getDBExtra() + " WHERE player1 = ? AND player2 = ?;";

        Map<Ladder, Integer> ratings = new HashMap<>();

        // First get their yolo queue ratings
        try {
            ps = connection.prepareStatement(query);

            ps.setString(1, ratingPair.uuids().get(0).toString());
            ps.setString(2, ratingPair.uuids().get(1).toString());

            rs = Gberry.executeQuery(connection, ps);

            // Get their ratings
            while (rs.next()) {
                // Skip global ladder id
                if (rs.getInt("lid") == 0) {
                    continue;
                }

                ratings.put(Ladder.getLadder(rs.getInt("lid"), Ladder.LadderType.TwoVsTwoRanked), rs.getInt("rating"));
            }

            // Handle global rating
            int totalRating = 0;
            int i = 0;
            for (Integer r : ratings.values()) {
                totalRating += r;
                i++;
            }

            // -2 for custom and event?
            while (i++ != Ladder.globalRankedLadders) {
                totalRating += RatingManager.DEFAULT_RATING;
            }

            Gberry.log("RATING", "Total rating for " + ratingPair.toString() + " is " + totalRating);
            int globalRating = totalRating / Ladder.globalRankedLadders;
            RatingManager.setDBGroupRating(group, 0, globalRating, winOrLoss, connection);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(rs, ps);
        }
    }

	public static int getGroupRating(Group group, Ladder ladder) throws NoRatingFoundException {
		List<Player> players = group.sortedPlayers();
		if (players.size() == 1) {
			return RatingManager.getPlayerRating(players.get(0).getUniqueId(), ladder);
		}  else if (players.size() != 2 && players.size() != 3 && players.size() != 5) {
			throw new RuntimeException("Asking for rating when a group has more than 2 or 3 or 5 players");
		}

		return RatingManager.getPartyRating(players, ladder);
	}

	/**
	 * MUST CALL THIS ASYNC
	 */
	public static int getDBPlayerGlobalRating(final UUID uuid) {
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		String query = "SELECT * FROM ladder_ratings_s12" + PotPvP.getInstance().getDBExtra() + " WHERE uuid = ? AND lid = ?;";

		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);

			ps.setString(1, uuid.toString());
			ps.setInt(2, 0);

			rs = Gberry.executeQuery(connection, ps);

			// Get their ratings
			while (rs.next()) {
				return rs.getInt("rating");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(rs, ps, connection);
		}

		return RatingManager.DEFAULT_RATING;
	}

	/**
	 * MUST CALL THIS ASYNC
	 */
	public static int getDBPartyGlobalRating(Group group) {
		RatingPair ratingPair = RatingManager.getRatingPair(group.players());

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		String query = "SELECT * FROM ladder_ratings_party_" + group.players().size() + "_s12" + PotPvP.getInstance().getDBExtra() + " WHERE player1 = ? AND player2 = ? AND lid = ?;";

		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);

			ps.setString(1, ratingPair.uuids().get(0).toString());
			ps.setString(2, ratingPair.uuids().get(1).toString());
			ps.setInt(3, 0);

			rs = Gberry.executeQuery(connection, ps);

			// Get their ratings
			while (rs.next()) {
				return rs.getInt("rating");
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

    /**
     * Method must be called async
     */
    public static void setGroupRating(final Group group, final Ladder ladder, final int rating, final double winOrLoss, Connection connection) {
        RatingPair ratingPair = null;
        ConcurrentHashMap<Ladder, Integer> ratings = null;
        if (!group.isParty()) {
            ratings = RatingManager.rankedRatings.get(group.getLeader().getUniqueId());
        } else if (group.players().size() != 2 && group.players().size() != 3 && group.players().size() != 5) {
            throw new RuntimeException("Asking for rating when a group has more than 2 or 3 or 5 players");
        } else {
            ratingPair = RatingManager.getRatingPair(group.players());

            // Make a new ratingpair if we don't have one
            ratings = RatingManager.rankedPartyRatings.get(ratingPair);
            if (ratings == null) {
                ratings = new ConcurrentHashMap<>();
                RatingManager.rankedPartyRatings.put(ratingPair, ratings);
            }
        }

        // Update correct map (possible they logged off already)
        if (ratings != null) {
            Gberry.log("RATING", "Adding rating " + rating + " to map for " + ladder.toString() + " for leader " + group.getLeader().getUniqueId().toString());
            ratings.put(ladder, rating);

            if (!group.isParty()) {
                RatingManager.updateGlobalRating(group.getLeader().getUniqueId());
            }
        }

        // Update DB
        if (!group.isParty()) {
            RatingManager.setDBGroupRating(group, 0, RatingManager.globalRatings.get(group.getLeader().getUniqueId()), winOrLoss, connection);
        } else if (group.players().size() == 2) {
            // Set's the global rating to 0
            RatingManager.handle2GlobalRating(ratingPair, connection, group, winOrLoss);
        }

        RatingManager.setDBGroupRating(group, ladder.getLadderId(), rating, winOrLoss, connection);
    }

    public static void deleteRating(UUID uuid, Ladder ladder) {
        ConcurrentHashMap<Ladder, Integer> ratings = RatingManager.rankedRatings.get(uuid);
        ratings.remove(ladder);

	    // Add default rating
	    ratings.put(ladder, RatingManager.DEFAULT_RATING);
    }

    public static int getPlayerRating(UUID uuid, Ladder ladder) throws NoRatingFoundException {
        ConcurrentHashMap<Ladder, Integer> ratings = RatingManager.rankedRatings.get(uuid);
        int rating = RatingManager.getRatingCommon(ratings, ladder);
        Gberry.log("RATING", "Retrieved rating " + rating + " for " + ladder.toString() + " for UUID " + uuid.toString());
        return rating;
    }

    public static int getPartyRating(List<Player> players, Ladder ladder) throws NoRatingFoundException {
        RatingPair ratingPair = RatingManager.getRatingPair(players);

        ConcurrentHashMap<Ladder, Integer> ratings = RatingManager.rankedPartyRatings.get(ratingPair);
        return RatingManager.getRatingCommon(ratings, ladder);
    }

    /**
     * Needs to be called ASYNC
     */
    private static Runnable getDBUserRatings(Connection connection, final UUID uuid) {
        String query = "SELECT * FROM ladder_ratings_s12" + PotPvP.getInstance().getDBExtra() + " WHERE uuid = ?;";

        ResultSet rs = null;
        PreparedStatement ps = null;

        final Map<Ladder, Integer> ratings = RatingManager.rankedRatings.get(uuid);

        if (ratings == null) return null;

        // First get their yolo queue ratings
        try {
            ps = connection.prepareStatement(query);
            ps.setString(1, uuid.toString());
            rs = Gberry.executeQuery(connection, ps);

            // Store their ratings
            while (rs.next()) {
                if (rs.getInt("lid") == 0) {
                    continue;
                }

                Gberry.log("RATING", "Adding rating " + rs.getInt("rating") + " for ladder " +
                                             Ladder.getLadder(rs.getInt("lid"), Ladder.LadderType.OneVsOneRanked).toString() +
                                             " with UUID " + uuid.toString());

                ratings.put(Ladder.getLadder(rs.getInt("lid"), Ladder.LadderType.OneVsOneRanked), rs.getInt("rating"));
            }

            // Go through and add default ratings
            for (Ladder ladder : Ladder.getLadderMap(Ladder.LadderType.OneVsOneRanked).values()) {
                if (ratings.get(ladder) == null) {
                    ratings.put(ladder, RatingManager.DEFAULT_RATING);
                }
            }

            // Next get all of their 2v2 ratings
            query = "SELECT * FROM ladder_ratings_party_2_s12" + PotPvP.getInstance().getDBExtra() + " WHERE player1 = ? OR player2 = ?;";

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
                ConcurrentHashMap<Ladder, Integer> map = RatingManager.rankedPartyRatings.get(ratingPair);
                if (map == null) {
                    map = new ConcurrentHashMap<>();
                    RatingManager.rankedPartyRatings.put(ratingPair, map);
                }

                Gberry.log("RATING", "Adding rating " + rs.getInt("rating") + " for ladder " +
                                             Ladder.getLadder(rs.getInt("lid"), Ladder.LadderType.TwoVsTwoRanked).toString() +
                                             " with UUIDs " + rs.getString("player1") + " AND " + rs.getString("player2"));

                map.put(Ladder.getLadder(rs.getInt("lid"), Ladder.LadderType.TwoVsTwoRanked), rs.getInt("rating"));

                // Store the rating pair for this UUID if it isn't already stored
                ConcurrentLinkedQueue<RatingPair> ratingPairs = RatingManager.playerToRatingPairs.get(uuid);
                if (ratingPairs != null) {
                    if (!ratingPairs.contains(ratingPair)) {
                        ratingPairs.add(ratingPair);
                    }
                }
            }

            // Next get all of their 3v3 ratings
            query = "SELECT * FROM ladder_ratings_party_3_s12" + PotPvP.getInstance().getDBExtra() + " WHERE player1 = ? OR player2 = ? OR player3 = ?;";

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
                ConcurrentHashMap<Ladder, Integer> map = RatingManager.rankedPartyRatings.get(ratingPair);
                if (map == null) {
                    map = new ConcurrentHashMap<>();
                    RatingManager.rankedPartyRatings.put(ratingPair, map);
                }

                Gberry.log("RATING", "Adding rating " + rs.getInt("rating") + " for ladder " +
                                             Ladder.getLadder(rs.getInt("lid"), Ladder.LadderType.ThreeVsThreeRanked).toString() +
                                             " with UUIDs " + rs.getString("player1") + " AND " + rs.getString("player2") + " AND " + rs.getString("player3"));

                map.put(Ladder.getLadder(rs.getInt("lid"), Ladder.LadderType.ThreeVsThreeRanked), rs.getInt("rating"));

                // Store the rating pair for this UUID if it isn't already stored
                ConcurrentLinkedQueue<RatingPair> ratingPairs = RatingManager.playerToRatingPairs.get(uuid);
                if (ratingPairs != null) {
                    if (!ratingPairs.contains(ratingPair)) {
                        ratingPairs.add(ratingPair);
                    }
                }
            }

            query = "SELECT * FROM ladder_ratings_party_5_s12" + PotPvP.getInstance().getDBExtra() + " WHERE player1 = ? OR player2 = ? OR player3 = ? OR player4 = ? OR player5 = ?;";

            ps = connection.prepareStatement(query);

            ps.setString(1, uuid.toString());
            ps.setString(2, uuid.toString());
            ps.setString(3, uuid.toString());
            ps.setString(4, uuid.toString());
            ps.setString(5, uuid.toString());

            rs = Gberry.executeQuery(connection, ps);

            // Store their ratings
            while (rs.next()) {
                RatingPair ratingPair = new RatingPair(UUID.fromString(rs.getString("player1")), UUID.fromString(rs.getString("player2")),
                                                       UUID.fromString(rs.getString("player3")), UUID.fromString(rs.getString("player4")),
                                                       UUID.fromString(rs.getString("player5")));

                // Make a new rating party entry if we don't have one already
                ConcurrentHashMap<Ladder, Integer> map = RatingManager.rankedPartyRatings.get(ratingPair);
                if (map == null) {
                    map = new ConcurrentHashMap<>();
                    RatingManager.rankedPartyRatings.put(ratingPair, map);
                }

                Gberry.log("RATING", "Adding rating " + rs.getInt("rating") + " for ladder " +
                                             Ladder.getLadder(rs.getInt("lid"), Ladder.LadderType.FiveVsFiveRanked).toString() +
                                             " with UUIDs " + ratingPair.toString());

                map.put(Ladder.getLadder(rs.getInt("lid"), Ladder.LadderType.FiveVsFiveRanked), rs.getInt("rating"));

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

        return new Runnable() {
            @Override
            public void run() {
                PotPvP.getInstance().getServer().getPluginManager().callEvent(new RatingRetrievedEvent(uuid, (ConcurrentHashMap<Ladder, Integer>) ratings));
            }
        };
    }

    private static int getRatingCommon(ConcurrentHashMap<Ladder, Integer> ratings, Ladder ladder) throws NoRatingFoundException {
        if (ratings == null) {
            throw new NoRatingFoundException();
        }

        Integer rating = ratings.get(ladder);
        if (rating == null) {
            throw new NoRatingFoundException();
        }

        return rating;
    }

    private static void setDBGroupRating(Group group, int ladderId, int rating, double winOrLoss, Connection connection) {
        String sql = null, sql2 = null;
        if (group.isParty()) {
            StringBuilder questions = new StringBuilder();
            StringBuilder playersBuilder = new StringBuilder();
            StringBuilder playersAndBuilder = new StringBuilder();
            for (int i = 0; i < group.players().size(); i++) {
                questions.append("?, ");
                playersBuilder.append("player");
                playersBuilder.append(i + 1);
                playersBuilder.append(", ");
                playersAndBuilder.append("player");
                playersAndBuilder.append(i + 1);
                playersAndBuilder.append(" = ? AND ");
            }

            String playersString = playersBuilder.toString();
            String playersAndString = playersAndBuilder.substring(0, playersAndBuilder.length() - 5);
            String questionString = questions.toString();

            sql = "UPDATE ladder_ratings_party_" + group.players().size() + "_s12" + PotPvP.getInstance().getDBExtra() + " SET rating = ?, wins = wins + 1 WHERE lid = ? AND " + playersAndString + ";\n";
            sql += "INSERT INTO ladder_ratings_party_" + group.players().size() + "_s12" + PotPvP.getInstance().getDBExtra() + " (lid, " + playersString + "rating, wins, losses) SELECT ?, " + questionString + "?, ?, ? WHERE NOT EXISTS " +
                    "(SELECT 1 FROM ladder_ratings_party_" + group.players().size() + "_s12" + PotPvP.getInstance().getDBExtra() + " WHERE lid = ? AND " + playersAndString + ");";
            sql2 = "UPDATE ladder_ratings_party_" + group.players().size() + "_s12" + PotPvP.getInstance().getDBExtra() + " SET rating = ?, losses = losses + 1 WHERE lid = ? AND " + playersAndString + ";\n";
            sql2 += "INSERT INTO ladder_ratings_party_" + group.players().size() + "_s12" + PotPvP.getInstance().getDBExtra() + " (lid, " + playersString + "rating, wins, losses) SELECT ?, " + questionString + "?, ?, ? WHERE NOT EXISTS " +
                    "(SELECT 1 FROM ladder_ratings_party_" + group.players().size() + "_s12" + PotPvP.getInstance().getDBExtra() + " WHERE lid = ? AND " + playersAndString + ");";
        } else {
            sql = "UPDATE ladder_ratings_s12" + PotPvP.getInstance().getDBExtra() + " SET rating = ?, wins = wins + 1 WHERE lid = ? AND uuid = ?;\n";
            sql += "INSERT INTO ladder_ratings_s12" + PotPvP.getInstance().getDBExtra() + " (lid, uuid, rating, wins, losses) SELECT ?, ?, ?, ?, ? WHERE NOT EXISTS " +
                    "(SELECT 1 FROM ladder_ratings_s12" + PotPvP.getInstance().getDBExtra() + " WHERE lid = ? AND uuid = ?);";
            sql2 = "UPDATE ladder_ratings_s12" + PotPvP.getInstance().getDBExtra() + " SET rating = ?, losses = losses + 1 WHERE lid = ? AND uuid = ?;\n";
            sql2 += "INSERT INTO ladder_ratings_s12" + PotPvP.getInstance().getDBExtra() + " (lid, uuid, rating, wins, losses) SELECT ?, ?, ?, ?, ? WHERE NOT EXISTS " +
                    "(SELECT 1 FROM ladder_ratings_s12" + PotPvP.getInstance().getDBExtra() + " WHERE lid = ? AND uuid = ?);";
        }

        PreparedStatement ps = null;

        try {
            // Update player rating or create a new one if newbie
            ps = null;
            if (winOrLoss == 1) {
                ps = connection.prepareStatement(sql);
            } else {
                ps = connection.prepareStatement(sql2);
            }

            ps.setInt(1, rating);
            ps.setInt(2, ladderId);

            int index = 3;
            List<Player> players = group.sortedPlayers();

            for (Player pl : players) {
                Gberry.log("RATING", "Setting rating " + rating + " for " + ladderId + " for UUID " + pl.getUniqueId().toString());
                ps.setString(index++, pl.getUniqueId().toString());
            }

            ps.setInt(index++, ladderId);

            for (Player pl : players) {
                ps.setString(index++, pl.getUniqueId().toString());
            }

            ps.setInt(index++, rating);
            if (winOrLoss == 1) {
                ps.setInt(index++, 1);
                ps.setInt(index++, 0);
            } else {
                ps.setInt(index++, 0);
                ps.setInt(index++, 1);
            }

            ps.setInt(index++, ladderId);

            for (Player pl : players) {
                ps.setString(index++, pl.getUniqueId().toString());
            }

            Gberry.executeUpdate(connection, ps);

            Gberry.log("RATING", "Rating committed");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Gberry.closeComponents(ps);
        }
    }

    private static class RatingPair {

        private List<UUID> uuidsSorted = new ArrayList<>();

        public RatingPair(UUID ...uuids) {
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
