package net.badlion.tournament.bracket.filter;

import net.badlion.tournament.bracket.tree.bracket.Bracket;
import net.badlion.tournament.bracket.tree.bracket.SeriesNode;
import net.badlion.tournament.teams.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ActiveUUIDFilter extends UUIDFilter {

    public ActiveUUIDFilter(Bracket bracket, UUID uuid) {
        super(bracket, uuid);
    }

    @Override
    public List<SeriesNode> filter() {
        List<SeriesNode> filteredNodes = new ArrayList<>();
        for (SeriesNode node : this.getBracket().getActiveNodes()) {
            for (Team team : node.getContent().getTeams()) {
                if (team.hasUUID(this.getUUID())) {
                    filteredNodes.add(node);
                }
            }
        }
        return filteredNodes;
    }

}
