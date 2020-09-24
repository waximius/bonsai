/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rootedconcepts.steam;

import com.codedisaster.steamworks.SteamAPI;
import com.codedisaster.steamworks.SteamUserStats;

/**
 *
 * @author TheDrydens
 */
public class SteamController {
    public static final boolean STEAM_ENABLED = true;
    private static boolean m_steamInitted = false;
    
    private SteamUserStats m_userStats;
    private SteamUserStatsCallbackImpl m_userStatsCallback = new SteamUserStatsCallbackImpl();
    
    private ProcessThread m_processThread = new ProcessThread ();
    
    private int m_branchesPruned = -1;
    private int m_trunksWhacked = -1;
    private int m_treesGrown = -1;
    
    public SteamController () {
        if (STEAM_ENABLED) {
            // TODO Init pre-steam anything here
        }
    }
    
    //------------------------------------------------------------------------//
    // Manage thread
    //------------------------------------------------------------------------//
    public void start () {
        if (STEAM_ENABLED) {
            // Start the steam API
            if (false == SteamAPI.init()) {
                // TODO report error?
                m_steamInitted = false;
            } else {
                // Init post-steam stuff here
                m_steamInitted = true;
                registerInterfaces ();
                
                // Kick off our update thread
                m_processThread.start ();
            }
        }
    }
    
    public void shutdown () {
        if (STEAM_ENABLED && m_steamInitted) {
            m_processThread.shutdown();
            
            // Wait for thread to shut down
            while (m_processThread.isAlive()) {
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    // TODO log error?
                }
            }
            
            unregisterInterfaces ();
            
            SteamAPI.shutdown();
        }
    }
    
    protected void registerInterfaces () {
        if (STEAM_ENABLED && m_steamInitted) {
            // TODO - m_userStats may need to be thread safe'd
            m_userStats = new SteamUserStats(m_userStatsCallback);
            m_userStatsCallback.setUserStats (m_userStats);
            m_userStatsCallback.setSteamController(this);
        }
    }
    
    protected void unregisterInterfaces () {
        if (STEAM_ENABLED && m_steamInitted) {
            m_userStats.dispose();
            m_userStats = null;
        }
    }
    
    //------------------------------------------------------------------------//
    // Allow requests to Steam
    //------------------------------------------------------------------------//
    public void processInput (String input) {
        if (STEAM_ENABLED && m_steamInitted) {
            if (input.equals(SteamConstants.STATS_REQUEST)) {
                m_userStats.requestCurrentStats();
            } else if (input.equals(SteamConstants.STATS_STORE)) {
                m_userStats.storeStats();
            } else if (input.startsWith(SteamConstants.ACH_SET_BEGIN)) {
                String achievementName = input.substring(SteamConstants.ACH_SET_BEGIN.length());
                m_userStats.setAchievement(achievementName);
            }
        }
    }
    
    public void initStats () {
        if (STEAM_ENABLED && m_steamInitted) {
            m_branchesPruned = m_userStats.getStatI(SteamConstants.STAT_BRANCHES_PRUNED, -1);
            m_treesGrown = m_userStats.getStatI(SteamConstants.STAT_TREES_GROWN, -1);
            m_trunksWhacked = m_userStats.getStatI(SteamConstants.STAT_TRUNKS_WHACKED, -1);
        }
    }
    
    /**
     * Update the local branches pruned counts
     */
    public void incrementBranchesPruned() {
        if (STEAM_ENABLED && m_steamInitted) {
            if (m_branchesPruned >= 0) {
                m_branchesPruned++;
                m_userStats.setStatI(SteamConstants.STAT_BRANCHES_PRUNED, m_branchesPruned);
            }
        }
    }
    
    /**
     * Update the local trunk whacked counts
     */
    public void incrementTrunkWhacked() {
        if (STEAM_ENABLED && m_steamInitted) {
            if (m_trunksWhacked >= 0) {
                m_trunksWhacked++;
                m_userStats.setStatI(SteamConstants.STAT_TRUNKS_WHACKED, m_trunksWhacked);
            }
        }
    }
    
    /**
     * Update the local trunk whacked counts
     */
    public void incrementTreesGrown() {
        if (STEAM_ENABLED && m_steamInitted) {
            if (m_treesGrown >= 0) {
                m_treesGrown++;
                m_userStats.setStatI(SteamConstants.STAT_TREES_GROWN, m_treesGrown);
            }
        }
    }
    
    /**
     * Unlock the achievement
     */
    public void unlockAchievement(String achievement) {
        if (STEAM_ENABLED && m_steamInitted) {
            if (false == m_userStats.isAchieved(achievement, false)) {
                m_userStats.setAchievement(achievement);
            }
        }
    }
    
    /**
     * Tell the API to update Steam
     */
    public void storeStats () {
        if (STEAM_ENABLED && m_steamInitted) {
            m_processThread.storeStats();
        }
    }
    
    //------------------------------------------------------------------------//
    // Handle all responses from Steam for callbacks to function
    //------------------------------------------------------------------------//
    private class ProcessThread extends Thread {
        private final Object m_lock = new Object ();
        private boolean m_alive = true;
        private boolean m_storeStats = false;
        
        @Override
        public void run () {
            boolean alive = true;
            boolean storeStats = false;
            
            // Begin by initializing game stats
            processInput(SteamConstants.STATS_REQUEST);
            
            while (alive && (true == SteamAPI.isSteamRunning())) {            
                SteamAPI.runCallbacks();
                
                if (storeStats) {
                    m_userStats.storeStats();
                }

                try {
                    Thread.sleep(66);
                } catch (Exception e) {
                    // TODO log error?
                }
                
                synchronized (m_lock) {
                    alive = m_alive;
                    storeStats = m_storeStats;
                }
            }
            
            shutdown ();
        }
        
        public void shutdown () {
            synchronized (m_lock) {
                m_alive = false;
            }
        }
        
        public void storeStats () {
            synchronized (m_lock) {
                m_storeStats = true;
            }
        }
    }
}
