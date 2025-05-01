package net.badlion.tournament.bracket.filter;

import net.badlion.tournament.bracket.tree.bracket.Bracket;
import net.badlion.tournament.bracket.tree.bracket.SeriesNode;

import java.util.ArrayList;
import java.util.List;

public class SeriesIDFilter extends SeriesFilter {

    private int id;

    public SeriesIDFilter(Bracket bracket, int id) {
        super(bracket);
        this.id = id;
    }

    public int getID() {
        return id;
    }

    @Override
    public List<SeriesNode> filter() {
        List<SeriesNode> filteredNodes = new ArrayList<>();
        for (SeriesNode node : this.getBracket().getNodes()) {
            if (node.getSeries() == getID()) {
                filteredNodes.add(node);
            }
        }
        return filteredNodes;
    }

}
