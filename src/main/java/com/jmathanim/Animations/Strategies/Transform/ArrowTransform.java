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
import com.jmathanim.mathobjects.Point;

/**
 * Transfom stratregy from one arrow to another
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class ArrowTransform extends AnimationWithEffects {

    Arrow2D arOrig, arDst;
    AnimationWithEffects anim;

    public ArrowTransform(double runTime, Arrow2D arOrig, Arrow2D arDst) {
        super(runTime);
        this.arOrig = arOrig;
        this.arDst = arDst;
    }

    @Override
    public void initialize(JMathAnimScene scene) {
        super.initialize(scene);
        Point a = arOrig.getStart().copy();
        Point b = arOrig.getEnd().copy();
        Point c = arDst.getStart().copy();
        Point d = arDst.getEnd().copy();
        anim = Commands.homothecy(runTime, a, b, c, d, arOrig);
        this.copyEffectParametersTo(anim);
        this.copyAnimationParametersTo(anim);
        anim.initialize(scene);
    }

    @Override
    public boolean processAnimation() {
        super.processAnimation();
        return anim.processAnimation();
        // TODO: Implement creation/deletion of arrow heads (with shape transform)
    }

    @Override
    public void doAnim(double t) {
    }

    @Override
    public void finishAnimation() {
        super.finishAnimation();
        removeObjectsToscene(arOrig);
        addObjectsToscene(arDst);
    }

}
