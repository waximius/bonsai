/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rootedconcepts.tree.maple;

import com.rootedconcepts.tree.BonsaiTree;

/**
 *
 * @author TheDrydens
 */
public class BonsaiTreeMaple extends BonsaiTree {
    private static final long serialVersionUID = 7526472295622776147L;
    
    public BonsaiTreeMaple () {
        super ();
        
        long ms = (long)(2 * 60 * 60 * 1000) / (long)BonsaiTree.M_MAX_ROOT_THICKNESS;
        //ms = 2000;
        setMsUntilGrowth(ms);
        setWaterAdjustMs((long)(ms * 0.005));
    }
    
    public BonsaiTreeMaple (int growthMsOverride) {
        super ();
        setMsUntilGrowth(growthMsOverride);
        setWaterAdjustMs((long)(growthMsOverride * 0.005));
    }
}
