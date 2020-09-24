/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rootedconcepts.tree;

/**
 *
 * @author TheDrydens
 */
public interface BonsaiBranchInterface {
    /**
     * Grow another part of the trunk from this branch
     * @param step How many growth steps our trunk has gone through.
     * @param prevBranch The previous branch for this growth spurt.  May ne null
     * if growing from the root node.
     */
    public void growTrunk(int step, BonsaiBranchInterface prevBranch);
    
    /**
     * Grow the branches on this node
     * @param step How many growth steps our trunk has gone through.
     * @param prevBranch The previous branch for this growth spurt.  May ne null
     * if growing from the root node.
     */
    public void growBranch(int step, BonsaiBranchInterface prevBranch);
    
    /**
     * Find out the maturity level of our branch.  i.e. how long it's been 
     * growing in steps.
     * @return The maturity level of this branch
     */
    public int getMaturityLevel();
    
    /**
     * Check to see if we have no children branches
     * @return True if we have no branches
     */
    public boolean hasNoBranches ();
    
    /**
     * Check to see if we have children branches
     * @return True if we have branches
     */
    public boolean hasBranches ();
    
    /**
     * Check to see if we need to grow out
     * @return True if we do
     */
    public boolean needsToGrow ();
    
    /**
     * Check to see if this branch is a leaf.
     * 1) We have no children (are undrawable because we have no length)
     * 2) Have children with no children (are drawable but our children aren't).
     * @return True if this branch is a leaf of the tree
     */
    public boolean isTreeLeaf ();
    
    /**
     * Check to see if this branch is part of the trunk.
     * @return True if this branch is part of the trunk
     */
    public boolean isPartOfTrunk ();
    
    /**
     * Don't call directly!!  Use the method in the tree class
     * Remove children from this branch down part of the branch.  Need to pass in
     * the branch so we know if it's the left or right side we're removing
     * @param branch The branch to remove and all it's children.
     */
    public void prune (BonsaiBranchInterface branch);
    
    /**
     * Get the left branch, can be null
     * @return The branch or null
     */
    public BonsaiBranchInterface getLeftBranch();
    
    /**
     * Get the right branch, can be null
     * @return The branch or null
     */
    public BonsaiBranchInterface getRightBranch();
}
