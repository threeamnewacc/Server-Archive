package net.badlion.smellylobby.manager;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.smellylobby.SmellyLobby;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FactionsDeathbanManager {

	private static Map<UUID, Integer> livesMap = new ConcurrentHashMap<>();
	private static Map<UUID, Timestamp> deathBannedPlayers = new ConcurrentHashMap<>();


	// Lives

	private static void loadLives(final Player player) {
		new BukkitRunnable() {
			@Override
			public void run() {
				String query = "SELECT * FROM usfactions_num_of_lives WHERE uuid = ?;";

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;

				try {
					connection = Gberry.getConnection();
					ps = connection.prepareStatement(query);

					ps.setString(1, player.getUniqueId().toString());

					rs = Gberry.executeQuery(connection, ps);

					while (rs.next()) {
						FactionsDeathbanManager.livesMap.put(UUID.fromString(rs.getString("uuid")), rs.getInt("num_of_lives"));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					Gberry.closeComponents(rs, ps, connection);
				}
			}
		}.runTaskAsynchronously(SmellyLobby.getInstance());
	}

	public static int getNumOfLives(UUID uuid) {
		if (FactionsDeathbanManager.livesMap.containsKey(uuid)) {
			return FactionsDeathbanManager.livesMap.get(uuid);
		}

		return 0;
	}

	public static void addLives(final UUID uuid, int numOfLives, boolean syncdb) {
		int numOfCurrentLives = 0;

		if (FactionsDeathbanManager.livesMap.containsKey(uuid)) {
			numOfCurrentLives = FactionsDeathbanManager.livesMap.get(uuid);
		}

		final int newLives = numOfCurrentLives + numOfLives;
		FactionsDeathbanManager.livesMap.put(uuid, newLives);

		if (syncdb) {
			// Sync DB
			FactionsDeathbanManager.syncLivesDB(uuid, newLives);
		}
	}

	public static void removeLives(final UUID uuid, int numOfLives, boolean syncdb) {
		int numOfCurrentLives = 0;

		if (FactionsDeathbanManager.livesMap.containsKey(uuid)) {
			numOfCurrentLives = FactionsDeathbanManager.livesMap.get(uuid);
		}

		int tmpLives = numOfCurrentLives - numOfLives;
		if (tmpLives < 0) {
			tmpLives = 0;
		}

		final int newLives = tmpLives;
		FactionsDeathbanManager.livesMap.put(uuid, newLives);

		if (syncdb) {
			// Sync DB
			FactionsDeathbanManager.syncLivesDB(uuid, newLives);
		}
	}

	private static void syncLivesDB(final UUID uuid, final int newLives) {
		BukkitUtil.runTaskAsync(new Runnable() {
			@Override
			public void run() {
				String query = "UPDATE usfactions_num_of_lives SET num_of_lives = ? WHERE uuid = ?;\n";
				query += "INSERT INTO usfactions_num_of_lives (uuid, num_of_lives) SELECT ?, ? WHERE NOT EXISTS " +
						"(SELECT 1 FROM usfactions_num_of_lives WHERE uuid = ?);";

				Connection connection = null;
				PreparedStatement ps = null;

				try {
					connection = Gberry.getConnection();
					ps = connection.prepareStatement(query);
					ps.setInt(1, newLives);
					ps.setString(2, uuid.toString());
					ps.setString(3, uuid.toString());
					ps.setInt(4, newLives);
					ps.setString(5, uuid.toString());

					Gberry.executeUpdate(connection, ps);
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					Gberry.closeComponents(ps, connection);
				}
			}
		});
	}


	// Deathbans
	
	private static void getDeathban(final Player player) {
		new BukkitRunnable() {
			@Override
			public void run() {
				String query = "SELECT * FROM usfactions_death_bans WHERE unban_time > ? AND uuid = ?;";

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;

				try {
					connection = Gberry.getConnection();
					ps = connection.prepareStatement(query);

					ps.setTimestamp(1, new Timestamp(new DateTime(DateTimeZone.UTC).getMillis()));
					ps.setString(2, player.getUniqueId().toString());

					rs = Gberry.executeQuery(connection, ps);

					while (rs.next()) {
						Timestamp ts = rs.getTimestamp("unban_time");
						FactionsDeathbanManager.deathBannedPlayers.put(UUID.fromString(rs.getString("uuid")), ts);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					Gberry.closeComponents(rs, ps, connection);
				}
			}
		}.runTaskAsynchronously(SmellyLobby.getInstance());
	}


	public static boolean isDeathBanned(UUID uuid) {
		Timestamp ts = FactionsDeathbanManager.deathBannedPlayers.get(uuid);
		if (ts == null) {
			return false;
		}

		if (ts.getTime() < System.currentTimeMillis()) {
			FactionsDeathbanManager.deathBannedPlayers.remove(uuid);
			return false;
		}

		return true;
	}

	public static Timestamp getDeathBanTime(UUID uuid) {
		return FactionsDeathbanManager.deathBannedPlayers.get(uuid);
	}


	public static void unDeathBanPlayer(final UUID uuid) {
		Timestamp ts = FactionsDeathbanManager.deathBannedPlayers.remove(uuid);

		if (ts != null) {
			BukkitUtil.runTaskAsync(new Runnable() {
				@Override
				public void run() {
					Connection connection = null;
					PreparedStatement ps = null;

					String query = "DELETE FROM usfactions_death_bans WHERE uuid = ?";

					try {
						connection = Gberry.getConnection();
						ps = connection.prepareStatement(query);
						ps.setString(1, uuid.toString());

						Gberry.executeUpdate(connection, ps);
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						Gberry.closeComponents(ps, connection);
					}
				}
			});
		}
	}

	public static String getDeathbannedTime(UUID playerId, boolean hasLives, int lives) {
		return "Deathbanned for "
				+ DurationFormatUtils.formatDurationWords(getDeathBanTime(playerId).getTime() - System.currentTimeMillis(), true, true) + "."
				+ (hasLives ? ChatColor.GREEN + " Reconnect to use a life. You have " + ChatColor.BLUE + lives + ChatColor.GREEN + " left."
				: ChatColor.GREEN + " You can buy a life at http://store.badlion.net/");
	}
}
