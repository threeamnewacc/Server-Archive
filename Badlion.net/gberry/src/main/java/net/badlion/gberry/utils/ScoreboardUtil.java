package net.badlion.gberry.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardUtil {

    public static final String DEFAULT_TEAM_NAME = "§§§§§§§§§§§§";
    public static final String BLUE_TEAM = "§§blueteam";
    public static final String RED_TEAM = "§§redteam";
    public static final String SAFE_TEAM_PREFIX = "§§";
    private static Team defaultTeam = null;

    public static void initialize() {
        // Force remove this old DEFAULT_TEAM_NAME (bug should never have been added)
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team team = scoreboard.getTeam(ScoreboardUtil.DEFAULT_TEAM_NAME);
        if (team != null) {
            team.unregister();
        }
    }

    public static void resetScoreboardWithoutDefaultPlayers(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        ScoreboardUtil.addPlayerToDefaultTeam(player.getScoreboard(), player);
    }

    public static void resetScoreboard(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        Team team = ScoreboardUtil.addPlayerToDefaultTeam(player.getScoreboard(), player);

        // Have to add every player to this new scoreboard :'(
        for (Player pl : Bukkit.getServer().getOnlinePlayers()) {
            team.addPlayer(pl);
        }
    }

    // Bad idea because of tab list
    /*public static void cleanScoreboard(Player player) {
        Objective objective = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
        if (objective != null) {
            objective.unregister();
        }

        for (Team team : player.getScoreboard().getTeams()) {
            if (!team.getName().equals(ScoreboardUtil.DEFAULT_TEAM_NAME)) {
                team.unregister();
            }
        }
    }*/

    public static void removePlayerFromDefaultTeam(Player player) {
        ScoreboardUtil.defaultTeam.removePlayer(player);
    }

    private static void addPlayerToDefaultTeam(Player player) {
        ScoreboardUtil.defaultTeam.addPlayer(player);
    }

	public static Team addPlayerToDefaultTeam(Scoreboard scoreboard, Player player) {
		Team team = ScoreboardUtil.createDefaultTeam(scoreboard);
		team.addPlayer(player);
		return team;
	}

	public static Team addEntryToDefaultTeam(Scoreboard scoreboard, String entry) {
		Team team = ScoreboardUtil.createDefaultTeam(scoreboard);
		team.addEntry(entry);
		return team;
	}

    public static Team createDefaultTeam(Scoreboard scoreboard) {
        Team team = scoreboard.getTeam(ScoreboardUtil.DEFAULT_TEAM_NAME);
        if (team == null) {
            team = ScoreboardUtil.getTeam(scoreboard, ScoreboardUtil.DEFAULT_TEAM_NAME, "", ScoreboardUtil.DEFAULT_TEAM_NAME);
        }

        return team;
    }

    /**
     * Helper Functions
     */
    public static Scoreboard getNewScoreboard(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        return player.getScoreboard();
    }

    public static Objective getObjective(Scoreboard scoreboard, String objectiveName, DisplaySlot displaySlot, String displayName) {
        Objective objective = scoreboard.getObjective(objectiveName);
        if (objective == null) {
            objective = scoreboard.registerNewObjective(objectiveName, "dummy");
            objective.setDisplaySlot(displaySlot);

            if (displayName != null) {
                objective.setDisplayName(displayName);
            }
        }

        return objective;
    }

    public static Team getTeam(Scoreboard scoreboard, String teamName, String prefix, String name) {
        Team team = scoreboard.getTeam(teamName);
        if (team != null) {
            return team;
        }

        team = scoreboard.registerNewTeam(teamName);
        team.setPrefix(prefix);
        team.setDisplayName(name);
        team.addEntry(name);

        return team;
    }

	/**
	 * Caution: Does not update suffix if team is found
	 */
	public static Team getTeam(Scoreboard scoreboard, String teamName, String prefix, String name, String suffix) {
		Team team = scoreboard.getTeam(teamName);
		if (team != null) {
			return team;
		}

		team = scoreboard.registerNewTeam(teamName);
		team.setPrefix(prefix);
		team.setSuffix(suffix);
		team.setDisplayName(name);
		team.addEntry(name);

		return team;
	}

}
