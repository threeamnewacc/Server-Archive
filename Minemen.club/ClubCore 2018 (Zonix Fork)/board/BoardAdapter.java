package us.zonix.core.board;

import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

public interface BoardAdapter {

	List<String> getScoreboard(Player player, Board board);

	String getTitle(Player player);

	long getInterval();

	void onScoreboardCreate(Player player, Scoreboard board);

	void preLoop();

}