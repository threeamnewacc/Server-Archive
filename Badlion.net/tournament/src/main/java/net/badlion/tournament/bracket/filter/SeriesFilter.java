package net.badlion.tournament.bracket.filter;

import net.badlion.tournament.bracket.tree.bracket.Bracket;
import net.badlion.tournament.bracket.tree.bracket.SeriesNode;
import net.badlion.tournament.bracket.filter.Filter;

import java.util.ArrayList;
import java.util.List;

public abstract class SeriesFilter implements Filter {

    private Bracket bracket;

    SeriesFilter(Bracket bracket) {
        this.bracket = bracket;
    }

    public Bracket getBracket() {
        return bracket;
    }

    public List<SeriesNode> filter() {
        return new ArrayList<>();
    }

}
