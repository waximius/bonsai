/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rootedconcepts;

import com.rootedconcepts.tree.BonsaiTreeInterface;
import com.rootedconcepts.tree.BonsaiTree;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author TheDrydens
 */
public class FileManager {
    private static String SAVES_FOLDER = 
            System.getProperty("user.home") + File.separator + "Bonsai" + 
            File.separator + "Saves";
    
    public FileManager () {
        // Set our saves directory
        String os = (System.getProperty("os.name")).toUpperCase();
        if (os.contains("WIN")) 
        { 
            // %APPDATA%/Bonsai/Saves
            SAVES_FOLDER = System.getenv("AppData") +
                    File.separator + "Bonsai" + 
                    File.separator + "Saves";
        } 
        //Otherwise, we assume Linux or Mac 
        else 
        { 
            // ~/Bonsai/Saves
            SAVES_FOLDER = System.getProperty("user.home") + 
                    File.separator + "Bonsai" + 
                    File.separator + "Saves";
        } 
        
        // Create our saves directory if it doesn't exist
        File saveDir = new File (getSaveDir());
        if (false == saveDir.exists()) {
            saveDir.mkdirs();
        }
    }
    
    /**
     * Gets the state of a saved game slot.  If it exists, it is not a new game
     * @param slot The slot to check (1, 2, or 3)
     * @return True if the slot is in use
     */
    public boolean getSaveSlotIsUsed(int slot) {
        File saveFile = new File (getSaveFile(slot));
        boolean retval = saveFile.exists();
        return retval;
    }
    
    /**
     * Deletes a tree from a save slot
     * @param slot The slot to delete (1, 2, or 3)
     * @return True if the file was deleted successfully
     */
    public boolean deleteTree(int slot) {
        boolean retval = false;
        File file = new File (getSaveFile(slot));
        if(file.exists()) {
            retval = file.delete();
        }
        return retval;
    }
    
    /**
     * Saves a tree to a save slot
     * @param tree The tree to save
     * @param slot The save slot to save to
     */
    public void saveTree(BonsaiTreeInterface tree, int slot) {
        try {
            if (null != tree) {
                File saveFile = new File (getSaveFile(slot));
                if (false == saveFile.exists()) {
                    saveFile.createNewFile();
                }

                if (true == saveFile.exists()) {
                    FileOutputStream fos = new FileOutputStream(saveFile);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);

                    oos.writeObject(tree);
                    oos.flush();
                    fos.flush();
                    oos.close();
                    fos.close();
                }
                // TODO - Else this is a big error, couldn't save! :(
            }
        }  
        catch (Exception ex) {  
            ex.printStackTrace();  
        }  
    }
    
    /**
     * Saves a tree to a save slot
     * @param tree The tree to save
     * @param slot The save slot to save to
     */
    public BonsaiTree loadTree(int slot) {
        BonsaiTree retval = null;
        
        try {
            File saveFile = new File (getSaveFile(slot));
            if (true == saveFile.exists()) {
                FileInputStream fis = new FileInputStream(saveFile);
                ObjectInputStream ois = new ObjectInputStream(fis);

                retval = (BonsaiTree)ois.readObject();
                ois.close();
                fis.close();
                
                retval.setTreeNeedsDrawn(true); // On load it needs drawn
                retval.resetGrowthCycle();
                
                // TODO - catch up all the growth between now and when the
                // tree was saved?
                //retval.catchUpGrowthCycles();
            }
            // Else it is OK, we're trying to load something that doesn't exist
        }  
        catch (Exception ex) {
            ex.printStackTrace();  
        }
        
        return retval;
    }
    
    private String getSaveDir() {
        String retval = SAVES_FOLDER;
        //System.out.println (retval);
        return retval;
    }
    
    private String getSaveFile(int slot) {
        String retval = getSaveDir() + File.separator + "Tree" + slot + ".tree";
        //System.out.println (retval);
        return retval;
    }
}
