/*
 * Copyright (C) 2020 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.jmathanim.Utils;

import com.jmathanim.Cameras.Camera;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Styling.*;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;

import java.io.File;
import java.net.URL;

/**
 * Stores all the data related to global configuration, to be accessed from any
 * object that requires it. This class implements the singleton pattern.
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class JMathAnimConfig {

    private String outputFileName;
    private String ffmpegBinExecutable;
    private boolean soundsEnabled;
    private File resourcesDir;
    private File outputDir;
    private File saveFilePath;

    public File getSaveFilePath() {
        return saveFilePath;
    }

    public void setSaveFilePath(File saveFilePath) {
        this.saveFilePath = saveFilePath;
    }
    /**
     * Width of media screen. Typically 800 or 1920.
     */
    public int mediaW = 800;
    /**
     * Height of media screen. Typically 600 or 1080.
     */
    public int mediaH = 600;

    /**
     * Frames per second to use in the video. Typically 30 or 60.
     */
    public int fps = 30;
    /**
     * Singleton pattern to ensure there is a common config class for whole
     * execution
     */

    /**
     * If true, FPS will be restricted in preview windows. If false, animation will be played at max speed possible.
     */
    private boolean limitFPS;

    private static JMathAnimConfig singletonConfig;
    /**
     * The scene used
     */
    private JMathAnimScene scene;
    /**
     * The renderer used
     */
    private Renderer renderer;
    /**
     * If true, the current renderer should render the result into a movie
     */
    private boolean createMovie = false;

    /**
     * If true, the current renderer should save each frame onto a separate png
     * file
     */
    private boolean saveToPNG = false;
    /**
     * If true, the current renderer should show a preview of the result
     */
    private boolean showPreview = true;
    /**
     * A dictionary of the styles to be used in the objects
     */
    private final HashMapUpper<String, MODrawProperties> styles;

     /**
     * A dictionary of the latexStyles to be used in LaTexMathObjects
     */
    private final HashMapUpper<String, LatexStyle> latexStyles;
    
    /**
     * Returns the config, using the singleton pattern.
     *
     * @return The config object, global scope.
     */
    public static JMathAnimConfig getConfig() {
        if (singletonConfig == null) {

            singletonConfig = new JMathAnimConfig();
        }
        return singletonConfig;
    }

    // Background color, default black
    private PaintStyle backgroundColor = JMColor.BLACK;
    public boolean delay = true;

    // Shadow parameters
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
    public float shadowAlpha = .5f;
    private URL backGroundImage = null;

    /**
     * If true, the frame number will be superimposed on screen, for debugging
     * purposes
     */
    public boolean showFrameNumbers = false;

    public void setDrawShadow(boolean drawShadow) {
        this.drawShadow = drawShadow;
    }

    public void setShadowParameters(int kernelSize, int offsetX, int offsetY, float alpha) {
        this.shadowKernelSize = kernelSize;
        this.shadowOffsetX = offsetX;
        this.shadowOffsetY = offsetY;
        this.shadowAlpha = alpha;
    }

    public void setBackGroundImage(String name) {
        ResourceLoader rl = new ResourceLoader();
        setBackGroundImage(rl.getResource(name, "images"));
        JMathAnimScene.logger.info("Background image set to " + rl.getResource(name, "images"));
    }

    public void setBackGroundImage(URL backGroundImage) {
        this.backGroundImage = backGroundImage;
    }

    public URL getBackGroundImage() {
        return backGroundImage;
    }

    private JMathAnimConfig() {// Private constructor
        styles = new HashMapUpper<>("styles");
        latexStyles = new HashMapUpper<>("latex styles");
        setDefaultMP();// Load "default" drawing style in dictionary
        resourcesDir = new File("." + File.separator + "resources");
        outputDir = new File("." + File.separator + "media");
        ffmpegBinExecutable = "";
        soundsEnabled = true;
        limitFPS=false;
    }

    public boolean isSoundsEnabled() {
        return soundsEnabled;
    }

    public void setSoundsEnabled(boolean enableSounds) {
        this.soundsEnabled = enableSounds;
    }

    public String getFfmpegBinExecutable() {
        return ffmpegBinExecutable;
    }

    public void setFfmpegExecutable(String ffmpegBinDir) {
        this.ffmpegBinExecutable = ffmpegBinDir;
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
        mediaH = 720;
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

    public PaintStyle getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(PaintStyle bakcgroundColor) {
        this.backgroundColor = bakcgroundColor;
    }

    public void setCreateMovie(boolean createMovie) {
        this.createMovie = createMovie;
    }

    public void setShowPreviewWindow(boolean showPreviewWindow) {
        this.showPreview = showPreviewWindow;
    }

    // MathObjectDrawingProperties
    /**
     * Set default values, in case no xml config file is loaded
     */
    public final void setDefaultMP() {
        // Default, boring values
        //in case no style.xml file has been loaded
        MODrawProperties defaultMP = new MODrawProperties();
        defaultMP.setDrawColor(JMColor.WHITE);
        defaultMP.setFillColor(JMColor.GRAY);
        defaultMP.setFillAlpha(0);// No filling by default
        defaultMP.setThickness(4d);
        defaultMP.setDashStyle(MODrawProperties.DashStyle.SOLID);
        defaultMP.setAbsoluteThickness(false);
        styles.put("DEFAULT", defaultMP);

        MODrawProperties defaultDotMP = new MODrawProperties();
        defaultDotMP.setDrawColor(JMColor.WHITE);
        defaultDotMP.setFillColor(JMColor.GRAY);
        defaultDotMP.setDotStyle(Point.DotSyle.CIRCLE);
        defaultDotMP.setThickness(30d);
        styles.put("DOTDEFAULT", defaultMP);

        MODrawProperties latexDefaultMP = defaultMP.copy();
        latexDefaultMP.setFillColor(JMColor.WHITE);
        latexDefaultMP.setFillAlpha(1);// Latex formulas are filled by default
        styles.put("LATEXDEFAULT", latexDefaultMP);

        MODrawProperties defaultArrowMP = new MODrawProperties();
        defaultDotMP.setDrawColor(JMColor.WHITE);
        defaultDotMP.setFillColor(JMColor.WHITE);
        defaultDotMP.setThickness(8d);
        styles.put("ARROWDEFAULT", defaultArrowMP);
    }

    public MODrawProperties getDefaultMP() {
        return styles.get("DEFAULT").copy();
    }

    public File getResourcesDir() {
        return resourcesDir;
    }

    /**
     * Sets the resources path. When a relative path, it refers to the current
     * execution directory.
     *
     * @param path A string with the resources path
     */
    public void setResourcesDir(String path) {
        this.resourcesDir = new File(path);
    }

    public String getOutputFileName() {
        return outputFileName;
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    public File getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(String str) {
        this.outputDir = new File(str);
    }

    public void setOutputDir(File outputDir) {
        this.outputDir = outputDir;
    }

    public HashMapUpper<String, MODrawProperties> getStyles() {
        return styles;
    }
    
    public HashMapUpper<String, LatexStyle> getLatexStyles() {
        return latexStyles;
    }

    public boolean isCreateMovie() {
        return createMovie;
    }

    public boolean isShowPreview() {
        return showPreview;
    }

    public MODrawProperties createStyleFrom(Stylable mp, String styleName) {
        JMathAnimScene.logger.info("Creating style {}", styleName.toUpperCase());
        MODrawProperties mpO = mp.getFirstMP();
        return styles.put(styleName.toUpperCase(), mpO);
    }

    public MODrawProperties createStyleFrom(MathObject obj, String styleName) {
        return createStyleFrom(obj.getMp(), styleName);
    }

    public void setMediaW(int mediaW) {
        this.mediaW = mediaW;
    }

    public void setMediaH(int mediaH) {
        this.mediaH = mediaH;
    }

    public void setFPS(int fps) {
        this.fps = fps;
    }

    public void parseFile(String url) {
        ConfigLoader.parseFile(url);
    }

    /**
     * Returns the save to png flag
     *
     * @return True if the current renderer saves each frame to a png file,
     * false otherwise.
     */
    public boolean isSaveToPNG() {
        return saveToPNG;
    }

    /**
     * Sets the save to png flag
     *
     * @param saveToPNG True if the current renderer saves each frame to a png
     * file, false otherwise.
     */
    public void setSaveToPNG(boolean saveToPNG) {
        this.saveToPNG = saveToPNG;
    }

    public void setShowDebugFrameNumbers(boolean showFrameNumbers) {
        this.showFrameNumbers = showFrameNumbers;
    }


    public boolean isLimitFPS() {
        return limitFPS;
    }

    public void setLimitFPS(boolean limitFPS) {
        this.limitFPS = limitFPS;
    }
}
