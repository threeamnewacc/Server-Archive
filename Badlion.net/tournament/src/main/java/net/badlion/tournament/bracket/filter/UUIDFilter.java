package net.badlion.tournament.bracket.filter;

import net.badlion.tournament.bracket.tree.bracket.Bracket;
import net.badlion.tournament.bracket.tree.bracket.SeriesNode;
import net.badlion.tournament.teams.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UUIDFilter extends SeriesFilter {

    private UUID uuid;

    public UUIDFilter(Bracket bracket, UUID uuid) {
        super(bracket);
        this.uuid = uuid;
    }

    public UUID getUUID() {
        return uuid;
    }

    @Override
    public List<SeriesNode> filter() {
        List<SeriesNode> filteredNodes = new ArrayList<>();
        for (SeriesNode node : this.getBracket().getNodes()) {
            for (Team team : node.getContent().getTeams()) {
                if (team.hasUUID(this.getUUID())) {
                    filteredNodes.add(node);
                }
            }
        }
        return filteredNodes;
    }

}
