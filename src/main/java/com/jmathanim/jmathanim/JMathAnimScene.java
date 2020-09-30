/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.jmathanim;

import com.jmathanim.Animations.Animation;
import com.jmathanim.Animations.PlayAnim;
import com.jmathanim.Cameras.Camera;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.updateableObjects.Updateable;
import java.util.ArrayList;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class JMathAnimScene {

    String[] DEFAULT_CONFIG = {
        "WIDTH", "800",
        "HEIGHT", "600",
        "FPS", "60"
    };
    int contador = 0;
    int x;
    final ArrayList<MathObject> objects;
    final ArrayList<Updateable> objectsToBeUpdated;
    protected Renderer SCRenderer;
    protected int frameCount;
    protected double fps;
    protected double dt;
    public JMathAnimConfig conf;
    protected final PlayAnim playAnim;
    public long nanoTime;
    public long previousNanoTime;

    public JMathAnimScene() {
        objects = new ArrayList<>(); //TODO: Extends this to include layers
        conf = JMathAnimConfig.getConfig();
        conf.setLowQuality();//by default, set low quality
        objectsToBeUpdated = new ArrayList<>();
        playAnim = new PlayAnim(this);
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
        JMathAnim.logger.info("Running sketch: " + nombre);
        setupSketch();
        createRenderer();
        JMathAnimConfig.getConfig().setRenderer(SCRenderer);

        //In the global variable store Scene, Renderer and main Camera
        conf.setScene(this);
        runSketch();
        SCRenderer.finish(frameCount);//Finish rendering jobs

    }

    public abstract void runSketch();

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

    public synchronized final MathObject[] remove(MathObject...objs) {
        for (MathObject obj:objs){
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
        objects.sort((MathObject o1, MathObject o2) -> (o1.getLayer()- o2.getLayer()));
        for (MathObject obj : objects) {
            if (obj.visible) {
                obj.draw(SCRenderer);
            }
        }

    }

    /**
     * Advance one frame, making all necessary drawings
     */
    public final void advanceFrame() {
        SCRenderer.clear();
        doDraws();
        frameCount++;
        saveMPFrame();
        previousNanoTime=nanoTime;
           nanoTime = System.nanoTime();

    }

    private void saveMPFrame() {

        SCRenderer.saveFrame(frameCount);
    }

    /**
     * Overloaded function, to admit a variable number of parameters
     *
     * @param anims Animations to play
     */
    public void play(Animation... anims) {
        ArrayList<Animation> animArray = new ArrayList<>();
        for (Animation anim : anims) {
            animArray.add(anim);
        }
        this.play(animArray);
    }

    /**
     * Play the given animations, generating new frames automatically until all
     * animations have finished.
     *
     * @param anims An ArrayList with Animation objects.
     */
    public void play(ArrayList<Animation> anims) {
        for (Animation anim : anims) {
            anim.addObjectsToScene(this); //Add main object if it's not already in the scene.
            anim.initialize();//Perform needed steps immediately before playing
        }

        boolean finished = false;
        while (!finished) {
            finished = true;
            for (Animation anim : anims) {
                finished = finished & anim.processAnimation(fps);
//                Java2DRenderer r = (Java2DRenderer) SCRenderer;
//            r.debugText("t: " + anims.get(0).mobj, 10, 10);
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
        return SCRenderer.getCamera();
    }

}
