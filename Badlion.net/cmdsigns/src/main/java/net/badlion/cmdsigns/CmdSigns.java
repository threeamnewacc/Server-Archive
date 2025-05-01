package net.badlion.cmdsigns;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import net.badlion.gberry.Gberry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CmdSigns extends JavaPlugin {
	
	private HashSet<String> validHashes;
	private Map<Block, CmdSign> cmdSigns;
	private ArrayList<Player> authorizedRemovalPlayers;
	private Map<Player, String> authorizedAddPlayers;
	private RandomString randomString;
	private String tableName;

	private Gberry gberry;
	
	public CmdSigns() {
		this.validHashes = new HashSet<String>();
		this.cmdSigns = new HashMap<Block, CmdSign>();
		this.authorizedRemovalPlayers = new ArrayList<Player>();
		this.authorizedAddPlayers = new HashMap<Player, String>();
	}
	
	@Override
	public void onEnable() {
		// Link DB
		this.gberry = (Gberry) this.getServer().getPluginManager().getPlugin("Gberry");
		
		this.randomString = new RandomString(10);
		this.saveDefaultConfig();
		
		this.tableName = this.getConfig().getString("cmdsigns.name") + "_cmdsigns";
		if (!tableName.equals("default_cmdsigns")){
			
			Connection connection = null;
			PreparedStatement ps = null;
			
			try {
				String query = "CREATE TABLE IF NOT EXISTS " + this.tableName + "(\n" +
					   "    x integer NOT NULL,\n" +
					   "    y integer NOT NULL,\n" +
					   "    z integer NOT NULL,\n" +
					   "    commands text NOT NULL,\n" +
					   "    p integer DEFAULT 0 NOT NULL,\n" +
					   "    world character varying(32) NOT NULL\n" +
					   ");\n";
				connection = Gberry.getUnsafeConnection();
				ps = connection.prepareStatement(query);
				Gberry.executeUpdate(connection, ps);
			} catch (SQLException e) {
				e.printStackTrace();
				Bukkit.getLogger().severe("SQL Error on startup, disabling CmdSigns!!!");
				Bukkit.getPluginManager().disablePlugin(this);
			} finally {
				if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
				if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
			}

			getCmdSignsFromDatabase(true);
		}
		
		else {
			Bukkit.getLogger().severe("Default Name Specified, disbaling CmdSigns!!!");
			Bukkit.getPluginManager().disablePlugin(this);
		}
		
		
		
		this.getServer().getPluginManager().registerEvents(new CmdSignListener(this), this);
		this.getServer().getPluginManager().registerEvents(new PreCommandListener(this), this);
	}
	
	@Override
	public void onDisable() {
	}
	
	public String generateHash() {
		// Generate a hash code and store for later use
		String hash = this.getRandomString().nextString();
		
		while (this.getValidHashes().contains(hash)) {
			hash = this.getRandomString().nextString();
		}
		
		this.getValidHashes().add(hash);
		return hash;
	}

    public boolean validateHash(String hash) {
        return this.validHashes.remove(hash);
    }
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		if (sender instanceof Player) {
			Player player = (Player) sender;
			if (command.getName().equalsIgnoreCase("removesign")) {
				if (player.hasPermission("CmdSigns.remove")) {
					// Can only be authorized for 1 thing at a time
					if (this.authorizedAddPlayers.containsKey(player)) {
						this.authorizedAddPlayers.remove(player);
					}
					
					// Authorized now to delete 1 cmd sign
					this.authorizedRemovalPlayers.add(player);
				} else {
					player.sendFormattedMessage("{0}You do not have permission to use this command.", ChatColor.RED);
				}
			} else if (command.getName().equalsIgnoreCase("addsign") || command.getName().equalsIgnoreCase("updatesign")) {
				if (player.hasPermission("CmdSigns.add")) {
					StringBuilder commandParameter = new StringBuilder();
					
					// Premium hack
					if (args[0].equalsIgnoreCase("p")) {
						commandParameter.append("&p");
						for (int i = 1; i < args.length; ++i) {
							commandParameter.append(args[i]);
							if (i != args.length - 1)
								commandParameter.append(" ");
						}
					} else {
						for (int i = 0; i < args.length; ++i) {
							commandParameter.append(args[i]);
							if (i != args.length - 1)
								commandParameter.append(" ");
						}
					}
					
					
					
					// Can only be authorized for 1 thing at a time
					if (this.authorizedRemovalPlayers.contains(player)) {
						this.authorizedRemovalPlayers.remove(player);
					}

					// Authorized now to add 1 cmd sign
					this.authorizedAddPlayers.put(player, commandParameter.toString());
				}
			}
		}
		return true;
	}
	
	public void getCmdSignsFromDatabase(boolean isCmdSigns) {
		Connection connection = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		
		try {
			String sql = "SELECT * FROM " + this.tableName + ";";
			connection = Gberry.getUnsafeConnection();
			ps = connection.prepareStatement(sql);
			rs = Gberry.executeQuery(connection, ps);

			while (rs.next()) {
				if (isCmdSigns && !rs.getString("world").equals("world")) {
					continue; // hack in other plugins
				}
				Block block = this.getServer().getWorld(rs.getString("world")).getBlockAt(rs.getInt("x"), rs.getInt("y"), rs.getInt("z"));
				CmdSign cmdSign = new CmdSign(block, (rs.getInt("p") == 1 ? true : false), rs.getString("commands"));
				cmdSigns.put(block, cmdSign);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			org.bukkit.Bukkit.getLogger().severe(ex.getMessage());
		} finally {
			if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
			if (connection != null) { try { connection.close(); } catch (SQLException e) { e.printStackTrace(); } }
		}
	}

	public HashSet<String> getValidHashes() {
		return validHashes;
	}

	public void setValidHashes(HashSet<String> validHashes) {
		this.validHashes = validHashes;
	}

	public Map<Block, CmdSign> getCmdSigns() {
		return cmdSigns;
	}

	public void setCmdSigns(Map<Block, CmdSign> commandSigns) {
		this.cmdSigns = commandSigns;
	}
	
	public Gberry getGberry() {
		return gberry;
	}

	public void setGberry(Gberry gberry) {
		this.gberry = gberry;
	}

	public ArrayList<Player> getAuthorizedRemovalPlayers() {
		return authorizedRemovalPlayers;
	}

	public void setAuthorizedRemovalPlayers(
			ArrayList<Player> authorizedRemovalPlayers) {
		this.authorizedRemovalPlayers = authorizedRemovalPlayers;
	}

	public Map<Player, String> getAuthorizedAddPlayers() {
		return authorizedAddPlayers;
	}

	public void setAuthorizedAddPlayers(Map<Player, String> authorizedAddPlayers) {
		this.authorizedAddPlayers = authorizedAddPlayers;
	}

	public RandomString getRandomString() {
		return randomString;
	}

	public void setRandomString(RandomString randomString) {
		this.randomString = randomString;
	}
	
	public String getTableName(){
		return tableName;
	}

}
