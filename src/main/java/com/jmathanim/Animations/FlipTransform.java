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

import com.jmathanim.Utils.OrientationType;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;

/**
 * Transforms one object to another, animating a flip effect
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class FlipTransform extends AnimationWithEffects {

//    /**
//     * Flip type
//     */
//    public enum FlipType {
//        /**
//         * Horizontal flip. Scale in X and leaves Y scale unchanged.
//         */
//        HORIZONTAL, 
//        /**
//         * Vertical flip. Scale in Y and leaves X scale unchanged.
//         */
//        VERTICAL,
//        /**
//         * Flip both in X and Y 
//        */
//        BOTH
//    }
    private final OrientationType flipType;
    private final MathObject objOrig;
    private final MathObject objDst;
    private MathObject intermediateObject;
    private Point origCenter;
    private Point dstCenter;

    /**
     * Creates a new FlipTransform animation, that flips the original object
     * horizontally or vertically to become the destiny object. When finished
     * the animation, original object is removed and destiniy object is added to
     * scene.
     *
     * @param runTime Duration in seconds
     * @param flipType Flip type, a value of the enum variable FlipType
     * @param objOrig Original object
     * @param objDst Destiny object
     */
    public FlipTransform(double runTime, OrientationType flipType, MathObject objOrig, MathObject objDst) {
        super(runTime);
        setDebugName("FlipTransform");
        this.flipType = flipType;
        this.objDst = objDst;
        this.objOrig = objOrig;

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
        return new FlipTransform(runTime, OrientationType.HORIZONTAL, objOrig, objDst);
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
        return new FlipTransform(runTime, OrientationType.VERTICAL, objOrig, objDst);
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
        return new FlipTransform(runTime, OrientationType.BOTH, objOrig, objDst);
    }

    @Override
    public boolean doInitialization() {
        //TODO: Implement exceptions for detailed errors initializating animations
        super.doInitialization();
        origCenter = objOrig.getCenter();
        dstCenter = objDst.getCenter();
        addObjectsToscene(objOrig, objDst);
        saveStates(objOrig, objDst);
        objDst.visible(false);// At first this is hidden
        prepareJumpPath(origCenter, dstCenter, objDst);
        prepareJumpPath(origCenter, dstCenter, objOrig);
        intermediateObject = objOrig;
        return true;
    }

    @Override
    public void doAnim(double t) {
        super.doAnim(t);
        double lt = getLT(t);
        objOrig.visible(lt < .5);
        objDst.visible(lt >= .5);
        if (lt < .5) {// Here we scale the first object, the second remains hidden
            intermediateObject = objOrig;
        } else {
            intermediateObject = objDst;
        }
        restoreStates(intermediateObject);
        double[] scales = computeScale(lt);
        intermediateObject.scale(scales[0], scales[1]);
        intermediateObject.moveTo(origCenter.interpolate(dstCenter, lt));
        applyAnimationEffects(lt, intermediateObject);
    }

    @Override
    public void finishAnimation() {
        super.finishAnimation(); // To change body of generated methods, choose Tools | Templates.
        cleanAnimationAt(t);
    }

    @Override
    public void cleanAnimationAt(double t) {
        double lt = getLT(t);
        doAnim(t);
        if (lt >= .5) {
            restoreStates(objOrig);
            removeObjectsFromScene(objOrig);
            addObjectsToscene(objDst);
            intermediateObject = objDst;
        } else {
            restoreStates(objDst);
            removeObjectsFromScene(objDst);
            addObjectsToscene(objOrig);
            intermediateObject = objOrig;
        }
    }

    @Override
    public void prepareForAnim(double t) {
        double lt = getLT(t);
        addObjectsToscene(objDst, objOrig);
        objOrig.visible(lt < .5);
        objDst.visible(lt >= .5);
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

    /**
     * Returns the intermediate transformed object. It is either an instance of
     * origin object or destiny object depending on the actual animation time.
     *
     * @return The currently object being animated (origin or destiny)
     */
    @Override
    public MathObject getIntermediateObject() {
        return intermediateObject;
    }
}
