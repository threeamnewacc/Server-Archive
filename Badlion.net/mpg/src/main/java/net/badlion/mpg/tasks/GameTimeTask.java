package net.badlion.mpg.tasks;

import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.bukkitevents.MPGGameTimeEvent;
import net.badlion.mpg.managers.MPGPlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static net.badlion.mpg.MPGGame.GameState.POST_GAME;

public class GameTimeTask extends BukkitRunnable {

    protected static GameTimeTask instance;

	private int numberOfPlayersOnline;
	private int numberOfSpectatorsOnline;

    protected int seconds = 0;
    protected int minutes = 0;
    protected int hours = -1; // Don't show hours by default

	private String timeString = "00:00";

    public GameTimeTask() {
        GameTimeTask.instance = this;
    }

    public void run() {
	    MPGGameTimeEvent event = new MPGGameTimeEvent(this.getTotalSeconds());
	    MPG.getInstance().getServer().getPluginManager().callEvent(event);

	    if (MPG.getInstance().getMPGGame() == null || MPG.getInstance().getMPGGame().getGameState() == POST_GAME) {
		    // Update everyone's scoreboard one last time
		    for (MPGPlayer mpgPlayer : MPGPlayerManager.getAllMPGPlayers()) {
			    mpgPlayer.update();
		    }

		    this.cancel();
		    return;
	    }

	    // Update player and spectator count
	    if (this.seconds % 5 == 0) {
		    this.numberOfPlayersOnline = 0;
		    this.numberOfSpectatorsOnline = 0;

		    // Count how many players are online
		    for (MPGPlayer mpgPlayer: MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.PLAYER)) {
			    Player pl = mpgPlayer.getPlayer();
			    if (pl != null && pl.isOnline()) {
				    ++this.numberOfPlayersOnline;
			    }
		    }

		    // Count how many spectators are online
		    for (MPGPlayer mpgPlayer: MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.SPECTATOR)) {
			    Player pl = mpgPlayer.getPlayer();
			    if (pl != null && pl.isOnline()) {
				    ++this.numberOfSpectatorsOnline;
			    }
		    }

		    for (MPGPlayer mpgPlayer: MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.MOD)) {
			    Player pl = mpgPlayer.getPlayer();
			    if (pl != null && pl.isOnline()) {
				    ++this.numberOfSpectatorsOnline;
			    }
		    }

		    for (MPGPlayer mpgPlayer: MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.HOST)) {
			    Player pl = mpgPlayer.getPlayer();
			    if (pl != null && pl.isOnline()) {
				    ++this.numberOfSpectatorsOnline;
			    }
		    }
	    }

        // Handle game time
        if (++this.seconds == 60) {
            this.minutes++;
            this.seconds = 0;
        }

        if (this.minutes == 60) {
	        if (this.hours == -1) {
		        this.hours = 0;
	        }

            this.hours++;
            this.minutes = 0;
        }

	    // Update time string every second
	    this.timeString = this.niceTime(this.hours, this.minutes, this.seconds);

	    // Update every player
	    for (MPGPlayer mpgPlayer : MPGPlayerManager.getAllMPGPlayers()) {
		    mpgPlayer.update();
	    }

    }

    public int getTotalSeconds() {
	    // Are we only using minutes and seconds for this game?
	    if (this.hours == -1) {
		    return this.seconds + (this.minutes * 60);
	    } else {
		    return this.seconds + (this.minutes * 60) + (this.hours * 3600);
	    }
    }

    public String getGameTime() {
        return this.timeString;
    }

    public String niceTime(int hours, int minutes, int seconds) {
        StringBuilder builder = new StringBuilder();

        // Skip hours
        if (hours != -1) {
            builder.append(' ');
            if (hours < 10) {
                builder.append('0');
            }
            builder.append(hours);
            builder.append(':');
        }

        if (minutes < 10/* && hours != -1*/) {
            builder.append('0');
        }

        builder.append(minutes);
        builder.append(':');

        if (seconds < 10) {
            builder.append('0');
        }

        builder.append(seconds);

        return builder.toString();
    }

	public static GameTimeTask getInstance() {
		return GameTimeTask.instance;
	}

	public int getNumberOfPlayersOnline() {
		return this.numberOfPlayersOnline;
	}

	public int getNumberOfSpectatorsOnline() {
		return this.numberOfSpectatorsOnline;
	}

}
