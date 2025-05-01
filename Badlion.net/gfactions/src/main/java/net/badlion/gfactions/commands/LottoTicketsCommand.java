package net.badlion.gfactions.commands;

import net.badlion.gfactions.GFactions;
import net.badlion.gberry.Gberry;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.joda.time.*;

import java.sql.*;
import java.util.ArrayList;

public class LottoTicketsCommand implements CommandExecutor {

	private GFactions plugin;
	
	public LottoTicketsCommand(GFactions plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, final String[] args) {
		if (sender instanceof Player) {
			final Player player = (Player) sender;
            final String lotteryText = ChatColor.DARK_AQUA + "[" + ChatColor.LIGHT_PURPLE + "LOTTO" + ChatColor.DARK_AQUA + "] " + ChatColor.WHITE;

            if (args.length == 0) {

                // Find closest lotto draw
                DateTime now = DateTime.now();
                DateTime closest = null;
                for (DateTime dt : this.plugin.getLotteryTimes()) {
                    if (closest == null) {
                        closest = dt;
                        continue;
                    }

                    if (closest.isAfter(dt)) {
                        closest = dt;
                    }
                }

                // Calculate time differences
                int days = 0;
                boolean singleDays = false;
                int hours = 0;
                boolean singleHours = false;
                int minutes = 0;
                boolean singleMinutes = false;
                int seconds = 0;
                boolean singleSeconds = false;
                if (closest != null) {
                    days = Days.daysBetween(now, closest).getDays();
                    if (days == 1) singleDays = true;
                    closest = closest.minusDays(days);
                    hours = Hours.hoursBetween(now, closest).getHours();
                    if (hours == 1) singleHours = true;
                    closest = closest.minusHours(hours);
                    minutes = Minutes.minutesBetween(now, closest).getMinutes();
                    if (minutes == 1) singleMinutes = true;
                    closest = closest.minusMinutes(minutes);
                    seconds = Seconds.secondsBetween(now, closest).getSeconds();
                    if (seconds == 1) singleSeconds = true;
                }

                player.sendMessage(ChatColor.GREEN + "--------------------------------------------------");
                player.sendMessage(lotteryText + "Draw in: " + ChatColor.RED + days + " day" + (singleDays ? "" : "s") + ", "
                        + hours + " hour" + (singleHours ? "" : "s") + ", " + minutes + " minute" + (singleMinutes ? "" : "s") + ", and "
                        + seconds + " second" + (singleSeconds ? "" : "s"));
                player.sendMessage(lotteryText + "Buy a ticket for " + ChatColor.RED + "$" + this.plugin.getLottoTicketPrice()
                        + ChatColor.WHITE + " with " + ChatColor.RED + "/lotto buy");
	            player.sendMessage(lotteryText + "Or buy multiple tickets using" + ChatColor.RED + "/lotto buy <n>");
                player.sendMessage(lotteryText + "There is currently $" + ChatColor.GREEN + plugin.getConfig().getInt("gfactions.lotto.jackpot") + ChatColor.WHITE + " in the pot!");
                player.sendMessage(lotteryText + "See your lotto tickets with " + ChatColor.RED + "/lotto tickets");
            } else if (args.length == 1 && (args[0].equalsIgnoreCase("ticket") || args[0].equalsIgnoreCase("tickets"))) {
                // Get player's tickets
                this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

                    public void run() {
                        String query = "SELECT COUNT(*) FROM " + GFactions.PREFIX + "_lotto_tickets;";

                        Connection connection = null;
                        PreparedStatement ps = null;
                        ResultSet rs = null;

                        try {
                            connection = Gberry.getConnection();
                            ps = connection.prepareStatement(query);
                            rs = Gberry.executeQuery(connection, ps);

                            if (rs.next()) {
                                int numOfTicketsPurchased = rs.getInt(1);
                                if (numOfTicketsPurchased > 9900) {
                                    player.sendMessage(ChatColor.RED + "All the lotto tickets have been purchased. Please tell an admin.");
                                    return;
                                }
                            }

                            query = "SELECT * FROM faction_lotto_tickets WHERE uuid = ?;";
                            ps = connection.prepareStatement(query);
                            ps.setString(1, player.getUniqueId().toString());

                            rs = Gberry.executeQuery(connection, ps);

                            StringBuilder sb = new StringBuilder();
                            ArrayList<String> messages = new ArrayList<String>();

                            int count = 0;
                            int totalCount = 0;
                            boolean hasTicket = false;
                            while (rs.next()) {
                                if (count == 7) {
                                    messages.add(lotteryText + sb.toString().substring(4));
                                    sb.setLength(0);
                                    count = 0;
                                }
                                int ticketNumber = rs.getInt("lotto_ticket_number");
                                String niceNumber = plugin.niceLottoTicketNumber(ticketNumber);
                                sb.append(ChatColor.WHITE);
                                sb.append(", ");
                                sb.append(ChatColor.GREEN);
                                sb.append(niceNumber);

                                count++;
                                totalCount++;
                                hasTicket = true;
                            }

                            if (sb.length() != 0) {
                                messages.add(lotteryText + sb.toString().substring(4));
                            }

							// Smelly, do you even test? lol <3
							if (!hasTicket) {
								player.sendMessage(lotteryText + "You have " + ChatColor.RED + "0" + ChatColor.WHITE + " lotto tickets!");
							} else {
                            	player.sendMessage(lotteryText + "You have " + ChatColor.RED + totalCount + ChatColor.WHITE + " lotto tickets:");
							}

                            for (String message : messages) {
                                player.sendMessage(message);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } finally {
                            if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
                            if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
                            if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
                        }
                    }
                });
            } else if ((args.length == 1 || args.length == 2) && (args[0].equalsIgnoreCase("buy") || args[0].equalsIgnoreCase("b"))) {

                int n = 1;
                if (args.length == 2) {
                    try {
                        n = Integer.valueOf(args[1]);
                        if (n < 1 || n > 10000) throw new NumberFormatException();

	                    // Don't let them buy over 100 tickets
	                    if (n > 100) {
		                    player.sendMessage(ChatColor.RED + "You cannot buy more than 100 lotto tickets!");
		                    return true;
	                    }
                    } catch(NumberFormatException e) {
                        player.sendMessage(ChatColor.RED + "You have entered an invalid number of lotto tickets to buy!");
                        return true;
                    }
                }

                int balance = this.plugin.getArchMoney().checkBalance(player.getUniqueId().toString());

                // Withdraw lotto ticket price
                int totalPrice = this.plugin.getLottoTicketPrice() * n;
                if (balance > totalPrice) {
                    this.plugin.getArchMoney().changeBalance(player.getUniqueId().toString(), (-1) * totalPrice, "Bought lottery ticket");
                } else {
                    player.sendMessage(ChatColor.RED + "You do not have enough money for " + n + " lotto ticket(s).  It costs $" +
                            totalPrice + " for " + n + " lotto ticket(s).");
                    return true;
                }

                final int n2 = n; // Hack

                this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
                    public void run() {
                        // player1
                        Connection connection = null;
                        PreparedStatement ps = null;
                        ResultSet rs = null;

                        boolean newNumber = false;
                        final Integer[] ticketNumbers = new Integer[n2];
                        try {
                            connection = Gberry.getConnection();

                            final String uuid = player.getUniqueId().toString();

	                        // Are they trying to purchase more tickets than they can have (100)?
	                        String query = "SELECT COUNT(*) FROM " + GFactions.PREFIX + "_lotto_tickets WHERE uuid = ?;";

	                        ps = connection.prepareStatement(query);
	                        ps.setString(1, uuid);
	                        rs = Gberry.executeQuery(connection, ps);

	                        if (rs.next()) {
		                        int currentTickets = rs.getInt(1);
		                        int amountCanPurchase = 100 - currentTickets;
		                        if (n2 > amountCanPurchase) {
			                        player.sendMessage(ChatColor.RED + "You have " + currentTickets + " tickets, you can only buy " + amountCanPurchase + " more!");
			                        return;
		                        }
	                        }

                            java.util.Date today = new java.util.Date();
                            Timestamp now = new java.sql.Timestamp(today.getTime());

                            for (int x = 0; x < n2; x++) {
                                do {
                                    ticketNumbers[x] = plugin.generateRandomInt(0, 9999);
                                    String query2 = "SELECT * FROM faction_lotto_tickets WHERE lotto_ticket_number = ?;";

                                    ps = connection.prepareStatement(query2);
                                    ps.setInt(1, ticketNumbers[x]);
                                    rs = Gberry.executeQuery(connection, ps);

                                    // Found a not-used lotto ticket number.
                                    if (!rs.next()) {
                                        newNumber = true;
                                    }
                                } while (!newNumber);

                                String query2 = "INSERT INTO faction_lotto_tickets (uuid, lotto_ticket_number, purchase_time) VALUES(?, ?, ?);";

                                ps = connection.prepareStatement(query2);
                                ps.setString(1, uuid);
                                ps.setInt(2, ticketNumbers[x]);
                                ps.setTimestamp(3, now);
                                Gberry.executeUpdate(connection, ps);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        } finally {
                            if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
                            if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
                            if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
                        }

                        plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                for (int x = 0; x < n2; x++) {
                                    // Update jackpot
                                    plugin.getConfig().set("gfactions.lotto.jackpot",
                                            plugin.getLottoTicketPrice() + plugin.getConfig().getInt("gfactions.lotto.jackpot"));
                                    plugin.saveConfig();
                                    player.sendMessage(lotteryText + "You have purchased lotto ticket "
                                            + ChatColor.GREEN + plugin.niceLottoTicketNumber(ticketNumbers[x]));
                                }
                            }
                        });
                    }
                });
            }
		}
		return true;
	}

}
