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
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;

/**
 * A general abstract affine transform strategy
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class AffineTransformStrategy extends TransformStrategy {

    protected final Shape shDestiny;
    protected final Shape shOrigin;
    Point A;
    Point B;
    Point C;
    Point D;
    Point E;
    Point F;

    public AffineTransformStrategy(double runTime, Shape origin, Shape destiny) {
        super(runTime);
        this.setDestiny(destiny);
        this.setOrigin(origin);
        this.shOrigin = origin;
        this.shDestiny = destiny;
        this.setIntermediate(origin.copy());
    }

    @Override
    public boolean doInitialization() {
        super.doInitialization();
        A = shOrigin.getPoint(0).copy();
        B = shOrigin.getPoint(1).copy();
        C = shOrigin.getPoint(2).copy();
        D = shDestiny.getPoint(0).copy();
        E = shDestiny.getPoint(1).copy();
        F = shDestiny.getPoint(2).copy();
        saveStates(getIntermediateObject());
        AffineJTransform tr = createIntermediateTransform(1);
        Point center = getIntermediateObject().getCenter();
        prepareJumpPath(center, tr.getTransformedObject(center), getIntermediateObject());
        return true;
    }

    @Override
    public void doAnim(double t) {
        super.doAnim(t);
        double lt = getLT(t);
        restoreStates(getIntermediateObject());
        AffineJTransform tr = createIntermediateTransform(lt);
        tr.applyTransform(getIntermediateObject());
        if (isShouldInterpolateStyles()) {
            getIntermediateObject().getMp().interpolateFrom(getOriginObject().getMp(), getDestinyObject().getMp(), lt);
        }
        // Transform effects
        applyAnimationEffects(lt, getIntermediateObject());
    }

    protected abstract AffineJTransform createIntermediateTransform(double lt);

}
