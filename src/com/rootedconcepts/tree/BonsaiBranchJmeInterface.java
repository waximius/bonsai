/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rootedconcepts.tree;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.rootedconcepts.BonsaiApplication;

/**
 *
 * @author TheDrydens
 */
public interface BonsaiBranchJmeInterface {
    public static final String TREE_ROOT_NAME = "TreeRoot";
    public static final String BRANCH_CYLINDER_NAME = "Branch";
    public static final String BRANCH_TIP_NAME_START = "BranchTipStart";
    public static final String BRANCH_TIP_NAME_END = "BranchTipEnd";
    public static final String BRANCH_LEAF_NAME = "LeafCluster";
    
    /**
     * Render this branch and return the root node for it.
     * @param app The BonsaiApp this branch is attached to
     * @param assetMgr The asset Manager
     * @param percent SET TO 100!  The percentage of the tree branches to grow
     * @return The root node for this branch
     */
    public Node render (BonsaiApplication app, AssetManager assetManager, float percent, Node rootNode);
}
