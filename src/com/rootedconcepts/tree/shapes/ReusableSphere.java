/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rootedconcepts.tree.shapes;

import com.jme3.scene.shape.Sphere;

/**
 *
 * @author TheDrydens
 */
public class ReusableSphere extends Sphere {
    private boolean m_isInUse = false;
    
    public ReusableSphere(int zSamples, int radialSamples, float radius) {
        super(zSamples, radialSamples, radius);
    }
            
    public void setIsInUse(boolean inUse) {
        m_isInUse = inUse;
    }
    
    public boolean getIsInUse () {
        return m_isInUse;
    }
}
