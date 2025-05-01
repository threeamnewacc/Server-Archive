package net.badlion.arenalobby.helpers;

import net.badlion.arenacommon.rulesets.KitRuleSet;
import net.badlion.arenalobby.ArenaLobby;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gberry.utils.RatingUtil;
import net.badlion.gspigot.Hologram;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RatingSignsHelper extends BukkitUtil.Listener {

	private static List<RatingSign> ratingSigns = new ArrayList<>();
	private static Map<UUID, String> usernameCache = new ConcurrentHashMap<>();

	public static void initialize() {
		World world = ArenaLobby.getInstance().getServer().getWorld("world");

		// Create all rating sign objects                // NOTE: IF WE EVER ENABLE SKULLS, FIX COORDS
		//new RatingSign(KitRuleSet.uhcRuleSet, BlockFace.NORTH, new Location(world, -20, 73, -1));

		new RatingSign(BlockFace.WEST, new Location(world, -3, 134, -13)); // Global ladder
		new RatingSign(KitRuleSet.uhcRuleSet, BlockFace.SOUTH, new Location(world, 91, 34, -52));
		new RatingSign(KitRuleSet.diamondOCNRuleSet, BlockFace.NORTH, new Location(world, 62.5, 39, -14.5));
		new RatingSign(KitRuleSet.ironOCNRuleSet, BlockFace.NORTH, new Location(world, 57.5, 39, -25.5));
		new RatingSign(KitRuleSet.buildUHCRuleSet, BlockFace.NORTH, new Location(world, -45, 37, -16));
		new RatingSign(KitRuleSet.ironSoupRuleSet, BlockFace.NORTH, new Location(world, -36, 95, 0));
		new RatingSign(KitRuleSet.archerRuleSet, BlockFace.WEST, new Location(world, -21.5, 99, 29.5));
		new RatingSign(KitRuleSet.sgRuleSet, BlockFace.EAST, new Location(world, -4, 29, 67));
		new RatingSign(KitRuleSet.noDebuffRuleSet, BlockFace.SOUTH, new Location(world, 47, 31, 96));
		new RatingSign(KitRuleSet.kohiRuleSet, BlockFace.SOUTH, new Location(world, 73, 32, 91));
		new RatingSign(KitRuleSet.vanillaRuleSet, BlockFace.EAST, new Location(world, 98, 30, 71));
		new RatingSign(KitRuleSet.godAppleRuleSet, BlockFace.EAST, new Location(world, 93, 66, 18));

		new RatingSign(KitRuleSet.horseRuleSet, BlockFace.EAST, new Location(world, 46.5, 70, 59.5));

		//new RatingSign(KitRuleSet.skyWarsRuleSet, BlockFace.SOUTH, new Location(world, -22, 73, -13));
		//new RatingSign(KitRuleSet.godAppleRuleSet, BlockFace.SOUTH, new Location(world, -20, 73, -13));

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
			String query = "SELECT * FROM ladder_ratings_s14 WHERE lid = ?  AND (ranked_wins + ranked_losses) >= 10 ORDER BY mu DESC LIMIT 10;";
			ps = con.prepareStatement(query);

			if (ratingSign.getKitRuleSet() != null) {
				ps.setInt(1, ratingSign.getKitRuleSet().getId());
			} else {
				ps.setInt(1, 0);
			}

			rs = Gberry.executeQuery(con, ps);

			Map<UUID, Double> ratings = ratingSign.getPlayerRatings();

			while (rs.next()) {
				ratings.put(UUID.fromString(rs.getString("uuid")), rs.getDouble("mu"));
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

		private Map<UUID, Double> playerRatings = new ConcurrentHashMap<>();

		private Hologram topText;

		private Map<Integer, Hologram> holograms = new HashMap<>();

		/**
		 * ONLY USED FOR GLOBAL LADDER
		 */
		public RatingSign(BlockFace skullRotation, Location firstSignLocation) {
			// Get all the locations
			this.topText = Bukkit.newHologram(firstSignLocation, ChatColor.GOLD + "Global Top 10");

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
			this.topText = Bukkit.newHologram(firstSignLocation, ChatColor.GOLD + this.kitRuleSet.getName() + " Top 10");

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

		public void updateTop10Signs() {
			// Sort ratings
			final List<Map.Entry<UUID, Double>> ratings = this.getSortedRatings();
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
					int i = 0;
					String name;
					for (Map.Entry<UUID, Double> entry : ratings) {
						if (i >= 10) {
							return;
						}
						name = names.get(entry.getKey());
						RatingUtil.Rank rank = RatingUtil.Rank.getRankByElo(entry.getValue());

						if (holograms.containsKey(i)) {
							Hologram hologram = RatingSign.this.holograms.get(i);
							hologram.setMessage(ChatColor.GREEN + "#" + (i + 1)
									+ " " + name
									+ " " + rank.getChatColor() + rank.getName()
									+ " " + chatColorFromPoints(RatingUtil.Rank.getPoints(entry.getValue())) + RatingUtil.Rank.getPoints(entry.getValue())
									+ " points");
						} else {
							Location holoLoc = RatingSign.this.topText.getLocation().clone().subtract(0, i == 0 ? .6 : ((i + 1) * 0.3) + 0.3, 0);
							Hologram hologram = Bukkit.newHologram(holoLoc, ChatColor.GREEN + "#" + (i + 1)
									+ " " + name
									+ " " + rank.getChatColor() + rank.getName()
									+ " " + chatColorFromPoints(RatingUtil.Rank.getPoints(entry.getValue())) + RatingUtil.Rank.getPoints(entry.getValue())
									+ " points");
							RatingSign.this.holograms.put(i, hologram);
						}
						i++;
					}
				}
			});
		}

		private List<Map.Entry<UUID, Double>> getSortedRatings() {
			// Sort ratings
			List<Map.Entry<UUID, Double>> ratings = new LinkedList<>();

			ratings.addAll(this.playerRatings.entrySet());

			Collections.sort(ratings, new Comparator<Map.Entry<UUID, Double>>() {
				@Override
				public int compare(Map.Entry<UUID, Double> o1, Map.Entry<UUID, Double> o2) {
					// We return negative of the actual compareTo() value because we sort ASC
					return -o1.getValue().compareTo(o2.getValue());
				}
			});

			return ratings;
		}

		public KitRuleSet getKitRuleSet() {
			return kitRuleSet;
		}

		public Map<UUID, Double> getPlayerRatings() {
			return playerRatings;
		}

	}

	public static ChatColor chatColorFromPoints(double points) {
		if (points <= 250.0) {
			return ChatColor.RED;
		}
		if (points <= 500.0) {
			return ChatColor.YELLOW;
		}
		return ChatColor.GREEN;
	}

}
