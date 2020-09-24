/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rootedconcepts.deprecated;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import java.util.ArrayList;

/**
 *
 * @author TheDrydens
 */
public class BonsaiTree {

    private final int ALPHA = 0;
    private final int BETA = 1;
    private final int PHI = 2;
    
    private final int m_alpha = 32;
    private final int m_beta = 25;
    private final int m_phi = 25;
    private final int m_scale = 1;
    
    private boolean m_growFlag = true;
    
    private String m_str = "A(-5)";
    private StringBuilder m_strNext = new StringBuilder("");
    
    private int m_stack = 0;
    private int m_step = 0;
    private final int M_STEPMAX = 15;
    
    private final int MAX = 100;
    private double x[] = new double[MAX];
    private double y[] = new double[MAX];
    private double z[] = new double[MAX];
    private int t[] = new int[MAX];
    private int p[] = new int[MAX];
    
    private final AssetManager m_assetManager;
    private final Node m_rootNode;
    private int m_percent = 100;
    
    private ArrayList<Spatial> m_currentTree = new ArrayList();
    private ArrayList<Spatial> m_oldTree = new ArrayList();

    public BonsaiTree(AssetManager assetManager, Node rootNode) {
        m_assetManager = assetManager;
        m_rootNode = rootNode;
    }

    /**
     * Causes the mesh to grow by one growth phase
     */
    public void grow() {
        if (m_growFlag) {
            m_step++;
        }
        
        // Update the tree one growth step
        x[0] = 0;
        y[0] = 0;
        z[0] = 0;
        t[0] = 0;
        p[0] = 45;
        m_strNext.setLength(0);
        read();
        m_str = m_strNext.toString();
        
        // Clean up the old tree model
        if (m_oldTree.size() > 0) {
            for(Spatial spatial : m_oldTree) {
                m_rootNode.detachChild(spatial);
            }
        }
        m_oldTree.clear();
        ArrayList<Spatial> tmp = m_oldTree;
        m_oldTree = m_currentTree;
        m_currentTree = tmp;

        if (m_step >= M_STEPMAX) {
            m_growFlag = false;
        }
        
        /*if (m_growFlag) {
            Log.debugln(m_str);
            Log.debugln("------");
        }*/
    }
    
    public void growPartial(int percent) {
        if (false != m_growFlag) {
            boolean growth = m_growFlag;
            m_percent = percent;
            m_growFlag = false;
            grow();
            m_percent = 100;
            m_growFlag = growth;
        }
    }

    /**
     * Read the next value of the string to decode
     */
    private void read() {
        int len = m_str.length(); //get length of string pn
        int now = 0;

        // Decode string one by one
        while (now < len) {
            char type = m_str.charAt(now);
            if (type == '[') {
                m_stack++;
                x[m_stack] = x[0];
                y[m_stack] = y[0];
                z[m_stack] = z[0];
                t[m_stack] = t[0];
                p[m_stack] = p[0];
                m_strNext.append("[");
            } else if (type == ']') {
                x[0] = x[m_stack];
                y[0] = y[m_stack];
                z[0] = z[m_stack];
                t[0] = t[m_stack];
                p[0] = p[m_stack];
                m_stack--;
                m_strNext.append("]");
            } else {
                now++;
                String str_buf = m_str.substring(now + 1, len);
                int endp = str_buf.indexOf(")");
                int param = java.lang.Integer.parseInt(str_buf.substring(0, endp));
                now += (endp + 1);
                decode(type, param);
            }
            now++;
        }
    }

    /**
     * Decode the input to see if this is a forward, apex or bud step.  Etc.
     * @param type The type of action.
     * @param param  The value for the param
     */
    private void decode(char type, int param) {
        if (type == 'F') {
            forward(param);
        } else if (type == 'A') {
            apex(param);
        } else if (type == 'B') {
            bud(param);
        } else if (type == '+') {
            t[0] = (t[0] + param);
            m_strNext.append("+(").append(param).append(")");
        } else if (type == '-') {
            t[0] = (t[0] - param);
            m_strNext.append("-(").append(param).append(")");
        } else if (type == '/') {
            p[0] = (p[0] + param);
            m_strNext.append("/(").append(param).append(")");
        }
    }

    /**
     * Grow the tree forward one step.
     * @param r The step in the growth process.
     */
    public void forward(int r) {
        double x1 = x[0] - (double) m_scale * Math.sin((double) t[0] * Math.PI / 180) * Math.cos((double) p[0] * Math.PI / 180);
        double y1 = y[0] - (double) m_scale * Math.sin((double) t[0] * Math.PI / 180) * Math.sin((double) p[0] * Math.PI / 180);
        double z1 = z[0] + (double) m_scale * Math.cos((double) t[0] * Math.PI / 180);
        
        float percent = (r > 1) ? 100.0f : m_percent;
        //Log.debugln("Step=" + String.valueOf(m_step) + ": r=" + String.valueOf(r));

        Vector3f p0 = new Vector3f((float) x[0], (float) z[0], (float) y[0]);
        Vector3f p1 = new Vector3f((float) x1, (float) z1, (float) y1);
        Cylinder cyl = new Cylinder(4, 8, getRadius(true, r), getRadius(false, r), p0.distance(p1) * (((float)percent) / 100.0f), true, false);
        Geometry geo = new Geometry("Branch", cyl);
        geo.setLocalTranslation(FastMath.interpolateLinear(0.5f * (((float)percent) / 100.0f), p0, p1));
        geo.lookAt(p1, Vector3f.UNIT_Y);
        Material mat = new Material(m_assetManager, "Common/MatDefs/Light/Lighting.j3md");
        Texture tex = m_assetManager.loadTexture("Textures/barkthin_low.jpg");
        tex.setWrap(Texture.WrapMode.Repeat);
        mat.setTexture("DiffuseMap", tex);
        mat.setColor("Diffuse", ColorRGBA.White);
        mat.setColor("Specular", ColorRGBA.White);
        mat.setFloat("Shininess", 20f);  // [0,128]
        geo.setMaterial(mat);
        geo.getMesh().scaleTextureCoordinates(new Vector2f(3, 8));
        m_rootNode.attachChild(geo);
        m_currentTree.add(geo);
        
        // Add sphere at the end unless this is a leaf
        if (r < m_step - 1) {
            Sphere sphere = new Sphere(8, 8, getRadius(true, r));
            geo = new Geometry("BranchEnd", sphere);
            geo.setLocalTranslation(FastMath.interpolateLinear(1.0f * (((float)m_percent) / 100.0f), p0, p1));
            geo.lookAt(p1, Vector3f.UNIT_Y);
            geo.setMaterial(mat);
            geo.getMesh().scaleTextureCoordinates(new Vector2f(1, 1));
            m_rootNode.attachChild(geo);
            m_currentTree.add(geo);
        }

        x[0] = x1;
        y[0] = y1;
        z[0] = z1;

        if (m_growFlag) {
            m_strNext.append("F(").append(r + 1).append(")");
        } else {
            m_strNext.append("F(").append(r).append(")");
        }
    }

    /**
     * The apex of the tree, i.e. determine how the split should occur.  One branch or two and what orientation.
     * @param k The apex constant.  Dunno what this means yet.
     */
    private void apex(int k) {
        if (m_growFlag) {
            if (k <= 0) {
                m_strNext.append("F(1)A(").append(k + 1).append(")");
            } else {
                double prob = Math.min(1.0, (2 * (double) k + 1) / Math.pow((double) k, 2));
                // Draws a 2d tree with no depth
                /*if (0.5 > Math.random()) {
                 //p1
                 if (prob > Math.random()) {
                 m_strNext += "[+(" + (-getAngle(ALPHA)) + ")F(1)A(" + (k + 1) + ")]-(" + (-getAngle(BETA)) + ")F(1)A(" + (k + 1) + ")";
                 } //p2
                 else {
                 m_strNext += "B(1)-(" + (-getAngle(BETA)) + ")F(1)A(" + (k + 1) + ")";
                 }
                 } else {
                 //p1
                 if (prob > Math.random()) {
                 m_strNext += "[+(" + getAngle(ALPHA) + ")F(1)A(" + (k + 1) + ")]-(" + getAngle(BETA) + ")F(1)A(" + (k + 1) + ")";
                 } //p2
                 else {
                 m_strNext += "B(1)-(" + getAngle(BETA) + ")F(1)A(" + (k + 1) + ")";
                 }
                 }*/
                // Draws a 3d tree
                if (0.5 > Math.random()) {
                    if (0.5 > Math.random()) {
                        //p1
                        if (prob > Math.random()) {
                            m_strNext.append("/(").append(getAngle(PHI)).append(")[+(").
                                      append(-getAngle(ALPHA)).append(")F(1)A(").append(k + 1).
                                      append(")]-(").append(-getAngle(BETA)).append(")F(1)A(").
                                      append(k + 1).append(")");
                        } //p2
                        else {
                            m_strNext.append("/(").append(getAngle(PHI)).append(")B(1)-(").
                                      append(-getAngle(BETA)).append(")F(1)A(").append(k + 1).
                                      append(")");
                        }
                    } else {
                        //p1
                        if (prob > Math.random()) {
                            m_strNext.append("/(").append(getAngle(PHI)).append(")[+(").
                                      append(getAngle(ALPHA)).append(")F(1)A(").append(k + 1).
                                      append(")]-(").append(getAngle(BETA)).append(")F(1)A(").
                                      append(k + 1).append(")");
                        } //p2
                        else {
                            m_strNext.append("/(").append(getAngle(PHI)).append(")B(1)-(").
                                      append(getAngle(BETA)).append(")F(1)A(").append(k + 1).
                                      append(")");
                        }
                    }
                } else {
                    if (0.5 > Math.random()) {
                        //p1
                        if (prob > Math.random()) {
                            m_strNext.append("/(").append(-getAngle(PHI)).append(")[+(").
                                      append(-getAngle(ALPHA)).append(")F(1)A(").append(k + 1).
                                      append(")]-(").append(-getAngle(BETA)).append(")F(1)A(").
                                      append(k + 1).append(")");
                        } //p2
                        else {
                            m_strNext.append("/(").append(-getAngle(PHI)).append(")B(1)-(").
                                      append(-getAngle(BETA)).append(")F(1)A(").append(k + 1).
                                      append(")");
                        }
                    } else {
                        //p1
                        if (prob > Math.random()) {
                            m_strNext.append("/(").append(-getAngle(PHI)).append(")[+(").
                                      append(getAngle(ALPHA)).append(")F(1)A(").append(k + 1).
                                      append(")]-(").append(getAngle(BETA)).append(")F(1)A(").
                                      append(k + 1).append(")");
                        } //p2
                        else {
                            m_strNext.append("/(").append(-getAngle(PHI)).append(")B(1)-(").
                                      append(getAngle(BETA)).append(")F(1)A(").append(k + 1).
                                      append(")");
                        }
                    }
                }
            }
        } else {
            m_strNext.append("A(").append(k).append(")");
        }
    }

    /**
     * Draw a bud on the tree
     * @param s Size of buds
     */
    private void bud(int s) {
        //start draw circle
        //xp0 = (int) ((double) w / 2 + y[0]);
        //yp0 = (int) ((double) h - z[0]);
        //buf_g.setColor(Color.green);
        //buf_g.fillOval(xp0,yp0,s,s);
        //end draw cirle
        if (m_growFlag) {
            m_strNext.append("B(").append(s + 1).append(")");
        } else {
            m_strNext.append("B(").append(s).append(")");
        }
    }
    
    /**
     * Gets the radius for a cylinder.  
     * @param start The starting radius for the cylinder (base)
     * @param r The step in the growth process we are
     * @return The radius of the cylinder (start or ending radius) for a given growth segment
     */
    private float getRadius(boolean branchBase, int r) {
        final int algo = 2;
        float retval = 0.0f;
        
        if (true == branchBase) {
            if (1 == algo) {
                retval = .02f * r;
            } else if (2 == algo) {
                retval = 1.0f + (float)(1/-Math.exp(0.02 * (r)));
            } else {
                retval = 1.0f + (float)(1/-(1 + (0.02 * (r))));
            }
        } else {
            if (1 == algo) {
                retval = (.02f * (r + 1));
            } else if (2 == algo) {
                retval = 1.0f + (float)(1/-Math.exp(0.02 * (r + 1)));
            } else {
                retval = 1.0f + (float)(1/-(1 + (0.02 * (r + 1))));
            }
        }
        
        return retval;
    }
    
    /**
     * Get alpha, beta, or phi, randomly.  Always positive
     * @param value 0 = alpha, 1 = beta, 2 = phi
     * @return The angle
     */
    private int getAngle(int value) {
        int retval = 0;
        if (0 == value) {
            //retval = m_alpha;
            retval = (int)(Math.random() * (float)(m_alpha - 17)) + 17;
        } else if (1 == value) {
            //retval = m_beta;
            retval = (int)(Math.random() * (float)(m_beta - 17)) + 17;
        } else if (2 == value) {
            //retval = m_phi;
            retval = (int)(Math.random() * (float)(m_phi - 17)) + 17;
        }
        return retval;
    }
}
