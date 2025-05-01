package net.badlion.gfactions.commands;

import net.badlion.gfactions.GFactions;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClaimCommand implements CommandExecutor {

    private GFactions plugin;

    public ClaimCommand(GFactions plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, final String[] args) {
        if (sender instanceof Player) {
            ((Player) sender).performCommand("auction claim");
            /*final Player player = (Player) sender;

            this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

                @Override
                public void run() {
                    String query = "SELECT * FROM faction_tickets WHERE uuid = ?;";

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
                                plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {

                                    @Override
                                    public void run() {
                                        int count = 0;
                                        for (ItemStack item : player.getInventory()) {
                                            if (item == null) {
                                                count++;
                                            }
                                        }

                                        if (count == 0) {
                                            player.sendMessage(ChatColor.RED + "You have no space in your inventory for the vouchers. Make room and then use /claim again.");
                                        } else {
                                            int tmp = Math.min(count, numOfTicketsAvailable);
                                            for (int i = 0; i < tmp; i++) {
                                                // Generate an item
                                                int rand = plugin.generateRandomInt(1, 100);
                                                ArrayList<ItemStack> items = null;
                                                if (1 <= rand && rand < 3) {
                                                    items = plugin.getItemGenerator().generateRandomSuperRareItem(1);
                                                } else if (3 <= rand && rand < 13) {
                                                    items = plugin.getItemGenerator().generateRandomRareItem(1);
                                                } else if (13 <= rand && rand <= 65) {
                                                    items = plugin.getItemGenerator().generateRandomCommonItem(1);
                                                } else {
                                                    items = plugin.getItemGenerator().generateRandomTrashItem(1);
                                                }

                                                if (items.get(0).getType() == Material.PAPER) {
                                                    player.getInventory().addItem(items.get(0)); // already a voucher
                                                } else {
                                                    player.getInventory().addItem(TicketManager.createNoteFromItemStack(items.get(0))); // make a voucher
                                                }
                                            }

                                            // Pretty messages
                                            if (tmp == numOfTicketsAvailable) {
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
                                                        // TODO Auto-generated catch block
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
                                            });
                                        }
                                    }

                                }, 1); // 1 tick
                            } else {
                                player.sendMessage(ChatColor.RED + "No vouchers to redeem at the moment. Vote daily at www.badlion.net to earn more.");
                            }
                        } else {
                            player.sendMessage(ChatColor.RED + "No vouchers to redeem at the moment. Vote daily at www.badlion.net to earn more.");
                        }
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

            });*/
        }

        return true;
    }
}
