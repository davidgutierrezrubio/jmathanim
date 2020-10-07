/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.jmathanim;

import ch.qos.logback.classic.Level;
import com.jmathanim.Animations.Animation;
import com.jmathanim.Animations.PlayAnim;
import com.jmathanim.Cameras.Camera;
import com.jmathanim.Renderers.Java2DRenderer;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.updateableObjects.Updateable;
import java.util.ArrayList;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class JMathAnimScene {

    public final static Logger logger = LoggerFactory.getLogger("com.jmathanim.jmathanim.JMathAnimScene");
    public static final double PI = 3.14159265358979323846;
    public static final double DEGREES = PI / 180;
    int contador = 0;
    int x;
    final ArrayList<MathObject> objects;
    final ArrayList<Updateable> objectsToBeUpdated;
    protected Renderer renderer;
    protected int frameCount;
    protected double fps;
    protected double dt;
    public JMathAnimConfig conf;
    protected final PlayAnim play;
    public long nanoTime;
    public long previousNanoTime;
    private int exitCode;

    public JMathAnimScene() {
        objects = new ArrayList<>(); //TODO: Extends this to include layers
        conf = JMathAnimConfig.getConfig();
        conf.setLowQuality();//by default, set low quality
        objectsToBeUpdated = new ArrayList<>();
        play = new PlayAnim(this);//Convenience class for fast access to common animations
        conf.setOutputFileName(this.getClass().getSimpleName());
    }

    /**
     * Preparation code for the animation should go here
     */
    public abstract void setupSketch();

    /**
     * This method handles the creation of the renderer(s)
     */
    public abstract void createRenderer();

    /**
     *
     */
    public final void execute() {

        String nombre = this.getClass().getName();
        logger.info("Running sketch {} ", nombre);
        setupSketch();
        createRenderer();
        JMathAnimConfig.getConfig().setRenderer(renderer);
        exitCode=0;
        //In the global variable store Scene, Renderer and main Camera
        conf.setScene(this);
        try {
            runSketch();
        } catch (Exception ex) {
            exitCode=1;
            logger.error(ex.toString());
            ex.printStackTrace();
            if (renderer instanceof Java2DRenderer) {
                Java2DRenderer ren = (Java2DRenderer) renderer;
                if (ren.getPreviewWindow() != null) {
                    ren.getPreviewWindow().setVisible(true);
                }
            }
        } finally {
            //Try anyway to finish the rendering
            renderer.finish(frameCount);//Finish rendering jobs
        }
        if (exitCode!=0)
        {
            logger.error("An error ocurred. Check the logs.");
        }
        System.exit(0);
    }

    /**
     * Returns the list of objects to be drawn
     *
     * @return An ArrayList of MathObject
     */
    public ArrayList<MathObject> getObjects() {
        return objects;
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

    public abstract void runSketch() throws Exception;

    public synchronized final void add(ArrayList<MathObject> objs) {
        for (MathObject obj : objs) {
            add(obj);
        }
    }

    public synchronized final void registerObjectToBeUpdated(Updateable... objs) {
        for (Updateable obj : objs) {
            if (!objectsToBeUpdated.contains(obj)) {
                objectsToBeUpdated.add(obj);
            }
        }
    }

    public synchronized final void unregisterObjectToBeUpdated(Updateable obj) {
        if (obj instanceof Updateable) {
            objectsToBeUpdated.remove((Updateable) obj);
        }
    }

    public synchronized final void add(MathObject... objs) {
        for (MathObject obj : objs) {
            if (!objects.contains(obj)) {
                objects.add(obj);
                obj.addScene(this);
                //Check if this object is Updateable.
                //This interface is present in every MathObject which needs to
                //be updated every frame
                registerObjectToBeUpdated(obj);
                obj.registerChildrenToBeUpdated(this);
            }
        }
    }

    public synchronized final MathObject[] remove(MathObject... objs) {
        for (MathObject obj : objs) {
            objects.remove(obj);
            obj.removeScene(this);
            unregisterObjectToBeUpdated(obj);
            obj.unregisterChildrenToBeUpdated(this);//TODO: Really unregister children??
        }
        return objs;
    }

    /**
     * Call the draw method in all mathobjects
     */
    protected final void doDraws() {

        //For the array of objects to be updated (not necessarily drawn), I sort them by the updatelevel variable
        //updatelevel 0 gets updated first.
        //Objects with updatelevel n depend directly from those with level n-1
        objectsToBeUpdated.sort((Updateable o1, Updateable o2) -> o1.getUpdateLevel() - o2.getUpdateLevel());
        for (Updateable obj : objectsToBeUpdated) {
            obj.update();
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
     * Advance one frame, making all necessary drawings
     */
    public final void advanceFrame() {
        renderer.clear();
        doDraws();
        frameCount++;
        saveMPFrame();
        previousNanoTime = nanoTime;
        nanoTime = System.nanoTime();

    }

    private void saveMPFrame() {

        renderer.saveFrame(frameCount);
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
                    finished = finished & anim.processAnimation(fps);
                }
            }

            advanceFrame();
        }
    }

    public void waitSeconds(double time) {
        int numFrames = (int) (time * fps);
        for (int n = 0; n < numFrames; n++) {
            advanceFrame();
        }

    }

    public Camera getCamera() {
        return renderer.getCamera();
    }

}
