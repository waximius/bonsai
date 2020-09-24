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
public interface BonsaiTreeJmeInterface {
    /**
     * Render this tree and return the root node for it.
     * @param app The BonsaiApp this tree is attached to
     * @param assetMgr The asset Manager
     * @param percent SET TO 100!  The percentage of the tree branches to grow
     * @return The root node for this tree
     */
    public Node render (BonsaiApplication app, AssetManager assetMgr, float percent);
}
