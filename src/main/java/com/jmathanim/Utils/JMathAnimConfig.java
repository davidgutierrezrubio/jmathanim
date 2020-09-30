/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Utils;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.jmathanim.JMathAnimScene;
import java.io.File;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Stores all the data related to global configuration, to be accessed from any
 * object that requires it. This class implements the singleton pattern.
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class JMathAnimConfig {

    public File resourcesDir;
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

    public boolean createMovie;
    public boolean showPreview;
    public final HashMap<String,MathObjectDrawingProperties> templates;

    public static JMathAnimConfig getConfig() {
        if (singletonConfig == null) {

            singletonConfig = new JMathAnimConfig();
        }
        return singletonConfig;
    }
    //Background color, default black
    private JMColor backgroundColor = JMColor.BLACK;
    public boolean delay = true;

    //Shadow parameters
    /**
     * If true, draw a shadow of objects over the background image
     */
    public boolean drawShadow = false;
    /**
     * Amount of blurring. Bigger es more blurred (and more cpu expensive) a
     * value of 0 means no blurring
     */
    public int shadowKernelSize = 10;
    /**
     * XOffset for shadow
     */
    public int shadowOffsetX = 5;
    /**
     * YOffset for shadow
     */
    public int shadowOffsetY = 5;
    /**
     * alpha shadow multiplier. A value of .5 lets alpha shadow in 50%
     */
    public float shadowAlpha = 1f;//TODO: Doesn't work as expected
    public String backGroundImage = null;//"c:\\media\\hoja.jpg"

    private JMathAnimConfig() {//Private constructor
        templates=new HashMap<>();
        setDefaultMP();//Load "default" drawing style in dictionary
    }

    /**
     * Set low quality settings (854,480, 30fps). These are the default settings
     */
    public void setLowQuality() {
        mediaW = 854;
        mediaH = 480;
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

    public void setAdjustPreviewToFPS(boolean delay) {
        this.delay = delay;
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

    public JMColor getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(JMColor bakcgroundColor) {
        this.backgroundColor = bakcgroundColor;
    }

    public void setCreateMovie(boolean createMovie) {
        this.createMovie = createMovie;
    }

    public void setShowPreviewWindow(boolean showPreviewWindow) {
        this.showPreview = showPreviewWindow;
    }

    //MathObjectDrawingProperties
    /**
     * Set default values, in case no xml config file is loaded
     */
    public final void setDefaultMP() {
        MathObjectDrawingProperties defaultMP = new MathObjectDrawingProperties();
        //Default, boring values
        defaultMP.drawColor.set(JMColor.WHITE);
        defaultMP.fillColor.set(JMColor.GRAY);
        defaultMP.setFillAlpha(0);//No filling by default
        defaultMP.thickness = 1d;
        defaultMP.dashStyle = MathObjectDrawingProperties.SOLID;
        defaultMP.absoluteThickness = false;
        templates.put("default", defaultMP);
    }

    public MathObjectDrawingProperties getDefaultMP() {
        return templates.get("default").copy();
    }

}
