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

import com.jmathanim.jmathanim.JMathAnimScene;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.DoubleUnaryOperator;

/**
 * Stores a group of animations, to be played at the same time. This class is
 * used mainly for internal use, to be able to define complex animations using
 * simpler ones, as in the Concatenate class.
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class AnimationGroup extends Animation {

    private final ArrayList<Animation> animations;

    /**
     * Returns the list of the animations to play
     *
     * @return An ArrayList with the animations
     */
    public ArrayList<Animation> getAnimations() {
        return animations;
    }

    /**
     * Creates a new, empty, AnimationGroup. This class stores a group of
     * animations, to be played at the same time.
     */
    public AnimationGroup() {
        this.animations = new ArrayList<>();
    }

    /**
     * Creates a new AnimationGroup with given animations.This class stores a
     * group of animations, to be played at the same time.
     *
     * @param animations Animations to add (varargs)
     */
    public AnimationGroup(Animation... animations) {
        this.animations = new ArrayList<>();
        this.animations.addAll(Arrays.asList(animations));

    }

    @Override
    public boolean processAnimation() {
        boolean finishedAll = true;
        for (Animation anim : animations) {
            finishedAll = finishedAll & anim.processAnimation();
        }
        return finishedAll;
    }

    /**
     * Overloaded method. Creates a new AnimationGroup with given animations
     * using an ArrayList.This class stores a group of animations, to be played
     * at the same time.
     *
     * @param animations Animations to add (varargs)
     */
    public AnimationGroup(ArrayList<Animation> animations) {
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
        for (Animation anim : anims) {
            animations.add(anim);
        }
        return (T) this;
    }

    @Override
    public void initialize(JMathAnimScene scene) {
        super.initialize(scene);
        for (Animation anim : animations) {
            anim.initialize(scene);
        }
    }

    @Override
    public void doAnim(double t) {
        for (Animation anim : animations) {
            anim.doAnim(t);
        }
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
    public <T extends Animation> T setLambda(DoubleUnaryOperator lambda) {
        super.setLambda(lambda);
        for (Animation anim : animations) {
            anim.setLambda(lambda);
        }
        return (T) this;
    }

    @Override
    public <T extends Animation> T setUseObjectState(boolean shouldSaveState) {
        for (Animation anim : animations) {
            anim.setUseObjectState(shouldSaveState);
        }
        return (T) this;
    }

    @Override
    public <T extends Animation> T setAddObjectsToScene(boolean addToScene) {
        for (Animation anim : animations) {
            anim.setAddObjectsToScene(addToScene);
        }
        return (T) this;
    }

}
