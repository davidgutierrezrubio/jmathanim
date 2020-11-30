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

import com.jmathanim.Animations.Strategies.Transform.Optimizers.OptimizePathsStrategy;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import java.util.function.DoubleUnaryOperator;

/**
 * This abstract class stores an Animation Animations are always played using a
 * parameter t from 0 to 1. Each animation has its own t parameter, which steps
 * depending on fps
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class Animation {

    /**
     * Default run time for animations, 1 second
     */
    public static final double DEFAULT_TIME = 1;
    private double t, dt;
//    public final MathObject mobj;
    /**
     * Time span of the animation, in seconds
     */
    protected double runTime;
    /**
     * Frames per second. This will be needed to compute time step for each
     * frame.
     */
    protected double fps;
//    private int numFrames; //Number of frames of animation
//    private int frame;
    private boolean isInitialized = false;
    private boolean isEnded = false;
    /**
     * Scene where this animation belongs
     */
    protected JMathAnimScene scene;
    /**
     * Optimization strategy to apply before performing the animation, if any
     */
    protected OptimizePathsStrategy optimizeStrategy = null;
    /**
     * Lambda smooth function, ideally a growing function that maps 0 into 0 and
     * 1 into 1
     */
    public DoubleUnaryOperator lambda;

    private boolean useObjectState;
    
    private boolean shouldAddObjectsToScene;

    /**
     * Returns true if the animation has ended
     *
     * @return True if ended, false otherwise
     */
    public boolean isEnded() {
        return isEnded;
    }

    public void setEnded(boolean value) {
        isEnded = value;
    }

    /**
     * Return the use object state flag. This flag controls whether the
     * animation should restore the initial state of the object prior to do each
     * frame of the animation.
     *
     * @return True if restore state, false otherwise
     */
    public boolean isUseObjectState() {
        return useObjectState;
    }

    /**
     * Sets the use object state flag.This flag controls whether the animation
     * should restore the initial state of the object prior to do each frame of
     * the animation.By default is true,but it may be necessary to set to false
     * when combining 2 animations. For example a shift and a rotation, should
     * deactivate the flag in the second.
     *
     * @param <T> Animation subclass that calls this method
     * @param shouldSaveState True if restore state, false otherwise
     * @return This object
     */
    public <T extends Animation> T setUseObjectState(boolean shouldSaveState) {
        this.useObjectState = shouldSaveState;
        return (T) this;
    }

       public <T extends Animation> T setAddObjectsToScene(boolean addToScene) {
        this.shouldAddObjectsToScene = addToScene;
        return (T) this;
    }
    
    
    /**
     * Creates an empty animation, with the default run time. This constructor
     * should be called only from implementing subclasses.
     */
    public Animation() {
        this(DEFAULT_TIME);

    }

    /**
     * Creates an empty animation, with specified run time.This constructor
     * should be called only from implementing subclasses.
     *
     * @param runTime Duration of animation, in seconds
     */
    public Animation(double runTime) {
        this.runTime = runTime;
        this.useObjectState = true;
        this.shouldAddObjectsToScene = true;
//        scene = JMathAnimConfig.getConfig().getScene();
        lambda = (x) -> lambdaDefault(x, .9d);
    }

    /**
     * Sets the frames per second. This value is automatically set by the
     * initialize method
     *
     * @param fps Frames per second
     */
    protected void setFps(double fps) {
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
            JMathAnimScene.logger.error("Animation " + this.getClass().getCanonicalName() + " not initialized. Animation will not be done");
            return true;
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
//            this.finishAnimation();
            isEnded = true;
        }
        return resul;
    }

    /**
     * Initialize animation.This method should be called immediately before
     * playing
     *
     * @param scene Scene where the animation is invoked from.
     */
    public void initialize(JMathAnimScene scene) {
        this.scene = scene;
        setFps(scene.getConfig().fps);
        this.isInitialized = true;
    }

    /**
     * Executes one frame of the animation, given by the time t, from 0 to 1
     *
     * @param t double between 0 and 1 0=start, 1=end. This value is passed as
     * needed by some special animations. The lambda function should be used to
     * smooth animation.
     */
    abstract public void doAnim(double t);

    /**
     * Finish animation, deleting auxiliary objects or anything necessary.
     */
    abstract public void finishAnimation();

    private double hh(double t) {
        return (t == 0 ? 0 : Math.exp(-1 / t));
    }

    //Smooth function from https://math.stackexchange.com/questions/328868/how-to-build-a-smooth-transition-function-explicitly
    //TODO: Adapt this to use Cubic Bezier splines
    /**
     * Default lambda function
     *
     * @param t Parameter to compute value, from 0 to 1
     * @param smoothness 1 full smoothnes, 0 makes the function identity
     * @return
     */
    protected double lambdaDefault(double t, double smoothness) {
        double h = hh(t);
        double h2 = hh(1 - t);
        return (1 - smoothness) * t + smoothness * h / (h + h2);

//        return t * t * (3 - 2 * t);
//        return t;
    }

    /**
     * Sets the optimization strategy. If null, the animation will try to find
     * the most suitable optimization.
     *
     * @param strategy Optimization strategy
     */
    public <T extends Animation> T setOptimizationStrategy(OptimizePathsStrategy strategy) {
        optimizeStrategy = strategy;
        return (T) this;
    }

    /**
     * Returns the smooth function
     *
     * @return A lambda operator with the smooth function
     */
    public DoubleUnaryOperator getLambda() {
        return lambda;
    }

    /**
     * Sets the smooth function
     *
     * @param <T> Animation subclass
     * @param lambda A lambda operator with the new smooth function
     * @return The animation
     */
    public <T extends Animation> T setLambda(DoubleUnaryOperator lambda) {
        this.lambda = lambda;
        return (T) this;
    }

    /**
     * Save state of all given mathobjects. If the useObjectState flag is set to
     * false, this method does nothing
     *
     * @param mathObjects MathObjects to save state (varargs)
     */
    protected void saveStates(MathObject... mathObjects) {
        if (this.isUseObjectState()) {
            for (MathObject obj : mathObjects) {
                obj.saveState();
            }
        }
    }

    /**
     * Restore state of all given mathobjects. If the useObjectState flag is set
     * to false, this method does nothing
     *
     * @param mathObjects MathObjects to restore state (varargs)
     */
    protected void restoreStates(MathObject... mathObjects) {
        if (this.isUseObjectState()) {
            for (MathObject obj : mathObjects) {
                obj.restoreState();
            }
        }
    }
    protected void addObjectsToscene(MathObject... mathObjects){
       if (this.shouldAddObjectsToScene) {
            scene.add(mathObjects);
        }  
    }
    
}
