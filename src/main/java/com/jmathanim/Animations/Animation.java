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
package com.jmathanim.Animations;

import com.jmathanim.Animations.Strategies.Transform.Optimizers.NullOptimizationStrategy;
import com.jmathanim.Animations.Strategies.Transform.Optimizers.OptimizePathsStrategy;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.sun.org.apache.xpath.internal.operations.UnaryOperation;
import java.util.function.DoubleUnaryOperator;

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
    private boolean isEnded = false;
    protected final JMathAnimScene scene;
    protected OptimizePathsStrategy optimizeStrategy = null;
    public DoubleUnaryOperator lambda;

    public boolean isEnded() {
        return isEnded;
    }

    public Animation() {
        this(DEFAULT_TIME);
    }

    public Animation(int runTime) {
        this((double) runTime);
    }

    public Animation(double runTime) {
        this.runTime = runTime;
        scene = JMathAnimConfig.getConfig().getScene();
        lambda = (x) -> lambdaDefault(x,.9d);
    }

    public double getFps() {
        return fps;
    }

    public void setFps(double fps) {
        this.fps = fps;
        dt = 1.d / (runTime * fps + 3);
        t = 0;
    }

    /**
     * Process one frame of current animation If calling when finished, does
     * nothing
     *
     * @return True if animation has finished
     */
    public boolean processAnimation() {
        if (isEnded) {
            return true;
        }
        if (!isInitialized) { //If not initalized, do it now
            isInitialized = true;
            setFps(JMathAnimConfig.getConfig().fps);
        }
        boolean resul;
//        if (frame < numFrames || t < 1 + dt) {
        
        if (t < 1 && t >= 0) {
            this.doAnim(t);

//            frame++;
            resul = false;
        } else {
            resul = true;
        }
        t += dt;
        if (resul) {
            t = 1;
            this.finishAnimation();
            isEnded = true;
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
     * @param t double between 0 and 1 0=start, 1=end. This value is passed as
     * needed by some special animations. The lambda function should be used to
     * smooth animation.
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
    protected double lambdaDefault(double t,double smoothness) {
        double h = hh(t);
        double h2 = hh(1 - t);
        return (1-smoothness)*t+smoothness*h / (h + h2);

//        return t * t * (3 - 2 * t);
//        return t;
    }

    abstract public void addObjectsToScene(JMathAnimScene scene);

    public void setOptimizationStrategy(OptimizePathsStrategy strategy) {
            optimizeStrategy = strategy;
    }

    public DoubleUnaryOperator getLambda() {
        return lambda;
    }

    public void setLambda(DoubleUnaryOperator lambda) {
        this.lambda = lambda;
    }
     public void setOptimizeStrategy(OptimizePathsStrategy optimizeStrategy) {
        this.optimizeStrategy = optimizeStrategy;
    }
}
