/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rootedconcepts;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.texture.Texture;
import com.rootedconcepts.enums.GraphicsLevel;

/**
 *
 * @author TheDrydens
 */
public class TextureManager {
    BonsaiApplication m_app = null;
    AssetManager m_assetManager = null;
    
    Material m_highGrass = null;
    Material m_medGrass = null;
    Material m_lowGrass = null;
    
    Material m_highSoil = null;
    Material m_medSoil = null;
    Material m_lowSoil = null;
    
    ColorRGBA m_darkerWhite = new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f);
    
    public TextureManager (BonsaiApplication app, AssetManager assetManager) {
        m_app = app;
        m_assetManager = assetManager;
        
        initGrass();
        initSoil();
    }
    
    /**
     * Get the grass material
     * @return The grass material
     */
    public Material getGrassMaterial () {
        Material retval = null;
        if (GraphicsLevel.High == m_app.getGraphicsLevel()) {
            retval = m_highGrass;
        } else if (GraphicsLevel.Medium == m_app.getGraphicsLevel()) {
            retval = m_medGrass;
        } else { // Low
            retval = m_lowGrass;
        }
        return retval;
    }
    
    /**
     * Get a new grass material
     * @return The grass material
     */
    public Material getNewGrassMaterial () {
        return getGrassMaterial().clone();
    }
    
    /**
     * Get the soil material
     * @return The soil material
     */
    public Material getSoilMaterial () {
        Material retval = null;
        if (GraphicsLevel.High == m_app.getGraphicsLevel()) {
            retval = m_highSoil;
        } else if (GraphicsLevel.Medium == m_app.getGraphicsLevel()) {
            retval = m_medSoil;
        } else { // Low
            retval = m_lowSoil;
        }
        return retval;
    }
    
    /**
     * Get a new soil material
     * @return The soil material
     */
    public Material getNewSoilMaterial () {
        return getSoilMaterial().clone();
    }
    
    private void initGrass () {
        Material mat = new Material(m_assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture tex = m_assetManager.loadTexture("Textures/Ground/Grass256.jpg");
        mat.setTexture("ColorMap", tex);
        mat.setColor("Color", m_darkerWhite);
        m_lowGrass = mat;
        
        mat = new Material(m_assetManager, "Common/MatDefs/Light/Lighting.j3md");
        tex = m_assetManager.loadTexture("Textures/Ground/Grass256.jpg");
        mat.setTexture("DiffuseMap", tex);
        tex = m_assetManager.loadTexture("Textures/Ground/GrassBump256.jpg");
        mat.setTexture("NormalMap", tex);
        mat.setReceivesShadows(false);
        mat.setColor("Diffuse", ColorRGBA.White);
        mat.setColor("Specular", ColorRGBA.White);
        mat.setFloat("Shininess", 20f);  // [0,128]
        mat.clearParam("GlowColor");
        m_medGrass = mat;
        
        mat = mat.clone();
        tex = m_assetManager.loadTexture("Textures/Ground/Grass512.jpg");
        mat.setTexture("DiffuseMap", tex);
        tex = m_assetManager.loadTexture("Textures/Ground/GrassBump512.jpg");
        mat.setTexture("NormalMap", tex);
        //tex = m_assetManager.loadTexture("Textures/Ground/GrassSpec512.jpg");
        //mat.setTexture("SpecularMap", tex);
        //tex = m_assetManager.loadTexture("Textures/Ground/GrassDisp512.jpg");
        //mat.setTexture("ParallaxMap", tex);
        mat.setReceivesShadows(true);
        m_highGrass = mat;
    }
    
    private void initSoil () {
        Material mat = new Material(m_assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture tex = m_assetManager.loadTexture("Textures/Ground/Soil256.jpg");
        mat.setTexture("ColorMap", tex);
        mat.setColor("Color", m_darkerWhite);
        m_lowSoil = mat;
        
        mat = new Material(m_assetManager, "Common/MatDefs/Light/Lighting.j3md");
        tex = m_assetManager.loadTexture("Textures/Ground/Soil256.jpg");
        mat.setTexture("DiffuseMap", tex);
        tex = m_assetManager.loadTexture("Textures/Ground/SoilBump256.jpg");
        mat.setTexture("NormalMap", tex);
        mat.setReceivesShadows(false);
        mat.setColor("Diffuse", ColorRGBA.White);
        mat.setColor("Specular", ColorRGBA.White);
        mat.setFloat("Shininess", 20f);  // [0,128]
        mat.clearParam("GlowColor");
        m_medSoil = mat;
        
        mat = mat.clone();
        tex = m_assetManager.loadTexture("Textures/Ground/Soil512.jpg");
        mat.setTexture("DiffuseMap", tex);
        tex = m_assetManager.loadTexture("Textures/Ground/SoilBump512.jpg");
        mat.setTexture("NormalMap", tex);
        //tex = m_assetManager.loadTexture("Textures/Ground/SoilSpec512.jpg");
        //mat.setTexture("SpecularMap", tex);
        //tex = m_assetManager.loadTexture("Textures/Ground/SoilDisp512.jpg");
        //mat.setTexture("ParallaxMap", tex);
        mat.setReceivesShadows(true);
        m_highSoil = mat;
    }
}
