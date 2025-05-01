package net.badlion.tournament.bracket.filter;

import net.badlion.tournament.bracket.tree.bracket.Bracket;
import net.badlion.tournament.bracket.tree.bracket.SeriesNode;
import net.badlion.tournament.teams.Team;

import java.util.ArrayList;
import java.util.List;

public class TeamFilter extends SeriesFilter {

    private Team team;

    public TeamFilter(Bracket bracket, Team team) {
        super(bracket);
        this.team = team;
    }

    public Team getTeam() {
        return team;
    }

    @Override
    public List<SeriesNode> filter() {
        List<SeriesNode> filteredNodes = new ArrayList<>();
        for (SeriesNode node : this.getBracket().getActiveNodes()) {
            for (Team team : node.getContent().getTeams()) {
                if (team.equals(getTeam())) {
                    filteredNodes.add(node);
                }
            }
        }
        return filteredNodes;
    }

}
