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
package com.jmathanim.Animations.Strategies.Transform;

import com.jmathanim.Animations.Animation;
import com.jmathanim.Animations.AnimationGroup;
import com.jmathanim.Animations.Commands;
import com.jmathanim.Styling.MODrawProperties;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class GeneralAffineTransformAnimation extends Animation {

    private Animation affine;

    private AnimationGroup anim;

    private final Shape mobjDestiny;

    private final Shape mobjTransformed;

    private final Shape mobjTransformedOrig;
    private final MODrawProperties mpBase;

    public GeneralAffineTransformAnimation(double runTime, Shape objTr, Shape objDst) {
        super(runTime);
        this.mobjTransformed = objTr;
        this.mobjTransformedOrig = objTr.copy();
        this.mobjDestiny = objDst;
        mpBase = objTr.getMp().copy();
    }

    @Override
    public void initialize(JMathAnimScene scene) {
        super.initialize(scene);
        Point a = this.mobjTransformed.getPoint(0);
        Point b = this.mobjTransformed.getPoint(1);
        Point c = this.mobjTransformed.getPoint(2);
        Point d = this.mobjDestiny.getPoint(0);
        Point e = this.mobjDestiny.getPoint(1);
        Point f = this.mobjDestiny.getPoint(2);

        anim = new AnimationGroup();
        affine = Commands.affineTransform(runTime, a, b, c, d, e, f, this.mobjTransformed);
        affine.setUseObjectState(this.isUseObjectState());
        anim.add(affine);
        anim.add(Commands.setMP(runTime, mobjDestiny.getMp().copy(), this.mobjTransformed).setUseObjectState(false));
        anim.setLambda(lambda);
        anim.initialize(scene);

    }


    @Override
    public boolean processAnimation() {
        super.processAnimation();
        return anim.processAnimation();
    }

    @Override
    public void doAnim(double t) {
    }

    @Override
    public void finishAnimation() {
        super.finishAnimation();
        anim.finishAnimation();
    }

}
