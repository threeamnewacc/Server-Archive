package net.badlion.arenalobby.managers;

import net.badlion.arenalobby.ArenaLobby;
import net.badlion.arenalobby.PotPvPPlayer;
import net.badlion.arenalobby.bukkitevents.RankedLeftChangeEvent;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.events.AsyncPlayerJoinEvent;
import net.badlion.gberry.events.GSyncEvent;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gpermissions.GPermissions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class RankedLeftManager extends BukkitUtil.Listener {

	public static int DEFAULT_RANKED_MATCHES_PER_DAY = 10;
	public static int DEFAULT_RANKED_MATCHES_PER_DAY_REGISTERED = 20;

	public static int DEFAULT_UNRANKED_MATCHES_PER_DAY = 20;
	public static int DEFAULT_UNRANKED_MATCHES_PER_DAY_REGISTERED = 30;
	private static Map<UUID, Integer> rankedMatchesLeftToday = new ConcurrentHashMap<>();
	private static Map<UUID, Integer> unRankedMatchesLeftToday = new ConcurrentHashMap<>();

	@EventHandler
	public void onPlayerAsyncJoin(final AsyncPlayerJoinEvent event) {
		RankedLeftManager.getMatchesLeftDB(event.getConnection(), event.getUuid());

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
		return getNumberOfRankedMatchesLeft(player.getUniqueId());
	}

	public static Integer getNumberOfRankedMatchesLeft(UUID uuid) {
		if (RankedLeftManager.rankedMatchesLeftToday.containsKey(uuid)) {
			return RankedLeftManager.rankedMatchesLeftToday.get(uuid);
		}

		if (GPermissions.plugin.userHasPermission(uuid.toString(), "badlion.registered")) {
			RankedLeftManager.rankedMatchesLeftToday.put(uuid, RankedLeftManager.DEFAULT_RANKED_MATCHES_PER_DAY_REGISTERED);
			return RankedLeftManager.DEFAULT_RANKED_MATCHES_PER_DAY_REGISTERED;
		}

		RankedLeftManager.rankedMatchesLeftToday.put(uuid, RankedLeftManager.DEFAULT_RANKED_MATCHES_PER_DAY);
		return RankedLeftManager.DEFAULT_RANKED_MATCHES_PER_DAY;
	}

	public static Integer getNumberOfUnRankedMatchesLeft(Player player) {
		return getNumberOfUnRankedMatchesLeft(player.getUniqueId());
	}

	public static Integer getNumberOfUnRankedMatchesLeft(UUID uuid) {
		if (RankedLeftManager.unRankedMatchesLeftToday.containsKey(uuid)) {
			return RankedLeftManager.unRankedMatchesLeftToday.get(uuid);
		}

		if (GPermissions.plugin.userHasPermission(uuid.toString(), "badlion.registered")) {
			RankedLeftManager.unRankedMatchesLeftToday.put(uuid, RankedLeftManager.DEFAULT_UNRANKED_MATCHES_PER_DAY_REGISTERED);
			return RankedLeftManager.DEFAULT_UNRANKED_MATCHES_PER_DAY_REGISTERED;
		}

		RankedLeftManager.unRankedMatchesLeftToday.put(uuid, RankedLeftManager.DEFAULT_UNRANKED_MATCHES_PER_DAY);
		return RankedLeftManager.DEFAULT_UNRANKED_MATCHES_PER_DAY;
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
		RankedLeftManager.rankedMatchesLeftToday.put(player.getUniqueId(), rankedLeft + amount);

		try {
			SidebarManager.removeSidebar(player);
			SidebarManager.addSidebarItems(player);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		RankedLeftChangeEvent event = new RankedLeftChangeEvent(player, rankedLeft + amount);
		ArenaLobby.getInstance().getServer().getPluginManager().callEvent(event);

		if (syncAcrossServers) {
			RankedLeftManager.syncToDBAndOtherServers(player.getUniqueId(), amount);
		}
	}

	public static void addRankedMatches(final UUID uuid, final int amount, final boolean syncAcrossServers) {
		int rankedLeft = RankedLeftManager.getNumberOfRankedMatchesLeft(uuid);
		RankedLeftManager.rankedMatchesLeftToday.put(uuid, rankedLeft + amount);

		// Fire off an event for our wonderful le SmerryPrengruin
		Player player = ArenaLobby.getInstance().getServer().getPlayer(uuid);
		if (player != null) {
			RankedLeftChangeEvent event = new RankedLeftChangeEvent(player, rankedLeft + amount);
			ArenaLobby.getInstance().getServer().getPluginManager().callEvent(event);
		}

		if (syncAcrossServers) {
			RankedLeftManager.syncToDBAndOtherServers(uuid, amount);
		}
	}

	public static void getMatchesLeftDB(Connection connection, final UUID uuid) {
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
								ArenaLobby.getInstance().getServer().dispatchCommand(Bukkit.getConsoleSender(), "user " + uuid + " addperm badlion.registered");
							}
						}.runTask(ArenaLobby.getInstance());
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
				}
			}

			query = "SELECT * FROM potion_matches_left_s14 WHERE uuid = ? AND day = ?;";
			ps = connection.prepareStatement(query);

			ps.setString(1, uuid.toString());
			Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			ps.setTimestamp(2, new Timestamp(new DateTime(DateTimeZone.UTC).getMillis()), calendar);

			rs = Gberry.executeQuery(connection, ps);

			if (rs.next()) {
				int rankedLeft = rs.getInt("num_ranked_left");
				RankedLeftManager.rankedMatchesLeftToday.put(uuid, rankedLeft);
				int unrankedLeft = rs.getInt("num_unranked_left");
				RankedLeftManager.unRankedMatchesLeftToday.put(uuid, unrankedLeft);
				return;
			}

			// No record found, insert them into the database
			query = "INSERT INTO potion_matches_left_s14 (uuid, num_ranked_left, num_unranked_left, day) VALUES (?, ?, ?, ?);";
			ps = connection.prepareStatement(query);

			ps.setString(1, uuid.toString());
			if (registered) {
				ps.setInt(2, RankedLeftManager.DEFAULT_RANKED_MATCHES_PER_DAY_REGISTERED);
				ps.setInt(3, RankedLeftManager.DEFAULT_UNRANKED_MATCHES_PER_DAY_REGISTERED);
			} else {
				ps.setInt(2, RankedLeftManager.DEFAULT_RANKED_MATCHES_PER_DAY);
				ps.setInt(3, RankedLeftManager.DEFAULT_UNRANKED_MATCHES_PER_DAY);
			}
			ps.setTimestamp(4, new Timestamp(new DateTime(DateTimeZone.UTC).getMillis()), calendar);
			Bukkit.getLogger().log(Level.INFO, "Matches Left " + uuid.toString() + " " + new DateTime(DateTimeZone.UTC).toString());

			Gberry.executeUpdate(connection, ps);

			if (registered) {
				RankedLeftManager.rankedMatchesLeftToday.put(uuid, RankedLeftManager.DEFAULT_RANKED_MATCHES_PER_DAY_REGISTERED);
				RankedLeftManager.unRankedMatchesLeftToday.put(uuid, RankedLeftManager.DEFAULT_UNRANKED_MATCHES_PER_DAY_REGISTERED);
				return;
			} else {
				RankedLeftManager.rankedMatchesLeftToday.put(uuid, RankedLeftManager.DEFAULT_RANKED_MATCHES_PER_DAY);
				RankedLeftManager.unRankedMatchesLeftToday.put(uuid, RankedLeftManager.DEFAULT_UNRANKED_MATCHES_PER_DAY);
				return;
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
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
		}

		return;
	}

	private static void updateRankedMatchesLeftDB(UUID uuid, int amount, Connection connection) {
		String query = "UPDATE potion_matches_left_s14 SET num_ranked_left = num_ranked_left + (?) WHERE uuid = ? AND day = ?;";

		PreparedStatement ps = null;

		try {
			ps = connection.prepareStatement(query);

			ps.setInt(1, amount);
			ps.setString(2, uuid.toString());
			Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
			ps.setTimestamp(3, new Timestamp(new DateTime(DateTimeZone.UTC).getMillis()), calendar);

			Gberry.executeUpdate(connection, ps);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(ps);
		}
	}
}
