/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rootedconcepts.tree;

/**
 *
 * @author TheDrydens
 */
public interface BonsaiBranchGeometryInterface {
    /**
     * Prunes this branch from the model and all children
     */
    public void pruneBranch ();
    
    public void waterBranch ();
    
    public boolean isPartOfTrunk ();
    
    public void setBranchesHighlighted (boolean glowOn);
}
