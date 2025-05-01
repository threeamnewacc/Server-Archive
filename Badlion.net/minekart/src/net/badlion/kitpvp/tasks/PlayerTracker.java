package net.badlion.kitpvp.tasks;

import java.util.ArrayList;

import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.tinywebteam.badlion.MineKart;
import com.tinywebteam.badlion.Race;
import com.tinywebteam.badlion.Racer;

public class PlayerTracker extends BukkitRunnable {
	
	private MineKart plugin;
	private ArrayList<Race> races;
	
	public PlayerTracker(MineKart plugin, ArrayList<Race> races) {
		this.plugin = plugin;
		this.races = races;
	}
	
	@Override
	public void run() {
		// Go through each racer and see if they hit a checkpoint or not
		for (Race race : this.races) {
			for (Racer racer : race.getRacers()) {
				Block blockAt = racer.getPlayer().getLocation().getBlock();
				Vector to = racer.getNextCheckPoint().getVector(); // never changes, its a checkpoint
				Vector at = new Vector(blockAt.getX(), blockAt.getY(), blockAt.getZ()); // this changes
				
				if (to.distance(at) < 25) {
					// Shift old checkpoint
					racer.setPreviousCheckPoint(racer.getNextCheckPoint());
					
					// Set their next checkpoint
					racer.setNextCheckPoint(racer.getTrack().getCheckPointToNextCheckPoint().get(racer.getNextCheckPoint()));
					
					// Prevent overlap of checkpoints
					racer.setNumOfCheckPointsPassed(racer.getNumOfCheckPointsPassed() + 1);
					
					// Allow them to pass GO
					if (racer.getTrack().getCheckPoints().size() - 1 == racer.getTrack().getCheckPoints().indexOf(racer.getPreviousCheckPoint())) {
						racer.setAllowedToIncrementLap(true);
					}
				}
			}
		}
	}
}
