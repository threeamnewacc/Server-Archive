package net.badlion.uhcmeetup;

import net.badlion.gberry.utils.ItemStackUtil;
import net.badlion.gberry.utils.ScoreboardUtil;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.managers.MPGPlayerManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Map;
import java.util.UUID;

public class UHCMeetupPlayer extends MPGPlayer {

	private int absorptionHearts = 0;
	private int goldenHeadsEaten = 0;
	private int goldenApplesEaten = 0;

	public UHCMeetupPlayer(UUID uuid, String username) {
		super(uuid, username);
	}

	@Override
	public void update() {
		final Player player = UHCMeetup.getInstance().getServer().getPlayer(this.uuid);

		// Are they offline?
		if (player == null) {
			return;
		}

		// Initialize
		if (player.getScoreboard().equals(UHCMeetup.getInstance().getServer().getScoreboardManager().getMainScoreboard())) {
			ScoreboardUtil.getNewScoreboard(player);
		}

		Scoreboard scoreboard = player.getScoreboard();
		Objective objective = ScoreboardUtil.getObjective(scoreboard, ChatColor.AQUA + "UHC Meetup", DisplaySlot.SIDEBAR, ChatColor.AQUA + "UHC Meetup");

		Team team = null;

		// Only show number of kills if they're not a spectator
		if (MPGPlayerManager.getMPGPlayer(player).getState() != PlayerState.SPECTATOR) {
			team = ScoreboardUtil.getTeam(scoreboard, "KILLS", ChatColor.GREEN + "Your ", "Kills: " + ChatColor.WHITE);
			team.setSuffix(this.getKills() + "");
			objective.getScore("Kills: " + ChatColor.WHITE).setScore(5);

			team = ScoreboardUtil.getTeam(scoreboard, "", ChatColor.GREEN + "", "");
			team.setSuffix("");
			objective.getScore("").setScore(4);
		}

		team = ScoreboardUtil.getTeam(scoreboard, "PLAYERS_LEFT", ChatColor.GREEN + "Players ", "Left: " + ChatColor.WHITE);
		team.setSuffix(MPGPlayerManager.getMPGPlayersByState(PlayerState.PLAYER).size() + "");
		objective.getScore("Left: " + ChatColor.WHITE).setScore(3);

		team = ScoreboardUtil.getTeam(scoreboard, " ", ChatColor.GREEN + "", " ");
		team.setSuffix(" ");
		objective.getScore(" ").setScore(2);

		ScoreboardUtil.getTeam(scoreboard, "WEBSITE", ChatColor.AQUA + "", "www.badlion.net");
		objective.getScore("www.badlion.net").setScore(1);
	}

	@Override
	public void handlePlayerDeathInternal(LivingEntity livingEntity, Map<String, Object> extraPayload) {
		super.handlePlayerDeathInternal(livingEntity, extraPayload);

		// Drop a Golden Head since we disable crafting benches
		livingEntity.getWorld().dropItemNaturally(livingEntity.getLocation(), ItemStackUtil.createGoldenHead());
	}

	public int getAbsorptionHearts() {
		return this.absorptionHearts;
	}

	public int getGoldenHeadsEaten() {
		return this.goldenHeadsEaten;
	}

	public int getGoldenApplesEaten() {
		return this.goldenApplesEaten;
	}

}