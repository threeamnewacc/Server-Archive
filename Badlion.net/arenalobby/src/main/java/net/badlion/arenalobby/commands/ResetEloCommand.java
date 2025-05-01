package net.badlion.arenalobby.commands;

import net.badlion.arenacommon.ArenaCommon;
import net.badlion.arenalobby.ArenaLobby;
import net.badlion.arenalobby.bukkitevents.RatingChangeEvent;
import net.badlion.arenalobby.bukkitevents.RatingRetrievedEvent;
import net.badlion.arenalobby.ladders.Ladder;
import net.badlion.arenalobby.managers.LadderManager;
import net.badlion.arenalobby.managers.RatingManager;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.events.GSyncEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class ResetEloCommand extends GCommandExecutor implements Listener {

	private static Map<UUID, Integer> resettingElo = new HashMap<>();
	private static Map<UUID, Integer> eloResetID = new ConcurrentHashMap<>();

	public ResetEloCommand() {
		super(0); // Minimum 0 arguments

		// Fetch all elo resets
		this.fetchAllEloResets();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	// High priority so it gets send near the end of the string of messages
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		if (ResetEloCommand.eloResetID.containsKey(event.getPlayer().getUniqueId())) {
			event.getPlayer().sendFormattedMessage("{0}You have an ELO reset token! Type \"{1}\" to redeem it!", ChatColor.GOLD, "/resetelo");
		}
	}

	@EventHandler
	public void onGSyncEvent(GSyncEvent event) {
		if (event.getArgs().size() != 3) {
			return;
		}

		String subChannel = event.getArgs().get(0);
		if (subChannel.equals("ResetEloSync")) {
			UUID uuid = UUID.fromString(event.getArgs().get(1));
			int newResetID = Integer.valueOf(event.getArgs().get(2));

			if (newResetID != -1) {
				ResetEloCommand.eloResetID.put(uuid, newResetID);
			} else {
				ResetEloCommand.eloResetID.remove(uuid);
			}
		}
	}

	@Override
	public void onGroupCommand(Command command, String label, final String[] args) {
		if (args.length == 1) {
			if (ResetEloCommand.resettingElo.containsKey(this.player.getUniqueId())) {
				if (args[0].equalsIgnoreCase("confirm")) {
					final Integer data = ResetEloCommand.resettingElo.remove(this.player.getUniqueId());

					ArenaLobby.getInstance().getServer().getScheduler().runTaskAsynchronously(ArenaLobby.getInstance(), new Runnable() {
						public void run() {
							String query = "DELETE FROM elo_resets WHERE reset_id = ?;";
							Connection connection = null;
							PreparedStatement ps = null;
							ResultSet rs = null;

							try {
								connection = Gberry.getConnection();
								ps = connection.prepareStatement(query);

								// Remove from table
								ps.setInt(1, ResetEloCommand.eloResetID.remove(ResetEloCommand.this.player.getUniqueId()));

								Gberry.executeUpdate(connection, ps);

								if (data == 0) {
									query = "DELETE FROM ladder_ratings_s14 WHERE uuid = ?;";
								}

								ps = connection.prepareStatement(query);

								ps.setString(1, ResetEloCommand.this.player.getUniqueId().toString());

								Gberry.executeUpdate(connection, ps);

								// Delete all ratings from cache
								for (Ladder ladder : LadderManager.getLadderMap(ArenaCommon.LadderType.RANKED_1V1).values()) {
									RatingManager.deleteRating(ResetEloCommand.this.player.getUniqueId(), ladder);
								}

								// Update our tablist to have no ratings on it.
								new BukkitRunnable() {
									@Override
									public void run() {
										final Map<Ladder, Double> ratings = new ConcurrentHashMap<>();
										for (Ladder ladder : LadderManager.getLadderMap(ArenaCommon.LadderType.RANKED_1V1).values()) {
											ratings.put(ladder, RatingManager.DEFAULT_RATING);
										}
										ArenaLobby.getInstance().getServer().getPluginManager().callEvent(
												new RatingChangeEvent(ResetEloCommand.this.player.getUniqueId(), -1.0, (ConcurrentHashMap<Ladder, Double>) ratings));
									}
								}.runTask(ArenaLobby.getInstance());


								RatingManager.updateGlobalRating(ResetEloCommand.this.player.getUniqueId());

								// TODO: RIP THIS CODE FOR GLOBAL ELO REWORK
								// Set wins/losses to proper amount too
								/*String query = "SELECT * FROM ladder_ratings_s12 WHERE uuid = ? AND lid != 0;";
								ps = connection.prepareStatement(query);

								ps.setString(1, ResetEloCommand.this.player.getUniqueId().toString());

								rs = Gberry.executeQuery(connection, ps);

								int wins = 0;
								int losses = 0;
								while (rs.next()) {
									wins += rs.getInt("wins");
									losses += rs.getInt("losses");
								}



								ps = connection.prepareStatement("UPDATE ladder_ratings_s12 SET wins = ?, losses = ? WHERE uuid = ? AND lid = 0");
								ps.setInt(1, wins);
								ps.setInt(2, losses);
								ps.setString(3, ResetEloCommand.this.player.getUniqueId().toString());
								Gberry.executeUpdate(connection, ps);*/

								ResetEloCommand.this.player.sendFormattedMessage("{0}Your ELO has been reset.", ChatColor.GREEN);

								int newResetID = -1;

								// Cache next reset ID if they have another elo reset
								query = "SELECT * FROM elo_resets WHERE uuid = ?;";
								ps = connection.prepareStatement(query);

								ps.setString(1, ResetEloCommand.this.player.getUniqueId().toString());

								rs = Gberry.executeQuery(connection, ps);

								if (rs.next()) {
									newResetID = rs.getInt("reset_id");

									// Add the Elo reset to our cache
									ResetEloCommand.eloResetID.put(ResetEloCommand.this.player.getUniqueId(), newResetID);
								}

								// Sync across network
								List<String> args = new ArrayList<>();
								args.add("ResetEloSync");
								args.add(ResetEloCommand.this.player.getUniqueId() + "");
								args.add(newResetID + "");

								// Send GSync event
								Gberry.sendGSyncEvent(args);
							} catch (SQLException e) {
								e.printStackTrace();
							} finally {
								Gberry.closeComponents(rs, ps, connection);
							}
						}

					});
				} else if (args[0].equalsIgnoreCase("deny")) {
					this.player.sendFormattedMessage("{0}Cancelled ELO reset.", ChatColor.YELLOW);
					ResetEloCommand.resettingElo.remove(this.player.getUniqueId());
				} else {
					this.usageConfirmDeny(this.player);
				}
			} else if (ResetEloCommand.eloResetID.containsKey(this.player.getUniqueId())) {
				int data;

				if (args[0].equals("1.7/1.8")) {
					data = 0;
				} else {
					this.usage(this.player);
					return;
				}

				if (data == 0) {
					this.player.sendFormattedMessage("{0}You have chosen to reset your ELO on all {1}ladders.", ChatColor.GOLD, ChatColor.RED + "1.7/1.8 " + ChatColor.GOLD);
				}

				this.usageConfirmDeny(this.player);

				ResetEloCommand.resettingElo.put(this.player.getUniqueId(), data);
			} else {
				this.player.sendFormattedMessage("{0}You do not have any ELO resets available. You may purchase one at {1}", ChatColor.RED, "http://store.badlion.net/");
			}
		} else {
			this.usage(this.player);
		}
	}

	/**
	 * Extend/override this method
	 */
	public void onSenderCommand(final CommandSender sender, Command command, String label, final String[] args) {
		if (args[0].equalsIgnoreCase("add")) {
			ArenaLobby.getInstance().getServer().getScheduler().runTaskAsynchronously(ArenaLobby.getInstance(), new Runnable() {
				public void run() {
					String query = "INSERT INTO elo_resets (uuid) VALUES (?);";
					Connection connection = null;
					PreparedStatement ps = null;
					ResultSet rs = null;


					UUID uuid = Gberry.getOfflineUUID(args[1]);
					if (uuid == null) {
						Bukkit.getLogger().info("UUID not found");
						return;
					}

					try {
						connection = Gberry.getConnection();
						ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

						ps.setString(1, uuid.toString());

						Gberry.executeUpdate(connection, ps);
						rs = ps.getGeneratedKeys();

						if (rs.next()) {
							ResetEloCommand.eloResetID.put(uuid, ps.getGeneratedKeys().getInt("reset_id"));

							List<String> args = new ArrayList<>(Arrays.asList(new String[]{"ResetEloSync", uuid.toString(), "" + ps.getGeneratedKeys().getInt("reset_id")}));
							Gberry.sendGSyncEvent(args);
						}
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						Gberry.closeComponents(rs, ps, connection);
					}

					sender.sendFormattedMessage("{0}Elo reset given to {1}", ChatColor.GREEN, args[1]);
				}
			});
		}
	}

	@Override
	public void usage(CommandSender sender) {
		sender.sendFormattedMessage("{0}How to reset your ELO for {1}", ChatColor.GOLD, ChatColor.RED + "1.7/1.8" + ChatColor.GOLD + ":");
		sender.sendMessage(ChatColor.YELLOW + " /resetelo 1.7/1.8");
	}

	public void usageConfirmDeny(CommandSender sender) {
		sender.sendFormattedMessage("{0}Use \"{1}\" to confirm or deny the reset.", ChatColor.AQUA, "/resetelo confirm/deny");
	}

	private void fetchAllEloResets() {
		ArenaLobby.getInstance().getServer().getScheduler().runTaskAsynchronously(ArenaLobby.getInstance(), new Runnable() {
			public void run() {
				String query = "SELECT * FROM elo_resets;";
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;

				try {
					connection = Gberry.getConnection();
					ps = connection.prepareStatement(query);
					rs = Gberry.executeQuery(connection, ps);

					while (rs.next()) {
						ResetEloCommand.eloResetID.put(UUID.fromString(rs.getString("uuid")), rs.getInt("reset_id"));
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					Gberry.closeComponents(rs, ps, connection);
				}
			}
		});
	}

}
