package net.badlion.gfactions.managers;

import io.nv.bukkit.CleanroomGenerator.CleanroomChunkGenerator;
import net.badlion.gfactions.events.Dungeon;
import net.badlion.gfactions.events.DungeonChest;
import net.badlion.gfactions.GFactions;
import net.badlion.gfactions.bukkitevents.EventStateChangeEvent;
import net.badlion.gfactions.tasks.dungeon.DungeonDamageTask;
import net.badlion.gberry.Gberry;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class DungeonManager {
	
	private GFactions plugin;
	private String spawnedDungeonWorldName;
	private int zombieSpawnPercentage;
	private int skeletonSpawnPercentage;
	private int creeperSpawnPercentage;
	private int spiderSpawnPercentage;
	private int blazeSpawnPercentage;
	private int ghastSpawnPercentage;
	private int giantSpawnPercentage;
	
	private int zombieRawNumber;
	private int skeletonRawNumber;
	private int creeperRawNumber;
	private int spiderRawNumber;
	private int blazeRawNumber;
	private int ghastRawNumber;
	private int giantRawNumber;
	
	private ArrayList<String> dungeonsToChooseFrom;
    private int portalToLoad;
	private ArrayList<Dungeon> dungeons;
	private ArrayList<DungeonChest> chests;
	private Dungeon currentDungeon;
	private int spawnX;
	private int spawnY;
	private int spawnZ;
	private Location spawnLocation;
	private World world;
	private Location location1;
	private Location location2;
	private boolean allowEntry;
    private boolean portalEvent;
	
	private BukkitTask nightTask;
	private BukkitTask damageTask;
	
	private ArrayList<Location> dungeonPortal1;
	private ArrayList<Location> dungeonPortal2;
	
	public DungeonManager(GFactions plugin) {
		this.plugin = plugin;
		this.dungeonsToChooseFrom = new ArrayList<String>();
		this.dungeons = new ArrayList<Dungeon>();
		this.dungeonPortal1 = new ArrayList<Location>();
		this.dungeonPortal2 = new ArrayList<Location>();
        this.portalEvent = false;
	}
	
	@SuppressWarnings("unchecked")
	public void loadConfig() {
		// Load all the shit
		for (String dungeonName : this.dungeonsToChooseFrom) {
			// Generate the chest list
			ArrayList<DungeonChest> chests = new ArrayList<DungeonChest>();
			for (int i = 0; i < this.plugin.getConfig().getInt("gfactions.dungeons." + dungeonName + ".number_of_chests"); i++) {
				DungeonChest chest = new DungeonChest(
							this.plugin.getConfig().getInt("gfactions.dungeons." + dungeonName + ".chest" + i + ".chest_x"),
							this.plugin.getConfig().getInt("gfactions.dungeons." + dungeonName + ".chest" + i + ".chest_y"),
							this.plugin.getConfig().getInt("gfactions.dungeons." + dungeonName + ".chest" + i + ".chest_z")
						);
				chests.add(chest);
			}
			
			Dungeon dungeon = new Dungeon(
						this.plugin.getConfig().getString("gfactions.dungeons." + dungeonName + ".world_name"),
						this.plugin.getConfig().getInt("gfactions.dungeons." + dungeonName + ".spawn_x"),
						this.plugin.getConfig().getInt("gfactions.dungeons." + dungeonName + ".spawn_y"),
						this.plugin.getConfig().getInt("gfactions.dungeons." + dungeonName + ".spawn_z"),
						this.plugin.getConfig().getInt("gfactions.dungeons." + dungeonName + ".number_of_chests"),
						chests,
						this.plugin.getConfig().getInt("gfactions.dungeons." + dungeonName + ".zombie_percentage"),
						this.plugin.getConfig().getInt("gfactions.dungeons." + dungeonName + ".skeleton_percentage"),
						this.plugin.getConfig().getInt("gfactions.dungeons." + dungeonName + ".creeper_percentage"),
						this.plugin.getConfig().getInt("gfactions.dungeons." + dungeonName + ".spider_percentage"),
						this.plugin.getConfig().getInt("gfactions.dungeons." + dungeonName + ".blaze_percentage"),
						this.plugin.getConfig().getInt("gfactions.dungeons." + dungeonName + ".ghast_percentage"),
						this.plugin.getConfig().getInt("gfactions.dungeons." + dungeonName + ".giant_percentage"),
						this.plugin.getConfig().getInt("gfactions.dungeons." + dungeonName + ".max_mob_level_y"),
						this.plugin.getConfig().getInt("gfactions.dungeons." + dungeonName + ".max_player_level_y")
					);
			this.dungeons.add(dungeon);
		}
		
		// Load the portals
		ArrayList<String> portals = (ArrayList<String>) this.plugin.getConfig().getList("gfactions.dungeon_portals");
		for (String portal : portals) {
			// Break it off into two coords
			String [] rawLocations = portal.split("x");
			
			// Break off part 1
			String [] location1Raw = rawLocations[0].split(" ");
			int [] location1 = new int [3];
			int i = 0;
			for (String coord : location1Raw) {
				try {
					location1[i++] = Integer.parseInt(coord);
				} catch (NumberFormatException e) {
					e.printStackTrace();
					return;
				}
			}
			
			// Break off part 2
			String [] location2Raw = rawLocations[1].split(" ");
			int [] location2 = new int [3];
			i = 0;
			for (String coord : location2Raw) {
				try {
					location2[i++] = Integer.parseInt(coord);
				} catch (NumberFormatException e) {
					e.printStackTrace();
					return;
				}
			}
			
			// Store in the portal listings
			this.dungeonPortal1.add(new Location(this.plugin.getServer().getWorld("world"), location1[0], location1[1], location1[2]));
			this.dungeonPortal2.add(new Location(this.plugin.getServer().getWorld("world"), location2[0], location2[1], location2[2]));
			
			this.plugin.getLogger().info(new Location(this.plugin.getServer().getWorld("world"), location1[0], location1[1], location1[2]).toString());
			this.plugin.getLogger().info(new Location(this.plugin.getServer().getWorld("world"), location2[0], location2[1], location2[2]).toString());
		}
	}

    public void generateRandomPortal() {
        int rand = this.plugin.generateRandomInt(0, this.dungeonPortal1.size() - 1);
        this.portalToLoad = rand;

        // Randomly activate a portal
        this.location1 = this.dungeonPortal1.get(this.portalToLoad);
        this.location2 = this.dungeonPortal2.get(this.portalToLoad);

		Gberry.broadcastMessage(ChatColor.YELLOW + "The following Dungeon Portal will be open in 10 minutes: " +
                                                         this.location1.getBlockX() + ", " + this.location1.getBlockY() + ", " + this.location1.getBlockZ());
        this.portalEvent = true;
    }
	
	public Dungeon createNewDungeon() {
        // We did this without our other function calling first, because of bad reboot
        if (this.location1 == null) {
            int rand = this.plugin.generateRandomInt(0, this.dungeonPortal1.size() - 1);
            this.portalToLoad = rand;

            // Randomly activate a portal
            this.location1 = this.dungeonPortal1.get(this.portalToLoad);
            this.location2 = this.dungeonPortal2.get(this.portalToLoad);
        }

        this.portalEvent = false;

		// Pick one at random
		int dungeonNumber = this.plugin.generateRandomInt(0, this.dungeons.size() - 1);
		Dungeon dungeon = this.dungeons.get(dungeonNumber);
		this.spawnedDungeonWorldName = dungeon.getDungeonName();
		this.currentDungeon = dungeon;
		
		// Load the actual world
		WorldCreator wc = new WorldCreator(dungeon.getDungeonName());
		wc.generator(new CleanroomChunkGenerator("."));
		world = this.plugin.getServer().createWorld(wc);
		world.setMonsterSpawnLimit(500);
		this.plugin.getServer().getWorlds().add(world);
		
		// Protect immediately
		this.plugin.getgGuardPlugin().getProtectedRegionFromConfig(dungeon.getDungeonName());
		
		// Transfer over this stuff
		this.zombieSpawnPercentage = dungeon.getZombiePercentage();
		this.skeletonSpawnPercentage = dungeon.getSkeletonPercentage();
		this.creeperSpawnPercentage = dungeon.getCreeperPercentage();
		this.spiderSpawnPercentage = dungeon.getSpiderPercentage();
		this.blazeSpawnPercentage = dungeon.getBlazePercentage();
		this.ghastSpawnPercentage = dungeon.getGhastPercentage();
		this.giantSpawnPercentage = dungeon.getGiantPercentage();
		this.spawnX = dungeon.getSpawnX();
		this.spawnY = dungeon.getSpawnY();
		this.spawnZ = dungeon.getSpawnZ();
		this.chests = dungeon.getChests();
		
		// Fix the spawn percentage numbers
		this.updateSpawnPercentages();
		
		// Spawn location
		this.plugin.getLogger().info(this.spawnedDungeonWorldName);
		this.plugin.getLogger().info(this.plugin.getServer().getWorld(this.spawnedDungeonWorldName).getName());
		this.spawnLocation = this.plugin.getServer().getWorld(this.spawnedDungeonWorldName).getBlockAt(this.spawnX, this.spawnY, this.spawnZ).getLocation();
		
		// Allow people to teleport in
		this.allowEntry = true;
		
		// Announce the Dungeon
		Gberry.broadcastMessage(ChatColor.GREEN + "New dungeon opened.  The following portal has been activated: " +
										this.location1.getBlockX() + ", " + this.location1.getBlockY() + ", " + this.location1.getBlockZ());
		
		// Keep it night time
		this.nightTask = this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, new Runnable() {
			
			@Override
			public void run() {
				world.setTime(18000);
			}
			
		}, 10, 100);
		
		// Damage the bitches
		this.damageTask = this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, new DungeonDamageTask(this.plugin), 0, 20);
		
		// Fill the loot chests
		for (DungeonChest dungeonChest : dungeon.getChests()) {
			Block block = world.getBlockAt(dungeonChest.getX(), dungeonChest.getY(), dungeonChest.getZ());
			this.plugin.getLogger().info(block.getLocation().toString());
			if (block.getType() == Material.CHEST) {
				Chest chest = (Chest) block.getState();
				chest.getBlockInventory().clear();
				
				// Pick 4 random items
				for (int i = 0; i < 4; i++) {
					int r = this.plugin.generateRandomInt(1, 100);
					ArrayList<ItemStack> items = null;
					if (1 <= r && r < 10) {
						items = this.plugin.getItemGenerator().generateRandomSuperRareItem(1);
					} else if (10 <= r && r < 40) {
						items = this.plugin.getItemGenerator().generateRandomRareItem(1);
					} else if (40 <= r && r < 90) {
						items = this.plugin.getItemGenerator().generateRandomCommonItem(1);
					} else if (90 <= r && r <= 100) {
						items = this.plugin.getItemGenerator().generateRandomTrashItem(1);
					}
					
					chest.getBlockInventory().addItem(items.get(0));
				}
			} else {
				this.plugin.getLogger().info("Invalid chest coords.");
			}
		}
		
		// Once we create the portal, save the info to the config file for crash recovery purposes
		// TODO:
		
		// Later close the portal
		this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new Runnable() {
			
			@Override
			public void run() {
				plugin.getDungeonManager().disablePortalEntrance();
			}
			
		}, 60 * 20 * 15);
		
		// After that actually unload everything
		this.plugin.getServer().getScheduler().runTaskLater(this.plugin, new Runnable() {
			
			@Override
			public void run() {
				plugin.getDungeonManager().closeDungeon();
			}
			
		}, 60 * 20 * 20);

        // Call TabList event
        EventStateChangeEvent event = new EventStateChangeEvent("Dungeon", true);
        this.plugin.getServer().getPluginManager().callEvent(event);
		
		return dungeon;
	}
	
	public void disablePortalEntrance() {
		// Ok this is pretty simple...set a flag
		this.allowEntry = false;
		Gberry.broadcastMessage(ChatColor.YELLOW + "Dungeon portal is now closed. People inside have 5 minutes to get out or will be eliminated.");
	}
	
	public void closeDungeon() {
		List<Player> players = world.getPlayers();
		
		for (Player player : players) {
			player.setHealth(0); // kill them
			player.sendMessage(ChatColor.RED + "You did not escape the dungeon before the portal closed.");
		}
		
		this.plugin.getServer().unloadWorld(this.getSpawnedDungeonWorldName(), false);
		this.setCurrentDungeon(null);
		
		// Cancel the events
		this.damageTask.cancel();
		this.nightTask.cancel();

        // Call TabList event
        EventStateChangeEvent event = new EventStateChangeEvent("Dungeon", false);
        this.plugin.getServer().getPluginManager().callEvent(event);
	}
	
	public void updateSpawnPercentages() {
		this.zombieRawNumber = this.zombieSpawnPercentage;
		this.skeletonRawNumber = this.zombieRawNumber + this.skeletonSpawnPercentage;
		this.creeperRawNumber = this.skeletonRawNumber + this.creeperSpawnPercentage;
		this.spiderRawNumber = this.creeperRawNumber + this.spiderSpawnPercentage;
		this.blazeRawNumber = this.spiderRawNumber + this.blazeSpawnPercentage;
		this.ghastRawNumber = this.blazeRawNumber + this.ghastSpawnPercentage;
		this.giantRawNumber = this.ghastRawNumber + this.giantSpawnPercentage;
	}

	public GFactions getPlugin() {
		return plugin;
	}

	public void setPlugin(GFactions plugin) {
		this.plugin = plugin;
	}

	public int getZombieSpawnPercentage() {
		return zombieSpawnPercentage;
	}

	public void setZombieSpawnPercentage(int zombieSpawnPercentage) {
		this.zombieSpawnPercentage = zombieSpawnPercentage;
	}

	public int getSkeletonSpawnPercentage() {
		return skeletonSpawnPercentage;
	}

	public void setSkeletonSpawnPercentage(int skeletonSpawnPercentage) {
		this.skeletonSpawnPercentage = skeletonSpawnPercentage;
	}

	public int getCreeperSpawnPercentage() {
		return creeperSpawnPercentage;
	}

	public void setCreeperSpawnPercentage(int creeperSpawnPercentage) {
		this.creeperSpawnPercentage = creeperSpawnPercentage;
	}

	public int getSpiderSpawnPercentage() {
		return spiderSpawnPercentage;
	}

	public void setSpiderSpawnPercentage(int spiderSpawnPercentage) {
		this.spiderSpawnPercentage = spiderSpawnPercentage;
	}

	public int getBlazeSpawnPercentage() {
		return blazeSpawnPercentage;
	}

	public void setBlazeSpawnPercentage(int blazeSpawnPercentage) {
		this.blazeSpawnPercentage = blazeSpawnPercentage;
	}

	public int getGhastSpawnPercentage() {
		return ghastSpawnPercentage;
	}

	public void setGhastSpawnPercentage(int ghastSpawnPercentage) {
		this.ghastSpawnPercentage = ghastSpawnPercentage;
	}

	public int getGiantSpawnPercentage() {
		return giantSpawnPercentage;
	}

	public void setGiantSpawnPercentage(int giantSpawnPercentage) {
		this.giantSpawnPercentage = giantSpawnPercentage;
	}

	public int getZombieRawNumber() {
		return zombieRawNumber;
	}

	public void setZombieRawNumber(int zombieRawNumber) {
		this.zombieRawNumber = zombieRawNumber;
	}

	public int getSkeletonRawNumber() {
		return skeletonRawNumber;
	}

	public void setSkeletonRawNumber(int skeletonRawNumber) {
		this.skeletonRawNumber = skeletonRawNumber;
	}

	public int getCreeperRawNumber() {
		return creeperRawNumber;
	}

	public void setCreeperRawNumber(int creeperRawNumber) {
		this.creeperRawNumber = creeperRawNumber;
	}

	public int getSpiderRawNumber() {
		return spiderRawNumber;
	}

	public void setSpiderRawNumber(int spiderRawNumber) {
		this.spiderRawNumber = spiderRawNumber;
	}

	public int getBlazeRawNumber() {
		return blazeRawNumber;
	}

	public void setBlazeRawNumber(int blazeRawNumber) {
		this.blazeRawNumber = blazeRawNumber;
	}

	public int getGhastRawNumber() {
		return ghastRawNumber;
	}

	public void setGhastRawNumber(int ghastRawNumber) {
		this.ghastRawNumber = ghastRawNumber;
	}

	public int getGiantRawNumber() {
		return giantRawNumber;
	}

	public void setGiantRawNumber(int giantRawNumber) {
		this.giantRawNumber = giantRawNumber;
	}

	public ArrayList<String> getDungeonsToChooseFrom() {
		return dungeonsToChooseFrom;
	}

	public void setDungeonsToChooseFrom(ArrayList<String> dungeonsToChooseFrom) {
		this.dungeonsToChooseFrom = dungeonsToChooseFrom;
	}

	public ArrayList<Dungeon> getDungeons() {
		return dungeons;
	}

	public void setDungeons(ArrayList<Dungeon> dungeons) {
		this.dungeons = dungeons;
	}

	public ArrayList<DungeonChest> getChests() {
		return chests;
	}

	public void setChests(ArrayList<DungeonChest> chests) {
		this.chests = chests;
	}

	public int getSpawnX() {
		return spawnX;
	}

	public void setSpawnX(int spawnX) {
		this.spawnX = spawnX;
	}

	public int getSpawnY() {
		return spawnY;
	}

	public void setSpawnY(int spawnY) {
		this.spawnY = spawnY;
	}

	public int getSpawnZ() {
		return spawnZ;
	}

	public void setSpawnZ(int spawnZ) {
		this.spawnZ = spawnZ;
	}

	public String getSpawnedDungeonWorldName() {
		return spawnedDungeonWorldName;
	}

	public void setSpawnedDungeonWorldName(String spawnedDungeonWorldName) {
		this.spawnedDungeonWorldName = spawnedDungeonWorldName;
	}

	public Location getLocation1() {
		return location1;
	}

	public void setLocation1(Location location1) {
		this.location1 = location1;
	}

	public Location getLocation2() {
		return location2;
	}

	public void setLocation2(Location location2) {
		this.location2 = location2;
	}

	public Location getSpawnLocation() {
		return spawnLocation;
	}

	public void setSpawnLocation(Location spawnLocation) {
		this.spawnLocation = spawnLocation;
	}

	public BukkitTask getNightTask() {
		return nightTask;
	}

	public void setNightTask(BukkitTask nightTask) {
		this.nightTask = nightTask;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public Dungeon getCurrentDungeon() {
		return currentDungeon;
	}

	public void setCurrentDungeon(Dungeon currentDungeon) {
		this.currentDungeon = currentDungeon;
	}

	public BukkitTask getDamageTask() {
		return damageTask;
	}

	public void setDamageTask(BukkitTask damageTask) {
		this.damageTask = damageTask;
	}

	public boolean isAllowEntry() {
		return allowEntry;
	}

	public void setAllowEntry(boolean allowEntry) {
		this.allowEntry = allowEntry;
	}

	public ArrayList<Location> getDungeonPortal1() {
		return dungeonPortal1;
	}

	public void setDungeonPortal1(ArrayList<Location> dungeonPortal1) {
		this.dungeonPortal1 = dungeonPortal1;
	}

	public ArrayList<Location> getDungeonPortal2() {
		return dungeonPortal2;
	}

	public void setDungeonPortal2(ArrayList<Location> dungeonPortal2) {
		this.dungeonPortal2 = dungeonPortal2;
	}

    public int getPortalToLoad() {
        return portalToLoad;
    }

    public void setPortalToLoad(int portalToLoad) {
        this.portalToLoad = portalToLoad;
    }

    public boolean isPortalEvent() {
        return portalEvent;
    }

    public void setPortalEvent(boolean portalEvent) {
        this.portalEvent = portalEvent;
    }
}
