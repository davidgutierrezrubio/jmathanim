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
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public abstract class Animation {

    private double t, dt;
    protected final MathObject mobj;
    protected double runTime;
    protected double fps;

    public Animation(MathObject mobj) {
        this(mobj, 1);
    }

    public Animation(MathObject mobj, double runTime) {
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
        double numFrames = runTime * fps;
        dt = 1 / numFrames;
        t = 0;
    }

    /**
     * Process one frame of current animation If calling when finished, does
     * nothing
     *
     * @return True if animation has finished
     */
    public boolean processAnimation() {
        boolean resul=false;
        if (t < 1) {
            resul = false;
        }

        if (t >= 1) {
            t = 1;
            resul = true;
        }

        this.doAnim(t);
        t += dt;
        return resul;
    }

    /**
     * Do animation
     *
     * @param t double betwenn 0 and 1 0=start, 1=end
     */
    abstract public void doAnim(double t);
}
