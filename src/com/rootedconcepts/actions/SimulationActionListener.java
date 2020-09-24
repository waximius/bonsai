/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rootedconcepts.actions;

import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.rootedconcepts.BonsaiApplication;
import com.rootedconcepts.camera.SimulationCamera;
import com.rootedconcepts.gui.BonsaiScreenController;
import com.rootedconcepts.music.SoundManager;
import com.rootedconcepts.steam.SteamConstants;
import com.rootedconcepts.steam.SteamController;
import com.rootedconcepts.tree.BonsaiBranchGeometry;
import com.rootedconcepts.tree.BonsaiBranchJmeInterface;

/**
 *
 * @author TheDrydens
 */
public class SimulationActionListener implements ActionListener {
    public static final String ACTION_PRUNE = "prune";
    public static final String ACTION_PRUNE_MODE_TOGGLE = "prunemodetoggle";
    public static final String ACTION_WATER = "water";
    public static final String ACTION_WATER_MODE_TOGGLE = "watermodetoggle";
    public static final String ACTION_MUTE_TOGGLE = "mutetoggle";
    public static final String ACTION_CENTER_CAMERA = "center";
    public static final String ACTION_CENTER_CAMERA_RIGHT_MOUSE = "rmcenter";
    public static final String ACTION_ESCAPE = "escape";
    public static final String ACTION_ZOOM = "zoom";
    
    private Camera m_camera = null;
    private SimulationCamera m_simCam = null;
    private InputManager m_inputManager = null;
    private AssetManager m_assetManager = null;
    private BonsaiScreenController m_screenController = null;
    private Node m_rootNode = null;
    private SteamController m_steamController = null;
    
    private SoundManager m_soundManager = null;
    
    private boolean m_pruningEnabled = true;
    private boolean m_wateringEnabled = false;
    private long m_rmbDownTime = System.currentTimeMillis();
    
    //private JmeCursor m_pruneCursor = null;
    
    public SimulationActionListener (
            Camera camera, SimulationCamera simCam, InputManager inputManager, 
            AssetManager assetManager, SoundManager soundManager, 
            BonsaiScreenController screenController, Node rootNode,
            SteamController steamController) {
        super();
        
        m_camera = camera;
        m_simCam = simCam;
        m_inputManager = inputManager;
        m_assetManager = assetManager;
        m_soundManager = soundManager;
        m_screenController = screenController;
        m_rootNode = rootNode;
        m_steamController = steamController;

        //m_pruneCursor = (JmeCursor) m_assetManager.loadAsset("Interface/Shears.ico");
    }
    
    /**
     * Initialize all the listeners
     */
    public void init() {        
        m_inputManager.addListener(this, SimulationActionListener.ACTION_ESCAPE);
        m_inputManager.addListener(this, SimulationActionListener.ACTION_CENTER_CAMERA);
        m_inputManager.addListener(this, SimulationActionListener.ACTION_CENTER_CAMERA_RIGHT_MOUSE);
        m_inputManager.addListener(this, SimulationActionListener.ACTION_PRUNE);
        m_inputManager.addListener(this, SimulationActionListener.ACTION_PRUNE_MODE_TOGGLE);
        m_inputManager.addListener(this, SimulationActionListener.ACTION_WATER);
        m_inputManager.addListener(this, SimulationActionListener.ACTION_WATER_MODE_TOGGLE);
        m_inputManager.addListener(this, SimulationActionListener.ACTION_MUTE_TOGGLE);
        //m_inputManager.addListener(this, SimulationActionListener.ACTION_ZOOM);
        
        m_inputManager.addMapping(SimulationActionListener.ACTION_ESCAPE,
                new KeyTrigger (KeyInput.KEY_ESCAPE));
        m_inputManager.addMapping(SimulationActionListener.ACTION_CENTER_CAMERA,
                new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE),
                new KeyTrigger (KeyInput.KEY_SPACE));
        m_inputManager.addMapping(SimulationActionListener.ACTION_CENTER_CAMERA_RIGHT_MOUSE,
                new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        m_inputManager.addMapping(SimulationActionListener.ACTION_PRUNE,
                new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        m_inputManager.addMapping(SimulationActionListener.ACTION_PRUNE_MODE_TOGGLE,
                new KeyTrigger (KeyInput.KEY_P));
        m_inputManager.addMapping(SimulationActionListener.ACTION_WATER,
                new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        m_inputManager.addMapping(SimulationActionListener.ACTION_WATER_MODE_TOGGLE,
                new KeyTrigger (KeyInput.KEY_W));
        m_inputManager.addMapping(SimulationActionListener.ACTION_MUTE_TOGGLE,
                new KeyTrigger (KeyInput.KEY_M));
        
        // Camera controls
        //m_inputManager.addMapping(SimulationActionListener.ACTION_ZOOM,
        //        new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true),
        //        new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
    }

    public void onAction(String name, boolean isPressed, float tpf) {
        if (ACTION_ESCAPE.equals(name) && isPressed) {
            m_screenController.escapeKeyPressed();
        }
        else if (true == m_screenController.isHudScreen()) {
            // Prune the selected branch
            if (ACTION_PRUNE.equals(name) && isPressed) {
                if (true == m_pruningEnabled) {
                    Geometry target = getClickedBranch ();
                    if (null != target) {
                        m_steamController.incrementBranchesPruned();
                        if (((BonsaiBranchGeometry)target).isPartOfTrunk()) {
                            m_steamController.incrementTrunkWhacked();
                        }
                        m_steamController.storeStats();
                        
                        ((BonsaiBranchGeometry)target).pruneBranch ();

                        m_soundManager.playClippers();
                    }
                }
            }
            else if (ACTION_WATER.equals(name) && isPressed) {
                if (true == m_wateringEnabled) {
                    Geometry target = getClickedBranch ();
                    if (null != target) {
                        //m_steamController.incrementBranchesPruned();
                        //if (((BonsaiBranchGeometry)target).isPartOfTrunk()) {
                        //    m_steamController.incrementTrunkWhacked();
                        //}
                        //m_steamController.storeStats();
                        
                        ((BonsaiBranchGeometry)target).waterBranch ();
                        
                        m_screenController.setGrowthBarColor(BonsaiScreenController.GrowthBarType.GROWTH_BLUE);

                        m_soundManager.playWateringCan();
                    }
                }
            }
            // Center the camera on the object clicked
            else if (ACTION_CENTER_CAMERA.equals(name) && isPressed) {
                Geometry target = getClickedBranch ();
                if (null != target) {
                    Node camNode = (Node)m_rootNode.getChild(BonsaiApplication.CAMERA_NODE_NAME);
                    Vector3f pos = camNode.getLocalTranslation();
                    camNode.setLocalTranslation(FastMath.interpolateLinear(1f, pos, target.getLocalTranslation()));
                }
            }
            // Center the camera on the object clicked
            else if ((ACTION_CENTER_CAMERA_RIGHT_MOUSE.equals(name)) && 
                     (false == isPressed)) {
                // Compare the time since RMB down
                if ((System.currentTimeMillis() - m_rmbDownTime) < 250) {
                    Geometry target = getClickedBranchOrLeaf ();
                    if (null != target) {
                        Node camNode = (Node)m_rootNode.getChild(BonsaiApplication.CAMERA_NODE_NAME);
                        Vector3f pos = camNode.getLocalTranslation();
                        camNode.setLocalTranslation(FastMath.interpolateLinear(1f, pos, target.getLocalTranslation()));
                    }
                }
            }
            // Center the camera on the object clicked
            else if (ACTION_CENTER_CAMERA_RIGHT_MOUSE.equals(name) && isPressed) {
                m_rmbDownTime = System.currentTimeMillis();
            }
            // Center the camera on the object clicked
            else if (ACTION_PRUNE_MODE_TOGGLE.equals(name) && isPressed) {
                m_screenController.togglePruningEnabledGui();
            }
            // Center the camera on the object clicked
            else if (ACTION_WATER_MODE_TOGGLE.equals(name) && isPressed) {
                m_screenController.toggleWateringEnabledGui();
            }
            // Center the camera on the object clicked
            else if (ACTION_MUTE_TOGGLE.equals(name) && isPressed) {
                m_screenController.toggleMute();
            }
            // Center the camera on the object clicked
            else if (ACTION_ZOOM.equals(name)) {
                Geometry target = getClickedBranchOrLeaf ();
                if (null != target) {
                    Node camNode = (Node)m_rootNode.getChild(BonsaiApplication.CAMERA_NODE_NAME);
                    Vector3f pos = camNode.getLocalTranslation();
                    camNode.setLocalTranslation(FastMath.interpolateLinear(1f, pos, target.getLocalTranslation()));
                }
            }
        }
    }
    
    /**
     * Get the ability to prune a tree
     * @return If true, we can prune on click
     */
    public boolean getPruningEnabled () {
        return m_pruningEnabled;
    }
    
    /**
     * Turn on or off the ability to prune a tree
     * @param enabled Pruning enabled
     */
    public void setPruningEnabled (boolean enabled) {
        m_pruningEnabled = enabled;
    }
    
    /**
     * Get the ability to water a tree
     * @return If true, we can water on click
     */
    public boolean getWateringEnabled () {
        return m_wateringEnabled;
    }
    
    /**
     * Turn on or off the ability to water a tree
     * @param enabled Watering enabled
     */
    public void setWateringEnabled (boolean enabled) {
        m_wateringEnabled = enabled;
    }
    
    private Geometry getClickedBranch () {
        CollisionResults results = new CollisionResults();

        // Convert screen click to 3d position
        Vector2f click2d = m_inputManager.getCursorPosition();
        Vector3f click3d = m_camera.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = m_camera.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();

        // Aim the ray from the clicked spot forwards.
        Ray ray = new Ray(click3d, dir);

        // Collect intersections between ray and all nodes in results list.
        m_rootNode.collideWith(ray, results);
        
        Geometry retval = null;
        if (results.size() > 0) {
            CollisionResult prevCollision = null;
            
            for (CollisionResult collision : results) {
                if (collision.getGeometry().getName().equals(BonsaiBranchJmeInterface.BRANCH_CYLINDER_NAME)) {
                    if (null == prevCollision) {
                        prevCollision = collision;
                    }
                    
                    if (collision.getDistance() <= prevCollision.getDistance()) {
                        prevCollision = collision;
                        
                        // Keep this item
                        retval = collision.getGeometry();
                    }
                }
            }
        }
        
        return retval;
    }
    
    private Geometry getClickedBranchOrLeaf () {
        CollisionResults results = new CollisionResults();

        // Convert screen click to 3d position
        Vector2f click2d = m_inputManager.getCursorPosition();
        Vector3f click3d = m_camera.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = m_camera.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();

        // Aim the ray from the clicked spot forwards.
        Ray ray = new Ray(click3d, dir);

        // Collect intersections between ray and all nodes in results list.
        m_rootNode.collideWith(ray, results);
        
        Geometry retval = null;
        if (results.size() > 0) {
            CollisionResult prevCollision = null;
            
            for (CollisionResult collision : results) {
                if (collision.getGeometry().getName().equals(BonsaiBranchJmeInterface.BRANCH_CYLINDER_NAME) ||
                    collision.getGeometry().getName().equals(BonsaiBranchJmeInterface.BRANCH_LEAF_NAME)) {
                    if (null == prevCollision) {
                        prevCollision = collision;
                    }
                    
                    if (collision.getDistance() <= prevCollision.getDistance()) {
                        prevCollision = collision;
                        
                        // Keep this item
                        retval = collision.getGeometry();
                    }
                }
            }
        }
        
        return retval;
    }
    
    private Geometry getBranchUnderMouse () {
        CollisionResults results = new CollisionResults();

        // Convert screen click to 3d position
        Vector3f origin = m_camera.getWorldCoordinates(m_inputManager.getCursorPosition(), 0.0f);
        Vector3f direction = m_camera.getWorldCoordinates(m_inputManager.getCursorPosition(), 0.3f);
        direction.subtractLocal(origin).normalizeLocal();
        
        // Aim the ray from the clicked spot forwards.
        Ray ray = new Ray(origin, direction);

        // Collect intersections between ray and all nodes in results list.
        m_rootNode.collideWith(ray, results);
        
        Geometry retval = null;
        if (results.size() > 0) {
            CollisionResult prevCollision = null;
            
            for (CollisionResult collision : results) {
                if (collision.getGeometry().getName().equals(BonsaiBranchJmeInterface.BRANCH_CYLINDER_NAME)) {
                    if (null == prevCollision) {
                        prevCollision = collision;
                    }
                    
                    if (collision.getDistance() <= prevCollision.getDistance()) {
                        prevCollision = collision;
                        
                        // Keep this item
                        retval = collision.getGeometry();
                    }
                }
            }
        }
        
        return retval;
    }
}
