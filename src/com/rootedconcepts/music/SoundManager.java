/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rootedconcepts.music;

import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioNode;
import com.jme3.audio.AudioSource.Status;
import com.rootedconcepts.Prefs;

/**
 *
 * @author TheDrydens
 */
public class SoundManager {
    private boolean m_muted = Prefs.getBoolean("muted", false);
    private float m_volume = 100.0f;
    
    private AssetManager m_assetManager = null;
    
    private AudioNode m_clippers = null;
    private AudioNode m_water = null;
    private AudioNode m_music = null;
    
    private int m_lastMusic = 0;
    private String[] m_musicPaths = {
      "Sounds/Music/Calm/Wallpaper.ogg",
      "Sounds/Music/Calm/Carefree.ogg",
      "Sounds/Music/Calm/Fretless.ogg",
      "Sounds/Music/Calm/Life of Riley.ogg"
    };
    
    public SoundManager (AssetManager assetManager) {
        m_assetManager = assetManager;
        
        if(null != m_assetManager) {
            m_clippers = new AudioNode(m_assetManager, "Sounds/Effects/Clippers2.wav", false);
            m_clippers.setPositional(false);
            
            m_water = new AudioNode(m_assetManager, "Sounds/Effects/Water.wav", false);
            m_water.setPositional(false);
            
            m_music = new AudioNode(m_assetManager, m_musicPaths[m_lastMusic], true);
            m_music.setPositional(false);
            // m_music.setLooping(true); // CANNOT loop streaming music.
            
            setMuted (m_muted);
            m_music.play();
        }
    }
    
    /**
     * Checks the state of the music.  We need to manually loop the music, thanks jME!
     * This is a really stupid thing.
     */
    public void checkState () {
        if (Status.Stopped == m_music.getStatus()) {
            m_lastMusic = (m_lastMusic + 1) % m_musicPaths.length;
            m_music = new AudioNode(m_assetManager, m_musicPaths[m_lastMusic], true);
            m_music.setPositional(false);
            setMuted (m_muted);
            m_music.play();
        }
    }
    
    /**
     * Play the clipping noise for pruning branches
     */
    public void playClippers () {
        if (false == m_muted) {
            m_clippers.play();
        }
    }
    
    /**
     * Play the watering can noise for watering branches
     */
    public void playWateringCan () {
        if (false == m_muted) {
            m_water.play();
        }
    }
    
    /**
     * Set the volume level
     * @param volume 0-100
     */
    public void setVolume(float volume) {
        m_volume = volume;
        
        m_music.setVolume(m_volume);
        m_clippers.setVolume(m_volume);
        m_water.setVolume(m_volume);
    }
    
    /**
     * Gets the muted state of the sound
     * @return True if muted
     */
    public boolean getMuted() {
        return m_muted;
    }
    
    /**
     * Sets the state of being muted.
     * @param mute True to turn off all sound.
     */
    public void setMuted(boolean mute) {
        m_muted = mute;
        if (true == m_muted) {
            m_music.setVolume(0.0f);
            m_clippers.setVolume(0.0f);
            m_water.setVolume(0.0f);
        } else {
            m_music.setVolume(m_volume);
            m_clippers.setVolume(m_volume);
            m_water.setVolume(m_volume);
        }
        Prefs.putBoolean("muted", m_muted);
    }
}
