/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rootedconcepts.tree;

/**
 *
 * @author TheDrydens
 */
public interface BonsaiTreeInterface {
    
    /**
     * Set the baseline time between growth cycles
     * @param ms The number of milliseconds
     */
    public void setMsUntilGrowth(long ms);
    
    /**
     * Set the adjustment factor each time the tree is watered
     * @param ms The number of milliseconds to subtract from the current growth
     * cycle each time the tree is watered
     */
    public void setWaterAdjustMs(long ms);
    
    /**
     * Sets the flag whether this tree is allowed to grow right now.  We
     * can pause the tree growth this way
     * @param enable True means the tree can grow when grow() is called
     */
    public void setGrowthEnabled(boolean enable);
    
    /**
     * Get the growth flag
     * @return True if the tree is allowed to grow when grow() is called.
     */
    public boolean getGrowthEnabled();
    
    /**
     * Get the percentage of growth of the tree cycle
     * @return A number between 0 and 1
     */
    public float getGrowthCyclePercent ();
    
    /**
     * Instantly grow the tree out to its trunk height
     */
    public void growFullTrunk ();
    
    /**
     * Instantly grow the tree out to part of its trunk height
     */
    public void growPartialTrunk ();
    
    /**
     * Check to see if the tree is ready to grow out for its next cycle.
     * @return True if the tree is ready to grow
     */
    public boolean getReadyToGrow ();
    
    /**
     * Grow the tree by 1 length
     */
    public void grow ();
    
    /**
     * Use this method to catch up all growth cycles over extended periods of time.
     * For example, if the tree was saved a day ago, and loaded today, run all
     * the growth periods between now and then.  If this method is NOT called
     * before the next call to grow(), then all that time will be lost.
     */
    public void catchUpGrowthCycles();
    
    /**
     * Prunes the tree, pruning this way is the correct way.
     * @param branchBase The start node
     * @param branchEnd  The end node
     */
    public void prune (BonsaiBranchInterface branchBase, BonsaiBranchInterface branchEnd);
    
    /**
     * Water this tree
     */
    public void water ();
    
    /**
     * Get the last time of watering
     */
    public long getWateredTime ();
    
    /**
     * Get the maturity level of the tree.  How long this tree has been alive.
     * @return The number of growth cycles of the tree
     */
    public int getMaturityLevel();
    
    /**
     * Check to see if the root can get thicker.  In other words, it hasn't 
     * reached full maturity.
     * @return Returns true if the root can get thicker, false if not
     */
    public boolean canGetThicker ();
    
    /**
     * This tree has been changed, it needs re-drawn by whatever engine is rendering it.
     * @param needsDrawn True if the tree needs to be re-drawn
     */
    public void setTreeNeedsDrawn(boolean needsDrawn);
    
    /**
     * Gets the state of the tree.  If the tree has changed since the last time
     * render() was called, this should be true.
     * @return True if the tree has changed since the last render() call.
     */
    public boolean getTreeNeedsDrawn();
    
    /**
     * Tells the tree it needs to grow it's next cycle from scratch again.
     */
    public void resetGrowthCycle();
    
    /**
     * Call this to deconstruct the tree for garbage collecting
     */
    public void destroy ();
}
