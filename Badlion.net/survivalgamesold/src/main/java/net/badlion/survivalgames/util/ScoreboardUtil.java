package net.badlion.survivalgames.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardUtil {

    /**
     * Helper Functions
     */
    public static Scoreboard getScoreboard(Player player) {
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

    public static Team getTeam(Scoreboard scoreboard, String teamName, String prefix, String name, String suffix) {
        Team team = scoreboard.getTeam(teamName);
        if (team != null) {
            return team;
        }

        team = scoreboard.registerNewTeam(teamName);
        team.setPrefix(prefix);
        team.setDisplayName(name);
        team.setSuffix(suffix);
        team.addEntry(name);

        return team;
    }

}
