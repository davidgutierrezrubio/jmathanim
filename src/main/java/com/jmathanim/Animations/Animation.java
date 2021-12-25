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
import com.jmathanim.Utils.UsefulLambdas;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.DoubleUnaryOperator;

/**
 * This abstract class stores an Animation, which can be played with the
 * playAnim method
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class Animation {

    /**
     * Animation status
     */
    public enum Status {
        /**
         * Animation is not initialized yet
         */
        NOT_INITIALIZED,
        /**
         * Animation initialized, ready to be played
         */
        INITIALIZED,
        /**
         * Animation is currently being played
         */
        RUNNING,
        /**
         * Animation is finished. Subsequent calls to processAnimation() makes
         * no effect
         */
        FINISHED
    }

    private String debugName;

    private Status status;
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
//    private boolean isInitialized = false;
//    private boolean isEnded = false;
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
    protected DoubleUnaryOperator lambda;

    protected Boolean useObjectState;

    protected Boolean shouldAddObjectsToScene;
    protected Boolean shouldInterpolateStyles;
    private final HashMap<MathObject, MathObject> backups;

    /**
     * Creates an empty animation, with specified run time.This constructor
     * should be called only from implementing subclasses.
     *
     * @param runTime Duration of animation, in seconds
     */
    protected Animation(double runTime) {
        this.debugName = "";
        this.status = Status.NOT_INITIALIZED;
        this.runTime = runTime;
        this.useObjectState = true;
        this.shouldAddObjectsToScene = true;
        this.shouldInterpolateStyles = true;
        lambda = UsefulLambdas.smooth(.9d);
        backups = new HashMap<>();
    }

    /**
     * Returns the interpolate styles flag. This flag controls whether the
     * animation should perform any changes in the styles of the MathObject
     * animated classes.
     *
     * @return True if should animate styles, false otherwise.
     */
    public Boolean isShouldInterpolateStyles() {
        return shouldInterpolateStyles;
    }

    /**
     * Sets the interpolate styles flag.This flag controls whether the animation
     * should perform any changes in the styles of the MathObject animated
     * classes.
     *
     * @param interpolateStyles True if should animate styles, false otherwise.
     */
    public <T extends Animation> T setShouldInterpolateStyles(boolean interpolateStyles) {
        this.shouldInterpolateStyles = interpolateStyles;
        return (T) this;
    }

    /**
     * Returns the use object state flag. This flag controls whether the
     * animation should restore the initial state of the object prior to do each
     * frame of the animation.
     *
     * @return True if restore state, false otherwise
     */
    public Boolean isUseObjectState() {
        return useObjectState;
    }

    /**
     * Sets the use object state flag. This flag controls whether the animation
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

    /**
     * Activates or deactivates whether this animation should automatically add
     * needed objects to the scene
     *
     * @param <T> Animation subclass that calls this method
     * @param addToScene If true, objects will be added (to or removed from) the
     * scene as needed.
     * @return This object
     */
    public <T extends Animation> T setAddObjectsToScene(boolean addToScene) {
        this.shouldAddObjectsToScene = addToScene;
        return (T) this;
    }

    /**
     * Creates an empty animation, with the default run time. This constructor
     * should be called only from implementing subclasses.
     */
//    public Animation() {
//        this(DEFAULT_TIME);
//    }
    /**
     * Sets the frames per second. This value is automatically set by the
     * initialize method
     *
     * @param fps Frames per second
     */
    protected void setFps(double fps) {
        this.fps = fps;
//        dt = 1.d / (runTime * fps + 3);
        dt = 1. / (runTime * fps);
        t = 0;
    }

    /**
     * Process one frame of current animation If calling when finished, does
     * nothing
     *
     * @return True if animation has finished
     */
    public boolean processAnimation() {
        if (status == Status.FINISHED) {
            return true;
        }
        if (status == Status.NOT_INITIALIZED) {
            JMathAnimScene.logger.error(
                    "Animation " + this.getClass().getCanonicalName() + " not initialized. Animation will not be done");
            return true;
        }
        boolean resul;
        if (t <= 1 && t >= 0) {
            this.doAnim(t);

            resul = false;
        } else {
            resul = true;
        }
        t += dt;
        //If t is closer to 1 than dt, make it 1
        if ((t<1)&&(1 - t < dt)) {
            t = 1;
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
        status = Status.INITIALIZED;
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
    public void finishAnimation() {
        status = Status.FINISHED;
    }

    // Smooth function from
    // https://math.stackexchange.com/questions/328868/how-to-build-a-smooth-transition-function-explicitly
    // TODO: Adapt this to use Cubic Bezier splines
    /**
     * Default lambda function
     *
     * @param t Parameter to compute value, from 0 to 1
     * @param smoothness 1 full smoothnes, 0 makes the function identity
     * @return
     */
//	public static double lambdaDefault(double t, double smoothness) {
//		double h = smoothFunctionAux(t);
//		double h2 = smoothFunctionAux(1 - t);
//		return (1 - smoothness) * t + smoothness * h / (h + h2);
//
////        return t * t * (3 - 2 * t);
////        return t;
//	}
    /**
     * Sets the optimization strategy.If null, the animation will try to find
     * the most suitable optimization.
     *
     * @param <T> The calling subclass
     * @param strategy Optimization strategy
     * @return This object
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
     * Save state of all given mathobjects.If the useObjectState flag is set to
     * false, this method does nothing
     *
     * @param mathObjects MathObjects to save state (varargs)
     */
    protected void saveStates(MathObject... mathObjects) {
        if (this.isUseObjectState()) {
            backups.clear();
            for (MathObject obj : mathObjects) {
//                obj.saveState();
                backups.put(obj, obj.copy());
            }
        }
    }

    /**
     * Restore state of all given mathobjects.If the useObjectState flag is set
     * to false, this method does nothing
     *
     * @param mathObjects MathObjects to restore state (varargs)
     */
    protected void restoreStates(MathObject... mathObjects) {
        if (this.isUseObjectState()) {
            int n = 0;
            for (MathObject obj : mathObjects) {
//                obj.restoreState();
                obj.copyStateFrom(backups.get(obj));
                n++;
            }
        }
    }

    /**
     * Add the specified objects to the scene. Adding objects to the scene
     * should be done through this method.
     *
     * @param mathObjects Objects to add (varargs)
     */
    protected void addObjectsToscene(MathObject... mathObjects) {
        if (this.shouldAddObjectsToScene) {
            scene.add(mathObjects);
        }
    }

    /**
     * remove the specified objects from the scene. Removing objects from the
     * scene should be done through this method.
     *
     * @param mathObjects Objects to add (varargs)
     */
    protected void removeObjectsFromScene(MathObject... mathObjects) {
        if (this.shouldAddObjectsToScene) {
            scene.remove(mathObjects);
        }
    }

    protected void removeObjectsFromScene(ArrayList<MathObject> removeThisAtTheEnd) {
        removeObjectsFromScene(removeThisAtTheEnd.toArray(new MathObject[0]));
    }

    protected void addObjectsToscene(ArrayList<MathObject> addThisAtTheEnd) {
        addObjectsToscene(addThisAtTheEnd.toArray(new MathObject[0]));
    }

    /**
     * Whether the objects should be added to the scene or not using this
     * animation
     *
     * @return True if objects are automatically added to the scene when
     * initialized, false otherwise
     */
    public Boolean isShouldAddObjectsToScene() {
        return shouldAddObjectsToScene;
    }

    /**
     * Returns the current status of the animation
     *
     * @return A value of the enum Status, NOT_INITIALIZED, INITIALIZED,
     * RUNNING, FINISHED
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Sets the current status of the animation
     *
     * @param status A value of the enum Status: NOT_INITIALIZED, INITIALIZED,
     * RUNNING, FINISHED
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Sets the time parameter. A value or 0 means beginning of the animation
     * and 1 the ending.
     *
     * @param time A value from 0 to 1
     */
    public void setT(double time) {
        this.t = time;
    }

    /**
     * Copy basic parameters from this animation to another one. This method is
     * used mainly when an Animation subclass contains another animation. Only
     * copy non-null values for parameters. Some Animation subclasses, like
     * AnimationGroup, can define null values for this parameter so they are not
     * propagated to stored animations.
     *
     * @param anim Animation to copy parameters
     */
    protected void copyAnimationParametersTo(Animation anim) {
        if (this.getLambda() != null) {
            anim.setLambda(this.getLambda());
        }

        if (null != this.isShouldAddObjectsToScene()) {
            anim.setAddObjectsToScene(this.isShouldAddObjectsToScene());
        }
        if (null != this.isShouldInterpolateStyles()) {
            anim.setShouldInterpolateStyles(this.isShouldInterpolateStyles());
        }

        if (null != this.isShouldAddObjectsToScene()) {
            anim.setAddObjectsToScene(this.isShouldAddObjectsToScene());
        }
    }

    /**
     * .
     * Returns the debug message. Used to show info when running
     *
     * @return A String with the debug message
     */
    public String getDebugName() {
        return debugName;
    }

    /**
     * Sets the debug message. This message will be logged at INFO level when
     * animation starts executing
     *
     * @param debugName Debug message
     */
    public final void setDebugName(String debugName) {
        this.debugName = debugName;
    }

    public double getRunTime() {
        return runTime;
    }

    public <T extends Animation> T setRunTime(double runTime) {
        this.runTime = runTime;
        return (T) this;
    }

}
