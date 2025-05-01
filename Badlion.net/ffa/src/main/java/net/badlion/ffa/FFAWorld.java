package net.badlion.ffa;

import net.badlion.ffa.gamemodes.SGGamemode;
import net.badlion.gguard.GGuard;
import net.badlion.gguard.PolygonRegion;
import net.badlion.mpg.MPGWorld;
import net.badlion.worldrotator.GWorld;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FFAWorld extends MPGWorld {

	private int spawnPlatformYLimit;

    public FFAWorld(GWorld gWorld) {
        super(gWorld);
    }

    @Override
    public void load() {
	    super.load();

	    this.spawnPlatformYLimit = FFA.getInstance().getConfig().getInt("spawn_platform_y_limit");

	    // Clean the map
	    this.cleanMap();

	    // Is this an SG FFA?
	    if (FFA.FFA_GAMEMODE instanceof SGGamemode) {
		    // Load Tier 1 and Tier 2 chests
		    this.loadChests();
	    }
    }

	private void loadChests() {
		World world = this.getSpawnLocation().getWorld();

		List<Location> tier1Locations = new ArrayList<>();
		List<Location> tier2Locations = new ArrayList<>();

		// Load 76x76 chunks in the world
		for (int i = -38; i < 39; i++) {
			for (int j = -38; j < 39; j++) {
				world.loadChunk(i, j);
			}
		}

		// Scan for all chests and enderchests
		for (Chunk chunk : world.getLoadedChunks()) {
			BlockState[] tileEntities = chunk.getTileEntities();
			for (BlockState blockState : tileEntities) {
				if (blockState.getType() == Material.CHEST) {
					// Add to Tier 1 chest locations
					tier1Locations.add(blockState.getLocation());
				} else if (blockState.getType() == Material.ENDER_CHEST) {
					// Add to Tier 2 chest locations
					tier2Locations.add(blockState.getLocation());
				} else if (blockState.getType() == Material.TRAPPED_CHEST) {
					// Set to chest, we don't want trapped chests
					blockState.setType(Material.CHEST);

					// Add to Tier 1 chest locations
					tier1Locations.add(blockState.getLocation());
					System.out.println("Tier 1 TRAPPED CHEST FOUND AT " + blockState.getLocation());
				}
			}
		}

		// Get all polygon regions for our SG FFA regions
		Map<String, PolygonRegion> polygonRegions = new HashMap<>();
		for (String regionName : SGGamemode.REGIONS) {
			polygonRegions.put(regionName, GGuard.getInstance().getPolygonRegion(regionName));
		}

		SGGamemode sgGamemode = (SGGamemode) FFA.getInstance().getFFAGame().getGamemode();

		// Create inventories for Tier 1 chests
		for (Location location : tier1Locations) {
			Chest chest = (Chest) location.getBlock().getState();
			chest.getInventory().clear();

			// Create a new inventory
			sgGamemode.getTier1Chests().put(location, FFA.getInstance().getServer().
					createInventory(null, 27, ChatColor.YELLOW + ChatColor.BOLD.toString() + "Tier 1 Chest"));

			// Check to see if this chest is in one of the SG FFA regions
			/*UnsafeLocation unsafeLocation = new UnsafeLocation(location);
			for (Map.Entry<String, PolygonRegion> entry : polygonRegions.entrySet()) {
				if (entry.getValue().isLocationInProtectedRegion(unsafeLocation)) {
					List<Location> chestLocations = SGGamemode.REGION_CHESTS.get(entry.getKey());
					chestLocations.add(location);
				}
			}*/
		}

		// Create inventories for Tier 2 chests
		for (Location location : tier2Locations) {
			// Set ender chest to normal chest
			location.getBlock().setType(Material.CHEST);

			// Create a new inventory
			sgGamemode.getTier2Chests().put(location, FFA.getInstance().getServer().
					createInventory(null, 27, ChatColor.GOLD + ChatColor.BOLD.toString() + "Tier 2 Chest"));

			// Check to see if this chest is in one of the SG FFA regions
			/*UnsafeLocation unsafeLocation = new UnsafeLocation(location);
			for (Map.Entry<String, PolygonRegion> entry : polygonRegions.entrySet()) {
				if (entry.getValue().isLocationInProtectedRegion(unsafeLocation)) {
					List<Location> chestLocations = SGGamemode.REGION_CHESTS.get(entry.getKey());
					chestLocations.add(location);
				}
			}*/
		}

		// Clean the map
		this.cleanMap();

		// Fill all chests
		for (Map.Entry<Location, Inventory> entry : sgGamemode.getTier1Chests().entrySet()) {
			((SGGamemode) FFA.FFA_GAMEMODE).fillChest(entry.getValue(), 1);
		}

		for (Map.Entry<Location, Inventory> entry : sgGamemode.getTier2Chests().entrySet()) {
			((SGGamemode) FFA.FFA_GAMEMODE).fillChest(entry.getValue(), 2);
		}
	}

	public int getSpawnPlatformYLimit() {
		return this.spawnPlatformYLimit;
	}

	public Location getSpawnLocation() {
		return this.getSpawnLocation(0);
	}

}
