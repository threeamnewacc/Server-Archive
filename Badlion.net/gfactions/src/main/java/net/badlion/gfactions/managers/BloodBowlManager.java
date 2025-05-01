package net.badlion.gfactions.managers;

import com.massivecraft.factions.Faction;
import io.nv.bukkit.CleanroomGenerator.CleanroomChunkGenerator;
import net.badlion.gfactions.GFactions;
import net.badlion.gfactions.bukkitevents.EventStateChangeEvent;
import net.badlion.gfactions.tasks.bloodbowl.BloodBowlScoreTrackerTask;
import net.badlion.gberry.Gberry;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

public class BloodBowlManager {
	
	private GFactions plugin;
	private int xMin;
	private int xMax;
	private int zMin;
	private int zMax;
	private Map<String, Integer> mapOfScores;
	private Map<String, Integer> mapOfCaptures;
	private HashSet<String> participants;
	private BloodBowlScoreTrackerTask bloodBowlScoreTracker;
	private boolean running;
	private ArrayList<Location> warpLocations;
	private Map<Faction, Location> factionToSpawnLocationMap;
	private Queue<Location> locationQueue;
	private BukkitTask scoreTrackerTask;

	private Location capzoneLocation1;
	private Location capzoneLocation2;

	private Location lowerBoundEntry1;
	private Location upperBoundEntry1;
	private Location lowerBoundEntry2;
	private Location upperBoundEntry2;
	private Location lowerBoundEntry3;
	private Location upperBoundEntry3;
	private Location lowerBoundEntry4;
	private Location upperBoundEntry4;

	public BloodBowlManager(GFactions plugin) {
		this.plugin = plugin;
		this.mapOfScores = new HashMap<String, Integer>();
		this.participants = new HashSet<String>();
		this.running = false;
		this.warpLocations = new ArrayList<Location>();
		this.factionToSpawnLocationMap = new HashMap<Faction, Location>();
		this.locationQueue = new LinkedList<Location>();
		this.mapOfCaptures = new HashMap<String, Integer>();

		// Portals
		this.lowerBoundEntry1 = new Location(Bukkit.getWorld("world"), 337, 71, 498);
		this.upperBoundEntry1 = new Location(Bukkit.getWorld("world"), 353, 84, 510);
		this.lowerBoundEntry2 = new Location(Bukkit.getWorld("world"), 417, 68, -202);
		this.upperBoundEntry2 = new Location(Bukkit.getWorld("world"), 431, 79, -192);
		this.lowerBoundEntry3 = new Location(Bukkit.getWorld("world"), -61, 70, 424);
		this.upperBoundEntry3 = new Location(Bukkit.getWorld("world"), -48, 80, 432);
		this.lowerBoundEntry4 = new Location(Bukkit.getWorld("world"), -461, 70, -474);
		this.upperBoundEntry4 = new Location(Bukkit.getWorld("world"), -447, 80, -465);
	}
	
	public void startBloodBowl() {
		// Only one BloodBowl at a time
		if (this.running) {
			return;
		}

		// Load the world
		WorldCreator wc = new WorldCreator("bloodbowl");
		wc.generator(new CleanroomChunkGenerator("."));
		World world = this.plugin.getServer().createWorld(wc);
		world.setMonsterSpawnLimit(0);
		world.setAnimalSpawnLimit(0);

		// Protect immediately
		this.plugin.getgGuardPlugin().getProtectedRegionFromConfig("bloodbowl");

		// Hardcoded
		this.capzoneLocation1 = new Location(Bukkit.getWorld("bloodbowl"), -6, 120, -6);
		this.capzoneLocation2 = new Location(Bukkit.getWorld("bloodbowl"), 7, 124, 7);

		// Add location stuff
		this.locationQueue.add(new Location(this.plugin.getServer().getWorld("bloodbowl"), -126.5, 118, 49.5, 225, 0));
		this.locationQueue.add(new Location(this.plugin.getServer().getWorld("bloodbowl"), 113.5, 119, -67.5, 45, 0));
		this.locationQueue.add(new Location(this.plugin.getServer().getWorld("bloodbowl"), -119.5, 118, -92.5, 315, 0));
		this.locationQueue.add(new Location(this.plugin.getServer().getWorld("bloodbowl"), 108.5, 119, 81.5, 135, 0));
		this.locationQueue.add(new Location(this.plugin.getServer().getWorld("bloodbowl"), -59.5, 118, 105.5, 225, 0));
		this.locationQueue.add(new Location(this.plugin.getServer().getWorld("bloodbowl"), -78.5, 118, -130.5, 315, 0));
		this.locationQueue.add(new Location(this.plugin.getServer().getWorld("bloodbowl"), 58.5, 119, 131.5, 135, 0));
		this.locationQueue.add(new Location(this.plugin.getServer().getWorld("bloodbowl"), 66.5, 118, -113.5, 45, 0));

		this.xMin = 89;
		this.xMax = 92;
		this.zMin = -96;
		this.zMax = -93;
		
		// Start up the task
		this.bloodBowlScoreTracker = new BloodBowlScoreTrackerTask(this.plugin);
		this.scoreTrackerTask = this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, this.bloodBowlScoreTracker, 20, 20);
			
		// Allow ppl to join
		this.running = true;
		Gberry.broadcastMessage(ChatColor.GREEN + "BloodBowl is now open. Use /bb to find the portal locations to join.");

        // Call event for tablist
        EventStateChangeEvent e = new EventStateChangeEvent("BloodBowl", true);
        this.plugin.getServer().getPluginManager().callEvent(e);
    }
	
	public void endBloodBowl() {	
		// tp people remaining out
		World world = this.plugin.getServer().getWorld("bloodbowl");
		List<Player> players = world.getPlayers();
		for (Player player : players) {
			player.teleport(this.plugin.getSpawnLocation());
		}

		// Turn off scoreboards for everyone
		for (Player p : this.plugin.getServer().getOnlinePlayers()) {
            Scoreboard board = p.getScoreboard();
            if (board != null) {
                Objective objective = board.getObjective(DisplaySlot.SIDEBAR);
                if (objective != null) {
                    objective.unregister();
                }
            }
		}

        // TOOD: Pay the winners $1k
		
		// Clean up
		this.mapOfCaptures.clear();
		this.mapOfScores.clear();
		this.participants.clear();
		this.locationQueue.clear();
		this.factionToSpawnLocationMap.clear();
		
		// Unload the world
		this.plugin.getServer().unloadWorld("bloodbowl", false);
		
		// Stop score tracker
		this.scoreTrackerTask.cancel();
		
		this.running = false;

        // Call event for tablist
        EventStateChangeEvent e = new EventStateChangeEvent("BloodBowl", false);
        this.plugin.getServer().getPluginManager().callEvent(e);
	}

	public GFactions getPlugin() {
		return plugin;
	}

	public void setPlugin(GFactions plugin) {
		this.plugin = plugin;
	}

	public int getxMin() {
		return xMin;
	}

	public void setxMin(int xMin) {
		this.xMin = xMin;
	}

	public int getxMax() {
		return xMax;
	}

	public void setxMax(int xMax) {
		this.xMax = xMax;
	}

	public int getzMin() {
		return zMin;
	}

	public void setzMin(int zMin) {
		this.zMin = zMin;
	}

	public int getzMax() {
		return zMax;
	}

	public void setzMax(int zMax) {
		this.zMax = zMax;
	}

	public Map<String, Integer> getMapOfScores() {
		return mapOfScores;
	}

	public void setMapOfScores(Map<String, Integer> mapOfScores) {
		this.mapOfScores = mapOfScores;
	}

	public HashSet<String> getParticipants() {
		return participants;
	}

	public void setParticipants(HashSet<String> participants) {
		this.participants = participants;
	}

	public BloodBowlScoreTrackerTask getBloodBowlScoreTracker() {
		return bloodBowlScoreTracker;
	}

	public void setBloodBowlScoreTracker(
			BloodBowlScoreTrackerTask bloodBowlScoreTracker) {
		this.bloodBowlScoreTracker = bloodBowlScoreTracker;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public ArrayList<Location> getWarpLocations() {
		return warpLocations;
	}

	public void setWarpLocations(ArrayList<Location> warpLocations) {
		this.warpLocations = warpLocations;
	}

	public Map<Faction, Location> getFactionToSpawnLocationMap() {
		return factionToSpawnLocationMap;
	}

	public void setFactionToSpawnLocationMap(
			Map<Faction, Location> factionToSpawnLocationMap) {
		this.factionToSpawnLocationMap = factionToSpawnLocationMap;
	}

	public Queue<Location> getLocationQueue() {
		return locationQueue;
	}

	public void setLocationQueue(Queue<Location> locationQueue) {
		this.locationQueue = locationQueue;
	}

	public Location getCapzoneLocation1() {
		return capzoneLocation1;
	}

	public void setCapzoneLocation1(Location capzoneLocation1) {
		this.capzoneLocation1 = capzoneLocation1;
	}

	public Location getCapzoneLocation2() {
		return capzoneLocation2;
	}

	public void setCapzoneLocation2(Location capzoneLocation2) {
		this.capzoneLocation2 = capzoneLocation2;
	}

	public Map<String, Integer> getMapOfCaptures() {
		return mapOfCaptures;
	}

	public void setMapOfCaptures(Map<String, Integer> mapOfCaptures) {
		this.mapOfCaptures = mapOfCaptures;
	}

	public BukkitTask getScoreTrackerTask() {
		return scoreTrackerTask;
	}

	public void setScoreTrackerTask(BukkitTask scoreTrackerTask) {
		this.scoreTrackerTask = scoreTrackerTask;
	}

	public Location getLowerBoundEntry1() {
		return lowerBoundEntry1;
	}

	public void setLowerBoundEntry1(Location lowerBoundEntry1) {
		this.lowerBoundEntry1 = lowerBoundEntry1;
	}

	public Location getUpperBoundEntry1() {
		return upperBoundEntry1;
	}

	public void setUpperBoundEntry1(Location upperBoundEntry1) {
		this.upperBoundEntry1 = upperBoundEntry1;
	}

	public Location getLowerBoundEntry2() {
		return lowerBoundEntry2;
	}

	public void setLowerBoundEntry2(Location lowerBoundEntry2) {
		this.lowerBoundEntry2 = lowerBoundEntry2;
	}

	public Location getUpperBoundEntry2() {
		return upperBoundEntry2;
	}

	public void setUpperBoundEntry2(Location upperBoundEntry2) {
		this.upperBoundEntry2 = upperBoundEntry2;
	}

	public Location getLowerBoundEntry3() {
		return lowerBoundEntry3;
	}

	public void setLowerBoundEntry3(Location lowerBoundEntry3) {
		this.lowerBoundEntry3 = lowerBoundEntry3;
	}

	public Location getUpperBoundEntry3() {
		return upperBoundEntry3;
	}

	public void setUpperBoundEntry3(Location upperBoundEntry3) {
		this.upperBoundEntry3 = upperBoundEntry3;
	}

	public Location getLowerBoundEntry4() {
		return lowerBoundEntry4;
	}

	public void setLowerBoundEntry4(Location lowerBoundEntry4) {
		this.lowerBoundEntry4 = lowerBoundEntry4;
	}

	public Location getUpperBoundEntry4() {
		return upperBoundEntry4;
	}

	public void setUpperBoundEntry4(Location upperBoundEntry4) {
		this.upperBoundEntry4 = upperBoundEntry4;
	}
}
