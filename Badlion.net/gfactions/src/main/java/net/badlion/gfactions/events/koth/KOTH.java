package net.badlion.gfactions.events.koth;

import net.badlion.gfactions.GFactions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class KOTH {

	private GFactions plugin;
	private Map<String, Integer> mapOfScores = new HashMap<String, Integer>();
	private ArrayList<String> participants = new ArrayList<String>();
	//private Random randomGenerator;
	//private int eventZone;
	//private int maxZones;
	//private int xMin;
	//private int xMax;
	//private int yMin;
	//private int yMax;
	//private int zMin;
	//private int zMax;
	//private int switchLength;
    //private BukkitTask changeHillTask;
	private BukkitTask scoreTrackerTask;
    private KOTHScoreTrackerTask kothScoreTracker;
	private String kothName;
    private int length;
    private Location arenaLocation1;
    private Location arenaLocation2;
    private Location capzoneLocation1;
    private Location capzoneLocation2;
	
	public KOTH(GFactions plugin, int length, String kothName) {
		this.plugin = plugin;
        this.length = length;
        this.kothName = kothName;

        this.arenaLocation1 = new Location(Bukkit.getWorld("world"), this.plugin.getConfig().getInt("gfactions.koth." + kothName + ".area.x_min"),
                                             this.plugin.getConfig().getInt("gfactions.koth." + kothName + ".area.y_min"),
                                             this.plugin.getConfig().getInt("gfactions.koth." + kothName + ".area.z_min"));
        this.arenaLocation2 = new Location(Bukkit.getWorld("world"), this.plugin.getConfig().getInt("gfactions.koth." + kothName + ".area.x_max"),
                                             this.plugin.getConfig().getInt("gfactions.koth." + kothName + ".area.y_max"),
                                             this.plugin.getConfig().getInt("gfactions.koth." + kothName + ".area.z_max"));

        this.capzoneLocation1 = new Location(Bukkit.getWorld("world"), this.plugin.getConfig().getInt("gfactions.koth." + kothName + ".capzone.x_min"),
                                            this.plugin.getConfig().getInt("gfactions.koth." + kothName + ".capzone.y_min"),
                                            this.plugin.getConfig().getInt("gfactions.koth." + kothName + ".capzone.z_min"));
        this.capzoneLocation2 = new Location(Bukkit.getWorld("world"), this.plugin.getConfig().getInt("gfactions.koth." + kothName + ".capzone.x_max"),
                                             this.plugin.getConfig().getInt("gfactions.koth." + kothName + ".capzone.y_max"),
                                             this.plugin.getConfig().getInt("gfactions.koth." + kothName + ".capzone.z_max"));

        //this.randomGenerator = new Random();
		//this.maxZones = this.plugin.getConfig().getInt("gfactions.koth." + kothName + ".num_of_zones");
		//this.eventZone = this.randomGenerator.nextInt(this.maxZones); // [0-n)
		
		//this.changeCapZone();
	}
	
	/*public void changeCapZone() {
		this.plugin.getServer().getLogger().info("KOTH Event Zone #" + this.eventZone);
		
		// Load the coords from our config stuff
		this.xMin = this.plugin.getConfig().getInt("gfactions.koth." + kothName + ".zone_" + this.eventZone + ".x_min");
		this.xMax = this.plugin.getConfig().getInt("gfactions.koth." + kothName + ".zone_" + this.eventZone + ".x_max");
		this.yMin = this.plugin.getConfig().getInt("gfactions.koth." + kothName + ".zone_" + this.eventZone + ".y_min");
		this.yMax = this.plugin.getConfig().getInt("gfactions.koth." + kothName + ".zone_" + this.eventZone + ".y_max");
		this.zMin = this.plugin.getConfig().getInt("gfactions.koth." + kothName + ".zone_" + this.eventZone + ".z_min");
		this.zMax = this.plugin.getConfig().getInt("gfactions.koth." + kothName + ".zone_" + this.eventZone + ".z_max");
		
		// ANNOUNCE IT TO THE WORLD
		this.plugin.getServer().broadcastMessage(ChatColor.YELLOW + "NEW KING OF THE HILL CAPZONE LOCATED AT (" + this.xMin + ", " + this.yMin + ", " + this.zMin + ") to (" + this.xMax + ", " + this.yMax + ", " + this.zMax + ").");
	}*/

	public GFactions getPlugin() {
		return plugin;
	}

	public void setPlugin(GFactions plugin) {
		this.plugin = plugin;
	}

	public Map<String, Integer> getMapOfScores() {
		return mapOfScores;
	}

	public void setMapOfScores(Map<String, Integer> mapOfScores) {
		this.mapOfScores = mapOfScores;
	}

	/*public int getEventZone() {
		return eventZone;
	}

	public void setEventZone(int eventZone) {
		this.eventZone = eventZone;
	}*/

    public Location getArenaLocation1() {
        return arenaLocation1;
    }

    public void setArenaLocation1(Location arenaLocation1) {
        this.arenaLocation1 = arenaLocation1;
    }

    public Location getArenaLocation2() {
        return arenaLocation2;
    }

    public void setArenaLocation2(Location arenaLocation2) {
        this.arenaLocation2 = arenaLocation2;
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

    public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public ArrayList<String> getParticipants() {
		return participants;
	}

	public void setParticipants(ArrayList<String> participants) {
		this.participants = participants;
	}

	/*public Random getRandomGenerator() {
		return randomGenerator;
	}

	public void setRandomGenerator(Random randomGenerator) {
		this.randomGenerator = randomGenerator;
	}

	public int getMaxZones() {
		return maxZones;
	}

	public void setMaxZones(int maxZones) {
		this.maxZones = maxZones;
	}

	public int getSwitchLength() {
		return switchLength;
	}

	public void setSwitchLength(int switchLength) {
		this.switchLength = switchLength;
	}*/

	public BukkitTask getScoreTrackerTask() {
		return scoreTrackerTask;
	}

	public void setScoreTrackerTask(BukkitTask scoreTrackerTask) {
		this.scoreTrackerTask = scoreTrackerTask;
	}

	/*public BukkitTask getChangeHillTask() {
		return changeHillTask;
	}

	public void setChangeHillTask(BukkitTask changeHillTask) {
		this.changeHillTask = changeHillTask;
	}*/

	public KOTHScoreTrackerTask getKothScoreTracker() {
		return kothScoreTracker;
	}

	public void setKothScoreTracker(KOTHScoreTrackerTask kothScoreTracker) {
		this.kothScoreTracker = kothScoreTracker;
	}

	public String getKothName() {
		return kothName;
	}

	public void setKothName(String kothName) {
		this.kothName = kothName;
	}

}
