/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.jmathanim;

import com.jmathanim.Animations.Animation;
import com.jmathanim.Cameras.Camera;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.mathobjects.MathObject;
import java.util.ArrayList;
import java.util.Comparator;

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
    ArrayList<MathObject> objects;
    protected Renderer SCRenderer;
    protected int frameCount;
    protected double fps;
    protected double dt;
    public JMathAnimConfig conf;

    
    public JMathAnimScene() {
        objects = new ArrayList<>(); //TODO: Extends this to include layers
        conf = new JMathAnimConfig();
        conf.setLowQuality();//by default, set low quality
    }

//    /**
//     *
//     */
//    public final void runSketch() {
//        frames = 0;
//        mainLoop();
//        makeMovie();
//        System.exit(0);
//
//    }
    /**
     * Preparation code for the animation should go here
     */
    public abstract void setupSketch();

    /**
     *
     */
    public final void execute() {
        String nombre = this.getClass().getName();
        System.out.println("Run sketch: " + nombre);
        setupSketch();
        runSketch();
        SCRenderer.finish();//Finish rendering jobs

    }

    public abstract void runSketch();

    public final void add(ArrayList<MathObject> objs) {
        for (MathObject obj : objs) {
            add(obj);
        }
    }

    public final void add(MathObject... objs) {
        for (MathObject obj : objs) {
            if (!objects.contains(obj)) {
                objects.add(obj);
                obj.addScene(this);
            }
        }
    }

    public final MathObject remove(MathObject obj) {
        objects.remove(obj);
        obj.removeScene(this);
        return obj;
    }

    /**
     * Call the draw method in all mathobjects
     */
    protected final void doDraws() {
        objects.sort(new Comparator<MathObject>() {
            @Override
            public int compare(MathObject o1, MathObject o2) {
                return (o1.mp.layer - o2.mp.layer);
            }
        });
        for (MathObject obj : objects) {
            obj.draw(SCRenderer);
        }

    }

    /**
     * Advance one frame, making all necessary drawings
     */
    public final void advanceFrame() {
        doDraws();
        frameCount++;
        saveMPFrame();
        SCRenderer.clear();
    }

    private void saveMPFrame() {

        SCRenderer.saveFrame(frameCount);
    }

    /**
     * Overloaded function, to admit a variable number of parameters
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
     * Play the given animations, generating new frames automatically until
     * all animations have finished. 
     * @param anims An ArrayList with Animation objects.
     */
    public void play(ArrayList<Animation> anims) {
        for (Animation anim : anims) {
            add(anim.mobj); //Add main object if it's not already in the scene.
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
