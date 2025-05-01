package net.badlion.gfactions.events.supermine;

import net.badlion.common.libraries.DateCommon;
import net.badlion.gfactions.Config;
import net.badlion.gfactions.GFactions;
import net.badlion.gfactions.tasks.CheckTimeTask;
import org.bukkit.Location;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class SuperMineConfig extends Config {

	private List<DateTime> eventTimes = new ArrayList<>();

    private List<Location> cobbleLocations = new ArrayList<>();
    private Location minLocation;
    private Location maxLocation;
    private int coalChance = 0;
    private int ironChance = 0;
    private int goldChance = 0;
    private int redStoneChance = 0;
    private int lapisChance = 0;
    private int diamondChance = 0;
    private int emeraldChance = 0;

    private int blocksPerTick = 0;
    private int resetTimeInSeconds = 0;
    private int numOfResets = 0;

    private String startMsg = "";
    private String refreshMsg = "";
    private String endMsg = "";

    private String tenMinuteWarning = "";
    private String fiveMinuteWarning = "";
    private String twoMinuteWarning = "";

    public SuperMineConfig(String fileName) {
        super(fileName);

        // Load config
        this.load();

	    // Add to check time component
	    CheckTimeTask.addCheckTimeComponent(new CheckTimeTask.CheckTimeComponent() {
		    @Override
		    public void run(CheckTimeTask task) {
			    if (GFactions.plugin.getSuperMine() == null) {
				    DateTime now = DateTime.now();
				    for (DateTime dt : SuperMineConfig.this.eventTimes) {
					    // The datetimes are almost never going to be equal, check day of week and minute of day
					    //if (now.getDayOfWeek() == dt.getDayOfWeek() && now.getMinuteOfDay() == dt.getMinuteOfDay() - 10) {
					    if (now.getDayOfWeek() == dt.getDayOfWeek() && now.getSecondOfDay() == dt.getSecondOfDay() - 15) {
						    GFactions.plugin.getServer().dispatchCommand(GFactions.plugin.getServer().getConsoleSender(), "supermine start");
						    break;
					    }
				    }
			    }
		    }
	    });
    }

    @Override
    public void load() {
	    // Load times
	    this.eventTimes.clear();
	    for (String time : this.config.getStringList("event_times")) {
		    DateTime dateTime = DateCommon.parseDateTime(time);
		    if (dateTime != null) {
			    this.eventTimes.add(dateTime.withSecondOfMinute(0).withMillisOfSecond(0));
		    } else {
			    dateTime = DateCommon.parseDayTime(time);
			    if (dateTime != null) {
				    this.eventTimes.add(dateTime.withSecondOfMinute(0).withMillisOfSecond(0));
			    } else {
				    GFactions.plugin.getServer().getLogger().severe("Unable to parse event time in stronghold config: " + time);
			    }
		    }
	    }

        List<String> locationsValues = this.config.getStringList("cobble-locations");
        for (String locationString : locationsValues) {
            this.cobbleLocations.add(GFactions.plugin.parseLocation(locationString));
        }

        this.minLocation = GFactions.plugin.parseLocation(this.config.getString("min-region-location"));
        this.maxLocation = GFactions.plugin.parseLocation(this.config.getString("max-region-location"));

        this.coalChance = this.config.getInt("coal-chance");
        this.ironChance = this.config.getInt("iron-chance");
        this.goldChance = this.config.getInt("gold-chance");
        this.redStoneChance = this.config.getInt("redstone-chance");
        this.lapisChance = this.config.getInt("lapis-chance");
        this.diamondChance = this.config.getInt("diamond-chance");
        this.emeraldChance = this.config.getInt("emerald-chance");

        this.blocksPerTick = this.config.getInt("blocks-per-tick");
        this.resetTimeInSeconds = this.config.getInt("reset-time-in-seconds");
        this.numOfResets = this.config.getInt("number-of-resets");

        this.startMsg = this.config.getString("msgs.start");
        this.refreshMsg = this.config.getString("msgs.refresh");
        this.endMsg = this.config.getString("msgs.end");

        this.tenMinuteWarning = this.config.getString("msgs.ten");
        this.fiveMinuteWarning = this.config.getString("msgs.five");
        this.twoMinuteWarning = this.config.getString("msgs.two");
    }



    public List<Location> getCobbleLocations() {
        return cobbleLocations;
    }

    public Location getMinLocation() {
        return minLocation;
    }

    public Location getMaxLocation() {
        return maxLocation;
    }

    public int getCoalChance() {
        return coalChance;
    }

    public int getIronChance() {
        return ironChance;
    }

    public int getGoldChance() {
        return goldChance;
    }

    public int getRedStoneChance() {
        return redStoneChance;
    }

    public int getLapisChance() {
        return lapisChance;
    }

    public int getDiamondChance() {
        return diamondChance;
    }

    public int getEmeraldChance() {
        return emeraldChance;
    }

    public int getBlocksPerTick() {
        return blocksPerTick;
    }

    public int getResetTimeInSeconds() {
        return resetTimeInSeconds;
    }

    public int getNumOfResets() {
        return numOfResets;
    }

    public String getStartMsg() {
        return startMsg;
    }

    public String getRefreshMsg() {
        return refreshMsg;
    }

    public String getEndMsg() {
        return endMsg;
    }

    public String getTenMinuteWarning() {
        return tenMinuteWarning;
    }

    public String getFiveMinuteWarning() {
        return fiveMinuteWarning;
    }

    public String getTwoMinuteWarning() {
        return twoMinuteWarning;
    }
}
