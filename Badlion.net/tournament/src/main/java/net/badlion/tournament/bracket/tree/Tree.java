package net.badlion.tournament.bracket.tree;

import java.util.List;

public interface Tree {

    /**
     * Get the root node
     */
    TreeNode getRoot();

    /**
     * Get all child nodes from the Tree
     */
    <T extends TreeNode> List<T> getNodes();

}
