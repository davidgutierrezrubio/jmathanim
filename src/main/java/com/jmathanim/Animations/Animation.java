/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Animations;

import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.jmathanim.JMathAnimScene;

/**
 * This abstract class stores an Animation Animations are always played using a
 * parameter t from 0 to 1. Each animation has its own t parameter, which steps
 * depending on fps
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class Animation {

    private static final double DEFAULT_TIME = 1;
    private double t, dt;
//    public final MathObject mobj;
    protected double runTime;
    protected double fps;
//    private int numFrames; //Number of frames of animation
//    private int frame;
    private boolean isInitialized = false;
    protected final JMathAnimScene scene;

    public Animation() {
        this(DEFAULT_TIME);
    }

    public Animation(int runTime) {
        this((double) runTime);
    }

    public Animation(double runTime) {
        this.runTime = runTime;
        scene = JMathAnimConfig.getConfig().getScene();
    }

    public double getFps() {
        return fps;
    }

    public void setFps(double fps) {
        this.fps = fps;
//        numFrames = (int) (runTime * fps) + 3;//TODO: Check this!
        dt = 1.d / (runTime * fps + 3);
        t = 0;
//        frame = 0;
    }

    /**
     * Process one frame of current animation If calling when finished, does
     * nothing
     *
     * @param fps
     * @return True if animation has finished
     */
    public boolean processAnimation(double fps) {
        if (!isInitialized) { //If not initalized, do it now
//            initialize();
            isInitialized = true;
            setFps(fps);
        }
        boolean resul;
//        if (frame < numFrames || t < 1 + dt) {
        double lt = lambda(t);
        if (lt < 1 && lt >= 0) {
            this.doAnim((lt));

//            frame++;
            resul = false;
        } else {
            resul = true;
        }
        t += dt;
        if (resul) {
            t = 1;
            this.finishAnimation();
        }
        return resul;
    }

    /**
     * Initialize animation. This method is immediately called before playing
     */
    abstract public void initialize();

    /**
     * Executes one frame of the animation, given by the time t, from 0 to 1
     *
     * @param t double betwenn 0 and 1 0=start, 1=end
     */
    abstract public void doAnim(double t);

    abstract public void finishAnimation();

    public double getT() {
        return t;
    }

    private double hh(double t) {
        return (t == 0 ? 0 : Math.exp(-1 / t));
    }

    //Smooth function from https://math.stackexchange.com/questions/328868/how-to-build-a-smooth-transition-function-explicitly
    //TODO: Adapt this to use Cubic Bezier splines
    private double lambda(double t) {
        double h = hh(t);
        double h2 = hh(1 - t);
        return h / (h + h2);

//        return t * t * (3 - 2 * t);
//        return t;
    }

    abstract public void addObjectsToScene(JMathAnimScene scene);
}
