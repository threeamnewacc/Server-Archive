package net.badlion.smellyloot;

import net.badlion.smellyloot.commands.ReloadDropsCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class SmellyLoot extends JavaPlugin {

	private static SmellyLoot plugin;

	private Random random;

	private Map<String, Map<String, Integer>> eventDrops = new HashMap<>();
	private Map<String, DropParty> dropParties = new HashMap<>();
	private Map<String, Integer> runningDropParties = new HashMap<>();
	private Map<String, Map<Integer, ItemStack>> lootTablesRandomDrops = new HashMap<>();
	private Map<String, List<ItemStack>> lootTablesCommonDrops = new HashMap<>();
	private Map<String, Integer> totalDropChances = new HashMap<>();

	@Override
	public void onEnable() {
		SmellyLoot.plugin = this;

		this.random = new Random();

		// Config file stuff
		this.saveDefaultConfig();

		// Load config
		this.loadLootConfig();

		this.getCommand("reloaddrops").setExecutor(new ReloadDropsCommand());
	}

	@Override
	public void onDisable() {

	}

	public ItemStack getRandomItem(String lootTable) {
        Bukkit.getLogger().info("rloot " + lootTable);
		Map<Integer, ItemStack> items = SmellyLoot.getInstance().getLootTablesRandomDrops().get(lootTable);

		int randNumber = SmellyLoot.getInstance().generateNumber(SmellyLoot.getInstance().getTotalDropChances().get(lootTable));

        for (Map.Entry<String, Integer> entry : SmellyLoot.getInstance ().getTotalDropChances().entrySet()) {
            Bukkit.getLogger().info("key " + entry.getKey() + " v " + entry.getValue());
        }

        Bukkit.getLogger().info("randNumber " + randNumber);
		// Pick the item the random number corresponds to
		int currentValue = 0;
		for (Integer i : items.keySet()) {
            Bukkit.getLogger().info("i  " + i);
			if (randNumber > currentValue && randNumber < i + currentValue) {
				return items.get(i);
			}

			currentValue += i;
		}

		return null;
	}

	public List<ItemStack> getItemsToDrop(String lootTable, int numberOfItems) {
		List<ItemStack> itemsDropped = new ArrayList<>();

		// Random drops
		for (int i = 0; i < numberOfItems; i++) {
			ItemStack randomItem = SmellyLoot.getInstance().getRandomItem(lootTable);

            Bukkit.getLogger().info("randomItem " + randomItem);
			if (randomItem != null) {
				itemsDropped.add(randomItem);
			}
		}

		// Common drops
		for (ItemStack commonItem : SmellyLoot.getInstance().getLootTablesCommonDrops().get(lootTable)) {
			itemsDropped.add(commonItem);
		}

		return itemsDropped;
	}

	public void loadLootConfig() {
		try {
			// Load loot tables
			for (String lootTable : this.getConfig().getStringList("tiers_keys")) {
				// Load random items
				Map<Integer, ItemStack> randomItems = new HashMap<>();
				int dropChanceCounter = 0;
				for (String itemName : this.getConfig().getStringList("tiers." + lootTable + ".random_items_keys")) {
					Bukkit.getLogger().info(this.getConfig().getString("tiers." + lootTable + ".random_items." + itemName + ".material"));
                    ItemStack item = new ItemStack(Material.valueOf(this.getConfig().getString("tiers." + lootTable + ".random_items." + itemName + ".material")),
							this.getConfig().getInt("tiers." + lootTable + ".random_items." + itemName + ".amount"),
							(short) this.getConfig().getInt("tiers." + lootTable + ".random_items." + itemName + ".durability"));
					String displayName = this.getConfig().getString("tiers." + lootTable + ".random_items." + itemName + ".meta.title");

					List<String> lore = this.getConfig().getStringList("tiers." + lootTable + ".random_items." + itemName + ".meta.lines");

					ItemMeta itemMeta = item.getItemMeta();

					// Display name
					if (displayName != null) {
						itemMeta.setDisplayName(displayName);
					}

					// Lore
					if (lore != null) {
						itemMeta.setLore(lore);
					}

					// Enchants
					for (String key : this.getConfig().getStringList("tiers." + lootTable + ".random_items." + itemName + ".enchants_keys")) {
						itemMeta.addEnchant(
								Enchantment.getByName(this.getConfig().getString("tiers." + lootTable + ".random_items." + itemName + ".enchants." + key + ".name")),
								this.getConfig().getInt("tiers." + lootTable + ".random_items." + itemName + ".enchants." + key + ".level") - 1,
								true);
					}


					item.setItemMeta(itemMeta);

					int dropChance = this.getConfig().getInt("tiers." + lootTable + ".random_items." + itemName + ".drop_chance");
					dropChanceCounter += dropChance;

                    Bukkit.getLogger().info("drop chance " + dropChance);
                    Bukkit.getLogger().info("item is " + item);
					randomItems.put(dropChance, item);
				}

                Bukkit.getLogger().info("loottable drop chance " + lootTable + " " + dropChanceCounter + " " + randomItems.size());
				this.lootTablesRandomDrops.put(lootTable, randomItems);
				this.totalDropChances.put(lootTable, dropChanceCounter);

				// Load common items
				List<ItemStack> commonItems = new ArrayList<>();
				for (String itemName : this.getConfig().getStringList("tiers." + lootTable + ".common_items_keys")) {
					ItemStack item = new ItemStack(Material.valueOf(this.getConfig().getString("tiers." + lootTable + ".common_items." + itemName + ".material")),
							this.getConfig().getInt("tiers." + lootTable + ".common_items." + itemName + ".amount"),
							(short) this.getConfig().getInt("tiers." + lootTable + ".common_items." + itemName + ".durability"));
					String displayName = this.getConfig().getString("tiers." + lootTable + ".common_items." + itemName + ".meta.title");

					List<String> lore = this.getConfig().getStringList("tiers." + lootTable + ".common_items." + itemName + ".meta.lines");

					ItemMeta itemMeta = item.getItemMeta();

					// Display name
					if (displayName != null) {
						itemMeta.setDisplayName(displayName);
					}

					// Lore
					if (lore != null) {
						itemMeta.setLore(lore);
					}

					// Enchants
					for (String key : this.getConfig().getStringList("tiers." + lootTable + ".random_items." + itemName + ".enchants_keys")) {
						itemMeta.addEnchant(
								Enchantment.getByName(this.getConfig().getString("tiers." + lootTable + ".random_items." + itemName + ".enchants." + key + ".name")),
								this.getConfig().getInt("tiers." + lootTable + ".random_items." + itemName + ".enchants." + key + ".level") - 1,
								true);
					}


					item.setItemMeta(itemMeta);

					commonItems.add(item);
				}

				this.lootTablesCommonDrops.put(lootTable, commonItems);
			}

			// Load event drops
			for (String event : this.getConfig().getStringList("event_drops_keys")) {
				Map<String, Integer> itemDropAmounts = new HashMap<>();

				for (String lootTable : this.lootTablesCommonDrops.keySet()) {
					int numberOfItems = this.getConfig().getInt("event_drops." + event + "." + lootTable);

					// Does the value exist in config?
					if (numberOfItems != 0) {
						itemDropAmounts.put(lootTable, numberOfItems);
					}
				}

                Bukkit.getLogger().info("event " + event);

				this.eventDrops.put(event, itemDropAmounts);
			}

			// Load drop parties
			for (String dropParty : this.getConfig().getStringList("drop_parties_keys")) {
				List<Location> dropLocations = new ArrayList<>();
				for (String locationString : this.getConfig().getStringList("drop_parties." + dropParty + ".locations")) {
					String[] components = locationString.split(",");
					dropLocations.add(new Location(this.getServer().getWorld(components[3]), Integer.valueOf(components[0]),
							Integer.valueOf(components[0]), Integer.valueOf(components[0])));
				}

				this.dropParties.put(dropParty, new DropParty(this.getConfig().getString("drop_parties." + dropParty + ".loot_table"),
						this.getConfig().getLong("drop_parties." + dropParty + ".drop_interval"),
						this.getConfig().getDouble("drop_parties." + dropParty + ".drop_chance"),
						dropLocations));
			}
		} catch (Exception e) {
			this.getServer().getLogger().severe("Error loading loot tables, fix error(s) and run /reloaddrops!");
			this.getServer().getLogger().severe("Error loading loot tables, fix error(s) and run /reloaddrops!");
			this.getServer().getLogger().severe("Error loading loot tables, fix error(s) and run /reloaddrops!");

			e.printStackTrace();
		}
	}

    public static void dropItemsFromChest(PlayerInteractEvent event) {
        event.setCancelled(true);

        // Spew out the items on the ground
        Chest chest = (Chest) event.getClickedBlock().getState();
        for (ItemStack itemStack : chest.getBlockInventory().getContents()) {
            if (itemStack != null && itemStack.getType() != Material.AIR) {
                event.getClickedBlock().getWorld().dropItemNaturally(event.getClickedBlock().getLocation(), itemStack);
            }
        }

        // Remove the chest
        event.getClickedBlock().setType(Material.AIR);
    }

	public int generateNumber(int max) {
        Bukkit.getLogger().info("max " + max);
		return this.random.nextInt(max + 1);
	}

	public static SmellyLoot getInstance() {
		return SmellyLoot.plugin;
	}

	public Map<String, Map<String, Integer>> getEventDrops() {
		return eventDrops;
	}

	public Map<String, DropParty> getDropParties() {
		return dropParties;
	}

	public Map<String, Integer> getRunningDropParties() {
		return runningDropParties;
	}

	public Map<String, Map<Integer, ItemStack>> getLootTablesRandomDrops() {
		return lootTablesRandomDrops;
	}

	public Map<String, List<ItemStack>> getLootTablesCommonDrops() {
		return lootTablesCommonDrops;
	}

	public Map<String, Integer> getTotalDropChances() {
		return totalDropChances;
	}

	public class DropParty {

		private long dropInterval;
		private double dropChance;

		private String lootTable;
		private List<Location> dropLocations = new ArrayList<>();

		public DropParty(String lootTable, long dropInterval, double dropChance, List<Location> dropLocations) {
			this.lootTable = lootTable;
			this.dropInterval = dropInterval;
			this.dropChance = dropChance;
			this.dropLocations = dropLocations;
		}

		public long getDropInterval() {
			return dropInterval;
		}

		public double getDropChance() {
			return dropChance;
		}

		public String getLootTable() {
			return lootTable;
		}

		public List<Location> getDropLocations() {
			return dropLocations;
		}

	}

}
