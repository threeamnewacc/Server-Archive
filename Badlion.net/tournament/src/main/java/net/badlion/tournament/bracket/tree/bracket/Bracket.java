package net.badlion.tournament.bracket.tree.bracket;

import net.badlion.tournament.bracket.SingleKnockoutBracket.AdvanceState;
import net.badlion.tournament.bracket.tree.Tree;
import net.badlion.tournament.bracket.filter.SeriesFilter;
import net.badlion.tournament.teams.Team;
import net.badlion.tournament.tournaments.Tournament;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface Bracket extends Tree {

    /**
     * Get the ID of the bracket
     */
    UUID getID();

    /**
     * Generate a list of nodes with series based on how many teams are present
     * @return if the bracket was successfully generated
     */
    boolean generate(List<Team> teams, boolean newTournament);

    /**
     * Moves a winning team up a level in the bracket
     * @return the state of the bracket
     */
    AdvanceState advance(SeriesNode node);

    boolean startSeries(SeriesNode node, boolean force);

    boolean endSeries(SeriesNode node, Team winningTeam);

    /**
     * Get the tournament the bracket is used in
     */
    Tournament getTournament();

    /**
     * Set the tournament the bracket is used in
     */
    void setTournament(Tournament tournament);

    /**
     * Get the root node
     */
    SeriesNode getRoot();

    /**
     * Set the root node
     */
    void setRoot(SeriesNode node);

    /**
     * Get all nodes from the Tree
     */
    List<SeriesNode> getNodes();

    /**
     * Set all nodes in the Tree
     */
    void setNodes(List<SeriesNode> nodes);

    /**
     * Get the amount of teams per series
     */
    int getTeamsPerMatch();

    /**
     * Get how many rounds a team has to win in order to win a series
     */
    int getDefaultRoundsToWin();

    /**
     * Get how many matches should be played for specific rounds.
     * The first integer stands for round, the second stands for how many matches.
     *
     * For example, putting a value of (0, 5) means that the finals match will be a best of five.
     * Putting a value of (2, 3) means the quarter finals, semi finals, and finals will be at least a best of three.
     */
    Map<Integer, Integer> getRoundsToWin();

    /**
     * Set the rounds required to win
     */
    void setRoundsToWin(Map<Integer, Integer> roundsToWin);

    /**
     * Get how many rounds a team has to win to advance at a specific round
     */
    int getRoundsToWin(int round);

    /**
     * Get a list of nodes that meet the category of the filter applied
     */
    List<SeriesNode> search(SeriesFilter filter);

    /**
     * Check if a node is active in its tournament
     * @return if the node is active
     */
    boolean isActive(SeriesNode node);

    /**
     * Get all nodes from the current round from the Tree
     */
    List<SeriesNode> getActiveNodes();

    /**
     * Set if the tournament advances to the next series when a series ends
     */
    void setAdvanceAutomatically(boolean advanceAutomatically);

    /**
     * Automatically advance to the next match when match ends
     * @return advance to the next match automatically
     */
    boolean advancesAutomatically();

    /**
     * See if all teams need to /ready in order to start a new series
     */
    boolean requiresReady();

    /**
     * Set if teams need to /ready to start a new series
     */
    void setRequiresReady(boolean requiresReady);

    /**
     * Mark a team as ready
     */
    boolean readyTeam(SeriesNode node, Team team);

    /**
     * Check if the bracket has been updated
     */
    boolean isEdited();

}
