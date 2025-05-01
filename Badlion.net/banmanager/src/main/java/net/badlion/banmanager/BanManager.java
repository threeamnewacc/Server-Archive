package net.badlion.banmanager;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.badlion.banmanager.commands.Ban;
import net.badlion.banmanager.commands.BanWaveCommand;
import net.badlion.banmanager.commands.JudgementDayCommand;
import net.badlion.banmanager.commands.Kick;
import net.badlion.banmanager.commands.Mute;
import net.badlion.banmanager.commands.Unban;
import net.badlion.banmanager.commands.Unmute;
import net.badlion.banmanager.events.PunishedPlayerEvent;
import net.badlion.banmanager.listeners.GSyncListener;
import net.badlion.common.GetCommon;
import net.badlion.common.libraries.StringCommon;
import net.badlion.common.libraries.exceptions.HTTPRequestFailException;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.gpermissions.GPermissions;
import net.badlion.smellyinventory.SmellyInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.json.simple.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BanManager extends JavaPlugin {

	final public static java.sql.Timestamp default_perm_time = new java.sql.Timestamp(946684800);
	public static BanManager plugin;
	public static int PERMANENT_TIME = -4;
	public static String CONSOLE_SENDER = "~Console";
	public static boolean isLobby = false;
	// private static BoneCP connectionPool;
	private static HikariConfig hikariConfig;
	private static HikariDataSource hikariDataSource;
	private String server_name;
	public static boolean isAllowedMultipleConsoleBans = false;
	public static boolean useLineSeparators = true;

	private static final DateTime newMuteSystemTime = new DateTime(DateTimeZone.UTC).withDate(2016, 4, 10).withTime(15, 0, 0, 0);

	public static BanManager getInstance() {
		return BanManager.plugin;
	}

	public static Connection getUnsafeConnection() throws SQLException {
		try {
			return hikariDataSource.getConnection();
		} catch (SQLException e) {
			// Try one more time at max (load of kitpvp servers a lot gets overloaded)
			return hikariDataSource.getConnection();
		}
	}

	public static Connection getConnection() throws SQLException {
		// Safety
		Gberry.catchNonAsyncThread();

		try {
			return hikariDataSource.getConnection();
		} catch (SQLException e) {
			// Try one more time at max (load of kitpvp servers a lot gets overloaded)
			return hikariDataSource.getConnection();
		}
	}

	@Override
	public void onEnable() {
		BanManager.plugin = this;

		// Config file stuff
		this.saveDefaultConfig();

		BanManager.isAllowedMultipleConsoleBans = this.getConfig().getBoolean("allowed-multiple-console-bans", false);

		if (Gberry.serverName.startsWith("lobby") || Gberry.serverName.contains("bllobby")) {
			BanManager.isLobby = true;
		}

		SmellyInventory.initialize(this, false);

		//Gberry.loggingTags.add("BM");

		String url = GetCommon.getIpForDB();// this.getConfig().getString("banmanager.connection.url");
		String user = this.getConfig().getString("banmanager.connection.user");
		String pass = this.getConfig().getString("banmanager.connection.pass");
		String db = this.getConfig().getString("banmanager.connection.db");
		int maxConnections = this.getConfig().getInt("banmanager.connection.min", 5);
		String fullURL = "jdbc:postgresql://" + url + "/" + db;

		this.server_name = Gberry.serverName;

		if (url == null || user == null || pass == null) {
			Bukkit.getLogger().severe("no db login found.");
			return;
		}

		Bukkit.getLogger().severe("Loading BanManager DB Conection");

		BanManager.hikariConfig = new HikariConfig();
		BanManager.hikariConfig.setJdbcUrl(fullURL);
		BanManager.hikariConfig.setUsername(user);
		BanManager.hikariConfig.setPassword(pass);
		BanManager.hikariConfig.setConnectionTimeout(2 * 1000);
		BanManager.hikariConfig.setIdleTimeout(60 * 1000);
		BanManager.hikariConfig.setMaxLifetime(300 * 1000);
		BanManager.hikariConfig.setMinimumIdle(1);
		BanManager.hikariConfig.setMaximumPoolSize(maxConnections);

		try {
			BanManager.hikariDataSource = new HikariDataSource(BanManager.hikariConfig);
		} catch (Exception e) {
			e.printStackTrace();
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
		}

		this.server_name = Gberry.serverName;

		this.getCommand("ban").setExecutor(new Ban(this));
		this.getCommand("kick").setExecutor(new Kick(this));
		this.getCommand("mute").setExecutor(new Mute(this));
		this.getCommand("unban").setExecutor(new Unban(this));
		this.getCommand("unmute").setExecutor(new Unmute(this));
		this.getCommand("judgementday").setExecutor(new JudgementDayCommand());
		this.getCommand("banwave").setExecutor(new BanWaveCommand());

		this.getServer().getPluginManager().registerEvents(new GSyncListener(), this);
	}

	@Override
	public void onDisable() {

	}

	public void syncAcrossServers(final String uuid, final Timestamp timestamp, final String type, final String reason) {
		new BukkitRunnable() {

			@Override
			public void run() {
				List<String> args = new ArrayList<>();
				args.add("BanManager");

				if (type.equals("ban")) {
					args.add("BanSync");
				} else {
					return;
				}

				args.add(uuid);

				Gberry.sendGSyncEvent(args);
			}

		}.runTaskAsynchronously(this);
	}

	public String nicePunishmentTime(long time) {
		if (time == BanManager.PERMANENT_TIME) {
			return "forever";
		}

		// Cut off millis
		time /= 1000;

		if (time < 3600) {
			return time / 60 + " Minute(s)";
		} else if (time < 84600) {
			return time / 3600  + " Hour(s)";
		} else if (time < 86400 * 7) {
			return time / 86400 + " Day(s)";
		} else if (time < 86400 * 31) {
			return time / (86400 * 7) + " Week(s)";
		} else {
			return time / (86400 * 30) + " Month(s)";
		}
	}

	public long getTimeToPunish(String[] args, CommandSender sender, PUNISHMENT_TYPE type) {
		if (type == PUNISHMENT_TYPE.MUTE && args.length < 2) {
			return -1;
		} else if (type == PUNISHMENT_TYPE.BAN && args.length < 2) {
			return BanManager.PERMANENT_TIME;
		}

		if (args[1].equalsIgnoreCase("perma") && sender.hasPermission("badlion.globalmod")) {
			return BanManager.PERMANENT_TIME;
		}

		if (args[1].substring(0, 1).matches("[0-9]")) {
			try {
				char timeLength = args[1].charAt(args[1].length() - 1);
				long time = Long.parseLong(args[1].substring(0, args[1].length() - 1));
				if (time < 1) {
					return -10;
				}

				if (timeLength == 's' || timeLength == 'S') {
					time = time * 1000;
				} else if (timeLength == 'm' || timeLength == 'M') {
					time = time * 1000 * 60;
				} else if (timeLength == 'h' || timeLength == 'H') {
					time = time * 1000 * 60 * 60;
				} else if (timeLength == 'd' || timeLength == 'D') {
					time = time * 1000 * 60 * 60 * 24;
				} else if (timeLength == 'w' || timeLength == 'W') {
					time = time * 1000 * 60 * 60 * 24 * 7;
				} else if (timeLength == 'n' || timeLength == 'N') {
					time = time * 1000 * 60 * 60 * 24 * 30;
				} else if (timeLength == 'y' || timeLength == 'Y') {
					time = time * 1000 * 60 * 60 * 24 * 365;
				} else {
					sender.sendMessage(ChatColor.RED + "Valid date format required (#s/m/h/d/w/n)");
					return -30;
				}

				if (sender != null && !sender.hasPermission("bm.admin") && time > (long) 1000 * 60 * 60 * 24 * 365) {
					sender.sendMessage(ChatColor.RED + "You are not allowed to ban/mute for more than 1 year");
					return -20;
				}

				return time;
			} catch (Exception e) {
				sender.sendMessage(ChatColor.RED + "Invalid date format: " + args[1]);
				return -30;
			}
		}

		if (type == PUNISHMENT_TYPE.BAN) {
			return BanManager.PERMANENT_TIME;
		}

		sender.sendMessage(ChatColor.RED + "Invalid date format: " + args[1]);
		return -30;
	}

	private void sendPokenickAPM(CommandSender sender) {
		sender.sendMessage(Gberry.getLineSeparator(ChatColor.DARK_RED));
		sender.sendMessage(ChatColor.RED + "You cannot punish a Famous+ member for more than a 2 hour mute.");
		sender.sendMessage(ChatColor.RED + "Send your evidence in a PM to Pokenick https://www.badlion.net/messages/new?recipients=Pokenick");
		sender.sendMessage(Gberry.getLineSeparator(ChatColor.DARK_RED));
	}

	public void insertPunishment(final CommandSender sender, final String punisherUUID, final PUNISHMENT_TYPE type,
	                             final String punishedName, final String reason, final long time) {
		Player punishedPlayer = Bukkit.getPlayerExact(punishedName);

		// UUID support
		if (punishedPlayer == null) {
			try {
				if (punishedName.length() == 36) {
					punishedPlayer = Bukkit.getPlayer(UUID.fromString(punishedName));
				} else if (punishedName.length() == 32) {
					punishedPlayer = Bukkit.getPlayer(StringCommon.uuidFromStringWithoutDashes(punishedName));
				}
			} catch (Exception e) {
				// Do nothing
			}
		}

		Gberry.log("BM", "Punishment of " + type.name() + " received for " + punishedName);

		final String niceType;

		switch (type) {
			case BAN:
				niceType = "banned";
				break;
			case MUTE:
				niceType = "muted";
				break;
			case KICK:
				niceType = "kicked";
				break;
			default:
				niceType = "punished";
		}

		final Player finalPunishedPlayer = punishedPlayer;
		// Run next tick to avoid errors with order of operations
		BukkitUtil.runTaskNextTick(new Runnable() {
			@Override
			public void run() {
				if (finalPunishedPlayer != null && (type == PUNISHMENT_TYPE.BAN || type == PUNISHMENT_TYPE.KICK)) {
					if (!sender.hasPermission("badlion.admin") && finalPunishedPlayer.hasPermission("badlion.famousplus") && type != PUNISHMENT_TYPE.KICK) {
						// Do nothing
					}
					if (time == BanManager.PERMANENT_TIME) {
						finalPunishedPlayer.kickPlayer("You have been " + niceType + " for " + reason);
					} else {
						finalPunishedPlayer.kickPlayer("You have been temporarily " + niceType + " for " + reason);
					}
				}
			}
		});

		final java.sql.Timestamp unpunishTime;
		final long unpunishUTCTimestamp;
		if (time == BanManager.PERMANENT_TIME) {
			unpunishTime = new java.sql.Timestamp(0);
			unpunishUTCTimestamp = 0;
		} else if (time != -1) {
			unpunishTime = new java.sql.Timestamp(new DateTime(DateTimeZone.UTC).getMillis() + time);
			unpunishUTCTimestamp = new DateTime(DateTimeZone.UTC).getMillis() + time;
		} else {
			unpunishTime = null;
			unpunishUTCTimestamp = 0;
		}

		if (time != -1) {
			Gberry.log("BM", "Unpunishment time of " + unpunishTime.toString());
		}

		final String finalNiceType = niceType;
		Bukkit.getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
			public void run() {
				String punishedUUID = null;
				UUID tmpUUID = null;

				// UUID Support (dashes, w/o dashes, and usernames)
				if (punishedName.length() == 36) {
					tmpUUID = UUID.fromString(punishedName);
					punishedUUID = tmpUUID.toString();
				} else if (punishedName.length() == 32) {
					tmpUUID = StringCommon.uuidFromStringWithoutDashes(punishedName);
					punishedUUID = tmpUUID.toString();
				} else {
					UUID uuid = Gberry.getOfflineUUID(punishedName);
					if (uuid != null) {
						punishedUUID = uuid.toString();
					}
				}

				if (punishedUUID == null) {
					sender.sendMessage(ChatColor.RED + punishedName + "'s UUID could not be found!");
					return;
				}

				// If we are not an admin, the person we are punishing is a Famous+, and the punishment is not a mute
				if (!sender.hasPermission("badlion.admin")
						&& GPermissions.plugin.userHasPermission(punishedUUID, "badlion.famousplus")) {
					if (type == PUNISHMENT_TYPE.BAN || (type == PUNISHMENT_TYPE.MUTE && (time == BanManager.PERMANENT_TIME || time > 1000 * 60 * 60 * 2))) {
						BanManager.this.sendPokenickAPM(sender);
						return;
					}
				}

				long ip = Gberry.getOfflineIP(punishedName);
				if (ip == -1 && tmpUUID != null) {
					ip = Gberry.getOfflineIP(tmpUUID);
				}

				if (ip == -1) {
					sender.sendMessage(ChatColor.RED + punishedName + "'s IP could not be found!");
				}

				String query = "SELECT * FROM punishments WHERE punished_uuid = ? AND type = " + PUNISHMENT_TYPE.MUTE.ordinal() + " AND false_punishment = 'false' ORDER BY punishment_id DESC;";

				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;

				// Custom mute system
				java.sql.Timestamp unpunishTimeAgain;
				long unpunishUTCTimestampAgain;
				int totalCount = 1; // Include this punishment
				long totalMuteTime = 0;
				if (type == PUNISHMENT_TYPE.MUTE && time == -1) {
					try {
						connection = BanManager.getConnection();
						ps = connection.prepareStatement(query);
						ps.setString(1, punishedUUID);

						rs = Gberry.executeQuery(connection, ps);

						int thisReason = 0;
						while (rs.next()) {
							// Only start counting from implementation date
							if (new DateTime((DateTimeZone.UTC)).minusMonths(3).getMillis() > newMuteSystemTime.getMillis()) {
								if (rs.getTimestamp("punishment_time").getTime() > new DateTime((DateTimeZone.UTC)).minusMonths(3).getMillis()) {
									totalCount++;
								} else {
									continue;
								}
							} else {
								if (rs.getTimestamp("punishment_time").getTime() > newMuteSystemTime.getMillis()) {
									totalCount++;
								} else {
									continue;
								}
							}

							// Handle logic for reason +
							if (rs.getString("reason").equalsIgnoreCase(reason)) {
								try {
									Mute.MUTE_REASON muteReason = Mute.MUTE_REASON.valueOf(reason.toUpperCase().replace(" ", "_"));
									if (Mute.exemptSixMonthTimes.contains(muteReason)) {
										thisReason++;
									} else if (rs.getTimestamp("unpunish_time").getTime() > new DateTime(DateTimeZone.UTC).minusMonths(6).getMillis()) {
										thisReason++;
									}
								} catch (IllegalArgumentException e) {
									// Pass
								}

							}
						}

						if (thisReason > 3) {
							thisReason = 3;
						}

						try {
							Mute.MUTE_REASON muteReason = Mute.MUTE_REASON.valueOf(reason.toUpperCase().replace(" ", "_"));
							List<Integer> times = Mute.punishmentTimes.get(muteReason);
							long thisPunishmentTime = times.get(thisReason);

							if (totalCount >= 10) {
								if (thisPunishmentTime < 60 * 60 * 24 * 90) {
									thisPunishmentTime = 60 * 60 * 24 * 90;
								}
							} else if (totalCount >= 5) {
								if (thisPunishmentTime < 60 * 60 * 24 * 7) {
									thisPunishmentTime = 60 * 60 * 24 * 7;
								}
							}

							// To millis
							thisPunishmentTime *= 1000;

							totalMuteTime = thisPunishmentTime;

							// New custom punishment time
							unpunishTimeAgain = new java.sql.Timestamp(new DateTime(DateTimeZone.UTC).getMillis() + thisPunishmentTime);
							unpunishUTCTimestampAgain = new DateTime(DateTimeZone.UTC).getMillis() + thisPunishmentTime;
						} catch (IllegalArgumentException e) {
							unpunishTimeAgain = unpunishTime;
							unpunishUTCTimestampAgain = unpunishUTCTimestamp;
							totalMuteTime = time;
						}
					} catch (SQLException e) {
						e.printStackTrace();
						return;
					} finally {
						Gberry.closeComponents(rs, ps, connection);
					}
				} else {
					unpunishTimeAgain = unpunishTime;
					unpunishUTCTimestampAgain = unpunishUTCTimestamp;
					totalMuteTime = time;
				}

				switch (type) {
					case BAN:
						if ((sender instanceof Player || !BanManager.isAllowedMultipleConsoleBans) && isBanned(punishedUUID)) {
							sender.sendMessage(ChatColor.RED + "This player is banned already.");
							return;
						}
						break;
					case MUTE:
						if (isMuted(punishedUUID)) {
							sender.sendMessage(ChatColor.RED + "This player is muted already.");
							return;
						}
						break;
				}

				query = "INSERT INTO punishments (punished_uuid, punisher_uuid, punishment_time, unpunish_time, server, reason, type, ip) values (?, ?, ?, ?, ?, ?, ?, ?)";


				try {
					connection = BanManager.getConnection();
					ps = connection.prepareStatement(query);

					ps.setString(1, punishedUUID);
					ps.setString(2, punisherUUID);
					ps.setTimestamp(3, new java.sql.Timestamp(new DateTime(DateTimeZone.UTC).getMillis()));
					ps.setTimestamp(4, unpunishTimeAgain);
					ps.setString(5, server_name);
					ps.setString(6, reason);
					ps.setInt(7, type.ordinal());
					ps.setLong(8, ip);

					ps.executeUpdate();
				} catch (SQLException e) {
					e.printStackTrace();
					sender.sendMessage(ChatColor.RED + "Something broke when trying to punish this player. Maybe they are already punished? If not contact an admin.");
					return;
				} finally {
					Gberry.closeComponents(ps, connection);
				}

				// Send to caches after in database so MCP can have it stored
				switch (type) {
					case BAN:
						pSync("ban", punishedUUID, unpunishUTCTimestampAgain, reason);
						Unban.banTimes.put(UUID.fromString(punishedUUID), new java.sql.Timestamp(new DateTime(DateTimeZone.UTC).getMillis()));
						break;
					case MUTE:
						pSync("mute", punishedUUID, unpunishUTCTimestampAgain, reason);
						Unmute.muteTimes.put(UUID.fromString(punishedUUID), new java.sql.Timestamp(new DateTime(DateTimeZone.UTC).getMillis()));
						break;
				}

				// Do we need to IP ban?
				if (ip != -1 && type == PUNISHMENT_TYPE.BAN && time == BanManager.PERMANENT_TIME) {
					query = "SELECT * FROM punishments WHERE type = " + PUNISHMENT_TYPE.BAN.ordinal() + " AND ip = ?;";

					connection = null;
					ps = null;
					rs = null;

					int numOfBans = 0;

					try {
						connection = BanManager.getConnection();
						ps = connection.prepareStatement(query);

						ps.setLong(1, ip);

						rs = Gberry.executeQuery(connection, ps);
						while (rs.next()) {
							Timestamp ipPunishedTime = rs.getTimestamp("punishment_time");
							Timestamp ipUnpunishedTime = rs.getTimestamp("unpunish_time");

							if (ipUnpunishedTime.getTime() < BanManager.default_perm_time.getTime()) {
								numOfBans += 1;
							// Unpunishment is later than now and before the punishment time (not less than) and the unpunishment time is at least 29 days ahead of the punishment time
							} else if (ipUnpunishedTime.getTime() > new DateTime(DateTimeZone.UTC).getMillis() && ipUnpunishedTime.getTime() -  ipPunishedTime.getTime() > 0 && ipUnpunishedTime.getTime() -  ipPunishedTime.getTime() >= 60 * 60 * 24 * 29 * 1000L) {
								numOfBans += 1;
							}
						}
					} catch (SQLException e) {
						e.printStackTrace();
						sender.sendMessage(ChatColor.RED + "Something broke when trying to punish this player. Contact an admin.");
						return;
					} finally {
						Gberry.closeComponents(rs, ps, connection);
					}

					query = "INSERT INTO banned_ips (ip, reason, ban_time) VALUES (?, ?, ?);";

					if (numOfBans >= 2) {
						try {
							connection = Gberry.getConnection();
							ps = connection.prepareStatement(query);

							ps.setLong(1, ip);
							ps.setString(2, "Too many bans");
							ps.setTimestamp(3, new java.sql.Timestamp(new DateTime(DateTimeZone.UTC).getMillis()));

							Gberry.executeUpdate(connection, ps);
						} catch (SQLException e) {
							e.printStackTrace();
							sender.sendMessage(ChatColor.RED + "Something broke when trying to punish this player. Contact an admin.");
							return;
						} finally {
							Gberry.closeComponents(ps, connection);
						}
					}
				}

				// Sync servers
				final String finalPunishedUUID = punishedUUID;
				final java.sql.Timestamp unpunishTimeAgainTmp = unpunishTimeAgain;
				final int finalTotalCount = totalCount;
				final long finalMuteTime = totalMuteTime;
				BanManager.this.getServer().getScheduler().runTask(BanManager.this, new Runnable() {
					@Override
					public void run() {
						BanManager.this.getServer().getPluginManager().callEvent(new PunishedPlayerEvent(UUID.fromString(finalPunishedUUID), type));
						BanManager.this.syncAcrossServers(finalPunishedUUID, unpunishTimeAgainTmp, type.name().toLowerCase(), reason);

						// Send extra information
						if (type == PUNISHMENT_TYPE.MUTE) {
							Player player = Bukkit.getPlayer(UUID.fromString(finalPunishedUUID));
							if (player != null) {
								player.sendMessage(Gberry.getLineSeparator(ChatColor.RED));
								player.sendMessage(ChatColor.RED + "You have a total of " + ChatColor.UNDERLINE + finalTotalCount + ChatColor.RESET + ChatColor.RED + " mutes since Badlion Punishments 2.0 went into effect.");
								player.sendMessage(ChatColor.RED + "The severity of the mute will increase with each punishment. You should be careful what you say to others.");
							}
						}

						String punishmentString = ChatColor.RED + punishedName + " has been " + finalNiceType + " for " + reason;
						if (type != PUNISHMENT_TYPE.KICK) {
							punishmentString += " for a period of " + nicePunishmentTime(finalMuteTime);
						}

						if (type != PUNISHMENT_TYPE.MUTE && BanManager.useLineSeparators) {
							Bukkit.broadcastMessage(Gberry.getLineSeparator(ChatColor.RED));
						}
						Bukkit.broadcastMessage(punishmentString);
						if (type != PUNISHMENT_TYPE.MUTE && BanManager.useLineSeparators) {
							Bukkit.broadcastMessage(Gberry.getLineSeparator(ChatColor.RED));
						}
					}
				});
			}
		});

	}

	public boolean isBanned(String uuid) {
		String query = "SELECT unpunish_time, reason FROM punishments WHERE type = "
				+ PUNISHMENT_TYPE.BAN.ordinal() + " AND (unpunish_time < ? OR unpunish_time > ?) AND punished_uuid = ?;";

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			connection = BanManager.getUnsafeConnection();
			ps = connection.prepareStatement(query);
			ps.setTimestamp(1, BanManager.default_perm_time);
			ps.setTimestamp(2, new Timestamp(new DateTime(DateTimeZone.UTC).getMillis()));
			ps.setString(3, uuid);
			rs = ps.executeQuery();

			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(rs, ps, connection);
		}
		return false;
	}

	public boolean isMuted(String uuid) {
		String query = "SELECT unpunish_time, reason FROM punishments WHERE type = "
				+ PUNISHMENT_TYPE.MUTE.ordinal() + " AND (unpunish_time < ? OR unpunish_time > ?) AND punished_uuid = ?;";

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			connection = BanManager.getUnsafeConnection();
			ps = connection.prepareStatement(query);
			ps.setTimestamp(1, BanManager.default_perm_time);
			ps.setTimestamp(2, new Timestamp(new DateTime(DateTimeZone.UTC).getMillis()));
			ps.setString(3, uuid);
			rs = ps.executeQuery();

			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			Gberry.closeComponents(rs, ps, connection);
		}

		return false;
	}

	public void pSync(final String punishmentType, final String uuid) {
		List<String> cmds = new ArrayList<>();
		cmds.add("psync " + punishmentType + " " + uuid);
		Gberry.sendToAll(cmds);

		// Send new system also (we will be removing the above soon)
		try {
			// TODO: Change to use local region instead of master
			JSONObject data = new JSONObject();
			List<String> args = new ArrayList<>();
			args.add(punishmentType);
			args.add(uuid);
			data.put("args", args);

			// Ship it off
			Gberry.contactMCP("psync", data);
		} catch (HTTPRequestFailException e) {
			// Swallow it for now
		}
	}

	public void pSync(final String punishmentType, final String uuid, final long unpunishUTCTimestamp, final String reason) {
		List<String> cmds = new ArrayList<>();
		cmds.add("psync " + punishmentType + " " + uuid + " " + unpunishUTCTimestamp + " " + reason);
		Gberry.sendToAll(cmds);

		// Send new system also (we will be removing the above soon)
		try {
			// TODO: Change to use local region instead of master
			JSONObject data = new JSONObject();
			List<String> args = new ArrayList<>();
			args.add(punishmentType);
			args.add(uuid);
			data.put("args", args);

			// Ship it off
			Gberry.contactMCP("psync", data);
		} catch (HTTPRequestFailException e) {
			// Swallow it for now
		}
	}

	public enum PUNISHMENT_TYPE {BAN, MUTE, KICK}

}