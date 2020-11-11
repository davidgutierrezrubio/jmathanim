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

import com.jmathanim.Animations.Animation;
import com.jmathanim.Animations.PlayAnim;
import com.jmathanim.Cameras.Camera;
import com.jmathanim.Renderers.Java2DAwtRenderer;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MathObjectGroup;
import com.jmathanim.mathobjects.updateableObjects.Updateable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class JMathAnimScene {

    /**
     * Logger class
     */
    public final static Logger logger = LoggerFactory.getLogger("com.jmathanim.jmathanim.JMathAnimScene");
    /**
     * Our loved constant PI
     */
    public static final double PI = 3.14159265358979323846;
    /**
     * Constant to specify easily angles by degrees, like 45*DEGREES
     */
    public static final double DEGREES = PI / 180;
    /**
     * List of objects which needs to be drawn on the screen
     */
    final ArrayList<MathObject> objects;
    /**
     * List of objects which needs to be updated (not necessarily drawn)
     */
    final ArrayList<Updateable> objectsToBeUpdated;
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

    /**
     * Creates a new Scene with default settings.
     */
    public JMathAnimScene() {
        objects = new ArrayList<>();
        config = JMathAnimConfig.getConfig();
        config.setLowQuality();
        objectsToBeUpdated = new ArrayList<>();
        play = new PlayAnim(this);//Convenience class for fast access to common animations
        config.setOutputFileName(this.getClass().getSimpleName());
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

        String nombre = this.getClass().getName();
        logger.info("Running sketch {} ", nombre);
        setupSketch();
        createRenderer();
        JMathAnimConfig.getConfig().setRenderer(renderer);
        exitCode = 0;
        //In the global variable store Scene, Renderer and main Camera
        config.setScene(this);
        try {
            runSketch();
        } catch (Exception ex) {
            exitCode = 1;
            logger.error(ex.toString());
            ex.printStackTrace();
            if (renderer instanceof Java2DAwtRenderer) {
                Java2DAwtRenderer ren = (Java2DAwtRenderer) renderer;
                if (ren.getPreviewWindow() != null) {
                    ren.getPreviewWindow().setVisible(true);
                }
            }
        } finally {
            //Try anyway to finish the rendering
            renderer.finish(frameCount);//Finish rendering jobs
        }
        if (exitCode != 0) {
            logger.error("An error ocurred. Check the logs.");
        }
        return exitCode;
    }

    /**
     * Returns the list of objects to be drawn
     *
     * @return An ArrayList of MathObject
     */
    public ArrayList<MathObject> getObjects() {
        return objects;
    }

    public MathObject[] everything() {
        MathObject[] arr = new MathObject[objects.size()];
        for (int n = 0; n < objects.size(); n++) {
            arr[n] = objects.get(n);
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
     * @param objs {@link Updateable} objects (varargs)
     */
    public synchronized final void registerObjectToBeUpdated(Updateable... objs) {
        for (Updateable obj : objs) {
            if (!objectsToBeUpdated.contains(obj)) {
                objectsToBeUpdated.add(obj);
            }
        }
    }

    /**
     * Unregister the given objects to be updated. Any class that implements the
     * interface {@link Updateable} may be added here.
     *
     * @param objs {@link Updateable} objects (varargs)
     */
    public synchronized final void unregisterObjectToBeUpdated(Updateable... objs) {
        objectsToBeUpdated.removeAll(Arrays.asList(objs));
    }

    /**
     * Add the specified MathObjects to the scene
     *
     * @param objs Mathobjects (varargs)
     */
    public synchronized final void add(MathObject... objs) {
        for (MathObject obj : objs) {
            if (!objects.contains(obj)) {
                if (obj instanceof MathObjectGroup) {
                    for (MathObject subobj : ((MathObjectGroup) obj).getObjects()) {
                        add(subobj);
                    }
                } else {
                    objects.add(obj);
                }
                obj.addScene(this);
                //Check if this object is Updateable.
                //This interface is present in every MathObject which needs to
                //be updated every frame
                registerObjectToBeUpdated(obj);
                obj.registerChildrenToBeUpdated(this);
            }
        }
    }

    /**
     * Remove the specified MathObjects from the scene
     *
     * @param objs Mathobjects (varargs)
     */
    public synchronized final void remove(MathObject... objs) {
        for (MathObject obj : objs) {
            objects.remove(obj);
            obj.removeScene(this);
            unregisterObjectToBeUpdated(obj);
        }
    }

    /**
     * This method performs the necessary drawing methods. First updates all
     * updateable objects and then draw all objects added to the scene. Objects
     * are sorted by layer, so that lower layers means drawing under.
     */
    protected final void doDraws() {

        //For the array of objects to be updated (not necessarily drawn), I sort them by the updatelevel variable
        //updatelevel 0 gets updated first.
        //Objects with updatelevel n depend directly from those with level n-1
        objectsToBeUpdated.sort((Updateable o1, Updateable o2) -> o1.getUpdateLevel() - o2.getUpdateLevel());
        for (Updateable obj : objectsToBeUpdated) {
            obj.update(this);
        }
        //Objects to be drawn on screen. Sort them by layer
        objects.sort((MathObject o1, MathObject o2) -> (o1.getLayer() - o2.getLayer()));
        for (MathObject obj : objects) {
            if (obj.visible) {
                obj.draw(renderer);
            }
        }

    }

    /**
     * Advance one frame, making all necessary drawings and saving frame
     */
    public final void advanceFrame() {

        renderer.clear();
        doDraws();
        frameCount++;
        saveMPFrame();
        previousNanoTime = nanoTime;
        nanoTime = System.nanoTime();

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
     * @param anims An ArrayList with Animation objects.
     */
    public void playAnimation(ArrayList<Animation> anims) {
        for (Animation anim : anims) {
            if (anim != null) {
                anim.initialize();//Perform needed steps immediately before playing
                anim.addObjectsToScene(this); //Add necessary objects if not already in the scene.
            }
        }

        boolean finished = false;
        while (!finished) {
            finished = true;
            for (Animation anim : anims) {
                if (anim != null) {
                    finished = finished & anim.processAnimation();
                }
            }

            advanceFrame();
        }
    }

    /**
     * Wait the specified time, generating the frames.
     *
     * @param time Time in seconds.
     */
    public void waitSeconds(double time) {
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

}
