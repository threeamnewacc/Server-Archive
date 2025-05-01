package net.badlion.tournament.bracket;

import net.badlion.tournament.TournamentPlugin;
import net.badlion.tournament.bracket.tree.bracket.Bracket;
import net.badlion.tournament.bracket.tree.bracket.SeriesNode;
import net.badlion.tournament.bracket.filter.SeriesFilter;
import net.badlion.tournament.events.bracket.MatchEndEvent;
import net.badlion.tournament.events.bracket.SeriesStartEvent;
import net.badlion.tournament.events.team.TeamReadyEvent;
import net.badlion.tournament.events.tournament.TournamentStartEvent;
import net.badlion.tournament.matches.Series;
import net.badlion.tournament.teams.Team;
import net.badlion.tournament.tournaments.Tournament;

import java.util.*;

public class SingleKnockoutBracket implements Bracket {

    private UUID id;
    private Tournament tournament;
    private SeriesNode root;
    private int teamsPerMatch = 2;
    private int defaultRoundsToWin = 1;
    private Map<Integer, Integer> roundsToWin = new HashMap<>();
    private List<SeriesNode> nodes = new ArrayList<>();
    private boolean advanceAutomatically = true;
    private boolean requiresReady = false;
    private boolean edited = false;

    public SingleKnockoutBracket(UUID id, int teamsPerMatch, int defaultRoundsToWin, Map<Integer, Integer> roundsToWin, boolean edited) {
        this.id = id;
        this.teamsPerMatch = teamsPerMatch;
        this.defaultRoundsToWin = defaultRoundsToWin;
        this.roundsToWin = roundsToWin;
        this.setEdited(edited);
    }

    public SingleKnockoutBracket(UUID id, Tournament tournament, int teamsPerMatch, int defaultRoundsToWin, Map<Integer, Integer> roundsToWin) {
        this.id = id;
        this.tournament = tournament;
        this.teamsPerMatch = teamsPerMatch;
        this.defaultRoundsToWin = defaultRoundsToWin;
        this.roundsToWin = roundsToWin;
        this.setEdited(true);
    }

    @Override
    public UUID getID() {
        return id;
    }

    public boolean generate(List<Team> teams, boolean newTournament) {

        if (teams.size() <= 1) {
            return false;
        }

        int amountOfRounds = (int)Math.round((double)teams.size()/2);
        int treeSize = this.getTwoFactor(amountOfRounds, 0);
        this.setRoot(new SeriesNode(UUID.randomUUID(), new Series(UUID.randomUUID(), this.getRoundsToWin(0)), this, null, treeSize + 1));

        // Add children to all nodes for the amount of series there will be
        for (int i = 1; i <= treeSize; i++) {
            this.addSeriesNodes(treeSize - i + 1, i);
        }

        // Shuffle and assign teams to the series generated
        this.shuffle(teams);
        for (Team team : teams) {
            for (SeriesNode node : this.getEndNodes()) {
                if (node.getContent().getTeams().size() < this.getTeamsPerMatch()) {
                    node.getContent().addTeam(team, true);
                    break;
                }
            }
        }

        if (newTournament) {
            TournamentPlugin.getInstance().getServer().getPluginManager().callEvent(new TournamentStartEvent(this.getTournament()));
        }

        for (SeriesNode node : this.getActiveNodes()) {
            this.startSeries(node, false);
        }

        return true;
    }

    public AdvanceState advance(SeriesNode node) {

        if (this.getRoot().getContent().getWinningTeam() != null) {
            return AdvanceState.BRACKET_END;
        }

        if (node.getContent().getWinningTeam() != null) {
            Team winningTeam = node.getContent().getWinningTeam();
            Series parentContent = node.getParent().getContent();
            parentContent.addTeam(winningTeam, true);

            if (parentContent.getTeams().size() >= this.getTeamsPerMatch()) {
                startSeries(node.getParent(), false);
            }

        } else {
            return AdvanceState.FAIL;
        }

        return AdvanceState.SUCCESS;
    }

    public enum AdvanceState {

        SUCCESS(),
        FAIL(),
        BRACKET_END(),
        BRACKET_SILENT_END();

        AdvanceState() {

        }

    }

    public boolean startSeries(SeriesNode node, boolean force) {
        Series series = node.getContent();

        if (!series.allReady() && this.requiresReady()) {
            return false;
        }

        if (this.advancesAutomatically() || force) {
            TournamentPlugin.getInstance().getServer().getPluginManager().callEvent(new SeriesStartEvent(this.getTournament(), node));
            if (series.getTeams().size() == 2) {
                if (series.getRounds().size() > 0) {
                    TournamentPlugin.getInstance().getServer().getPluginManager().callEvent(new MatchEndEvent(tournament, node, series.getRounds().get(series.getRounds().size() - 1)));
                }
                if (series.getTeams().get(0).getLeader() == null) {
                    this.endSeries(node, series.getTeams().get(1));
                } else if (series.getTeams().get(1).getLeader() == null) {
                    this.endSeries(node, series.getTeams().get(0));
                }
            }
            series.setStarted(true, true);
            return true;
        }
        return false;
    }

    public boolean endSeries(SeriesNode node, Team winningTeam) {

        if (!this.isActive(node)) {
            return false;
        }

        Series series = node.getContent();

        series.setPoints(winningTeam, node.getContent().getRoundsToWin(), true);
        TournamentPlugin.getInstance().getServer().getPluginManager().callEvent(new MatchEndEvent(this.getTournament(), node, series.getRounds().get(series.getRounds().size() - 1), winningTeam));
        return true;
    }

    public Tournament getTournament() {
        return tournament;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public SeriesNode getRoot() {
        return root;
    }

    public void setRoot(SeriesNode root) {
        this.root = root;
    }

    public List<SeriesNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<SeriesNode> nodes) {
        this.nodes = nodes;
    }

    public int getTeamsPerMatch() {
        return teamsPerMatch;
    }

    public int getDefaultRoundsToWin() {
        return defaultRoundsToWin;
    }

    public Map<Integer, Integer> getRoundsToWin() {
        return roundsToWin;
    }

    public void setRoundsToWin(Map<Integer, Integer> roundsToWin) {
        this.roundsToWin = roundsToWin;
    }

    public int getRoundsToWin(int currentRound) {

        if (this.getRoundsToWin().get(currentRound) != null) {
            return this.getRoundsToWin().get(currentRound);
        }

        Integer roundsToWin = this.getDefaultRoundsToWin();
        for (Integer round : this.getRoundsToWin().keySet()) {
            if (roundsToWin < this.getRoundsToWin().get(round) && currentRound < round) {
                roundsToWin = this.getRoundsToWin().get(round);
            }
        }

        return roundsToWin;
    }

    public List<SeriesNode> search(SeriesFilter filter) {
        return filter.filter();
    }

    /**
     * Shuffles the teams. Can be overwritten to sort by seed, rankings, etc.
     */
    public void shuffle(List<Team> teams) {
        Collections.shuffle(teams);
    }

    /**
     * Gets the smallest factor of two that an integer is less or equal than
     */
    public int getTwoFactor(int i, int currentFactor) {

        if (i > Math.pow(2, currentFactor)) {
            return this.getTwoFactor(i, currentFactor + 1);
        }

        return currentFactor;
    }

    public void addSeriesNodes(SeriesNode node) {
        getRoot().getChildren().add(node);
    }

    public void addSeriesNodes(int series, int roundsToWin) {
        List<SeriesNode> nodes = new ArrayList<>(this.getNodes());

        for (SeriesNode node : nodes) {
            if (node.isLeaf() || (node.isRoot() && !node.hasChildren())) {
                this.addSeriesNodes(node, series, roundsToWin);
            }
        }
    }

    /**
     * Adds ChildSeriesNodes a SeriesNode
     */
    public void addSeriesNodes(SeriesNode parent, int series, int roundsToWin) {
        for (int i = 1; i <= this.getTeamsPerMatch(); i++) {
            SeriesNode node = new SeriesNode(UUID.randomUUID(), new Series(UUID.randomUUID(), getRoundsToWin(roundsToWin)), this, parent, series);
            parent.getChildren().add(node);
        }
    }

    public boolean isActive(SeriesNode node) {
        boolean parentHasChildTeam = false;

        if (node.getParent() != null) {
            for (Team team : node.getParent().getContent().getTeams()) {
                if (node.getContent().getTeams().contains(team)) {
                    parentHasChildTeam = true;
                }
            }
        }

        return (node.getContent().getTeams().size() >= this.getTeamsPerMatch())
                && ((node.getParent() != null && !parentHasChildTeam && node.getParent().getContent().getTeams().size() <= this.getTeamsPerMatch())
                || (node.isRoot() && node.getContent().getWinningTeam() == null));
    }

    /**
     * Gets all series with teams that are in the same series as the current bracket series
     */
    public List<SeriesNode> getActiveNodes() {
        List<SeriesNode> activeNodes = new ArrayList<>();

        for (SeriesNode node : this.getNodes()) {
            if (this.isActive(node))  {
                activeNodes.add(node);
            }
        }

        return activeNodes;
    }

    /**
     * Gets all nodes that are on the end of a bracket
     */
    public List<SeriesNode> getEndNodes() {

        if (!this.getRoot().hasChildren()) {
            return Collections.singletonList(this.getRoot());
        }

        List<SeriesNode> endNodes = new ArrayList<>();

        for (SeriesNode node : this.getNodes()) {
            if (node.isLeaf()) {
                endNodes.add(node);
            }
        }

        return endNodes;
    }

    public boolean advancesAutomatically() {
        return advanceAutomatically;
    }

    public void setAdvanceAutomatically(boolean advanceAutomatically) {
        this.advanceAutomatically = advanceAutomatically;
    }

    public boolean requiresReady() {
        return requiresReady;
    }

    public void setRequiresReady(boolean requiresReady) {
        this.requiresReady = requiresReady;
    }

    public boolean readyTeam(SeriesNode node, Team team) {
        Series series = node.getContent();

        if (!series.getTeams().contains(team)) {
            return false;
        }

        series.setReady(team);

        if (series.allReady() && !series.isStarted()) {
            startSeries(node, false);
        }

        TournamentPlugin.getInstance().getServer().getPluginManager().callEvent(new TeamReadyEvent(this.getTournament(), node, team));
        return true;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }
}
