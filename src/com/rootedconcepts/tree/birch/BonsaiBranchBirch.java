/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rootedconcepts.tree.birch;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingSphere;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh.Type;
import com.jme3.effect.shapes.EmitterPointShape;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.debug.WireSphere;
import com.jme3.scene.shape.Sphere;
import com.rootedconcepts.BonsaiApplication;
import com.rootedconcepts.tree.BonsaiBranch;
import com.rootedconcepts.tree.BonsaiBranchInterface;
import com.rootedconcepts.tree.BonsaiBranchJmeInterface;
import com.rootedconcepts.tree.shapes.BonsaiBranchShapePool;
import com.rootedconcepts.tree.shapes.ReusableCylinder;
import com.rootedconcepts.tree.shapes.ReusableSphere;

/**
 *
 * @author TheDrydens
 */
public class BonsaiBranchBirch extends BonsaiBranch {
    private static final long serialVersionUID = 8526422215622776447L;
    
    private static final boolean M_VARIABLE_BRANCH_LENGTH = true;
    
    private static final boolean DEBUG_NO_LEAVES = false;
    
    private BonsaiTreeBirch m_tree = null;
    
    private BonsaiBranchBirch m_parentBranch = null;
    private BonsaiBranchBirch m_centerBranch = null;
    private BonsaiBranchBirch m_leftBranch = null;
    private BonsaiBranchBirch m_rightBranch = null;
    
    public static final int TINY_ANGLE  = 2;
    public static final int PHI_ANGLE   = 32; // 25
    public static final int ALPHA_ANGLE = 40; // 32
    public static final int BETA_ANGLE  = 32; // 25
    public static final int TREE_SCALE = 1;
    
    private double m_x0 = 0;
    private double m_y0 = 0;
    private double m_z0 = 0;
    private double m_p0 = 0;
    private double m_t0 = 0;
    
    private int m_step = 1;
    
    private boolean m_isTrunk = true;
    private boolean m_isBud = true;
    
    private boolean m_needsToGrow = false;
    
    public BonsaiBranchBirch (BonsaiTreeBirch tree, BonsaiBranchBirch parentBranch,
            double x, double y, double z, double t, double p,
            int step, boolean isTrunk, boolean isBud) {
        m_tree = tree;
        m_parentBranch = parentBranch;
        
        m_x0 = x;
        m_y0 = y;
        m_z0 = z;
        m_t0 = t;
        m_p0 = p;
        
        m_step = step;
        
        m_isTrunk = isTrunk;
        m_isBud = isBud;
        
        // Any time a new branch is created, the tree needs re-drawn
        m_tree.setTreeNeedsDrawn(true);
    }
    
    /**
     * Grow another part of the trunk from this branch
     * @param step How many growth steps our trunk has gone through.
     * @param prevBranch The previous branch for this growth spurt.  May ne null
     * if growing from the root node.
     */
    @Override
    public void growTrunk(int step, BonsaiBranchInterface prevBranch) {
        //m_step = step; // Increase step size
        
        // If we can get thicker, then we need re-drawn and our step needs to increase
        if (m_tree.canGetThicker()) {
            m_tree.setTreeNeedsDrawn(true);
            m_step++;
        }
                
        // If we have no child branches, or we have to grow again, then grow a trunk length
        // If we're not thick enough to support children, don't try to
        if ((hasNoBranches() || needsToGrow ()) && (m_step > 1)) {
            if(null == m_centerBranch) {
                m_centerBranch = generateCenterBranch ();
            }
            
            // Do this at the end
            m_needsToGrow = false;
        }
        // Otherwise grow the children
        else {
            if (null != m_centerBranch) {
                m_centerBranch.growTrunk(step - 1, this);
            }
        }
    }
    
    private BonsaiBranchBirch generateCenterBranch() {
        BonsaiBranchBirch branch = null;
        
        if (0.5 > Math.random()) {
            if (0.5 > Math.random()) {
                branch = generateBranch(
                    randomPositiveAngle(TINY_ANGLE, 0), 
                    randomPositiveAngle(TINY_ANGLE, 0),
                    false, true);
            } else {
                branch = generateBranch(
                    randomPositiveAngle(TINY_ANGLE, 0), 
                    -randomPositiveAngle(TINY_ANGLE, 0),
                    false, true);
            }
        } else {
            if (0.5 > Math.random()) {
                branch = generateBranch(
                    -randomPositiveAngle(TINY_ANGLE, 0), 
                    randomPositiveAngle(TINY_ANGLE, 0),
                    false, true);
            } else {
                branch = generateBranch(
                    -randomPositiveAngle(TINY_ANGLE, 0), 
                    -randomPositiveAngle(TINY_ANGLE, 0),
                    false, true);
            }
        }
        
        return branch;
    }
    
    /**
     * Grow the branches on this node
     * @param step How many growth steps our trunk has gone through.
     * @param prevBranch The previous branch for this growth spurt.  May ne null
     * if growing from the root node.
     */
    @Override
    public void growBranch(int step, BonsaiBranchInterface prevBranch) {
        //m_step = step; // Increase step size
        
        // If we can get thicker, then we need re-drawn and our step needs to increase
        if (m_tree.canGetThicker()) {
            m_tree.setTreeNeedsDrawn(true);
            m_step++;
        }
        
        // If we have no child branches, or we have to grow again, then grow a trunk length
        // If we're not thick enough to support children, don't try to
        if ((hasNoBranches() || needsToGrow ()) && (m_step > 1)) {
            if (m_isTrunk) {
                m_step--; // growTrunk increases our step size
                growTrunk(m_step - 1, this);
            }
            
            double prob = Math.min(1.0, (2 * (double) m_tree.getMaturityLevel() + 1) / Math.pow((double) m_tree.getMaturityLevel(), 2));
            if (0.5 > Math.random()) {
                if (0.5 > Math.random()) {
                    if (prob > Math.random()) {
                        // p1
                        if (null == m_leftBranch) {
                            m_leftBranch = generateBranch (
                                    -randomPositiveAngle(ALPHA_ANGLE, 17), 
                                    randomPositiveAngle(PHI_ANGLE, 17),
                                    false);
                        }
                        if (null == m_rightBranch) {
                            m_rightBranch = generateBranch (
                                    randomPositiveAngle(BETA_ANGLE, 17),
                                    randomPositiveAngle(PHI_ANGLE, 17),
                                    false);
                        }
                    } else {
                        // p2
                        if (null == m_leftBranch) {
                            m_leftBranch = generateBranch (
                                    randomPositiveAngle(BETA_ANGLE, 17), 
                                    randomPositiveAngle(PHI_ANGLE, 17),
                                    true);
                        }
                        //m_rightBranch = null;
                    }
                } else {
                    if (prob > Math.random()) {
                        // p1
                        if (null == m_leftBranch) {
                            m_leftBranch = generateBranch (
                                    randomPositiveAngle(ALPHA_ANGLE, 17), 
                                    randomPositiveAngle(PHI_ANGLE, 17),
                                    false);
                        }
                        if (null == m_rightBranch) {
                            m_rightBranch = generateBranch (
                                    -randomPositiveAngle(BETA_ANGLE, 17),
                                    randomPositiveAngle(PHI_ANGLE, 17),
                                    false);
                        }
                    } else {
                        // p2
                        if (null == m_leftBranch) {
                            m_leftBranch = generateBranch (
                                    -randomPositiveAngle(BETA_ANGLE, 17), 
                                    randomPositiveAngle(PHI_ANGLE, 17),
                                    true);
                        }
                        //m_rightBranch = null;
                    }
                }
            } else {
                if (0.5 > Math.random()) {
                    if (prob > Math.random()) {
                        // p1
                        if (null == m_leftBranch) {
                            m_leftBranch = generateBranch (
                                    -randomPositiveAngle(ALPHA_ANGLE, 17), 
                                    -randomPositiveAngle(PHI_ANGLE, 17),
                                    false);
                        }
                        if (null == m_rightBranch) {
                            m_rightBranch = generateBranch (
                                    randomPositiveAngle(BETA_ANGLE, 17),
                                    -randomPositiveAngle(PHI_ANGLE, 17),
                                    false);
                        }
                    } else {
                        // p2
                        if (null == m_leftBranch) {
                            m_leftBranch = generateBranch (
                                    randomPositiveAngle(BETA_ANGLE, 17), 
                                    -randomPositiveAngle(PHI_ANGLE, 17),
                                    true);
                        }
                        //m_rightBranch = null;
                    }
                } else {
                    if (prob > Math.random()) {
                        // p1
                        if (null == m_leftBranch) {
                            m_leftBranch = generateBranch (
                                    randomPositiveAngle(ALPHA_ANGLE, 17), 
                                    -randomPositiveAngle(PHI_ANGLE, 17),
                                    false);
                        }
                        if (null == m_rightBranch) {
                            m_rightBranch = generateBranch (
                                    -randomPositiveAngle(BETA_ANGLE, 17),
                                    -randomPositiveAngle(PHI_ANGLE, 17),
                                    false);
                        }
                    } else {
                        // p2
                        if (null == m_leftBranch) {
                            m_leftBranch = generateBranch (
                                    -randomPositiveAngle(BETA_ANGLE, 17), 
                                    -randomPositiveAngle(PHI_ANGLE, 17),
                                    true);
                        }
                        //m_rightBranch = null;
                    }
                }
            }
            
            // Do this at the end
            m_needsToGrow = false;
        } else {
            if (null != m_leftBranch) {
                m_leftBranch.growBranch(step - 1, this);
            }
            if (null != m_rightBranch) {
                m_rightBranch.growBranch(step - 1, this);
            }
            if (null != m_centerBranch) {
                m_centerBranch.growBranch(step - 1, this);
            }
        }
    }
    
    /**
     * Find out the maturity level of our branch.  i.e. how long it's been 
     * growing in steps.
     * @return The maturity level of this branch
     */
    @Override
    public int getMaturityLevel() {
        return m_step;
    }
    
    /**
     * Check to see if we have no children branches
     * @return True if we have no branches
     */
    @Override
    public boolean hasNoBranches () {
        return ((null == m_leftBranch) &&
                (null == m_rightBranch) &&
                (null == m_centerBranch));
    }
    
    /**
     * Check to see if we have children branches
     * @return True if we have branches
     */
    @Override
    public boolean hasBranches () {
        return ((null != m_leftBranch) ||
                (null != m_rightBranch) ||
                (null != m_centerBranch));
    }
    
    /**
     * Check to see if we need to grow out
     * @return True if we do
     */
    @Override
    public boolean needsToGrow () {
        return m_needsToGrow;
    }
    
    /**
     * Check to see if this branch is a leaf.
     * 1) We have no children (are undrawable because we have no length)
     * 2) Have children with no children (are drawable but our children aren't).
     * @return True if this branch is a leaf of the tree
     */
    @Override
    public boolean isTreeLeaf () {
        boolean retval = false;
        
        // If we don't have branches, then we're a leaf (undrawable, but still a leaf)
        if (false == hasBranches()) {
            retval = true;
        } 
        // If we have a branch or two, and either of them doesn't have branches
        // of their own, then we're a drawable leaf
        else if (((null != m_leftBranch) && (true == m_leftBranch.hasNoBranches())) ||
                 ((null != m_rightBranch) && (true == m_rightBranch.hasNoBranches())) ||
                 ((null != m_centerBranch) && (true == m_centerBranch.hasNoBranches()))) {
            retval = true;
        }
        
        return retval;
    }
    
    /**
     * Check to see if this branch is part of the trunk.
     * @return True if this branch is part of the trunk
     */
    @Override
    public boolean isPartOfTrunk () {
        return m_isTrunk;
    }
    
    /**
     * Don't call directly!!  Use the method in the tree class
     * Remove children from this branch down part of the branch.  Need to pass in
     * the branch so we know if it's the left or right side we're removing
     * @param branch The branch to remove and all it's children.
     */
    @Override
    public void prune (BonsaiBranchInterface branch) {
        if (null != branch) {
            // This branch will need to grow, all the children will be removed
            m_needsToGrow = true;
            
            // Prune...
            if (branch == m_leftBranch) {
                m_leftBranch.prune(null);
                m_leftBranch = null;
            } else if (branch == m_rightBranch) {
                m_rightBranch.prune(null);
                m_rightBranch = null;
            } else if (branch == m_centerBranch) {
                m_centerBranch.prune(null);
                m_centerBranch = null;
            }
        } else {
            // Else just prune it all off
            if (null != m_leftBranch) {
                m_leftBranch.prune (null);
                m_leftBranch = null;
            }
            if (null != m_rightBranch) {
                m_rightBranch.prune (null);
                m_rightBranch = null;
            }
            if (null != m_centerBranch) {
                m_centerBranch.prune (null);
                m_centerBranch = null;
            }
        }
    }
    
    /**
     * Get the left branch, can be null
     * @return The branch or null
     */
    @Override
    public BonsaiBranchInterface getLeftBranch() {
        return m_leftBranch;
    }
    
    /**
     * Get the right branch, can be null
     * @return The branch or null
     */
    @Override
    public BonsaiBranchInterface getRightBranch() {
        return m_rightBranch;
    }
    
    /**
     * Get the center branch (trunk), can be null
     * @return The center branch or null
     */
    public BonsaiBranchInterface getCenterBranch() {
        return m_centerBranch;
    }
    
    /**
     * Gets the radius for the branch cylinder.  
     * @param branch Since this is a node, we'll get the radius for either the base
     * node or one of the branches (left or right).  Pass in either the root (this) or 
     * one of the children.
     * @param r The step in the growth process we are
     * @return The radius of the cylinder (start or ending radius) for a given growth segment
     */
    private float getRadius(BonsaiBranchBirch branch, int r) {
        final int algo = 2;
        float retval = 0.0f;
        
        if (null != branch) {
            if (1 == algo) {
                retval = .02f * (r  + (getMaturityLevel() - branch.getMaturityLevel()));
            } else if (2 == algo) {
                //retval = 1.0f + (float)(1/-Math.exp(0.02 * (r + (getMaturityLevel() - branch.getMaturityLevel()))));
                retval = 1.0f + (float)(1/-Math.exp(0.01 * (r + (getMaturityLevel() - branch.getMaturityLevel()))));
            } else {
                retval = 1.0f + (float)(1/-(1 + (0.02 * (r + (getMaturityLevel() - branch.getMaturityLevel())))));
            }
        }
        
        return retval;
    }
    
    /**
     * Looking down on base      Looking at side of tree
     * deltaP                    deltaT
     * 
     * 270  |   0                 0   |   90
     *   \  |  /                   \  |  /
     *   +\-|-/+                    \ | /
     *   |  x  |                      |-----135
     *   +/-|-\+                    / | \
     *   /  |  \                   /  |  \
     * 180     90                270  |  180
     *                        ------(BOX)------
     * @param deltaT
     * @param deltaP
     * @param bud
     * @return 
     */
    private BonsaiBranchBirch generateBranch (int deltaT, int deltaP, boolean bud) {
        if (isPartOfTrunk()) {
            //deltaT += randomPositiveAngle(40, 50) * ((Math.random() > 0.5) ? 1 : -1);
            deltaT = (75 * ((deltaT > 0) ? 1 : -1)) + (randomPositiveAngle(0, 10) * ((Math.random() > 0.5) ? 1 : -1));
            //deltaP = 0;
        }
        return generateBranch(deltaT, deltaP, bud, false);
    }
    
    /**
     * Generate a new branch with a t and p delta.
     * @param deltaT Change in t for next branch
     * @param deltaP Change in p for next branch
     * @return A new branch
     */
    private BonsaiBranchBirch generateBranch (int deltaT, int deltaP, boolean bud, boolean trunk) {
        double t1 = m_t0 + deltaT;
        double p1 = m_p0 + deltaP;
        double x1 = 0;
        double y1 = 0;
        double z1 = 0;
        
        // Add in variance
        if (true == M_VARIABLE_BRANCH_LENGTH) {
            if (0.5 > Math.random ()) {
                double length = ((double)TREE_SCALE/2.5) + (Math.random() * ((double)TREE_SCALE * 0.40));
                if (isPartOfTrunk() && trunk == false) { // Shorten horizontal branches
                    length = (double)TREE_SCALE / 4.0;
                } else if (isPartOfTrunk() && trunk == true) { // Trunk is longer than branches
                    length = (double)TREE_SCALE + (Math.random() * ((double)TREE_SCALE * 0.50));
                }
                x1 = m_x0 - (double) length * Math.sin((double) t1 * Math.PI / 180) * Math.cos((double) p1 * Math.PI / 180);
                y1 = m_y0 - (double) length * Math.sin((double) t1 * Math.PI / 180) * Math.sin((double) p1 * Math.PI / 180);
                z1 = m_z0 + (double) length * Math.cos((double) t1 * Math.PI / 180);
            } else {
                double length = (double)TREE_SCALE/2.5 - (Math.random() * ((double)TREE_SCALE * 0.20));
                if (isPartOfTrunk() && trunk == false) { // Shorten horizontal branches
                    length = (double)TREE_SCALE / 4.0;
                } else if (isPartOfTrunk() && trunk == true) { // Trunk is longer than branches
                    length = (double)TREE_SCALE - (Math.random() * ((double)TREE_SCALE * 0.20));
                }
                x1 = m_x0 - (double) length * Math.sin((double) t1 * Math.PI / 180) * Math.cos((double) p1 * Math.PI / 180);
                y1 = m_y0 - (double) length * Math.sin((double) t1 * Math.PI / 180) * Math.sin((double) p1 * Math.PI / 180);
                z1 = m_z0 + (double) length * Math.cos((double) t1 * Math.PI / 180);
            }
        } 
        // No variance
        else {
            x1 = m_x0 - (double) TREE_SCALE * Math.sin((double) t1 * Math.PI / 180) * Math.cos((double) p1 * Math.PI / 180);
            y1 = m_y0 - (double) TREE_SCALE * Math.sin((double) t1 * Math.PI / 180) * Math.sin((double) p1 * Math.PI / 180);
            z1 = m_z0 + (double) TREE_SCALE * Math.cos((double) t1 * Math.PI / 180);
        }
        
        // If we have to grow it means we were pruned, at least make sure 
        // there's a scar on the tree
        int stepSize = needsToGrow() ? Math.max(1, m_step - 2) : m_step - 1;
        
        return new BonsaiBranchBirch (m_tree, this, 
            x1, y1, z1, t1, p1,
            stepSize, trunk, bud
        );
    }
    
    /**
     * Gets a random positive angle between min and max.
     * @param min The minimum acceptable angle (guaranteed not to be lower)
     * @param max The max angle (guaranteed not to exceed)
     * @return The random angle.
     */
    private int randomPositiveAngle (int min, int max) {
        double retval = (Math.random() * (double)(max - min)) + (double)min;
        return (int)retval;
    }
    
    // ------------  JME-Specific Code Below This Line  ------------ //
    private BonsaiBranchBirchGeometry m_geometry = null;
    
    @Override
    public Node render (BonsaiApplication app, AssetManager assetManager, float percent, Node rootNode) {
        float growPercent = (isTreeLeaf()) ? percent : 100.0f;
        
        Node retval = rootNode;
        if (null == retval) {
            retval = new Node ();
            retval.setName(BonsaiBranchBirch.TREE_ROOT_NAME);
        }

        Vector3f p0 = getVector3fStart();
        
        BonsaiBranchBirch[] branches = {m_leftBranch, m_rightBranch, m_centerBranch};
        for (BonsaiBranchBirch branch : branches) {
            if (null != branch) {
                Vector3f pB = branch.getVector3fStart();
                
                double length = pB.distance(p0);
                final float baseRadius = getRadius(this, m_step);
                final float tipRadius = getRadius(branch, m_step);
                
                final int axisSamples = (baseRadius > 0.2f) ? 6 : (baseRadius > 0.1f) ? 5 : 4;
                final int radialSamples = (baseRadius > 0.2f) ? 4 : 4;
                
                Material treeBarkMat = BirchTextureManager.getNewTreeBarkMaterial();

                //Cylinder cyl = new Cylinder(4, 6, baseRadius, tipRadius, (float)length * (((float)growPercent) / 100.0f), true, false);
                ReusableCylinder cyl = BonsaiBranchShapePool.getCylinder(radialSamples, axisSamples, baseRadius, tipRadius, (float)length * (((float)growPercent) / 100.0f), true, false);
                Geometry geo = new BonsaiBranchBirchGeometry(BonsaiBranchJmeInterface.BRANCH_CYLINDER_NAME, cyl, m_tree, this, branch);
                geo.setLocalTranslation(FastMath.interpolateLinear(0.5f * (((float)growPercent) / 100.0f), p0, pB));
                geo.lookAt(pB, Vector3f.UNIT_Y);
                geo.setMaterial(treeBarkMat);
                // TangentBinormalGenerator.generate(cyl); // TODO - Whoa!  This actually looks better
                geo.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
                branch.setCylinderGeometry((BonsaiBranchBirchGeometry)geo);
                retval.attachChild(geo);

                // Add sphere at the ends
                //Sphere sphere = new Sphere(6, 6, baseRadius);
                ReusableSphere sphere = BonsaiBranchShapePool.getSphere(axisSamples, axisSamples, baseRadius);
                geo = new Geometry(BonsaiBranchBirch.BRANCH_TIP_NAME_START, sphere);
                geo.setLocalTranslation(FastMath.interpolateLinear(1.0f * (((float)growPercent) / 100.0f), p0, pB));
                geo.lookAt(pB, Vector3f.UNIT_Y);
                geo.setMaterial(treeBarkMat);
                geo.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
                retval.attachChild(geo);

                //sphere = new Sphere(6, 6, tipRadius);
                sphere = BonsaiBranchShapePool.getSphere(axisSamples, axisSamples, tipRadius);
                geo = new Geometry(BonsaiBranchBirch.BRANCH_TIP_NAME_END, sphere);
                geo.setLocalTranslation(FastMath.interpolateLinear(0.0f * (((float)growPercent) / 100.0f), p0, pB));
                geo.lookAt(pB, Vector3f.UNIT_Y);
                geo.setMaterial(treeBarkMat);
                geo.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
                retval.attachChild(geo);
                
                
                // Add leaves if this is a bud
                if ((true == m_isBud) && (false == DEBUG_NO_LEAVES)) {
                    Node budNode = new Node ("BUD");
                    
                    // TODO - 
                    // Random number of leaves
                    // Angle leaves towards end of tree, normalized-ish to branch
                    // Random size of sphere
                    // Offset sphere or ellipse above the branch
                    // Randomize size of individual leaves.  Size of branch radius?
                    final int NUM_LEAVES = 50;
                    ParticleEmitter leafCluster = new ParticleEmitter (BRANCH_LEAF_NAME, Type.Triangle, NUM_LEAVES);

                    // Shape the leaf cluster
                    if ((null == leafCluster.getShape()) || (leafCluster.getShape() instanceof EmitterPointShape)) {
                        EmitterSphereShape emitSphere = new EmitterSphereShape(Vector3f.ZERO, baseRadius * 100);
                        leafCluster.setShape(emitSphere); // TODO make this shaped more upwards than down
                    } else {
                        ((EmitterSphereShape)leafCluster.getShape()).setRadius(baseRadius * 100);
                    }

                    // Size the leaf cluster
                    // TODO make this more heavily exponential
                    leafCluster.setStartSize(baseRadius * 3);
                    leafCluster.setEndSize(baseRadius * 3);

                    // Move the cluster to the base of the branch
                    leafCluster.setLocalTranslation(FastMath.interpolateLinear(1.0f * (((float)growPercent) / 100.0f), p0, pB));
                    leafCluster.lookAt(pB, Vector3f.UNIT_Y);

                    // Set our material
                    Material leafMat = BirchTextureManager.getLeafMaterial();
                    //Material leafMat = BirchTextureManager.getNewLeafMaterial();

                    // Set and configure our material
                    leafCluster.setMaterial(leafMat);
                    leafCluster.setImagesX(2); // columns
                    leafCluster.setImagesY(2); // rows
                    leafCluster.setGravity(Vector3f.ZERO);
                    leafCluster.setRandomAngle(true);
                    leafCluster.setSelectRandomImage(true);
                    leafCluster.setEnabled(false);
                    leafCluster.setShadowMode(RenderQueue.ShadowMode.Cast);
                    leafCluster.setQueueBucket(RenderQueue.Bucket.Transparent);

                    // Update the model bounds in case we re-use a mesh
                    leafCluster.updateModelBound();
                    leafCluster.updateGeometricState();
                    budNode.attachChild(leafCluster);
                    
                    
                    // DEBUG
                    /*WireSphere ws = new WireSphere();
                    ws.fromBoundingSphere(new BoundingSphere(((EmitterSphereShape)leafCluster.getShape()).getRadius(),
                            ((EmitterSphereShape)leafCluster.getShape()).getCenter()));
                    Geometry wsGeo = new Geometry ("DEBUG", ws);
                    Material wsMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                    wsMat.setColor("Color", ColorRGBA.Blue);
                    wsGeo.setMaterial(wsMat);
                    wsGeo.updateModelBound();
                    wsGeo.updateGeometricState();
                    budNode.attachChild(wsGeo);*/
                    // DEBUG
                    
                    //budNode.setLocalTranslation(FastMath.interpolateLinear(1.0f * (((float)growPercent) / 100.0f), p0, pB));
                    budNode.move(FastMath.interpolateLinear(1.0f * (((float)growPercent) / 100.0f), p0, pB));
                    budNode.lookAt(pB, Vector3f.UNIT_Y);
                    
                    // Emit them all after the translation has happened
                    leafCluster.emitAllParticles();
                    
                    retval.attachChild(budNode);
                }

                branch.render(app, assetManager, percent, retval);
            }
        }
        
        return retval;
    }
    
    /**
     * JME - Turn the start of this branch into a vector
     * @return Vector for the start of this branch's location in space.
     */
    public Vector3f getVector3fStart () {
        return new Vector3f((float)m_x0, (float)m_z0, (float)m_y0);
    }
    
    /**
     * JME - Set the geometry for this tree
     * @param geo The geometry
     */
    public void setCylinderGeometry (BonsaiBranchBirchGeometry geo) {
        m_geometry = geo;
    }
    
    /**
     * JME - Get the geometry for this tree
     */
    public BonsaiBranchBirchGeometry getCylinderGeometry () {
        return m_geometry;
    }
}
