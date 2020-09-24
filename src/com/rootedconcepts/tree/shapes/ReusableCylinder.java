/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rootedconcepts.tree.shapes;

import com.jme3.scene.shape.Cylinder;

/**
 *
 * @author TheDrydens
 */
public class ReusableCylinder extends Cylinder {
    private boolean m_isInUse = false;
    
    public ReusableCylinder(int axisSamples, int radialSamples,
            float radius, float radius2, float height, boolean closed, boolean inverted) {
        super(axisSamples, radialSamples, radius, radius2, height, closed, inverted);
    }
            
    public void setIsInUse(boolean inUse) {
        m_isInUse = inUse;
    }
    
    public boolean getIsInUse () {
        return m_isInUse;
    }
}
