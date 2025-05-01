package net.badlion.uhcmeetup;

import net.badlion.gberry.utils.Pair;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGWorld;
import net.badlion.worldrotator.GWorld;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.NumberConversions;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;

public class UHCMeetupWorld extends MPGWorld {

	public static final int WORLD_RADIUS = 100;
	public static final String WORLD_NAME = "uhcmeetupworld";

	private static final int SKY_BASING_LIMIT = 5;

	private static final Material WALL_MATERIAL = Material.GLASS;

	private Set<Material> blackListedMaterials = new HashSet<>();

	private Map<Pair, Integer> blockYLimits = new HashMap<>();

    public UHCMeetupWorld(GWorld gWorld) {
        super(gWorld);
    }

    @Override
    public void load() {
	    super.load();

	    // Hardcode spectator location to 0,0
	    int y = this.gWorld.getBukkitWorld().getHighestBlockYAt(0, 0) + 15;
	    this.spectatorLocation = new Location(this.gWorld.getBukkitWorld(), 0.5, y, 0.5, -359, 0);

	    // Clean the map
	    this.cleanMap();

	    // Cache block y levels for sky basing checks
	    this.scanArena();

	    // Generate spawn locations
	    this.generateSpawnLocations();
    }

	private void scanArena() {
		// Add blacklisted locations
		this.blackListedMaterials.add(Material.GLASS);
		this.blackListedMaterials.add(Material.AIR);
		this.blackListedMaterials.add(Material.LOG);
		this.blackListedMaterials.add(Material.LOG_2);
		this.blackListedMaterials.add(Material.YELLOW_FLOWER);
		this.blackListedMaterials.add(Material.RED_ROSE);
		this.blackListedMaterials.add(Material.BROWN_MUSHROOM);
		this.blackListedMaterials.add(Material.RED_MUSHROOM);
		this.blackListedMaterials.add(Material.DOUBLE_PLANT);
		this.blackListedMaterials.add(Material.LONG_GRASS);
		this.blackListedMaterials.add(Material.LEAVES);
		this.blackListedMaterials.add(Material.LEAVES_2);

		Location location = this.spectatorLocation;

		// Find the edges
		int safety = 0;
		Location xMinLoc = location.clone();
		while (safety < 300 && xMinLoc.getBlock().getType() != UHCMeetupWorld.WALL_MATERIAL) {
			++safety;
			xMinLoc.add(-1, 0, 0);
		}

		safety = 0;
		Location xMaxLoc = location.clone();
		while (safety < 300 && xMaxLoc.getBlock().getType() != UHCMeetupWorld.WALL_MATERIAL) {
			++safety;
			xMaxLoc.add(1, 0, 0);
		}

		safety = 0;
		Location zMinLoc = location.clone();
		while (safety < 300 && zMinLoc.getBlock().getType() != UHCMeetupWorld.WALL_MATERIAL) {
			++safety;
			zMinLoc.add(0, 0, -1);
		}

		safety = 0;
		Location zMaxLoc = location.clone();
		while (safety < 300 && zMaxLoc.getBlock().getType() != UHCMeetupWorld.WALL_MATERIAL) {
			++safety;
			zMaxLoc.add(0, 0, 1);
		}

		// Create our internal corners (corners inside the actual arena [not wool])
		int xMin = xMinLoc.getBlockX() + 1;
		int xMax = xMaxLoc.getBlockX() - 1;
		int zMin = zMinLoc.getBlockZ() + 1;
		int zMax = zMaxLoc.getBlockZ() - 1;

		for (int x = xMin; x <= xMax; x++) {
			for (int z = zMin; z <= zMax; z++) {
				Block block = xMinLoc.getWorld().getHighestBlockAt(x, z);
				Block under = block.getRelative(0, -1, 0);

				// While the under block isn't something we want
				while (this.blackListedMaterials.contains(under.getType()) && under.getY() > 0) {
					under = under.getRelative(0, -1, 0);
				}

				if (under.getY() == 0) {
					throw new RuntimeException("Invalid block found " + under.toString());
				}

				Pair pair = Pair.of(under.getX(), under.getZ());
				Integer max = under.getY() + UHCMeetupWorld.SKY_BASING_LIMIT;

				this.blockYLimits.put(pair, max);
			}
		}
	}

	private void generateSpawnLocations() {
		Random rand = new Random();

		int radius = UHCMeetupWorld.WORLD_RADIUS - 10;
		double minDistance = 20D; // Solves issues with small player counts
		World world = this.gWorld.getBukkitWorld();

		for (int i = 0; i < MPG.getInstance().getIntegerConfigOption(MPG.ConfigFlag.MAX_PLAYERS); i++) {
			boolean goodSpawnPointFound = false;
			Location scatterPoint = new Location(world, 0.0D, 0.0D, 0.0D);
			Location backupLocation = null;
			for (int k = 0; k < 500; k++) { // 500 tries
				double d1 = rand.nextDouble() * radius * 2.0D - radius;
				double d2 = rand.nextDouble() * radius * 2.0D - radius;
				d1 = Math.round(d1) + 0.5D;
				d2 = Math.round(d2) + 0.5D;
				scatterPoint.setX(d1);
				scatterPoint.setZ(d2);
				scatterPoint.setY(world.getHighestBlockYAt(scatterPoint) + 5);

				if (this.isLocationBlockValid(scatterPoint)) {
					// Set backup spawn location
					if (backupLocation == null) backupLocation = scatterPoint;

					if (this.isLocationValid(scatterPoint, this.getSpawnLocations(), minDistance)) {
						goodSpawnPointFound = true;
						break;
					}
				}
			}

			if (!goodSpawnPointFound) {
				scatterPoint = backupLocation;
				Bukkit.getLogger().log(Level.WARNING, "MaxAttemptsReachedException"); // Didn't feel like making an exception
			}

			this.spawnLocations.put(i, scatterPoint);
		}
	}

	private boolean isLocationValid(Location location, Collection<Location> locations, Double d) {
		for (Location loc : locations) {
			if (Math.sqrt(NumberConversions.square(loc.getX() - location.getX()) + NumberConversions.square(loc.getZ() - location.getZ())) < d) {
				return false;
			}
		}
		return true;
	}

	private boolean isLocationBlockValid(Location loc) {
		Material type = loc.getBlock().getRelative(0, -6, 0).getType();
		//System.out.println("Block Location: " + loc.getBlock().getRelative(0, -6, 0).getLocation());
		//System.out.println("Block Type: " + loc.getBlock().getRelative(0, -6, 0).getType());
		return !(type == Material.LAVA || type == Material.STATIONARY_LAVA || type == Material.WATER || type == Material.STATIONARY_WATER);
	}

	public int getMaxBlockYLevel(Location location) {
		Pair pair = Pair.of(location.getBlockX(), location.getBlockZ());

		Integer yLimit = this.blockYLimits.get(pair);

		if (yLimit == null) {
			throw new IllegalArgumentException("Missing block y limit for " + pair);
		}

		return yLimit;
	}

}
