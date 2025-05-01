package net.badlion.tournament.bracket.tree.bracket;

import net.badlion.tournament.bracket.tree.TreeNode;
import net.badlion.tournament.matches.Series;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SeriesNode implements TreeNode {

    private UUID id;
    private Series content;
    private SeriesNode parent;
    private List<SeriesNode> children = new ArrayList<>();
    private Bracket bracket;
    private int series = -1;
    private boolean edited = false;

    public SeriesNode(UUID id, Series content, Bracket bracket, SeriesNode parent, int series) {
        this(id, content, bracket, parent, series, true);
    }

    public SeriesNode(UUID id, Series content, Bracket bracket, SeriesNode parent, int series, boolean edited) {
        this.id = id;
        this.content = content;
        this.parent = parent;
        this.bracket = bracket;
        this.series = series;
        bracket.getNodes().add(this);
        this.setEdited(edited);
    }

    public UUID getID() {
        return id;
    }

    public Series getContent() {
        return content;
    }

    public SeriesNode getParent() {
        return parent;
    }

    public void setParent(SeriesNode parent) {
        this.parent = parent;
    }

    public List<SeriesNode> getChildren() {
        return children;
    }

    public boolean hasChildren() {
        return this.getChildren().size() > 0;
    }

    public boolean isRoot() {
        return this.getBracket().getRoot().equals(this);
    }

    public boolean isChild() {
        return this.getParent() != null;
    }

    public boolean isLeaf() {
        return this.getChildren().size() == 0 && !this.isRoot();
    }

    public boolean isInterior() {
        return this.getChildren().size() > 0 && this.isChild();
    }

    public Bracket getBracket() {
        return bracket;
    }

    public int getSeries() {
        return series;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }
}
