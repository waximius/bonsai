/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rootedconcepts.tree;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.rootedconcepts.BonsaiApplication;
import com.rootedconcepts.tree.birch.BonsaiBranchBirch;
import com.rootedconcepts.tree.birch.BonsaiTreeBirch;
import com.rootedconcepts.tree.japmaple.BonsaiBranchJapMaple;
import com.rootedconcepts.tree.japmaple.BonsaiTreeJapMaple;
import com.rootedconcepts.tree.maple.BonsaiBranchMaple;
import com.rootedconcepts.tree.maple.BonsaiTreeMaple;
import com.rootedconcepts.tree.oak.BonsaiBranchOak;
import com.rootedconcepts.tree.oak.BonsaiTreeOak;
import com.rootedconcepts.tree.sycamore.BonsaiBranchSycamore;
import com.rootedconcepts.tree.sycamore.BonsaiTreeSycamore;
import java.io.Serializable;

/**
 *
 * @author TheDrydens
 */
public abstract class BonsaiTree implements BonsaiTreeInterface, BonsaiTreeJmeInterface, Serializable {
    
    // Increasing this will give the tree a higher chance of making branches when it grows.
    // Set it equal to the trunk height for standard thickness
    private static final int M_THICKNESS_CORRECTION = 10;
    
    public static final int M_TRUNK_HEIGHT = 10;
    public static final int M_MAX_ROOT_THICKNESS = 50;
    
    // Flag to determine whether the tree should be re-drawn by the engine.
    // Basically, whenever the tree has changed in some way since the last
    // time it was drawn, then this flag needs to be set.
    private boolean m_treeNeedsDrawn = true;
    
    // If this is false, do not let the tree grow even if it is ready.
    // If true, the tree is able to grow when grow() is called.
    private boolean m_growthFlag = true;
    
    private long m_wateredTime = 0;
    
    private final float M_DEBUG_SPEEDUP = 1.0f; // Use to debug by speeding pu growth of tree.  1.0 = no change in growth speed
    private long MS_UNTIL_GROWTH = (2 * 60 * 60 * 1000) / M_MAX_ROOT_THICKNESS; // grow fully over 2 hours
    private long m_msUntilGrowth = MS_UNTIL_GROWTH;
    private long WATER_ADJ = (long)(MS_UNTIL_GROWTH * 0.5); // 5% faster
    
    // The time of the last growth cycle in milliseconds since the epoch
    private long m_lastGrowthCycle = 0;
    
    private int m_step = 0;
    
    private BonsaiBranch m_rootBranch = null;
    
    public BonsaiTree () {
        // Nothing to do
    }
    
    @Override
    public void setMsUntilGrowth(long ms) {
        MS_UNTIL_GROWTH = ms;
    }
    
    @Override
    public void setWaterAdjustMs(long ms) {
        WATER_ADJ = ms;
    }
    
    /**
     * Grow the tree by 1 length
     */
    @Override
    public void grow () {
        if (true == m_growthFlag) {
            m_step++;
            resetGrowthCycle();
            
            if (null == m_rootBranch) {
                // TODO - When a new tree is added, add it here!
                if (this instanceof BonsaiTreeMaple) {
                    m_rootBranch = new BonsaiBranchMaple(
                        (BonsaiTreeMaple)this, null,
                        0.0, 0.0, 0.0, 0.0, 45.0,
                        m_step, true, false);
                } else if (this instanceof BonsaiTreeOak) {
                    m_rootBranch = new BonsaiBranchOak(
                        (BonsaiTreeOak)this, null,
                        0.0, 0.0, 0.0, 0.0, 45.0,
                        m_step, true, false);
                } else if (this instanceof BonsaiTreeJapMaple) {
                    m_rootBranch = new BonsaiBranchJapMaple(
                        (BonsaiTreeJapMaple)this, null,
                        0.0, 0.0, 0.0, 0.0, 45.0,
                        m_step, true, false);
                } else if (this instanceof BonsaiTreeSycamore) {
                    m_rootBranch = new BonsaiBranchSycamore(
                        (BonsaiTreeSycamore)this, null,
                        0.0, 0.0, 0.0, 0.0, 45.0,
                        m_step, true, false);
                } else if (this instanceof BonsaiTreeBirch) {
                    m_rootBranch = new BonsaiBranchBirch(
                        (BonsaiTreeBirch)this, null,
                        0.0, 0.0, 0.0, 0.0, 45.0,
                        m_step, true, false);
                }
            } else {
                if (M_TRUNK_HEIGHT > m_step) {
                    m_rootBranch.growTrunk (m_step, null);
                } else {
                    m_rootBranch.growBranch (m_step, null);
                }
            }
        }
    }
    
    /**
     * Sets the flag whether this tree is allowed to grow right now.  We
     * can pause the tree growth this way
     * @param enable True means the tree can grow when grow() is called
     */
    @Override
    public void setGrowthEnabled(boolean enable) {
        m_growthFlag = enable;
    }
    
    /**
     * Get the growth flag
     * @return True if the tree is allowed to grow when grow() is called.
     */
    @Override
    public boolean getGrowthEnabled() {
        return m_growthFlag;
    }
    
    /**
     * Get the percentage of growth of the tree cycle
     * @return A number between 0 and 1
     */
    @Override
    public float getGrowthCyclePercent () {
        long now = System.currentTimeMillis();
        long lastGrowth = m_lastGrowthCycle;
        float percent = (float)((double)(now - lastGrowth) / getMsUntilGrowth());
        if (m_step < M_TRUNK_HEIGHT) {
            percent = (float)((double)(now - lastGrowth) / (getMsUntilGrowth()/2.0));
        }
        return percent;
    }
    
    /**
     * Instantly grow the tree out to its trunk height
     */
    @Override
    public void growFullTrunk () {
        while ((M_TRUNK_HEIGHT-1) > m_step) {
            grow ();
        }
    }
    
    /**
     * Instantly grow the tree out to part of its trunk height
     */
    @Override
    public void growPartialTrunk () {
        while ((M_TRUNK_HEIGHT-8) > m_step) {
            grow ();
        }
    }
    
    /**
     * Check to see if the tree is ready to grow out for its next cycle.
     * @return True if the tree is ready to grow
     */
    @Override
    public boolean getReadyToGrow () {
        boolean retval = false;
        long now = System.currentTimeMillis();
        // If the last time grow() was called is longer than a growth cycle,
        // then we can grow again.
        if ((now - m_lastGrowthCycle) > getMsUntilGrowth()) {
            retval = true;
        } else if ((m_step < M_TRUNK_HEIGHT) &&
            (now - m_lastGrowthCycle) > (getMsUntilGrowth()/2.0)) {
            retval = true;
        }
        return retval;
    }
    
    /**
     * Use this method to catch up all growth cycles over extended periods of time.
     * For example, if the tree was saved a day ago, and loaded today, run all
     * the growth periods between now and then.  If this method is NOT called
     * before the next call to grow(), then all that time will be lost.
     */
    @Override
    public void catchUpGrowthCycles() {
        long now = System.currentTimeMillis();
        long lastGrowth = m_lastGrowthCycle;
        while ((now - lastGrowth) > getMsUntilGrowth()) {
            grow();
            
            // Each loop, increase the last growth cycle timestamp by one growth cycles
            // amount.
            lastGrowth += (long)getMsUntilGrowth();
        }
    }
    
    /**
     * Prunes the tree, pruning this way is the correct way.
     * @param branchBase The start node
     * @param branchEnd  The end node
     */
    @Override
    public void prune (BonsaiBranchInterface branchBase, BonsaiBranchInterface branchEnd) {
        branchBase.prune (branchEnd);
        setTreeNeedsDrawn(true);
    }
    
    /**
     * Water this tree
     */
    @Override
    public void water () {
        m_wateredTime = System.currentTimeMillis();
        m_lastGrowthCycle -= WATER_ADJ;
    }
    
    /**
     * Get the time of watering
     */
    @Override
    public long getWateredTime () {
        return m_wateredTime;
    }
    
    /**
     * Get the maturity level of the tree.  How long this tree has been alive.
     * @return The number of growth cycles of the tree
     */
    @Override
    public int getMaturityLevel() {
        return m_step - M_THICKNESS_CORRECTION;
    }
    
    /**
     * Check to see if the root can get thicker.  In other words, it hasn't 
     * reached full maturity.
     * @return Returns true if the root can get thicker, false if not
     */
    @Override
    public boolean canGetThicker () {
        boolean retval = true;
        if (null != m_rootBranch) {
            retval = m_rootBranch.getMaturityLevel() < M_MAX_ROOT_THICKNESS;
        }
        return retval;
    }
    
    /**
     * This tree has been changed, it needs re-drawn by whatever engine is rendering it.
     * @param needsDrawn True if the tree needs to be re-drawn
     */
    @Override
    public void setTreeNeedsDrawn(boolean needsDrawn) {
        m_treeNeedsDrawn = needsDrawn;
    }
    
    /**
     * Gets the state of the tree.  If the tree has changed since the last time
     * render() was called, this should be true.
     * @return True if the tree has changed since the last render() call.
     */
    @Override
    public boolean getTreeNeedsDrawn() {
        return m_treeNeedsDrawn;
    }
    
    /**
     * Tells the tree it needs to grow it's next cycle from scratch again.
     */
    @Override
    public void resetGrowthCycle() {
        m_lastGrowthCycle = System.currentTimeMillis();
        m_msUntilGrowth = MS_UNTIL_GROWTH;
    }
    
    /**
     * Call this to deconstruct the tree for garbage collecting
     */
    @Override
    public void destroy () {
        if (null != m_rootBranch) {
            m_rootBranch.prune (null);
            m_rootBranch = null;
        }
    }
    
    // ------------  JME-Specific Code Below This Line  ------------ //    
    /**
     * Render this tree and return the root node for it.
     * @param app The BonasiApp this tree is attached to
     * @param assetMgr The asset Manager
     * @param percent SET TO 100!  The percentage of the tree branches to grow
     * @return The root node for this tree
     */
    @Override
    public Node render (BonsaiApplication app, AssetManager assetMgr, float percent) {
        Node retval = null;
        if (null != m_rootBranch) {
            retval = m_rootBranch.render(app, assetMgr, percent, null);
            
            // Do this so frame rate is consistent whether we are zoomed in or out
            retval.setCullHint(Spatial.CullHint.Never);
        }
        setTreeNeedsDrawn(false);
        return retval;
    }
    
    private double getMsUntilGrowth () {
        return ((double)m_msUntilGrowth) / M_DEBUG_SPEEDUP;
    }
}
