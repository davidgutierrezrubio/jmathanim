/*
 * Copyright (C) 2021 David Gutierrez Rubio davidgutierrezrubio@gmail.com
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

import com.jmathanim.Animations.AnimationWithEffects;
import com.jmathanim.Animations.Commands;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Arrow2D;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;

/**
 * Transfom stratregy from one arrow to another
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class ArrowTransform extends TransformStrategy {

    AnimationWithEffects anim;

    public ArrowTransform(double runTime, Arrow2D origin, Arrow2D destiny) {
        super(runTime);
        this.origin = origin;
        this.destiny = destiny;
        intermediate = this.origin.copy();
    }

    @Override
    public void initialize(JMathAnimScene scene) {
        super.initialize(scene);
        Point a = ((Arrow2D)origin).getStart().copy();
        Point b = ((Arrow2D)origin).getEnd().copy();
        Point c = ((Arrow2D)destiny).getStart().copy();
        Point d = ((Arrow2D)destiny).getEnd().copy();
        intermediate.copyStateFrom(this.origin);
        anim = Commands.isomorphism(runTime, a, b, c, d, intermediate);
        this.copyEffectParametersTo(anim);
        this.copyAnimationParametersTo(anim);
        anim.setLambda(getTotalLambda());
        anim.initialize(scene);
    }

    @Override
    public void doAnim(double t) {
        super.doAnim(t);
        anim.doAnim(t);
    }

    @Override
    public void finishAnimation() {
        super.finishAnimation();
        anim.finishAnimation();
    }


    @Override
    public MathObject getIntermediateObject() {
        return anim.getIntermediateObject();
    }
}
