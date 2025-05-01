package net.badlion.gfactions.tasks;

import net.badlion.gfactions.GFactions;
import net.badlion.gberry.Gberry;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LottoDrawingTask extends BukkitRunnable  {

	private GFactions plugin;
	
	public LottoDrawingTask(GFactions plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void run() {
		final String lotteryText = ChatColor.DARK_AQUA + "[" + ChatColor.LIGHT_PURPLE + "LOTTO" + ChatColor.DARK_AQUA + "] " + ChatColor.WHITE;
		this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

            public void run() {
                // player1
                Connection connection = null;
                PreparedStatement ps = null;
                ResultSet rs = null;

                synchronized (plugin.getArchMoney()) {
                    int ticketNumber = -1;
                    try {
                        // Winning ticket #
                        ticketNumber = plugin.generateRandomInt(0, 9999);
                        String query = "SELECT * FROM " + GFactions.PREFIX + "_lotto_tickets WHERE lotto_ticket_number = ?;";

                        connection = Gberry.getConnection();
                        ps = connection.prepareStatement(query);
                        ps.setInt(1, ticketNumber);
                        rs = Gberry.executeQuery(connection, ps);

                        if (rs.next()) {
                            // We have a winner!
                            final String winner = rs.getString("uuid");
                            final int winningNumber = rs.getInt("lotto_ticket_number");
                            plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {

                                @Override
                                public void run() {
                                    long jackpot = plugin.getConfig().getLong("gfactions.lotto.jackpot");
                                    jackpot *= plugin.getConfig().getDouble("gfactions.lotto.jackpot_multiplier");
                                    plugin.getArchMoney().changeBalance(winner, (int) jackpot, "Lottery jackpot payout");
                                    plugin.getConfig().set("gfactions.lotto.jackpot", 0);
                                    plugin.saveConfig();

									Gberry.broadcastMessage(lotteryText + winner + " has won the lottery with a total prize of " + jackpot +
                                            ". Winning ticket #" + plugin.niceLottoTicketNumber(winningNumber));
                                }

                            }, 1); // 1 tick
                        } else {
							Gberry.broadcastMessage(lotteryText + "No one has won the lottery this time, better luck next time!");
                        }

                        // Set LottoDrawingTask to null after 5 minutes
                        plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
                            @Override
                            public void run() {
                                plugin.setLottoDrawingTask(null);
                            }
                        }, 20L * 60L * 5L);

                        query = "DELETE FROM faction_lotto_tickets;";
                        Gberry.executeUpdate(connection, connection.prepareStatement(query));

                    } catch (SQLException e) {
                        // TODO Auto-generated catch block
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
                }
            }
        });
	}
	
}
