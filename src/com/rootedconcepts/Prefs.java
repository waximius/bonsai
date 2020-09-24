/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rootedconcepts;

import java.util.prefs.Preferences;

/**
 *
 * @author TheDrydens
 */
public class Prefs {
    private static Preferences m_prefs = Preferences.userRoot().node(BonsaiApplication.class.getName());
    
    
    public static void put (String key, String value) {
        m_prefs.put(key, value);
    }
    
    public static String get (String key, String def) {
        return m_prefs.get(key, def);
    }
    
    public static void putInt (String key, int value) {
        m_prefs.putInt(key, value);
    }
    
    public static int getInt (String key, int def) {
        return m_prefs.getInt(key, def);
    }
    
    public static void putBoolean (String key, boolean value) {
        m_prefs.putBoolean(key, value);
    }
    
    public static boolean getBoolean (String key, boolean def) {
        return m_prefs.getBoolean(key, def);
    }
}
