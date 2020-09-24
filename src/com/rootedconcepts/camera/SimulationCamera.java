/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rootedconcepts.camera;

import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;
import com.rootedconcepts.BonsaiApplication;

/**
 *
 * @author TheDrydens
 */
public class SimulationCamera extends ChaseCamera {
    public static final boolean AUTO_ROTATE_CAMERA_ON_IDLE = true;
    public static final Vector3f DEFAULT_TRANSLATION = new Vector3f(0, 8, 0);
    public static final float DEFAULT_DISTANCE = 20f;
    
    private static float SECONDS_UNTIL_AUTO_ROTATE = 10f;
    private float m_timeUntilAutoRotate = 0f;
    
    public SimulationCamera (Camera cam, Spatial target, InputManager inputManager) {
        super(cam, target, inputManager);
        
        setEnabled(true);
            
        setSmoothMotion(true);
        
        setDefaultDistance(40f);
        if (BonsaiApplication.CINEMATIC_MODE) {
            SimulationCamera.SECONDS_UNTIL_AUTO_ROTATE = 2f;
            setDefaultDistance(20f);
        }
        
        if (true == BonsaiApplication.CINEMATIC_MODE) {
            setDefaultVerticalRotation(-FastMath.PI / 18f);
        } else {
            setDefaultVerticalRotation(FastMath.PI / 18f);
        }
        
        setHideCursorOnRotate(true);
        
        setMinDistance(3);
        setMaxDistance(100);
        
        setZoomSensitivity(3f);
        setRotationSpeed(2.5f);

        setMinVerticalRotation(-(FastMath.PI / 6));
        
        // TODO - What's best here?!
        setInvertVerticalAxis(true);
        //setInvertHorizontalAxis(true);
        
        setToggleRotationTrigger(new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
    }
    
    public void resetAutoRotateCounter() {
        if (true == AUTO_ROTATE_CAMERA_ON_IDLE) {
            if (false == BonsaiApplication.CINEMATIC_MODE) {
                if (false == dragToRotate) {
                    setDragToRotate(true);
                    setHideCursorOnRotate(true);
                }
                m_timeUntilAutoRotate = 0f;
            }
        }
    }
    
    public void doAutoRotateIfNeeded(float tpf) {
        if (true == AUTO_ROTATE_CAMERA_ON_IDLE) {
            m_timeUntilAutoRotate += tpf;
            if (m_timeUntilAutoRotate > SECONDS_UNTIL_AUTO_ROTATE) {
                if (true == dragToRotate) {
                    setDragToRotate(false);
                    setHideCursorOnRotate(false);
                }
                rotateCamera(((2f * FastMath.PI) / 920f) * tpf);
            }
        }
    }
}
