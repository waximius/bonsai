/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rootedconcepts;

import com.jme3.system.AppSettings;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 *
 * @author TheDrydens
 */
public class DefaultAppSettings {
    private AppSettings m_appSettings = null;
    
    public DefaultAppSettings() {
        GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        m_appSettings = new AppSettings(true);
        m_appSettings.setSettingsDialogImage("Interface/Splash.png");
        m_appSettings.setSamples(4);
        m_appSettings.setFrameRate(60);
        //String res = Prefs.get("resolution", "800 x 600");
        String res = Prefs.get("resolution", "Fullscreen");
        //res = "1536 x 720";
        //res = "1280 x 720";
        
        if(res.equals("Fullscreen") && false == device.isFullScreenSupported()) {
            res = "800 x 600";
        }
        
        if (res.equals("Fullscreen")) {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            double width = screenSize.getWidth();
            double height = screenSize.getHeight();
            DisplayMode[] modes = device.getDisplayModes();
            
            // Find the highest quality match for our device at the 
            // default screen resolution
            int defMode = 0;
            int bitDepth = -1;
            int refRate = 40;
            boolean found = false;
            for (int curMode = 0; curMode < modes.length; curMode++) {
                if ((modes[curMode].getWidth() == (int)width) && 
                    (modes[curMode].getHeight() == (int)height)){
                    if ((modes[curMode].getBitDepth() >= bitDepth) &&
                        (modes[curMode].getRefreshRate() >= refRate) &&
                        (modes[curMode].getRefreshRate() <= 85)){
                        bitDepth = modes[curMode].getBitDepth();
                        refRate = modes[curMode].getRefreshRate();
                        defMode = curMode;
                        found = true;
                    }
                }
            }
            
            if (true == found) {
                m_appSettings.setResolution((int)width, (int)height);
                m_appSettings.setFullscreen(device.isFullScreenSupported());
                m_appSettings.setFrequency(modes[defMode].getRefreshRate());
                if (bitDepth > -1) {
                    m_appSettings.setBitsPerPixel(modes[defMode].getBitDepth());
                } else {
                    m_appSettings.setBitsPerPixel(24);
                }
            } else {
                m_appSettings.setResolution(800, 600);
                m_appSettings.setFullscreen(false);
                m_appSettings.setFrequency(60);
                m_appSettings.setBitsPerPixel(24);
            }
        } else {
            String[] vals = res.split(" x ");
            
            // Turn off fullscreen and set resolution
            int width = Integer.parseInt(vals[0]);
            int height = Integer.parseInt(vals[1]);
            
            DisplayMode[] modes = device.getDisplayModes();
            int defMode = 0;
            int bitDepth = -1;
            int refRate = 40;
            boolean found = false;
            for (int curMode = 0; curMode < modes.length; curMode++) {
                if ((modes[curMode].getWidth() == (int)width) && 
                    (modes[curMode].getHeight() == (int)height)){
                    if ((modes[curMode].getBitDepth() >= bitDepth) &&
                        (modes[curMode].getRefreshRate() >= refRate) &&
                        (modes[curMode].getRefreshRate() <= 85)){
                        bitDepth = modes[curMode].getBitDepth();
                        refRate = modes[curMode].getRefreshRate();
                        defMode = curMode;
                        found = true;
                    }
                }
            }
            
            if (true == found) {
                m_appSettings.setResolution(width, height);
                m_appSettings.setFullscreen(false);
                m_appSettings.setFrequency(modes[defMode].getRefreshRate());
                if (bitDepth > -1) {
                    m_appSettings.setBitsPerPixel(modes[defMode].getBitDepth());
                } else {
                    m_appSettings.setBitsPerPixel(24);
                }
            } else {
                m_appSettings.setResolution(800, 600);
                m_appSettings.setFullscreen(false);
                m_appSettings.setFrequency(60);
                m_appSettings.setBitsPerPixel(24);
            }
        }
        
        //appSettings.setResolution(1280, 1024);
        m_appSettings.setTitle("Bonsai");
        try {
            m_appSettings.setIcons(new BufferedImage[]{
                ImageIO.read(BonsaiApplication.class.getResourceAsStream("/Interface/Icons/Icon16.png")),
                ImageIO.read(BonsaiApplication.class.getResourceAsStream("/Interface/Icons/Icon32.png")),
                ImageIO.read(BonsaiApplication.class.getResourceAsStream("/Interface/Icons/Icon64.png")),
                ImageIO.read(BonsaiApplication.class.getResourceAsStream("/Interface/Icons/Icon128.png")),
                ImageIO.read(BonsaiApplication.class.getResourceAsStream("/Interface/Icons/Icon256.png"))});
        } catch (Exception e) {
            // Nothing to do.  Just be sad and iconless
        }
    }
    
    public AppSettings getAppSettings () {
        return m_appSettings;
    }
}
