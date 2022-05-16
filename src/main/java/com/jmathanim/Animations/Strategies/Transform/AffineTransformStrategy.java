/*
 * Copyright (C) 2022 David Gutierrez Rubio davidgutierrezrubio@gmail.com
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

import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;

/**
 * A general abstract affine transform strategy
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class AffineTransformStrategy extends TransformStrategy {
    
    protected final Shape destiny;
    protected final Shape origin;
    protected final Shape intermediate;
    Point A;
    Point B;
    Point C;
    Point D;
    Point E;
    Point F;

    public AffineTransformStrategy(double runTime,Shape origin,Shape destiny) {
        super(runTime);
        this.destiny=destiny;
        this.origin=origin;
        this.intermediate=origin.copy();
    }

    @Override
    public void initialize(JMathAnimScene scene) {
        super.initialize(scene);
        A = origin.getPoint(0).copy();
        B = origin.getPoint(1).copy();
        C = origin.getPoint(2).copy();
        D = destiny.getPoint(0).copy();
        E = destiny.getPoint(1).copy();
        F = destiny.getPoint(2).copy();
        saveStates(intermediate);
        AffineJTransform tr = createIntermediateTransform(1);
        prepareJumpPath(intermediate.getCenter(), tr.getTransformedObject(intermediate.getCenter()), intermediate);
    }

    @Override
    public void doAnim(double t) {
        double lt = lambda.applyAsDouble(t);
        restoreStates(intermediate);
        AffineJTransform tr = createIntermediateTransform(lt);
        tr.applyTransform(intermediate);
        if (isShouldInterpolateStyles()) {
            intermediate.getMp().interpolateFrom(origin.getMp(), destiny.getMp(), lt);
        }
        // Transform effects
        applyAnimationEffects(lt, intermediate);
    }

    protected abstract AffineJTransform createIntermediateTransform(double lt);

    @Override
    public void finishAnimation() {
        super.finishAnimation();
        doAnim(1);
    }

    @Override
    public MathObject getIntermediateTransformedObject() {
        return intermediate;
    }

    @Override
    public MathObject getOriginObject() {
        return origin;
    }

    @Override
    public MathObject getDestinyObject() {
        return destiny;
    }
    
}
