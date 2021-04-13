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
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import java.util.HashMap;

/**
 * A generic shift animation. This subclass is instatiated from every
 * implementation of a shift, like align, stackto or moveIn, moveOut animations
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class ShiftAnimation extends Animation {

    private double delayPercentage;

    protected MathObject[] mathObjects;
    HashMap<MathObject, Vec> shiftVectors;
    private final HashMap<MathObject, AnimationEffect> effects;
    private final HashMap<MathObject, Double> beginningTimes;

    public ShiftAnimation(double runTime, MathObject[] mathObjects) {
        super(runTime);
        this.mathObjects = mathObjects;
        shiftVectors = new HashMap<>();
        effects = new HashMap<>();
        beginningTimes = new HashMap<>();
        delayPercentage = 0;
    }

    @Override
    public void initialize(JMathAnimScene scene) {
        super.initialize(scene);
        saveStates(mathObjects);
        addObjectsToscene(mathObjects);
        int size = mathObjects.length-1;
        int k = 0;
        for (MathObject obj : mathObjects) {
            double name = k *(delayPercentage)/size;
            beginningTimes.put(obj, name);
            System.out.println("k="+k+",  "+name);
            k++;
        }

    }

    public double allocateToNewTime(double a, double b, double t) {
        if (t<a) return 0;
        if (t>b) return 1;
        return (t-a)/(b-a);
    }
    
    
    @Override
    public void doAnim(double t) {

        double lt = getLambda().applyAsDouble(t);
        restoreStates(mathObjects);
        double b=(1-delayPercentage);
        for (MathObject obj : mathObjects) {
            Vec v = shiftVectors.get(obj);//Gets the shift vector for this object
            if (delayPercentage>0) {
                double a=beginningTimes.get(obj);
            double newT = allocateToNewTime(a, a+b, t);
                lt=getLambda().applyAsDouble(newT);
            }
            obj.shift(v.mult(lt));
            if (effects.containsKey(obj)) {
                effects.get(obj).applyAnimationEffects(lt, obj);
            }
        }
    }

    @Override
    public void finishAnimation() {
        super.finishAnimation();
        doAnim(1);
    }

    public Vec getShiftVector(MathObject obj) {
        return this.shiftVectors.get(obj);
    }

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
    public double getRotationAngle(MathObject obj) {
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
     * @param <T> The calling subccass
     * @param obj The mathobject to apply the jump
     * @param jumpHeight Height of the jump. Negative heights can be passed.
     * @param jumpType
     * @return This object
     */
    public <T extends ShiftAnimation> T addJumpEffect(MathObject obj, double jumpHeight, AnimationEffect.JumpType jumpType) {
        if (!effects.containsKey(obj)) {
            effects.put(obj, new AnimationEffect());
        }
        effects.get(obj).addJumpEffect(jumpHeight, jumpType);
        return (T) this;
    }

    public <T extends ShiftAnimation> T addJumpEffect(MathObject obj, double jumpHeight) {
        addJumpEffect(obj, jumpHeight, AnimationEffect.JumpType.PARABOLICAL);
        return (T) this;
    }

    /**
     * Sets the jump height for all the objects added to the animation
     *
     * @param <T> The calling subclass
     * @param jumpHeight Height of the jump. Negative heights can be passed.
     * @return This object
     */
    public <T extends ShiftAnimation> T addJumpEffect(double jumpHeight) {
        for (MathObject obj : mathObjects) {
            addJumpEffect(obj, jumpHeight);
        }
        return (T) this;
    }

    public <T extends ShiftAnimation> T addJumpEffect(double jumpHeight, AnimationEffect.JumpType jumpType) {
        for (MathObject obj : mathObjects) {
            addJumpEffect(obj, jumpHeight, jumpType);
        }
        return (T) this;
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
     * @param <T> The calling subclass
     * @param scaleEffect The amount to scale. A value of null or 1 means no
     * effect.
     * @return This object
     */
    public <T extends ShiftAnimation> T addScaleEffect(double scaleEffect) {
        for (MathObject obj : mathObjects) {
            addScaleEffect(obj, scaleEffect);
        }
        return (T) this;
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
     * @param <T> The calling subclass
     * @param numTurns Angle ro rotate. A value of null or 0 means no effect
     * @return This object
     */
    public <T extends ShiftAnimation> T addRotationEffect(int numTurns) {
        for (MathObject obj : mathObjects) {
            addRotationEffect(obj, numTurns);
        }
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
}
