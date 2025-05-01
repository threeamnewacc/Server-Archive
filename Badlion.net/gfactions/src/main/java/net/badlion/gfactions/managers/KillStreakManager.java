package net.badlion.gfactions.managers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import net.badlion.gfactions.GFactions;
import net.badlion.gberry.Gberry;

public class KillStreakManager {
	
	private GFactions plugin;
 	private Map<String, Integer> killStreakMap;
 	private Map<String, Integer> noobStreakMap;
	
	public KillStreakManager(GFactions plugin) {
		this.plugin = plugin;
		this.killStreakMap = new HashMap<String, Integer>();
		this.noobStreakMap = new HashMap<String, Integer>();
	}
	
	public void resetKillStreakForPlayer(final String uuid) {
		this.killStreakMap.put(uuid, 0);
		this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
			
			@Override
			public void run() {
				String query = "UPDATE " + GFactions.PREFIX + "_kill_noob_streak SET kill_streak = 0 WHERE uuid = ?;";
				
				Connection connection = null;
				PreparedStatement ps = null;
				
				try {
					// player1
					connection = Gberry.getConnection();
					ps = connection.prepareStatement(query);
					ps.setString(1, uuid);
					Gberry.executeUpdate(connection, ps);
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
					if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
				}
			}
			
		});
	}
	
	public void addKillToPlayer(final String uuid) {
		/*this.killStreakMap.put(uuid, this.killStreakMap.get(uuid) + 1);
		this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
			
			@Override
			public void run() {
				String query = "INSERT INTO faction_kill_noob_streak (uuid, kill_streak, noob_streak) VALUES (?, 1, 0) "
						+ "ON DUPLICATE KEY UPDATE kill_streak = kill_streak + 1;";
				
				Connection connection = null;
				PreparedStatement ps = null;
				
				try {
					// player1
					connection = Gberry.getConnection();
					ps = connection.prepareStatement(query);
					ps.setString(1, uuid);
					Gberry.executeUpdate(connection, ps);
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
					if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
				}
			}
			
		});*/
	}
	
	public void addNoobKillToPlayer(final String uuid) {
		/*this.killStreakMap.put(uuid, this.killStreakMap.get(uuid) - 2);
		this.noobStreakMap.put(uuid, this.noobStreakMap.get(uuid) + 1);
		this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
			
			@Override
			public void run() {
				String query = "INSERT INTO faction_kill_noob_streak (uuid, kill_streak, noob_streak) VALUES (?, -2, 1) "
						+ "ON DUPLICATE KEY UPDATE kill_streak = kill_streak - 2, noob_streak = noob_streak + 1;";
				
				Connection connection = null;
				PreparedStatement ps = null;
				
				try {
					// player1
					connection = Gberry.getConnection();
					ps = connection.prepareStatement(query);
					ps.setString(1, uuid);
					Gberry.executeUpdate(connection, ps);
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
					if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
				}
			}
			
		});*/
	}
	
	public void addKillNoobPointsForPlayer(final String uuid) {
		this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
			
			@Override
			public void run() {
				String query = "SELECT * FROM " + GFactions.PREFIX + "_kill_noob_streak WHERE uuid = ?;";
				
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
						killStreakMap.put(uuid, rs.getInt("kill_streak"));
						noobStreakMap.put(uuid, rs.getInt("noob_streak"));
					} else {
						killStreakMap.put(uuid, 0);
						noobStreakMap.put(uuid, 0);
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

	public GFactions getPlugin() {
		return plugin;
	}

	public void setPlugin(GFactions plugin) {
		this.plugin = plugin;
	}

	public Map<String, Integer> getKillStreakMap() {
		return killStreakMap;
	}

	public void setKillStreakMap(Map<String, Integer> killStreakMap) {
		this.killStreakMap = killStreakMap;
	}

	public Map<String, Integer> getNoobStreakMap() {
		return noobStreakMap;
	}

	public void setNoobStreakMap(Map<String, Integer> noobStreakMap) {
		this.noobStreakMap = noobStreakMap;
	}
	
	

}
