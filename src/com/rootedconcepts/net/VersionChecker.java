/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rootedconcepts.net;

import com.rootedconcepts.BonsaiApplication;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 *
 * @author TheDrydens
 */
public class VersionChecker {
    public static boolean isNewVersionAvailable() {
        boolean retval = false;
        
        try {
            URL url = new URL("http://thebonsaigame.com/version.txt");
            BufferedReader in = new BufferedReader(
                new InputStreamReader(url.openStream()));

            String inputLine;
            while (((inputLine = in.readLine()) != null) && !retval) {
                retval = BonsaiApplication.VERSION.equals(inputLine.trim());
            }
            in.close();
        } catch (Exception e) {
            retval = false;
        }
        
        return retval;
    }
}
