package net.badlion.tournament.bracket;

import net.badlion.tournament.TournamentPlugin;
import net.badlion.tournament.bracket.filter.SeriesIDFilter;
import net.badlion.tournament.bracket.filter.WinFilter;
import net.badlion.tournament.bracket.tree.bracket.Bracket;
import net.badlion.tournament.bracket.tree.bracket.SeriesNode;
import net.badlion.tournament.events.bracket.MatchEndEvent;
import net.badlion.tournament.events.tournament.TournamentStartEvent;
import net.badlion.tournament.matches.Round;
import net.badlion.tournament.matches.Series;
import net.badlion.tournament.teams.DefaultTeam;
import net.badlion.tournament.teams.Team;
import net.badlion.tournament.tournaments.Tournament;

import java.util.*;

public class RoundRobinBracket extends SingleKnockoutBracket implements Bracket {

    private List<Group> groups = new ArrayList<>();
    private int groupSize = 3;
    private int amountOfWinningTeams = 2;
    private int series = 1;

    public RoundRobinBracket(UUID id, int teamsPerMatch, int defaultRoundsToWin, Map<Integer, Integer> roundsToWin) {
        super(id, teamsPerMatch, defaultRoundsToWin, roundsToWin, false);
    }

    public RoundRobinBracket(UUID id, Tournament tournament, int teamsPerMatch, int defaultRoundsToWin, Map<Integer, Integer> roundsToWin, int groupSize) {
        super(id, tournament, teamsPerMatch, defaultRoundsToWin, roundsToWin);
        this.setGroupSize(groupSize);
    }

    @Override
    public boolean generate(List<Team> teams, boolean newTournament) {

        if (teams.size() <= 1) {
            return false;
        }

        int amountOfGroups = (int)Math.round((double)teams.size()/(double)this.getGroupSize());
        int treeSize = this.getTwoFactor(amountOfGroups, 0);
        this.setRoot(new SeriesNode(UUID.randomUUID(), new Series(UUID.randomUUID(), this.getRoundsToWin(0)), this, null, treeSize - 1));
        this.getRoot().getContent().setEdited(true);

        this.shuffle(teams);

        List<Group> groups = new ArrayList<>();

        for (Team team : teams) {
            if (groups.size() == 0) {
                List<Team> groupTeams = new ArrayList<>();
                groupTeams.add(team);
                Group group = new Group(this, groupTeams);
                groups.add(group);
                continue;
            }

            Group finalGroup = groups.get(groups.size() - 1);
            if (finalGroup.getTeams().size() == this.getGroupSize()) {
                List<Team> groupTeams = new ArrayList<>();
                groupTeams.add(team);
                Group group = new Group(this, groupTeams);
                groups.add(group);
            } else {
                finalGroup.getTeams().add(team);
            }

        }

        for (Group group : groups) {

            getGroups().add(group);

            if (group.getTeams().size() % 2 != 0) {
                group.getTeams().add(new DummyTeam());
            }

            while (group.getTeams().size() < this.getGroupSize()) {
                group.getTeams().add(new DummyTeam());
            }

            for (int i = 1; i <= group.getTeams().size() - 1; i++) {
                for (int i2 = 0; i2 <= ((group.getTeams().size()/2) - 1); i2++) {
                    SeriesNode node = new SeriesNode(UUID.randomUUID(), new Series(UUID.randomUUID(), this.getRoundsToWin(i)), this, null, i);
                    node.getContent().addTeam(group.getTeams().get(i2), true);
                    node.getContent().addTeam(group.getTeams().get(i2 + (group.getTeams().size()/2)), true);
                    this.addSeriesNodes(node);
                }

                Team previousTeam = group.getTeams().get(group.getTeams().size() - 1);
                for (int i2 = 1; i2 <= group.getTeams().size() - 1; i2++) {
                    Team nextTeam = group.getTeams().get(i2);
                    group.getTeams().set(i2, previousTeam);
                    previousTeam = nextTeam;
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

    @Override
    public AdvanceState advance(SeriesNode node) {

        for (SeriesNode node2 : new SeriesIDFilter(this, node.getSeries()).filter()) {
            if (!node2.isRoot() && node2.getContent() != null && node2.getContent().getWinningTeam() == null) {
                return AdvanceState.FAIL;
            }
        }

        List<SeriesNode> nodes = new SeriesIDFilter(this, node.getSeries() + 1).filter();

        if (nodes.size() == 0) {
            Bracket bracket = new SingleKnockoutBracket(this.getID(), getTeamsPerMatch(), getDefaultRoundsToWin(), getRoundsToWin(), true);
            List<Team> winningTeams = new ArrayList<>();
            for (Group group : getGroups()) {
                winningTeams.addAll(group.getWinningTeams(getAmountOfWinningTeams()));
            }
            bracket.generate(winningTeams, false);
            getTournament().setBracket(bracket);
            return AdvanceState.BRACKET_SILENT_END;
        }

        this.setSeries(getSeries() + 1);

        for (SeriesNode node2 : nodes) {
            this.startSeries(node2, false);
        }

        return AdvanceState.SUCCESS;
    }

    @Override
    public boolean endSeries(SeriesNode node, Team winningTeam) {

        if (!this.isActive(node)) {
            return false;
        }


        Series series = node.getContent();

        series.setPoints(winningTeam, node.getContent().getRoundsToWin(), true);
        if (series.getRounds().size() > 0) {
            Round round = series.getRounds().get(series.getRounds().size() - 1);

            TournamentPlugin.getInstance().getServer().getPluginManager().callEvent(new MatchEndEvent(this.getTournament(), node, round, winningTeam));
        }
        return true;
    }

    @Override
    public boolean isActive(SeriesNode node) {
        return node.getSeries() == this.getSeries();
    }

    public List<Group> getGroups() {
        return groups;
    }

    public int getGroupSize() {
        return groupSize;
    }

    public void setGroupSize(int groupSize) {
        this.groupSize = groupSize;
    }

    public int getAmountOfWinningTeams() {
        return amountOfWinningTeams;
    }

    public int getSeries() {
        return series;
    }

    public void setSeries(int series) {
        this.series = series;
    }

    private class Group {

        private Bracket bracket;
        private List<Team> teams = new ArrayList<>();

        Group(Bracket bracket, List<Team> teams) {
            this.bracket = bracket;
            this.teams = teams;
        }

        public Bracket getBracket() {
            return bracket;
        }

        public List<Team> getTeams() {
            return teams;
        }

        public List<Team> getWinningTeams(int amount) {
            List<Team> teams = getTeams();
            Arrays.sort(teams.toArray(), new Comparator<Object>() {
                @Override
                public int compare(Object o1, Object o2) {
                    return new WinFilter(getBracket(), (Team)o1).filter().size() - new WinFilter(getBracket(), (Team)o2).filter().size();
                }
            });
            return teams.subList(0, amount);
        }
    }

    private class DummyTeam extends DefaultTeam {

        DummyTeam() {
            super();
        }

    }

}
