package net.badlion.potpvp.events;

import net.badlion.common.libraries.StringCommon;
import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.ScoreboardUtil;
import net.badlion.potpvp.Group;
import net.badlion.potpvp.GroupStateMachine;
import net.badlion.potpvp.PotPvP;
import net.badlion.potpvp.arenas.KOTHArena;
import net.badlion.potpvp.bukkitevents.FollowedPlayerTeleportEvent;
import net.badlion.potpvp.exceptions.OutOfArenasException;
import net.badlion.potpvp.helpers.PlayerHelper;
import net.badlion.potpvp.managers.ArenaManager;
import net.badlion.potpvp.managers.EnderPearlManager;
import net.badlion.potpvp.managers.RespawnManager;
import net.badlion.potpvp.managers.StasisManager;
import net.badlion.potpvp.rulesets.KitRuleSet;
import net.badlion.potpvp.states.matchmaking.MatchMakingState;
import net.badlion.statemachine.IllegalStateTransitionException;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class KOTH extends RefreshKitEvent {

    public static int GAME_TIME_LIMIT = 15 * 60; // seconds
    public static int NUM_OF_POINTS_TO_WIN = 200;
    public static int RESPAWN_TIME = 10; // In seconds
    public static int RESISTANCE_TIME = 6; // In seconds

    public static ChatColor[] TEAM_COLORS = new ChatColor[] { ChatColor.BLUE, ChatColor.RED, ChatColor.GREEN, ChatColor.GOLD };

    private Map<ChatColor, List<UUID>> totalTeams = new HashMap<>();
    private Map<UUID, ChatColor> teamToTeamColor = new HashMap<>();
    private Map<ChatColor, Integer> teamScores = new HashMap<>();

	private ChatColor winningTeam;

    public KOTH(Player creator, ItemStack eventItem, KitRuleSet kitRuleSet, ItemStack[] armorContents, ItemStack[] inventoryContents) throws OutOfArenasException {
        super(creator, eventItem, kitRuleSet, EventType.KOTH, ArenaManager.ArenaType.KOTH);

        this.armorContents = armorContents;
        this.inventoryContents = inventoryContents;

        for (ChatColor color : KOTH.TEAM_COLORS) {
            this.totalTeams.put(color, new ArrayList<UUID>());
            this.teamScores.put(color, 0);
        }

        this.minPlayers = 4;
        this.maxPlayers = 20;
    }

    @Override
    public void startGame() {
        super.startGame();

        // Assign everyone to a team
        int i = 0;
        for (Player player : this.players) {
	        ChatColor team = KOTH.TEAM_COLORS[i % KOTH.TEAM_COLORS.length];
            this.totalTeams.get(team).add(player.getUniqueId());
            this.teamToTeamColor.put(player.getUniqueId(), team);
	        i++;
        }

        // Setup team scoreboards and such
        for (List<UUID> uuids : this.totalTeams.values()) {
            for (UUID uuid : uuids) {
                Player pl = PotPvP.getInstance().getServer().getPlayer(uuid);

                // Make their custom scoreboard
	            Objective objective = ScoreboardUtil.getObjective(pl.getScoreboard(), "koth", DisplaySlot.SIDEBAR, ChatColor.AQUA + "Badlion KOTH");

	            // Add everyone to this scoreboard
	            int counter = this.totalTeams.size() + 3;
	            for (ChatColor teamColor2 : this.totalTeams.keySet()) {
		            Team team = ScoreboardUtil.getTeam(pl.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + teamColor2.name(), teamColor2.toString(), "");

		            for (UUID uuid1 : this.totalTeams.get(teamColor2)) {
			            Player p2 = PotPvP.getInstance().getServer().getPlayer(uuid1);
			            team.addEntry(p2.getName());
		            }

		            // Side scoreboard
		            team = ScoreboardUtil.getTeam(pl.getScoreboard(), teamColor2.name() + "points", teamColor2 + StringCommon.cleanEnum(teamColor2.name()) + " ", teamColor2 + "Points: " + ChatColor.WHITE);
		            team.setSuffix("0");

		            objective.getScore(teamColor2 + "Points: " + ChatColor.WHITE).setScore(counter);

		            counter--;
	            }

	            // Setup rest of the scoreboard
	            Team team = ScoreboardUtil.getTeam(pl.getScoreboard(), pl.getName().hashCode() + "kls", "", ChatColor.GOLD + "Kills: " + ChatColor.WHITE);
	            team.setSuffix("0");
	            team = ScoreboardUtil.getTeam(pl.getScoreboard(), pl.getName().hashCode() + "dths", "", ChatColor.GOLD + "Deaths: " + ChatColor.WHITE);
	            team.setSuffix("0");

	            objective.getScore(ChatColor.GOLD + "Kills: " + ChatColor.WHITE).setScore(2);
	            objective.getScore(ChatColor.GOLD + "Deaths: " + ChatColor.WHITE).setScore(1);

	            // Spacer
	            ScoreboardUtil.getTeam(pl.getScoreboard(), pl.getName().hashCode() + "sp1", "", " ");

	            objective.getScore(" ").setScore(3);
            }
        }

        new ScoreTask().runTaskTimer(PotPvP.getInstance(), 20, 20);

        // Teleport our players
        for (Player player : this.players) {
            // No EP glitches
            EnderPearlManager.remove(player);

            player.sendMessage(ChatColor.GOLD + "Welcome to KOTH! First team to " + KOTH.NUM_OF_POINTS_TO_WIN + " points wins.");
            player.sendMessage(ChatColor.GOLD + "You gain points by standing on the hill un-contested (no other teams on it). Good luck!");

            // Remove stuff we don't want them to have
            PlayerHelper.healAndPrepPlayerForBattle(player);

            Gberry.safeTeleport(player, this.getArena().getSpawnLocation(this.teamToTeamColor.get(player.getUniqueId())));

            // Load kit
            this.refreshKit(player, false);

            PotPvP.getInstance().getServer().getPluginManager().callEvent(new FollowedPlayerTeleportEvent(player));

            try {
                GroupStateMachine.matchMakingState.push(GroupStateMachine.kothState, PotPvP.getInstance().getPlayerGroup(player), this);
            } catch (IllegalStateTransitionException e) {
                PotPvP.getInstance().somethingBroke(player, PotPvP.getInstance().getPlayerGroup(player));
            }

            Gberry.log("KOTH", "Setup " + player.getName());
        }
    }

    public boolean canHurt(Player attacker, Player defender) {
        return this.teamToTeamColor.get(attacker.getUniqueId()) != this.teamToTeamColor.get(defender.getUniqueId());
    }

    @Override
    public void handleDeath(Player player) {
	    Team team = ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "dths", "", ChatColor.GOLD + "Deaths: " + ChatColor.WHITE);
	    team.setSuffix(Integer.valueOf(team.getSuffix()) + 1 + "");

	    if (player.getKiller() != null) {
		    Player killer = player.getKiller();
		    team = ScoreboardUtil.getTeam(killer.getScoreboard(), killer.getName().hashCode() + "kls", "", ChatColor.GOLD + "Kills: " + ChatColor.WHITE);
		    team.setSuffix(Integer.valueOf(team.getSuffix()) + 1 + "");
	    }
    }

    private void removeTeams(Player player) {
        for (ChatColor teamColor2 : this.totalTeams.keySet()) {
            ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + teamColor2.name(), teamColor2.toString(), "").unregister();
            ScoreboardUtil.getTeam(player.getScoreboard(), teamColor2.name() + "points", teamColor2 + StringCommon.cleanEnum(teamColor2.name()) + " ", teamColor2 + "Points: " + ChatColor.WHITE);
        }

        ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "dths", "", ChatColor.GOLD + "Deaths: " + ChatColor.WHITE).unregister();
        ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "kls", "", ChatColor.GOLD + "Kills: " + ChatColor.WHITE).unregister();
        ScoreboardUtil.getTeam(player.getScoreboard(), ScoreboardUtil.SAFE_TEAM_PREFIX + "sp1", "", " ").unregister();

        player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).unregister();
    }

    @Override
    public boolean handleQuit(Player player, String reason) {
	    ChatColor color = this.teamToTeamColor.remove(player.getUniqueId());
        this.totalTeams.get(color).remove(player.getUniqueId());
        this.players.remove(player);

	    // Tell everyone that the nerd left
	    for (Player pl : this.players) {
		    pl.sendMessage(color + player.getName() + " has left the KOTH!");
	    }

	    // No scoreboard
        this.removeTeams(player);

        // Everyone left :(
        if (this.players.size() == 1) {
	        this.winningTeam = this.teamToTeamColor.get(this.players.get(0).getUniqueId());
	        KOTH.this.winners.addAll(KOTH.this.totalTeams.get(this.winningTeam));

	        this.endGame(false);
        }

        return true;
    }

    @Override
    public Location handleRespawn(Player player) {
        Location location = this.getArena().getSpawnLocation(this.teamToTeamColor.get(player.getUniqueId()));

        RespawnManager.addPlayerRespawning(this, player, location, KOTH.RESPAWN_TIME, KOTH.RESISTANCE_TIME);

        return location;
    }

    @Override
    public void endGame(boolean premature) {
	    super.endGame(premature);

        if (!premature) {
            String winMessage = this.winningTeam + "The " + StringCommon.cleanEnum(this.winningTeam.name()) + " team has won the KOTH!";

            for (Player player : this.players) {
                // Send win message
                player.sendMessage(winMessage);

                StasisManager.addToStasis(PotPvP.getInstance().getPlayerGroup(player), new MatchMakingState.MatchStasisHandler());

                // No scoreboard
                this.removeTeams(player);

                Group group = PotPvP.getInstance().getPlayerGroup(player);
                try {
                    GroupStateMachine.transitionBackToDefaultState(GroupStateMachine.getInstance().getCurrentState(group), group);
                } catch (IllegalStateTransitionException e) {
                    PotPvP.getInstance().somethingBroke(player, group);
                }

                Gberry.log("LMS", "Removing player " + player + " from GroupGame");
            }
        }
    }

    @Override
    public KOTHArena getArena() {
        return (KOTHArena) this.arena;
    }

    public void sendMessage(String msg) {
        for (Player player : this.players) {
            player.sendMessage(msg);
        }
    }

    private class ScoreTask extends BukkitRunnable {

        private ChatColor hillOwners = null;
        private boolean hillContested = false;
        private int numOfTicks = 0;

	    boolean firstPoint = true;

        @Override
        public void run() {
            // Been too long?
            if (++this.numOfTicks >= KOTH.GAME_TIME_LIMIT) {
                this.cancel();

                // Figure out who won
	            ChatColor winnerTeamColor = null;
                int highScore = -1;
                for (ChatColor color : KOTH.TEAM_COLORS) {
                    int score = KOTH.this.teamScores.get(color);
                    if (score > highScore) {
                        highScore = score;
                        winnerTeamColor = color;
                    }
                }

                // Add winners and end game
	            KOTH.this.winningTeam = winnerTeamColor;
                KOTH.this.winners.addAll(KOTH.this.totalTeams.get(winnerTeamColor));
                KOTH.this.endGame(false);

                return;
            }

            // Figure out if the hill is un-contested or not
	        ChatColor firstTeamColor = null;
            for (Player player : KOTH.this.players) {
                if (Gberry.isLocationInBetween(KOTH.this.getArena().getMinCorner(), KOTH.this.getArena().getMaxCorner(), player.getLocation())) {
	                ChatColor teamColor = KOTH.this.teamToTeamColor.get(player.getUniqueId());
                    if (firstTeamColor == null) {
                        firstTeamColor = teamColor;
                        this.hillOwners = firstTeamColor;
                    } else if (!teamColor.equals(firstTeamColor)) {
                        if (!this.hillContested) {
                            KOTH.this.sendMessage(ChatColor.AQUA + "Hill is now contested.");
                            this.hillContested = true;
                        }

                        return;
                    }
                }
            }

            // Made it this far, we don't have a contested hill if firstTeamColor is not null
            if (firstTeamColor != null) {
                if (this.hillContested) {
                    KOTH.this.sendMessage(this.hillOwners + this.hillOwners.name() + " now controls the hill.");

	                this.hillContested = false;
                } else if (this.firstPoint) {
	                KOTH.this.sendMessage(this.hillOwners + this.hillOwners.name() + " now controls the hill.");

	                this.firstPoint = false;
                }

                int points = KOTH.this.teamScores.get(this.hillOwners) + 1;
                KOTH.this.teamScores.put(this.hillOwners,  points);

                // Update their side scoreboards
                for (Player player : KOTH.this.players) {
                    Team team = ScoreboardUtil.getTeam(player.getScoreboard(), this.hillOwners.name() + "points", this.hillOwners + StringCommon.cleanEnum(this.hillOwners.name()) + " ", this.hillOwners + "Points: ");
                    team.setSuffix(Integer.valueOf(team.getSuffix()) + 1 + "");
                }

	            if (points >= KOTH.NUM_OF_POINTS_TO_WIN) {
		            KOTH.this.winningTeam = this.hillOwners;
		            KOTH.this.winners.addAll(KOTH.this.totalTeams.get(this.hillOwners));
		            KOTH.this.endGame(false);

		            this.cancel();
	            }
            } else {
	        	this.firstPoint = true;
            }
        }

    }

}
