/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rootedconcepts.tree.shapes;

import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author TheDrydens
 */
public class BonsaiBranchShapePool {
    private static ArrayList<ReusableCylinder> m_cylinders = new ArrayList();
    private static ArrayList<ReusableSphere> m_spheres = new ArrayList();
    
    public static ReusableCylinder getCylinder (int axisSamples, int radialSamples,
            float radius, float radius2, float height, boolean closed, boolean inverted) {
        ReusableCylinder retval = null;
        
        // Find an unused cylinder in our array (parent is null)
        for (Iterator<ReusableCylinder> it = m_cylinders.iterator(); it.hasNext();) {
            ReusableCylinder shape = it.next();
            if (false == shape.getIsInUse()) {
                retval = shape;
                break;
            }
        }
        
        // Create a new shape
        if (null == retval) {
            retval = new ReusableCylinder(axisSamples, radialSamples, radius, radius2, height, closed, inverted);
            m_cylinders.add(retval);
        } 
        // Else, update our geometry
        else {
            retval.updateGeometry(axisSamples, radialSamples, radius, radius2, height, closed, inverted);
        }
        
        retval.setDynamic(); // Fixes the issue with glow on hovering not being detected on long-lived trees.  
        retval.setIsInUse(true);
        return retval;
    }
    
    public static ReusableSphere getSphere (int zSamples, int radialSamples, float radius) {
        ReusableSphere retval = null;
        
        // Find an unused cylinder in our array (parent is null)
        for (Iterator<ReusableSphere> it = m_spheres.iterator(); it.hasNext();) {
            ReusableSphere shape = it.next();
            if (false == shape.getIsInUse()) {
                retval = shape;
                break;
            }
        }
        
        // Create a new shape
        if (null == retval) {
            retval = new ReusableSphere(zSamples, radialSamples, radius);
            m_spheres.add(retval);
        } 
        // Else, update our geometry
        else {
            retval.updateGeometry(zSamples, radialSamples, radius);
        }
        
        retval.setIsInUse(true);
        return retval;
    }
    
    public static void freeShapes () {
        for (Iterator<ReusableCylinder> it = m_cylinders.iterator(); it.hasNext();) {
            ReusableCylinder shape = it.next();
            shape.setIsInUse(false);
        }
        for (Iterator<ReusableSphere> it = m_spheres.iterator(); it.hasNext();) {
            ReusableSphere shape = it.next();
            shape.setIsInUse(false);
        }
    }
}
