package net.badlion.potpvp.events;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.ScoreboardUtil;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.bukkitevents.FollowedPlayerTeleportEvent;
import net.badlion.potpvp.bukkitevents.MessageEvent;
import net.badlion.potpvp.exceptions.OutOfArenasException;
import net.badlion.potpvp.helpers.PlayerHelper;
import net.badlion.potpvp.managers.*;
import net.badlion.potpvp.rulesets.KitRuleSet;
import net.badlion.potpvp.states.matchmaking.MatchMakingState;
import net.badlion.potpvp.tasks.EventTieTask;
import net.badlion.statemachine.IllegalStateTransitionException;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

public class Slaughter extends RefreshKitEvent {

    public static int SCORE_TO_WIN = 10;
	public static int RESPAWN_TIME = 5; // In seconds
	public static int RESISTANCE_TIME = 6; // In seconds

	private List<Location> spawnLocations = new ArrayList<>();

    public Slaughter(Player creator, ItemStack eventItem, KitRuleSet kitRuleSet, ItemStack[] armorContents, ItemStack[] inventoryContents) throws OutOfArenasException {
        super(creator, eventItem, kitRuleSet, EventType.SLAUGHTER, ArenaManager.ArenaType.SLAUGHTER);

	    this.armorContents = armorContents;
	    this.inventoryContents = inventoryContents;

        for (int i = 1; i < 21; i++) {
            this.spawnLocations.add(ArenaManager.getWarp(this.arena.getArenaName() + "-" + i));
        }

		this.maxPlayers = 10;
    }

    @Override
    public void startGame() {
		super.startGame();

	    for (Player player : this.players) {
		    // No EP glitches
		    EnderPearlManager.remove(player);

		    player.sendMessage(ChatColor.GOLD + "Welcome to Slaughter! First to " + Slaughter.SCORE_TO_WIN + " kills wins. Good luck.");

		    // Make their custom scoreboard
		    Objective objective = ScoreboardUtil.getObjective(player.getScoreboard(), "slaughter", DisplaySlot.SIDEBAR, ChatColor.AQUA + "Badlion Slaughter");

		    ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "ktw", ChatColor.GOLD + "Kills to ", ChatColor.GOLD + "Win: " + ChatColor.WHITE).setSuffix(Slaughter.SCORE_TO_WIN + "");
		    ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "kls", "", ChatColor.GOLD + "Kills: " + ChatColor.WHITE).setSuffix("0");
		    ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "dths", "", ChatColor.GOLD + "Deaths: " + ChatColor.WHITE).setSuffix("0");

		    objective.getScore(ChatColor.GOLD + "Win: " + ChatColor.WHITE).setScore(4);
		    objective.getScore(ChatColor.GOLD + "Kills: " + ChatColor.WHITE).setScore(2);
		    objective.getScore(ChatColor.GOLD + "Deaths: " + ChatColor.WHITE).setScore(1);

		    // Spacer
		    ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "sp1", "", " ");

		    objective.getScore(" ").setScore(3);

		    // Remove stuff we don't want them to have
		    PlayerHelper.healAndPrepPlayerForBattle(player);

		    Gberry.safeTeleport(player, this.getRandomSpawn());

		    // Load kit
		    this.refreshKit(player, false);

		    PotPvP.getInstance().getServer().getPluginManager().callEvent(new FollowedPlayerTeleportEvent(player));

		    try {
			    GroupStateMachine.matchMakingState.push(GroupStateMachine.slaughterState, PotPvP.getInstance().getPlayerGroup(player), this);
		    } catch (IllegalStateTransitionException e) {
			    PotPvP.getInstance().somethingBroke(player, PotPvP.getInstance().getPlayerGroup(player));
		    }

		    Gberry.log("SLAUGHTER", "Setup " + player.getName());
	    }

        // Start time limit task
        this.eventTieTask = new EventTieTask(this);
        this.eventTieTask.runTaskLater(PotPvP.getInstance(), 20 * 60 * this.getEventType().getMatchLength());
    }

	private void resetScoreboard(Player player) {
		ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "ktw", ChatColor.GOLD + "Kills to ", ChatColor.GOLD + "Win: " + ChatColor.WHITE).unregister();
		ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "kls", "", ChatColor.GOLD + "Kills: " + ChatColor.WHITE).unregister();
		ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "dths", "", ChatColor.GOLD + "Deaths: " + ChatColor.WHITE).unregister();
		ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "sp1", "", ScoreboardUtil.SAFE_TEAM_PREFIX + " ").unregister();

		player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).unregister();
	}

    @Override
    public void handleDeath(Player player) {
	    // Add death
	    Team team = ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "dths", "", ChatColor.GOLD + "Deaths: " + ChatColor.WHITE);
	    team.setSuffix(Integer.valueOf(team.getSuffix()) + 1 + "");

	    // Did they have a killer?
	    Player killer = player.getKiller();
	    if (killer != null) {
		    // Add kill
		    team = ScoreboardUtil.getTeam(killer.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "kls", "", ChatColor.GOLD + "Kills: " + ChatColor.WHITE);
		    int kills = Integer.valueOf(team.getSuffix()) + 1;
		    team.setSuffix(kills + "");

		    // Make sure killer is still in the slaughter
		    if (this.players.contains(killer)) {
			    // Did they reach the kill cap?
			    if (kills == Slaughter.SCORE_TO_WIN) {
				    this.winners.add(killer.getUniqueId());

				    this.handleEndGame(killer);
				    return;
			    } else if (this.players.size() == 1) { // Only one player left?
				    Player winner = this.players.get(0);
				    this.winners.add(winner.getUniqueId());

				    this.handleEndGame(winner);
				    return;
			    }
		    }

		    if (!killer.isDead()) {
			    this.refreshKit(killer, true);
		    }
	    } else if (this.players.size() == 1) { // Only one player left?
		    Player winner = this.players.get(0);
		    this.winners.add(winner.getUniqueId());

		    this.handleEndGame(winner);
	    }
    }

    @Override
    public Location handleRespawn(Player player) {
	    // Get a new spawn location
	    Location location = this.getRandomSpawn();

		RespawnManager.addPlayerRespawning(this, player, location, Slaughter.RESPAWN_TIME, Slaughter.RESISTANCE_TIME);

	    return location;
    }

    @Override
    public boolean handleQuit(Player player, String reason) {
	    this.players.remove(player);

	    StasisManager.addToStasis(PotPvP.getInstance().getPlayerGroup(player), new MatchMakingState.MatchStasisHandler());

	    MessageEvent messageEvent = new MessageEvent(MessageManager.MessageType.EVENT_MESSAGES,
			    ChatColor.GOLD + player.getName() + " is a wimp and quit Slaughter because he/she sucks at PvP.", null, this.players);
	    PotPvP.getInstance().getServer().getPluginManager().callEvent(messageEvent);

	    this.handleDeath(player);

	    // No scoreboard
		player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).unregister();

	    Group group = PotPvP.getInstance().getPlayerGroup(player);
	    try {
		    GroupStateMachine.transitionBackToDefaultState(GroupStateMachine.getInstance().getCurrentState(group), group);
	    } catch (IllegalStateTransitionException e) {
		    PotPvP.getInstance().somethingBroke(player, group);
	    }

	    Gberry.log("LMS", "Removing player " + player + " from GroupGame");

	    return false;
    }

	public void handleEndGame(Player winner) {
		int winnerKills = this.getKills(winner);
		String winMessage;
		if (winnerKills > 0) {
			if (winnerKills > 1) {
				winMessage = ChatColor.YELLOW + winner.getName() + ChatColor.GOLD + " has won the Slaughter by reaching "
						+ ChatColor.YELLOW + Slaughter.SCORE_TO_WIN + ChatColor.GOLD + " kills!";
			} else {
				winMessage = ChatColor.YELLOW + winner.getName() + ChatColor.GOLD + " has won the Slaughter by reaching "
						+ ChatColor.YELLOW + "1" + ChatColor.GOLD + " kill!";
			}
		} else {
			winMessage = ChatColor.YELLOW + winner.getName() + ChatColor.GOLD + " has won the Slaughter with "
					+ ChatColor.YELLOW + 0 + ChatColor.GOLD + " kills by being the last player in the Slaughter!";
		}

		for (Player player : this.players) {
			StasisManager.addToStasis(PotPvP.getInstance().getPlayerGroup(player), new MatchMakingState.MatchStasisHandler());

			player.sendMessage(winMessage);

			Group group = PotPvP.getInstance().getPlayerGroup(player);
			try {
				GroupStateMachine.transitionBackToDefaultState(GroupStateMachine.getInstance().getCurrentState(group), group);
			} catch (IllegalStateTransitionException e) {
				PotPvP.getInstance().somethingBroke(player, group);
			}

			Gberry.log("LMS", "Removing player " + player + " from GroupGame");
		}

		this.endGame(false);

		for (Player player : this.players) {
			// Reset scoreboard last because endGame() uses it
			this.resetScoreboard(player);
		}
	}

	public int getKills(Player player) {
		return Integer.valueOf(ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "kls", "", ChatColor.GOLD + "Kills: " + ChatColor.WHITE).getSuffix());
	}

	public int getDeaths(Player player) {
		return Integer.valueOf(ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "dths", "", ChatColor.GOLD + "Deaths: " + ChatColor.WHITE).getSuffix());
	}


	private Location getRandomSpawn() {
		Location location;

		do {
			location = this.spawnLocations.get(Gberry.generateRandomInt(0, 19));
		} while (location == null);

		return location;
	}

}
