package net.badlion.gfactions.events.scavenge;

import net.badlion.common.libraries.DateCommon;
import net.badlion.gfactions.Config;
import net.badlion.gfactions.GFactions;
import net.badlion.gfactions.tasks.CheckTimeTask;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

public class ScavengeConfig extends Config {

	private List<DateTime> eventTimes = new ArrayList<>();

    private int numOfScavengeChests = 0;
    private int numOfSecondsTillDespawn = 0;

    private String scavengeStart = "";
    private String foundAllChests = "";
    private String expiredChests = "";

    private String tenMinuteWarning = "";
    private String fiveMinuteWarning = "";
    private String twoMinuteWarning = "";

    public ScavengeConfig(String fileName) {
        super(fileName);

	    // Load config
	    this.load();

	    // Add to check time component
	    CheckTimeTask.addCheckTimeComponent(new CheckTimeTask.CheckTimeComponent() {
		    @Override
		    public void run(CheckTimeTask task) {
			    if (GFactions.plugin.getScavenge() == null) {
				    DateTime now = DateTime.now();
				    for (DateTime dt : ScavengeConfig.this.eventTimes) {
					    // The datetimes are almost never going to be equal, check day of week and minute of day
					    //if (now.getDayOfWeek() == dt.getDayOfWeek() && now.getMinuteOfDay() == dt.getMinuteOfDay() - 10) {
					    if (now.getDayOfWeek() == dt.getDayOfWeek() && now.getSecondOfDay() == dt.getSecondOfDay() - 15) {
						    GFactions.plugin.getServer().dispatchCommand(GFactions.plugin.getServer().getConsoleSender(), "scavenge start");
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
				    GFactions.plugin.getServer().getLogger().severe("Unable to parse event time in scavenge config: " + time);
			    }
		    }
	    }

        this.numOfScavengeChests = this.config.getInt("num-of-chest");
        this.numOfSecondsTillDespawn = this.config.getInt("despawn-time-in-sec");

        this.scavengeStart = this.config.getString("msgs.start");
        this.foundAllChests = this.config.getString("msgs.found-all-chests");
        this.expiredChests = this.config.getString("msgs.chests-expired");

        this.tenMinuteWarning = this.config.getString("msgs.ten");
        this.fiveMinuteWarning = this.config.getString("msgs.five");
        this.twoMinuteWarning = this.config.getString("msgs.two");
    }

    public int getNumOfScavengeChests() {
        return numOfScavengeChests;
    }

    public int getNumOfSecondsTillDespawn() {
        return numOfSecondsTillDespawn;
    }

    public String getScavengeStart() {
        return scavengeStart;
    }

    public String getFoundAllChests() {
        return foundAllChests;
    }

    public String getExpiredChests() {
        return expiredChests;
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
