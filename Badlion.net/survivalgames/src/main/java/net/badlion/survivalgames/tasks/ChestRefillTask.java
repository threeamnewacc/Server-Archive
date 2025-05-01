package net.badlion.survivalgames.tasks;

import net.badlion.gberry.Gberry;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGGame;
import net.badlion.mpg.tasks.GameTimeTask;
import net.badlion.survivalgames.SurvivalGames;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class ChestRefillTask extends BukkitRunnable {

	public static final int REFILL_TIME = 450; // In seconds

	@Override
	public void run() {
		if (MPG.getInstance().getMPGGame().getGameState() == MPGGame.GameState.GAME) {
			if (GameTimeTask.getInstance().getTotalSeconds() >= ChestRefillTask.REFILL_TIME) {
				// Refill chests
				SurvivalGames.getInstance().getSGGame().refillChests();

				Gberry.broadcastMessageNoBalance(MPG.MPG_PREFIX + ChatColor.GOLD + "Chests have been refilled!");

				this.cancel();
			}
		} else {
			this.cancel();
		}
	}

}
