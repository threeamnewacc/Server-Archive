package net.badlion.gfactions.events;

import com.sk89q.worldedit.EmptyClipboardException;
import com.sk89q.worldedit.FilenameException;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.data.DataException;
import net.badlion.gfactions.GFactions;
import net.badlion.gfactions.bukkitevents.EventStateChangeEvent;
import net.badlion.gberry.Gberry;
import net.badlion.gguard.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Tower {

	private GFactions plugin;
	private Location chestLocation;
	private int towerSchematic;
	private File towerFile;
    private File level1File;
    private File level2File;
    private File roofFile;
	private File saveFile;
	private Location towerLocation;
	private Location bottomLeftCornerLocation;
	private Location topRightCornerLocation;
	private ProtectedRegion region;
	private Chest chest;
    private boolean claimed = false;
	
	public Tower(GFactions plugin, int towerSchematic, Location location) {
		this.plugin = plugin;
		this.towerSchematic = towerSchematic;
		
		// Spawn and load the tower
		Gberry.broadcastMessage(ChatColor.GOLD + "New tower spawned at " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ". Be the first to the top and claim the loot!");
		this.towerFile = new File(this.plugin.getDataFolder(), "TowerBase");
        this.level1File = new File(this.plugin.getDataFolder(), "TowerSection" + this.plugin.generateRandomInt(1, 8));
        this.level2File = new File(this.plugin.getDataFolder(), "TowerSection" + this.plugin.generateRandomInt(1, 8));
        this.roofFile = new File(this.plugin.getDataFolder(), "TowerRoof");
		this.saveFile = new File(this.plugin.getDataFolder(), "towerBackup");
		
		// Save the original terrain quickly
		this.bottomLeftCornerLocation = new Location(location.getWorld(), location.getBlockX(), 0, location.getBlockZ());
		this.topRightCornerLocation = new Location(location.getWorld(), location.getBlockX() + 15, 255, location.getBlockZ() + 15);
		
		try {
			this.plugin.saveTerrain(this.saveFile, this.bottomLeftCornerLocation, this.topRightCornerLocation);
		} catch (FilenameException e) {
			// thrown by WorldEdit - it doesn't like the file name/location etc.
			e.printStackTrace();
		} catch (DataException e) {
			// thrown by WorldEdit - problem with the data
			e.printStackTrace();
		} catch (IOException e) {
			// problem with creating/writing to the file
			e.printStackTrace();
		}
		
		// TODO: right here store the location that we made this backup from so we can restore on server crash/recover
		this.plugin.getConfig().set("gfactions.tower.reload_on_crash", true);
		this.plugin.getConfig().set("gfactions.tower.x", this.bottomLeftCornerLocation.getBlockX());
		this.plugin.getConfig().set("gfactions.tower.y", this.bottomLeftCornerLocation.getBlockY());
		this.plugin.getConfig().set("gfactions.tower.z", this.bottomLeftCornerLocation.getBlockZ());
		this.plugin.saveConfig();
		
		try {
			this.plugin.loadSchematic(this.towerFile, location);
            this.plugin.loadSchematic(this.level1File, new Location(location.getWorld(), location.getX(), location.getY() + 6, location.getZ()));
            this.plugin.loadSchematic(this.level2File, new Location(location.getWorld(), location.getX(), location.getY() + 13, location.getZ()));
            this.plugin.loadSchematic(this.roofFile, new Location(location.getWorld(), location.getX(), location.getY() + 21, location.getZ()));
		} catch (FilenameException e) {
			e.printStackTrace();
		} catch (DataException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (MaxChangedBlocksException e) {
			e.printStackTrace();
		} catch (EmptyClipboardException e) {
			e.printStackTrace();
		}

        // TP people too close away since we want to do some worldedits
        Location safeBottomLeft = new Location(this.bottomLeftCornerLocation.getWorld(), this.bottomLeftCornerLocation.getX() - 1,
                this.bottomLeftCornerLocation.getY(), this.bottomLeftCornerLocation.getZ() - 1);
        Location safeTopRight = new Location(this.topRightCornerLocation.getWorld(), this.topRightCornerLocation.getX() + 1,
                this.topRightCornerLocation.getY(), this.topRightCornerLocation.getZ() + 1);
        World world = safeBottomLeft.getWorld();

        // Iterate through and TP anyone too close
        for (Player player : this.plugin.getServer().getOnlinePlayers()) {
            if (Gberry.isLocationInBetween(safeBottomLeft, safeTopRight, player.getLocation())) {
                int x = this.plugin.generateRandomInt(0, 1) == 0 ? this.plugin.generateRandomInt(20, 200) : this.plugin.generateRandomInt(-200, -20);
                int z = this.plugin.generateRandomInt(0, 1) == 0 ? this.plugin.generateRandomInt(20, 200) : this.plugin.generateRandomInt(-200, -20);
                player.teleport(new Location(world, x + player.getLocation().getBlockX(), world.getHighestBlockYAt(x + player.getLocation().getBlockX(),
                        z + player.getLocation().getBlockZ()) + 10, z + player.getLocation().getBlockZ()));
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 10, 128));
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 20, 128));
                player.sendMessage(ChatColor.BLUE + "You have been teleported away from the tower because you were too close to it when it spawned.");
            }
        }

        // Fix the 2 block border around the tower
        Material toReplace = location.getWorld().getBlockAt(new Location(location.getWorld(), location.getBlockX(), location.getBlockY() - 1, location.getBlockZ())).getType();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                if (x > 1 && x < 14 && z > 1 && z < 14) {
                    // do nothing
                } else {
                    Block block = location.getWorld().getBlockAt(new Location(location.getWorld(), location.getBlockX() + x, location.getBlockY() - 1, location.getBlockZ() + z));
                    block.setType(toReplace);
                }
            }
        }

        // Fix everything underneath
        if (toReplace == Material.GRASS) {
            toReplace = Material.DIRT;
        } else if (toReplace == Material.AIR) {
			toReplace = Material.DIRT;
		}

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 1; y < location.getBlockY(); y++) {
                    if (y == 1) {
                        Block block = location.getWorld().getBlockAt(new Location(location.getWorld(), location.getBlockX() + x, y, location.getBlockZ() + z));
                        block.setType(Material.BEDROCK);
                    } else {
                        Block block = location.getWorld().getBlockAt(new Location(location.getWorld(), location.getBlockX() + x, y, location.getBlockZ() + z));
                        block.setType(toReplace);
                    }
                }
            }
        }
		
		this.towerLocation = location;
		
		// Ok tower is loaded, add a protected region
		region = new ProtectedRegion("tower", this.bottomLeftCornerLocation, this.topRightCornerLocation);
		region.setAllowBrokenBlocks(false);
		region.setAllowCreeperBlockDamage(false);
		region.setAllowFire(false);
		region.setAllowPistonUsage(false);
		region.setAllowPlacedBlocks(false);
		region.setAllowTNTBlockDamage(false);
		region.setChangeMobDamageToPlayer(true);
		region.setAllowBlockMovement(false);
		region.setAllowedBucketPlacements(false);
		region.setDamageMultiplier(2.0);
        region.setAllowEnderPearls(false);
        region.setAllowEndermanMoveBlocks(false);
		region.setAllowPlantGrowth(false);
		region.setAllowPlantSpread(false);
		region.setAllowHangingItems(false);
		region.setAllowItemInteraction(false);
		region.setAllowBlockChangesByEntities(false);
		region.setAllowLeafDecay(false);
		this.plugin.getgGuardPlugin().addProtectedRegion(region);
	}
	
	public void despawn() {
		// This function will tp anyone in the tower out within 200 blocks and then destroy the tower itself
		Location safeBottomLeft = new Location(this.bottomLeftCornerLocation.getWorld(), this.bottomLeftCornerLocation.getX() - 1,
				this.bottomLeftCornerLocation.getY(), this.bottomLeftCornerLocation.getZ() - 1);
		Location safeTopRight = new Location(this.topRightCornerLocation.getWorld(), this.topRightCornerLocation.getX() + 1,
				this.topRightCornerLocation.getY(), this.topRightCornerLocation.getZ() + 1);
		World world = safeBottomLeft.getWorld();

		boolean flag = false;

		// Iterate through and TP anyone too close
		for (Player player : this.plugin.getServer().getOnlinePlayers()) {
			if (Gberry.isLocationInBetween(safeBottomLeft, safeTopRight, player.getLocation())) {
				// Remove mobs
				if (!flag) {
					flag = true;
					List<Entity> entities = player.getNearbyEntities(16, 50, 16);
					for (Entity entity : entities) {
						// Make sure its a mob and not a player
						if (entity instanceof Creature && !(entity instanceof Player)) {
							entity.remove();
						}
					}
				}

				int x = this.plugin.generateRandomInt(0, 1) == 0 ? this.plugin.generateRandomInt(20, 200) : this.plugin.generateRandomInt(-200, -20);
				int z = this.plugin.generateRandomInt(0, 1) == 0 ? this.plugin.generateRandomInt(20, 200) : this.plugin.generateRandomInt(-200, -20);
				player.teleport(new Location(world, x + player.getLocation().getBlockX(), world.getHighestBlockYAt(x + player.getLocation().getBlockX(), 
						z + player.getLocation().getBlockZ()) + 10, z + player.getLocation().getBlockZ()));
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 10, 128));
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 20, 128));
				player.sendMessage(ChatColor.BLUE + "You have been teleported away from the tower because either it got captured or despawned.");
			}
		}

		// Unprotect the region
		this.plugin.getgGuardPlugin().deleteProtectedRegion(region);
		
		try {
			this.plugin.loadSchematic(this.saveFile, new Location(this.towerLocation.getWorld(), this.towerLocation.getBlockX(), 0, this.towerLocation.getBlockZ()));
		} catch (FilenameException e) {
			e.printStackTrace();
		} catch (DataException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (MaxChangedBlocksException e) {
			e.printStackTrace();
		} catch (EmptyClipboardException e) {
			e.printStackTrace();
		}
		
		// Set the flag to false, we have restored the world
		this.plugin.getConfig().set("gfactions.tower.reload_on_crash", false);
		this.plugin.saveConfig();

        // Don't leak memory...
        this.plugin.setTower(null);

		// Call out tab list event
		EventStateChangeEvent event = new EventStateChangeEvent("Tower", false);
		this.plugin.getServer().getPluginManager().callEvent(event);
	}

	public Location getChestLocation() {
		return chestLocation;
	}

	public void setChestLocation(Location chestLocation) {
		this.chestLocation = chestLocation;
	}

	public GFactions getPlugin() {
		return plugin;
	}

	public void setPlugin(GFactions plugin) {
		this.plugin = plugin;
	}

	public int getTowerSchematic() {
		return towerSchematic;
	}

	public void setTowerSchematic(int towerSchematic) {
		this.towerSchematic = towerSchematic;
	}

	public File getTowerFile() {
		return towerFile;
	}

	public void setTowerFile(File towerFile) {
		this.towerFile = towerFile;
	}

	public File getSaveFile() {
		return saveFile;
	}

	public void setSaveFile(File saveFile) {
		this.saveFile = saveFile;
	}

	public Location getTowerLocation() {
		return towerLocation;
	}

	public void setTowerLocation(Location towerLocation) {
		this.towerLocation = towerLocation;
	}

	public Location getBottomLeftCornerLocation() {
		return bottomLeftCornerLocation;
	}

	public void setBottomLeftCornerLocation(Location bottomLeftCornerLocation) {
		this.bottomLeftCornerLocation = bottomLeftCornerLocation;
	}

	public Location getTopRightCornerLocation() {
		return topRightCornerLocation;
	}

	public void setTopRightCornerLocation(Location topRightCornerLocation) {
		this.topRightCornerLocation = topRightCornerLocation;
	}

	public ProtectedRegion getRegion() {
		return region;
	}

	public void setRegion(ProtectedRegion region) {
		this.region = region;
	}

	public Chest getChest() {
		return chest;
	}

	public void setChest(Chest chest) {
		this.chest = chest;
	}

	public boolean isClaimed() {
		return claimed;
	}

	public void setClaimed(boolean claimed) {
		this.claimed = claimed;
	}

}
