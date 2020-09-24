/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rootedconcepts.tree.birch;

import com.jme3.math.ColorRGBA;
import com.jme3.scene.Mesh;
import com.rootedconcepts.tree.BonsaiBranchGeometry;
import com.rootedconcepts.tree.BonsaiBranchInterface;

/**
 *
 * @author TheDrydens
 */
public class BonsaiBranchBirchGeometry extends BonsaiBranchGeometry {
    private static final long serialVersionUID = 1126421215622776117L;
    
    private final boolean HIGHLIGHT_CHILDREN = true;
    
    private BonsaiTreeBirch m_tree = null;
    private BonsaiBranchBirch m_branchBase = null;
    private BonsaiBranchBirch m_branchEnd = null;
    
    public BonsaiBranchBirchGeometry (String name, Mesh mesh, BonsaiTreeBirch tree, BonsaiBranchBirch branchBase, BonsaiBranchBirch branchEnd) {
        super(name, mesh, tree, branchBase, branchEnd);
        
        m_tree = tree;
        m_branchBase = branchBase;
        m_branchEnd = branchEnd;
    }
    
    /**
     * Prunes this branch from the model and all children
     */
    @Override
    public void pruneBranch () {
        m_tree.prune ((BonsaiBranchInterface)m_branchBase, (BonsaiBranchInterface)m_branchEnd);
        //removeFromParent();
    }
    
    @Override
    public void waterBranch () {
        m_tree.water ();
    }
    
    @Override
    public boolean isPartOfTrunk () {
        return m_branchBase.isPartOfTrunk ();
    }
    
    @Override
    public void setBranchesHighlighted(boolean glowOn) {
        if (true == glowOn) {
            getMaterial().setColor("GlowColor", new ColorRGBA(0.7f, 0.7f, 0.6f, 1.0f));
        } else {
            getMaterial().clearParam("GlowColor");
        }
        
        if (true == HIGHLIGHT_CHILDREN) {
            BonsaiBranchBirch left = (BonsaiBranchBirch)m_branchEnd.getLeftBranch();
            if ((null != left) && (null != left.getCylinderGeometry())) {
                left.getCylinderGeometry().setBranchesHighlighted(glowOn);
            }
            
            BonsaiBranchBirch right = (BonsaiBranchBirch)m_branchEnd.getRightBranch();
            if ((null != right) && (null != right.getCylinderGeometry())) {
                right.getCylinderGeometry().setBranchesHighlighted(glowOn);
            }
            
            BonsaiBranchBirch center = (BonsaiBranchBirch)m_branchEnd.getCenterBranch();
            if ((null != center) && (null != center.getCylinderGeometry())) {
                center.getCylinderGeometry().setBranchesHighlighted(glowOn);
            }
        }
    }
}
