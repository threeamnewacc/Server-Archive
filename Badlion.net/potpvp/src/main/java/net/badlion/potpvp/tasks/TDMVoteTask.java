package net.badlion.potpvp.tasks;

import net.badlion.potpvp.tdm.TDMGame;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TDMVoteTask extends BukkitRunnable {

	public static final int VOTING_TIME = 30;

	private TDMGame tdmGame;
	private int seconds = TDMVoteTask.VOTING_TIME;

	public TDMVoteTask(TDMGame tdmGame) {
		this.tdmGame = tdmGame;
	}

	@Override
	public void run() {
		this.seconds--;

		if (this.seconds == 0) {
			this.tdmGame.restart();

			this.cancel();
			return;
		}

		// Send message every 10 seconds
		if (this.seconds % 10 == 0) {
			for (Player player : this.tdmGame.getPlayers()) {
				player.sendMessage(TDMGame.PREFIX + ChatColor.GOLD + "Voting ends in " + this.seconds + " seconds!");
			}
		}
	}

}
