package net.badlion.gfactions.commands;

import net.badlion.gfactions.GFactions;
import net.badlion.gberry.Gberry;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.sql.*;
import java.util.UUID;

public class DonatorCommand implements CommandExecutor {

    private GFactions plugin;

    public DonatorCommand(GFactions plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, final String[] args) {

        if (sender.isOp()) {
            if (args.length == 4 && args[0].equals("addxpboost")) {
                this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

                    @Override
                    public void run() {
                        plugin.getDonatorManager().addXPBoost(args[1], Double.parseDouble(args[2]), Integer.parseInt(args[3]));
                    }

                });

                this.plugin.getDonatorManager().getBoostMap().put(args[1], Double.parseDouble(args[2]));
            } else if (args.length == 2 && args[0].equals("removexpboost")) {
                this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

                    @Override
                    public void run() {
                        plugin.getDonatorManager().removeXPBoost(args[1]);
                    }

                });

                this.plugin.getDonatorManager().getBoostMap().remove(args[1]);
            } else if (args.length == 3 && args[0].equals("addmoney")) {
				this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

					@Override
					public void run() {
						UUID uuid = Gberry.getOfflineUUID(args[1]);
						if (uuid == null) {
							return;
						}

						plugin.getArchMoney().changeBalance(uuid.toString(), Integer.parseInt(args[2]), "Donation");
					}

				});
            } else if (args.length == 3 && args[0].equals("removemoney")) {
                final String uuid1 = this.plugin.getServer().getPlayerExact(args[1]).getUniqueId().toString();
                this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

                    @Override
                    public void run() {
                        if (uuid1 == null) {
                            plugin.getArchMoney().changeBalance(Gberry.getOfflineUUID(args[1]).toString(), (-1) * Integer.parseInt(args[2]), "Donation");
                        } else {
                            plugin.getArchMoney().changeBalance(uuid1, (-1) * Integer.parseInt(args[2]), "Donation");
                        }
                    }

                });
            } else if (args.length == 3 && args[0].equals("upgrade")) {
	            if (args[1].equals("emperor")) {
		            this.plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "user " + args[2] + " setgroup " + args[1]);
		            this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

			            @Override
			            public void run() {
				            UUID uuid = Gberry.getOfflineUUID(args[2]);
				            if (uuid == null) {
					            return;
				            }

				            plugin.getArchMoney().changeBalance(uuid.toString(), 40000);
			            }

		            });
	            } else if (args[1].equals("king") || args[1].equals("queen") || args[1].equals("emerald")) {
					this.plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "user " + args[2] + " setgroup " + args[1]);
					this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

						@Override
						public void run() {
							UUID uuid = Gberry.getOfflineUUID(args[2]);
							if (uuid == null) {
								return;
							}

							plugin.getArchMoney().changeBalance(uuid.toString(), 30000);
						}

					});
				} else if (args[1].equals("prince") || args[1].equals("princess") || args[1].equals("diamond")) {
					this.plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "user " + args[2] + " setgroup " + args[1]);
					this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

						@Override
						public void run() {
							UUID uuid = Gberry.getOfflineUUID(args[2]);
							if (uuid == null) {
								return;
							}

							plugin.getArchMoney().changeBalance(uuid.toString(), 20000);
						}

					});
				} else if (args[1].equals("iron")) {
					this.plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "user " + args[2] + " setgroup iron");
					this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

						@Override
						public void run() {
							UUID uuid = Gberry.getOfflineUUID(args[2]);
							if (uuid == null) {
								return;
							}

							plugin.getArchMoney().changeBalance(uuid.toString(), 15000);
						}

					});
				} else if (args[1].equals("gold")) {
					this.plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "user " + args[2] + " setgroup gold");
					this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

						@Override
						public void run() {
							plugin.getDonatorManager().addReservedSlot(args[2]);
							UUID uuid = Gberry.getOfflineUUID(args[2]);
							if (uuid == null) {
								return;
							}

							plugin.getArchMoney().changeBalance(uuid.toString(), 7000);
						}

					});
				} else if (args[1].equals("coal")) {
					this.plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "user " + args[2] + " setgroup coal");
					this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

						@Override
						public void run() {
							UUID uuid = Gberry.getOfflineUUID(args[2]);
							if (uuid == null) {
								return;
							}

							plugin.getArchMoney().changeBalance(uuid.toString(), 5000);
						}

					});
				} else if (args[1].equals("stone")) {
					this.plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "user " + args[2] + " setgroup stone");
					this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

						@Override
						public void run() {
							UUID uuid = Gberry.getOfflineUUID(args[2]);
							if (uuid == null) {
								return;
							}

							plugin.getArchMoney().changeBalance(uuid.toString(), 2000);
						}

					});
				}
			} else if (args.length == 3 && args[0].equals("rank")) {
	            if (args[1].equals("emperor")) {
		            this.plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "user " + args[2] + " setgroup " + args[1]);
		            this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

			            @Override
			            public void run() {
				            UUID uuid = Gberry.getOfflineUUID(args[2]);
				            if (uuid == null) {
					            return;
				            }

				            plugin.getDonatorManager().addReservedSlot(uuid.toString());
				            plugin.getArchMoney().changeBalance(uuid.toString(), 120000);
			            }

		            });
	            } else if (args[1].equals("king") || args[1].equals("queen") || args[1].equals("emerald")) {
					this.plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "user " + args[2] + " setgroup " + args[1]);
					this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

						@Override
						public void run() {
							UUID uuid = Gberry.getOfflineUUID(args[2]);
							if (uuid == null) {
								return;
							}

							plugin.getDonatorManager().addReservedSlot(uuid.toString());
							plugin.getArchMoney().changeBalance(uuid.toString(), 80000);
						}

					});
				} else if (args[1].equals("prince") || args[1].equals("princess") || args[1].equals("diamond")) {
                    this.plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "user " + args[2] + " setgroup " + args[1]);
					this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

						@Override
						public void run() {
							UUID uuid = Gberry.getOfflineUUID(args[2]);
							if (uuid == null) {
								return;
							}

							plugin.getDonatorManager().addReservedSlot(uuid.toString());
							plugin.getArchMoney().changeBalance(uuid.toString(), 50000);
						}

					});
                } else if (args[1].equals("iron")) {
					this.plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "user " + args[2] + " setgroup iron");
					this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

						@Override
						public void run() {
							UUID uuid = Gberry.getOfflineUUID(args[2]);
							if (uuid == null) {
								return;
							}

							plugin.getDonatorManager().addReservedSlot(uuid.toString());
							plugin.getArchMoney().changeBalance(uuid.toString(), 30000);
						}

					});
                } else if (args[1].equals("gold")) {
					this.plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "user " + args[2] + " setgroup gold");
					this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

						@Override
						public void run() {
							plugin.getDonatorManager().addReservedSlot(args[2]);
							UUID uuid = Gberry.getOfflineUUID(args[2]);
							if (uuid == null) {
								return;
							}

							plugin.getArchMoney().changeBalance(uuid.toString(), 15000);
						}

					});
                } else if (args[1].equals("coal")) {
					this.plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "user " + args[2] + " setgroup coal");
					this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

						@Override
						public void run() {
							UUID uuid = Gberry.getOfflineUUID(args[2]);
							if (uuid == null) {
								return;
							}

							plugin.getDonatorManager().addReservedSlot(uuid.toString());
							plugin.getArchMoney().changeBalance(uuid.toString(), 8000);
						}

					});
                } else if (args[1].equals("stone")) {
					this.plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "user " + args[2] + " setgroup stone");
					this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

						@Override
						public void run() {
							UUID uuid = Gberry.getOfflineUUID(args[2]);
							if (uuid == null) {
								return;
							}

							plugin.getDonatorManager().addReservedSlot(uuid.toString());
							plugin.getArchMoney().changeBalance(uuid.toString(), 3000);
						}

					});
                } else if (args[1].equals("squire")) {
					this.plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "user " + args[2] + " setgroup squire");
					this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

						@Override
						public void run() {
							UUID uuid = Gberry.getOfflineUUID(args[2]);
							if (uuid == null) {
								return;
							}

							plugin.getDonatorManager().addReservedSlot(uuid.toString());
							plugin.getArchMoney().changeBalance(uuid.toString(), 1000);
						}

					});
				}
            } else if (args.length == 2 && args[0].equals("ban")) {
                this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

                    @Override
                    public void run() {
                        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), args[1] + " Chargebacks are not allowed");
                    }

                });
            } else if (args.length == 3 && args[0].equals("kit")) {
                if (args[1].equals("tnt")) {
					this.plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "user " + args[2] + " addperm GFactions.kit.tnt");
                } else if (args[1].equals("potion")) {
					this.plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), "user " + args[2] + " addperm GFactions.kit.potions");
				}
            }
        }

        return true;
    }

    public static void insertNewDonation(String donatorUUID, String donationBuffType, String miscDonationData, Timestamp endDateTime, Long duration) {

        String query;
        Connection connection = null;
        PreparedStatement ps = null;

        query = "INSERT INTO " + GFactions.PREFIX + "_bonuses (uuid_donator, type, data, start_date, end_date) VALUES (?, ?, ?, ?, ?);";

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, donatorUUID);
            ps.setString(2, donationBuffType);
            ps.setString(3, miscDonationData);
            ps.setTimestamp(4, new java.sql.Timestamp(new java.util.Date().getTime())); // ?????????????
            ps.setTimestamp(5, new java.sql.Timestamp(new java.util.Date().getTime() + duration));

            Gberry.executeUpdate(connection, ps);
        } catch (SQLException e) {
            e.printStackTrace();
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "stop");
        } finally {
            if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
    }

    public static void getAllDonationBuffs(){

        String query = "SELECT * FROM " + GFactions.PREFIX + "_bonuses WHERE start_time < " + new java.sql.Timestamp(new java.util.Date().getTime()) + " AND end_time > " + new java.sql.Timestamp(new java.util.Date().getTime()) + ";";
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            rs = Gberry.executeQuery(connection, ps);

            while (rs.next()) {
                if (rs.getString(2).equals("xp_mult")) {
                    //DonatorListener. //WHAT THE FUCK
                    // Set global variable for XP rate to rs.getString(3)
                    // Brodcast message ChatColor + re.getString(1) + " has unlocked an + rs.getString(3) + "x XP boost for everyone for X amount of hours.

                } else if (rs.getString(2).equals("tower")) {
                    // Staart tower
                    // broadcast message
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "stop");
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
    }
}
