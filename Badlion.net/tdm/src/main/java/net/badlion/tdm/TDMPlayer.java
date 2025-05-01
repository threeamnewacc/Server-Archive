package net.badlion.tdm;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.ScoreboardUtil;
import net.badlion.mpg.MPG;
import net.badlion.mpg.MPGPlayer;
import net.badlion.mpg.MPGTeam;
import net.badlion.mpg.managers.MPGKitManager;
import net.badlion.mpg.managers.MPGTeamManager;
import net.badlion.mpg.tasks.GameTimeTask;
import net.badlion.smellymapvotes.VoteManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.UUID;


public class TDMPlayer extends MPGPlayer {

	private boolean destructionScore = false;
	private boolean deathmatchScore = false;

	public TDMPlayer(UUID uuid, String username) {
		super(uuid, username);
	}

	@Override
	public void giveWhitelistSlots() {

	}

	public void handleDeathban() {

	}

	public void handleNewPlayer() {
		Player player = this.getPlayer();
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);

		player.setHealth(20);
		player.setFoodLevel(20);
		player.setFireTicks(0);

		for (PotionEffect pe : getPlayer().getActivePotionEffects()) {
			player.removePotionEffect(pe.getType());
		}
	}

	@Override
	public void handlePlayerDeathInternal() {
		super.handlePlayerDeathInternal();
	}

	public void handleSpectatorToPlayer() {

	}

	public void handleAliveToDeadPlayer(Player killer) {

	}

	public void handleLastAlivePlayerDisconnect() {

	}

	private void sendMessagesCommon() {
		Player player = this.getPlayer();
		if (player != null) {      // TODO: HOW ARE WE DOING LOBBIES?
			this.getPlayer().sendMessage(Gberry.getLineSeparator(org.bukkit.ChatColor.GOLD));
			VoteManager.sendVoteMessage(this.getPlayer(), TDM.getInstance().getCurrentGame().getGWorld().getInternalName());
			this.getPlayer().sendMessage("");
			BaseComponent[] components = new ComponentBuilder("Go back to the")
					.color(ChatColor.BLUE)
					.append("[SkyWars Lobby]")
					.color(ChatColor.GOLD)
					.append("Click to go back")
					.color(ChatColor.GOLD)
					.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
							new ComponentBuilder("Click to go back")
									.color(ChatColor.GOLD)
									.create()))
					.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
							"/server " + MPG.SERVER_ON_END))
					.append(" or ")
					.color(ChatColor.BLUE)
					.append("[Play Again]")
					.color(ChatColor.BLUE)
					.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
							new ComponentBuilder("Click to play again")
									.color(ChatColor.GOLD)
									.create()))
					.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
							"/playagain"))
					.create();
			this.getPlayer().spigot().sendMessage(components);
			this.getPlayer().sendMessage(Gberry.getLineSeparator(org.bukkit.ChatColor.GOLD));
		}
	}

	public void sendDeathMessages() {
		this.sendMessagesCommon();
	}

	public void sendWinnerMessages() {
		this.sendMessagesCommon();
	}

	public enum SCOREBOARD_ENTRIES {
		GAME_TIME, TEAM, KILL_STREAK, KILLS, DEATHS, ASSISTS, COUNTER, WEBSITE
	}

	@Override
	public Location handlePlayerRespawn(Player player) {
		MPGKitManager.loadKit(player, this.kit);

		// TODO: NO INSTANT RESPAWNS RIGHT?
		return null;
	}

	@Override
	public void initializeScoreboard() {
		final Player player = TDM.getInstance().getServer().getPlayer(this.uuid);
		if (player == null) {
			return;
		}

		// Initialize
		if (player.getScoreboard().equals(TDM.getInstance().getServer().getScoreboardManager().getMainScoreboard())) {
			ScoreboardUtil.getNewScoreboard(player);
		}

		Scoreboard scoreboard = player.getScoreboard();
		Objective objective = ScoreboardUtil.getObjective(player.getScoreboard(), "tdm", DisplaySlot.SIDEBAR, ChatColor.AQUA + "Badlion TDM");

		// Setup team scoreboards
		int counter = 8;
		for (MPGTeam mpgTeam : MPGTeamManager.getAllMPGTeams()) {
			Team team = ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + mpgTeam.getColor().name(), mpgTeam.getColor() + "", "");

			for (UUID uuid : mpgTeam.getUUIDs()) {
				Player pl = MPG.getInstance().getServer().getPlayer(uuid);

				// Add everyone to player's scoreboard
				team.addEntry(pl.getName());

				// Add player to everyone's scoreboard
				Team team2 = ScoreboardUtil.getTeam(pl.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + this.team.getColor().name(), this.team.getColor() + "", "");
				team2.addEntry(player.getName());

			}

			// Side scoreboard
			ScoreboardUtil.getTeam(player.getScoreboard(), mpgTeam.getTeamName() + SCOREBOARD_ENTRIES.TEAM, "", mpgTeam.getColor() + "Points: " + ChatColor.WHITE).setSuffix(mpgTeam.getKills() + "");

			objective.getScore(mpgTeam.getColor() + "Points: " + ChatColor.WHITE).setScore(counter);

			counter++;
		}

		ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + SCOREBOARD_ENTRIES.GAME_TIME.name(), ChatColor.GOLD + "Time", ChatColor.GOLD + " Left: " + ChatColor.WHITE).setSuffix(GameTimeTask.getInstance().getGameTime());
		ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + SCOREBOARD_ENTRIES.KILL_STREAK.name(), ChatColor.GOLD + "Kill", ChatColor.GOLD + " Streak: " + ChatColor.WHITE).setSuffix("0");
		ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + SCOREBOARD_ENTRIES.KILLS.name(), "", ChatColor.GOLD + "Kills: " + ChatColor.WHITE).setSuffix(this.team.getKills() + "");
		ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + SCOREBOARD_ENTRIES.DEATHS.name(), ChatColor.GOLD + "Dea", "ths: " + ChatColor.WHITE).setSuffix("0");
		ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + SCOREBOARD_ENTRIES.ASSISTS.name(), ChatColor.GOLD + "Ass", "ists: " + ChatColor.WHITE).setSuffix("0");
		ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + SCOREBOARD_ENTRIES.COUNTER.name(), ChatColor.GOLD + "Cou", "nter: " + ChatColor.WHITE).setSuffix("0%");
		ScoreboardUtil.getTeam(scoreboard, SCOREBOARD_ENTRIES.WEBSITE.name(), ChatColor.AQUA + "", "www.badlion.net");

		objective.getScore(ChatColor.GOLD + " Left: " + ChatColor.WHITE).setScore(MPG.getInstance().getIntegerConfigOption(MPG.ConfigFlag.NUM_OF_TEAMS) + 11);
		objective.getScore(ChatColor.GOLD + " Streak: " + ChatColor.WHITE).setScore(8);
		objective.getScore(ChatColor.GOLD + "Kills: " + ChatColor.WHITE).setScore(7);
		objective.getScore("ths: " + ChatColor.WHITE).setScore(6);
		objective.getScore("ists: " + ChatColor.WHITE).setScore(4);
		objective.getScore("nter: " + ChatColor.WHITE).setScore(3);
		objective.getScore("www.badlion.net").setScore(1);


		// Spacers
		ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "sp1", "", ScoreboardUtil.SAFE_TEAM_PREFIX + " ");
		ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "sp2", "", ScoreboardUtil.SAFE_TEAM_PREFIX + "  ");
		ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "sp3", "", ScoreboardUtil.SAFE_TEAM_PREFIX + "   ");

		objective.getScore(" ").setScore(MPG.getInstance().getIntegerConfigOption(MPG.ConfigFlag.NUM_OF_TEAMS) + 8);
		objective.getScore("  ").setScore(7);
		objective.getScore("   ").setScore(2);
	}

	@Override
	public void update() {
		final Player player = TDM.getInstance().getServer().getPlayer(this.uuid);
		if (player == null) {
			return;
		}
		                          // TODO: FIX ASSISTS AND THEN FIX SCOREBOARD PART
		ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "time", ChatColor.GOLD + "Time", ChatColor.GOLD + " Left: " + ChatColor.WHITE).setSuffix(GameTimeTask.getInstance().getGameTime());
		ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "ks", ChatColor.GOLD + "Kill", ChatColor.GOLD + " Streak: " + ChatColor.WHITE).setSuffix(this.getCurrentKillStreak() + "");
		ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "kls", "", ChatColor.GOLD + "Kills: " + ChatColor.WHITE).setSuffix(this.team.getKills() + "");
		ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "dths", ChatColor.GOLD + "Dea", "ths: " + ChatColor.WHITE).setSuffix(this.getDeaths() + "");
		ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "asst", ChatColor.GOLD + "Ass", "ists: " + ChatColor.WHITE).setSuffix(this.getAssists() + "");
		ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "cntr", ChatColor.GOLD + "Cou", "nter: " + ChatColor.WHITE).setSuffix("0%");
	}

	@Override
	public String toString() {
		return this.username + " - " + this.playerState.name() + " - " + this.team.getDeaths();
	}

}
