package net.badlion.ffa;

import net.badlion.gberry.utils.ScoreboardUtil;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.tasks.GameTimeTask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.UUID;

public class FFAPlayer extends MPGPlayer {

	private int pastTotalKills;
	private int pastTotalDeaths;

	public FFAPlayer(UUID uuid, String username) {
		super(uuid, username);
	}

	@Override
	public Location handlePlayerRespawn(Player player) {
		FFA.getInstance().prepPlayerForSpawn(player);

		// Respawn the player at the spawn location
		return FFA.getInstance().getFFAGame().getWorld().getSpawnLocation();
	}

	@Override
	public void handlePlayerStateTransition(Player player) {
		// Do nothing if they're transitioning from DC
		if (this.playerState == PlayerState.DC) return;

		FFA.getInstance().prepPlayerForSpawn(player);

		player.teleport(FFA.getInstance().getFFAGame().getWorld().getSpawnLocation());
	}

	@Override
	public void update() {
		final Player player = FFA.getInstance().getServer().getPlayer(this.uuid);

		// Are they offline?
		if (player == null) {
			return;
		}

		// Initialize
		if (player.getScoreboard().equals(FFA.getInstance().getServer().getScoreboardManager().getMainScoreboard())) {
			ScoreboardUtil.getNewScoreboard(player);
		}

		Scoreboard scoreboard = player.getScoreboard();
		Objective objective = ScoreboardUtil.getObjective(scoreboard, ChatColor.AQUA + "Badlion FFA", DisplaySlot.SIDEBAR,
				ChatColor.AQUA + FFA.FFA_NAME + " FFA");

		Team team = ScoreboardUtil.getTeam(scoreboard, "PLAYERS", ChatColor.GREEN + "Play", "ers: " + ChatColor.WHITE);
		team.setSuffix(GameTimeTask.getInstance().getNumberOfPlayersOnline() + "");
		objective.getScore("ers: " + ChatColor.WHITE).setScore(13);

		team = ScoreboardUtil.getTeam(scoreboard, "", ChatColor.GREEN + "", "");
		team.setSuffix("");
		objective.getScore("").setScore(12);

		team = ScoreboardUtil.getTeam(scoreboard, "KILLS", ChatColor.GREEN + "Ki", "lls: " + ChatColor.WHITE);
		team.setSuffix(this.getKills() + "");
		objective.getScore("lls: " + ChatColor.WHITE).setScore(11);

		team = ScoreboardUtil.getTeam(scoreboard, "DEATHS", ChatColor.GREEN + "Dea", "ths: " + ChatColor.WHITE);
		team.setSuffix(this.getDeaths() + "");
		objective.getScore("ths: " + ChatColor.WHITE).setScore(10);

		team = ScoreboardUtil.getTeam(scoreboard, "KS", ChatColor.GREEN + "Kill Str", "eak: " + ChatColor.WHITE);
		team.setSuffix(this.getCurrentKillStreak() + "");
		objective.getScore("eak: " + ChatColor.WHITE).setScore(9);

		team = ScoreboardUtil.getTeam(scoreboard, " ", ChatColor.GREEN + "", " ");
		team.setSuffix(" ");
		objective.getScore(" ").setScore(8);

		team = ScoreboardUtil.getTeam(scoreboard, "TOTAL_KILLS", ChatColor.GREEN + "Total K", "ills: " + ChatColor.WHITE);
		team.setSuffix(this.pastTotalKills + this.getKills() + "");
		objective.getScore("ills: " + ChatColor.WHITE).setScore(7);

		team = ScoreboardUtil.getTeam(scoreboard, "TOTAL_DEATHS", ChatColor.GREEN + "Total De", "aths: " + ChatColor.WHITE);
		team.setSuffix(this.pastTotalDeaths + this.getDeaths() + "");
		objective.getScore("aths: " + ChatColor.WHITE).setScore(6);

		team = ScoreboardUtil.getTeam(scoreboard, "  ", ChatColor.GREEN + "", "  ");
		team.setSuffix("  ");
		objective.getScore("  ").setScore(5);

		team = ScoreboardUtil.getTeam(scoreboard, "HIGHEST_KS1", ChatColor.GREEN + "Highes", "t" + ChatColor.WHITE);
		objective.getScore("t" + ChatColor.WHITE).setScore(4);

		team = ScoreboardUtil.getTeam(scoreboard, "HIGHEST_KS2", ChatColor.GREEN + "Kill St", "reak: " + ChatColor.WHITE);
		team.setSuffix(this.getHighestKillStreak() + "");
		objective.getScore("reak: " + ChatColor.WHITE).setScore(3);

		team = ScoreboardUtil.getTeam(scoreboard, "   ", ChatColor.GREEN + "", "   ");
		team.setSuffix("   ");
		objective.getScore("   ").setScore(2);

		ScoreboardUtil.getTeam(scoreboard, "WEBSITE", ChatColor.AQUA + "", "www.badlion.net");
		objective.getScore("www.badlion.net").setScore(1);
	}

	public int getPastTotalKills() {
		return this.pastTotalKills;
	}

	public void setPastTotalKills(int pastTotalKills) {
		this.pastTotalKills = pastTotalKills;
	}

	public int getPastTotalDeaths() {
		return this.pastTotalDeaths;
	}

	public void setPastTotalDeaths(int pastTotalDeaths) {
		this.pastTotalDeaths = pastTotalDeaths;
	}

}