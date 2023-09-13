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

    private final Arrow2D origin;
    private final Arrow2D destiny;
    private final Arrow2D intermediateObject;
    AnimationWithEffects anim;

    public ArrowTransform(double runTime, Arrow2D origin, Arrow2D destiny) {
        super(runTime);
        this.origin = origin;
        this.destiny = destiny;
        intermediateObject = this.origin.copy();
    }

    @Override
    public void initialize(JMathAnimScene scene) {
        super.initialize(scene);
        Point a = origin.getStart().copy();
        Point b = origin.getEnd().copy();
        Point c = destiny.getStart().copy();
        Point d = destiny.getEnd().copy();
        intermediateObject.copyStateFrom(this.origin);
        anim = Commands.isomorphism(runTime, a, b, c, d, intermediateObject);
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
    public MathObject getOriginObject() {
        return origin;
    }

    @Override
    public MathObject getDestinyObject() {
        return destiny;
    }

    @Override
    public MathObject getIntermediateObject() {
        return intermediateObject;
    }

    @Override
    public void cleanAnimationAt(double t) {
        double lt = getLT(t);
        if (lt == 0) {//If ends at t=0, keep original
            removeObjectsFromScene(destiny, intermediateObject);
            addObjectsToscene(origin);
            return;
        }
        if (lt == 1) {//If ends at t=1 keep destiny
            removeObjectsFromScene(origin, intermediateObject);
            addObjectsToscene(destiny);
            return;
        }
        //Case 0<t<1
        removeObjectsFromScene(origin, destiny);
        addObjectsToscene(intermediateObject);
    }

    @Override
    public void prepareForAnim(double t) {
        addObjectsToscene(intermediateObject);
    }
}
