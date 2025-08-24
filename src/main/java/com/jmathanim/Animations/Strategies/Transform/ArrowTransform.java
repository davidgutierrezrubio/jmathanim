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
import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.Arrow;
import com.jmathanim.mathobjects.MathObject;

/**
 * Transfom stratregy from one arrow to another. Currently only changes
 * starting/ending points, not head transform is done
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class ArrowTransform extends TransformStrategy {

    AnimationWithEffects anim;

    public ArrowTransform(double runTime, Arrow origin, Arrow destiny) {
        super(runTime);
        this.setOrigin(origin);
        this.setDestiny(destiny);
        setIntermediate(this.getOriginObject().copy());
    }

    @Override
    public boolean doInitialization() {
        super.doInitialization();
        Vec a = ((Arrow) getOriginObject()).getStart().copy();
        Vec b = ((Arrow) getOriginObject()).getEnd().copy();
        Vec c = ((Arrow) getOriginObject()).getStart().copy();
        Vec d = ((Arrow) getDestinyObject()).getEnd().copy();
        getIntermediateObject().copyStateFrom(this.getOriginObject());
        anim = Commands.isomorphism(runTime, a, b, c, d, getIntermediateObject());
        this.copyEffectParametersTo(anim);
        this.copyAnimationParametersTo(anim);
        anim.setLambda(getTotalLambda());
        anim.initialize(scene);
        return true;
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

    @Override
    public void reset() {
        super.reset();
        anim.reset();
    }
}
