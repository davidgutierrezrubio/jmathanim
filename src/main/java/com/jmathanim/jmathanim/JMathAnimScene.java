/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.jmathanim;

import com.jmathanim.Animations.Animation;
import com.jmathanim.Cameras.Camera;
import com.jmathanim.Renderers.Java2DRenderer;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.ConfigUtils;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.mathobjects.MathObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Properties;

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

    public void play(Animation... anims) {
        ArrayList<Animation> animArray = new ArrayList<>();
        for (Animation anim : anims) {
            animArray.add(anim);
        }
        this.play(animArray);
    }

    public void play(ArrayList<Animation> anims) {
        for (Animation anim : anims) {
            add(anim.mobj);
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
