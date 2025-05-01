package net.badlion.sgrankedmatchmaker.commands;

import net.badlion.gberry.Gberry;
import net.badlion.sgrankedmatchmaker.SGRankedMatchMaker;
import net.badlion.sgrankedmatchmaker.managers.RankedLeftManager;
import net.badlion.gberry.utils.BukkitUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.joda.time.DateTime;

import java.sql.*;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GiftMatchesCommand implements CommandExecutor, Listener {

	public static int FREE_NUM_OF_MATCHES_TO_GIVE = 5;

	private static ConcurrentHashMap<UUID, Boolean> givenMatches = new ConcurrentHashMap<>();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, final String[] args) {
		if (sender instanceof Player) {
			final Player player = (Player) sender;

			if (args.length == 0) {
				sender.sendMessage(ChatColor.RED + "Command usage: /giftmatches [name]");
				sender.sendMessage(ChatColor.YELLOW + "This will give the specified player 5 extra ranked matches :)");
				return true;
			}

			BukkitUtil.runTaskAsync(new Runnable() {
				@Override
				public void run() {
					final UUID uuid = Gberry.getOfflineUUID(args[0]);
					if (uuid == null) {
						player.sendMessage(ChatColor.RED + "Player not found, invalid username entered.");
					} else {
						BukkitUtil.runTask(new Runnable() {
							@Override
							public void run() {
								Boolean hasGiven = GiftMatchesCommand.givenMatches.get(player.getUniqueId());
								if (hasGiven == null || !hasGiven) {
									GiftMatchesCommand.givenMatches.put(player.getUniqueId(), true);
								} else {
									player.sendMessage(ChatColor.RED + "You have already given out your extra ranked matches today.");
									return;
								}

								// Persist in DB
								BukkitUtil.runTaskAsync(new Runnable() {
									@Override
									public void run() {
										GiftMatchesCommand.this.addGivenMatchesRecord(player.getUniqueId());
									}
								});

								Player gifted = SGRankedMatchMaker.getInstance().getServer().getPlayer(uuid);
								if (gifted != null) {
									gifted.sendMessage(ChatColor.GREEN + "You have been given 20 extra ranked matches by " + player.getDisplayName());
									player.sendMessage(ChatColor.GREEN + "You have given your extra ranked matches for the day to " + gifted.getName());
								} else {
									player.sendMessage(ChatColor.GREEN + "You have given your extra ranked matches for the day to " + args[0]);
								}

								RankedLeftManager.removeNumberOfMatches(uuid, GiftMatchesCommand.FREE_NUM_OF_MATCHES_TO_GIVE);

								for (int i = 0; i < 5; i++) {
									SGRankedMatchMaker.getInstance().getServer().dispatchCommand(SGRankedMatchMaker.getInstance().getServer().getConsoleSender(),
											"extrasgrankedmatches " + uuid);
								}
							}
						});
					}
				}
			});
		}
		return true;
	}

	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent event) {
		BukkitUtil.runTaskAsync(new Runnable() {
			@Override
			public void run() {
				GiftMatchesCommand.givenMatches.put(event.getPlayer().getUniqueId(), hasAlreadyGivenMatchesToday(event.getPlayer().getUniqueId()));
			}
		});
	}

	public boolean hasAlreadyGivenMatchesToday(UUID uuid) {
		String query = "SELECT * FROM sg_extra_matches WHERE uuid = ? AND given_date = ?;";

		Connection connection = null;
		ResultSet rs = null;
		PreparedStatement ps = null;

		// First get their yolo queue ratings
		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, uuid.toString());
			ps.setTimestamp(2, new Timestamp(new DateTime().toDate().getTime()));
			rs = Gberry.executeQuery(connection, ps);

			return rs.next();
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
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return false;
	}

	public void addGivenMatchesRecord(UUID uuid) {
		String query = "INSERT INTO sg_extra_matches (uuid, given_date) VALUES (?, ?);";

		Connection connection = null;
		PreparedStatement ps = null;

		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, uuid.toString());
			ps.setTimestamp(2, new Timestamp(new DateTime().toDate().getTime()));
			Gberry.executeUpdate(connection, ps);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
