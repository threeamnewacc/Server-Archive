package net.badlion.kits;

import net.badlion.gberry.Gberry;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

public class LoadCommand implements org.bukkit.command.CommandExecutor {

	private final KitLoader plugin;
  
	public LoadCommand(KitLoader plugin) {
		this.plugin = plugin;
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
	 
	public static List<ConfigurationSerializable> deserializeItemList(List<Map<String, Object>> itemList) {
	    List<ConfigurationSerializable> returnVal = new ArrayList<ConfigurationSerializable>();
	    for (Map<String, Object> map : itemList) {
	    	if (map == null)
	    		returnVal.add(null);
	    	else
	    		returnVal.add(deserialize(map));
	    }
	    return returnVal;
	}
  
  	public boolean onCommand(final CommandSender sender, org.bukkit.command.Command cmd, String label, final String[] args) {
  		if ((sender instanceof Player)) {
  			Player p = (Player) sender;
  			if (args.length < 2) {
  				return false;
  			}
	  		if (!p.isOp() && !this.plugin.getCmdSigns().getValidHashes().contains(args[0])) {
				p.sendMessage("Invalid authorization.");
				return false;
			}
  		}

		if ((sender instanceof Player)) {
			final Player p = (Player)sender;
			if (args.length >= 2) {
				this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

					@SuppressWarnings("unchecked")
					public void run() {
						Connection con = null;
						ResultSet rs = null;
						PreparedStatement ps = null;
						try {
							con = Gberry.getConnection();
							String tag = args.length == 3 ? args[2] : "";
							String query = "SELECT * FROM kits WHERE kitname = ? AND tag = ?;";
							ps = con.prepareStatement(query);
							ps.setString(1, args[1]);
							ps.setString(2, tag);
							rs = Gberry.executeQuery(con, ps);

							if (rs.next()) {
								// Items
								final ByteArrayInputStream baisItems = new ByteArrayInputStream(rs.getBytes("items"));
								final ByteArrayInputStream baisArmor = new ByteArrayInputStream(rs.getBytes("armor"));

								ItemStack [] items = null;
								ItemStack [] armor = null;

								// More efficient to handle this ASYNC
								try {
									ObjectInputStream ins = new ObjectInputStream(baisItems);
									List<ConfigurationSerializable> list = deserializeItemList((List<Map<String, Object>>)ins.readObject());
									items = new ItemStack[list.size()];
									for (int i = 0; i < items.length; ++i) {
										if (list.get(i) == null) {
											items[i] = null;
										} else {
											items[i] = (ItemStack) list.get(i);
										}
									}

									ins = new ObjectInputStream(baisArmor);
									list = deserializeItemList((List<Map<String, Object>>)ins.readObject());
									armor = new ItemStack[list.size()];
									for (int i = 0; i < armor.length; ++i) {
										if (list.get(i) == null) {
											armor[i] = null;
										} else {
											armor[i] = (ItemStack) list.get(i);
										}
									}

									final ItemStack[] finalItems = items;
									final ItemStack[] finalArmor = armor;

									LoadCommand.this.plugin.getServer().getScheduler().runTask(LoadCommand.this.plugin, new Runnable() {
										@Override
										public void run() {
											p.getInventory().setContents(finalItems);
											p.getInventory().setArmorContents(finalArmor);
											p.sendMessage(ChatColor.GREEN + "Kit loaded.");
										}
									});
								} catch(StreamCorruptedException e) {
									// Corrupt kit
									p.sendMessage(ChatColor.RED + "Your kit is corrupt. Please re-create it.");
									query = "DELETE FROM kits WHERE kitname = ? AND tag = ?;";
									ps.close();

									ps = con.prepareStatement(query);
									ps.setString(1, args[1]);
									ps.setString(2, tag);

									Gberry.executeUpdate(con, ps);
								} catch (IOException e) {
									p.sendMessage(ChatColor.RED + "This kit is corrupted. It needs to be re-saved. Contact an admin if this keeps happening.");
								} catch(ClassNotFoundException ex) {
									p.sendMessage(ChatColor.RED + "This kit is corrupted. It needs to be re-saved. Contact an admin if this keeps happening.");
								}
							} else {
								p.sendMessage(ChatColor.RED + "No kit found.");
							}
						} catch (SQLException ex) {
							ex.printStackTrace();
							org.bukkit.Bukkit.getLogger().severe(ex.getMessage());
							p.sendMessage(ChatColor.RED + "Something broke.  Contact an admin if it continues to not work.");
						} finally {
							if (rs != null) { try { rs.close(); } catch (SQLException e) { e.printStackTrace(); } }
							if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
							if (con != null) { try { con.close(); } catch (SQLException e) { e.printStackTrace(); } }
						}
					}
				});
			}
		}

		return true;
	}

	public byte[] decompress(byte [] bytes) {
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			GZIPInputStream gis = new GZIPInputStream(bis);
			byte [] buf = new byte[1024];
			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			int len;
			while ((len = gis.read(buf)) > 0) {
				bos.write(buf);
			}

			return bos.toByteArray();
		} catch (Exception e) {
			return new byte [] {};
		}
	}

}
