/*
 * Copyright (C) 2021 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
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

import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;

/**
 * Transforms one object to another, animating a flip effect
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class FlipTransform extends AnimationWithEffects {

    /**
     * Flip type
     */
    public enum FlipType {
        /**
         * Horizontal flip. Scale in X and leaves Y scale unchanged.
         */
        HORIZONTAL, 
        /**
         * Vertical flip. Scale in Y and leaves X scale unchanged.
         */
        VERTICAL,
        /**
         * Flip both in X and Y 
        */
        BOTH
    }

    private FlipType flipType;
    private MathObject objOrig, objDst;
    private Point origCenter, dstCenter;

    /**
     * Creates a new FlipTransform animation, that flips the original object
     * horizontally or vertically to become the destiny object. When finished
     * the animation, original object is removed from scene.
     *
     * @param runTime Duration in seconds
     * @param flipType Flip type, a value of the enum variable FlipType
     * @param objOrig Original object
     * @param objDst Destiny object
     */
    public FlipTransform(double runTime, FlipType flipType, MathObject objOrig, MathObject objDst) {
        super(runTime);
        this.flipType = flipType;
        this.objDst = objDst;
        this.objOrig = objOrig;
        origCenter = objOrig.getCenter();
        dstCenter = objDst.getCenter();
    }

    /**
     * Static method to build a horizontal flip animation
     *
     * @param runTime Duration in seconds
     * @param objOrig Original object
     * @param objDst Destiny object
     * @return The animation to play with the playAnim method
     */
    public static FlipTransform HFlip(double runTime, MathObject objOrig, MathObject objDst) {
        return new FlipTransform(runTime, FlipType.HORIZONTAL, objOrig, objDst);
    }

    /**
     * Static method to build a vertical flip animation
     *
     * @param runTime Duration in seconds
     * @param objOrig Original object
     * @param objDst Destiny object
     * @return The animation to play with the playAnim method
     */
    public static FlipTransform VFlip(double runTime, MathObject objOrig, MathObject objDst) {
        return new FlipTransform(runTime, FlipType.VERTICAL, objOrig, objDst);
    }

    /**
     * Static method to build a flip animation both vertically and horizontally
     *
     * @param runTime Duration in seconds
     * @param objOrig Original object
     * @param objDst Destiny object
     * @return The animation to play with the playAnim method
     */
    public static FlipTransform Flip(double runTime, MathObject objOrig, MathObject objDst) {
        return new FlipTransform(runTime, FlipType.BOTH, objOrig, objDst);
    }

    @Override
    public void initialize(JMathAnimScene scene) {
        super.initialize(scene);
        addObjectsToscene(objOrig, objDst);
        saveStates(objOrig, objDst);
        objDst.visible(false);// At first this is hidden
        prepareJumpPath(origCenter, dstCenter, objDst);
        prepareJumpPath(origCenter, dstCenter, objOrig);
    }

    @Override
    public void doAnim(double t) {
        double lt = getLambda().applyAsDouble(t);
        objOrig.visible(lt < .5);
        objDst.visible(lt >= .5);
        MathObject objectToScale;
        if (lt < .5) {// Here we scale the first object, the second remains hidden
            objectToScale = objOrig;
        } else {
            objectToScale = objDst;
        }
        restoreStates(objectToScale);
        double scales[] = computeScale(lt);
        objectToScale.scale(scales[0], scales[1]);
        objectToScale.moveTo(origCenter.interpolate(dstCenter, lt));
        applyAnimationEffects(lt, objectToScale);
    }

    @Override
    public void finishAnimation() {
        super.finishAnimation(); // To change body of generated methods, choose Tools | Templates.
        removeObjectsToscene(objOrig);
    }

    private double[] computeScale(double lt) {
        double[] scales = new double[2];
        switch (flipType) {
            case HORIZONTAL:
                scales[0] = (lt < .5 ? 1 - 2 * lt : 2 * lt - 1);
                scales[1] = 1;
                break;
            case VERTICAL:
                scales[0] = 1;
                scales[1] = (lt < .5 ? 1 - 2 * lt : 2 * lt - 1);
                break;
            case BOTH:
                scales[0] = (lt < .5 ? 1 - 2 * lt : 2 * lt - 1);
                scales[1] = (lt < .5 ? 1 - 2 * lt : 2 * lt - 1);

                break;
        }
        return scales;
    }

}
