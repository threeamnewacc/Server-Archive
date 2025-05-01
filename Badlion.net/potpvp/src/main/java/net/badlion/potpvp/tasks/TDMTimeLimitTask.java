package net.badlion.potpvp.tasks;

import net.badlion.gberry.utils.ScoreboardUtil;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.inventories.tdm.TDMVoteInventory;
import net.badlion.potpvp.tdm.TDMGame;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

public class TDMTimeLimitTask extends BukkitRunnable {

	private TDMGame tdmGame;

	private int seconds = 600; // 10 minutes

	public TDMTimeLimitTask(TDMGame tdmGame) {
		this.tdmGame = tdmGame;

		// Covers edge case when TDM game restarts
		String time = this.niceTime();
		for (Player player : this.tdmGame.getPlayers()) {
			Team team = ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "time", ChatColor.GOLD + "Time", ChatColor.GOLD + " Left: " + ChatColor.WHITE);
			team.setSuffix(time);
		}
	}

	@Override
	public void run() {
		this.seconds--;

		// Time limit reached?
		if (this.seconds == 0) {
			// Figure out which team had the most points
			List<TDMGame.TDMTeam> tied = new ArrayList<>();

			TDMGame.TDMTeam tdmTeam = null;
			for (TDMGame.TDMTeam team : this.tdmGame.getTeamsToPlayers().keySet()) {
				if (team.getScore() == 0) continue;

				if (tdmTeam == null) {
					tdmTeam = team;
					continue;
				}

				if (team.getScore() > tdmTeam.getScore()) {
					tdmTeam = team;

					// Empty tied list
					tied.clear();
				} else if (team.getScore() == tdmTeam.getScore()) {
					// Tie
					tied.add(team);
				}
			}

			// Create winner message
			StringBuilder sb = new StringBuilder();
			sb.append(TDMGame.PREFIX);
			sb.append(ChatColor.AQUA);

			if (tdmTeam == null) {
				sb.append("All teams had a score of 0, no team has won the TDM.");
			} else if (tied.isEmpty()) {
				sb.append("The ");
				sb.append(tdmTeam.getColor()).append(tdmTeam.getName());
				sb.append(ChatColor.AQUA);
				sb.append(" team has won the TDM!");
			} else {
				sb.append("The ");
				sb.append(tdmTeam.getColor()).append(tdmTeam.getName());

				for (int i = 0; i < tied.size(); i++) {
					TDMGame.TDMTeam team = tied.get(i);

					sb.append(ChatColor.AQUA);

					if (tied.size() != 2) {
						sb.append(", ");
					}

					if (i == tied.size() - 1) {
						sb.append("and ");
					}

					sb.append(team.getColor());
					sb.append(team.getName());
				}

				sb.append(ChatColor.AQUA);
				sb.append(" teams have won the TDM!");
			}

			String winMessage = sb.toString();

			// Send message to players and change gamemodes
			for (Player player : this.tdmGame.getPlayers()) {
				player.sendMessage(TDMGame.PREFIX + ChatColor.GOLD + "Time limit reached!");
				player.sendMessage(winMessage);

				player.sendMessage(TDMGame.PREFIX + ChatColor.YELLOW + "Right click with the book to vote for the next kit!");
				player.sendMessage(TDMGame.PREFIX + ChatColor.GOLD + "Voting ends in " + TDMVoteTask.VOTING_TIME + "  seconds!");

				// Put everyone in spectator mode
				player.setGameMode(GameMode.CREATIVE);
				player.spigot().setCollidesWithEntities(false);

				player.getInventory().clear();
				player.getInventory().setArmorContents(null);

				// Add voting item to inventory
				player.getInventory().setItem(0, TDMVoteInventory.getVoteItem());
			}

			this.tdmGame.setVoting(true);

			new TDMVoteTask(this.tdmGame).runTaskTimer(PotPvP.getInstance(), 20L, 20L);

			this.cancel();
		}

		// Update time every 5 seconds
		if (this.seconds % 5 == 0) {
			String time = this.niceTime();
			for (Player player : this.tdmGame.getPlayers()) {
				Team team = ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "time", ChatColor.GOLD + "Time", ChatColor.GOLD + " Left: " + ChatColor.WHITE);
				team.setSuffix(time);
			}
		}
	}

	public String niceTime() {
		StringBuilder builder = new StringBuilder();
		builder.append(' ');

		int minutes = this.seconds / 60;
		int seconds = this.seconds % 60;

		if (seconds % 5 != 0) {
			// Show in 5's
			seconds += 5 - seconds % 5;
		}

		if (minutes < 10) {
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

}