package net.badlion.potpvp.managers;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.events.AsyncDelayedPlayerJoinEvent;
import net.badlion.gberry.events.GSyncEvent;
import net.badlion.gpermissions.GPermissions;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.PotPvPPlayer;
import net.badlion.potpvp.bukkitevents.RankedLeftChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RankedLeftManager extends BukkitUtil.Listener {

	public static int DEFAULT_RANKED_MATCHES_PER_DAY = 10;
	public static int DEFAULT_RANKED_MATCHES_PER_DAY_REGISTERED = 20;
	private static Map<UUID, Integer> matchesLeftToday = new ConcurrentHashMap<>();

	@EventHandler
	public void onPlayerAsyncJoinDelayed(final AsyncDelayedPlayerJoinEvent event) {
	 	RankedLeftManager.matchesLeftToday.put(event.getUuid(), RankedLeftManager.getRankedMatchesLeftDB(event.getConnection(), event.getUuid()));

		event.getRunnables().add(new Runnable() {
			public void run() {
				PotPvPPlayer potPvPPlayer = PotPvPPlayerManager.getPotPvPPlayer(event.getUuid());
				if (potPvPPlayer != null) {
					potPvPPlayer.setRankedLeftLoaded(true);
				}
			}
		});
	}

	@EventHandler
	public void onGSync(GSyncEvent event) {
		if (event.getArgs().size() < 4) {
			return;
		}

		String subChannel = event.getArgs().get(0);
		if (subChannel.equals("AddMatches")) {
			UUID uuid = UUID.fromString(event.getArgs().get(1));
			Integer extraMatches = Integer.parseInt(event.getArgs().get(2));

			RankedLeftManager.addRankedMatches(uuid, extraMatches, false);
		}
	}

	public static Integer getNumberOfRankedMatchesLeft(Player player) {
		if (RankedLeftManager.matchesLeftToday.containsKey(player.getUniqueId())) {
			return RankedLeftManager.matchesLeftToday.get(player.getUniqueId());
		}

		if (player.hasPermission("badlion.registered")) {
			RankedLeftManager.matchesLeftToday.put(player.getUniqueId(), RankedLeftManager.DEFAULT_RANKED_MATCHES_PER_DAY_REGISTERED);
			return RankedLeftManager.DEFAULT_RANKED_MATCHES_PER_DAY_REGISTERED;
		}

		RankedLeftManager.matchesLeftToday.put(player.getUniqueId(), RankedLeftManager.DEFAULT_RANKED_MATCHES_PER_DAY);
		return RankedLeftManager.DEFAULT_RANKED_MATCHES_PER_DAY;
	}

	public static Integer getNumberOfRankedMatchesLeft(UUID uuid) {
		if (RankedLeftManager.matchesLeftToday.containsKey(uuid)) {
			return RankedLeftManager.matchesLeftToday.get(uuid);
		}

		if (GPermissions.plugin.userHasPermission(uuid.toString(), "badlion.registered")) {
			RankedLeftManager.matchesLeftToday.put(uuid, RankedLeftManager.DEFAULT_RANKED_MATCHES_PER_DAY_REGISTERED);
			return RankedLeftManager.DEFAULT_RANKED_MATCHES_PER_DAY_REGISTERED;
		}

		RankedLeftManager.matchesLeftToday.put(uuid, RankedLeftManager.DEFAULT_RANKED_MATCHES_PER_DAY);
		return RankedLeftManager.DEFAULT_RANKED_MATCHES_PER_DAY;
	}

	private static void syncToDBAndOtherServers(final UUID uuid, final int amount) {
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
					RankedLeftManager.updateRankedMatchesLeftDB(uuid, amount, connection);
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					Gberry.closeComponents(connection);
				}
			}
		});
	}

	public static void addRankedMatches(final Player player, final int amount, final boolean syncAcrossServers) {
		int rankedLeft = RankedLeftManager.getNumberOfRankedMatchesLeft(player);
		RankedLeftManager.matchesLeftToday.put(player.getUniqueId(), rankedLeft + amount);

		RankedLeftChangeEvent event = new RankedLeftChangeEvent(player, rankedLeft + amount);
		PotPvP.getInstance().getServer().getPluginManager().callEvent(event);

		if (syncAcrossServers) {
			RankedLeftManager.syncToDBAndOtherServers(player.getUniqueId(), amount);
		}
	}

    public static void addRankedMatches(final UUID uuid, final int amount, final boolean syncAcrossServers) {
        int rankedLeft = RankedLeftManager.getNumberOfRankedMatchesLeft(uuid);
		RankedLeftManager.matchesLeftToday.put(uuid, rankedLeft + amount);

        // Fire off an event for our wonderful le SmerryPrengruin
        Player player = PotPvP.getInstance().getServer().getPlayer(uuid);
        if (player != null) {
            RankedLeftChangeEvent event = new RankedLeftChangeEvent(player, rankedLeft + amount);
            PotPvP.getInstance().getServer().getPluginManager().callEvent(event);
        }

		if (syncAcrossServers) {
			RankedLeftManager.syncToDBAndOtherServers(uuid, amount);
		}
    }

	/**
	 * Needs to be called ASYNC
	 */
	public static void removeRankedLeft(Player player, int amount, Connection connection) {
		int rankedLeft = RankedLeftManager.getNumberOfRankedMatchesLeft(player);
		RankedLeftManager.matchesLeftToday.put(player.getUniqueId(), rankedLeft - amount);

		// Sync database
        RankedLeftManager.updateRankedMatchesLeftDB(player.getUniqueId(), -amount, connection);
	}

	public static void removeOneRankedMatch(UUID uuid) {
		int rankedLeft = RankedLeftManager.getNumberOfRankedMatchesLeft(uuid);
		RankedLeftManager.matchesLeftToday.put(uuid, rankedLeft - 1);
	}

	public static int getRankedMatchesLeftDB(Connection connection, final UUID uuid) {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			String query;

			boolean registered = GPermissions.plugin.userHasPermission(uuid.toString(), "badlion.registered");
			if (!registered) {
				query = "SELECT * FROM users WHERE uuid = ? AND minecraft_verified = 1 AND active = 1;";

				try {
					ps = connection.prepareStatement(query);
					ps.setString(1, uuid.toString());
					rs = Gberry.executeQuery(connection, ps);

					if (rs.next()) {
						new BukkitRunnable() {
						 	public void run() {
								PotPvP.getInstance().getServer().dispatchCommand(Bukkit.getConsoleSender(), "user " + uuid + " addperm badlion.registered");
							}
						}.runTask(PotPvP.getInstance());
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
					if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
				}
			}

			query = "SELECT * FROM potion_ranked_left WHERE uuid = ? AND day = ?;";
			ps = connection.prepareStatement(query);

			ps.setString(1, uuid.toString());
			ps.setTimestamp(2, new Timestamp(new DateTime(DateTimeZone.UTC).getMillis()));

			rs = Gberry.executeQuery(connection, ps);

			if (rs.next()) {
				int rankedLeft = rs.getInt("num_ranked_left");
				RankedLeftManager.matchesLeftToday.put(uuid, rankedLeft);
				return rankedLeft;
			}

			// No record found, insert them into the database
			query = "INSERT INTO potion_ranked_left (uuid, num_ranked_left, day) VALUES (?, ?, ?);";
			ps = connection.prepareStatement(query);

			ps.setString(1, uuid.toString());
			if (registered) {
				ps.setInt(2, RankedLeftManager.DEFAULT_RANKED_MATCHES_PER_DAY_REGISTERED);
			} else {
				ps.setInt(2, RankedLeftManager.DEFAULT_RANKED_MATCHES_PER_DAY);
			}
			ps.setTimestamp(3, new Timestamp(new DateTime(DateTimeZone.UTC).getMillis()));

			Gberry.executeUpdate(connection, ps);

			if (registered) {
				RankedLeftManager.matchesLeftToday.put(uuid, RankedLeftManager.DEFAULT_RANKED_MATCHES_PER_DAY_REGISTERED);
				return RankedLeftManager.DEFAULT_RANKED_MATCHES_PER_DAY_REGISTERED;
			} else {
				RankedLeftManager.matchesLeftToday.put(uuid, RankedLeftManager.DEFAULT_RANKED_MATCHES_PER_DAY);
				return RankedLeftManager.DEFAULT_RANKED_MATCHES_PER_DAY;
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}

		return -1337; // Error code :P
	}
	
	private static void updateRankedMatchesLeftDB(UUID uuid, int amount, Connection connection) {
		String query = "UPDATE potion_ranked_left SET num_ranked_left = num_ranked_left + (?) WHERE uuid = ? AND day = ?;";

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
