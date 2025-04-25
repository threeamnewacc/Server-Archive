package us.zonix.practice.board;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import us.zonix.practice.Practice;

public class BoardManager implements Runnable {
    private final Map<UUID, Board> playerBoards = new HashMap<>();
    private final BoardAdapter adapter;

    @Override
    public void run() {
        this.adapter.preLoop();

        for (Player player : Practice.getInstance().getServer().getOnlinePlayers()) {
            Board board = this.playerBoards.get(player.getUniqueId());
            if (board != null) {
                try {
                    Scoreboard scoreboard = board.getScoreboard();
                    List<String> scores = this.adapter.getScoreboard(player, board);
                    if (scores != null) {
                        for (int i = 0; i < scores.size(); i++) {
                            if (scores.get(i) != null) {
                                scores.set(i, ChatColor.translateAlternateColorCodes('&', scores.get(i)));
                            }
                        }
                    }

                    if (scores != null) {
                        Collections.reverse(scores);
                        Objective objective = board.getObjective();
                        if (!objective.getDisplayName().equals(this.adapter.getTitle(player))) {
                            objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', this.adapter.getTitle(player)));
                        }

                        if (scores.isEmpty()) {
                            Iterator<BoardEntry> iter = board.getEntries().iterator();

                            while (iter.hasNext()) {
                                BoardEntry boardEntry = iter.next();
                                boardEntry.remove();
                                iter.remove();
                            }
                            continue;
                        }

                        label108:
                        for (int ix = 0; ix < scores.size(); ix++) {
                            String text = scores.get(ix);
                            int position = ix + 1;

                            for (BoardEntry boardEntry : new LinkedList<>(board.getEntries())) {
                                Score score = objective.getScore(boardEntry.getKey());
                                if (score != null && boardEntry.getText().equals(text) && score.getScore() == position) {
                                    continue label108;
                                }
                            }

                            Iterator<BoardEntry> iter = board.getEntries().iterator();

                            while (iter.hasNext()) {
                                BoardEntry boardEntryx = iter.next();
                                int entryPosition = scoreboard.getObjective(DisplaySlot.SIDEBAR).getScore(boardEntryx.getKey()).getScore();
                                if (entryPosition > scores.size()) {
                                    boardEntryx.remove();
                                    iter.remove();
                                }
                            }

                            int positionToSearch = position - 1;
                            BoardEntry entry = board.getByPosition(positionToSearch);
                            if (entry == null) {
                                new BoardEntry(board, text).send(position);
                            } else {
                                entry.setText(text).setup().send(position);
                            }

                            if (board.getEntries().size() > scores.size()) {
                                iter = board.getEntries().iterator();

                                while (iter.hasNext()) {
                                    BoardEntry boardEntryx = iter.next();
                                    if (!scores.contains(boardEntryx.getText())
                                        || Collections.frequency(board.getBoardEntriesFormatted(), boardEntryx.getText()) > 1) {
                                        boardEntryx.remove();
                                        iter.remove();
                                    }
                                }
                            }
                        }
                    } else if (!board.getEntries().isEmpty()) {
                        board.getEntries().forEach(BoardEntry::remove);
                        board.getEntries().clear();
                    }

                    this.adapter.onScoreboardCreate(player, scoreboard);
                    player.setScoreboard(scoreboard);
                } catch (Exception var14) {
                    var14.printStackTrace();
                }
            }
        }
    }

    public Map<UUID, Board> getPlayerBoards() {
        return this.playerBoards;
    }

    public BoardAdapter getAdapter() {
        return this.adapter;
    }

    public BoardManager(BoardAdapter adapter) {
        this.adapter = adapter;
    }
}
