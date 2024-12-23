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

import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MathObjectGroup;
import com.jmathanim.mathobjects.Point;
import java.util.HashMap;

/**
 * A generic shift animation. This subclass is instatiated from every
 * implementation of a shift, like align, stackto or moveIn, moveOut animations
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class ShiftAnimation extends AnimationWithEffects {

    private double delayPercentage;

    private final MathObject[] mathObjects;
    private final HashMap<MathObject, Vec> shiftVectors;
    private final HashMap<MathObject, AnimationEffect> effects;
    private final HashMap<MathObject, Double> beginningTimes;
    private final HashMap<MathObject, Double> rotationAngles;
    protected boolean[] shouldBeAdded;

    public ShiftAnimation(double runTime, MathObject[] mathObjects) {
        super(runTime);
        this.mathObjects = mathObjects;
        shiftVectors = new HashMap<>();
        effects = new HashMap<>();
        beginningTimes = new HashMap<>();
        rotationAngles = new HashMap<>();
        delayPercentage = 0;
        for (MathObject obj : mathObjects) {
            effects.put(obj, new AnimationEffect());
            rotationAngles.put(obj, 0d);
        }
    }

    @Override
    public boolean doInitialization() {
        super.doInitialization();
        saveStates(mathObjects);
        shouldBeAdded=new boolean[mathObjects.length];
        for (int i = 0; i < mathObjects.length; i++) {
            //True if object is NOT added to the scene
            shouldBeAdded[i]=!scene.isInScene(mathObjects[i]);
        }
        
        int size = mathObjects.length;
        int k = 0;
        if (size > 1) {// Only works when group has at least 2 members...
            for (MathObject obj : mathObjects) {
                beginningTimes.put(obj, k * (delayPercentage) / (size - 1));
                k++;
            }
        }
        return true;
    }

    /**
     * This function rescales the time parameter so that (0,1) becomes the (a,b)
     * interval
     *
     * @param a left side of new interval
     * @param b right side of new interval
     * @param t parameter to evaluate
     * @return The new parameter rescaled
     */
    public double allocateToNewTime(double a, double b, double t) {
        if (t < a) {
            return 0;
        }
        if (t > b) {
            return 1;
        }
        return (t - a) / (b - a);
    }

    @Override
    public void doAnim(double t) {
         super.doAnim(t);
        int size = mathObjects.length;
        double lt = getLT(t);
        restoreStates(mathObjects);
        double b = (1 - delayPercentage);
        for (MathObject obj : mathObjects) {
            Vec v = shiftVectors.get(obj);// Gets the shift vector for this object
            if ((size > 1) && (delayPercentage > 0)) {
                double a = beginningTimes.get(obj);
                double newT = allocateToNewTime(a, a + b, t);
                lt = getTotalLambda().applyAsDouble(newT);
            }
            obj.shift(v.mult(lt));
            effects.get(obj).applyAnimationEffects(lt, obj);
            if (rotationAngles.get(obj) != 0) {
                obj.rotate(rotationAngles.get(obj) * lt);
            }
        }
    }

    /**
     * Returns the current shift vector for the given object
     *
     * @param obj MathObject to get it shift vector
     * @return The shift vector
     */
    public Vec getShiftVector(MathObject obj) {
        return this.shiftVectors.get(obj);
    }

    /**
     * Sets the shift vector to given object
     *
     * @param <T> Calling subclass
     * @param obj Object to set shift vector
     * @param shiftVector Shift vector
     * @return This object
     */
    public <T extends ShiftAnimation> T setShiftVector(MathObject obj, Vec shiftVector) {
        this.shiftVectors.put(obj, shiftVector);
        if (!effects.containsKey(obj)) {
            effects.put(obj, new AnimationEffect());
        }
        Point c = obj.getCenter();
        effects.get(obj).prepareJumpPath(c, c.copy().shift(shiftVector), obj);

        return (T) this;
    }

    /**
     * Returns the specific jump height for the given object
     *
     * @param obj Mathobject to get the jump height from
     * @return the jump height of the given object. A value or null or zero
     * means no jump at all.
     */
    public double getJumpHeight(MathObject obj) {
        return effects.get(obj).jumpHeight;
    }

    /**
     * Returns the rotation numTurns assigned to the given object
     *
     * @param obj Mathobject to get the rotation numTurns from
     * @return Angle to rotate the object. A value of null or 0 means no effect
     */
    public double getNumTurns(MathObject obj) {
        return effects.get(obj).numTurns;
    }

    /**
     * Returns the amount of scale effect assigned to the given object
     *
     * @param obj Mathobject to get the scale from
     * @return Amount of scale to apply to the object. A value of null or 1
     * means no effect
     */
    public double getScaleEffect(MathObject obj) {
        return effects.get(obj).scaleEffect;
    }

    /**
     * Adds a jump effect to the shift animation.The direction of the jump is
     * the shift vector rotated 90 degrees counterclockwise.
     *
     * @param obj The mathobject to apply the jump
     * @param jumpHeight Height of the jump. Negative heights can be passed.
     * @param jumpType Jump path, a value of enum JumpType
     * @return This object
     */
    public ShiftAnimation addJumpEffect(MathObject obj, double jumpHeight,
            AnimationEffect.JumpType jumpType) {
        if (!effects.containsKey(obj)) {
            effects.put(obj, new AnimationEffect());
        }
        effects.get(obj).addJumpEffect(jumpHeight, jumpType);
        return this;
    }

    /**
     * Adds a parabolical jump effect to the shift animation.The direction of
     * the jump is the shift vector rotated 90 degrees counterclockwise.
     *
     * @param <T> The calling subccass
     * @param obj The mathobject to apply the jump
     * @param jumpHeight Height of the jump. Negative heights can be passed.
     * @return This object
     */
    public <T extends ShiftAnimation> T addJumpEffect(MathObject obj, double jumpHeight) {
        addJumpEffect(obj, jumpHeight, AnimationEffect.JumpType.PARABOLICAL);
        return (T) this;
    }

    /**
     * Sets the jump height for all the objects added to the animation
     *
     * @param jumpHeight Height of the jump. Negative heights can be passed.
     * @return This object
     */
    @Override
    public ShiftAnimation addJumpEffect(double jumpHeight) {
        for (MathObject obj : mathObjects) {
            addJumpEffect(obj, jumpHeight);
        }
        return this;
    }

    /**
     * Sets the jump height for all the objects added to the animation
     *
     * @param jumpHeight Height of the jump. Negative heights can be passed.
     * @param jumpType
     * @return This object
     */
    public ShiftAnimation addJumpEffect(double jumpHeight, AnimationEffect.JumpType jumpType) {
        for (MathObject obj : mathObjects) {
            addJumpEffect(obj, jumpHeight, jumpType);
        }
        return this;
    }

    /**
     * Adds a scaling back and forth effect to the shift animation
     *
     * @param <T> The calling subclass
     * @param obj The matobject to apply the effect
     * @param scaleEffect The amount to scale. A value of null or 1 means no
     * effect.
     * @return This object
     */
    public <T extends ShiftAnimation> T addScaleEffect(MathObject obj, double scaleEffect) {
        effects.get(obj).addScaleEffect(scaleEffect);
        return (T) this;
    }

    /**
     * Adds a scaling back and forth effect to all the objects added to the
     * animation
     *
     * @param scaleEffect The amount to scale. A value of null or 1 means no
     * effect.
     * @return This object
     */
    @Override
    public ShiftAnimation addScaleEffect(double scaleEffect) {
        for (MathObject obj : mathObjects) {
            addScaleEffect(obj, scaleEffect);
        }
        return this;
    }

    /**
     * Adds a rotation effect to the shift animation
     *
     * @param <T> The calling subclass
     * @param obj The mathobject to apply the rotation
     * @param numTurns Angle ro rotate. A value of null or 0 means no effect
     * @return This object
     */
    public <T extends ShiftAnimation> T addRotationEffect(MathObject obj, int numTurns) {
        effects.get(obj).addRotationEffect(numTurns);
        return (T) this;
    }

    /**
     * Adds a rotation effect to every mathobject added to the animation
     *
     * @param numTurns Angle ro rotate. A value of null or 0 means no effect
     * @return This object
     */
    @Override
    public ShiftAnimation addRotationEffect(int numTurns) {
        for (MathObject obj : mathObjects) {
            addRotationEffect(obj, numTurns);
        }
        return this;
    }

    /**
     * Adds a rotation effect with a specified angle to every mathobject added
     * to the animation
     *
     * @param <T> The calling subclass
     * @param rotationAngle Rotation angle
     * @return This object
     */
    public <T extends ShiftAnimation> T addRotationEffectByAngle(double rotationAngle) {
        for (MathObject obj : mathObjects) {
            addRotationEffectByAngle(obj, rotationAngle);
        }
        return (T) this;
    }

    /**
     * Adds a rotation effect with a specified angle to a given mathobject added
     * to the animation
     *
     * @param <T> The calling subclass
     * @param obj MathObject to rotate
     * @param rotationAngle Rotation angle
     * @return This object
     */
    public <T extends ShiftAnimation> T addRotationEffectByAngle(MathObject obj, double rotationAngle) {
        rotationAngles.put(obj, rotationAngle);
        return (T) this;
    }

    /**
     * Adds a alpha scale effect to the specified object. A back and forth alpha
     * effect
     *
     * @param <T> The calling subclass
     * @param obj The MathObject to apply the scale
     * @param alphaScale The amount to scale. A value of .5 will reduce the
     * alpha to 50% at the middle of the animation.
     * @return This object
     */
    public <T extends ShiftAnimation> T addAlphaScaleEffect(MathObject obj, double alphaScale) {
        effects.get(obj).addAlphaEffect(alphaScale);
        return (T) this;
    }

    /**
     * Sets the delay percentage. A number between 0 and 1 that controls the
     * time gap between consecutive objects when shifting multiple ones. For
     * example, if you set the delay to 0.75, all shift animations will last 25
     * percent of initial time, evenly spaced over the total duration of the
     * animation. So, for an animation who shifts 3 objects for 2 seconds, each
     * one will last 2*0.25=.5 seconds, starting at 0, .75 and 1.5 respectively
     *
     * @param <T> Calling subclass
     * @param delayPercentage The delay. A number of 0 means no effect. A number
     * greater than 0
     * @return
     */
    public <T extends ShiftAnimation> T addDelayEffect(double delayPercentage) {
        if ((delayPercentage <= 0) || (delayPercentage >= 1)) {
            return (T) this;
        }
        this.delayPercentage = delayPercentage;

        return (T) this;
    }

    @Override
    protected void copyEffectParametersFrom(AnimationWithEffects anim) {
        for (MathObject obj : mathObjects) {
            effects.get(obj).copyEffectParametersFrom(anim.getEffect());
        }
    }

    @Override
    public void cleanAnimationAt(double t) {
        double lt=getLT(t);
        if (lt==0) {
            for (int i = 0; i < mathObjects.length; i++) {
                //If object initially wasn't in the scene, remove it
                if (shouldBeAdded[i]){
                    scene.remove(mathObjects[i]);
                }
            }
        }
        else {
            //for any other lt, object are automatically added to the scene
            for (MathObject mathObject : mathObjects) {
                scene.add(mathObject);
            }
    }
        
        
        
    }

    @Override
    public void prepareForAnim(double t) {
        //It is a good idea to ensure that objects we are moving are in the scene, just in case
        addObjectsToscene(mathObjects);
    }

    @Override
    public MathObjectGroup getIntermediateObject() {
        return MathObjectGroup.make(mathObjects);
    }
    
    

}
