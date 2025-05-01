package net.badlion.archmoney.listeners;

import net.badlion.archmoney.ArchMoney;
import net.badlion.gberry.Gberry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;


public class LoginListener implements Listener {
	
	private ArchMoney plugin;
	
	public LoginListener(ArchMoney plugin){
		this.plugin = plugin;
	}
	
    @EventHandler(priority= EventPriority.LOW)
    public void onAsyncLogin(AsyncPlayerPreLoginEvent event) {
		// Cancelled by BM most likely, don't waste time
		if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
			return;
		}

    	String player_uuid = event.getUniqueId().toString();
    	Map<String, Integer> money_cache = plugin.getMoneyCache();
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
    	
		try {
			String query = "SELECT * FROM " + this.plugin.getServerName() + "_money_balances WHERE uuid = ?;";
			connection = Gberry.getConnection();
			ps = connection.prepareStatement(query);
			ps.setString(1, player_uuid);
			rs = Gberry.executeQuery(connection, ps);
			
			if (rs.next()){
				money_cache.put(player_uuid, rs.getInt("balance"));
				if (rs.getInt("balance") < 0){
					event.setKickMessage("You are not allowed to join the server with a negative money balance - please contact an admin on forums!");
					event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
				}
			} else {
				money_cache.put(player_uuid, 0);
				plugin.changeBalance(player_uuid, ArchMoney.DEFAULT_MONEY_AMOUNT);
    			plugin.logTransaction(player_uuid, "~Console", ArchMoney.DEFAULT_MONEY_AMOUNT, "Initial join bonus");
			}
			

			
		} catch (SQLException e){
			e.printStackTrace();
			event.setKickMessage("Server malfunction, please contact an Administrator if this continues to happen!");
			event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
		} finally {		
			if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}
    }
}
