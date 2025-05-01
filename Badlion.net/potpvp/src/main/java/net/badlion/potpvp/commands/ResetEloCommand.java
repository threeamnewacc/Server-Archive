package net.badlion.potpvp.commands;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.events.GSyncEvent;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.ladders.Ladder;
import net.badlion.potpvp.managers.RatingManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

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

public class ResetEloCommand extends GCommandExecutor implements Listener {

	private static Map<UUID, Integer> resettingElo = new HashMap<>();
	private static Map<UUID, Integer> eloResetID = new ConcurrentHashMap<>();

    public ResetEloCommand() {
        super(0); // Minimum 0 arguments

	    // Fetch all elo resets
	    this.fetchAllEloResets();
    }

	@EventHandler(priority = EventPriority.HIGHEST) // High priority so it gets send near the end of the string of messages
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		if (ResetEloCommand.eloResetID.containsKey(event.getPlayer().getUniqueId())) {
			event.getPlayer().sendMessage(ChatColor.GOLD + "You have an ELO reset token! Type \"/resetelo\" to redeem it!");
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

					PotPvP.getInstance().getServer().getScheduler().runTaskAsynchronously(PotPvP.getInstance(), new Runnable() {
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
									query = "DELETE FROM ladder_ratings_s12 WHERE uuid = ?;";
								} else if (data == 1) {
									query = "DELETE FROM ladder_ratings_s12_v19 WHERE uuid = ?;";
								} else if (data == 2) {
									query = "DELETE FROM ladder_ratings_s12 WHERE uuid = ?;";
									query += "DELETE FROM ladder_ratings_s12_v19 WHERE uuid = ?;";
								}

								ps = connection.prepareStatement(query);

								ps.setString(1, ResetEloCommand.this.player.getUniqueId().toString());

								if (data == 2) {
									ps.setString(2, ResetEloCommand.this.player.getUniqueId().toString());
								}

								Gberry.executeUpdate(connection, ps);

								// Delete all ratings from cache
								for (Ladder ladder : Ladder.getLadderMap(Ladder.LadderType.OneVsOneRanked).values()) {
									RatingManager.deleteRating(ResetEloCommand.this.player.getUniqueId(), ladder);
								}

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

								ResetEloCommand.this.player.sendMessage(ChatColor.GREEN + "Your ELO has been reset.");

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
					this.player.sendMessage(ChatColor.YELLOW + "Cancelled ELO reset.");
					ResetEloCommand.resettingElo.remove(this.player.getUniqueId());
				} else {
					this.usageConfirmDeny(this.player);
				}
			} else if (ResetEloCommand.eloResetID.containsKey(this.player.getUniqueId())) {
				int data;

				if (args[0].equals("1.7/1.8")) {
					data = 0;
				} else if (args[0].equals("1.9")) {
					data = 1;
				} else if (args[0].equalsIgnoreCase("both")) {
					data = 2;
				} else {
					this.usage(this.player);
					return;
				}

				if (data == 0) {
					this.player.sendMessage(ChatColor.GOLD + "You have chosen to reset your ELO on all " + ChatColor.RED + "1.7/1.8 " + ChatColor.GOLD + "ladders.");
				} else if (data == 1) {
					this.player.sendMessage(ChatColor.GOLD + "You have chosen to reset your ELO on all " + ChatColor.RED + "1.9 " + ChatColor.GOLD + "ladders.");
				} else {
					this.player.sendMessage(ChatColor.GOLD + "You have chosen to reset your ELO on all " + ChatColor.RED + "1.7/1.8 AND 1.9 " + ChatColor.GOLD + "ladders.");
				}

				this.usageConfirmDeny(this.player);

				ResetEloCommand.resettingElo.put(this.player.getUniqueId(), data);
			} else {
				this.player.sendMessage(ChatColor.RED + "You do not have any ELO resets available. You may purchase one at http://store.badlion.net/");
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
            PotPvP.getInstance().getServer().getScheduler().runTaskAsynchronously(PotPvP.getInstance(), new Runnable() {
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

		                    List<String> args = new ArrayList<>(Arrays.asList(new String[] {"ResetEloSync", uuid.toString(), "" + ps.getGeneratedKeys().getInt("reset_id")}));
		                    Gberry.sendGSyncEvent(args);
	                    }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
	                    Gberry.closeComponents(rs, ps, connection);
                    }

                    sender.sendMessage(ChatColor.GREEN + "Elo reset given to " + args[1]);
                }
            });
        }
    }

	@Override
	public void usage(CommandSender sender) {
		sender.sendMessage(ChatColor.GOLD + "How to reset your ELO for " + ChatColor.RED + "1.7/1.8 ONLY" + ChatColor.GOLD + ":");
		sender.sendMessage(ChatColor.YELLOW + " /resetelo 1.7/1.8");
		sender.sendMessage(ChatColor.GOLD + "How to reset your ELO for " + ChatColor.RED + "1.9 ONLY" + ChatColor.GOLD + ":");
		sender.sendMessage(ChatColor.YELLOW + " /resetelo 1.9");
		sender.sendMessage(ChatColor.GOLD + "How to reset your ELO for " + ChatColor.RED + "1.7/1.8 AND 1.9" + ChatColor.GOLD + ":");
		sender.sendMessage(ChatColor.YELLOW + " /resetelo both");
	}

	public void usageConfirmDeny(CommandSender sender) {
		sender.sendMessage(ChatColor.AQUA + "Use \"/resetelo confirm/deny\" to confirm or deny the reset.");
	}

	private void fetchAllEloResets() {
		PotPvP.getInstance().getServer().getScheduler().runTaskAsynchronously(PotPvP.getInstance(), new Runnable() {
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
