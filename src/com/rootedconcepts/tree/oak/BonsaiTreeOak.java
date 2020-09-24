/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rootedconcepts.tree.oak;

import com.rootedconcepts.tree.BonsaiTree;

/**
 *
 * @author TheDrydens
 */
public class BonsaiTreeOak extends BonsaiTree {
    private static final long serialVersionUID = 7526472295622776147L;
    
    public BonsaiTreeOak () {
        super ();
        
        long ms = (long)(3 * 60 * 60 * 1000) / (long)BonsaiTree.M_MAX_ROOT_THICKNESS;
        setMsUntilGrowth(ms);
        setWaterAdjustMs((long)(ms * 0.005));
    }
    
    public BonsaiTreeOak (int growthMsOverride) {
        super ();
        setMsUntilGrowth(growthMsOverride);
        setWaterAdjustMs((long)(growthMsOverride * 0.005));
    }
}
