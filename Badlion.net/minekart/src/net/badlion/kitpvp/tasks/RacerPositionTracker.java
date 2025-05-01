package net.badlion.kitpvp.tasks;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Score;
import org.bukkit.util.Vector;

import com.tinywebteam.badlion.CheckPoint;
import com.tinywebteam.badlion.MineKart;
import com.tinywebteam.badlion.Race;
import com.tinywebteam.badlion.Racer;

public class RacerPositionTracker extends BukkitRunnable {
	
	private MineKart plugin;
	private ArrayList<Race> races;
	
	public RacerPositionTracker(MineKart plugin, ArrayList<Race> races) {
		this.plugin = plugin;
		this.races = races;
	}
	
	@Override
	public void run() {
		for (Race race : this.races) {
			ArrayList<CheckPoint> checkpoints = race.getTrack().getCheckPoints();
			// For every player
			for (int i = 0; i < race.getPlayerPositions().size(); ++i) {
				Racer racer = race.getPlayerPositions().get(i);
				
				// If they already finished race, skip them
				if (!race.getRacersStillInRace().contains(racer)) {
					continue;
				}
				
				// For every other player
				for (int j = 0; j < race.getPlayerPositions().size(); ++j) {
					Racer racer2 = race.getPlayerPositions().get(j);
					
					// They finished already
					if (!race.getRacersStillInRace().contains(racer2)) {
						continue;
					}
					
					// If they are the same player it is pointless to compare
					if (racer.getPlayer().equals(racer2.getPlayer())) {
						continue;
					}
					if (racer.getCurrentLapNumber() > racer2.getCurrentLapNumber()) {
						// More laps? They are ahead, stop for this player
						break;
					} else if (racer.getCurrentLapNumber() < racer2.getCurrentLapNumber()) {
						// We are behind this player
						continue;
					} else if (racer.getNumOfCheckPointsPassed() > racer2.getNumOfCheckPointsPassed()) {
						// They have more checkpoints (probably a lap ahead and we have same checkpoint)
						break;
					} else if (racer.getNumOfCheckPointsPassed() < racer2.getNumOfCheckPointsPassed()) {
						// Behind in num of checkpoints
						continue;
					} else if (checkpoints.indexOf(racer.getNextCheckPoint()) > checkpoints.indexOf(racer2.getNextCheckPoint())) {
						// Further checkpoint, still ahead
						break;
					} else if (checkpoints.indexOf(racer.getNextCheckPoint()) < checkpoints.indexOf(racer2.getNextCheckPoint())) {
						// Behind checkpoint
						continue;
					} else {
						// Sigh, vector math time
						Vector checkPointVector = new Vector(racer.getNextCheckPoint().getBlock().getX(), racer.getNextCheckPoint().getBlock().getY(), racer.getNextCheckPoint().getBlock().getZ());
						Vector racer1Vector = new Vector(racer.getPlayer().getLocation().getX(), racer.getPlayer().getLocation().getY(), racer.getPlayer().getLocation().getZ());
						Vector racer2Vector = new Vector(racer2.getPlayer().getLocation().getX(), racer2.getPlayer().getLocation().getY(), racer2.getPlayer().getLocation().getZ());
						
						// Player 1 ahead of player2 in list already
						if (race.getPlayerPositions().indexOf(racer2) < race.getPlayerPositions().indexOf(racer)) {
							// Still behind
							if (checkPointVector.distance(racer1Vector) > checkPointVector.distance(racer2Vector)) {
								continue;
							}
						// Player 2 behind player 1 in list
						} else {
							// Still behind
							if (checkPointVector.distance(racer1Vector) < checkPointVector.distance(racer2Vector)) {
								continue;
							}
						}
					}
					// Player 2 closer, move them up
					race.getPlayerPositions().remove(racer2);
					race.getPlayerPositions().add(i, racer2);
				}
			}
			
			for (int j = 0; j < race.getPlayerPositions().size(); ++j) {
				if (race.getPlayerPositions().get(j).isStopScoreboard()) {
					continue;
				}
				// Remove old Lap #
				if (race.getPlayerPositions().get(j).isUpdateLap()) {
					OfflinePlayer oldLap = Bukkit.getOfflinePlayer("Lap " + (race.getPlayerPositions().get(j).getCurrentLapNumber() - 1) + " of ");
					race.getPlayerPositions().get(j).getBoard().resetScores(oldLap);
					race.getPlayerPositions().get(j).setUpdateLap(false);
					OfflinePlayer scorePlayer = Bukkit.getOfflinePlayer("Lap " + race.getPlayerPositions().get(j).getCurrentLapNumber() + " of ");
					Score lapScore = race.getPlayerPositions().get(j).getObjective().getScore(scorePlayer);
					lapScore.setScore(race.getTrack().getNumOfLaps());
				}
				//race.getPlayerPositions().get(j).getBoard().resetScores(scorePlayer);
				
				// Ok, now we are done sorting them, update scoreboards
				for (int i = 0; i < race.getPlayerPositions().size(); ++i) {
					OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(race.getPlayerPositions().get(i).getPlayer().getName());
					//race.getPlayerPositions().get(i).getBoard().resetScores(offlinePlayer);
					Score score = race.getPlayerPositions().get(j).getObjective().getScore(offlinePlayer);
					score.setScore(-1 * (i + 1));	
				}
				if (race.getPlayerPositions().get(j).getPlayer().isOnline()) {
					race.getPlayerPositions().get(j).getPlayer().setScoreboard(race.getPlayerPositions().get(j).getBoard());
				}
			}
		}
	}

}
