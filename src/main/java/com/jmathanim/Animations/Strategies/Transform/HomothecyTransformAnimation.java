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
package com.jmathanim.Animations.Strategies.Transform;

import com.jmathanim.Animations.AnimationGroup;
import com.jmathanim.Animations.AnimationWithEffects;
import com.jmathanim.Animations.Commands;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class HomothecyTransformAnimation extends AnimationWithEffects {

    AnimationGroup anim;
    private AnimationWithEffects homothecy;
    private final Shape mobjTransformed;
    private final Shape mobjDestiny;
//    private final Shape mobjTransformedOrig;

    public HomothecyTransformAnimation(double runtime, Shape mobjTransformed, Shape mobjDestiny) {
        super(runtime);
        this.mobjTransformed = mobjTransformed;// .copy();
//        this.mobjTransformedOrig = mobjTransformed;
        this.mobjDestiny = mobjDestiny;

    }

    @Override
    public void initialize(JMathAnimScene scene) {
        super.initialize(scene);
        Point a = this.mobjTransformed.getPoint(0);
        Point b = this.mobjTransformed.getPoint(1);
        Point c = this.mobjDestiny.getPoint(0);
        Point d = this.mobjDestiny.getPoint(1);
        anim = new AnimationGroup();
        homothecy = Commands.homothecy(runTime, a, b, c, d, this.mobjTransformed);
        homothecy.setUseObjectState(isUseObjectState());
        anim.add(homothecy);
        if (this.isShouldInterpolateStyles()) {
            anim.add(Commands.setMP(runTime, mobjDestiny.getMp().copy(), this.mobjTransformed).setUseObjectState(false));
        }
        this.copyAnimationParametersTo(anim);
        this.copyEffectParametersTo(anim);
//        homothecy.prepareJumpPath(this.mobjTransformed.getCenter(), this.mobjDestiny.getCenter(), this.mobjTransformed);
        anim.initialize(scene);

    }

    @Override
    public boolean processAnimation() {
        super.processAnimation();
        boolean value = anim.processAnimation();
        return value;
    }

    @Override
    public void doAnim(double t) {
    }

    @Override
    public void finishAnimation() {
        super.finishAnimation();
        removeObjectsToscene(this.mobjTransformed);
        anim.finishAnimation();
    }

}
