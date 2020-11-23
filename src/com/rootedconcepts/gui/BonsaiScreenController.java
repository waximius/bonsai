/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rootedconcepts.gui;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.rootedconcepts.BonsaiApplication;
import com.rootedconcepts.enums.GraphicsLevel;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import de.lessvoid.nifty.tools.Color;
import de.lessvoid.nifty.tools.SizeValue;

/**
 *
 * @author TheDrydens
 */
public class BonsaiScreenController extends AbstractAppState implements ScreenController {
    public enum GrowthBarType {
        GROWTH_GREEN,
        GROWTH_RED,
        GROWTH_BLUE,
    };
    
    private BonsaiApplication m_app = null;
    private Nifty m_nifty = null;
    private Screen m_screen = null;
    private String m_previousScreenId = null;
    private float m_zoomSensitivity = 0f;
    
    private final String OPTIONS_SCREEN  = "options";
    private final String START_SCREEN    = "start";
    private final String HUD_SCREEN      = "hud";
    private final String SETTINGS_SCREEN = "settings";
    
    private final String RESTART_TEXT        = "restartRequiredText";
    private final String RESOLUTION_BUTTON   = "changeResolutionButton";
    private final String DETAIL_LEVEL_BUTTON = "changeDetailLevelButton";
    private final String FPS_LIMIT_BUTTON    = "changeFpsButton";
    private final String DAY_NIGHT_BUTTON    = "dayNightCycleButton";
    
    private final String GAME_1_PANEL_BG = "backgroundImage1";
    private final String GAME_2_PANEL_BG = "backgroundImage2";
    private final String GAME_3_PANEL_BG = "backgroundImage3";
    
    private int m_lastResolution = 0;
    private final String[] RESOLUTIONS = {
        "800 x 600",
        "1024 x 768",
        "1280 x 1024",
        "Fullscreen",
    };
    
    private int m_lastDetail = 0;
    private final String[] DETAIL_LEVELS = {
        "Low Detail",
        "Medium Detail",
        "High Detail",
    };
    
    private int m_lastDayNight = 0;
    private final String[] DAY_NIGHT_MODES = {
        "Day and Night",
        "Day Only",
        "Night Only",
    };
    
    private int m_lastFps = 0;
    private final String[] FPS_LIMITS = {
        "Minimum of 30 FPS",
        "Minimum of 10 FPS",
        "Minimum of 15 FPS",
        "Minimum of 20 FPS",
        "Minimum of 25 FPS",
    };
    
    private int m_gameNumber = 0;
    private boolean m_resetGame = false;
    
    private NiftyImage m_unmutedImg = null;
    private NiftyImage m_mutedImg = null;
    private NiftyImage m_pruneEnabledImg = null;
    private NiftyImage m_pruneDisabledImg = null;
    private NiftyImage m_waterEnabledImg = null;
    private NiftyImage m_waterDisabledImg = null;
    private NiftyImage m_treeImg = null;
    private NiftyImage m_budImg = null;
    private NiftyImage m_saveImg = null;
    
    private NiftyImage m_lastBarImg = null;
    private NiftyImage m_greenBarImg = null;
    private NiftyImage m_redBarImg = null;
    private NiftyImage m_blueBarImg = null;
    
    public BonsaiScreenController(BonsaiApplication app) {
        m_app = app;
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
    }
 
    @Override
    public void update(float tpf) {
        if ((null != m_screen) && HUD_SCREEN.equals(m_screen.getScreenId())) {        
            Element button = m_screen.findElementByName("growFiller");
            if (null != button) {
                // Center-out growth
                float percent = m_app.getTreeGrowthCyclePercent();
                float width = button.getRenderer(ImageRenderer.class).getImage().getWidth();
                float height = button.getRenderer(ImageRenderer.class).getImage().getHeight();
                int wPix = Math.round(width * percent);
                wPix += (wPix % 2); // Make sure the bar grows evenly (left and right pixels appear together)
                int hPix = (int)height;//Math.round(height * percent);
                button.setConstraintWidth(new SizeValue(wPix + "px"));
                button.setConstraintHeight(new SizeValue(hPix + "px"));
                button.getParent().layoutElements();
            }
        }
    }
 
    @Override
    public void cleanup() {
        super.cleanup();
        //TODO: clean up what you initialized in the initialize method,
        //e.g. remove all spatials from rootNode
        //this is called on the OpenGL thread after the AppState has been detached
    }

    public void bind(Nifty nifty, Screen screen) {
        m_nifty = nifty;
        m_screen = screen;
        
        m_unmutedImg = m_nifty.createImage("Interface/Unmuted.png", false);
        m_mutedImg = m_nifty.createImage("Interface/Muted.png", false);
        
        m_pruneEnabledImg = m_nifty.createImage("Interface/PruneEnabled.png", false);
        m_pruneDisabledImg = m_nifty.createImage("Interface/PruneDisabled.png", false);
        
        m_waterEnabledImg = m_nifty.createImage("Interface/WaterEnabled.png", false);
        m_waterDisabledImg = m_nifty.createImage("Interface/WaterDisabled.png", false);
        
        m_treeImg = m_nifty.createImage("Interface/Tree.png", false);
        m_budImg = m_nifty.createImage("Interface/Bud.png", false);
        
        m_saveImg = m_nifty.createImage("Interface/Save.png", false);
        
        m_greenBarImg = m_nifty.createImage("Interface/GrowFiller.png", false);
        m_redBarImg = m_nifty.createImage("Interface/GrowFillerRed.png", false);
        m_blueBarImg = m_nifty.createImage("Interface/GrowFillerBlue.png", false);
        
        Element button = m_nifty.getScreen(HUD_SCREEN).findElementByName("saveButton");
        if (null != button) {
            button.hide();
        }
        
        initStartScreenImages();
        initSettingsButtons();
    }

    public void onStartScreen() {
        m_screen = m_nifty.getCurrentScreen();
        
        // Reset the world, do this here so the user doesn't see the
        // tree get whacked before the screen loads
        if (true == m_resetGame) {
            m_resetGame = false;
            m_app.resetGame(true);
        }
        
        // TODO - clear the loading screen?
        if (START_SCREEN.equals(m_screen.getScreenId())) {
            /*Element button = m_screen.findElementByName("loaderImagePanel");
            if (null != button) {
                button.hide();
            }*/
        }
        
        initStartScreenImages();
        initSettingsButtons();
        
        if (HUD_SCREEN.equals(m_screen.getScreenId())) {
            m_app.setAutoSaveEnabled(true);
            m_app.setHighlightOnHoverEnabled(true);
            m_app.getSimulationCamera().setZoomSensitivity(m_zoomSensitivity);
        
            Element button = m_screen.findElementByName("muteButton");
            if (null != button) {
                if (true == m_app.getSoundManager().getMuted()) {
                    button.getRenderer(ImageRenderer.class).setImage(m_mutedImg);
                } else {
                    button.getRenderer(ImageRenderer.class).setImage(m_unmutedImg);
                }
            }
        } else {
            m_app.setAutoSaveEnabled(false);
            m_app.setHighlightOnHoverEnabled(false);
        }
    }

    public void onEndScreen() {
        m_previousScreenId = m_nifty.getCurrentScreen().getScreenId();
        
        if (HUD_SCREEN.equals(m_previousScreenId)) {
            m_zoomSensitivity = m_app.getSimulationCamera().getZoomSensitivity();
            m_app.getSimulationCamera().setZoomSensitivity(0f);
        }
        
        // TODO - Show loading screen if needed?
        if (START_SCREEN.equals(m_previousScreenId)) {
            if (false == m_app.getWorldInitted()) {
                m_app.setInitWorld();
            }
            
            /*Element button = m_screen.findElementByName("loaderImagePanel");
            if (null != button) {
                button.show();
            }*/
        }
        
        setRestartTextEnabled(false);
    }
    
    /**
     * Checks to see if the HUD is active (game is running)
     * @return True if it is
     */
    public boolean isHudScreen() {
        boolean retval = false;
        if ((null != m_screen) &&
            (HUD_SCREEN.equals(m_screen.getScreenId()))) {
            retval = true;
        }
        return retval;
    }
    
    /**
     * Checks to see if the start is active (at main menu)
     * @return True if it is
     */
    public boolean isStartScreen() {
        boolean retval = false;
        if ((null != m_screen) &&
            (START_SCREEN.equals(m_screen.getScreenId()))) {
            retval = true;
        }
        return retval;
    }
    
    /**
     * Shows or hides the 'restart required' text
     * @param enabled True means show the text
     */
    public void setRestartTextEnabled(boolean enabled) {
        if (SETTINGS_SCREEN.equals(m_screen.getScreenId())) {
            Element button = m_screen.findElementByName(RESTART_TEXT);
            if (null != button) {
                Color c = button.getRenderer(TextRenderer.class).getColor();
                if (true == enabled) {
                    c.setAlpha(1.0f);
                } else {
                    c.setAlpha(0.0f);
                }
                button.getRenderer(TextRenderer.class).setColor(c);
            }
        }
    }
    
    /**
     * Changes the resolution that will be displayed
     */
    public void changeResolution() {
        m_lastResolution = (m_lastResolution + 1) % RESOLUTIONS.length;
        if (SETTINGS_SCREEN.equals(m_screen.getScreenId())) {
            Element button = m_screen.findElementByName(RESOLUTION_BUTTON);
            if (null != button) {
                String text = RESOLUTIONS[m_lastResolution];
                button.getRenderer(TextRenderer.class).setText(text);
                setRestartTextEnabled(true);
            }
        }
    }
    
    /**
     * Changes the resolution that will be displayed
     */
    public void changeDetailLevel() {
        m_lastDetail = (m_lastDetail + 1) % DETAIL_LEVELS.length;
        if (SETTINGS_SCREEN.equals(m_screen.getScreenId())) {
            Element button = m_screen.findElementByName(DETAIL_LEVEL_BUTTON);
            if (null != button) {
                String text = DETAIL_LEVELS[m_lastDetail];
                button.getRenderer(TextRenderer.class).setText(text);
            }
        }
    }
    
    /**
     * Changes the FPS that will limit growth
     */
    public void changeFpsLimit () {
        m_lastFps = (m_lastFps + 1) % FPS_LIMITS.length;
        if (SETTINGS_SCREEN.equals(m_screen.getScreenId())) {
            Element button = m_screen.findElementByName(FPS_LIMIT_BUTTON);
            if (null != button) {
                String text = FPS_LIMITS[m_lastFps];
                button.getRenderer(TextRenderer.class).setText(text);
            }
        }
    }
    
    /**
     * Changes the day/night cycle
     */
    public void changeDayNightCycle() {
        m_lastDayNight = (m_lastDayNight + 1) % DAY_NIGHT_MODES.length;
        if (SETTINGS_SCREEN.equals(m_screen.getScreenId())) {
            Element button = m_screen.findElementByName(DAY_NIGHT_BUTTON);
            if (null != button) {
                String text = DAY_NIGHT_MODES[m_lastDayNight];
                button.getRenderer(TextRenderer.class).setText(text);
            }
        }
    }
    
    public void setCameraZoomSensitivity(float sensitivity) {
        m_zoomSensitivity = sensitivity;
    }
    
    /**
     * Sets the correct images for our start screen
     * @param game The number of the game to delete (1, 2, or 3)
     */
    public void initStartScreenImages() {
        if (isStartScreen()) {
            // Initialize our pictures
            NiftyImage img = (m_app.getSaveSlotIsUsed(1)) ? m_treeImg : m_budImg;
            Element button = m_screen.findElementByName(GAME_1_PANEL_BG);
            if (null != button) {
                button.getRenderer(ImageRenderer.class).setImage(img);
            }

            img = (m_app.getSaveSlotIsUsed(2)) ? m_treeImg : m_budImg;
            button = m_screen.findElementByName(GAME_2_PANEL_BG);
            if (null != button) {
                button.getRenderer(ImageRenderer.class).setImage(img);
            }

            img = (m_app.getSaveSlotIsUsed(3)) ? m_treeImg : m_budImg;
            button = m_screen.findElementByName(GAME_3_PANEL_BG);
            if (null != button) {
                button.getRenderer(ImageRenderer.class).setImage(img);
            }
        }
    }
    
    /**
     * Starts a certain game number
     * @param game The number of the game to start (1, 2, or 3)
     */
    public void startGame(String game) {
        m_gameNumber = Integer.parseInt(game);
        
        // Initialize the game, invalid numbers will be caught by 
        // the load tree method
        m_app.loadTree(m_gameNumber);
        
        // Always go to the HUD next
        gotoScreen(HUD_SCREEN);
    }
    
    /**
     * Deletes a certain game number
     * @param game The number of the game to delete (1, 2, or 3)
     */
    public void deleteGame(String game) {
        int gameNumber = Integer.parseInt(game);
        
        m_app.deleteTree(gameNumber);
        initStartScreenImages();
    }
    
    /**
     * Make the auto save image visible for one cycle in the HUD
     */
    public void playAutoSaveImage () {
        if (HUD_SCREEN.equals(m_screen.getScreenId())) {        
            Element button = m_screen.findElementByName("saveButton");
            if (null != button) {
                //button.show();
                //button.hide();
            }
        }
    }
    
    /**
     * Opens the main menu after saving the game state
     */
    public void gotoMainMenu() {
        saveCurrentTree();
        
        m_resetGame = true;
        
        // Always go to the start screen next
        gotoScreen(START_SCREEN);
    }
    
    /**
     * Go to another screen
     * @param nextScreen The name of the screen to load
     */
    public void gotoScreen(String nextScreen) {
        m_nifty.gotoScreen(nextScreen);
        m_screen = m_nifty.getScreen(nextScreen);
        if (HUD_SCREEN.equals(nextScreen)) {
            m_app.setGamePaused(false);
        } else {
            m_app.setGamePaused(true);
        }
    }
    
    /**
     * Save changes and go to the previous screen
     */
    public void saveAndGotoPreviousScreen() {
        m_app.setScreenResolution(RESOLUTIONS[m_lastResolution]);
        
        m_app.setDayNightCycle(DAY_NIGHT_MODES[m_lastDayNight]);
        
        m_app.setFpsLimit(FPS_LIMITS[m_lastFps]);
                
        GraphicsLevel gl = GraphicsLevel.Low;
        if (2 == m_lastDetail) {
            gl = GraphicsLevel.High;
        } else if (1 == m_lastDetail) {
            gl = GraphicsLevel.Medium;
        }
        m_app.setGraphicsLevel(gl);
        
        gotoPreviousScreen();
    }
    
    /**
     * Go to the previous screen
     */
    public void gotoPreviousScreen() {
        if (null != m_previousScreenId) {
            gotoScreen(m_previousScreenId);
        }
    }
    
    /**
     * The escape key was pressed, do something
     */
    public void escapeKeyPressed() {
        if (null != m_screen) {
            if(HUD_SCREEN.equals(m_screen.getScreenId())) {
                gotoScreen(OPTIONS_SCREEN);
            } else if(OPTIONS_SCREEN.equals(m_screen.getScreenId())) {
                gotoScreen(HUD_SCREEN);
            }
        }
    }

    /**
     * Save the tree
     */
    public void saveCurrentTree() {
        // Save the game to the right game slot
        if (m_gameNumber > 0) {
            m_app.saveTree(m_gameNumber);
            //System.out.println("SAVED " + m_gameNumber);
        }
    }
    
    /**
     * Gets the state of a saved game slot.  If it exists, it is not a new game
     * @param slot The slot to check (1, 2, or 3)
     * @return True if the slot is in use
     */
    public boolean getSaveSlotIsUsed(int slot) {
        return m_app.getSaveSlotIsUsed(slot);
    }

    /**
     * Quit the application
     */
    public void quit() {
        // This will save and quit for us, we don't need to
        m_app.stop();
    }
    
    /**
     * Toggle the state of our music and sound effects
     */
    public void toggleMute() {
        boolean mute = !m_app.getSoundManager().getMuted ();
        m_app.getSoundManager().setMuted(mute);
        
        Element button = m_screen.findElementByName("muteButton");
        if (null != button) {
            if (true == mute) {
                button.getRenderer(ImageRenderer.class).setImage(m_mutedImg);
            } else {
                button.getRenderer(ImageRenderer.class).setImage(m_unmutedImg);
            }
        }
    }
    
    /**
     * Turn on/off shear mode
     */
    public void togglePruningEnabledGui () {
        togglePruningEnabled ();
        toggleWateringEnabled();
    }
    
    private void togglePruningEnabled () {
        boolean enabled = !m_app.getActionListener().getPruningEnabled();
        m_app.getActionListener().setPruningEnabled(enabled);
        m_app.getAnalogListener().setPruningEnabled(enabled);
        
        Element button = m_screen.findElementByName("pruneModeButton");
        if (null != button) {
            if (true == enabled) {
                button.getRenderer(ImageRenderer.class).setImage(m_pruneEnabledImg);
            } else {
                button.getRenderer(ImageRenderer.class).setImage(m_pruneDisabledImg);
            }
        }
    }
    
    /**
     * Turn on/off water mode
     */
    public void toggleWateringEnabledGui () {
        toggleWateringEnabled ();
        togglePruningEnabled ();
    }
    
    private void toggleWateringEnabled () {
        boolean enabled = !m_app.getActionListener().getWateringEnabled();
        m_app.getActionListener().setWateringEnabled(enabled);
        m_app.getAnalogListener().setWateringEnabled(enabled);
        
        Element button = m_screen.findElementByName("waterModeButton");
        if (null != button) {
            if (true == enabled) {
                button.getRenderer(ImageRenderer.class).setImage(m_waterEnabledImg);
            } else {
                button.getRenderer(ImageRenderer.class).setImage(m_waterDisabledImg);
            }
        }
    }
    
    public void setGrowthBarColor(GrowthBarType type) {
        Element button = m_screen.findElementByName("growFiller");
        if (null != button) {
            if ((type == GrowthBarType.GROWTH_RED) &&
                (m_lastBarImg != m_redBarImg)){
                button.getRenderer(ImageRenderer.class).setImage(m_redBarImg);
                m_lastBarImg = m_redBarImg;
            } else if ((type == GrowthBarType.GROWTH_BLUE) &&
                (m_lastBarImg != m_blueBarImg)) {
                button.getRenderer(ImageRenderer.class).setImage(m_blueBarImg);
                m_lastBarImg = m_blueBarImg;
            } else if ((type == GrowthBarType.GROWTH_GREEN) &&
                (m_lastBarImg != m_greenBarImg)) {
                button.getRenderer(ImageRenderer.class).setImage(m_greenBarImg);
                m_lastBarImg = m_greenBarImg;
            }
        }
    }
    
    private void initSettingsButtons() {
        if (SETTINGS_SCREEN.equals(m_screen.getScreenId())) {
            setRestartTextEnabled(false);
            
            Element button = m_screen.findElementByName(DAY_NIGHT_BUTTON);
            if (null != button) {
                String text = m_app.getDayNightCycle();
                for(int idx = 0; idx < DAY_NIGHT_MODES.length; idx++) {
                    if (DAY_NIGHT_MODES[idx].equals(text)) {
                        m_lastDayNight = idx;
                        break;
                    }
                }
                button.getRenderer(TextRenderer.class).setText(text);
            }
            
            button = m_screen.findElementByName(RESOLUTION_BUTTON);
            if (null != button) {
                String text = m_app.getScreenResolution();
                if (m_app.getIsFullscreen()) {
                    text = "Fullscreen";
                } 
                
                for(int idx = 0; idx < RESOLUTIONS.length; idx++) {
                    if (RESOLUTIONS[idx].equals(text)) {
                        m_lastResolution = idx;
                        break;
                    }
                }
                button.getRenderer(TextRenderer.class).setText(text);
            }
            
            button = m_screen.findElementByName(DETAIL_LEVEL_BUTTON);
            if (null != button) {
                GraphicsLevel gl = m_app.getGraphicsLevel();
                String text = DETAIL_LEVELS[0];
                m_lastDetail = 0;
                if (GraphicsLevel.High == gl) {
                    text = DETAIL_LEVELS[2];
                    m_lastDetail = 2;
                } else if (GraphicsLevel.Medium == gl) {
                    text = DETAIL_LEVELS[1];
                    m_lastDetail = 1;
                }
                button.getRenderer(TextRenderer.class).setText(text);
            }
            
            button = m_screen.findElementByName(FPS_LIMIT_BUTTON);
            if (null != button) {
                int limit = m_app.getFpsLimit();
                String text = Integer.toString(limit);
                for(int idx = 0; idx < FPS_LIMITS.length; idx++) {
                    if (FPS_LIMITS[idx].contains(text)) {
                        m_lastFps = idx;
                        break;
                    }
                }
                button.getRenderer(TextRenderer.class).setText(FPS_LIMITS[m_lastFps]);
            }
        }
    }
}
