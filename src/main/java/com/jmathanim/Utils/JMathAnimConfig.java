/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Utils;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.jmathanim.JMathAnimScene;
import java.awt.Color;

/**
 * Stores all the data related to global configuration, to be accessed from any
 * object that requires it. This class implements the singleton pattern.
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class JMathAnimConfig {

    public static MathObjectDrawingProperties getDefaultMP() {
        MathObjectDrawingProperties defaultMP = new MathObjectDrawingProperties();
        //Default, boring values
        defaultMP.drawColor.set(JMColor.WHITE);
//        defaultMP.fillColor = new Color(0, 0, 0, 0);//Transparent color
        defaultMP.fillColor.set(JMColor.GRAY);
        defaultMP.thickness = 1d;
        defaultMP.dashStyle = MathObjectDrawingProperties.SOLID;
        defaultMP.visible = true;
        defaultMP.setFillAlpha(0);
        defaultMP.drawPathBorder = false;
        defaultMP.absoluteThickness = false;
        defaultMP.layer = 1;//Layer 0 should be reserved for background
        defaultMP.absolutePosition = false;
        return defaultMP;
    }

    /**
     * Width of media screen. Typically 800 or 1920.
     */
    public int mediaW;
    /**
     * Height of media screen. Typically 600 or 1080.
     */
    public int mediaH;

    /**
     * Frames per second to use in the video. Typically 30 or 60.
     */
    public int fps;

    private static JMathAnimConfig singletonConfig;
    private JMathAnimScene scene;
    private Renderer renderer;
    private Camera camera;

    public static JMathAnimConfig getConfig() {
        if (singletonConfig == null) {

            singletonConfig = new JMathAnimConfig();
        }
        return singletonConfig;
    }

    private JMathAnimConfig() {//Private constructor
    }

    /**
     * Set low quality settings (800,600, 30fps). These are the default settings
     */
    public void setLowQuality() {
        mediaW = 800;
        mediaH = 600;
        fps = 30;
    }

    /**
     * Set high quality settings (1920,1080, 60fps)
     */
    public void setMediumQuality() {
        mediaW = 1280;
        mediaH = 1024;
        fps = 30;
    }

    /**
     * Set high quality settings (1920,1080, 60fps)
     */
    public void setHighQuality() {
        mediaW = 1920;
        mediaH = 1080;
        fps = 60;
    }

    public JMathAnimScene getScene() {
        return scene;
    }

    public void setScene(JMathAnimScene scene) {
        this.scene = scene;
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }

    public Camera getFixedCamera() {
        return renderer.getFixedCamera();
    }

    public Camera getCamera() {
        return renderer.getCamera();
    }

    public void setCamera(Camera camera) {
        this.renderer.setCamera(camera);
    }

}
