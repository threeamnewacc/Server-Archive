package net.badlion.potpvp.helpers;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.exceptions.NoRatingFoundException;
import net.badlion.potpvp.ladders.Ladder;
import net.badlion.potpvp.managers.RatingManager;
import net.badlion.potpvp.rulesets.KitRuleSet;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RatingSignsHelper extends BukkitUtil.Listener {

	private static List<RatingSign> ratingSigns = new ArrayList<>();
	private static Map<UUID, String> usernameCache = new ConcurrentHashMap<>();

	public static void initialize() {
		World world = PotPvP.getInstance().getServer().getWorld("world");

		// Create all rating sign objects                // NOTE: IF WE EVER ENABLE SKULLS, FIX COORDS
		new RatingSign(KitRuleSet.uhcRuleSet, BlockFace.NORTH, new Location(world, -20, 73, -1));
		new RatingSign(KitRuleSet.diamondOCNRuleSet, BlockFace.NORTH, new Location(world, -21, 73, -1));
		new RatingSign(KitRuleSet.ironOCNRuleSet, BlockFace.NORTH, new Location(world, -22, 73, -1));
		new RatingSign(KitRuleSet.archerRuleSet, BlockFace.NORTH, new Location(world, -23, 73, -1));
		new RatingSign(KitRuleSet.sgRuleSet, BlockFace.NORTH, new Location(world, -24, 73, -1));
		new RatingSign(KitRuleSet.ironSoupRuleSet, BlockFace.EAST, new Location(world, -28, 73, -5));
		new RatingSign(KitRuleSet.advancedUHCRuleSet, BlockFace.EAST, new Location(world, -28, 73, -6));
		new RatingSign(BlockFace.EAST, new Location(world, -28, 73, -7)); // Global ladder
		new RatingSign(KitRuleSet.horseRuleSet, BlockFace.EAST, new Location(world, -28, 73, -8));
		new RatingSign(KitRuleSet.buildUHCRuleSet, BlockFace.EAST, new Location(world, -28, 73, -9));
		new RatingSign(KitRuleSet.vanillaRuleSet, BlockFace.SOUTH, new Location(world, -24, 73, -13));
		new RatingSign(KitRuleSet.kohiRuleSet, BlockFace.SOUTH, new Location(world, -23, 73, -13));
		new RatingSign(KitRuleSet.skyWarsRuleSet, BlockFace.SOUTH, new Location(world, -22, 73, -13));
		new RatingSign(KitRuleSet.noDebuffRuleSet, BlockFace.SOUTH, new Location(world, -21, 73, -13));
		new RatingSign(KitRuleSet.godAppleRuleSet, BlockFace.SOUTH, new Location(world, -20, 73, -13));

		// Start task to update ratings every 10 seconds
		BukkitUtil.runTaskTimer(new Runnable() {
			@Override
			public void run() {
				BukkitUtil.runTaskAsync(new Runnable() {
					@Override
					public void run() {
						for (RatingSign ratingSign : RatingSignsHelper.ratingSigns) {
							RatingSignsHelper.getTop10Rankings(ratingSign);
						}
					}
				});
			}
		}, 200L, 200L);
	}

	private static void getTop10Rankings(final RatingSign ratingSign) {
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			con = Gberry.getConnection();
			String query = "SELECT * FROM ladder_ratings_s12" + PotPvP.getInstance().getDBExtra() + " WHERE lid = ?  ORDER BY rating DESC LIMIT 10;";
			ps = con.prepareStatement(query);

			if (ratingSign.getKitRuleSet() != null) {
				ps.setInt(1, ratingSign.getKitRuleSet().getId());
			} else {
				ps.setInt(1, 0);
			}

			rs = Gberry.executeQuery(con, ps);

			Map<UUID, Integer> ratings = ratingSign.getPlayerRatings();

			while (rs.next()) {
				ratings.put(UUID.fromString(rs.getString("uuid")), rs.getInt("rating"));
			}

			ratingSign.updateTop10Signs();
		} catch (SQLException ex) {
			ex.printStackTrace();
			Bukkit.getLogger().severe(ex.getMessage());
		} finally {
			Gberry.closeComponents(rs, ps, con);
		}
	}

	public static class RatingSign {

		private KitRuleSet kitRuleSet;

		private Map<UUID, Integer> playerRatings = new ConcurrentHashMap<>();

		private Location firstSignLocation;
		private Location secondSignLocation;
		private Location thirdSignLocation;

		/**
		 * ONLY USED FOR GLOBAL LADDER
		 */
		public RatingSign(BlockFace skullRotation, Location firstSignLocation) {
			// Get all the locations
			this.firstSignLocation = firstSignLocation;
			this.secondSignLocation = firstSignLocation.clone().add(0, -1, 0);
			this.thirdSignLocation = firstSignLocation.clone().add(0, -2, 0);

			// Update first sign
			Sign sign = (Sign) firstSignLocation.getWorld().getBlockAt(this.firstSignLocation).getState();
			sign.setLine(0, "Global Top 10");
			sign.setLine(2, "Your Rank:");
			sign.update(true, false); // Unsafe method to use generally, but should be safe in this case

			// Grab all ratings
			BukkitUtil.runTaskAsync(new Runnable() {
				@Override
				public void run() {
					RatingSignsHelper.getTop10Rankings(RatingSign.this);
				}
			});

			// Cache
			RatingSignsHelper.ratingSigns.add(this);
		}

		public RatingSign(KitRuleSet kitRuleSet, BlockFace skullRotation, Location firstSignLocation) {
			this.kitRuleSet = kitRuleSet;

			// Get all the locations
			this.firstSignLocation = firstSignLocation;
			this.secondSignLocation = firstSignLocation.clone().add(0, -1, 0);
			this.thirdSignLocation = firstSignLocation.clone().add(0, -2, 0);

			// Update first sign
			String kitRuleSetName = RatingSign.this.kitRuleSet.getName().length() > 8
					? RatingSign.this.kitRuleSet.getName().substring(0, 8) : RatingSign.this.kitRuleSet.getName();

			Sign sign = (Sign) firstSignLocation.getWorld().getBlockAt(this.firstSignLocation).getState();
			sign.setLine(0, kitRuleSetName + " Top 10");
			sign.setLine(2, "Your Rank:");
			sign.update(true, false); // Unsafe method to use generally, but should be safe in this case

			// Grab all ratings
			BukkitUtil.runTaskAsync(new Runnable() {
				@Override
				public void run() {
					RatingSignsHelper.getTop10Rankings(RatingSign.this);
				}
			});

			// Cache
			RatingSignsHelper.ratingSigns.add(this);
		}

		public void updatePlayerRating(final Player player) {
			Gberry.log("RATINGSIGNS", "updating player rating");
            Group group = PotPvP.getInstance().getPlayerGroup(player);

            if (group.isParty()) {
                return;
            }

			int newRating;
			try {
				newRating = RatingManager.getGroupRating(group, Ladder.getLadder(kitRuleSet.getName(), Ladder.LadderType.OneVsOneRanked));
			} catch (NoRatingFoundException e) {
				return;
			}

			final int finalNewRating = newRating;
            BukkitUtil.runTaskAsync(new Runnable() {
				@Override
				public void run() {
					Connection con = null;
					ResultSet rs = null;
					PreparedStatement ps = null;
					try {
						con = Gberry.getConnection();
						String query = "SELECT COUNT(*) FROM ladder_ratings_s12" + PotPvP.getInstance().getDBExtra() + " WHERE lid = ? AND rating >= ?;";
						ps = con.prepareStatement(query);
						ps.setInt(1, RatingSign.this.kitRuleSet.getId());
						ps.setInt(2, finalNewRating);
						rs = Gberry.executeQuery(con, ps);
						Gberry.log("RATINGSIGNS", "executed query");
						if (rs.next()) {
							Gberry.log("RATINGSIGNS", "rs.next()!!!");
							int newRank = rs.getInt(1);

							final String[] lines = new String[4];

							String kitRuleSetName = RatingSign.this.kitRuleSet.getName().length() > 8
															? RatingSign.this.kitRuleSet.getName().substring(0, 8) : RatingSign.this.kitRuleSet.getName();
							lines[0] = kitRuleSetName + " Top 10";
							lines[2] = "Your Rank:";
							lines[3] = "#" + newRank + " (" + finalNewRating + ")";

				            /*BukkitUtil.runTask(new Runnable() {
					            @Override
					            public void run() {
						            //Gberry.log("PACKET", "Adding rating sign");
						            //for (String s : lines) {
						            //    if (s != null) {
						            //        Gberry.log("PACKET", s);
						            //    }
						            //}

						            //BukkitUtil.sendSignChange(player, RatingSign.this.firstSignLocation, lines);
					            }
				            });*/
						}
					} catch (SQLException ex) {
						ex.printStackTrace();
						Bukkit.getLogger().severe(ex.getMessage());
					} finally {
						Gberry.closeComponents(rs, ps, con);
					}
				}
			});
        }

		public void updateTop10Signs() {
			// Sort ratings
			final List<Map.Entry<UUID, Integer>> ratings = this.getSortedRatings();
			final int size = ratings.size();

			// Grab all usernames
			final Map<UUID, String> names = new HashMap<>();

			for (UUID uuid : this.playerRatings.keySet()) {
				String username = RatingSignsHelper.usernameCache.get(uuid);

				// 12-15 queries per sec because of all the ladders and players, fk that
				if (username == null) {
					username = Gberry.getUsernameFromUUID(uuid.toString());
					RatingSignsHelper.usernameCache.put(uuid, username);
				}

				names.put(uuid, username);
			}

			BukkitUtil.runTask(new Runnable() {
				@Override
				public void run() {
					Sign sign = (Sign) RatingSign.this.firstSignLocation.getBlock().getState();
					for (int i = 1; i < 3; i++) {
						// TODO: DO NOT USE UNTIL HTTP PROBLEMS RESOLVED WITH USERCACHE
						// Set skull on top of fence post to #1 player's head
						/*if (i <= size && i == 1) {
							Block block = RatingSign.this.skullLocation.getBlock();
							block.setType(Material.SKULL);
							block.setData((byte) 1);
							Skull skull = (Skull) block.getState();
							skull.setSkullType(SkullType.PLAYER);
							skull.setOwner(names.get(ratings.get(i - 1).getKey())); // TODO: I THINK THIS CAUSES MICRO LAG SPIKES?
							skull.setRotation(RatingSign.this.skullRotation);
							skull.update();
						}*/

						String name = "";
						if (i <= size) { // Enough ratings to fill
							name = names.get(ratings.get(i - 1).getKey());
						}

						// Truncate name (1.9 doesn't handle this)
						if (name.length() > 13) {
							name = name.substring(0, 13);
						}

						sign.setLine(i + 1, "#" + i + " " + name);
					}
					sign.update(true, false); // Unsafe method to use generally, but should be safe in this case

					sign = (Sign) RatingSign.this.secondSignLocation.getBlock().getState();
					for (int i = 3; i < 7; i++) {
						String name = "";
						if (i <= size) { // Enough ratings to fill
							name = names.get(ratings.get(i - 1).getKey());
						}

						// Truncate name (1.9 doesn't handle this)
						if (name.length() > 13) {
							name = name.substring(0, 13);
						}

						sign.setLine(i - 3, "#" + i + " " + name);
					}
					sign.update(true, false); // Unsafe method to use generally, but should be safe in this case

					sign = (Sign) RatingSign.this.thirdSignLocation.getBlock().getState();
					for (int i = 7; i < 11; i++) {
						String name = "";
						if (i <= size) { // Enough ratings to fill
							name = names.get(ratings.get(i - 1).getKey());
						}

						// Truncate name (1.9 doesn't handle this)
						if (name.length() > 13) {
							name = name.substring(0, 13);
						}

						sign.setLine(i - 7, "#" + i + " " + name);
					}
					sign.update(true, false); // Unsafe method to use generally, but should be safe in this case
				}
			});
		}

		private List<Map.Entry<UUID, Integer>> getSortedRatings() {
			// Sort ratings
			List<Map.Entry<UUID, Integer>> ratings = new LinkedList<>();

			ratings.addAll(this.playerRatings.entrySet());

			Collections.sort(ratings, new Comparator<Map.Entry<UUID, Integer>>() {
				@Override
				public int compare(Map.Entry<UUID, Integer> o1, Map.Entry<UUID, Integer> o2) {
					// We return negative of the actual compareTo() value because we sort ASC
					return -o1.getValue().compareTo(o2.getValue());
				}
			});

			return ratings;
		}

		public KitRuleSet getKitRuleSet() {
			return kitRuleSet;
		}

		public Map<UUID, Integer> getPlayerRatings() {
			return playerRatings;
		}

	}

}
