package net.badlion.tournament.bracket.tree;

import java.util.List;

public interface TreeNode {

    /**
     * Get the content the node stores
     */
    Object getContent();

    /**
     * Get the parent node for this node
     */
    TreeNode getParent();

    /**
     * Get this node's child nodes
     */
    <T extends TreeNode> List<T> getChildren();

    /**
     * Check if this node has children
     * @return if the node has children
     */
    boolean hasChildren();

    /**
     * Check if this node is a root node
     * @return if this node is a root node
     */
    boolean isRoot();

    /**
     * Check if this node is a child node
     * @return if this node is a child node
     */
    boolean isChild();

    /**
     * Check if this node is a leaf node
     * @return if this node is a leaf node
     */
    boolean isLeaf();

    /**
     * Check if this node is an interior node
     * @return if this node is an interior node
     */
    boolean isInterior();

}
