/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Animations;

import com.jmathanim.mathobjects.MathObject;

/**
 * This abstract class stores an Animation Animations are always played using a
 * parameter t from 0 to 1. Each animation has its own t parameter, which steps
 * depending on fps
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class Animation {

    private double t, dt;
    protected final MathObject mobj;
    protected double runTime;
    protected double fps;
    private int numFrames; //Number of frames of animation
    private int frame;
    private boolean shouldSetFPSFirst;

    public Animation(MathObject mobj) {
        this(mobj, 1);
    }

    public Animation(MathObject mobj, double runTime) {
        this.shouldSetFPSFirst = true;
        this.mobj = mobj;
        this.runTime = runTime;
    }

    public Animation(MathObject mobj, int runTime) {
        this(mobj, (double) runTime);
    }

    public double getFps() {
        return fps;
    }

    public void setFps(double fps) {
        this.fps = fps;
        numFrames = (int) (runTime * fps) + 3;//TODO: Check this!
        dt = 1.d / (runTime * fps + 3);
        t = 0;
        frame = 0;
        shouldSetFPSFirst = false;
    }

    /**
     * Process one frame of current animation If calling when finished, does
     * nothing
     *
     * @return True if animation has finished
     */
    public boolean processAnimation(double fps) {
        if (shouldSetFPSFirst) {
            setFps(fps);
        }
        boolean resul = false;
//        if (frame < numFrames || t < 1 + dt) {
        if (t<1){
            this.doAnim(t);
            t += dt;
            if (t > 1) {
                t = 1;
            }
            frame++;
            resul = false;
        } else {
            resul = true;
        }

        return resul;
    }

    /**
     * Do animation
     *
     * @param t double betwenn 0 and 1 0=start, 1=end
     */
    abstract public void doAnim(double t);

    public double getT() {
        return t;
    }
}
