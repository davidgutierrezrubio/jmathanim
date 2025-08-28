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

import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.UsefulLambdas;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.jmathanim.LogUtils;
import com.jmathanim.mathobjects.Copyable;
import com.jmathanim.mathobjects.MathObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.DoubleUnaryOperator;

/**
 * This abstract class stores an Animation, which can be played with the playAnim method
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class Animation {

    protected final HashSet<MathObject<?>> removeThisAtTheEnd;
    protected final HashSet<MathObject<?>> addThisAtTheEnd;
    private final JMathAnimConfig config;
    private final HashMap<Copyable<?>, Copyable<?>> backups;
    protected double lastTComputed;
    protected double t, dt;
    protected boolean printProgressBar;
    /**
     * Time span of the animation, in seconds
     */
    protected double runTime;
//    public final MathObject mobj;
    /**
     * Frames per second. This will be needed to compute time step for each frame.
     */
    protected double fps;
    /**
     * Scene where this animation belongs
     */
    protected JMathAnimScene scene;
//    private int numFrames; //Number of frames of animation
//    private int frame;
//    private boolean isInitialized = false;
//    private boolean isEnded = false;
    /**
     * Lambda smooth function, ideally a growing function that maps 0 into 0 and 1 into 1
     */
    protected DoubleUnaryOperator lambda;
    protected Boolean useObjectState;
    protected Boolean shouldAddObjectsToScene;
    protected Boolean shouldInterpolateStyles;
    protected Runnable finishRunnable;
    protected Runnable initRunnable;
    protected double allocateStart;
    protected double allocateEnd;
    private String debugName;
    private boolean shouldResetAtFinish;
    private Status status;

    /**
     * Creates an empty animation, with specified run time.This constructor should be called only from implementing
     * subclasses.
     *
     * @param runTime Duration of animation, in seconds
     */
    protected Animation(double runTime) {
        this.debugName = "";
        this.status = Status.NOT_INITIALIZED;
        this.runTime = runTime;
        this.useObjectState = true;
        this.shouldAddObjectsToScene = true;
        this.shouldResetAtFinish = true;
        this.shouldInterpolateStyles = true;
        lambda = JMathAnimConfig.getConfig().getDefaultLambda();
        backups = new HashMap<>();
        addThisAtTheEnd = new HashSet<>();
        removeThisAtTheEnd = new HashSet<>();
        allocateStart = 0d;
        allocateEnd = 1d;
        config = JMathAnimConfig.getConfig();
        printProgressBar = true;
    }

    /**
     * Returns the interpolate styles flag. This flag controls whether the animation should perform any changes in the
     * styles of the MathObject animated classes.
     *
     * @return True if should animate styles, false otherwise.
     */
    public Boolean isShouldInterpolateStyles() {
        return shouldInterpolateStyles;
    }

    /**
     * Sets the interpolate styles flag.This flag controls whether the animation should perform any changes in the
     * styles of the MathObject animated classes.
     *
     * @param interpolateStyles True if should animate styles, false otherwise.
     */
    public <T extends Animation> T setShouldInterpolateStyles(boolean interpolateStyles) {
        this.shouldInterpolateStyles = interpolateStyles;
        return (T) this;
    }

    /**
     * Returns the use object state flag. This flag controls whether the animation should restore the initial state of
     * the object prior to do each frame of the animation.
     *
     * @return True if restore state, false otherwise
     */
    public Boolean isUseObjectState() {
        return useObjectState;
    }

    /**
     * Sets the use object state flag. This flag controls whether the animation should restore the initial state of the
     * object prior to do each frame of the animation.By default is true,but it may be necessary to set to false when
     * combining 2 animations. For example a shift and a rotation, should deactivate the flag in the second.
     *
     * @param <T>             Animation subclass that calls this method
     * @param shouldSaveState True if restore state, false otherwise
     * @return This object
     */
    public <T extends Animation> T setUseObjectState(boolean shouldSaveState) {
        this.useObjectState = shouldSaveState;
        return (T) this;
    }

    /**
     * Activates or deactivates whether this animation should automatically add needed objects to the scene
     *
     * @param <T>        Animation subclass that calls this method
     * @param addToScene If true, objects will be added (to or removed from) the scene as needed.
     * @return This object
     */
    public <T extends Animation> T setAddObjectsToScene(boolean addToScene) {
        this.shouldAddObjectsToScene = addToScene;
        return (T) this;
    }

    /**
     * Sets the frames per second. This value is automatically set by the initialize method
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
     * Creates an empty animation, with the default run time. This constructor
     * should be called only from implementing subclasses.
     */
//    public Animation() {
//        this(DEFAULT_TIME);
//    }

    /**
     * Process one frame of current animation If calling when finished, does nothing
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
        if (status == Status.INITIALIZED) {
            t += dt;
        }
        if (t > 1) t=1;
        if (t <0) t=0;
        boolean resul;
        if (t <= 1 && t >= 0) {
            this.doAnim(t);
            if (printProgressBar && config.isPrintProgressBar()) LogUtils.printProgressBar(t);
            resul = (t==1);
        } else {
            resul = true;
        }
        t += dt;
        //If t is closer to 1 than dt, make it 1
        if ((t < 1) && (1 - t < dt)) {
            t = 1;
        }
        //if t>1, do it 1
        if (t>1) t=1;
        return resul;
    }

    /**
     * Initialize animation.This method should be called immediately before playing
     *
     * @param scene Scene where the animation is invoked from.
     * @return True if animation was successfully initializated
     */
    public final boolean initialize(JMathAnimScene scene) {
        this.scene = scene;
        if (status == Status.NOT_INITIALIZED) {
            if (initRunnable != null) {
                initRunnable.run();
            }
            if (doInitialization()) {//If initialization returned sucess...
                setFps(scene.getConfig().fps);
                status = Status.INITIALIZED;
                return true;
            } else {
                JMathAnimScene.logger.error("Error initializating animation " + getDebugName());
                return false;
            }
        }
        return false;
    }

    /**
     * Do specific initialition methods. This method is called from initialize if animation is not previously
     * initalizated
     *
     * @return True if no problems were found initializating
     */
    protected boolean doInitialization() {
        return true;
    }

    /**
     * Executes one frame of the animation, given by the time t, from 0 to 1
     *
     * @param t double between 0 and 1 0=start, 1=end. This value is passed as needed by some special animations. The
     *          lambda function should be used to smooth animation.
     */
    public void doAnim(double t) {
        this.t = t;
        if (status == Status.INITIALIZED) {
            status = Status.RUNNING;//First time we use doAnim, let's prepare before!
            prepareForAnim(t);
        }
    }

    /**
     * Finish animation, deleting auxiliary objects or anything necessary.
     */
    public void finishAnimation() {
        status = Status.FINISHED;
        removeObjectsFromScene(removeThisAtTheEnd);
        addObjectsToscene(addThisAtTheEnd);
        cleanAnimationAt(t);
        if (finishRunnable != null) {
            finishRunnable.run();
        }
    }

    /**
     * Perform necessary cleaning operations after stopping the animation. Should perform adequate cleaning operations
     * depending on the time the animation is stopped.
     *
     * @param t Time at which the animation is stopped.
     */
    public abstract void cleanAnimationAt(double t);

    /**
     * Perform needed operations right before the animation starts. Usually adding or removing necessary objects
     *
     * @param t Time at which the animation starts
     */
    public abstract void prepareForAnim(double t);

    public DoubleUnaryOperator getTotalLambda() {
        return lambda.compose(UsefulLambdas.allocateTo(allocateStart, allocateEnd));
    }
    protected double allocateT(double t) {
        double rt = (t < 0 ? 0 : t);
        rt = (rt > 1 ? 1 : rt);
        return UsefulLambdas.allocateTo(allocateStart, allocateEnd).applyAsDouble(rt);
    }

    protected double getLT(double t) {
        t = (t < 0 ? 0 : t);
        t = (t > 1 ? 1 : t);

        return getTotalLambda().applyAsDouble(t);
    }

    public Animation setAllocationParameters(double start, double end) {
        this.allocateStart = start;
        this.allocateEnd = end;
        return this;
    }

    /**
     * Sets the lambda function to control the time behaviour of the animation. A proper lambda is a function with range
     * from 0 to 1 and dominion 0 to 1.
     *
     * @param <T>    Animation subclass
     * @param lambda A lambda operator with the new time function
     * @return The animation
     */
    public <T extends Animation> T setLambda(DoubleUnaryOperator lambda) {
        this.lambda = lambda;
        return (T) this;
    }

    /**
     * Save state of all given mathobjects.If the useObjectState flag is set to false, this method does nothing
     *
     * @param copyables MathObjects to save state (varargs)
     */
    protected void saveStates(Copyable<?>... copyables) {
        if (this.isUseObjectState()) {
            backups.clear();
            for (Copyable<?> obj : copyables) {
//                obj.saveState();
                Object aa = obj.copy();
                backups.put(obj, obj.copy());
            }
        }
    }

    /**
     * Restore state of all given mathobjects.If the useObjectState flag is set to false, this method does nothing
     *
     * @param copyables MathObjects to restore state (varargs)
     */
    protected void restoreStates(Copyable<?>... copyables) {
        if (this.isUseObjectState()) {
            for (Copyable<?> obj : copyables) {
                obj.copyStateFrom(backups.get(obj));
            }
        }
    }

    /**
     * Add the specified objects to the scene. Adding objects to the scene should be done through this method.
     *
     * @param mathObjects Objects to add (varargs)
     */
    protected void addObjectsToscene(MathObject<?>... mathObjects) {
        if (this.shouldAddObjectsToScene) {
            scene.add(mathObjects);
        }
    }

    /**
     * remove the specified objects from the scene. Removing objects from the scene should be done through this method.
     *
     * @param mathObjects Objects to add (varargs)
     */
    protected void removeObjectsFromScene(MathObject<?>... mathObjects) {
        if (this.shouldAddObjectsToScene) {
            scene.remove(mathObjects);
        }
    }

    protected void removeObjectsFromScene(HashSet<MathObject<?>> objectsToRemove) {
        removeObjectsFromScene(objectsToRemove.toArray(new MathObject[0]));
    }

    protected void addObjectsToscene(HashSet<MathObject<?>> objectsToAdd) {
        addObjectsToscene(objectsToAdd.toArray(new MathObject[0]));
    }

    /**
     * Whether the objects should be added to the scene or not using this animation
     *
     * @return True if objects are automatically added to the scene when initialized, false otherwise
     */
    public Boolean isShouldAddObjectsToScene() {
        return shouldAddObjectsToScene;
    }

    /**
     * Returns the current status of the animation
     *
     * @return A value of the enum Status, NOT_INITIALIZED, INITIALIZED, RUNNING, FINISHED
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Sets the current status of the animation
     *
     * @param status A value of the enum Status: NOT_INITIALIZED, INITIALIZED, RUNNING, FINISHED
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Gets the current animation time
     *
     * @return A value from 0 to 1
     */
    public double getT() {
        return t;
    }

    /**
     * Sets the time parameter. A value or 0 means beginning of the animation and 1 the ending.
     *
     * @param time A value from 0 to 1
     */
    public void setT(double time) {
        this.t = time;
    }

    /**
     * Copy basic parameters from this animation to another one. This method is used mainly when an Animation subclass
     * contains another animation. Only copy non-null values for parameters. Some Animation subclasses, like
     * AnimationGroup, can define null values for this parameter so they are not propagated to stored animations.
     *
     * @param anim Animation to copy parameters
     */
    protected void copyAnimationParametersTo(Animation anim) {
        if (this.lambda != null) {
            anim.setLambda(lambda);
        }

        if (null != this.isShouldAddObjectsToScene()) {
            anim.setAddObjectsToScene(this.isShouldAddObjectsToScene());
        }
        if (null != this.isShouldInterpolateStyles()) {
            anim.setShouldInterpolateStyles(this.isShouldInterpolateStyles());
        }

        if (null != this.isUseObjectState()) {
            anim.setUseObjectState(this.isUseObjectState());
        }
        if (null != this.isShouldInterpolateStyles()) {
            anim.setShouldInterpolateStyles(this.isShouldInterpolateStyles());
        }

        anim.allocateEnd = this.allocateEnd;
        anim.allocateStart = this.allocateStart;
    }

    /**
     * . Returns the debug message. Used to show info when running
     *
     * @return A String with the debug message
     */
    public String getDebugName() {
        return debugName;
    }

    /**
     * Sets the debug message. This message will be logged at INFO level when animation starts executing
     *
     * @param <T>       Calling class
     * @param debugName Debug message
     */
    public final <T extends Animation> T setDebugName(String debugName) {
        this.debugName = debugName;
        return (T) this;
    }

    /**
     * Returns the current runtime
     *
     * @return The runtime
     */
    public double getRunTime() {
        return runTime;
    }

    /**
     * Sets the runtime animation. This method should be called before initializing the animation.
     *
     * @param <T>     Calling class
     * @param runTime The new runtime
     * @return This object
     */
    public <T extends Animation> T setRunTime(double runTime) {
        this.runTime = runTime;
        return (T) this;
    }

    @Override
    public String toString() {
        return "Animation " + debugName;
    }

    /**
     * Gets the intermediate object(s) used by the animation. For simple animations like shift or rotate this is the
     * original object, but more complex animations like showcreation or transform need to create an auxiliar
     * intermediate object. If the animation is stopped at a time between 0 and 1, this is the object that should be in
     * the scene.
     *
     * @return The intermediate object
     */
    public abstract <T extends MathObject<?>> T getIntermediateObject();

    /**
     * Resets the animation so it can be reused with different initialization parameters.
     */
    public void reset() {
        status = Status.NOT_INITIALIZED;
    }

    public boolean isShouldResetAtFinish() {
        return shouldResetAtFinish;
    }

    public <T extends Animation> T setShouldResetAtFinish(boolean shouldResetAtFinish) {
        this.shouldResetAtFinish = shouldResetAtFinish;
        return (T) this;
    }

    /**
     * Filter MathObjects from an array
     * @param objects Objects to filter
     * @return An array of MathObjects
     */
    protected MathObject<?>[] filterMathObjects(Object[] objects) {
        return Arrays.stream(objects)
                .filter(a -> a instanceof MathObject<?>)
                .map(a -> (MathObject<?>) a)
                .toArray(MathObject<?>[]::new);
    }

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
         * Animation is finished. Subsequent calls to processAnimation() makes no effect
         */
        FINISHED
    }


}
