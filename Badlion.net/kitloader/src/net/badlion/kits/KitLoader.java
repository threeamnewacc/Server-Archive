package net.badlion.kits;

import net.badlion.cmdsigns.CmdSigns;
import net.badlion.gberry.Gberry;
import net.badlion.kitpvp.PVPServer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Referrence for more advanced program: http://forums.bukkit.org/threads/serializing-itemmeta-and-all-your-wildest-dreams.137325/

public class KitLoader extends JavaPlugin {
	
	private CmdSigns cmdSigns;
	private PVPServer pvp;
	private Map<String, ArrayList<Integer>> bannedItems;
	private Map<String, ArrayList<Integer>> bannedArmorItems;
	private Map<String, ArrayList<Pair<Enchantment, Integer>>> bannedEnchantments;
	private FileConfiguration fileConfig;
	
	@Override
	public void onEnable() {
		// Link DB
		this.bannedItems = new HashMap<String, ArrayList<Integer>>();
		this.bannedArmorItems = new HashMap<String, ArrayList<Integer>>();
		this.bannedEnchantments = new HashMap<String, ArrayList<Pair<Enchantment, Integer>>>();

		cmdSigns = (CmdSigns) this.getServer().getPluginManager().getPlugin("CmdSigns");
		pvp = (PVPServer) this.getServer().getPluginManager().getPlugin("PVPServer");
		this.loadConfiguration();
		
	    getCommand("save").setExecutor(new SaveCommand(this));
	    getCommand("load").setExecutor(new LoadCommand(this));
	}
	
	@Override
	public void onDisable() {
	}
	
	@SuppressWarnings("rawtypes")
	public void loadConfiguration() {
		this.fileConfig = Gberry.getFileConfiguration();
		
		// Get all rule sets
		if (this.fileConfig.getKeys(false).contains("rulesetsKeys")) {
			List<?> ruleSets = this.fileConfig.getList("rulesetsKeys");
			for (int i = 0; i < ruleSets.size(); ++i) {
				ArrayList<Integer> items = new ArrayList<Integer>();
				List<?> itemIds = this.fileConfig.getList("rulesets." + (String)ruleSets.get(i));
				if (itemIds != null) {
					for (int j = 0; j < itemIds.size(); ++j) {
						ItemStack item = new ItemStack((Integer)itemIds.get(j));
						if (item != null) {
							items.add(item.getTypeId());
						}
					}
					bannedItems.put((String)ruleSets.get(i), items);
				}
			}
		}
			
		// Get all banned armor sets
		if (this.fileConfig.getKeys(false).contains("rulearmorsetsKeys")) {
			List<?> ruleArmorSets = this.fileConfig.getList("rulearmorsetsKeys");
			if (ruleArmorSets != null) {
				for (int i = 0; i < ruleArmorSets.size(); ++i) {
					ArrayList<Integer> items = new ArrayList<Integer>();
					List<?> itemIds = this.fileConfig.getList("rulearmorsets." + (String)ruleArmorSets.get(i));
					if (itemIds != null) {
						for (int j = 0; j < itemIds.size(); ++j) {
							ItemStack item = new ItemStack((Integer)itemIds.get(j));
							if (item != null) {
								items.add(item.getTypeId());
							}
						}
						bannedArmorItems.put((String)ruleArmorSets.get(i), items);
					}
				}
			}
		}
		
		// Get all banned enchants
		if (this.fileConfig.getKeys(false).contains("ruleEnchantKeys")) {
			List<?> ruleEnchantSets = this.fileConfig.getList("ruleEnchantKeys");
			if (ruleEnchantSets != null) {
				for (int i = 0; i < ruleEnchantSets.size(); ++i) {
					ArrayList<Pair<Enchantment, Integer>> enchantments = new ArrayList<Pair<Enchantment, Integer>>();
					List<?> enchantIds = this.fileConfig.getList("ruleenchantsets." + (String)ruleEnchantSets.get(i));
					if (enchantIds != null) {
						for (int j = 0; j < enchantIds.size(); ++j) {
							String[] parts = ((String) enchantIds.get(j)).split("s");
							Enchantment enchantment = new EnchantmentWrapper(Integer.parseInt(parts[0]));
							if (enchantment != null) {
								enchantments.add(new Pair(enchantment, Integer.parseInt(parts[1])));
							}
						}
						bannedEnchantments.put((String)ruleEnchantSets.get(i), enchantments);
					}
				}
			}
		}
	}

	public PVPServer getPvp() {
		return pvp;
	}

	public void setPvp(PVPServer pvp) {
		this.pvp = pvp;
	}

	public CmdSigns getCmdSigns() {
		return cmdSigns;
	}

	public void setCmdSigns(CmdSigns cmdSigns) {
		this.cmdSigns = cmdSigns;
	}

	public Map<String, ArrayList<Integer>> getBannedItems() {
		return bannedItems;
	}

	public void setBannedItems(Map<String, ArrayList<Integer>> bannedItems) {
		this.bannedItems = bannedItems;
	}

	public Map<String, ArrayList<Integer>> getBannedArmorItems() {
		return bannedArmorItems;
	}

	public void setBannedArmorItems(Map<String, ArrayList<Integer>> bannedArmorItems) {
		this.bannedArmorItems = bannedArmorItems;
	}

	public Map<String, ArrayList<Pair<Enchantment, Integer>>> getBannedEnchantments() {
		return bannedEnchantments;
	}

	public void setBannedEnchantments(
			Map<String, ArrayList<Pair<Enchantment, Integer>>> bannedEnchantments) {
		this.bannedEnchantments = bannedEnchantments;
	}
}
