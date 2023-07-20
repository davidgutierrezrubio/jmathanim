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
package com.jmathanim.jmathanim;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import com.jmathanim.Animations.Animation;
import com.jmathanim.Animations.PlayAnim;
import com.jmathanim.Cameras.Camera;
import com.jmathanim.Constructible.GeogebraLoader;
import com.jmathanim.Renderers.MovieEncoders.SoundItem;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.ResourceLoader;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MathObjectGroup;
import com.jmathanim.mathobjects.MultiShapeObject;
import com.jmathanim.mathobjects.Shape;
import com.jmathanim.mathobjects.Text.LaTeXMathObject;
import com.jmathanim.mathobjects.shouldUdpateWithCamera;
import com.jmathanim.mathobjects.updateableObjects.Updateable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.slf4j.LoggerFactory;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class JMathAnimScene {

    /**
     * Logger class
     */
    public final static Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("com.jmathanim.jmathanim.JMathAnimScene");
    /**
     * Our loved constant PI
     */
    public static final double PI = 3.14159265358979323846;
    /**
     * The golden ratio
     */
    public static final double GOLDEN_RATIO = 1.6180339887498948482045868;
    /**
     * Constant to specify easily angles by degrees, like 45*DEGREES
     */
    public static final double DEGREES = PI / 180;
    /**
     * List of sceneObjects which needs to be drawn on the screen
     */
    private final ArrayList<MathObject> sceneObjects;
    private final HashSet<MathObject> objectsAlreadyDrawed;
    /**
     * List of sceneObjects which needs to be updated (not necessarily drawn)
     */
    final ArrayList<Updateable> objectsToBeUpdated;

    /**
     * List of sceneObjects which needs to be removed immediately after
     * rendering
     */
    final ArrayList<MathObject> objectsToBeRemoved;

    /**
     * Renderer to perform drawings
     */
    protected Renderer renderer;
    
    public Renderer getRenderer() {
        return renderer;
    }

    /**
     * Number of frames
     */
    protected int frameCount;
    
    public int getFrameCount() {
        return frameCount;
    }
    /**
     * Frames per second used in the animation
     */
    protected double fps;
    /**
     * Time step.
     */
    protected double dt;
    /**
     * Configuration
     */
    public JMathAnimConfig config;
    /**
     * This class is used to easily access to most common animations
     */
    protected final PlayAnim play;

    /**
     * Nanotime, used to control frame rate in preview window
     */
    public long nanoTime;
    /**
     * Previous nanotime in the last measure, used to control frame rate in
     * preview window
     */
    public long previousNanoTime;
    /**
     * Exit code of program
     */
    private int exitCode;
    private boolean animationIsDisabled;

    /**
     * Creates a new Scene with default settings.
     */
    public JMathAnimScene() {
        sceneObjects = new ArrayList<>();
        objectsAlreadyDrawed = new HashSet<>();
        config = JMathAnimConfig.getConfig();
        config.setLowQuality();
        objectsToBeUpdated = new ArrayList<>();
        objectsToBeRemoved = new ArrayList<>();
        play = new PlayAnim(this);// Convenience class for fast access to common animations
        config.setOutputFileName(this.getClass().getSimpleName());
        animationIsDisabled = false;
    }

    /**
     * Preparation code for the animation should go here
     */
    public abstract void setupSketch();

    /**
     * This method handles the creation of the renderer(s)
     */
    abstract void createRenderer();

    /**
     * Execute the current scene
     *
     * @return Exit code. 0 is no error, not 0 otherwise.
     */
    public final int execute() {
        
        String sketchName = this.getClass().getName();
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.reset();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(loggerContext);
        try {
            URL url = this.getClass().getClassLoader().getResource("logback.xml");// Loads default config for logger
            configurator.doConfigure(url);
        } catch (JoranException ex) {
            java.util.logging.Logger.getLogger(JMathAnimScene.class.getName()).log(Level.SEVERE, null, ex);
        }
        logger.info("Running sketch {} ", sketchName);
        
        setupSketch();
        createRenderer();
        JMathAnimConfig.getConfig().setRenderer(renderer);
        exitCode = 0;
        // In the global variable store Scene, Renderer and main Camera
        config.setScene(this);
//        try {
//            runSketch();
//        } catch (Exception ex) {
//            exitCode = 1;
//            logger.error(ex.toString());
//        } finally {
//            // Try anyway to finish the rendering
//            renderer.finish(frameCount);// Finish rendering jobs
//        }
        try {
            runSketch();
            renderer.finish(frameCount);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(JMathAnimScene.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (exitCode != 0) {
            logger.error("An error ocurred. Check the logs.");
        }
        return exitCode;
    }

    /**
     * Returns the list of sceneObjects to be drawn
     *
     * @return An ArrayList of MathObject
     */
    public ArrayList<MathObject> getMathObjects() {
        return sceneObjects;
    }
    
    public MathObject[] everything() {
        MathObject[] arr = new MathObject[sceneObjects.size()];
        for (int n = 0; n < sceneObjects.size(); n++) {
            arr[n] = sceneObjects.get(n);
        }
        return arr;
    }

    /**
     * Returns the list of objects to be updated. Note that this doesn't
     * necessarily matchs with objects drawn
     *
     * @return An ArrayList of MathObject
     */
    public ArrayList<Updateable> getObjectsToBeUpdated() {
        return objectsToBeUpdated;
    }

    /**
     * An abstract method to be overriden. Actual animations are implemented
     * here.
     *
     * @throws Exception Any expecption which may occur while performing the
     * animations
     */
    public abstract void runSketch() throws Exception;

    /**
     * Register the given objects to be updated. Any class that implements the
     * interface {@link Updateable} may be added here.
     *
     * @param objs {@link Updateable} sceneObjects (varargs)
     */
    public synchronized final void registerUpdateable(Updateable... objs) {
        for (Updateable obj : objs) {
            if ((obj != null) && (!objectsToBeUpdated.contains(obj))) {
                objectsToBeUpdated.add(obj);
                obj.registerUpdateableHook(this);
            }
        }
    }

    /**
     * Unregister the given objects to be updated. Any class that implements the
     * interface {@link Updateable} may be added here.
     *
     * @param objs {@link Updateable} sceneObjects (varargs)
     */
    public synchronized final void unregisterUpdateable(Updateable... objs) {
        objectsToBeUpdated.removeAll(Arrays.asList(objs));
    }

    /**
     * Adds the objects to scene but mark them for removal immediately after the
     * frame is drawn. This method is used mostly for frame-by-frame animations
     *
     * @param objs Objects to be drawn
     */
    public void addOnce(MathObject... objs) {
        add(objs);
        objectsToBeRemoved.addAll(Arrays.asList(objs));
    }

    /**
     * Overloaded method to add every MathObject from a Geogebra file
     *
     * @param gls GeogebraLoder with objects to add to scene
     */
    public synchronized final void add(GeogebraLoader... gls) {
        for (GeogebraLoader gl : gls) {
            add(gl.getObjects());
        }
    }

    /**
     * Add the specified MathObjects to the scene
     *
     * @param objs Mathobjects (varargs)
     */
    public synchronized final void add(MathObject... objs) {
        for (MathObject obj : objs) {
            if (obj != null) {
                if (!sceneObjects.contains(obj)) {
                    if (obj instanceof MathObjectGroup) {
                        for (MathObject subobj : ((MathObjectGroup) obj).getObjects()) {
                            add(subobj);
                        }
                    } else if (obj instanceof MultiShapeObject) {
//                        MultiShapeObject msh = (MultiShapeObject) obj;
//                        msh.isAddedToScene = true;
//                        for (Shape sh : msh) {
//                            add(sh);
//                        }
                        sceneObjects.add(obj);
                    } else {
                        sceneObjects.add(obj);
                        
                    }
                }
                registerUpdateable(obj);
                obj.addToSceneHook(this);
            }
            if (obj instanceof shouldUdpateWithCamera) {
                renderer.getCamera().registerUpdateable((shouldUdpateWithCamera) obj);
            }
        }
    }

    /**
     * Overloaded method. Remove the specified MathObjects from the scene
     *
     * @param objs ArrayList of Mathobjects
     */
    public synchronized final void remove(ArrayList<MathObject> objs) {
        remove((MathObject[]) objs.toArray(value -> new MathObject[value]));
        
    }

    /**
     * Remove the specified MathObjects from the scene
     *
     * @param objs Mathobjects (varargs)
     */
    public synchronized final void remove(MathObject... objs) {
        for (MathObject obj : objs) {
            if (obj != null) {
                if (obj instanceof MultiShapeObject) {
                    sceneObjects.remove(obj);
                    unregisterUpdateable(obj);
                    MultiShapeObject msh = (MultiShapeObject) obj;
                    msh.isAddedToScene = false;
                    for (Shape o : msh) {
                        this.remove(o);
                    }
                }
                
                if (obj instanceof MathObjectGroup) {
                    MathObjectGroup msh = (MathObjectGroup) obj;
                    for (MathObject o : msh) {
                        this.remove(o);
                    }
                }
                
                sceneObjects.remove(obj);
                obj.removedFromSceneHook(this);
                unregisterUpdateable(obj);
            }
              if (obj instanceof shouldUdpateWithCamera) {
                renderer.getCamera().unregisterUpdateable((shouldUdpateWithCamera) obj);
            }
        }
    }

    /**
     * This method performs the necessary drawing methods. First updates all
     * updateable objects and then draw all objects added to the scene. Objects
     * are sorted by layer, so that lower layers means drawing under.
     */
    protected final void doDraws() {
        objectsAlreadyDrawed.clear();
        doUpdates();
        if (!animationIsDisabled) {
            // Objects to be drawn on screen. Sort them by layer
            sceneObjects.sort((MathObject o1, MathObject o2) -> (o1.getLayer() - o2.getLayer()));
            for (MathObject obj : sceneObjects) {
                if (obj.isVisible()) {
                    if (!isAlreadyDrawed(obj)) {
                        obj.draw(this, renderer);
                        markAsAlreadyDrawed(obj);
                    }
                }
            }
        }

        // Now remove all marked sceneObjects from the scene
        remove((MathObject[]) objectsToBeRemoved.toArray(value -> new MathObject[value]));
        objectsToBeRemoved.clear();
    }

    /**
     * Perform all needed updates
     */
    private void doUpdates() {
        // For the array of sceneObjects to be updated (not necessarily drawn), I sort
        // them by the updatelevel variable
        // updatelevel 0 gets updated first (although negative values can be set too)
        // Objects with updatelevel n depend directly from those with level n-1
        objectsToBeUpdated.sort((Updateable o1, Updateable o2) -> o1.getUpdateLevel() - o2.getUpdateLevel());
        
        ArrayList<Updateable> updatesCopy = new ArrayList<>();
        updatesCopy.addAll(objectsToBeUpdated);
        
        for (Updateable obj : updatesCopy) {
            obj.update(this);
        }
    }

    /**
     * Advance one frame, making all necessary drawings and saving frame
     */
    public final void advanceFrame() {
        if (!animationIsDisabled) {
            renderer.clear();
        }
        doDraws();
        if (!animationIsDisabled) {
            frameCount++;
            saveMPFrame();
            previousNanoTime = nanoTime;
            nanoTime = System.nanoTime();
        }
        
    }

    /**
     * Saves the current image into a file. Format is guessed from the extension
     * of the file name. Formats supported depends on renderer used. If no
     * extension supplied, a png format is used. The file wil be saved in
     * project_home/media directory.
     *
     * @param filename Name of the file to be saved.
     */
    public final void saveImage(String filename) {
        doDraws();
        String fn;
        String format;
        //Determine extension
        Optional<String> extension = Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
        
        if (extension.isEmpty()) {
            //Add png as default extension
            fn = filename + ".png";
            format = "png";
        } else {
            format = extension.get();
            fn = filename;
        }
        
        renderer.saveImage(fn, format);
    }

    /**
     * Save the current frame using the renderer. Renderer should save the frame
     * to video, or any other format.
     */
    private void saveMPFrame() {
        
        try {
            renderer.saveFrame(frameCount);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(JMathAnimScene.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Plays the specified sound at the current frame.Sound files are loaded
 using the ResourceLoader class, so usual modifiers can be used
     *
     * @param soundName Name of sound file. By default it looks in
     * user_project/resources/sounds
     * @param pitch
     */
    public void playSound(String soundName,double pitch) {
        if (!config.isSoundsEnabled()) {
            return;
        }
        JMathAnimScene.logger.debug("Playing sound " + soundName+" with pitch "+pitch);
        ResourceLoader rl = new ResourceLoader();
        URL soundURL = rl.getResource(soundName, "sounds");
        long miliSeconds = (frameCount * 1000) / config.fps;

        SoundItem soundItem=SoundItem.make(soundURL, miliSeconds,pitch);
        renderer.addSound(soundItem);
    }

    public void playSound(String soundName) {
        playSound(soundName, 1);
    }

    
    /**
     * Play the given animations, generating new frames automatically until all
     * animations have finished.
     *
     * @param anims Animations to play, with a variable number or arguments
     */
    public void playAnimation(Animation... anims) {
        ArrayList<Animation> animArray = new ArrayList<>();
        animArray.addAll(Arrays.asList(anims));
        this.playAnimation(animArray);
    }

    /**
     * Play the given animations, generating new frames automatically until all
     * animations have finished.
     *
     * @param anims An ArrayList with Animation sceneObjects.
     */
    public void playAnimation(ArrayList<Animation> anims) {
        List<Animation> listAnims = anims.stream().filter(Objects::nonNull).collect(Collectors.toList());
        ArrayList<Animation> arAnims = new ArrayList<>(listAnims);
        for (Animation anim : arAnims) {
            if (anim.getStatus() == Animation.Status.FINISHED) {// This allow to reuse ended animations
                anim.setStatus(Animation.Status.NOT_INITIALIZED);
            }
            anim.initialize(this);// Perform needed steps immediately before playing
            if (!"".equals(anim.getDebugName())) {
                JMathAnimScene.logger.info("Begin animation: " + anim.getDebugName()+" ["+anim.getRunTime()+"s]");
            }
            
            if (animationIsDisabled) {
                anim.setT(1);
            }
        }
        
        boolean finished = false;
        while (!finished) {
            finished = true;
            boolean anyAnimationRunning = false;
            for (Animation anim : anims) {
                anyAnimationRunning = anyAnimationRunning | (anim.getStatus() == Animation.Status.RUNNING);
                final boolean resultAnimation = anim.processAnimation();
                finished = finished & resultAnimation;
                if (resultAnimation) {
                    anim.finishAnimation();
                }
            }
            if ((!finished) && (true)) {//If all animations are finished, no need to advance frame
                advanceFrame();
            }
        }
    }

    /**
     * Wait the specified time, generating the frames.
     *
     * @param time Time in seconds.
     */
    public void waitSeconds(double time) {
        if (animationIsDisabled) {
            return;
        }
        JMathAnimScene.logger.info("Waiting " + time + " seconds");
        int numFrames = (int) (time * fps);
        for (int n = 0; n < numFrames; n++) {
            try {
                advanceFrame();
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(JMathAnimScene.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }

    /**
     * Returns the current camera used in the scene
     *
     * @return The camera
     */
    public Camera getCamera() {
        return renderer.getCamera();
    }

    /**
     * Returns the fixed camera, used in vectors and other fixed-size elements
     *
     * @return The fixed camera
     */
    public Camera getFixedCamera() {
        return renderer.getFixedCamera();
    }
    
    public void formulaHelper(String... formulas) {
        LaTeXMathObject[] texes = new LaTeXMathObject[formulas.length];
        int n = 0;
        for (String t : formulas) {
            LaTeXMathObject lat = LaTeXMathObject.make(t);
            texes[n] = lat;
            n++;
        }
        formulaHelper(texes);
    }
    
    public void formulaHelper(LaTeXMathObject... texes) {
        MathObjectGroup group = new MathObjectGroup();
        for (LaTeXMathObject lat : texes) {
            int k = 0;
            for (Shape sh : lat) {
                sh.debugText("" + k);
                k++;
            }
            group.add(lat);
        }
        group.setLayout(MathObjectGroup.Layout.LOWER, .2);
        renderer.getCamera().zoomToObjects(group);
        add(group);
    }
    
    public JMathAnimConfig getConfig() {
        return config;
    }

    /**
     * Disable animations. If you invoke this method, subsequent drawings,
     * animations and movie generating will be disabled
     */
    public void disableAnimations() {
        this.animationIsDisabled = true;
    }
    
    public void enableAnimations() {
        this.animationIsDisabled = false;
    }

    /**
     * Gets the current visible area, in math coordinates
     *
     * @return A Rect object with the visible area
     */
    public Rect getMathView() {
        return renderer.getCamera().getMathView();
    }

    /**
     * Gets the current view width, in math coordinates
     *
     * @return The view width
     */
    public double getViewWidth() {
        return getMathView().getWidth();
    }

    /**
     * Gets the current view height, in math coordinates
     *
     * @return The view height
     */
    public double getViewHeight() {
        return getMathView().getHeight();
    }

    /**
     * Returns an Array with all objects added to the scene that are in the
     * specified layers
     *
     * @param layers Layers to retrieve objects from (varargs)
     * @return A MathObject[] array containing the objects
     */
    public MathObject[] getObjectsFromLayers(int... layers) {
        ArrayList<Integer> arLayers = new ArrayList<>();
        for (int k : layers) {
            arLayers.add(k);
        }
        MathObject[] resul = getMathObjects().stream().filter(obj -> arLayers.contains(obj.getLayer()))
                .toArray(MathObject[]::new);
        return resul;
    }

    /**
     * Check if a MathObject is already drawed in the current frame
     *
     * @param obj MathObject to check
     * @return True if is already drawed, false otherwise
     */
    public boolean isAlreadyDrawed(MathObject obj) {
        return objectsAlreadyDrawed.contains(obj);
    }

    /**
     * Mark a MathObject as drawed in the current frame
     *
     * @param obj MathObject to mark
     */
    public void markAsAlreadyDrawed(MathObject obj) {
        objectsAlreadyDrawed.add(obj);
    }

    /**
     * Reset the scene, deleting all objects, unregistering updaters, and
     * setting the camera to its default values
     */
    public void reset() {
        logger.info("Resetting scene");
        ArrayList<MathObject> objects = new ArrayList<>(getMathObjects());
        for (MathObject obj : objects) {
            remove(obj);
        }
        ArrayList<Updateable> updateables = new ArrayList<>(getObjectsToBeUpdated());
        for (Updateable upd : updateables) {
            unregisterUpdateable(upd);
        }
        renderer.getCamera().reset();
    }
    
}
