package us.zonix.core.board;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

@Getter
@Accessors(chain = true)
public class BoardEntry {

	private final Board board;

	private final String originalText;

	private Team team;

	@Setter
	private String text;
	private String key;

	public BoardEntry(Board board, String text) {
		this.board = board;
		this.text = text;
		this.originalText = text;
		this.key = board.getNewKey(this);

		this.setup();
	}

	public BoardEntry setup() {
		Scoreboard scoreboard = this.board.getScoreboard();

		String teamName = this.key;
		if (teamName.length() > 16) {
			teamName = teamName.substring(0, 16);
		}

		if (scoreboard.getTeam(teamName) != null) {
			this.team = scoreboard.getTeam(teamName);
		} else {
			this.team = scoreboard.registerNewTeam(teamName);
		}

		if (!(this.team.getEntries().contains(this.key))) {
			this.team.addEntry(this.key);
		}

		if (!(this.board.getEntries().contains(this))) {
			this.board.getEntries().add(this);
		}

		return this;
	}

	public BoardEntry send(int position) {
		Objective objective = board.getObjective();

		if (this.text.length() > 16) {
			this.team.setPrefix(this.text.substring(0, 16));

			boolean addOne = this.team.getPrefix().endsWith(ChatColor.COLOR_CHAR + "");

			if (addOne) {
				this.team.setPrefix(this.text.substring(0, 15));
			}

			String suffix = ChatColor.getLastColors(this.team.getPrefix())
					+ this.text.substring(addOne ? 15 : 16, this.text.length());

			if (suffix.length() > 16) {
				if (suffix.length() - 2 <= 16) {
					suffix = this.text.substring(addOne ? 15 : 16, this.text.length());
					this.team.setSuffix(suffix.substring(0, suffix.length()));
				} else {
					this.team.setSuffix(suffix.substring(0, 16));
				}

			} else {
				this.team.setSuffix(suffix);
			}
		} else {
			this.team.setSuffix("");
			this.team.setPrefix(this.text);
		}

		Score score = objective.getScore(this.key);
		score.setScore(position);

		return this;
	}

	public void remove() {
		this.board.getKeys().remove(this.key);
		this.board.getScoreboard().resetScores(this.key);
	}

}