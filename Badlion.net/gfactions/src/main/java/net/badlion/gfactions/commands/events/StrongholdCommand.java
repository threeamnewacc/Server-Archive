package net.badlion.gfactions.commands.events;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.P;
import net.badlion.gberry.Gberry;
import net.badlion.gfactions.GFactions;
import net.badlion.gfactions.events.stronghold.Keep;
import net.badlion.gfactions.events.stronghold.Stronghold;
import net.badlion.gberry.utils.BukkitUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

public class StrongholdCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("start")) {
				if (GFactions.plugin.getStronghold() == null) {
					sender.sendMessage(ChatColor.YELLOW + "Starting a new stronghold event");
					new Stronghold();
				} else {
					sender.sendMessage(ChatColor.YELLOW + "A stronghold event is already running");
				}
			} else if (args[0].equalsIgnoreCase("end") || args[0].equalsIgnoreCase("stop")) {
				if (GFactions.plugin.getStronghold() != null) {
					for (Keep keep : Keep.getKeeps()) {
						if (keep.getKeepTrackerTask() != null) {
							sender.sendMessage(ChatColor.YELLOW + "Stopping the stronghold event");
							GFactions.plugin.getStronghold().stop(true);
							return true;
						}
					}

					sender.sendMessage(ChatColor.YELLOW + "Wait for the event to start before stopping it");
				} else {
					sender.sendMessage(ChatColor.YELLOW + "A stronghold event is not running");
				}
			/*} else if (args[0].equalsIgnoreCase("reset")) {
				this.handleReset(sender, args);*/
			} else if (args[0].equalsIgnoreCase("resetcapture")) {
				this.handleResetCapture(sender, args);
			} else if (args[0].equalsIgnoreCase("removeownership")) {
				this.handleRemoveOwnership(sender, args);
			} else if (args[0].equalsIgnoreCase("grantownership")) {
				this.handleGrantOwnership(sender, args);
			} else if (args[0].equalsIgnoreCase("deterioration")) {
				this.handleDeterioration(sender, args);
			} else if (args[0].equalsIgnoreCase("blacklist")) {
				this.handleBlacklist(sender, args);
			} else if (args[0].equalsIgnoreCase("addinvolved")) {
				this.handleAddInvolved(sender, args);
			} else {
				return false;
			}

			return true;
		}
		return false;
	}

	private void handleReset(CommandSender sender, String[] args) {
		if (args.length == 2) {
			// Get specified keep
			for (Keep keep : Keep.getKeeps()) {
				if (keep.getName().equalsIgnoreCase(args[1].replaceAll("&", "§"))) {
					keep.setOwner(keep.getPreviousOwner());
					return;
				}
			}

			sender.sendMessage(ChatColor.YELLOW + "Keep not found");
		} else {
			sender.sendMessage("Unknown command. Type \"/help\" for help.");
		}
	}

	private void handleResetCapture(CommandSender sender, String[] args) {
		if (args.length == 2) {
			// Get specified keep
			for (Keep keep : Keep.getKeeps()) {
				if (keep.getName().equalsIgnoreCase(args[1].replaceAll("&", "§"))) {
					if (keep.getKeepTrackerTask() != null) {
						if (keep.getKeepTrackerTask().getCapper() == null) {
							sender.sendMessage(ChatColor.YELLOW + keep.getName() + " has no capper");
						} else {
							keep.getKeepTrackerTask().setPointsCaptured(0);
							sender.sendMessage(ChatColor.YELLOW + "Points captured has been reset to 0 for " + keep.getName());
						}
					} else {
						sender.sendMessage(ChatColor.YELLOW + "No one can cap " + keep.getName() + " right now");
					}
					return;
				}
			}

			sender.sendMessage(ChatColor.YELLOW + "Keep not found");
		} else {
			sender.sendMessage("Unknown command. Type \"/help\" for help.");
		}
	}

	private void handleRemoveOwnership(final CommandSender sender, String[] args) {
		// Is stronghold running?
		if (GFactions.plugin.getStronghold() != null) {
			sender.sendMessage(ChatColor.YELLOW + "Cannot use this command while a stronghold event is active.");
			return;
		}

		if (args.length == 2) {
			// Get specified keep
			for (final Keep keep : Keep.getKeeps()) {
				if (keep.getName().equalsIgnoreCase(args[1].replaceAll("&", "§"))) {
					if (GFactions.plugin.getStrongholdConfig().getStrongholdEventId() - 1 != 0) {
						if (keep.getPreviousOwner() != null) {
							// SQL Stuff - CME's should never be hit
							BukkitUtil.runTaskAsync(new Runnable() {
								@Override
								public void run() {
									String query = "UPDATE " + GFactions.PREFIX + "_stronghold_events SET ending_owners = ? WHERE stronghold_id = ?;";

									Connection connection = null;
									PreparedStatement ps = null;

									try {
										connection = Gberry.getConnection();
										ps = connection.prepareStatement(query);

										// Serialize new keep owners
										StringBuilder sb = new StringBuilder();
										for (Keep keep2 : Keep.getKeeps()) {
											sb.append(keep2.getName());
											sb.append(":");

											if (keep == keep2) {
												sb.append("none");
											} else {
												if (keep2.getOwner() == null) {
													sb.append("none");
												} else {
													sb.append(keep2.getOwner().getId());
												}
											}

											sb.append(",");
										}
										String str = sb.toString();
										ps.setString(1, str.substring(0, str.length() - 1));
										ps.setInt(2, GFactions.plugin.getStrongholdConfig().getStrongholdEventId() - 1);

										Gberry.executeUpdate(connection, ps);

										sender.sendMessage(ChatColor.YELLOW + "You have removed the owner of " + keep.getName());
										sender.sendMessage(ChatColor.YELLOW + "You will need to \"/reloadconfig stronghold\" for the changes to take place.");
									} catch (SQLException | NumberFormatException e) {
										e.printStackTrace();
									} finally {
										if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
										if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
									}
								}
							});
						} else {
							sender.sendMessage(ChatColor.YELLOW + "No faction is in control of " + keep.getName());
						}
					} else {
						sender.sendMessage(ChatColor.YELLOW + "There has never been a stronghold event, cannot run command.");
					}
					return;
				}
			}

			sender.sendMessage(ChatColor.YELLOW + "Keep not found");
		} else {
			sender.sendMessage("Unknown command. Type \"/help\" for help.");
		}
	}

	private void handleGrantOwnership(final CommandSender sender, String[] args) {
		// Is stronghold running?
		if (GFactions.plugin.getStronghold() != null) {
			sender.sendMessage(ChatColor.YELLOW + "Cannot use this command while a stronghold event is active.");
			return;
		}

		if (args.length == 3) {
			// Get specified keep
			for (final Keep keep : Keep.getKeeps()) {
				if (keep.getName().equalsIgnoreCase(args[1].replaceAll("&", "§"))) {
					if (GFactions.plugin.getStrongholdConfig().getStrongholdEventId() - 1 != 0) {
						// Get specified faction
						final Faction faction = Factions.i.getByTag(args[2]);
						if (faction == null) {
							sender.sendMessage(ChatColor.YELLOW + "Faction not found");
							return;
						}

						// SQL Stuff - CME's should never be hit
						BukkitUtil.runTaskAsync(new Runnable() {
							@Override
							public void run() {
								String query = "UPDATE " + GFactions.PREFIX + "_stronghold_events SET ending_owners = ? WHERE stronghold_id = ?;";

								Connection connection = null;
								PreparedStatement ps = null;

								try {
									connection = Gberry.getConnection();
									ps = connection.prepareStatement(query);

									// Serialize new keep owners
									StringBuilder sb = new StringBuilder();
									for (Keep keep2 : Keep.getKeeps()) {
										sb.append(keep2.getName());
										sb.append(":");

										if (keep == keep2) {
											sb.append(faction.getId());
										} else {
											if (keep2.getOwner() == null) {
												sb.append("none");
											} else {
												sb.append(keep2.getOwner().getId());
											}
										}

										sb.append(",");
									}
									String str = sb.toString();
									ps.setString(1, str.substring(0, str.length() - 1));
									ps.setInt(2, GFactions.plugin.getStrongholdConfig().getStrongholdEventId() - 1);

									Gberry.executeUpdate(connection, ps);

									sender.sendMessage(ChatColor.YELLOW + "You have set the owner of " + keep.getName() + ChatColor.YELLOW + " to " + faction.getTag());
									sender.sendMessage(ChatColor.YELLOW + "You will need to \"/reloadconfig stronghold\" for the changes to take place.");
								} catch (SQLException | NumberFormatException e) {
									e.printStackTrace();
								} finally {
									if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
									if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
								}
							}
						});
					} else {
						sender.sendMessage(ChatColor.YELLOW + "There has never been a stronghold event, cannot run command.");
					}
					return;
				}
			}

			sender.sendMessage(ChatColor.YELLOW + "Keep not found");
		} else {
			sender.sendMessage("Unknown command. Type \"/help\" for help.");
		}
	}

	private void handleDeterioration(final CommandSender sender, String[] args) {
		// Is stronghold running?
		if (GFactions.plugin.getStronghold() != null) {
			sender.sendMessage(ChatColor.YELLOW + "Cannot use this command while a stronghold event is active.");
			return;
		}

		if (args.length == 3) {
			final boolean add;
			if (args[1].equalsIgnoreCase("add")) {
				add = true;
			} else if (args[1].equalsIgnoreCase("rm") || args[1].equalsIgnoreCase("remove")) {
				add = false;
			} else {
				sender.sendMessage(ChatColor.YELLOW + "Add or remove not specified");
				return;
			}

			// Get specified keep
			for (final Keep keep : Keep.getKeeps()) {
				if (keep.getName().equalsIgnoreCase(args[2].replaceAll("&", "§"))) {
					if (keep.getOwner() == null) {
						sender.sendMessage(ChatColor.YELLOW + "Specified keep has no owner");
						return;
					}

					BukkitUtil.runTaskAsync(new Runnable() {
						@Override
						public void run() {
							Connection connection = null;
							PreparedStatement ps = null;

							try {
								String query = "UPDATE " + GFactions.PREFIX + "_stronghold_deterioration SET money_owed = ?, items_owed = ? WHERE stronghold_id = ? AND keep_name = ?;" +
										"INSERT INTO " + GFactions.PREFIX + "_stronghold_deterioration (stronghold_id, keep_name, money_owed, items_owed) SELECT ?, ?, ?, ? WHERE NOT EXISTS " +
										"(SELECT 1 FROM " + GFactions.PREFIX + "_stronghold_deterioration WHERE stronghold_id = ? AND keep_name = ?);";

								connection = Gberry.getConnection();
								ps = connection.prepareStatement(query);

								if (add) {
									ps.setInt(1, GFactions.plugin.getStrongholdConfig().getDeteriorationMoney());
									ps.setString(2, GFactions.plugin.getStrongholdConfig().serializeDeteriorationItemsOwed());
									ps.setInt(3, GFactions.plugin.getStrongholdConfig().getStrongholdEventId() - 1);
									ps.setString(4, keep.getName());
									ps.setInt(5, GFactions.plugin.getStrongholdConfig().getStrongholdEventId() - 1);
									ps.setString(6, keep.getName());
									ps.setInt(7, GFactions.plugin.getStrongholdConfig().getDeteriorationMoney());
									ps.setString(8, GFactions.plugin.getStrongholdConfig().serializeDeteriorationItemsOwed());
									ps.setInt(9, GFactions.plugin.getStrongholdConfig().getStrongholdEventId() - 1);
									ps.setString(10, keep.getName());

									// Update cache
									keep.setDeteriorationMoneyOwed(GFactions.plugin.getStrongholdConfig().getDeteriorationMoney());
									keep.setDeteriorationItemsOwed(new HashMap<>(GFactions.plugin.getStrongholdConfig().getDeteriorationItems()));
									keep.checkDeteriorationApplied(null);

									sender.sendMessage(ChatColor.YELLOW + "Deteriorating has been added for the owner of " + keep.getName());
								} else {
									ps.setInt(1, 0);
									ps.setString(2, "");
									ps.setInt(3, GFactions.plugin.getStrongholdConfig().getStrongholdEventId() - 1);
									ps.setString(4, keep.getName());
									ps.setInt(5, GFactions.plugin.getStrongholdConfig().getStrongholdEventId() - 1);
									ps.setString(6, keep.getName());
									ps.setInt(7, 0);
									ps.setString(8, "");
									ps.setInt(9, GFactions.plugin.getStrongholdConfig().getStrongholdEventId() - 1);
									ps.setString(10, keep.getName());

									// Update cache
									keep.setDeteriorationMoneyOwed(0);
									keep.getDeteriorationItemsOwed().clear();
									keep.checkDeteriorationApplied(null);

									sender.sendMessage(ChatColor.YELLOW + "Deteriorating has been removed for the owner of " + keep.getName());
								}

								ps.executeUpdate();
							} catch (SQLException | NumberFormatException e) {
								e.printStackTrace();
							} finally {
								if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
								if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
							}
						}
					});

					return;
				}
			}

			sender.sendMessage(ChatColor.YELLOW + "Keep not found");
		} else {
			sender.sendMessage("Unknown command. Type \"/help\" for help.");
		}
	}

	private void handleBlacklist(CommandSender sender, String[] args) {
		if (args.length == 3) {
			final boolean add;
			if (args[1].equalsIgnoreCase("add")) {
				add = true;
			} else if (args[1].equalsIgnoreCase("rm") || args[1].equalsIgnoreCase("remove")) {
				add = false;
			} else {
				sender.sendMessage(ChatColor.YELLOW + "Add or remove not specified");
				return;
			}

			// Find faction specified
			final Faction faction = Factions.i.getByTag(args[2]);
			if (faction == null) {
				sender.sendMessage(ChatColor.YELLOW + "Faction not found");
				return;
			}

			if (add) {
				if (!GFactions.plugin.getStrongholdConfig().getBlacklistedFactions().add(faction)) {
					sender.sendMessage(ChatColor.YELLOW + "Faction is already on the stronghold blacklist");
					return;
				}

				sender.sendMessage(ChatColor.YELLOW + "Faction has been added to the stronghold blacklist");
			} else {
				if (!GFactions.plugin.getStrongholdConfig().getBlacklistedFactions().remove(faction)) {
					sender.sendMessage(ChatColor.YELLOW + "Faction isn't on the stronghold blacklist");
					return;
				}

				sender.sendMessage(ChatColor.YELLOW + "Faction has been removed from the stronghold blacklist");
			}

			// Save in database
			BukkitUtil.runTaskAsync(new Runnable() {
				@Override
				public void run() {
					String query = "";

					Connection connection = null;
					PreparedStatement ps = null;

					try {
						if (add) {
							query = "INSERT INTO " + GFactions.PREFIX + "_stronghold_blacklists (faction_id) VALUES (?);";
						} else {
							query = "DELETE FROM " + GFactions.PREFIX + "_stronghold_blacklists WHERE faction_id = ?;";
						}

						connection = Gberry.getConnection();
						ps = connection.prepareStatement(query);

						ps.setInt(1, Integer.valueOf(faction.getId()));

						ps.executeUpdate();
					} catch (SQLException | NumberFormatException e) {
						e.printStackTrace();
					} finally {
						if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
						if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
					}
				}
			});
		} else {
			sender.sendMessage("Unknown command. Type \"/help\" for help.");
		}
	}

	private void handleAddInvolved(CommandSender sender, String[] args) {
		// Is stronghold not running?
		if (GFactions.plugin.getStronghold() == null) {
			sender.sendMessage(ChatColor.YELLOW + "Cannot use this command while a stronghold event is not active.");
			return;
		}

		if (args.length == 2) {
			// Get specified faction
			Faction faction = Factions.i.getByTag(args[1]);
			if (faction != null) {
				GFactions.plugin.getStronghold().getInvolvedFactions().add(faction.getId());

				// Lock this faction
				P.setFactionLocked(faction, true);

				// Send a message to online faction members
				for (Player pl : faction.getOnlinePlayers()) {
					pl.sendMessage(ChatColor.YELLOW + "Your faction has been locked by an administrator!");
				}

				sender.sendMessage(ChatColor.YELLOW + "Faction added to involved factions list");
			} else {
				sender.sendMessage(ChatColor.YELLOW + "Faction not found");
			}
		} else {
			sender.sendMessage("Unknown command. Type \"/help\" for help.");
		}
	}

}
