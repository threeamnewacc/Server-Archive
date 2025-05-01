package net.badlion.gfactions.listeners;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import net.badlion.gfactions.GFactions;
import net.badlion.gberry.Gberry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.joda.time.DateTime;

import java.sql.*;
import java.util.*;

public class VoteListener implements Listener {
	
	private GFactions plugin;

	private LinkedList<String> joinMessages = new LinkedList<String>();
	private LinkedList<String> voteMessages = new LinkedList<String>();
    public static HashSet<String> peopleWhoVotedToday = new HashSet<>();
	
	public VoteListener(GFactions plugin) {
		this.plugin = plugin;

		this.joinMessages.add("");
		this.joinMessages.add("");
		this.joinMessages.add("");
		this.joinMessages.add("");
		this.joinMessages.add("");
		this.joinMessages.add("");
        this.joinMessages.add("§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=");
        this.joinMessages.add(ChatColor.AQUA + "Welcome to " + ChatColor.YELLOW + ChatColor.BOLD + "Badlion Factions" + ChatColor.RESET + ChatColor.AQUA + "!");
        this.joinMessages.add(ChatColor.AQUA + "Please remember to vote every day by using " + ChatColor.YELLOW + "/vote" + ChatColor.AQUA + " We really appreciate it!");
        this.joinMessages.add(ChatColor.AQUA + "Visit our website at " + ChatColor.BLUE + "http://www.badlion.net/");
        this.joinMessages.add("§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=§3=§b=");

        this.voteMessages.add("§3=§b=§3=§b=§3=§b= " + ChatColor.YELLOW + ChatColor.BOLD + "Voting Prizes" + ChatColor.RESET + " §3=§b=§3=§b=§3=§b=");
        this.voteMessages.add(ChatColor.DARK_AQUA + "1/20 Chance for" + ChatColor.AQUA + " $1,000 Extra");
        this.voteMessages.add(ChatColor.DARK_AQUA + "1/50 Chance for" + ChatColor.AQUA + " 64 XP Bottles");
        this.voteMessages.add(ChatColor.DARK_AQUA + "1/1000 Chance for" + ChatColor.AQUA + " Protection IV Enchanting Book");
        this.voteMessages.add(ChatColor.DARK_AQUA + "1/1000 Chance for" + ChatColor.AQUA + " Sharpness V Enchanting Book");
        this.voteMessages.add(ChatColor.DARK_AQUA + "1/2000 Chance for" + ChatColor.AQUA + " 5 extra loot items from voting");
        this.voteMessages.add(ChatColor.DARK_AQUA + "1/5000 Chance for" + ChatColor.AQUA + " Protection IV Diamond Set");
        this.voteMessages.add(ChatColor.DARK_AQUA + "1/10000 Chance for" + ChatColor.AQUA + " God Loot");
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent e) {
        final Player p = e.getPlayer();
		if (e.getPlayer().hasPlayedBefore()) {
			synchronized (VoteListener.peopleWhoVotedToday) {
				if (!VoteListener.peopleWhoVotedToday.contains(p.getName())) {
					for (String message : this.joinMessages) {
						p.sendMessage(message);
					}
					for (String message : this.voteMessages) {
						p.sendMessage(message);
					}
				}
			}
		} else {
			this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new Runnable() {
				@Override
				public void run() {
					for (String message : joinMessages) {
						p.sendMessage(message);
					}
					for (String message : voteMessages) {
						p.sendMessage(message);
					}
				}
			}, 20 * 60 * 5);
		}

		// Spam them every 10 min?
		new BukkitRunnable() {
			@Override
			public void run() {
				synchronized (VoteListener.peopleWhoVotedToday) {
					if (!VoteListener.peopleWhoVotedToday.contains(p.getName())) {
						for (String message : voteMessages) {
							p.sendMessage(message);
						}
					} else {
						this.cancel(); // they voted, don't spam them
					}
				}
			}
		}.runTaskTimer(this.plugin, 20 * 60 * 10, 20 * 60 * 10);
    }

	@EventHandler
	public void onVotifierEvent(final VotifierEvent event) {
        final Vote vote = event.getVote();
        final Player player = Bukkit.getPlayer(vote.getUsername());

        // Don't spam the DB
        synchronized (VoteListener.peopleWhoVotedToday) {
            if (VoteListener.peopleWhoVotedToday.contains(vote.getUsername())) {
            }
        }

        // Always run, ignore errors
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

            @Override
            public void run() {
                String query = "INSERT INTO " + GFactions.PREFIX + "_vote_records (uuid, vote_date) VALUES (?, ?);";

                Connection connection = null;
                PreparedStatement ps = null;

                // Try to get the UUID, if we can't find it FUCK IT
                final UUID uuid = Gberry.getOfflineUUID(vote.getUsername());
                if (uuid == null) {
                    return;
                }

                try {
                    connection = Gberry.getConnection();
                    ps = connection.prepareStatement(query);
                    ps.setString(1, uuid.toString());
                    ps.setTimestamp(2, new Timestamp(new java.util.Date().getTime()));

                    Gberry.executeUpdate(connection, ps);

                    // Handle voting rewards
                    if (plugin.generateRandomInt(1, 10000) == 1) { // God Loot
                        if (plugin.generateRandomInt(1, 2) == 1) {
                            //plugin.getAuction().insertHeldAuctionItem(uuid.toString(), plugin.getItemGenerator().generateGodWeapon(1).get(0)); TODO
                        } else {
                            //plugin.getAuction().insertHeldAuctionItem(uuid.toString(), plugin.getItemGenerator().generateGodArmor(1).get(0)); TODO
                        }

                        if (player != null) {
                            player.sendMessage(ChatColor.GREEN + "You have won a god item from voting!");
                        }

                        // Send a message to everyone on the server
                        for (Player players : plugin.getServer().getOnlinePlayers()) {
                            if (players != player) {
                                players.sendMessage(ChatColor.GREEN + event.getVote().getUsername() + " has won a god item from voting!");
                            }
                        }
                    } else if (plugin.generateRandomInt(1, 5000) == 1) { // Protection IV Diamond Set
                        ItemStack helmet = new ItemStack(Material.DIAMOND_HELMET);
                        helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

                        ItemStack chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
                        chestplate.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

                        ItemStack leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
                        leggings.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

                        ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
                        boots.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);

                        /*plugin.getAuction().insertHeldAuctionItem(uuid.toString(), helmet); TODO
                        plugin.getAuction().insertHeldAuctionItem(uuid.toString(), chestplate);
                        plugin.getAuction().insertHeldAuctionItem(uuid.toString(), leggings);
                        plugin.getAuction().insertHeldAuctionItem(uuid.toString(), boots);*/

                        if (player != null) {
                            player.sendMessage(ChatColor.GREEN + "You have won a Protection IV diamond set from voting!");
                        }

                        // Send a message to everyone on the server
                        for (Player players : plugin.getServer().getOnlinePlayers()) {
                            if (players != player) {
                                players.sendMessage(ChatColor.GREEN + event.getVote().getUsername() + " has won a Protection IV diamond set from voting!");
                            }
                        }
                    } else if (plugin.generateRandomInt(1, 2000) == 1) { // Five Extra Items
                        // Add 5 tickets
                        for (int x = 0; x < 5; x++) {
                            int rand = plugin.generateRandomInt(1, 100);
                            ArrayList<ItemStack> items = null;
							if (1 == rand) {
								items = plugin.getItemGenerator().generateRandomSuperRareItem(1, true);
							} else if (2 <= rand && rand < 20) {
								items = plugin.getItemGenerator().generateRandomRareItem(1, true);
							} else {
								items = plugin.getItemGenerator().generateRandomCommonItem(1, true);
							}

	                        // TODO plugin.getAuction().insertHeldAuctionItems(uuid.toString(), items);
                        }

                        if (player != null) {
                            player.sendMessage(ChatColor.GREEN + "You have won 5 extra loot items from voting!");
                        }

                        // Send a message to everyone on the server
                        for (Player players : plugin.getServer().getOnlinePlayers()) {
                            if (players != player) {
                                players.sendMessage(ChatColor.GREEN + event.getVote().getUsername() + " has won 5 extra loot items from voting!");
                            }
                        }
                    } else if (plugin.generateRandomInt(1, 1000) == 1) { // Sharpness V Book
                        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
                        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
                        meta.addStoredEnchant(Enchantment.DAMAGE_ALL, 5, false);
                        book.setItemMeta(meta);
                        // TODO plugin.getAuction().insertHeldAuctionItem(uuid.toString(), book);

                        if (player != null) {
                            player.sendMessage(ChatColor.GREEN + "You have won a Sharpness V book from voting!");
                        }

                        // Send a message to everyone on the server
                        for (Player players : plugin.getServer().getOnlinePlayers()) {
                            if (players != player) {
                                players.sendMessage(ChatColor.GREEN + event.getVote().getUsername() + " has won a Sharpness V book from voting!");
                            }
                        }
                    } else if (plugin.generateRandomInt(1, 1000) == 1) { // Protection IV Book
                        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
                        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
                        meta.addStoredEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, false);
                        book.setItemMeta(meta);
	                    // TODO plugin.getAuction().insertHeldAuctionItem(uuid.toString(), book);

                        if (player != null) {
                            player.sendMessage(ChatColor.GREEN + "You have won a Protection IV book from voting!");
                        }

                        // Send a message to everyone on the server
                        for (Player players : plugin.getServer().getOnlinePlayers()) {
                            if (players != player) {
                                players.sendMessage(ChatColor.GREEN + event.getVote().getUsername() + " has won a Protection IV book from voting!");
                            }
                        }
                    } else if (plugin.generateRandomInt(1, 50) == 1) { // 64 EXP Bottles
	                    // TODO plugin.getAuction().insertHeldAuctionItem(uuid.toString(), new ItemStack(Material.EXP_BOTTLE, 64));

                        if (player != null) {
                            player.sendMessage(ChatColor.GREEN + "You have won 64 EXP bottles from voting!");
                        }

                        // Send a message to everyone on the server
                        for (Player players : plugin.getServer().getOnlinePlayers()) {
                            if (players != player) {
                                players.sendMessage(ChatColor.GREEN + event.getVote().getUsername() + " has won 64 EXP bottles from voting!");
                            }
                        }
                    } else if (plugin.generateRandomInt(1, 20) == 1) { // $2,500
						plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
							@Override
							public void run() {
								plugin.getArchMoney().changeBalance(uuid.toString(), 2500, "Voting reward");
							}
						});

                        if (player != null) {
                            player.sendMessage(ChatColor.GREEN + "You have won $2,500 from voting!");
                        }

                        // Send a message to everyone on the server
                        for (Player players : plugin.getServer().getOnlinePlayers()) {
                            if (players != player) {
                                players.sendMessage(ChatColor.GREEN + event.getVote().getUsername() + " has won $2,500 from voting!");
                            }
                        }
                    }

                    // Add 1 ticket
					/*int rand = plugin.generateRandomInt(1, 100);
					ArrayList<ItemStack> items = null;
					if (1 == rand) {
						items = plugin.getItemGenerator().generateRandomSuperRareItem(1, true);
					} else if (2 <= rand && rand < 20) {
						items = plugin.getItemGenerator().generateRandomRareItem(1, true);
					} else {
						items = plugin.getItemGenerator().generateRandomCommonItem(1, true);
					}

					plugin.getAuction().insertHeldAuctionItems(uuid.toString(), items);*/
                    //VoteListener.addVotingTickets(uuid.toString(), 1);

					ArrayList<ItemStack> items = plugin.getItemGenerator().generateRandomVoteItem(1);
	                // TODO plugin.getAuction().insertHeldAuctionItems(uuid.toString(), items);

					// Give 200 also
					plugin.getArchMoney().changeBalance(uuid.toString(), 200);

                    if (player != null) {
                        player.sendMessage(ChatColor.GREEN + "Thank you for voting and supporting the server!");
                        player.sendMessage(ChatColor.GREEN + "Use /claim to retrieve your loot items.");
                    }

                } catch (SQLException e) {
                    // Who cares, it won't work, move on with our FUCKING LIVES
                    //e.printStackTrace();
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

        });

        // Now spam the fuck outta everyone who hasn't
        final String msg = ChatColor.AQUA + vote.getUsername() + " earned $200 and a free item! Vote @ http://www.badlion.net daily on all 5 sites for rewards!";

        synchronized (VoteListener.peopleWhoVotedToday) {
            // Don't shoot the same message off for the same person voting
            if (!VoteListener.peopleWhoVotedToday.contains(vote.getUsername())) {
                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    if (!VoteListener.peopleWhoVotedToday.contains(p.getName()) && !p.getName().equals(vote.getUsername())) {
                        p.sendMessage(msg);
                    }
                }
            }
        }

        // Add to our record so we don't spam them
        synchronized (VoteListener.peopleWhoVotedToday) {
            VoteListener.peopleWhoVotedToday.add(vote.getUsername());
        }
	}

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        final Player player = event.getPlayer();
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

            @Override
            public void run() {
                String query = "SELECT * FROM " + GFactions.PREFIX  + "_tickets WHERE uuid = ?;";

                Connection connection = null;
                PreparedStatement ps = null;
                ResultSet rs = null;

                try {
                    // player1
                    connection = Gberry.getConnection();
                    ps = connection.prepareStatement(query);
                    ps.setString(1, player.getUniqueId().toString());
                    rs = Gberry.executeQuery(connection, ps);

                    if (rs.next()) {
                        final int numOfTicketsAvailable = rs.getInt("num_of_tickets");
                        if (numOfTicketsAvailable > 0) {
							//player.sendMessage(ChatColor.YELLOW + "You have vouchers ready for claiming. Use /claim to get your vouchers for voting for free loot!");
                            plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {

                                @Override
                                public void run() {
                                    /*int count = 0;
                                    for (ItemStack item : player.getInventory()) {
                                        if (item == null) {
                                            count++;
                                        }
                                    }

                                    if (count == 0) {
                                        player.sendMessage(ChatColor.YELLOW + "You have vouchers ready for claiming. Use /claim to get your vouchers for voting for free loot!");
                                    } else {
                                        int tmp = Math.min(count, numOfTicketsAvailable);*/
									for (int i = 0; i < numOfTicketsAvailable; i++) {
										// Generate an item
										int rand = plugin.generateRandomInt(1, 100);
										ArrayList<ItemStack> items = null;
										if (1 == rand) {
											items = plugin.getItemGenerator().generateRandomSuperRareItem(1, true);
										} else if (2 <= rand && rand < 20) {
											items = plugin.getItemGenerator().generateRandomRareItem(1, true);
										} else {
											items = plugin.getItemGenerator().generateRandomCommonItem(1, true);
										}

										/*if (items.get(0).getType() == Material.PAPER) {
											player.getInventory().addItem(items.get(0)); // already a voucher
										} else {
											player.getInventory().addItem(TicketManager.createNoteFromItemStack(items.get(0))); // make a voucher
										}*/

										final ArrayList<ItemStack> finalItems = items;
										//player.getInventory().addItem(items.get(0));
										plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
											@Override
											public void run() {
												// Give them one super rare item
												// TODO plugin.getAuction().insertHeldAuctionItems(player.getUniqueId().toString(), finalItems);
											}
										});
									}

									player.sendMessage(ChatColor.GOLD + "You have items to claim from voting! Use " + ChatColor.GREEN + "/claim" + ChatColor.GOLD + " to get your free loot!");

                                        // Pretty messages
                                        /*if (tmp == numOfTicketsAvailable) {
                                            player.sendMessage(ChatColor.GREEN + "All your vouchers have been claimed. Redeem vouchers at spawn for items.");
                                        } else {
                                            player.sendMessage(ChatColor.GREEN + "" + tmp + " vouchers have been claimed. Make more room for the rest.");
                                        }

                                        final int toRemove = tmp;

                                        // Update DB
                                        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {

                                            @Override
                                            public void run() {
                                                String query = "UPDATE faction_tickets SET num_of_tickets = num_of_tickets - ? WHERE uuid = ?;";

                                                Connection connection = null;
                                                PreparedStatement ps = null;

                                                try {
                                                    // player1
                                                    connection = Gberry.getConnection();
                                                    ps = connection.prepareStatement(query);
                                                    ps.setInt(1, toRemove);
                                                    ps.setString(2, player.getUniqueId().toString());

                                                    Gberry.executeUpdate(connection, ps);
                                                } catch (SQLException e) {
                                                    e.printStackTrace();
                                                } finally {
                                                    if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
                                                    if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
                                                }
                                            }
                                        });
                                    }*/
                                }

                            }, 1); // 1 tick
                        }
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
    }

    /*public static void addVotingTickets(String uuid, int numOfTickets) {
        // Give them tickets
        String query = "INSERT INTO faction_tickets (uuid, num_of_tickets) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE num_of_tickets = num_of_tickets + ?;";

        Connection connection = null;
        PreparedStatement ps = null;

        try {
            // player1
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, uuid);
            ps.setInt(2, numOfTickets);
            ps.setInt(3, numOfTickets);

            Gberry.executeUpdate(connection, ps);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
    }*/

    public static Map<String, Integer> getTopVotersOfMonth() {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT uuid, COUNT(*) AS c FROM " + GFactions.PREFIX + "_vote_records WHERE vote_date >= ? GROUP BY uuid ORDER BY c DESC LIMIT 10;";

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);

            // In theory this should get us the first second of the day of the month
            DateTime date = new DateTime();
            date = date.minusSeconds(date.getSecondOfDay());
            date = date.minusDays(date.getDayOfMonth() - 1);
            ps.setTimestamp(1, new Timestamp(date.toDate().getTime()));

            rs = Gberry.executeQuery(connection, ps);
			Map<String, Integer> topVoters = new HashMap<>();

            while (rs.next()) {
				// Get most recent username
                topVoters.put(Gberry.getUsernameFromUUID(rs.getString("uuid")), rs.getInt("c"));
            }

			return topVoters;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }

        return null;
    }

    public static int getNumOfVotesForThisMonth(String uuid) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        String query = "SELECT COUNT(*) FROM " + GFactions.PREFIX + "_vote_records WHERE uuid = ? AND vote_date >= ?;";

        try {
            connection = Gberry.getConnection();
            ps = connection.prepareStatement(query);
            ps.setString(1, uuid);

            // In theory this should get us the first second of the day of the month
            DateTime date = new DateTime();
            date = date.minusSeconds(date.getSecondOfDay());
            date = date.minusDays(date.getDayOfMonth() - 1);
            ps.setTimestamp(2, new Timestamp(date.toDate().getTime()));

            rs = Gberry.executeQuery(connection, ps);

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }

        return 0;
    }

}
