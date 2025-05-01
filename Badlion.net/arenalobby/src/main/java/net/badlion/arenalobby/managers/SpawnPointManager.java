package net.badlion.arenalobby.managers;

import net.badlion.arenacommon.rulesets.KitRuleSet;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class SpawnPointManager {

	private static Queue<Location> locationQueue;
	public static Map<String, Location> ladderLocationMap = new HashMap<>();

	public static void initialize() {
		SpawnPointManager.locationQueue = new LinkedList<>();

		// Lion (Top of spawn)
		SpawnPointManager.locationQueue.add(new Location(Bukkit.getWorld("world"), -21, 130, 1, -90, 0));
		SpawnPointManager.ladderLocationMap.put("Global", new Location(Bukkit.getWorld("world"), -21, 130, 1, -90, 0));

		// God Apple
		SpawnPointManager.locationQueue.add(new Location(Bukkit.getWorld("world"), 105, 61, 18, 90, 0));
		SpawnPointManager.ladderLocationMap.put(KitRuleSet.GAPPLE_LADDER_NAME, new Location(Bukkit.getWorld("world"), 105, 61, 18, 90, 0));

		// UHC
		SpawnPointManager.locationQueue.add(new Location(Bukkit.getWorld("world"), 91, 30, -44, 180, 0));
		SpawnPointManager.ladderLocationMap.put(KitRuleSet.UHC_LADDER_NAME, new Location(Bukkit.getWorld("world"), 91, 30, -44, 180, 0));

		// Diamond
		SpawnPointManager.locationQueue.add(new Location(Bukkit.getWorld("world"), 70, 36, -22, 45, 0));
		SpawnPointManager.ladderLocationMap.put(KitRuleSet.DIAMOND_LADDER_NAME, new Location(Bukkit.getWorld("world"), 70, 36, -22, 45, 0));

		// Iron
		SpawnPointManager.locationQueue.add(new Location(Bukkit.getWorld("world"), 57, 37, -35, 0, 0));
		SpawnPointManager.ladderLocationMap.put(KitRuleSet.IRON_LADDER_NAME, new Location(Bukkit.getWorld("world"), 57, 37, -35, 0, 0));

		// Vanilla
		SpawnPointManager.locationQueue.add(new Location(Bukkit.getWorld("world"), 101, 26, 77, 135, 0));
		SpawnPointManager.ladderLocationMap.put(KitRuleSet.VANILLA_LADDER_NAME, new Location(Bukkit.getWorld("world"), 101, 26, 77, 135, 0));

		// Debuff
		SpawnPointManager.locationQueue.add(new Location(Bukkit.getWorld("world"), 82, 26, 105, 154, 0));
		SpawnPointManager.ladderLocationMap.put(KitRuleSet.KOHI_LADDER_NAME, new Location(Bukkit.getWorld("world"), 82, 26, 105, 154, 0));

		// NoDebuff
		SpawnPointManager.locationQueue.add(new Location(Bukkit.getWorld("world"), 55, 27, 100, 143, 0));
		SpawnPointManager.ladderLocationMap.put(KitRuleSet.NODEBUFF_LADDER_NAME, new Location(Bukkit.getWorld("world"), 55, 27, 100, 143, 0));

		// Archer
		SpawnPointManager.locationQueue.add(new Location(Bukkit.getWorld("world"), -35, 91, 26, -135, 0));
		SpawnPointManager.ladderLocationMap.put(KitRuleSet.ARCHER_LADDER_NAME, new Location(Bukkit.getWorld("world"), -35, 91, 26, -135, 0));

		// Soup
		SpawnPointManager.locationQueue.add(new Location(Bukkit.getWorld("world"), -40, 91, -8, -60, 0));
		SpawnPointManager.ladderLocationMap.put(KitRuleSet.IRON_SOUP_LADDER_NAME, new Location(Bukkit.getWorld("world"), -40, 91, -8, -60, 0));

		// Build UHC
		SpawnPointManager.locationQueue.add(new Location(Bukkit.getWorld("world"), -51, 31, -19, -64, 0));
		SpawnPointManager.ladderLocationMap.put(KitRuleSet.BUILD_UHC_LADDER_NAME, new Location(Bukkit.getWorld("world"), -51, 31, -19, -64, 0));

		// SG
		SpawnPointManager.locationQueue.add(new Location(Bukkit.getWorld("world"), 2, 25, 64, 45, 0));
		SpawnPointManager.ladderLocationMap.put(KitRuleSet.SG_LADDER_NAME, new Location(Bukkit.getWorld("world"), 2, 25, 64, 45, 0));

		// Horse
		SpawnPointManager.locationQueue.add(new Location(Bukkit.getWorld("world"), 52, 66, 66, 135, 0));
		SpawnPointManager.ladderLocationMap.put(KitRuleSet.HORSE_LADDER_NAME, new Location(Bukkit.getWorld("world"), 52, 66, 66, 135, 0));
	}

	public static Location getNextSpawnPoint() {
		// Get first location and add it to the end of the queue
		Location location = SpawnPointManager.locationQueue.remove();
		SpawnPointManager.locationQueue.add(location);
		return location;
	}

}
