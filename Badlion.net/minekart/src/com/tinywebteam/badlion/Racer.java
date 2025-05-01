package com.tinywebteam.badlion;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.tinywebteam.badlion.tasks.OneMinuteLeftTask;

public class Racer {
	
	private Player player;
	private Horse horse;
	private CheckPoint nextCheckPoint;
	private CheckPoint previousCheckPoint;
	private int numOfCheckPointsPassed;
	private Track track;
	private int currentLapNumber;
	private Race race;
	private Boolean allowedToIncrementLap;
	private String [] places = {"st", "nd", "rd", "th", "th", "th", "th", "th"};
	private ScoreboardManager manager;
	private Scoreboard board;
	private Objective objective;
	private long lastMoveTime;
	private double previousSpeed;
	private boolean updateLap;
	private boolean lockSpeedChange;
	private boolean stopScoreboard;
	private boolean actuallyFinished;
	private boolean allowedToPickupItem;
	
	public Racer(Player player, Track track) {
		this.player = player;
		this.track = track;
		this.previousCheckPoint = this.track.getCheckPoints().get(this.track.getCheckPoints().size() - 1); // last CP
		this.nextCheckPoint = this.track.getCheckPoints().get(0); // first CP
		this.currentLapNumber = 1; 
		this.allowedToIncrementLap = false; // nope
		this.updateLap = true;
		this.lockSpeedChange = false;
		this.stopScoreboard = false;
		this.actuallyFinished = false;
		this.allowedToPickupItem = true;
		this.numOfCheckPointsPassed = 0;
		
		manager = Bukkit.getScoreboardManager();
		board = manager.getNewScoreboard();
		objective = board.registerNewObjective("Positions", "Positions");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		this.lastMoveTime = 0;
	}
	
	public void remove() {
		this.remove(false);
	}
	
	public void remove(Boolean actuallyFinished) {
		// TODO: Do something, need to make it threadsafe with the RacerPositionTracker
		if (actuallyFinished) {
			this.player.sendMessage(ChatColor.GREEN + "You came in " + (this.race.getPlayerPositions().indexOf(this) + 1) + 
				places[this.race.getPlayerPositions().indexOf(this)] + " place.");
			
			// If first place, 1 minute for rest of racers.
			if (this.race.getOneMinuteTask() == null) {
				this.race.setOneMinuteTask(this.race.getPlugin().getServer().getScheduler().runTaskLater(this.race.getPlugin(), new OneMinuteLeftTask(this.race.getPlugin(), this.race), 20 * 60));
				for (Racer racer : this.race.getPlayerPositions()) {
					if (racer.equals(this)) {
						continue;
					} else {
						if (this.race.getPlayerPositions().size() > 2) {
							racer.getPlayer().sendMessage(ChatColor.GREEN + "Someone has crossed the finish line.  You have one minute to complete the race!");
						}
					}
				}
			}
			
			this.actuallyFinished = true;
		} else {
			// Remove them from further tracking
			this.race.getPlayerPositions().remove(this);
			
			// Remove from score board
			for (int i = 0; i < race.getPlayerPositions().size(); ++i) {
				OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(this.player.getName());
				race.getPlayerPositions().get(i).getBoard().resetScores(offlinePlayer);
			}
		}
		
		// Remove the player from further calculations, race standing, and ingame stuff
		this.race.getRacersStillInRace().remove(this);
		this.race.getRacers().remove(this);
		this.race.getPlugin().getPlayerToRacer().remove(this.player);
		this.race.getPlugin().getInMatchMaking().remove(this.player);
		
		// No scoreboard
		this.stopScoreboard = true;
		this.player.setScoreboard(this.manager.getNewScoreboard());
		
		// TODO : horse stuff
		this.horse.eject();
		this.horse.remove();
		this.race.getPlugin().tpPlayerToSpawn(this.player);
		
		// Cleanup
		if (this.race.getRacers().size() == 1) {
			// Recursive call
			this.race.getRacers().get(0).remove(true); // i guess ur legit
			for (Block block : this.race.getRemoveBlocksFromTrack()) {
				this.getRace().getPlugin().getBlocksToBeRemoved().add(block);
				block.setTypeId(0); // air
			}
			for (Item item : this.race.getItemsOnTrack()) {
				item.remove(); // remove items
			}
		} else if (this.race.getRacers().size() == 0) {
			// Race over, release track, cleanup memory
			this.race.getPlayerPositions().clear();
			this.race.getPlugin().getAvailableTracks().set(this.track.getIndex(), true);
			this.race.getPlugin().getRaces().remove(this.race);
		}
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Horse getHorse() {
		return horse;
	}

	public void setHorse(Horse horse) {
		this.horse = horse;
	}

	public CheckPoint getNextCheckPoint() {
		return nextCheckPoint;
	}

	public void setNextCheckPoint(CheckPoint nextCheckPoint) {
		this.nextCheckPoint = nextCheckPoint;
	}

	public Track getTrack() {
		return track;
	}

	public void setTrack(Track track) {
		this.track = track;
	}

	public int getCurrentLapNumber() {
		return currentLapNumber;
	}

	public void setCurrentLapNumber(int currentLapNumber) {
		this.updateLap = true;
		this.currentLapNumber = currentLapNumber;
	}

	public Race getRace() {
		return race;
	}

	public void setRace(Race race) {
		this.race = race;
	}

	public CheckPoint getPreviousCheckPoint() {
		return previousCheckPoint;
	}

	public void setPreviousCheckPoint(CheckPoint previousCheckPoint) {
		this.previousCheckPoint = previousCheckPoint;
	}

	public Boolean getAllowedToIncrementLap() {
		return allowedToIncrementLap;
	}

	public void setAllowedToIncrementLap(Boolean allowedToIncrementLap) {
		this.allowedToIncrementLap = allowedToIncrementLap;
	}

	public Scoreboard getBoard() {
		return board;
	}

	public void setBoard(Scoreboard board) {
		this.board = board;
	}

	public Objective getObjective() {
		return objective;
	}

	public void setObjective(Objective objective) {
		this.objective = objective;
	}
	
	public ScoreboardManager getManager() {
		return manager;
	}

	public void setManager(ScoreboardManager manager) {
		this.manager = manager;
	}
	
	public String[] getPlaces() {
		return places;
	}

	public void setPlaces(String[] places) {
		this.places = places;
	}

	public long getLastMoveTime() {
		return lastMoveTime;
	}

	public void setLastMoveTime(long lastMoveTime) {
		this.lastMoveTime = lastMoveTime;
	}

	public double getPreviousSpeed() {
		return previousSpeed;
	}

	public void setPreviousSpeed(double previousSpeed) {
		this.previousSpeed = previousSpeed;
	}

	public boolean isUpdateLap() {
		return updateLap;
	}

	public void setUpdateLap(boolean updateLap) {
		this.updateLap = updateLap;
	}

	public boolean isLockSpeedChange() {
		return lockSpeedChange;
	}

	public void setLockSpeedChange(boolean lockSpeedChange) {
		this.lockSpeedChange = lockSpeedChange;
	}

	public boolean isStopScoreboard() {
		return stopScoreboard;
	}

	public void setStopScoreboard(boolean stopScoreboard) {
		this.stopScoreboard = stopScoreboard;
	}

	public int getNumOfCheckPointsPassed() {
		return numOfCheckPointsPassed;
	}

	public void setNumOfCheckPointsPassed(int numOfCheckPointsPassed) {
		this.numOfCheckPointsPassed = numOfCheckPointsPassed;
	}

	public boolean isActuallyFinished() {
		return actuallyFinished;
	}

	public void setActuallyFinished(boolean actuallyFinished) {
		this.actuallyFinished = actuallyFinished;
	}

	public boolean isAllowedToPickupItem() {
		return allowedToPickupItem;
	}

	public void setAllowedToPickupItem(boolean allowedToPickupItem) {
		this.allowedToPickupItem = allowedToPickupItem;
	}

}
