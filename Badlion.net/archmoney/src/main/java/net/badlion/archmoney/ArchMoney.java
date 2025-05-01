package net.badlion.archmoney;

import net.badlion.archmoney.commands.*;
import net.badlion.archmoney.events.MoneyChangeEvent;
import net.badlion.archmoney.listeners.LoginListener;
import net.badlion.gberry.Gberry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ArchMoney extends JavaPlugin {

	public static int DEFAULT_MONEY_AMOUNT = 100;
	public static int DEFAULT_FACTION_AMOUNT = 0;

    private ArchMoney plugin;

    private String serverName;
    private boolean factionServer = false;
	
	private Map<String, Integer> moneyCache = new ConcurrentHashMap<String, Integer>();

	public void onEnable(){
		this.plugin = this;
		
        this.saveDefaultConfig();
		
		this.serverName = this.getConfig().getString("archmoney.name");

        if (this.serverName.toLowerCase().contains("faction")) {
            this.factionServer = true;
        } else if (this.serverName.toLowerCase().equals("default")){
			Bukkit.getLogger().info(ChatColor.RED + "You are not allowed to use the default config");
			getServer().dispatchCommand(getServer().getConsoleSender(), "stop");
        }

		// TODO: FIX
        //Create code
		/*String query = "CREATE TABLE IF NOT EXISTS " + this.serverName + "_money_balances (uuid VARCHAR(64) NOT NULL,balance INT(11) NOT NULL,UNIQUE INDEX uuid (uuid),INDEX balance (balance)) COLLATE='utf8_general_ci' ENGINE=InnoDB;";
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			Gberry.executeUpdate(connection, ps);
			
		} catch (SQLException e) {
			e.printStackTrace();
			getServer().dispatchCommand(getServer().getConsoleSender(), "stop");
		} finally {		
			if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
		} 
		
		query = "CREATE TABLE IF NOT EXISTS " + this.serverName + "_money_history (to_uuid VARCHAR(64) NOT NULL,from_uuid VARCHAR(64) NOT NULL,amount INT(11) NOT NULL,reason VARCHAR(255) NOT NULL,\"time\" DATETIME NOT NULL,INDEX to_uuid (to_uuid),INDEX from_uuid (from_uuid)) COLLATE='utf8_general_ci' ENGINE=InnoDB;";
		connection = null;
		ps = null;
		
		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			Gberry.executeUpdate(connection, ps);
			
		} catch (SQLException e) {
			e.printStackTrace();
			getServer().dispatchCommand(getServer().getConsoleSender(), "stop");
		} finally {		
			if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}*/
		String query;
		Connection connection = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
        
		query = "SELECT * FROM " + this.serverName + "_money_balances WHERE uuid LIKE '~faction_%';";
		connection = null;
		ps = null;
		rs = null;
		
		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			rs = Gberry.executeQuery(connection, ps);
			
			while (rs.next()){
                this.moneyCache.put(rs.getString("uuid"), rs.getInt("balance"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			getServer().dispatchCommand(getServer().getConsoleSender(), "stop");
		} finally {		
			if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}
		
		this.getServer().getPluginManager().registerEvents(new LoginListener(this), this);
		
		this.getCommand("pay").setExecutor(new PayCommand(this));
		this.getCommand("moneygive").setExecutor(new GiveCommand(this));
		this.getCommand("moneytake").setExecutor(new TakeCommand(this));
		this.getCommand("money").setExecutor(new MoneyCommand(this));
		this.getCommand("top").setExecutor(new TopCommand(this));
	}

	public String format(int money) {
		return money + " Dollars";
	}

	/**
	* Returns balance for current UUID according to cache
	*/
	public Integer checkBalance(String uuid){
		Integer  balance;
		balance = this.moneyCache.get(uuid);
		if (balance == null) {
			balance = -1;
		}
		return balance;
	}
	
	/**
	* Changes a single UUID balance by the specified amount (can be + or -)
	* Edits both SQL and Cache
	* WARNING: Does not log, must call log function yourself
	*/
	public void changeBalance(final String uuid, final int amount){
		Integer balance = this.moneyCache.get(uuid);
		if (balance != null){
			this.moneyCache.put(uuid, balance + amount);
		}
			
		getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
			public void run(){
				//String query = "INSERT INTO " + serverName + "_money_balances (uuid, balance) VALUES (?,?) ON DUPLICATE KEY UPDATE balance=balance+?;";
				String query = "UPDATE " + ArchMoney.this.serverName + "_money_balances SET balance = balance + ? WHERE uuid = ?;\n";
				query += "INSERT INTO " + ArchMoney.this.serverName + "_money_balances (uuid, balance) SELECT ?, ? WHERE NOT EXISTS " +
								 "(SELECT 1 FROM " + ArchMoney.this.serverName + "_money_balances WHERE uuid = ?);";

				Connection connection = null;
				PreparedStatement ps = null;
				try{
					connection = Gberry.getConnection();
					ps = connection.prepareStatement(query);

					ps.setInt(1, amount);
					ps.setString(2, uuid);
					ps.setString(3, uuid);
					ps.setInt(4, amount);
					ps.setString(5, uuid);

                    // Try to stop race conditions with postgres
                    synchronized (this) {
					    Gberry.executeUpdate(connection, ps);
                    }

                    if (ArchMoney.this.factionServer) {

                        final boolean faction = uuid.startsWith("~faction_");

                        ArchMoney.this.plugin.getServer().getScheduler().runTask(ArchMoney.this.plugin, new Runnable() {
                            @Override
                            public void run() {

                                // Call tab list event
                                MoneyChangeEvent event = null;
                                if (faction) {
                                    event = new MoneyChangeEvent(null, uuid.substring(9));
                                } else {
                                    Player player = ArchMoney.this.plugin.getServer().getPlayer(UUID.fromString(uuid));

                                    if (player != null) { // Online
                                        event = new MoneyChangeEvent(player, null);
                                    }
                                }

                                if (event != null) {
                                    ArchMoney.this.plugin.getServer().getPluginManager().callEvent(event);
                                }
                            }
                        });
                    }

				} catch (SQLException e) {
		        	e.printStackTrace();
		        } finally {
		            if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
		            if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
		        }
			}
		});
		
	}
	
	/**
	* Changes a single UUID balance by the specified amount (can be + or -)
	* Edits both SQL and Cache and logs with the given reason
	*/
	public void changeBalance(final String uuid, final int amount, final String reason){
		Integer balance = this.moneyCache.get(uuid);
		if (balance != null){
			this.moneyCache.put(uuid, balance + amount);
		}
			
		getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
			public void run(){
				//String query = "INSERT INTO " + serverName + "_money_balances (uuid, balance) VALUES (?,?) ON DUPLICATE KEY UPDATE balance=balance+?;";
				String query = "UPDATE " + ArchMoney.this.serverName + "_money_balances SET balance = balance + ? WHERE uuid = ?;\n";
				query += "INSERT INTO " + ArchMoney.this.serverName + "_money_balances (uuid, balance) SELECT ?, ? WHERE NOT EXISTS " +
								 "(SELECT 1 FROM " + ArchMoney.this.serverName + "_money_balances WHERE uuid = ?);";

				Connection connection = null;
				PreparedStatement ps = null;
				try{
					connection = Gberry.getConnection();
					ps = connection.prepareStatement(query);

					ps.setInt(1, amount);
					ps.setString(2, uuid);
					ps.setString(3, uuid);
					ps.setInt(4, amount + ArchMoney.DEFAULT_MONEY_AMOUNT);
					ps.setString(5, uuid);

                    // Try to stop race conditions with postgres
                    synchronized (this) {
					    Gberry.executeUpdate(connection, ps);
                    }

                    if (ArchMoney.this.factionServer) {

                        final boolean faction = uuid.startsWith("~faction_");

                        ArchMoney.this.plugin.getServer().getScheduler().runTask(ArchMoney.this.plugin, new Runnable() {
                            @Override
                            public void run() {

                                // Call tab list event
                                MoneyChangeEvent event = null;
                                if (faction) {
                                    event = new MoneyChangeEvent(null, uuid.substring(9));
                                } else {
                                    Player player = ArchMoney.this.plugin.getServer().getPlayer(UUID.fromString(uuid));

                                    if (player != null) { // Online
                                        event = new MoneyChangeEvent(player, null);
                                    }
                                }

                                if (event != null) {
                                    ArchMoney.this.plugin.getServer().getPluginManager().callEvent(event);
                                }
                            }
                        });
                    }

				} catch (SQLException e) {
		        	e.printStackTrace();
		        } finally {
		            if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
		            if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
		        }
				if (amount > 0){
					ArchMoney.this.logTransaction(uuid, "~Console", amount, reason);
				} else {
					ArchMoney.this.logTransaction("~Console", uuid, amount * -1, reason);
				}
			}
		});
		
	}
	
	/**
	* Calls changeBalance on both sides
	* Edits both SQL and Cache
	*/ 
	public void transfer(final String to_uuid, final String from_uuid, final int amount, final String reason){
		this.changeBalance(to_uuid, amount);
		this.changeBalance(from_uuid, amount * -1);
		this.getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
			public void run() {
				ArchMoney.this.logTransaction(to_uuid, from_uuid, amount, reason);
			}
		});
	}
	
	/**
	* Logger for all money transactions
	* WARNING: This function should never be called on the main thread!
	*/
	public void logTransaction(String to_uuid, String from_uuid, int amount, String reason){
		String query = "INSERT INTO " + this.serverName + "_money_history (to_uuid, from_uuid, amount, reason, time) VALUES (?,?,?,?,?);";
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, to_uuid);
			ps.setString(2, from_uuid);
			ps.setInt(3, amount);
			ps.setString(4, reason);
			ps.setTimestamp(5, new java.sql.Timestamp(new DateTime(DateTimeZone.UTC).getMillis()));
			Gberry.executeUpdate(connection, ps);
        } catch (SQLException e) {
        	e.printStackTrace();
        } finally {
            if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
	}
	
	/**
	* Creates Row on SQL
	* Adds to cache
	* Calls logTransaction()
	* WARNING: This function should never be called on the main thread!
	*/
	public void createFactionBank(final String factionID){
		this.moneyCache.put("~faction_" + factionID, 0);
		this.changeBalance("~faction_" + factionID, ArchMoney.DEFAULT_FACTION_AMOUNT);
		this.getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
			public void run(){
				ArchMoney.this.logTransaction("~faction_" + factionID, "~Console", ArchMoney.DEFAULT_FACTION_AMOUNT, "Initial Faction Creation");
			}
		});
	}
	
	/**
	* Deletes Row on SQL
	* Removes from cache
	* Calls logTransaction()
	* WARNING: This function should never be called on the main thread!
	*/
	public void deleteFactionBank(String factionID){
		String query = "DELETE FROM " + this.serverName + "_money_balances WHERE uuid = ?;";
		Connection connection = null;
		PreparedStatement ps = null;
		try {
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, "~faction_" + factionID);
			Gberry.executeUpdate(connection, ps);
		} catch (SQLException e) {
        	e.printStackTrace();
        } finally {
            if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
            if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
		this.logTransaction("~Console", "~faction_" + factionID,  this.moneyCache.remove("~faction_" + factionID), "Deletion of faction to bank");
	}
	
	/**
	* Checks balance from SQL, returns balance or -1 if does not exist
	* WARNING: This function should never be called on the main thread!
	*/
	public Integer checkBalanceSQL(String uuid){
		Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
		Integer balance = -1;
        
        try {
			String query = "SELECT * FROM " + this.serverName + "_money_balances WHERE uuid = ?;";
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, uuid);
			rs = Gberry.executeQuery(connection, ps);
			
			if (rs.next()){
				balance = rs.getInt("balance");
			} 
		} catch (SQLException e){
			e.printStackTrace();
		} finally {		
			if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}
        
        return balance;
	}
	
	/**
	* Returns the whole moneyCache
	*/
	public Map <String, Integer> getMoneyCache(){
		return moneyCache;
	}

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public boolean isFactionServer() {
        return factionServer;
    }

    public void setFactionServer(boolean factionServer) {
        this.factionServer = factionServer;
    }

	public void setMoneyCache(Map<String, Integer> moneyCache) {
	   this.moneyCache = moneyCache;
	}

}
