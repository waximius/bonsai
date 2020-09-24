package com.rootedconcepts;

import com.rootedconcepts.tree.maple.BonsaiTreeMaple;
import com.rootedconcepts.actions.SimulationAnalogListener;
import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.post.Filter;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.SceneProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Dome;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.system.AppSettings;
import com.jme3.util.BufferUtils;
import com.rootedconcepts.actions.SimulationActionListener;
import com.rootedconcepts.camera.SimulationCamera;
import com.rootedconcepts.enums.GraphicsLevel;
import com.rootedconcepts.gui.BonsaiScreenController;
import com.rootedconcepts.music.SoundManager;
import com.rootedconcepts.steam.SteamConstants;
import com.rootedconcepts.steam.SteamController;
import com.rootedconcepts.tree.BonsaiTree;
import com.rootedconcepts.tree.birch.BirchTextureManager;
import com.rootedconcepts.tree.birch.BonsaiTreeBirch;
import com.rootedconcepts.tree.japmaple.BonsaiTreeJapMaple;
import com.rootedconcepts.tree.japmaple.JapMapleTextureManager;
import com.rootedconcepts.tree.maple.MapleTextureManager;
import com.rootedconcepts.tree.oak.BonsaiTreeOak;
import com.rootedconcepts.tree.oak.OakTextureManager;
import com.rootedconcepts.tree.shapes.BonsaiBranchShapePool;
import com.rootedconcepts.tree.sycamore.BonsaiTreeSycamore;
import com.rootedconcepts.tree.sycamore.SycamoreTextureManager;
import de.lessvoid.nifty.Nifty;
import java.util.Calendar;
import jme3utilities.Misc;
import jme3utilities.TimeOfDay;
import jme3utilities.sky.SkyControl;

/**
 *
 * @author TheDrydens
 */
public class BonsaiApplication extends SimpleApplication {
    public static final String VERSION = "1.0";
    
    public static final boolean FORCE_PAUSE = false;
    
    public static final boolean CINEMATIC_MODE = false;
    
    // 0 = Random Species
    // ! DEBUG VALUES !
    // 1 = Maple
    // 2 = JapMaple
    // 3 = Sycamore
    // 4 = Birch
    // 5 = Oak
    public static final int RANDOM_SPECIES = 0;
    
    public static final String CAMERA_NODE_NAME = "CameraNode";

    private static final boolean REAL_TIME_OF_DAY = true;
    private static final float FIXED_HOUR_OF_DAY = 1f; // If not real time of day, then this is the hour
    private static final float HOUR_OF_DAY = 10f; // If "Day Only", then this is the hour
    //private static final float HOUR_OF_DAY = 17f; // If "Day Only", then this is the hour
    private static final float HOUR_OF_NIGHT = 4f; // If "Night Only", then this is the hour
    private static final int MIN_FPS = 30;
    
    private static final int AUTO_SAVE_INTERVAL_MS = 5 * 60 * 1000;
    //private static final int AUTO_SAVE_INTERVAL_MS = 5 * 1000;
    private long m_lastAutoSave = System.currentTimeMillis();
    private boolean m_autoSaveEnabled = false;
    
    private boolean m_initialize = false;
    private boolean m_initialized = false;
    
    private boolean m_maturityTrigger = false;
    private boolean m_nurturerTrigger = false;
    
    private BonsaiTree m_tree;
    private Node m_treeNode = null;
    
    private SkyControl sc;
    private TimeOfDay m_timeOfDay = new TimeOfDay(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
    
    private SimulationCamera m_simCam = null;
    private SimulationAnalogListener m_analogListener = null;
    private SimulationActionListener m_actionListener = null;
    private SoundManager m_soundManager = null;
    private BonsaiScreenController m_screenController = null;
    private FileManager m_fileManager = new FileManager();
    private Nifty m_nifty = null;
    private TextureManager m_textureManager = null;
    private SteamController m_steamController = new SteamController ();
    
    private String m_dayNightCycle = Prefs.get("daynight", "Day and Night");
    private int m_fpsLimit = Prefs.getInt("fpslimit", MIN_FPS);
    
    // Load graphics level from user prefs, this is a horrible way of doing it.
    private int m_testGraphics = Prefs.getInt("graphics", 0); // Default to low
    private GraphicsLevel m_graphicsLevel = ((m_testGraphics == 2) ? GraphicsLevel.High : (m_testGraphics == 1) ? GraphicsLevel.Medium : GraphicsLevel.Low);

    public static void main(String[] args) {
        BonsaiApplication app = new BonsaiApplication();
        
        // Init stuff
        app.setPauseOnLostFocus(false);
        app.setDisplayFps(false);
        app.setDisplayStatView(false);
        app.setShowSettings(false);
        BufferUtils.setTrackDirectMemoryEnabled(true);
        
        // Initialize our settings
        AppSettings appSettings = new DefaultAppSettings().getAppSettings();
        app.setSettings(appSettings);
        
        // Start the app
        app.start();
    }
    
    public void setInitWorld () {
        m_initialize = true;
    }
    
    public boolean getWorldInitted () {
        return m_initialized;
    }
    
    public AppSettings getSettings () {
        return settings;
    }
    
    public SoundManager getSoundManager () {
        return m_soundManager;
    }
    
    public SimulationAnalogListener getAnalogListener () {
        return m_analogListener;
    }
    
    public SimulationActionListener getActionListener () {
        return m_actionListener;
    }
    
    public SimulationCamera getSimulationCamera () {
        return m_simCam;
    }
    
    public TextureManager getTextureManager () {
        return m_textureManager;
    }
    
    public SteamController getSteamController () {
        return m_steamController;
    }
    
    public void setGraphicsLevel (GraphicsLevel level) {
        if (m_graphicsLevel != level) {
            m_graphicsLevel = level;
            Prefs.putInt("graphics", ((m_graphicsLevel == GraphicsLevel.High) ? 2 : (m_graphicsLevel == GraphicsLevel.Medium) ? 1 : 0));
            
            // Redraw our tree, reload textures because graphics level changes
            m_tree.setTreeNeedsDrawn(true);
            renderTree();
            
            // Redraw our ground
            renderIsland();
            
            // Reset the lighting
            initLighting();
        }
    }
    
    public GraphicsLevel getGraphicsLevel () {
        return m_graphicsLevel;
    }
    
    /**
     * Pause or resume the game
     * @param pause If true, pause the game, else unpause
     */
    public void setGamePaused (boolean pause) {
        if (true == pause) {
            m_tree.setGrowthEnabled(false);
        } else {
            m_tree.setGrowthEnabled(true);
        }
    }
    
    @Override
    public void destroy () {
        // Only save the game if we're not on the start screen
        // If we're on the start screen, then we already saved
        // and reset the tree.  Saving will kill it
        if (!m_screenController.isStartScreen()) {
            m_screenController.saveCurrentTree();
        }
        m_steamController.shutdown();
        super.destroy ();
    }

    @Override
    public void simpleInitApp() {
        /*Collection<Caps> caps = renderer.getCaps();
        System.out.println(caps.toString());*/
        
        //setDisplayStatView(true);
        //setDisplayFps(true);
        
        // Start Steam (if enabled)
        m_steamController.start();
        
        // Remove default key bindings
        inputManager.deleteMapping(SimpleApplication.INPUT_MAPPING_EXIT);
        inputManager.deleteMapping(SimpleApplication.INPUT_MAPPING_MEMORY);
        inputManager.deleteMapping(SimpleApplication.INPUT_MAPPING_HIDE_STATS);
        inputManager.deleteMapping(SimpleApplication.INPUT_MAPPING_CAMERA_POS);
        
        resetGame (true);
        
        // Initialize our music
        m_soundManager = new SoundManager (assetManager);
        
        // Initialize the GUI
        initGui ();
        
        m_actionListener = new SimulationActionListener(
            cam, m_simCam, inputManager, assetManager, m_soundManager, m_screenController, rootNode, m_steamController);
        m_actionListener.init();
    }

    @Override
    public void simpleUpdate(float tpf) {
        if(true == m_initialize) {            
            m_textureManager = new TextureManager(this, assetManager);
        
            // Initialize our sky
            initSky();
            
            // Initialize our lighting
            initLighting();

            // Set up the camera
            initCamera();

            // Draw the ground for our tree to sit on
            initIsland();
        
            // Setup all the action listeners
            m_analogListener = new SimulationAnalogListener(
                cam, m_simCam, inputManager, assetManager, m_soundManager, m_screenController, rootNode);
            m_analogListener.init();
            
            m_initialize = false;
            m_initialized = true;
        }
        
        if (true == m_initialized) {
            if (null != m_tree) {
                boolean pauseGrowth = false;
                long now = System.currentTimeMillis();
                
                // Limit growth based on FPS.  if FPS is too low, then stop growing
                if (1/tpf <= m_fpsLimit) {
                    pauseGrowth = true;
                    if (m_tree.getReadyToGrow()) {
                        m_screenController.setGrowthBarColor(BonsaiScreenController.GrowthBarType.GROWTH_RED);
                    } else {
                        if (now - m_tree.getWateredTime () > 3000) {
                            m_screenController.setGrowthBarColor(BonsaiScreenController.GrowthBarType.GROWTH_GREEN);
                        } else {
                            m_screenController.setGrowthBarColor(BonsaiScreenController.GrowthBarType.GROWTH_BLUE);
                        }
                    }
                } else {
                    if (now - m_tree.getWateredTime () > 3000) {
                        m_screenController.setGrowthBarColor(BonsaiScreenController.GrowthBarType.GROWTH_GREEN);
                    } else {
                        m_screenController.setGrowthBarColor(BonsaiScreenController.GrowthBarType.GROWTH_BLUE);
                    }
                }

                m_soundManager.checkState();
                m_analogListener.highlightOnHover ();

                // Render the tree
                if (true == m_tree.getTreeNeedsDrawn()) {
                    renderTree();
                }

                // Grow after rendering
                if ((m_tree.getReadyToGrow() && (false == pauseGrowth)) && !FORCE_PAUSE) {
                    m_tree.grow();
                    
                    if ((m_tree.getMaturityLevel() >= SteamConstants.MATURITY_LEVEL_ACHIEVEMENT) &&
                        (false == m_maturityTrigger)){
                        // TODO - When a new tree is added, add it here!
                        if (m_tree instanceof BonsaiTreeMaple) {
                            m_steamController.unlockAchievement(SteamConstants.ACH_SUGAR_MAPLE);
                        } else if (m_tree instanceof BonsaiTreeOak) {
                            m_steamController.unlockAchievement(SteamConstants.ACH_OAK);
                        } else if (m_tree instanceof BonsaiTreeJapMaple) {
                            m_steamController.unlockAchievement(SteamConstants.ACH_JAPANESE_MAPLE);
                        } else if (m_tree instanceof BonsaiTreeSycamore) {
                            m_steamController.unlockAchievement(SteamConstants.ACH_SYCAMORE);
                        } else if (m_tree instanceof BonsaiTreeBirch) {
                            m_steamController.unlockAchievement(SteamConstants.ACH_BIRCH);
                        }
                        m_steamController.storeStats();
                        
                        // Don't let this trigger each growth cycle, just once per tree load
                        m_maturityTrigger = true;
                    }
                    
                    if ((false == m_tree.canGetThicker()) &&
                        (false == m_nurturerTrigger)){
                        m_steamController.unlockAchievement(SteamConstants.ACH_PLANT_NURTURER);
                        m_steamController.storeStats();
                        
                        // Don't let this trigger each growth cycle, just once per tree load
                        m_nurturerTrigger = true;
                    }
                }

                // Perform auto save
                if (((now - m_lastAutoSave) > AUTO_SAVE_INTERVAL_MS) &&
                    (true == m_autoSaveEnabled)){
                    m_screenController.saveCurrentTree();
                    m_lastAutoSave = now;

                    // TODO - flash the autosave icon for a second
                    m_screenController.playAutoSaveImage();
                }
            }

            // Keep track of time of day
            if (true == REAL_TIME_OF_DAY) {
                // Update the time each render
                if ("Day and Night".equals(m_dayNightCycle)) {
                    float hour = m_timeOfDay.getHour();
                    if (hour != sc.getSunAndStars().getHour()) {
                        sc.getSunAndStars().setHour(hour);
                    }
                } else if ("Day Only".equals(m_dayNightCycle)) {
                    sc.getSunAndStars().setHour(HOUR_OF_DAY);
                } else {
                    sc.getSunAndStars().setHour(HOUR_OF_NIGHT);
                }
            } else {
                sc.getSunAndStars().setHour(FIXED_HOUR_OF_DAY);
            }
            
            m_simCam.doAutoRotateIfNeeded(tpf);
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        // Add render code?
    }
    
    /**
     * Sets the resolution.  Restart required to see changes
     * @param resolution The screen resolution.  E.g. "800 x 600"
     */
    public void setScreenResolution (String resolution) {
        Prefs.put("resolution", resolution);
    }
    
    /**
     * Return the current screen size as a string
     * @return The screen size.  E.g. "800 x 600"
     */
    public String getScreenResolution() {
        String retval = getSettings().getWidth()+ " x " + getSettings().getHeight();
        return retval;
    }
    
    /**
     * Return the current screen settings
     * @return True if the screen is fullscreen
     */
    public boolean getIsFullscreen() {
        boolean retval = getSettings().isFullscreen();
        return retval;
    }
    
    /**
     * Sets the day/night mode
     * @param cycle The cycle mode.  E.g. "Day and Night"
     */
    public void setDayNightCycle (String cycle) {
        Prefs.put("daynight", cycle);
        m_dayNightCycle = cycle;
    }
    
    /**
     * Gets the day/night mode
     * @return A string representing the day/night mode
     */
    public String getDayNightCycle() {
        String retval = m_dayNightCycle;        
        return retval;
    }
    
    /**
     * Sets the FPS limits
     * @param limit The limit for FPS.  i.e. "Minimum of XX FPS"
     * @see BonsaiScreenController.FPS_LIMITS
     */
    public void setFpsLimit (String limit) {
        int limitVal = Integer.parseInt(limit.substring(11, 13));
        Prefs.putInt("fpslimit", limitVal);
        m_fpsLimit = limitVal;
    }
    
    /**
     * Gets the FPS limit
     * @return The minimum FPS
     */
    public int getFpsLimit () {
        return m_fpsLimit;
    }
    
    /**
     * Gets the state of a saved game slot.  If it exists, it is not a new game
     * @param slot The slot to check (1, 2, or 3)
     * @return True if the slot is in use
     */
    public boolean getSaveSlotIsUsed(int slot) {
        return m_fileManager.getSaveSlotIsUsed(slot);
    }
    
    /**
     * Deletes a save slot
     * @param slot Slot 1, 2, or 3
     * @return True if the file was deleted
     */
    public boolean deleteTree(int slot) {
        boolean retval = m_fileManager.deleteTree(slot);
        if (true == retval) {
            m_soundManager.playClippers();
        }
        return retval;
    }
    
    /**
     * Save the current tree to a save slot
     * @param slot Slot 1, 2, or 3
     */
    public void saveTree(int slot) {
        m_fileManager.saveTree(m_tree, slot);
    }
    
    /**
     * Save the current tree to a save slot
     * @param slot Slot 1, 2, or 3
     */
    public void loadTree(int slot) {
        m_tree.destroy();
        m_tree = m_fileManager.loadTree(slot);
        
        m_nurturerTrigger = false;
        m_maturityTrigger = false;
        
        // TODO - When a new tree is added, add it here!
        if (m_tree instanceof BonsaiTreeMaple) {
            MapleTextureManager.init(this, assetManager);
        } else if (m_tree instanceof BonsaiTreeOak) {
            OakTextureManager.init(this, assetManager);
        } else if (m_tree instanceof BonsaiTreeJapMaple) {
            JapMapleTextureManager.init(this, assetManager);
        } else if (m_tree instanceof BonsaiTreeSycamore) {
            SycamoreTextureManager.init(this, assetManager);
        } else if (m_tree instanceof BonsaiTreeBirch) {
            BirchTextureManager.init(this, assetManager);
        } 
        
        // If nothing to load, just create a new tree
        if(null == m_tree) {
            m_steamController.incrementTreesGrown();
            resetGame(false);
        }
        
        // Detach the old tree node if we have one
        if (null != m_treeNode) {
            m_treeNode.detachAllChildren();
            rootNode.detachChild(m_treeNode);
        }
        BonsaiBranchShapePool.freeShapes();
        m_treeNode = null;
        
        m_steamController.storeStats();
    }
    
    /**
     * Turn on or off auto saving
     * @param enable If true, auto saving will happen
     */
    public void setAutoSaveEnabled(boolean enable) {
        m_autoSaveEnabled = enable;
    }
    
    /**
     * Get the percentage of growth of the tree cycle
     * @return A number between 0 and 1
     */
    public float getTreeGrowthCyclePercent () {
        return Math.min(m_tree.getGrowthCyclePercent(), 1.0f);
    }
    
    /**
     * Turn on or off tree glow on hover
     * @param enable If true, tree glow will happen
     */
    public void setHighlightOnHoverEnabled(boolean enable) {
        if (null != m_analogListener) {
            m_analogListener.setHighlightOnHoverEnabled(enable);
        }
    }
    
    /**
     * Resets the state of the game
     * @param pauseAfterReset If true, pause the game after resetting the tree
     */
    public void resetGame(boolean pauseAfterReset) {        
        // Detach the old tree node if we have one
        if (null != m_treeNode) {
            m_treeNode.detachAllChildren();
            rootNode.detachChild(m_treeNode);
        }
        BonsaiBranchShapePool.freeShapes();
        m_treeNode = null;
        
        // Initialize the tree MODEL and grow it up a little bit before the simulation starts
        initTreeModel();
        
        m_nurturerTrigger = false;
        m_maturityTrigger = false;
        
        setGamePaused (pauseAfterReset);
        
        // Init the camera again
        //initCamera ();
    }
   
    private void initTreeModel () {
        if (null != m_tree) {
            m_tree.destroy();
        }
        
        // TODO - When a new tree is added, add it here!
        final double rando = Math.random();
        if (RANDOM_SPECIES == 0) {
            if(rando < 0.20) {
                MapleTextureManager.init(this, assetManager);
                m_tree = new BonsaiTreeMaple();
            } else if(rando >= 0.20 && rando < 0.40) {
                JapMapleTextureManager.init(this, assetManager);
                m_tree = new BonsaiTreeJapMaple();
            } else if(rando >= 0.40 && rando < 0.60) {
                SycamoreTextureManager.init(this, assetManager);
                m_tree = new BonsaiTreeSycamore();
            } else if(rando >= 0.60 && rando < 0.80) {
                BirchTextureManager.init(this, assetManager);
                m_tree = new BonsaiTreeBirch();
            } else {
                OakTextureManager.init(this, assetManager);
                m_tree = new BonsaiTreeOak();
            }
        } else {
            if(RANDOM_SPECIES == 1) {
                MapleTextureManager.init(this, assetManager);
                m_tree = new BonsaiTreeMaple(2000);
            } else if(RANDOM_SPECIES == 2) {
                JapMapleTextureManager.init(this, assetManager);
                m_tree = new BonsaiTreeJapMaple(2000);
            } else if(RANDOM_SPECIES == 3) {
                SycamoreTextureManager.init(this, assetManager);
                m_tree = new BonsaiTreeSycamore(2000);
            } else if(RANDOM_SPECIES == 4) {
                BirchTextureManager.init(this, assetManager);
                m_tree = new BonsaiTreeBirch(2000);
            } else {
                OakTextureManager.init(this, assetManager);
                m_tree = new BonsaiTreeOak(2000);
            }
        }
        
        //m_tree.grow();
        //m_tree.grow();
        //m_tree.growFullTrunk();
        m_tree.growPartialTrunk();
    }
    
    private void initCamera () {
        flyCam.setEnabled(false);
        Node camNode = (Node)rootNode.getChild(CAMERA_NODE_NAME);
        if (null == camNode) {
            camNode = new Node ();
            camNode.setName(CAMERA_NODE_NAME);
            rootNode.attachChild(camNode);
        }
        
        camNode.setLocalTranslation(FastMath.interpolateLinear(1f, Vector3f.ZERO, SimulationCamera.DEFAULT_TRANSLATION));
            
        if ((null == m_simCam) && (null != m_screenController)) {
            m_simCam = new SimulationCamera(cam, camNode, inputManager);
            m_screenController.setCameraZoomSensitivity(m_simCam.getZoomSensitivity());
            m_simCam.setZoomSensitivity(0f);
        }
    }
    
    private void initSky () {
        // Lighting for the sky
        DirectionalLight sun = new DirectionalLight();
        //Vector3f lightDirection = new Vector3f(-2f, -5f, 4f).normalize();
        sun.setColor(ColorRGBA.White.mult(1f));
        //sun.setDirection(lightDirection);
        sun.setName("main");
        rootNode.addLight(sun);
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White.mult(0.3f));
        ambient.setName("ambient");
        rootNode.addLight(ambient);
        /*DirectionalLight sun = new DirectionalLight();
        sun.setColor(ColorRGBA.White.mult(0.7f));
        sun.setName("main");
        rootNode.addLight(sun); 
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(ColorRGBA.White.mult(0.7f));
        ambient.setName("ambient");
        rootNode.addLight(ambient);*/
        
        // Extra lighting just so we can see our tree
        AmbientLight l1 = new AmbientLight();
        l1.setName("aux1");
        l1.setColor(ColorRGBA.White);
        //rootNode.addLight(l1);
        AmbientLight l2 = new AmbientLight();
        l2.setName("aux2");
        l2.setColor(ColorRGBA.White);
        //rootNode.addLight(l2);
        
        // Add the sky control, attach lights
        sc = new SkyControl(assetManager, cam, 0.9f, true, true);
        rootNode.addControl(sc);
        sc.setCloudYOffset(0.2f);
        sc.getUpdater().setMainLight(sun);
        sc.getUpdater().setAmbientLight(ambient);
        sc.getUpdater().setBloomEnabled(true);
        sc.getUpdater().setShadowFiltersEnabled(true);
        sc.getUpdater().setAmbientMultiplier(5);
        sc.getSunAndStars().setObserverLatitude(37.4046f * FastMath.DEG_TO_RAD);
        sc.getSunAndStars().setSolarLongitude(
                Calendar.getInstance().get(Calendar.MONTH), 
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        sc.setCloudiness(0.3f);
        sc.setEnabled(true);
        
        if (true == REAL_TIME_OF_DAY) {
            // Keep track of time of day
            stateManager.attach(m_timeOfDay);
            m_timeOfDay.setRate(1f);
            
            float hour = m_timeOfDay.getHour();
            sc.getSunAndStars().setHour(hour);
        } else {
            sc.getSunAndStars().setHour(FIXED_HOUR_OF_DAY);
        }
        
        // Make the sun pretty
        BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
        //bloom.setBloomIntensity(1.7f);
        bloom.setBlurScale(2.5f);
        bloom.setExposurePower(1f);
        Misc.getFpp(viewPort, assetManager).addFilter(bloom);
        sc.getUpdater().addBloomFilter(bloom);
        
        BloomFilter bloomTree = new BloomFilter(BloomFilter.GlowMode.Objects);
        bloomTree.setBlurScale(1.5f);
        bloomTree.setExposurePower(2f);
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        fpp.addFilter(bloomTree);
        viewPort.addProcessor(fpp);
        
        /*DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, 1024, 3);
        dlsf.setLight(sun);
        dlsf.setLambda(0.55f);
        dlsf.setShadowIntensity(0.6f);
        dlsf.setEdgeFilteringMode(EdgeFilteringMode.Nearest);
        Misc.getFpp(viewPort, assetManager).addFilter(dlsf);
        sc.getUpdater().addShadowFilter(dlsf);*/
        
        
        // Set the lighting for the scene
        for (Light light : rootNode.getLocalLightList()) {
            if (light.getName().equals("ambient")) {
                sc.getUpdater().setAmbientLight((AmbientLight) light);
            } else if (light.getName().equals("main")) {
                sc.getUpdater().setMainLight((DirectionalLight) light);
            }
        }
    }
    
    private void initLighting () {
        for (Light light : rootNode.getLocalLightList()) {
            if (light.getName().equals("main")) {
                if (m_graphicsLevel == GraphicsLevel.High) {
                    boolean addIt = true;
                    
                    // Check to see if the filter is already added
                    for (SceneProcessor sp : viewPort.getProcessors() ) {
                        if (sp instanceof DirectionalLightShadowFilter && 
                            ((DirectionalLightShadowFilter)sp).getName().equals("HGShadows")) {
                            addIt = false;
                        }
                    }
                    
                    // If there is no filter, add it
                    if (true == addIt) {
                        /*DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, 1024, 3);
                        dlsr.setLight(sun);
                        dlsr.setLambda(0.55f);
                        dlsr.setShadowIntensity(0.6f);
                        dlsr.setEdgeFilteringMode(EdgeFilteringMode.Nearest);
                        viewPort.addProcessor(dlsr);*/

                        DirectionalLightShadowFilter dlsf2 = new DirectionalLightShadowFilter(assetManager, 1024, 3);
                        dlsf2.setName("HGShadows");
                        dlsf2.setLight((DirectionalLight)light);
                        dlsf2.setLambda(0.65f);
                        dlsf2.setShadowIntensity(0.4f);
                        dlsf2.setEdgeFilteringMode(EdgeFilteringMode.PCFPOISSON);
                        FilterPostProcessor fpp2 = new FilterPostProcessor(assetManager);
                        fpp2.addFilter(dlsf2);
                        viewPort.addProcessor(fpp2);
                        
                        /*SSAOFilter ssao = new SSAOFilter(5.1f, 1.2f, 0.2f, 0.1f);
                        ssao.setName("HGAO");
                        FilterPostProcessor fpp3 = new FilterPostProcessor(assetManager);
                        fpp3.addFilter(ssao);
                        viewPort.addProcessor(fpp3);*/
                    }
                } 
                else {
                    // If we're not in high graphics, remove the filter
                    for (SceneProcessor sp : viewPort.getProcessors() ) {
                        try {
                            for (Filter filter : ((FilterPostProcessor)sp).getFilterList()) {
                                if (filter.getName().equals("HGShadows")) {
                                    viewPort.removeProcessor(sp);
                                }
                                if (filter.getName().equals("HGAO")) {
                                    viewPort.removeProcessor(sp);
                                }
                            }
                        } catch (Exception e) {
                            // Do nothing, not the right filter
                        }
                    }
                }
            }
        }
    }
    
    private void initIsland () {
        final int ISLAND = 0;
        final int MODEL = 1;
        final int BOX = 2;
        final int which = BOX;
        
        if (ISLAND == which) {
            final float radius = 2.0f;

            // Attach the grass
            Cylinder cyl = new Cylinder(4, 64, radius, radius, 0.2f, true, false);
            Geometry geo = new Geometry("IslandTop", cyl);

            // Flip it upside sideways
            Quaternion roll90 = new Quaternion(); 
            roll90.fromAngleAxis(FastMath.PI / 2 , new Vector3f(1,0,0) ); 
            geo.setLocalRotation(roll90);

            geo.setLocalTranslation(FastMath.interpolateLinear(1, Vector3f.ZERO, new Vector3f(0f, -0.1f, 0f)));
            /*Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
            Texture tex = assetManager.loadTexture("Textures/Ground/Grass3.jpg");
            tex.setWrap(Texture.WrapMode.Repeat);
            mat.setTexture("DiffuseMap", tex);
            geo.setMaterial(mat);
            geo.getMesh().scaleTextureCoordinates(new Vector2f(1, 1));*/
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", new ColorRGBA (120/255f, 166/255f, 85/255f, 1.0f));
            geo.setMaterial(mat);
            rootNode.attachChild(geo);

            // Now the dome on the bottom
            Dome dome = new Dome (new Vector3f(0f, 0.1f, 0f), 10, 64, radius, false);
            geo = new Geometry("IslandBottom", dome);

            // Flip it upside down
            Quaternion roll180 = new Quaternion(); 
            roll180.fromAngleAxis( FastMath.PI , new Vector3f(0,0,1) ); 
            geo.setLocalRotation(roll180);

            /*mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
            tex = assetManager.loadTexture("Textures/barkthin_low.jpg");
            tex.setWrap(Texture.WrapMode.Repeat);
            mat.setTexture("DiffuseMap", tex);
            geo.setMaterial(mat);*/
            mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", ColorRGBA.Brown);
            geo.setMaterial(mat);
            rootNode.attachChild(geo);
        } 
        else if (MODEL == which) {
            // Spatial model = assetManager.loadModel("Models/Circle.001.mesh.xml");
            Spatial model = assetManager.loadModel("Models/PlantColored.scene");
            /*Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
            mat.setBoolean("UseMaterialColors",true);
            mat.setColor("Ambient", ColorRGBA.Gray);
            mat.setColor("Diffuse", ColorRGBA.Gray);
            model.setMaterial(mat);*/
            rootNode.attachChild(model);
            
            /*model = assetManager.loadModel("Models/PlantColored.blend");
            final BinaryExporter exp = new BinaryExporter();
            final File outName = new File("PlantColored.j3o");
            try {
                FileOutputStream out = new FileOutputStream(outName);
                outName.createNewFile();
                exp.save(model, out);
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        } 
        else {
            final float width = 1.5f;

            // Attach the grass
            Geometry geo = (Geometry)rootNode.getChild("IslandTop");
            if (null == geo) {
                Box box = new Box(width, 0.02f, width);
                geo = new Geometry("IslandTop", box);
                geo.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
                geo.setLocalTranslation(FastMath.interpolateLinear(1, Vector3f.ZERO, new Vector3f(0f, 0.01f, 0f)));
            }
            Material mat = m_textureManager.getGrassMaterial();
            geo.setMaterial(mat);
            geo.getMesh().scaleTextureCoordinates(new Vector2f(1, 1));
            rootNode.attachChild(geo);

            // Now the Box on the bottom
            geo = (Geometry)rootNode.getChild("IslandBottom");
            if (null == geo) {
                Box box = new Box(width, 0.7f, width);
                geo = new Geometry("IslandBottom", box);
                geo.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
                geo.setLocalTranslation(FastMath.interpolateLinear(1, Vector3f.ZERO, new Vector3f(0f, -0.71f, 0f)));
            }
            mat = m_textureManager.getSoilMaterial();
            geo.setMaterial(mat);
            rootNode.attachChild(geo);
        }
    }
    
    private void initGui () {
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
        m_nifty = niftyDisplay.getNifty();
        m_screenController = new BonsaiScreenController(this);
        stateManager.attach(m_screenController);
        m_nifty.registerScreenController(m_screenController);
        m_nifty.fromXml("Interface/Interface.xml", "start");
        guiViewPort.addProcessor(niftyDisplay);
        flyCam.setDragToRotate(true);
    }
    
    private void renderIsland () {
        Geometry geo = (Geometry)rootNode.getChild("IslandTop");
        Material mat = m_textureManager.getGrassMaterial();
        geo.setMaterial(mat);
        
        geo = (Geometry)rootNode.getChild("IslandBottom");
        mat = m_textureManager.getSoilMaterial();
        geo.setMaterial(mat);
    }
    
    private void renderTree () {
        if (null != m_treeNode) {
            m_treeNode.detachAllChildren();
            rootNode.detachChild(m_treeNode);
        }
        BonsaiBranchShapePool.freeShapes();
        
        Node treeNode = m_tree.render(this, assetManager, 100.0f);
        if (treeNode != null) {
            m_treeNode = treeNode;
            
            rootNode.attachChild(m_treeNode);
        }
        
        /*int emitterCnt = 0;
        Node child = (Node)rootNode.getChild(BonsaiBranchMaple.TREE_ROOT_NAME);
        if(null != child) {
            for(Spatial child2 : child.getChildren()) {
                if (null != child2.getName() && child2.getName().equals(BonsaiBranchJmeInterface.BRANCH_LEAF_NAME)) {
                    emitterCnt++;
                }
            }
        }
        System.out.println ("Emitters: " + emitterCnt);*/
    }
}
