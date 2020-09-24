/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rootedconcepts.tree.japmaple;

import com.rootedconcepts.tree.BonsaiTree;

/**
 *
 * @author TheDrydens
 */
public class BonsaiTreeJapMaple extends BonsaiTree {
    private static final long serialVersionUID = 7526472295622776147L;
    
    public BonsaiTreeJapMaple () {
        super ();
        
        long ms = (long)(2 * 60 * 60 * 1000) / (long)BonsaiTree.M_MAX_ROOT_THICKNESS;
        setMsUntilGrowth(ms);
        setWaterAdjustMs((long)(ms * 0.005));
    }
    
    public BonsaiTreeJapMaple (int growthMsOverride) {
        super ();
        setMsUntilGrowth(growthMsOverride);
        setWaterAdjustMs((long)(growthMsOverride * 0.005));
    }
}
