/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rootedconcepts.actions;

import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.AnalogListener;
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
import com.rootedconcepts.tree.BonsaiBranch;
import com.rootedconcepts.tree.BonsaiBranchGeometry;
import com.rootedconcepts.tree.BonsaiBranchJmeInterface;

/**
 *
 * @author TheDrydens
 */
public class SimulationAnalogListener implements AnalogListener {
    public static final String ACTION_CENTER_CAMERA_LEFT = "centerLeft";
    public static final String ACTION_MOUSE_MOVE = "mouseMoved";
    
    private Camera m_camera = null;
    private SimulationCamera m_simCam = null;
    private InputManager m_inputManager = null;
    private AssetManager m_assetManager = null;
    private BonsaiScreenController m_screenController = null;
    private Node m_rootNode = null;
    
    private SoundManager m_soundManager = null;
    
    private boolean m_pruningEnabled = true;
    private boolean m_wateringEnabled = true;
    private boolean m_enableHighlightOnHover = true;
    
    private BonsaiBranchGeometry m_highlighted = null;
    
    //private JmeCursor m_pruneCursor = null;
    
    public SimulationAnalogListener (
            Camera camera, SimulationCamera simCam, InputManager inputManager, 
            AssetManager assetManager, SoundManager soundManager, BonsaiScreenController screenController, Node rootNode) {
        super();
        
        m_camera = camera;
        m_simCam = simCam;
        m_inputManager = inputManager;
        m_assetManager = assetManager;
        m_soundManager = soundManager;
        m_screenController = screenController;
        m_rootNode = rootNode;

        //m_pruneCursor = (JmeCursor) m_assetManager.loadAsset("Interface/Shears.ico");
    }
    
    /**
     * Initialize all the listeners
     */
    public void init() {
        m_inputManager.addListener(this, SimulationAnalogListener.ACTION_CENTER_CAMERA_LEFT);
        
        m_inputManager.addMapping(SimulationAnalogListener.ACTION_CENTER_CAMERA_LEFT,
                new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        
        if (true == SimulationCamera.AUTO_ROTATE_CAMERA_ON_IDLE) {
            m_inputManager.addListener(this, SimulationAnalogListener.ACTION_MOUSE_MOVE);
            
            m_inputManager.addMapping(SimulationAnalogListener.ACTION_MOUSE_MOVE,
                    new MouseAxisTrigger(MouseInput.AXIS_X, true),
                    new MouseAxisTrigger(MouseInput.AXIS_X, false),
                    new MouseAxisTrigger(MouseInput.AXIS_Y, true),
                    new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        }
    }

    public void onAnalog(String name, float value, float tpf) {
        // Center the camera on the object clicked
        /*if ((ACTION_CENTER_CAMERA_LEFT.equals(name)) && (false == m_pruningEnabled)) {
            Geometry target = getClickedBranch ();
            if (null != target) {
                Node camNode = (Node)m_rootNode.getChild(BonsaiApplication.CAMERA_NODE_NAME);
                Vector3f pos = camNode.getLocalTranslation();
                camNode.setLocalTranslation(FastMath.interpolateLinear(1f, pos, target.getLocalTranslation()));
            }
        }
        // Center the camera on the object clicked
        else*/ if (ACTION_MOUSE_MOVE.equals(name)) {
            if (null != m_simCam) {
                m_simCam.resetAutoRotateCounter();
            }
        }
    }
    
    /**
     * Turn on or off tree glow on hover
     * @param enable If true, tree glow will happen
     */
    public void setHighlightOnHoverEnabled(boolean enable) {
        m_enableHighlightOnHover = enable;
    }
    
    /**
     * Call this from the main update loop to highlight any hovered over branches.
     */
    public void highlightOnHover () {
        if (m_inputManager.isCursorVisible()) {
            Geometry target = getBranchUnderMouse();
            if ((null != target) && (getPruningEnabled() || getWateringEnabled()) && (true == m_enableHighlightOnHover)) {
                BonsaiBranchGeometry targetBranch = (BonsaiBranchGeometry)target;

                // Turn off highlighting for the previous object first
                if (null != m_highlighted) {
                    //m_highlighted.getMaterial().clearParam("GlowColor");
                    m_highlighted.setBranchesHighlighted(false);
                }

                m_highlighted = targetBranch;
                //m_highlighted.getMaterial().setColor("GlowColor", new ColorRGBA(0.7f, 0.7f, 0.6f, 1.0f));
                m_highlighted.setBranchesHighlighted(true);
                
                // Change mouse cursor
                //m_inputManager.setMouseCursor(m_pruneCursor);
            } 
            // Turn off the highlight if there's nothing under the mouse
            else {
                // Un-highlight the branch
                if (null != m_highlighted) {
                    //m_highlighted.getMaterial().clearParam("GlowColor");
                    m_highlighted.setBranchesHighlighted(false);
                }
                
                // Change Mouse cursor
                //m_inputManager.setMouseCursor(null);
            }
        }
    }
    
    /**
     * TODO - Set glow for this branch and all children
     * @param glowOn If true, turn on the glow
     */
    private void setGlowForBranch(boolean glowOn) {
        if (glowOn) {
            //m_highlighted.
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
    
    private Geometry getClickedGeometry () {
        CollisionResults results = new CollisionResults();

        // Convert screen click to 3d position
        Vector2f click2d = m_inputManager.getCursorPosition();
        Vector3f click3d = m_camera.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
        Vector3f dir = m_camera.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalizeLocal();

        // Aim the ray from the clicked spot forwards.
        Ray ray = new Ray(click3d, dir);

        // Collect intersections between ray and all nodes in results list.
        m_rootNode.collideWith(ray, results);

        // (Print the results so we see what is going on:)
        /*for (int i = 0; i < results.size(); i++) {
            // (For each hit, we know distance, impact point, geometry.)
            float dist = results.getCollision(i).getDistance();
            Vector3f pt = results.getCollision(i).getContactPoint();
            String target = results.getCollision(i).getGeometry().getName();
            System.out.println("Selection #" + i + ": " + target + " at " + pt + ", " + dist + " WU away.");
        }*/
        
        Geometry retval = null;
        if (results.size() > 0) {
            // The closest result is the target that the player picked:
            retval = results.getClosestCollision().getGeometry();
            
        }
        
        return retval;
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

        // (Print the results so we see what is going on:)
        /*for (int i = 0; i < results.size(); i++) {
            // (For each hit, we know distance, impact point, geometry.)
            float dist = results.getCollision(i).getDistance();
            Vector3f pt = results.getCollision(i).getContactPoint();
            String target = results.getCollision(i).getGeometry().getName();
            System.out.println("Selection #" + i + ": " + target + " at " + pt + ", " + dist + " WU away.");
        }*/
        
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
    
    private Geometry getGeometryUnderMouse () {
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
            // The closest result is the target that the player picked:
            retval = results.getClosestCollision().getGeometry();
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
