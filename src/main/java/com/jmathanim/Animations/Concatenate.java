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
import java.util.List;
import java.util.function.DoubleUnaryOperator;

/**
 * Stores 2 or more animations and play them in sequential order
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Concatenate extends Animation {

    private final ArrayList<Animation> animations;
    private int currentAnim;

    public Concatenate() {
        this(new ArrayList<Animation>());
    }

    public Concatenate(Animation... anims) {
        this(Arrays.asList(anims));
    }

    public Concatenate(List<Animation> anims) {
        super();
        this.animations = new ArrayList<Animation>();
        this.animations.addAll(anims);
        currentAnim = 0;

    }

    public final boolean add(Animation e) {
        return animations.add(e);
    }

    @Override
    public void initialize() {
        animations.get(0).initialize();
    }

    @Override
    public boolean processAnimation() {
        if (currentAnim == this.animations.size()) {//If I already finished...
            return true;
        }
        boolean resul = animations.get(currentAnim).processAnimation();
        if (resul) {
            animations.get(currentAnim).finishAnimation();
            currentAnim++;
            if (currentAnim < this.animations.size()) {
                animations.get(currentAnim).initialize();
                resul = false;
            }
        }
        return resul;
    }

    @Override
    public void finishAnimation() {
    }

    @Override
    public void addObjectsToScene(JMathAnimScene scene) {
    }

    @Override
    public void doAnim(double t) {
    }

    @Override
    public void setLambda(DoubleUnaryOperator lambda) {
        super.setLambda(lambda);
        for (Animation anim : animations) {
            anim.setLambda(lambda);
        }
    }
}
