package net.badlion.tournament.bracket.filter;

import net.badlion.tournament.bracket.tree.TreeNode;

import java.util.List;

interface Filter {

    /**
     * Filter nodes that meet a certain category
     */
    <T extends TreeNode> List<T> filter();

}
