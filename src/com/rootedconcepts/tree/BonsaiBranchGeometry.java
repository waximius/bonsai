/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rootedconcepts.tree;

import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import java.io.Serializable;

/**
 *
 * @author TheDrydens
 */
public abstract class BonsaiBranchGeometry extends Geometry implements BonsaiBranchGeometryInterface, Serializable {
    public BonsaiBranchGeometry (String name, Mesh mesh, BonsaiTree tree, BonsaiBranch branchBase, BonsaiBranch branchEnd) {
        super (name, mesh);
    }
}
