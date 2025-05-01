package net.badlion.uhc.commands;

import net.badlion.common.libraries.StringCommon;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.events.GSyncEvent;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.ministats.MiniStats;
import net.badlion.uhc.BadlionUHC;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ResetStatsCommand extends BukkitUtil.Listener implements CommandExecutor {

	private static Set<UUID> resettingStats = new HashSet<>();
	private static Map<UUID, Integer> statResetId = new ConcurrentHashMap<>();

    public ResetStatsCommand() {
	    // Fetch all elo resets
	    this.fetchAllStatResets();
    }

	@EventHandler(priority = EventPriority.HIGHEST) // High priority so it gets send near the end of the string of messages
	public void onPlayerJoinEvent(PlayerJoinEvent event) {
		if (ResetStatsCommand.statResetId.containsKey(event.getPlayer().getUniqueId())) {
			event.getPlayer().sendMessage(ChatColor.GOLD + "You have a stat reset token! Type \"/resetstats\" to redeem it!");
		}
	}

	@EventHandler
	public void onGSyncEvent(GSyncEvent event) {
		if (event.getArgs().size() < 4) {
			return;
		}

		String subChannel = event.getArgs().get(0);
		if (subChannel.equals("UHC")) {
			String msg = event.getArgs().get(1);
			UUID uuid = UUID.fromString(event.getArgs().get(2));
			Integer id = Integer.parseInt(event.getArgs().get(3));

			if (msg.equals("statreset")) {
				ResetStatsCommand.statResetId.put(uuid, id);
			}
		}
	}

	@Override
	public boolean onCommand(final CommandSender sender, Command command, String s, final String[] args) {
		if (sender instanceof Player) {
			final Player player = (Player) sender;

			// Is this a MiniUHC server?
			if (BadlionUHC.getInstance().isMiniUHC()) {
				player.sendMessage(ChatColor.RED + "UHC stat resets cannot be used on MiniUHC servers!");
				return true;
			}

			// Is the game starting?
			if (BadlionUHC.getInstance().getState().ordinal() >= BadlionUHC.BadlionUHCState.COUNTDOWN.ordinal()) {
				player.sendMessage(ChatColor.RED + "UHC stat resets can only be used before the game starts!");
				return true;
			}

			if (args.length > 0) {
				if (ResetStatsCommand.resettingStats.contains(player.getUniqueId())) {
					if (args[0].equalsIgnoreCase("confirm")) {
						BadlionUHC.getInstance().getServer().getScheduler().runTaskAsynchronously(BadlionUHC.getInstance(), new Runnable() {

							public void run() {
								String query = "DELETE FROM " + MiniStats.TABLE_NAME + " WHERE uuid = ?";
								Connection connection = null;
								PreparedStatement ps = null;
								ResultSet rs = null;

								try {
									connection = Gberry.getConnection();
									ps = connection.prepareStatement(query);

									ps.setString(1, player.getUniqueId().toString());

									Gberry.executeUpdate(connection, ps);

									ps.close();

									// Delete reset now
									query = "DELETE FROM uhc_stat_resets WHERE reset_id = ?";
									ps = connection.prepareStatement(query);

									ps.setInt(1, ResetStatsCommand.statResetId.get(player.getUniqueId()));

									Gberry.executeUpdate(connection, ps);

									int newResetID = -1;

									// Cache next reset ID if they have another stat reset
									query = "SELECT * FROM uhc_stat_resets WHERE uuid = ?;";

									ps = connection.prepareStatement(query);
									ps.setString(1, player.getUniqueId().toString());

									rs = Gberry.executeQuery(connection, ps);

									if (rs.next()) {
										newResetID = rs.getInt("reset_id");

										// Add the stat reset to our cache
										ResetStatsCommand.statResetId.put(player.getUniqueId(), newResetID);

										// Sync across network
										List<String> args = new ArrayList<>();
										args.add("UHC");
										args.add("statreset");
										args.add(player.getUniqueId().toString());
										args.add(newResetID + "");

										// Send GSync event
										Gberry.sendGSyncEvent(args);
									}
								} catch (SQLException e) {
									e.printStackTrace();
									player.sendMessage(ChatColor.RED + "Error resetting stats");
									return;
								} finally {
									Gberry.closeComponents(rs, ps, connection);
								}

								player.sendMessage(ChatColor.GREEN + "Your stats have been reset.");

								// Clear cache
								ResetStatsCommand.resettingStats.remove(player.getUniqueId()); // Call this async who cares
								ResetStatsCommand.statResetId.remove(player.getUniqueId());
							}

						});
					} else if (args[0].equalsIgnoreCase("deny")) {
						player.sendMessage(ChatColor.YELLOW + "Cancelled Stat Reset.");
						ResetStatsCommand.resettingStats.remove(player.getUniqueId());
					}
				} else {
					this.usage(player);
				}
			} else {
				if (ResetStatsCommand.statResetId.containsKey(player.getUniqueId())) {
					player.sendMessage(ChatColor.YELLOW + "You have chosen to reset your stats in UHC.");
					player.sendMessage(ChatColor.YELLOW + "Use \"/resetstats confirm\" or \"/resetstats deny\" to continue.");

					ResetStatsCommand.resettingStats.add(player.getUniqueId());
				} else {
					player.sendMessage(ChatColor.RED + "You do not have any stat resets available. You may purchase one at http://store.badlion.net/");
				}
			}
		} else if (args[0].equalsIgnoreCase("add")) {
			BadlionUHC.getInstance().getServer().getScheduler().runTaskAsynchronously(BadlionUHC.getInstance(), new Runnable() {
				public void run() {
					String query = "INSERT INTO uhc_stat_resets (uuid) VALUES (?);";
					Connection connection = null;
					PreparedStatement ps = null;
					ResultSet rs = null;

					UUID uuid = StringCommon.uuidFromStringWithoutDashes(args[1]);

					try {
						connection = Gberry.getConnection();
						ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

						ps.setString(1, uuid.toString());

						Gberry.executeUpdate(connection, ps);
						rs = ps.getGeneratedKeys();

						if (rs.next()) {
							ResetStatsCommand.statResetId.put(uuid, ps.getGeneratedKeys().getInt("reset_id"));

							List<String> args = new ArrayList<>(Arrays.asList(new String[] {"UHC", "statreset", uuid.toString(), "" + ps.getGeneratedKeys().getInt("reset_id")}));
							Gberry.sendGSyncEvent(args);
						}
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						Gberry.closeComponents(rs, ps, connection);
					}

					sender.sendMessage(ChatColor.GREEN + "Stat reset given to " + args[1]);
				}
			});
		}

		return true;
    }

	public void usage(CommandSender sender) {
		sender.sendMessage(ChatColor.GOLD + "How to reset your stats:");
		sender.sendMessage(ChatColor.YELLOW + "- /resetstats");
		sender.sendMessage(ChatColor.YELLOW + "- Use \"/resetstats confirm/deny\" to confirm or deny the reset");
	}

	private void fetchAllStatResets() {
		BadlionUHC.getInstance().getServer().getScheduler().runTaskAsynchronously(BadlionUHC.getInstance(), new Runnable() {
			public void run() {
				String query = "SELECT * FROM uhc_stat_resets;";
				Connection connection = null;
				PreparedStatement ps = null;
				ResultSet rs = null;

				try {
					connection = Gberry.getConnection();
					ps = connection.prepareStatement(query);
					rs = Gberry.executeQuery(connection, ps);

					while (rs.next()) {
						ResetStatsCommand.statResetId.put(UUID.fromString(rs.getString("uuid")), rs.getInt("reset_id"));
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
