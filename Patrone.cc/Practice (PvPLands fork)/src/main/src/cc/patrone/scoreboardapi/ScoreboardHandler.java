package cc.patrone.scoreboardapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardHandler {
	private static final String OBJECTIVE_ID = "objective";

	public void updateScoreboard(Player player) {
		Scoreboard scoreboard = player.getScoreboard();
		Objective objective = scoreboard.getObjective(OBJECTIVE_ID);

		if (objective == null) {
			objective = scoreboard.registerNewObjective(OBJECTIVE_ID, "dummy");

			objective.setDisplayName("");
			objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		}

		PlayerScoreboardUpdateEvent event = new PlayerScoreboardUpdateEvent(player, objective.getDisplayName());

		Bukkit.getPluginManager().callEvent(event);

		if (!objective.getDisplayName().equals(event.getTitle())) {
			objective.setDisplayName(event.getTitle());
		}

		List<ScoreboardLine> lines = event.getLines();

		if (lines.size() > 0) {
			if (!event.getHeader().isEmpty()) {
				event.insertLine(0, event.getHeader());
			}

			if (!event.getFooter().isEmpty()) {
				event.writeLine(event.getFooter());
			}
		}

		List<Team> teams = new ArrayList<>();

		for (int i = 0; i < ChatColor.values().length; i++) {
			if (scoreboard.getTeam("#line-" + i) == null) {
				scoreboard.registerNewTeam("#line-" + i);
			}

			teams.add(scoreboard.getTeam("#line-" + i));
		}

		for (int i = 0; i < lines.size(); i++) {
			Team team = teams.get(i);

			ScoreboardLine line = event.getLine(i);

			String prefix = line.getPrefix();
			String suffix = line.getSuffix();

			if (!team.getPrefix().equals(prefix)) {
				team.setPrefix(prefix);
			}

			if (!team.getSuffix().equals(suffix)) {
				team.setSuffix(line.getSuffix());
			}

			String entry = ChatColor.values()[i] + line.getPrefixFinalColor();
			Set<String> entries = team.getEntries();

			if (entries.size() == 0) {
				team.addEntry(entry);
				objective.getScore(entry).setScore(lines.size() - i);
			} else if (entries.size() == 1) {
				String already = entries.iterator().next();

				if (!entry.equals(already)) {
					scoreboard.resetScores(already);
					team.removeEntry(already);
					team.addEntry(entry);
					objective.getScore(entry).setScore(lines.size() - i);
				} else {
					objective.getScore(already).setScore(lines.size() - i);
				}
			}
		}

		for (int i = lines.size(); i < ChatColor.values().length; i++) {
			Team team = teams.get(i);
			Set<String> entries = team.getEntries();

			if (entries.size() > 0) {
				for (String entry : entries) {
					scoreboard.resetScores(entry);
					team.removeEntry(entry);
				}
			}
		}
	}
}
