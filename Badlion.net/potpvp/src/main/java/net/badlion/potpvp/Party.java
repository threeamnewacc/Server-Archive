package net.badlion.potpvp;

import net.badlion.gberry.Gberry;
import net.badlion.gberry.utils.ScoreboardUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class Party {

    private Set<Player> players = new HashSet<>();
    private Player partyLeader;

    /**
     * This constructor should never be used for anything but storing copies of the players
     */
    public Party(Player player) {
        this.partyLeader = player;
        this.players.add(player);
    }

    public Party(Player player, boolean nothing) {
        this(player);

        // Add objective and teams
        this.handleAddObjective(player);
        this.handleAddTeam(player);
    }

    private void handleAddObjective(Player player) {
        Objective objective = player.getScoreboard().registerNewObjective("Party", "Party");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        Score score = objective.getScore(this.partyLeader.getName());
        score.setScore(2);
    }

    private void handleAddTeam(Player player) {
        Team team = player.getScoreboard().registerNewTeam(ScoreboardUtil.BLUE_TEAM);
        team.setPrefix(ChatColor.BLUE + "");
        team.addEntry(player.getName());
        player.getScoreboard().registerNewTeam(ScoreboardUtil.RED_TEAM).setPrefix(ChatColor.RED + "");
    }

    /**
     * Get list of players
     */
    public List<Player> getPlayers() {
        List<Player> list = new ArrayList<>();
        list.addAll(this.players);
        return Collections.unmodifiableList(list);
    }

    public void addPlayerPrivately(Player player) {
        this.players.add(player);
    }

    public void addPlayer(Player player) {
        this.players.add(player);
        Gberry.log("PARTY", "Adding player " + player.getName() + " to party (" + this.players.size() + " total)");

        // Add objective and teams
        this.handleAddObjective(player);
        this.handleAddTeam(player);

        // Handle team scoreboard stuff
        for (Player pl : this.players) {
            pl.getScoreboard().getTeam(ScoreboardUtil.BLUE_TEAM).addEntry(player.getName());
            player.getScoreboard().getTeam(ScoreboardUtil.BLUE_TEAM).addEntry(pl.getName());
            Score score = pl.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(player.getName());
            score.setScore(1);

            // Already added
            if (pl == this.partyLeader) {
                continue;
            }

            // Add existing members
            score = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(pl.getName());
            score.setScore(1);
        }
    }

    public boolean removePlayer(Player player) {
        // Remove from side bar for other players
        for (Player pl : this.players) {
            pl.getScoreboard().resetScores(player.getName());
            pl.getScoreboard().getTeam(ScoreboardUtil.DEFAULT_TEAM_NAME).addEntry(player.getName());
            pl.showPlayer(player);
            player.showPlayer(pl);
        }

        // Remove teams
        player.getScoreboard().getTeam(ScoreboardUtil.BLUE_TEAM).unregister();
        player.getScoreboard().getTeam(ScoreboardUtil.RED_TEAM).unregister();

        // Remove side objective
        player.getScoreboard().getObjective(DisplaySlot.SIDEBAR).unregister();

        Gberry.log("PARTY", "Removing player " + player.getName() + " from party.");
        return this.players.remove(player);
    }

    public Player getPartyLeader() {
        return partyLeader;
    }

    public void setPartyLeader(Player partyLeader) {
        // Update the scores for everyone in the party
        for (Player pl : this.players) {
            Score score = pl.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(this.partyLeader.getName());
            score.setScore(1);

            score = pl.getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(partyLeader.getName());
            score.setScore(2);
        }

        // Finally make the change
        this.partyLeader = partyLeader;
    }

}
