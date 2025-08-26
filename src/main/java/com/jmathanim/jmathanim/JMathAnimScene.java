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
import com.jmathanim.Enum.LayoutType;
import com.jmathanim.Enum.LinkType;
import com.jmathanim.Renderers.MovieEncoders.SoundItem;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Styling.MODrawProperties;
import com.jmathanim.Utils.*;
import com.jmathanim.mathobjects.*;
import com.jmathanim.mathobjects.Text.LatexMathObject;
import com.jmathanim.mathobjects.Text.LatexShape;
import com.jmathanim.mathobjects.updateableObjects.Updateable;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.DoubleUnaryOperator;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
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
     * 2PI
     */
    public static final double PI2 = 2 * PI;
    /**
     * The golden ratio
     */
    public static final double GOLDEN_RATIO = 1.6180339887498948482045868;
    /**
     * Constant to specify easily angles by degrees, like 45*DEGREES
     */
    public static final double DEGREES = PI / 180;
    /**
     * This class is used to easily access to most common animations
     */
    public final PlayAnim play;
    protected final JMathAnimScene scene;
    /**
     * List of sceneObjects which needs to be updated (not necessarily drawn)
     */
    final ArrayList<Updateable> objectsToBeUpdated;
    /**
     * List of sceneObjects which needs to be removed immediately after rendering
     */
    final ArrayList<MathObject<?>> objectsToBeRemoved;
    /**
     * List of sceneObjects which needs to be drawn on the screen
     */
    private final ArrayList<MathObject<?>> sceneObjects;
    private final HashSet<MathObject<?>> objectsAlreadydrawn;
    /**
     * Links to be executed, right before the updates
     */
    private final ArrayList<Link> linksToBeDone;
    /**
     * A dictionary containing all loaded styles
     */
    private final HashMap<String, MODrawProperties> styles;
    /**
     * Configuration
     */
    public JMathAnimConfig config;
    /**
     * Nanotime, used to control frame rate in preview window
     */
    public long nanoTime;
    /**
     * Previous nanotime in the last measure, used to control frame rate in preview window
     */
    public long previousNanoTime;
    /**
     * Renderer to perform drawings
     */
    protected Renderer renderer;
    /**
     * Number of frames
     */
    protected int frameCount;
    /**
     * Frames per second used in the animation
     */
    protected double fps;
    /**
     * Time step.
     */
    protected double dt;
    /**
     * Exit code of program
     */

    /**
     * Time in milliseconds per frame
     */
    protected long timeMillisPerFrame;

    /**
     * Last time in milliseconds. Used to control fps
     */
    protected long lastTimeMillis;

    private int exitCode;
    /**
     * If true, frames are not generated and animations are instantly processed
     */
    private boolean animationIsDisabled;
    private long startTime;

    /**
     * Creates a new Scene with default settings.
     */
    public JMathAnimScene() {
        scene = this;
        sceneObjects = new ArrayList<>();
        objectsAlreadydrawn = new HashSet<>();
        config = JMathAnimConfig.getConfig();
        config.setLowQuality();
        linksToBeDone = new ArrayList<>();
        objectsToBeUpdated = new ArrayList<>();
        objectsToBeRemoved = new ArrayList<>();
        play = new PlayAnim(this);// Convenience class for fast access to common animations
        config.setOutputFileName(this.getClass().getSimpleName());
        animationIsDisabled = false;
        styles = config.getStyles();
        Locale.setDefault(Locale.US);
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public int getFrameCount() {
        return frameCount;
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

        startTime = System.currentTimeMillis();

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
        logger.setLevel(ch.qos.logback.classic.Level.INFO);//Default log level: INFO
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
        double secondsElapsed = (System.currentTimeMillis() - startTime) * 1d / 1000d;
        DecimalFormat df = new DecimalFormat("0.00");
        logger.info("Elapsed time " + df.format(secondsElapsed)
                + " seconds ("
                + df.format(frameCount * 1d / secondsElapsed)
                + " fps)");
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
    public ArrayList<MathObject<?>> getMathObjects() {
        return sceneObjects;
    }

    /**
     * Returns the list of objects to be updated. Note that this doesn't necessarily matchs with objects drawn
     *
     * @return An ArrayList of MathObject
     */
    public ArrayList<Updateable> getObjectsToBeUpdated() {
        return objectsToBeUpdated;
    }

    /**
     * An abstract method to be overridden. Actual animations are implemented here.
     *
     * @throws Exception Any exception which may occur while performing the animations
     */
    public abstract void runSketch() throws Exception;

    /**
     * Register the given objects to be updated. Any class that implements the interface {@link Updateable} may be added
     * here.
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
     * Unregister the given objects to be updated. Any class that implements the interface {@link Updateable} may be
     * added here.
     *
     * @param objs {@link Updateable} sceneObjects (varargs)
     */
    public synchronized final void unregisterUpdateable(Updateable... objs) {
        objectsToBeUpdated.removeAll(Arrays.asList(objs));
    }

    /**
     * Adds the objects to scene but mark them for removal immediately after the frame is drawn. This method is used
     * mostly for frame-by-frame animations
     *
     * @param objs Objects to be drawn
     */
    public void addOnce(MathObject<?>... objs) {
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
    public synchronized final void add(MathObject<?>... objs) {
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
                MediatorMathObject.addToSceneHook(obj, this);
                Camera cam = (obj.getCamera() == null ? renderer.getCamera() : obj.getCamera());
                if (obj instanceof shouldUdpateWithCamera) {
                    cam.registerUpdateable((shouldUdpateWithCamera) obj);
                }

            }

        }
    }

    /**
     * Overloaded method. Remove the specified MathObjects from the scene
     *
     * @param objs ArrayList of Mathobjects
     */
    public synchronized final void remove(ArrayList<MathObject<?>> objs) {
        remove((MathObject[]) objs.toArray());
//        remove((MathObject[]) objs.toArray(value -> new MathObject[value]));

    }

    /**
     * Remove the specified MathObjects from the scene
     *
     * @param objs Mathobjects (varargs)
     */
    public synchronized final void remove(MathObject<?>... objs) {
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

                MediatorMathObject.removedFromSceneHook(obj, this);
                unregisterUpdateable(obj);
            }
            if (obj instanceof shouldUdpateWithCamera) {
                renderer.getCamera().unregisterUpdateable((shouldUdpateWithCamera) obj);
            }
        }
    }

    /**
     * This method performs the necessary drawing methods. First updates all updateable objects, apply links, and draw
     * all objects added to the scene. Objects are sorted by layer, so that lower layers means drawing under.
     */
    protected final void doDraws() {
        objectsAlreadydrawn.clear();
        doUpdates();
        doLinks();

        if (!animationIsDisabled) {
            // Objects to be drawn on screen. Sort them by layer
            sceneObjects.sort((MathObject o1, MathObject o2) -> o1.getLayer().compareTo(o2.getLayer()));
            for (MathObject obj : sceneObjects) {
                if (obj.isVisible()) {
                    if (!isAlreadydrawn(obj)) {
                        obj.draw(this, renderer, obj.getCamera());
                        markAsAlreadydrawn(obj);
                    }
                }
            }
        }

        // Now remove all marked sceneObjects from the scene
        if (!objectsToBeRemoved.isEmpty())
            remove((MathObject[]) objectsToBeRemoved.toArray());
//        remove((MathObject[]) objectsToBeRemoved.toArray(value -> new MathObject[value]));
        objectsToBeRemoved.clear();
    }

    private void doLinks() {
        for (Link link : linksToBeDone) {
            link.apply();
        }
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
        long now = System.currentTimeMillis();


        if (!animationIsDisabled) {
            renderer.clearAndPrepareCanvasForAnotherFrame();
        }

        //This method performs all updates and drawings needed
        doDraws();


        if (!animationIsDisabled) {
            frameCount++;
            saveMPFrame();
            previousNanoTime = nanoTime;
            nanoTime = System.nanoTime();


            if (config.isLimitFPS()) {
                long elapsedTime = System.currentTimeMillis() - now;
                if (elapsedTime < timeMillisPerFrame) {
                    try {
                        Thread.sleep(timeMillisPerFrame - elapsedTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                lastTimeMillis = System.currentTimeMillis();
            }
        }
    }

    /**
     * Saves the current image into a file. Format is guessed from the extension of the file name. Formats supported
     * depends on renderer used. If no extension supplied, a png format is used. The file wil be saved in
     * project_home/media directory.
     *
     * @param filename Name of the file to be saved.
     */
    public final void saveImage(String filename) {
        renderer.clearAndPrepareCanvasForAnotherFrame();
        doDraws();
        String fn;
        String format;
        //Determine extension
        Optional<String> extension = Optional.ofNullable(filename).filter(f -> f.contains(".")).map(f -> f.substring(filename.lastIndexOf(".") + 1));

        if ("".equals(extension.toString())) {
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
     * Save the current frame using the renderer. Renderer should save the frame to video, or any other format.
     */
    private void saveMPFrame() {

        try {
            renderer.saveFrame(frameCount);
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(JMathAnimScene.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Plays the specified sound at the current frame.Sound files are loaded using the ResourceLoader class, so usual
     * modifiers can be used
     *
     * @param soundName Name of sound file. By default it looks in user_project/resources/sounds
     * @param pitch
     */
    public void playSound(String soundName, double pitch) {
        if (!config.isSoundsEnabled()) {
            if (!animationIsDisabled) {
                return;
            }
        }

        ResourceLoader rl = new ResourceLoader();
        URL soundURL = rl.getResource(soundName, "sounds");
        File file;
        try {
            file = new File(soundURL.toURI());
        } catch (URISyntaxException ex) {
            JMathAnimScene.logger.error("Sound " + soundName + " is not a correct URL resource.");
            return;
        }

        if (!file.exists()) {
            JMathAnimScene.logger.error("Sound " + soundName + " not found. Verify that the name is correct.");
            return;
        }

        JMathAnimScene.logger.debug("Playing sound " + soundName + " with pitch " + pitch);
        long miliSeconds = (frameCount * 1000L) / config.fps;

        SoundItem soundItem = SoundItem.make(soundURL, miliSeconds, pitch);
        renderer.addSound(soundItem);
    }

    public void playSound(String soundName) {
        playSound(soundName, 1);
    }

    /**
     * Play the given animations, generating new frames automatically until all animations have finished.
     *
     * @param anims Animations to play, with a variable number or arguments
     */
    public void playAnimation(Animation... anims) {
        ArrayList<Animation> animArray = new ArrayList<>();
        animArray.addAll(Arrays.asList(anims));
        this.playAnimation(animArray);
    }

    /**
     * Play the given animations, generating new frames automatically until all animations have finished.
     *
     * @param anims An ArrayList with Animation sceneObjects.
     */
    public void playAnimation(ArrayList<Animation> anims) {
        List<Animation> listAnims = anims.stream().filter(Objects::nonNull).collect(Collectors.toList());
        for (Animation anim : listAnims) {
            if (anim.isShouldResetAtFinish()) {
                anim.reset();
            }
            anim.setT(0);
            if (anim.getStatus() == Animation.Status.FINISHED) {// This allow to reuse ended animations
                anim.setStatus(Animation.Status.INITIALIZED);
            }
            anim.initialize(this);// Perform needed steps immediately before playing
            if (!"".equals(anim.getDebugName())) {
                JMathAnimScene.logger.info("Begin animation: " + LogUtils.CYAN + anim.getDebugName() + LogUtils.RESET + " [" + LogUtils.GREEN + anim.getRunTime() + "s" + LogUtils.RESET + "]");
            }

            if (animationIsDisabled) {
                anim.setT(1);
            }
        }

        boolean finished = false;
        while (!finished) {
            finished = true;
            boolean anyAnimationRunning = false;
            for (Animation anim : listAnims) {
                anyAnimationRunning = anyAnimationRunning | (anim.getStatus() == Animation.Status.RUNNING);
                final boolean resultAnimation = anim.processAnimation();
                finished = finished & resultAnimation;
                if (resultAnimation) {
                    anim.finishAnimation();
                }
            }
//            if (!finished) {//If all animations are finished, no need to advance frame
                advanceFrame();
//            }
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
        JMathAnimScene.logger.info("Waiting " + LogUtils.GREEN + time + "s" + LogUtils.RESET);
        int numFrames = (int) (time * fps);
        for (int n = 0; n < numFrames; n++) {
            try {
                if (config.isPrintProgressBar()) {
                    LogUtils.printProgressBar(1d * n / (numFrames - 1));
                }

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
        LatexMathObject[] texes = new LatexMathObject[formulas.length];
        int n = 0;
        for (String t : formulas) {
            LatexMathObject lat = LatexMathObject.make(t);
            texes[n] = lat;
            n++;
        }
        formulaHelper(texes);
    }

    public void formulaHelper(LatexMathObject... texes) {
        MathObjectGroup group = new MathObjectGroup();
        for (LatexMathObject lat : texes) {
            int k = 0;
            for (LatexShape sh : lat) {
                MediatorMathObject.setDebugText(sh, "" + k);
                k++;
            }
            group.add(lat);
        }
        group.setLayout(LayoutType.LOWER, .2);
        renderer.getCamera().zoomToObjects(group);
        add(group);
    }

    public JMathAnimConfig getConfig() {
        return config;
    }

    /**
     * Disable animations. If you invoke this method, subsequent drawings, animations and movie generating will be
     * disabled
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
     * Returns an Array with all objects added to the scene that are in the specified layers
     *
     * @param layers Layers to retrieve objects from (varargs)
     * @return A MathObject[] array containing the objects
     */
    public MathObject[] getObjectsFromLayers(int... layers) {
        ArrayList<Integer> arLayers = new ArrayList<>();
        for (int k : layers) {
            arLayers.add(k);
        }
        ArrayList<MathObject<?>> resul = new ArrayList<>();
        for (MathObject mathObject : getMathObjects()) {
            if (arLayers.contains(mathObject.getLayer())) {
                resul.add(mathObject);
            }
        }
        return (MathObject[]) resul.toArray();
    }

    /**
     * Check if a MathObject is already drawn in the current frame
     *
     * @param obj MathObject to check
     * @return True if is already drawn, false otherwise
     */
    public boolean isAlreadydrawn(MathObject obj) {
        return objectsAlreadydrawn.contains(obj);
    }

    /**
     * Mark a MathObject as drawn in the current frame
     *
     * @param obj MathObject to mark
     */
    public void markAsAlreadydrawn(MathObject obj) {
        objectsAlreadydrawn.add(obj);
    }

    /**
     * Reset the scene, deleting all objects, unregistering updaters, and setting the camera to its default values
     */
    public void reset() {
        logger.info("Resetting scene");
        ArrayList<MathObject<?>> objects = new ArrayList<>(getMathObjects());
        for (MathObject obj : objects) {
            remove(obj);
        }
        ArrayList<Updateable> updateables = new ArrayList<>(getObjectsToBeUpdated());
        for (Updateable upd : updateables) {
            unregisterUpdateable(upd);
        }
        for (Link link : linksToBeDone) {
            unregisterLink(link);
        }
        renderer.getCamera().reset();
        renderer.getFixedCamera().reset();
    }

    public MODrawProperties getStyle(String name) {
        name = name.toUpperCase();
        if (styles.containsKey(name)) {
            return styles.get(name);
        } else {
            JMathAnimScene.logger.warn("No style with name {} found, returning null style", name);
            return MODrawProperties.makeNullValues();
        }
    }

    public void infoMessage(String message) {
        logger.info(message);
    }

    /**
     * Convenience function. Returns the width of the current math view. Can be used to quicky acces to this parameter
     * if you need to perform measures relative to the math view rather than math units, so .1*mw() stands for 10% of
     * screen width.
     *
     * @return The current mathview width, in math units.
     */
    public double mw() {
        return config.getCamera().getMathView().getWidth();
    }

    /**
     * Check if an object is in the scene. MathObjectGroup objects are considered to be in the scene if all their
     * elements are.
     *
     * @param mathobject Object to check
     * @return True if object is in the scene. False otherwise.
     */
    public boolean isInScene(MathObject mathobject) {

        //If a MathObjectGroup, it is considered to be in the scene if all its elements are
        //An empty one returns true.
        if (mathobject instanceof MathObjectGroup) {
            MathObjectGroup mg = (MathObjectGroup) mathobject;
            boolean resul = true;
            for (MathObject subObj : mg) {
                resul = resul & isInScene(subObj);
                if (!resul) {
                    return false;
                }
            }
            return resul;
        }
        //Other case
        return getMathObjects().contains(mathobject);
    }

    public Link registerLink(Link link) {
        linksToBeDone.add(link);
        return link;
    }

    /**
     * Register a new Link to be done at every frame. A double value will be extracted from the origin object and
     * applied to the destiny
     *
     * @param origin      Origin object
     * @param originType  Origin link
     * @param destiny     Destiny object
     * @param destinyType Destiny link
     * @return The created link
     */
    public LinkArguments registerLink(Object origin, LinkType originType, Linkable destiny, LinkType destinyType) {
        LinkArguments link = LinkArguments.make(origin, originType, destiny, destinyType);
        linksToBeDone.add(link);
        return link;
    }

    /**
     * Register a new Link to be done at every frame. A double value will be extracted from the origin object and
     * applied to the destiny
     *
     * @param origin      Origin object
     * @param originType  Origin link.
     * @param destiny     Destiny object
     * @param destinyType Destiny link
     * @param function    Function to apply to the value before applying to the destiny object
     * @return The created link
     */
    public LinkArguments registerLink(Linkable origin, LinkType originType, Linkable destiny, LinkType destinyType, DoubleUnaryOperator function) {
        LinkArguments link = LinkArguments.make(origin, originType, destiny, destinyType, function);
        linksToBeDone.add(link);
        return link;
    }

    /**
     * Removes the given link from the list of links to be done
     *
     * @param link LinkArguments to remove
     * @return True if link existed and was removed
     */
    public boolean unregisterLink(Link link) {
        return linksToBeDone.remove(link);
    }

    /**
     * Returns the current time step for each frame.
     *
     * @return The time step
     */
    public double getDt() {
        return dt;
    }


}
