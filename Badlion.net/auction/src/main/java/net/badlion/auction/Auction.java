package net.badlion.auction;

import com.trc202.CombatTag.CombatTag;
import com.trc202.CombatTagApi.CombatTagApi;
import net.badlion.archmoney.ArchMoney;
import net.badlion.auction.commands.AuctionCommand;
import net.badlion.auction.commands.BidCommand;
import net.badlion.auction.commands.TradeCommand;
import net.badlion.auction.listeners.AuctionListener;
import net.badlion.factions.GFactions;
import net.badlion.gberry.Gberry;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;

// Referrence for more advanced program: http://forums.bukkit.org/threads/serializing-itemmeta-and-all-your-wildest-dreams.137325/

public class Auction extends JavaPlugin {
	
	private ArchMoney archMoney;
    private Queue<ItemForSale> itemsUpForAuction;
    private HashSet<Player> playersWhoHaveItemsUpForSale;
    private CombatTag combatTag;
    private CombatTagApi combatTagApi;
    private ItemForSale itemUpForSale;
    private AuctionCommand auctionCommand;
    private TradeCommand tradeCommand;
    private boolean allowAuctions;
    private GFactions gFactionsPlugin;
    
    public Auction() {
    	this.itemsUpForAuction = new LinkedList<ItemForSale>();
    	this.playersWhoHaveItemsUpForSale = new HashSet<Player>();
    	this.allowAuctions = true;
    }
	
	@Override
	public void onEnable() {
        this.archMoney = (ArchMoney) this.getServer().getPluginManager().getPlugin("ArchMoney");
        this.gFactionsPlugin = (GFactions) this.getServer().getPluginManager().getPlugin("GFactions");

        // Combat Tag hookup
        this.combatTag = (CombatTag) this.getServer().getPluginManager().getPlugin("CombatTag");
        this.combatTagApi = new CombatTagApi(this.combatTag);

        this.auctionCommand = new AuctionCommand(this);
        this.getCommand("auction").setExecutor(this.auctionCommand);
        this.getCommand("bid").setExecutor(new BidCommand());
        //this.tradeCommand = new TradeCommand(this);
        //this.getCommand("trade").setExecutor(this.tradeCommand);

        // Listeners
        this.getServer().getPluginManager().registerEvents(new AuctionListener(this), this);
    }

    @Override
	public void onDisable() {
		// Fail safe, try not to lose the items
		if (this.itemUpForSale != null) {
			this.itemsUpForAuction.add(itemUpForSale);
		}
		
		// Run this sync
		for (ItemForSale item : this.itemsUpForAuction) {
			insertHeldAuctionItem(item.getPlayer().getUniqueId().toString(), item.getItem());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// Simply spin off a thread, too lazy to C/P these few lines
	public void storeItemInDatabase(final Player player, final ItemStack item) {
		this.getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
			
			@Override
			public void run() {
				insertHeldAuctionItem(player.getUniqueId().toString(), item);
			}
			
		});
	}
	
	// Time for Deserialization
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static ConfigurationSerializable deserialize(Map<String, Object> map) {
	    for (Entry<String, Object> entry : map.entrySet()) {
	    	// Check if any of its sub-maps are ConfigurationSerializable. They need to be done first.
	        if (entry.getValue() instanceof Map && ((Map)entry.getValue()).containsKey(ConfigurationSerialization.SERIALIZED_TYPE_KEY)) {
	            entry.setValue(deserialize((Map)entry.getValue()));
	        }
	    }
	    return ConfigurationSerialization.deserializeObject(map);
	}
	
	public static Map<String, Object> serialize(ConfigurationSerializable cs) {
	    Map<String, Object> serialized = recreateMap(cs.serialize());
	    for (Entry<String, Object> entry : serialized.entrySet()) {
	        if (entry.getValue() instanceof ConfigurationSerializable) {
	            entry.setValue(serialize((ConfigurationSerializable)entry.getValue()));
	        }
	    }
	    serialized.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, ConfigurationSerialization.getAlias(cs.getClass()));
	    return serialized;
	}
	 
	public static Map<String, Object> recreateMap(Map<String, Object> original) {
	    Map<String, Object> map = new HashMap<String, Object>();
	    for (Entry<String, Object> entry : original.entrySet()) {
	        map.put(entry.getKey(), entry.getValue());
	    }
	    return map;
	}

	public void insertHeldAuctionItems(String uuid, ArrayList<ItemStack> items) {
		for (ItemStack item : items) {
			this.insertHeldAuctionItem(uuid, item);
		}
	}

	public void insertHeldAuctionItem(String uuid, ItemStack item) {
		String query = "INSERT INTO auction_items_held (uuid, auction_item, purchase_time) VALUES (?, ?, ?);";

		Connection connection = null;
		PreparedStatement ps = null;
		
		try {
			// player1
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, uuid);
			
			try {
				// Item
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(bos);

				// Re-organize the data
				Map<String, Object> data = serialize(item);
				oos.writeObject(data);

				// Close
				oos.flush();
				oos.close();
				bos.close();

				ps.setObject(2, bos.toByteArray());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			ps.setLong(3, System.nanoTime());
			Gberry.executeUpdate(connection, ps);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}
	}

	public void deleteHeldAuctionItems(String uuid, ArrayList<Long> times) {
		// Hackish way to build the query string
		String query = "DELETE FROM auction_items_held WHERE uuid = ? AND purchase_time IN (?";
		for (int i = 1; i < times.size(); ++i) {
			query += ", ?";
		}
		query += ");";

		Connection connection = null;
		PreparedStatement ps = null;

		try {
			// player1
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, uuid);
			// Handle the timestamps
			for (int i = 0; i < times.size(); ++i) {
				ps.setLong(i + 2, times.get(i));
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
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> getHeldAuctionItems(String uuid) {
		String query = "SELECT * FROM auction_items_held WHERE uuid = ?;";

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			// player1
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, uuid);
			rs = Gberry.executeQuery(connection, ps);
			
			Map<String, Object> results = new HashMap<String, Object>();
			ArrayList<ItemStack> items = new ArrayList<ItemStack>();
			ArrayList<Long> times = new ArrayList<Long>();
			while (rs.next()) {
				try {
					// Attempt to deserialize the information that we stored in the database
					ByteArrayInputStream bais = new ByteArrayInputStream(rs.getBytes("auction_item"));
					ObjectInputStream ins = new ObjectInputStream(bais);
					ConfigurationSerializable cs = deserialize((Map<String, Object>)ins.readObject());

					ItemStack item = (ItemStack) cs;
					items.add(item);

					// Ok we made it this far, add the timestamp
					times.add(rs.getLong("purchase_time"));

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			// Null if nothing
			if (items.size() == 0) {
				return null;
			}
			
			results.put("dates", times);
			results.put("items", items);
			return results;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}

		return null;
	}
	
	public void deletePlayerAlerts(String uuid) {
		String query = "DELETE FROM auction_alerts WHERE uuid = ?;";

		Connection connection = null;
		PreparedStatement ps = null;
		
		try {
			// player1
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, uuid);
			Gberry.executeUpdate(connection, ps);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}
	}
	
	public void turnOnPlayerAlerts(String uuid) {
		String query = "INSERT INTO auction_alerts (uuid) VALUES (?);";

		Connection connection = null;
		PreparedStatement ps = null;
		
		try {
			// player1
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, uuid);
			Gberry.executeUpdate(connection, ps);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}
	}
	
	public boolean arePlayerAlertsOn(String uuid) {
		String query = "SELECT * FROM auction_alerts WHERE uuid = ?;";

		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			// player1
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, uuid);
			rs = Gberry.executeQuery(connection, ps);
			
			if (rs.next()) {
				return false;
			} else {
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}
		
		return true;
	}

	public ArchMoney getArchMoney() {
		return archMoney;
	}

	public void setArchMoney(ArchMoney archMoney) {
		this.archMoney = archMoney;
	}

	public Queue<ItemForSale> getItemsUpForAuction() {
		return itemsUpForAuction;
	}

	public void setItemsUpForAuction(Queue<ItemForSale> itemsUpForAuction) {
		this.itemsUpForAuction = itemsUpForAuction;
	}

	public HashSet<Player> getPlayersWhoHaveItemsUpForSale() {
		return playersWhoHaveItemsUpForSale;
	}

	public void setPlayersWhoHaveItemsUpForSale(
			HashSet<Player> playersWhoHaveItemsUpForSale) {
		this.playersWhoHaveItemsUpForSale = playersWhoHaveItemsUpForSale;
	}

	public CombatTag getCombatTag() {
		return combatTag;
	}

	public void setCombatTag(CombatTag combatTag) {
		this.combatTag = combatTag;
	}

	public CombatTagApi getCombatTagApi() {
		return combatTagApi;
	}

	public void setCombatTagApi(CombatTagApi combatTagApi) {
		this.combatTagApi = combatTagApi;
	}

	public ItemForSale getItemUpForSale() {
		return itemUpForSale;
	}

	public void setItemUpForSale(ItemForSale itemUpForSale) {
		this.itemUpForSale = itemUpForSale;
	}

	public AuctionCommand getAuctionCommand() {
		return auctionCommand;
	}

	public void setAuctionCommand(AuctionCommand auctionCommand) {
		this.auctionCommand = auctionCommand;
	}

	public boolean isAllowAuctions() {
		return allowAuctions;
	}

	public void setAllowAuctions(boolean allowAuctions) {
		this.allowAuctions = allowAuctions;
	}

	public TradeCommand getTradeCommand() {
		return tradeCommand;
	}

	public void setTradeCommand(TradeCommand tradeCommand) {
		this.tradeCommand = tradeCommand;
	}

	public GFactions getgFactionsPlugin() {
		return gFactionsPlugin;
	}

	public void setgFactionsPlugin(GFactions gFactionsPlugin) {
		this.gFactionsPlugin = gFactionsPlugin;
	}
	
	
}
