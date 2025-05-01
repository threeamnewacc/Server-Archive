package net.badlion.kitpvp.tasks;

import java.util.ArrayList;

import org.bukkit.scheduler.BukkitRunnable;

import com.tinywebteam.badlion.MineKart;
import com.tinywebteam.badlion.Race;
import com.tinywebteam.badlion.Racer;

public class OneMinuteLeftTask extends BukkitRunnable {

	private MineKart plugin;
	private Race race;
	
	public OneMinuteLeftTask(MineKart plugin, Race race) {
		this.plugin = plugin;
		this.race = race;
	}
	
	@Override
	public void run() {
		// Time is up, clear track and kick them all out
		ArrayList<Racer> racers = race.getRacersStillInRace();
		for (Racer racer : racers) {
			racer.remove(true);
		}
	}
	
}
