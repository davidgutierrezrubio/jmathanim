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

    public final ArrayList<Animation> animations;

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
     * @param anmts Animations to add (varargs)
     */
    public AnimationGroup(Animation... anmts) {
        this.animations = new ArrayList<>();
        this.animations.addAll(Arrays.asList(anmts));

    }

    @Override
    public boolean processAnimation() {
        boolean finishedAll = true;
        for (Animation anim : animations) {
            finishedAll = finishedAll & anim.processAnimation();
        }
        return finishedAll;
    }

    public AnimationGroup(ArrayList<Animation> animations) {
        this.animations = animations;
    }

    public int size() {
        return animations.size();
    }

    public boolean add(Animation e) {
        return animations.add(e);
    }

    public void clear() {
        animations.clear();
    }

    public boolean addAll(Collection<? extends Animation> c) {
        return animations.addAll(c);
    }

    @Override
    public void initialize() {
        for (Animation anim : animations) {
            anim.initialize();
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
        for (Animation anim : animations) {
            if (!anim.isEnded()) {
                anim.finishAnimation();
            }
        }
    }


    @Override
    public void setLambda(DoubleUnaryOperator lambda) {
        super.setLambda(lambda); 
        for (Animation anim:animations) {
            anim.setLambda(lambda);
        }
    }

}
