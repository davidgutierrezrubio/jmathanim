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

import com.jmathanim.Animations.AnimationWithEffects;
import com.jmathanim.Styling.MODrawProperties;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class RotateAndScaleXYTransform extends AnimationWithEffects {

    private final Shape mobjDestiny;
    private final Shape mobjTransformed;
    private MODrawProperties mpBase;
    Point A, B, C, D, E, F;

    public RotateAndScaleXYTransform(double runtime, Shape mobjTransformed, Shape mobjDestiny) {
        super(runtime);
        this.mobjTransformed = mobjTransformed;
        this.mobjDestiny = mobjDestiny;
    }

    @Override
    public void initialize(JMathAnimScene scene) {
        super.initialize(scene);
        mpBase = mobjTransformed.getMp().copy();
        A = mobjTransformed.getPoint(0).copy();
        B = mobjTransformed.getPoint(1).copy();
        C = mobjTransformed.getPoint(2).copy();
        D = mobjDestiny.getPoint(0).copy();
        E = mobjDestiny.getPoint(1).copy();
        F = mobjDestiny.getPoint(2).copy();
        saveStates(mobjTransformed);
        addObjectsToscene(mobjTransformed);
        AffineJTransform tr = createIntermediateTransform(1);
        prepareJumpPath(mobjTransformed.getCenter(), tr.getTransformedObject(mobjTransformed.getCenter()), mobjTransformed);
    }

    @Override
    public void doAnim(double t) {
        double lt = lambda.applyAsDouble(t);
        restoreStates(mobjTransformed);

        AffineJTransform tr = createIntermediateTransform(lt);

        tr.applyTransform(mobjTransformed);
        mobjTransformed.getMp().interpolateFrom(mpBase, mobjDestiny.getMp(), lt);

        //Transform effects
        applyAnimationEffects(lt, mobjTransformed);

    }

    public AffineJTransform createIntermediateTransform(double lt) {
        //First map A,B into (0,0) and (1,0)
        AffineJTransform tr1 = AffineJTransform.createDirect2DHomothecy(A, B, new Point(0, 0), new Point(1, 0), 1);
        //Now I create a transformation that adjust the y-scale, proportionally
        //This transform will be applied inversely too
        AffineJTransform tr2 = new AffineJTransform();
        final double proportionalHeight = (F.to(E).norm() / D.to(E).norm()) / (B.to(C).norm() / B.to(A).norm());
        tr2.setV2Img(0, proportionalHeight * lt + (1 - lt) * 1); //Interpolated here
        //Finally, and homothecy to carry A,B into D,E
        AffineJTransform tr3 = AffineJTransform.createDirect2DHomothecy(A, B, D, E, lt);//Interpolated here
        //The final transformation
        AffineJTransform tr = tr1.compose(tr2).compose(tr1.getInverse()).compose(tr3);
        return tr;
    }

    @Override
    public void finishAnimation() {
        super.finishAnimation();
        doAnim(1);
    }

}
