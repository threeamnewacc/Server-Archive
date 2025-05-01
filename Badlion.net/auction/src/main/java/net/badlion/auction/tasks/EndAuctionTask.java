package net.badlion.auction.tasks;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import io.github.andrepl.chatlib.Text;
import net.badlion.auction.Auction;
import net.badlion.auction.ItemForSale;
import net.badlion.gberry.Gberry;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class EndAuctionTask extends BukkitRunnable {
	
	private Auction plugin;
	
	public EndAuctionTask(Auction plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void run() {
		final ItemForSale item = this.plugin.getItemUpForSale();
		this.plugin.setItemUpForSale(null);
		
		// What if no one bid?
		if (item.getPlayers().size() == 0) {
			this.handleNoBuyers(item);
			return;
		}
		
		// Take their money
		Player player = item.getPlayers().get(item.getPlayers().size() - 1);
		if (this.plugin.getArchMoney().checkBalance(player.getUniqueId().toString()) >= item.getCurrentBid()) {
			this.plugin.getArchMoney().changeBalance(player.getUniqueId().toString(), (-1) * item.getCurrentBid(), "Won auction");
		} else {
			item.getPlayers().remove(item.getPlayers().size() - 1);
			item.getBids().remove(item.getBids().size() - 1);
			item.getTimestamps().remove(item.getBids().size() - 1);
			
			// Possible first attempt we are successful
            if (this.plugin.getArchMoney().checkBalance(item.getPlayers().get(item.getPlayers().size() - 1).getUniqueId().toString()) >= item.getBids().get(item.getBids().size() - 1)) {
				item.setCurrentBid(item.getBids().get(item.getBids().size() - 1));
                this.plugin.getArchMoney().changeBalance(item.getPlayers().get(item.getPlayers().size() - 1).getUniqueId().toString(), (-1) * item.getCurrentBid(), "Won auction");
			} else {
				// Nope, gotta do more work
				ArrayList<Player> playersCopy = new ArrayList<Player>(item.getPlayers());
				ArrayList<Integer> bidsCopy = new ArrayList<Integer>(item.getBids());
				ArrayList<Timestamp> timestampsCopy = new ArrayList<Timestamp>(item.getTimestamps());
				
				int i = 100;
				for (i = item.getPlayers().size() - 1; i >= 0; i--) {
					// Does this person have enough money?
					if (this.plugin.getArchMoney().checkBalance(item.getPlayers().get(i).getUniqueId().toString()) >= item.getBids().get(i)) {
						// Cool someone has the money they promised, time to move on
						item.setPlayers(playersCopy);
						item.setBids(bidsCopy);
						item.setTimestamps(timestampsCopy);
						item.setCurrentBid(item.getBids().get(i));
                        this.plugin.getArchMoney().changeBalance(item.getPlayers().get(i).getUniqueId().toString(), (-1) * item.getCurrentBid(), "Won auction");
						break;
					} else {
						// Pop one off the end of the list
						playersCopy.remove(item.getPlayers().get(i));
						bidsCopy.remove(item.getBids().get(i));
						timestampsCopy.remove(item.getTimestamps().get(i));
					}
				}
				
				if (i < 0) {
					// No one had the money...
					this.handleNoBuyers(item);
					return;
				}
			}
		}
		
		// Lovely chat
		ArrayList<Player> players = this.plugin.getAuctionCommand().getPlayersWhoWantAuctionMessages();
		String endString = this.plugin.getAuctionCommand().getPrefix() + item.getPlayers().get(item.getPlayers().size() - 1).getName() + " has won the bid and paid "
				+ ChatColor.YELLOW + "$" + item.getCurrentBid() + ChatColor.BLUE + " for " + ChatColor.GOLD + item.getItem().getAmount() + ChatColor.AQUA + " [";
        Text text = new Text(endString);
        text.appendItem(item.getItem());
        text.append("§b]"); // Hardcoded ChatColor.AQUA to work
		for (Player p : players) {
			text.send(p);
		}
		
		// Give the item to the buyer
		Player tmp = this.plugin.getServer().getPlayer(player.getUniqueId().toString());
		if (player.equals(tmp)) { // equivalent of isOnline()
			int firstEmpty = player.getInventory().firstEmpty();
			if (firstEmpty == -1) {
				// Full inventory, to the database we go
				this.plugin.storeItemInDatabase(player, item.getItem());
				player.sendMessage(ChatColor.GREEN + "You won the auction and have been charged but need inventory space. Use \"/auction claim\" to claim your item later.");
			} else {
				player.getInventory().addItem(item.getItem());
				player.sendMessage(ChatColor.GREEN + "You have been given the item you purchased and the money has been taken from your account.");
			}
		} else {
			// Maybe they are online on a different player then we had before
			if (tmp == null) {
				// Store in database, they aren't online
				this.plugin.storeItemInDatabase(player, item.getItem());
			} else {
				// Found them, do the same checks as above
				player = tmp;

				int firstEmpty = player.getInventory().firstEmpty();
				if (firstEmpty == -1) {
					// Full inventory, to the database we go
					this.plugin.storeItemInDatabase(player, item.getItem());
					player.sendMessage(ChatColor.GREEN + "You won the auction and have been charged but need inventory space.. Use \"/auction claim\" to claim your item later.");
				} else {
					player.getInventory().addItem(item.getItem());
					player.sendMessage(ChatColor.GREEN + "You have been given the item you purchased and the money has been taken from your account.");
				}
			}
		}
		
		this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
			
			@Override
			public void run() {
				// Give money to seller
				String owner = plugin.getgFactionsPlugin().getPropertyManager().getOwnerOfProperty("auctionhouse");
				if (owner.equals("")) {
					// 1 tick later
					plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
						
						@Override
						public void run() {
                            plugin.getArchMoney().changeBalance(item.getPlayer().getUniqueId().toString(), (int) (item.getCurrentBid() * 0.95), "Auction money for seller");
						}
						
					}, 1);
					
					item.getPlayer().sendMessage(ChatColor.GREEN + "$" + ((double)item.getCurrentBid() * 0.95) + " has been depositied into your account (5% fee applied).");
				} else {
					FPlayer fplayer = FPlayers.i.get(owner);
					Faction faction = fplayer.getFaction();
					FPlayer fplayerSeller = FPlayers.i.get(item.getPlayer().getUniqueId().toString());
					Faction factionSeller = fplayerSeller.getFaction();
					
					// They are in same faction, apply discount
					if (faction.getId().equals(factionSeller.getId())) {
						int lvl = plugin.getgFactionsPlugin().getPropertyManager().getLevelOfProperty("auctionhouse");
						final double reductionRate = 0.5 + ((lvl - 1) * 0.08);
						// 1 tick later
						plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
							
							@Override
							public void run() {
                                plugin.getArchMoney().changeBalance(item.getPlayer().getUniqueId().toString(), (int) (item.getCurrentBid() * (0.95 + (reductionRate / 100))), "Auction money for seller");
							}
							
						}, 1);
						
						item.getPlayer().sendMessage(ChatColor.GREEN + "$" + ((double)item.getCurrentBid() * (((double)95 + reductionRate) / 100)) + " has been depositied into your account (" + (5 - reductionRate) + "% fee applied).");
					} else {
						// Diff factions no discount
						plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable() {
							
							@Override
							public void run() {
                                plugin.getArchMoney().changeBalance(item.getPlayer().getUniqueId().toString(), (int) (item.getCurrentBid() * 0.95), "Auction money for seller");
							}
							
						}, 1);
						
						item.getPlayer().sendMessage(ChatColor.GREEN + "$" + ((double)item.getCurrentBid() * 0.95) + " has been depositied into your account (5% fee applied).");
					}
				}
			}
			
		});
				
		// Store the information in the database
		this.storeSaleInDatabase(item);
		
		this.plugin.getAuctionCommand().incrementNextItem();
	}
	
	public void handleNoBuyers(ItemForSale item) {
		Player player = item.getPlayer();
		
		ArrayList<Player> players = this.plugin.getAuctionCommand().getPlayersWhoWantAuctionMessages();
		
		String s = this.plugin.getAuctionCommand().getPrefix() + "Auction Ended. No bids.";
		for (Player p : players) {
			p.sendMessage(s);
		}
		
		// Figure out where to shove the item
		Player tmp = this.plugin.getServer().getPlayer(player.getUniqueId());
		if (player.equals(tmp)) { // equivalent of isOnline()
			int firstEmpty = player.getInventory().firstEmpty();
			if (firstEmpty == -1) {
				// Full inventory, to the database we go
				this.plugin.storeItemInDatabase(player, item.getItem());
				player.sendMessage(ChatColor.RED + "No one bid on your item in the Auction House. Use \"/auction claim\" to claim your item when you have space in your inventory.");
			} else {
				player.getInventory().addItem(item.getItem());
				player.sendMessage(ChatColor.RED + "No one bid on your item in the Auction House. Putting it back in your inventory.");
			}
		} else {
			// Maybe they are online on a different player then we had before
			if (tmp == null) {
				// Store in database, they aren't online
				this.plugin.storeItemInDatabase(player, item.getItem());
			} else {
				// Found them, do the same checks as above
				player = tmp;

				int firstEmpty = player.getInventory().firstEmpty();
				if (firstEmpty == -1) {
					// Full inventory, to the database we go
					this.plugin.storeItemInDatabase(player, item.getItem());
					player.sendMessage(ChatColor.RED + "No one bid on your item in the Auction House. Use \"/auction claim\" to claim your item when you have space in your inventory.");
				} else {
					player.getInventory().addItem(item.getItem());
					player.sendMessage(ChatColor.RED + "No one bid on your item in the Auction House. Putting it back in your inventory.");
				}
			}
		}
		
		this.plugin.getAuctionCommand().incrementNextItem();
		
		return;
	}
	
	public void storeSaleInDatabase(final ItemForSale item) {
		this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
			
			@Override
			public void run() {
				int auctionId = storeItemInDatabase(item);
				if (auctionId == -1)
					return; // failed
				
				storeBidsInDatabase(item, auctionId);
				storeItemEnchantmentsInDatabase(item, auctionId);
			}
			
		});
	}
	
	public int storeItemInDatabase(ItemForSale item) {
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		String query = "INSERT INTO auction_items (item, stack_size, durability, sold_by, purchased_by, starting_bid, minimum_bid, final_bid, selling_time) VALUES " +
				"(?, ?, ?, ?, ?, ? , ?, ?, ?);";
		
		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
			ps.setString(1, item.getItem().getType().name().replace('_', ' '));
			ps.setInt(2, item.getItem().getAmount());
			ps.setInt(3, item.getItem().getDurability());
			ps.setString(4, item.getPlayer().getUniqueId().toString());
			ps.setString(5, item.getPlayers().get(item.getPlayers().size() - 1).getUniqueId().toString());
			ps.setInt(6, item.getPrice());
			ps.setInt(7, item.getIncrement());
			ps.setInt(8, item.getBids().get(item.getBids().size() - 1));
			ps.setTimestamp(9, new Timestamp(new DateTime(DateTimeZone.UTC).getMillis()));
			ps.executeUpdate();
			rs = ps.getGeneratedKeys();
			
			if (rs.next()) {
				return rs.getInt(1);
			} else {
				return -1;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}
		
		return -1;
	}
	
	public void storeBidsInDatabase(ItemForSale item, int auctionId) {
		Connection connection = null;
		PreparedStatement ps = null;
		
		StringBuilder builder = new StringBuilder();
		builder.append("INSERT INTO auction_bids (auction_id, player, bid, bid_time) VALUES (?, ?, ?, ?)");
		
		// Generate dynamic query
		for (int i = 1; i < item.getBids().size(); ++i) {
			builder.append(", (?, ?, ?, ?)");
		}
		builder.append(";");
		
		String query = builder.toString();
		
		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			
			// Iterate through enchantments and add them
			int i = 1;
			for (int j = 0; j < item.getBids().size(); ++j) {
				ps.setInt(i++, auctionId);
				ps.setString(i++, item.getPlayers().get(j).getUniqueId().toString());
				ps.setInt(i++, item.getBids().get(j));
				ps.setTimestamp(i++, item.getTimestamps().get(j));
			}
			
			Gberry.executeUpdate(connection, ps);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}
	}
	
	@SuppressWarnings("unused")
	public void storeItemEnchantmentsInDatabase(ItemForSale item, int auctionId) {
		Connection connection = null;
		PreparedStatement ps = null;
		
		StringBuilder builder = new StringBuilder();
		builder.append("INSERT INTO auction_items_enchantments (auction_id, enchantment, enchantment_level) VALUES (?, ?, ?)");
		
		// Generate dynamic query
		Map<String, Integer> enchantments = item.getEnchantments();
		Iterator<Entry<String, Integer>> it = enchantments.entrySet().iterator();
		ArrayList<String> keys = new ArrayList<String>();
		ArrayList<Integer> values = new ArrayList<Integer>();
		if (it.hasNext()) {
			Map.Entry<String, Integer> pair = (Map.Entry<String, Integer>) it.next();
			keys.add(pair.getKey());
			values.add(pair.getValue());
		} else {
			return; // no enchantments
		}
		while (it.hasNext()) {
			Map.Entry<String, Integer> pair = (Map.Entry<String, Integer>) it.next();
			keys.add(pair.getKey());
			values.add(pair.getValue());
			builder.append(", (?, ?, ?)");
		}
		builder.append(";");
		
		String query = builder.toString();
		
		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			
			// Iterate through enchantments and add them
			int j = 1;
			for (int i = 0; i < keys.size(); i++) {
				ps.setInt(j++, auctionId);
				ps.setString(j++, keys.get(i));
				ps.setInt(j++, values.get(i));
			}

			Gberry.executeUpdate(connection, ps);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}
	}

}
