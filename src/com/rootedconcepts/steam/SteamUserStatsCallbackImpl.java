/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rootedconcepts.steam;

import com.codedisaster.steamworks.SteamID;
import com.codedisaster.steamworks.SteamLeaderboardEntriesHandle;
import com.codedisaster.steamworks.SteamLeaderboardHandle;
import com.codedisaster.steamworks.SteamResult;
import com.codedisaster.steamworks.SteamUserStats;
import com.codedisaster.steamworks.SteamUserStatsCallback;

/**
 *
 * @author TheDrydens
 */
public class SteamUserStatsCallbackImpl implements SteamUserStatsCallback {
    SteamUserStats m_userStats = null;
    SteamController m_steamController = null;
    
    public void setSteamController(SteamController steamController) {
        m_steamController = steamController;
    }
    
    public void setUserStats(SteamUserStats userStats) {
        m_userStats = userStats;
    }

    @Override
    public void onUserStatsReceived(long gameId, SteamID steamIDUser, SteamResult result) {
        /*System.out.println("User stats received: gameId=" + gameId + 
                ", userId=" + steamIDUser.getAccountID() +
                ", result=" + result.toString());

        int numAchievements = m_userStats.getNumAchievements();
        System.out.println("Num of achievements: " + numAchievements);

        for (int i = 0; i < numAchievements; i++) {
            String name = m_userStats.getAchievementName(i);
            boolean achieved = m_userStats.isAchieved(name, false);
            System.out.println("# " + i + " : name=" + name + ", achieved=" + (achieved ? "yes" : "no"));
        }*/
        
        m_steamController.initStats();
    }

    @Override
    public void onUserStatsStored(long gameId, SteamResult result) {
        //System.out.println("User stats stored: gameId=" + gameId +
        //    ", result=" + result.toString());
    }

    @Override
    public void onUserStatsUnloaded(SteamID steamIDUser) {
        // Nothing to do
    }

    @Override
    public void onUserAchievementStored(long gameId, boolean isGroupAchievement, String achievementName, int curProgress, int maxProgress) {
        //System.out.println("User achievement stored: gameId=" + gameId + ", name=" + achievementName +
        //    ", progress=" + curProgress + "/" + maxProgress);
    }

    @Override
    public void onLeaderboardFindResult(SteamLeaderboardHandle leaderboard, boolean found) {
        // Nothing to do
    }

    @Override
    public void onLeaderboardScoresDownloaded(SteamLeaderboardHandle leaderboard, SteamLeaderboardEntriesHandle entries, int numEntries) {
        // Nothing to do
    }

    @Override
    public void onLeaderboardScoreUploaded(boolean success, SteamLeaderboardHandle leaderboard, int score, boolean scoreChanged, int globalRankNew, int globalRankPrevious) {
        // Nothing to do
    }
}
