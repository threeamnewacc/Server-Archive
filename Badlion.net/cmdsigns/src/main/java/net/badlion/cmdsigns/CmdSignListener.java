package net.badlion.cmdsigns;

import net.badlion.gberry.Gberry;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CmdSignListener implements Listener {
	
	private CmdSigns plugin;
	private Map<UUID, Long> lastUse;
	public static int COOLDOWN = 500;

	public CmdSignListener(CmdSigns plugin) {
		this.plugin = plugin;
		this.lastUse = new HashMap<>();
	}
	
	@EventHandler(priority=EventPriority.LOWEST, ignoreCancelled=true)
	public void onPlayerClickSign(PlayerInteractEvent event){
		// Fix so you can only right click (should have always been like this)
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}

		Block block = event.getClickedBlock();
		if (block != null && block.getType() == Material.WALL_SIGN) { // HAX, this is a sign
			Player player = event.getPlayer();
			
			// Spectator hack
			if (!player.spigot().getCollidesWithEntities()) {
				// Not allowed to use signs
				player.sendFormattedMessage("{0}Not allowed to use signs in spectator mode.", ChatColor.RED);
				return;
			}
			
			// We are trying to do something with the sign, nothing to do here
			if (this.plugin.getAuthorizedRemovalPlayers().contains(player) || this.plugin.getAuthorizedAddPlayers().containsKey(player)) {
				return;
			}
			
			// Are we spamming?
			Long now = System.currentTimeMillis();
			Long lastUsed  = lastUse.get(player.getUniqueId());
			if (lastUsed != null) {
				if (lastUsed + COOLDOWN > now) {
                    if (this.plugin.getConfig().getBoolean("cmdsigns.vebose")) {
					    player.sendFormattedMessage("{0}Stop spamming signs.  Wait a bit longer inbetween clicks.", ChatColor.RED);
                    }
					event.setCancelled(true);
					return;
				} else {
					this.lastUse.put(player.getUniqueId(), now);
				}
			} else {
				this.lastUse.put(player.getUniqueId(), now);
			}
			
			if (this.plugin.getCmdSigns().containsKey(block)) {
				CmdSign cmdSign = this.plugin.getCmdSigns().get(block);
				
				if (cmdSign.isPremium() && !player.hasPermission("badlion.donator")) {
					player.sendFormattedMessage("{0}This is a donator feature only.  Donate today at www.badlion.net", ChatColor.RED);
					return;
				}
				
				// Commands
				String commands = cmdSign.getCommands();
				
				String hash = this.plugin.generateHash();

				// Issue the commands 
				commands = commands.replaceAll("<hashcode>", hash);
				String [] commandArray = commands.split("\\\\n");
				for (String command : commandArray) {
					// Gotta manually fire the event
					this.plugin.getServer().getPluginManager().callEvent(new PlayerCommandPreprocessEvent(player, "/" + command));
				}

				// Delete the hash code
				this.plugin.getValidHashes().remove(hash);
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		this.lastUse.remove(event.getPlayer().getUniqueId()); // no memory leak
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent event)
	{
		if (event.isCancelled()) {
			return;
		}
		
		final Block block = event.getBlock();
		
		Player player = event.getPlayer();
		if (this.plugin.getCmdSigns().containsKey(block) && this.plugin.getAuthorizedRemovalPlayers().contains(player)){
			// Has permission to remove sign, purge from DB
			this.plugin.getCmdSigns().remove(block);
			
			this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
	  			
				@Override
				public void run() {
					Connection connection = null;
					PreparedStatement ps = null;
					
					try {
						String query = "DELETE FROM " + plugin.getTableName() + " WHERE x = ? AND y = ? AND z = ? AND world = ?;";
						connection = Gberry.getConnection();
						ps = connection.prepareStatement(query);
						ps.setInt(1, block.getX());
						ps.setInt(2, block.getY());
						ps.setInt(3, block.getZ());
						ps.setString(4, block.getWorld().getName());
						Gberry.executeUpdate(connection, ps);
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
						if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
					}
				}
			});
			
			// No more access
			this.plugin.getAuthorizedRemovalPlayers().remove(player);
			player.sendMessage(ChatColor.GREEN + "Removed command sign.");
		} else if (this.plugin.getAuthorizedAddPlayers().containsKey(player)) {
			// Add locally
			String cmd = this.plugin.getAuthorizedAddPlayers().get(player);
			
			boolean isPremium = false;
			if (cmd.startsWith("&p")) {
				isPremium = true;
				cmd = cmd.substring(2, cmd.length());
			}
			
			final boolean finalIsPremium = isPremium;
			final String finalCmd = cmd;
			
			// Apparently this will insert/update
			if (this.plugin.getCmdSigns().containsKey(block)) {
				this.plugin.getCmdSigns().get(block).setCommands(cmd);
			} else {
				this.plugin.getCmdSigns().put(block, new CmdSign(block, finalIsPremium, cmd));
			}
			
			this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
	  			
				@Override
				public void run() {
					Connection connection = null;
					PreparedStatement ps = null;
					
					try {
						//String query = "INSERT INTO " + plugin.getTableName() + " (x, y, z, commands, p, world) VALUES(?, ?, ?, ?, ?, ?) " +
						//		"ON DUPLICATE KEY UPDATE x=?, y=?, z=?, commands=?, p=?, world=?;";
						String query = "UPDATE " + plugin.getTableName() + " SET x = ?, y = ?, z = ?, commands = ?, p = ?, world = ? WHERE x = ? AND y = ? AND z = ? AND world = ?;\n";
						query += "INSERT INTO " + plugin.getTableName() + " (x, y, z, commands, p, world) SELECT ?, ?, ?, ?, ?, ? WHERE NOT EXISTS " +
										 "(SELECT 1 FROM " + plugin.getTableName() + " WHERE x = ? AND y = ? AND z = ? AND world = ?);";
						connection = Gberry.getConnection();
						ps = connection.prepareStatement(query);
						ps.setInt(1, block.getX());
						ps.setInt(2, block.getY());
						ps.setInt(3, block.getZ());
						ps.setString(4, finalCmd);
						ps.setInt(5, (finalIsPremium ? 1 : 0));
						ps.setString(6, block.getWorld().getName());
						ps.setInt(7, block.getX());
						ps.setInt(8, block.getY());
						ps.setInt(9, block.getZ());
						ps.setString(10, block.getWorld().getName());
						ps.setInt(11, block.getX());
						ps.setInt(12, block.getY());
						ps.setInt(13, block.getZ());
						ps.setString(14, finalCmd);
						ps.setInt(15, (finalIsPremium ? 1 : 0));
						ps.setString(16, block.getWorld().getName());
						ps.setInt(17, block.getX());
						ps.setInt(18, block.getY());
						ps.setInt(19, block.getZ());
						ps.setString(20, block.getWorld().getName());

						Gberry.executeUpdate(connection, ps);
					} catch (SQLException e) {
						e.printStackTrace();
					} finally {
						if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
						if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
					}
				}
			});
			
			// No more access
			this.plugin.getAuthorizedAddPlayers().remove(player);
			player.sendMessage(ChatColor.GREEN + "Added/updated command sign.");
			
			event.setCancelled(true);
		} else {
			// If they had permission, it is now expired
			if (this.plugin.getAuthorizedAddPlayers().containsKey(player)) {
				this.plugin.getAuthorizedAddPlayers().remove(player);
			}
			if (this.plugin.getAuthorizedRemovalPlayers().contains(player)) {
				this.plugin.getAuthorizedRemovalPlayers().remove(player);
			}
		}
	 }

}
