package net.badlion.kits;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPOutputStream;

import net.badlion.gberry.Gberry;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SaveCommand implements org.bukkit.command.CommandExecutor {

	private final KitLoader plugin;
	
	public SaveCommand(KitLoader plugin) {
		this.plugin = plugin;
	}
	
	public static List<Map<String, Object>> serializeItemList(List<ConfigurationSerializable> list) {
	    List<Map<String, Object>> returnVal = new ArrayList<Map<String, Object>>();
	    for (ConfigurationSerializable cs : list) {
	    	if (cs == null)
	    		returnVal.add(null);
	    	else
	    		returnVal.add(serialize(cs));
	    }
	    return returnVal;
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
	
	public boolean removeBannedItems(List<ConfigurationSerializable> itemsList, ArrayList<Integer> bannedItems, ArrayList<Pair<Enchantment, Integer>> bannedEnchantments, boolean stupidPotions) {
		boolean flag = false;
		if (itemsList == null)
			return flag;
		for (int i = 0; i < itemsList.size(); ++i) {
			ItemStack item = (ItemStack) itemsList.get(i);
			if (item != null) {
				if (bannedItems.contains(item.getTypeId())) {
					itemsList.set(i, null); // NOT ALLOWED 
					flag = true;
				} else if (item.getTypeId() == 373 && bannedItems.contains(1000000 + (int)item.getDurability())) {
					itemsList.set(i, null); // NOT ALLOWED HAX
					flag = true;
				}
				// TODO: maybe we can do better than O (n^2)...
				if (bannedEnchantments != null) {
					for (int j = 0; j < bannedEnchantments.size(); ++j) {
						if (item.containsEnchantment(bannedEnchantments.get(j).getLeft())) {
							if (bannedEnchantments.get(j).getRight() != 0) {
								// if enchant too high, remove, otherwise ur fine
								if (item.getEnchantmentLevel(bannedEnchantments.get(j).getLeft()) > bannedEnchantments.get(j).getRight()) {
									// Have to remove and then re-add to limit it appears
									item.removeEnchantment(bannedEnchantments.get(j).getLeft());
									item.addEnchantment(bannedEnchantments.get(j).getLeft(), bannedEnchantments.get(j).getRight());
									flag = true;
								}
							} else {
								// Just remove it
								item.removeEnchantment(bannedEnchantments.get(j).getLeft());
								flag = true;
							}
						}
					}
				}
			}
		}
		return flag;
	}

  	public boolean onCommand(final CommandSender sender, org.bukkit.command.Command cmd, String label, final String[] args) {
  		// Stupid exceptions
  		if (args.length < 1) {
  			return true;
  		}
  		
  		if ((sender instanceof Player)) {
  			Player p = (Player) sender;
	  		if (!p.isOp() && !this.plugin.getCmdSigns().getValidHashes().contains(args[0])) {
				p.sendMessage("Invalid authorization.");
				return false;
			}
  		}

		if ((sender instanceof Player)) {
			final Player p = (Player)sender;
			if (args.length >= 2) {
				try {
					// Serialize it all
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(bos);

					// Re-organize the data
					List<ConfigurationSerializable> itemsList = new ArrayList<ConfigurationSerializable>();
					Collections.addAll(itemsList, p.getInventory().getContents());

					// Remove any banned items
					String tag = args.length < 3 ? "" : args[2];
					boolean sentMessage = false;
					if (args.length == 3 && plugin.getBannedItems().containsKey(args[2])) {
						if (removeBannedItems(itemsList, plugin.getBannedItems().get(args[2]), plugin.getBannedEnchantments().get(args[2]), true)) {
							tag = args[2];
							p.sendMessage(ChatColor.RED + "Some items were not allowed for this rule set.  They have been removed.");
							sentMessage = true;

							// Update their inventory so they can see what isn't allowed
							ItemStack [] itemsArray = new ItemStack[itemsList.size()];
							for (int i = 0; i < itemsList.size(); ++i) {
								itemsArray[i] = (ItemStack)itemsList.get(i);
							}
							p.getInventory().setContents(itemsArray);
						}
					}

					List<Map<String, Object>> items = serializeItemList(itemsList);
					oos.writeObject(items);
					oos.flush();
					oos.close();
					bos.close();
					final byte [] data = bos.toByteArray();

					// Armor now
					ByteArrayOutputStream bos2 = new ByteArrayOutputStream();
					ObjectOutputStream oos2 = new ObjectOutputStream(bos2);

					List<ConfigurationSerializable> itemsList2 = new ArrayList<ConfigurationSerializable>();
					Collections.addAll(itemsList2, p.getInventory().getArmorContents());

					// Remove any banned items
					if (args.length == 3 && plugin.getBannedArmorItems().containsKey(args[2])) {
						if (removeBannedItems(itemsList2, plugin.getBannedArmorItems().get(args[2]), plugin.getBannedEnchantments().get(args[2]), false)) {
							tag = args[2];
							if (!sentMessage)
								p.sendMessage(ChatColor.RED + "Some items were not allowed for this rule set.  They have been removed.");

							// Update their inventory so they can see what isn't allowed
							ItemStack [] itemsArray = new ItemStack[itemsList2.size()];
							for (int i = 0; i < itemsList2.size(); ++i) {
								itemsArray[i] = (ItemStack)itemsList2.get(i);
							}
							p.getInventory().setArmorContents(itemsArray);
						}
					}

					// Force them to use armor for invis hack we do in LMS/Slaughter
					if (!tag.equalsIgnoreCase("")) {
						ItemStack [] armor = p.getInventory().getArmorContents();
						for (int i = 0; i < armor.length; ++i) {
							if (armor[i] == null || armor[i].getTypeId() == 0) {
								p.sendMessage(ChatColor.RED + "You must have armor equipped to save this kit.  Please go get some and try again.");
								return true;
							}
						}
					}

					List<Map<String, Object>> items2 = serializeItemList(itemsList2);
					oos2.writeObject(items2);
					oos2.flush();
					oos2.close();
					bos2.close();
					final byte [] data2 = bos2.toByteArray();

					final String finalTag = tag;
					this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {

						public void run() {
							Connection con = null;
							PreparedStatement ps = null;

							try {
								con = Gberry.getConnection();
								//String sql = "INSERT INTO kits (owner, kitname, items, armor, tag) VALUES(?, ?, ?, ?, ?) " +
								//		"ON DUPLICATE KEY UPDATE owner=?, kitname=?, items=?, armor=?, tag=?;";
								String query = "UPDATE kits SET owner = ?, kitname = ?, items = ?, armor = ?, tag = ? WHERE tag = ? AND kitname = ?;\n";
								query += "INSERT INTO kits (owner, kitname, items, armor, tag) SELECT ?, ?, ?, ?, ? WHERE NOT EXISTS " +
												 "(SELECT 1 FROM kits WHERE tag = ? AND kitname = ?);";

								ps = con.prepareStatement(query);
								ps.setString(1, p.getUniqueId().toString());
								ps.setString(2, args[1]);
								ps.setObject(3, data);
								ps.setObject(4, data2);
								ps.setString(5, finalTag);
								ps.setString(6, finalTag);
								ps.setString(7, args[1]);
								ps.setString(8, p.getUniqueId().toString());
								ps.setString(9, args[1]);
								ps.setObject(10, data);
								ps.setObject(11, data2);
								ps.setString(12, finalTag);
								ps.setString(13, finalTag);
								ps.setString(14, args[1]);

								Gberry.executeUpdate(con, ps);
								
								p.sendMessage(ChatColor.GREEN + "Kit saved.");
							} catch (SQLException ex) {
								ex.printStackTrace();
								org.bukkit.Bukkit.getLogger().severe(ex.getMessage());
								p.sendMessage(ChatColor.RED + "Something broke.  Contact an admin if it continues to not work.");
							} finally {
								if (ps != null) { try { ps.close(); } catch (SQLException e) { e.printStackTrace(); } }
								if (con != null) { try { con.close(); } catch (SQLException e) { e.printStackTrace(); } }
							}
						}

					});
				} catch(IOException ex) {
					ex.printStackTrace();
					org.bukkit.Bukkit.getLogger().severe(ex.getMessage());
					p.sendMessage(ChatColor.RED + "Something broke.  Contact an admin if it continues to not work.");
				}
  			}
  		}

  		return true;
  	}

	public byte[] compress(byte [] bytes) {
		try {
			ByteArrayOutputStream obj = new ByteArrayOutputStream();
			GZIPOutputStream gzip = new GZIPOutputStream(obj);
			gzip.write(bytes);
			gzip.close();
			return obj.toByteArray();
		} catch (Exception e) {
			return new byte [] {};
		}
	}
	
}
