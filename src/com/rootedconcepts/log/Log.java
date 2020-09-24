/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rootedconcepts.log;

/**
 *
 * @author TheDrydens
 */
public class Log {
    private static boolean DEBUG = true;
    
    public static void debugln(String str) {
        if(true == DEBUG) {
            System.out.println(str);
        }
    }
}
