package net.badlion.mpg.tasks;

import com.google.common.base.Joiner;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.BukkitUtil;
import net.badlion.ministats.MiniStats;
import net.badlion.ministats.managers.DatabaseManager;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGGame;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.bukkitevents.MPGEndGameEvent;
import net.badlion.mpg.managers.MPGPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.badlion.mpg.MPGGame.GameState.POST_GAME;

public class CheckForEndGame extends BukkitRunnable {

    private static CheckForEndGame instance;

	private boolean gameEnding = false;

    public CheckForEndGame() {
        CheckForEndGame.instance = this;
    }

    @Override
    public void run() {
	    // Prevents edge cases when we manually call this task on player death
	    if (this.gameEnding) {
		    return;
	    }

	    if (MPG.getInstance().getMPGGame() == null || MPG.getInstance().getMPGGame().getGameState() == POST_GAME) {
		    this.cancel();
		    return;
	    }

	    if (MPG.getInstance().getMPGGame().checkForEndGame()) {
		    MPGEndGameEvent event = new MPGEndGameEvent(MPG.getInstance().getMPGGame());
		    MPG.getInstance().getServer().getPluginManager().callEvent(event);

		    MPG.getInstance().getMPGGame().setEndTime(new DateTime(DateTimeZone.UTC).getMillis());

		    this.gameEnding = true;

		    // Cancel the task
		    this.cancel();

		    List<String> winnerPlayers = new ArrayList<>();
		    for (UUID uuid : MPG.getInstance().getMPGGame().getWinners()) {
			    MPGPlayer mpgPlayer = MPGPlayerManager.getMPGPlayer(uuid);

			    // Get the winner's names
			    winnerPlayers.add(mpgPlayer.getUsername());

			    MiniStats.getInstance().getPlayerDataListener().getPlayerData(mpgPlayer.getUniqueId()).addTotalTimePlayed(
					    (System.currentTimeMillis() - mpgPlayer.getStartTime()) / 1000);
		    }

		    // Is this a clan game?
		    if (MPG.getInstance().getMPGGame().isClanGame()) {
			    String winningClanName = MPGPlayerManager.getMPGPlayer(MPG.getInstance().getMPGGame().getWinners().iterator().next()).getTeam().getClanName();
			    String losingClanName = winningClanName.equals(MPG.getInstance().getMPGGame().getSenderClanName())
					    ? MPG.getInstance().getMPGGame().getTargetClanName() : MPG.getInstance().getMPGGame().getSenderClanName();

			    Gberry.broadcastMessage(MPG.MPG_PREFIX + ChatColor.AQUA + "Congratulations to " +
					    ChatColor.GOLD + winningClanName + ChatColor.AQUA + " for beating " + ChatColor.GOLD + losingClanName + ChatColor.AQUA + " to win the match!");
		    } else {
			    Gberry.broadcastMessage(MPG.MPG_PREFIX + ChatColor.AQUA + "Congratulations to " +
					    Joiner.on(", ").join(winnerPlayers) + " for winning the match!");
		    }

		    // Store stats
		    BukkitUtil.runTaskAsync(new Runnable() {
			    @Override
			    public void run() {
				    int result = DatabaseManager.saveMatchData(MPG.getInstance().getMPGGame());

				    // Was there an error?
				    if (result == -1) {
					    Gberry.broadcastMessageNoBalance(MPG.MPG_PREFIX + ChatColor.RED + "There was an error saving data, please report this to an administrator!.");
				    }

				    // End the game after stats save (2 seconds later just to be safe)
				    BukkitUtil.runTaskLater(new Runnable() {
					    @Override
					    public void run() {
						    MPG.getInstance().getMPGGame().setGameState(MPGGame.GameState.POST_GAME);
					    }
				    }, 40L);
			    }
		    });
	    }
    }

	public static CheckForEndGame getInstance() {
		return CheckForEndGame.instance;
	}

	public boolean isGameEnding() {
		return this.gameEnding;
	}

}
