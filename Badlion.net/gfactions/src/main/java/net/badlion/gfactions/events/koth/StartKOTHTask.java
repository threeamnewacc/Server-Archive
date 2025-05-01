package net.badlion.gfactions.events.koth;

import net.badlion.gfactions.GFactions;
import org.bukkit.scheduler.BukkitRunnable;

public class StartKOTHTask extends BukkitRunnable {

    private GFactions plugin;

    private KOTH koth;

    public StartKOTHTask(GFactions plugin, KOTH koth) {
        this.plugin = plugin;
        this.koth = koth;
    }

    @Override
    public void run() {
	    // End KOTH Task
        //EndKOTHTask endKOTHTask = new EndKOTHTask(this.plugin);
        //this.plugin.setEndKOTHTask(endKOTHTask);
		//endKOTHTask.runTaskLater(this.plugin, 20 * this.koth.getLength());

	    // KOTH Score Tracker Task
        KOTHScoreTrackerTask task = new AdvancedKOTHScoreTrackerTask(this.plugin);//new KOTHScoreTrackerTask(this.plugin);
        this.koth.setKothScoreTracker(task);
	    this.koth.setScoreTrackerTask(task.runTaskTimer(this.plugin, 20, 20));
    }
}
