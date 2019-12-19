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
import com.jmathanim.mathobjects.MathObject;
import java.util.ArrayList;
import java.util.Properties;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public abstract class JMathAnimScene {

    String[] DEFAULT_CONFIG = {
        "WIDTH", "800",
        "HEIGHT", "600",
        "FPS", "25"
    };
    int contador = 0;
    int x;
    protected final Properties cnf;
    ArrayList<MathObject> objects;
    protected Renderer SCRenderer;
    protected Camera SCCamera;
    protected int frameCount;
    protected double fps;

    public JMathAnimScene() {
        this(null);
    }

    public JMathAnimScene(Properties configParam) {
        cnf = new Properties();
        objects = new ArrayList<>();
        ConfigUtils.digest_config(cnf, DEFAULT_CONFIG, configParam);
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

    public final MathObject add(MathObject obj) {
        objects.add(obj);
        return obj;
    }

    public final MathObject remove(MathObject obj) {
        objects.remove(obj);
        return obj;
    }

    public final void doDraws() {
        for (MathObject obj : objects) {
            obj.draw(SCRenderer);
        }

    }

    public final void advanceFrame() {
        frameCount++;
        saveMPFrame();
        SCRenderer.clear();
    }

    private void saveMPFrame() {

        SCRenderer.saveFrame(frameCount);
    }

    public void play(Animation anim) {
        Animation[] anims = {anim};
        this.play(anims);
    }

    public void play(Animation anim1, Animation anim2) {
        Animation[] anims = {anim1, anim2};
        this.play(anims);
    }

    public void play(Animation anim1, Animation anim2, Animation anim3) {
        Animation[] anims = {anim1, anim2, anim3};
        this.play(anims);
    }

    public void play(Animation[] anims) {
        for (Animation anim : anims) {
            anim.setFps(fps);
        }
        boolean finished = false;
        while (!finished) {
            finished=true;
            for (Animation anim : anims) {
                finished=finished & anim.processAnimation();
            }
            doDraws();
            advanceFrame();
        }
    }

    public void waitSeconds(double time) {
        int numFrames=(int) (time*fps);
        for (int n = 0; n < numFrames; n++) {
            doDraws();
            advanceFrame();
        }

    }

}
