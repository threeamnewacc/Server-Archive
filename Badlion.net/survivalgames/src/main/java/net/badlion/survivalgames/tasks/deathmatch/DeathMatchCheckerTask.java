package net.badlion.survivalgames.tasks.deathmatch;

import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGGame;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.managers.MPGPlayerManager;
import net.badlion.mpg.tasks.CheckForEndGame;
import net.badlion.mpg.tasks.GameTimeTask;
import org.bukkit.scheduler.BukkitRunnable;

public class DeathMatchCheckerTask extends BukkitRunnable {

	@Override
	public void run() {
		if (CheckForEndGame.getInstance().isGameEnding() || MPG.getInstance().getMPGGame().getGameState().ordinal() >= MPGGame.GameState.PRE_DEATH_MATCH.ordinal()) {
			this.cancel();
			return;
		}

		int onlinePlayers = MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.PLAYER).size();
		int offlinePlayers = MPGPlayerManager.getMPGPlayersByState(MPGPlayer.PlayerState.DC).size();

		// Do we have enough players for a deathmatch?
		if (onlinePlayers + offlinePlayers <= MPG.getInstance().getIntegerConfigOption(MPG.ConfigFlag.NUM_OF_PLAYERS_FOR_DEATH_MATCH)) {
			MPG.getInstance().getMPGGame().setGameState(MPGGame.GameState.PRE_DEATH_MATCH);

			this.cancel();
			return;
		}

		// Time limit reached?
		if (GameTimeTask.getInstance().getTotalSeconds()
				>= MPG.getInstance().getIntegerConfigOption(MPG.ConfigFlag.DEATH_MATCH_START_TIME)) {

			MPG.getInstance().getMPGGame().setGameState(MPGGame.GameState.PRE_DEATH_MATCH);

			this.cancel();
		}
	}

}
