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

import com.jmathanim.Utils.UsefulLambdas;
import com.jmathanim.jmathanim.JMathAnimScene;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.DoubleUnaryOperator;

/**
 * Stores a group of animations, to be played at the same time. This class is
 * used mainly for internal use, to be able to define complex animations using
 * simpler ones, as in the Concatenate class.
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class AnimationGroup extends AnimationWithEffects {

    private final ArrayList<Animation> animations;
    private double delayPercentage;

    /**
     * Returns the list of the animations to play
     *
     * @return An ArrayList with the animations
     */
    public ArrayList<Animation> getAnimations() {
        return animations;
    }

    /**
     * Creates a new AnimationGroup with the given animations
     *
     * @param anims Animations to add (varargs)
     * @return The new AnimationGroup created
     */
    public static AnimationGroup make(Animation... anims) {
        return new AnimationGroup(anims);
    }

//    /**
//     * Creates a new, empty, AnimationGroup. This class stores a group of
//     * animations, to be played at the same time.
//     */
//    public AnimationGroup() {
//        super(0);
//        this.animations = new ArrayList<>();
//    }
    /**
     * Creates a new AnimationGroup with given animations.This class stores a
     * group of animations, to be played at the same time.
     *
     * @param animations Animations to add (varargs)
     */
    public AnimationGroup(Animation... animations) {
        super(0);
        super.setLambda(null);
        this.animations = new ArrayList<>();
        this.animations.addAll(Arrays.asList(animations));

        this.useObjectState = false;
        this.shouldAddObjectsToScene = false;
        this.shouldInterpolateStyles = false;
    }

    /**
     * Overloaded method. Creates a new AnimationGroup with given animations
     * using an ArrayList.This class stores a group of animations, to be played
     * at the same time.
     *
     * @param animations Animations to add (varargs)
     */
    public AnimationGroup(ArrayList<Animation> animations) {
        super(0);
        this.animations = animations;
    }

    /**
     * Add the given animations to the list
     *
     * @param <T> Calling subclass
     * @param anims Animations to add (varargs)
     * @return This object
     */
    public <T extends AnimationGroup> T add(Animation... anims) {
        animations.addAll(Arrays.asList(anims));
        return (T) this;
    }

    @Override
    public void initialize(JMathAnimScene scene) {
        //Compute runTime as the MAX runtime of all animations
        this.runTime=0;
        for (Animation an : animations) {
            runTime=(runTime<an.getRunTime() ? an.getRunTime() : runTime);
        }
        super.initialize(scene);

        int size = animations.size();
        if (!"".equals(getDebugName())) {
            setDebugName("Animation group with " + size + " animations");
        }
        int k = 0;

//        for (Animation anim : animations) {
//            this.copyAnimationParametersTo(anim);
//        }

        for (Animation anim : animations) {
            if (anim != null) {
                if (anim instanceof AnimationWithEffects) {
                    AnimationWithEffects animEf = (AnimationWithEffects) anim;
                    this.copyEffectParametersTo(animEf);
                }
                anim.initialize(scene);
            }
        }

        if ((size > 1) && (delayPercentage > 0)) {
            double b = 1 - delayPercentage;
            for (Animation anim : animations) {
                double a = k * (delayPercentage) / (size - 1);
                anim.setAllocationParameters(a, a+b);
                k++;
            }
        }
    }

//    @Override
//    public boolean processAnimation() {
//        boolean finishedAll = true;
//        for (Animation anim : animations) {
//            finishedAll = finishedAll & anim.processAnimation();
//        }
//        return finishedAll;
//    }

    @Override
    public void doAnim(double t) {
        for (Animation anim : animations) {
            double mt=t*runTime/anim.getRunTime();
            anim.doAnim(UsefulLambdas.allocateTo(allocateStart, allocateEnd).applyAsDouble(mt));
        }
    }

    @Override
    public double getRunTime() {
        double max=0;
        for (Animation anim:animations) {
            max=Math.max(max, anim.getRunTime());
        }
        return max;
    }

    
    
    @Override
    public void finishAnimation() {
        super.finishAnimation();
        for (Animation anim : animations) {
            if (anim.getStatus() != Status.FINISHED) {
                anim.finishAnimation();
            }
        }
    }

    @Override
    public final AnimationGroup setLambda(DoubleUnaryOperator lambda) {
        for (Animation anim : animations) {
            anim.setLambda(lambda);
        }
        return this;
    }


    @Override
    public AnimationGroup setUseObjectState(boolean shouldSaveState) {
        for (Animation anim : animations) {
            anim.setUseObjectState(shouldSaveState);
        }
        return this;
    }

    @Override
    public AnimationGroup setAddObjectsToScene(boolean addToScene) {
        for (Animation anim : animations) {
            anim.setAddObjectsToScene(addToScene);
        }
        return this;
    }

    @Override
    public AnimationGroup setShouldInterpolateStyles(boolean interpolateStyles) {
        for (Animation anim : animations) {
            anim.setShouldInterpolateStyles(interpolateStyles);
        }
        return this;
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
    public <T extends AnimationGroup> T addDelayEffect(double delayPercentage) {
        if ((delayPercentage <= 0) || (delayPercentage >= 1)) {
            return (T) this;
        }
        this.delayPercentage = delayPercentage;

        return (T) this;
    }

}
