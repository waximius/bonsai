/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rootedconcepts.tree.birch;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.texture.Texture;
import com.rootedconcepts.BonsaiApplication;
import com.rootedconcepts.enums.GraphicsLevel;

/**
 *
 * @author TheDrydens
 */
public class BirchTextureManager {
    private static BonsaiApplication m_app = null;
    private static AssetManager m_assetManager = null;
    
    private static Material m_highBark = null;
    private static Material m_medBark = null;
    private static Material m_lowBark = null;
    
    private static Material m_highLeaf = null;
    private static Material m_medLeaf = null;
    private static Material m_lowLeaf = null;
    
    private static boolean m_initted = false;
    
    private static ColorRGBA m_darkerWhite = new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f);
    
    public static void init (BonsaiApplication app, AssetManager assetManager) {
        if (false == m_initted) {
            m_app = app;
            m_assetManager = assetManager;

            initTreeBark ();
            initLeaves ();
        
            m_initted = true;
        }
    }
    
    /**
     * Get the tree bark material
     * @return The tree bark material
     */
    public static Material getTreeBarkMaterial () {
        Material retval = null;
        if (GraphicsLevel.High == m_app.getGraphicsLevel()) {
            retval = m_highBark;
        } else if (GraphicsLevel.Medium == m_app.getGraphicsLevel()) {
            retval = m_medBark;
        } else { // Low
            retval = m_lowBark;
        }
        return retval;
    }
    
    /**
     * Get a new tree bark material
     * @return The tree bark material
     */
    public static Material getNewTreeBarkMaterial () {
        return getTreeBarkMaterial().clone();
    }
    
    /**
     * Get the leaf material
     * @return The leaf material
     */
    public static Material getLeafMaterial () {
        Material retval = null;
        if (GraphicsLevel.High == m_app.getGraphicsLevel()) {
            retval = m_highLeaf;
        } else if (GraphicsLevel.Medium == m_app.getGraphicsLevel()) {
            retval = m_medLeaf;
        } else { // Low
            retval = m_lowLeaf;
        }
        return retval;
    }
    
    /**
     * Get a new leaf material
     * @return The leaf material
     */
    public static Material getNewLeafMaterial () {
        return getLeafMaterial().clone();
    }
    
    /**
     * Load the tree bark materials and textures
     */
    private static void initTreeBark () {
        Material mat = new Material(m_assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture tex = m_assetManager.loadTexture("Textures/Tree/Bark/Birch/Bark128.jpg");
        mat.setTexture("ColorMap", tex);
        mat.setColor("Color", m_darkerWhite);
        m_lowBark = mat;
        
        mat = new Material(m_assetManager, "Common/MatDefs/Light/Lighting.j3md");
        tex = m_assetManager.loadTexture("Textures/Tree/Bark/Birch/Bark256.jpg");
        mat.setTexture("DiffuseMap", tex);
        tex = m_assetManager.loadTexture("Textures/Tree/Bark/Birch/BarkBump256.jpg");
        mat.setTexture("NormalMap", tex);
        mat.setColor("Diffuse", ColorRGBA.White);
        mat.setColor("Specular", ColorRGBA.White);
        mat.setFloat("Shininess", 20f);  // [0,128]
        mat.clearParam("GlowColor");
        mat.setReceivesShadows(false);
        m_medBark = mat;
        
        mat = mat.clone();
        tex = m_assetManager.loadTexture("Textures/Tree/Bark/Birch/Bark512.jpg");
        mat.setTexture("DiffuseMap", tex);
        tex = m_assetManager.loadTexture("Textures/Tree/Bark/Birch/BarkBump512.jpg");
        mat.setTexture("NormalMap", tex);
        /*tex = m_assetManager.loadTexture("Textures/Tree/Bark/Birch/BarkSpec512.jpg");
        mat.setTexture("SpecularMap", tex);
        tex = m_assetManager.loadTexture("Textures/Tree/Bark/Birch/BarkDisp512.jpg");
        mat.setTexture("ParallaxMap", tex);*/
        mat.setReceivesShadows(true);
        m_highBark = mat;
    }
    
    /**
     * Load the leaf materials and textures
     */
    private static void initLeaves () {        
        Material mat = new Material(m_assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture tex = m_assetManager.loadTexture("Textures/Tree/Leaves/Birch/Leaf128x4.png");
        mat.setTexture("ColorMap", tex);
        mat.setColor("Color", m_darkerWhite);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        mat.getAdditionalRenderState().setAlphaFallOff(0.1f);
        mat.getAdditionalRenderState().setDepthWrite(true);
        mat.getAdditionalRenderState().setDepthTest(true);
        mat.getAdditionalRenderState().setAlphaTest(true);
        mat.getAdditionalRenderState().setColorWrite(true);
        mat.getAdditionalRenderState().setPolyOffset(-1f, -1f); // Z-Fighting fix
        m_lowLeaf = mat;
        
        mat = new Material(m_assetManager, "Common/MatDefs/Light/Lighting.j3md");
        tex = m_assetManager.loadTexture("Textures/Tree/Leaves/Birch/Leaf256x4.png");
        //tex.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        //tex.setMagFilter(Texture.MagFilter.Nearest);
        mat.setTexture("DiffuseMap", tex);
        tex = m_assetManager.loadTexture("Textures/Tree/Leaves/Birch/LeafBump256x4.png");
        //tex.setMinFilter(Texture.MinFilter.NearestNoMipMaps);
        //tex.setMagFilter(Texture.MagFilter.Nearest);
        mat.setTexture("NormalMap", tex);
        mat.setReceivesShadows(false);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        mat.getAdditionalRenderState().setAlphaFallOff(0.1f);
        mat.getAdditionalRenderState().setDepthWrite(true);
        mat.getAdditionalRenderState().setDepthTest(true);
        mat.getAdditionalRenderState().setAlphaTest(true);
        mat.getAdditionalRenderState().setColorWrite(true);
        //mat.setBoolean("UseAlpha",true);
        mat.setColor("Diffuse", ColorRGBA.White);
        mat.setColor("Specular", ColorRGBA.White);
        mat.setFloat("Shininess", 1f);  // [0,128]
        mat.clearParam("GlowColor");
        m_medLeaf = mat;
        
        mat = mat.clone();
        mat.setReceivesShadows(true);
        m_highLeaf = mat;
    }
}
