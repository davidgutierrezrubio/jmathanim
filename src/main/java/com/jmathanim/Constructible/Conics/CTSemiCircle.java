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
package com.jmathanim.Constructible.Conics;

import com.jmathanim.Constructible.Constructible;
import com.jmathanim.Constructible.Points.CTPoint;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Scalar;
import com.jmathanim.mathobjects.Shape;

import static com.jmathanim.jmathanim.JMathAnimScene.PI;

/**
 * Represents a Constructible semicircle
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class CTSemiCircle extends CTAbstractCircle{

    private final Shape arcTODraw;
    private final Shape arcTODrawOrig;
    private final CTPoint B;
    private final CTPoint A;

    /**
     * Overloaded method. Creates a Constructible semicircle from 2 given
     * points. The semicircle will run clockwise from first to second point
     *
     * @param A First point
     * @param B Second point
     * @return The created object
     */
    public static CTSemiCircle make(Point A, Point B) {
        return make(CTPoint.make(A), CTPoint.make(B));
    }

    /**
     * Creates a Constructible semicircle from 2 given points. The semicircle
     * will run counterclockwise from first to second point
     *
     * @param A First point
     * @param B Second point
     * @return The created object
     */
    public static CTSemiCircle make(CTPoint A, CTPoint B) {
        CTSemiCircle resul = new CTSemiCircle(A, B);
        resul.rebuildShape();
        return resul;
    }

    private CTSemiCircle(CTPoint A, CTPoint B) {
        this.A = A;
        this.B = B;
        arcTODraw = Shape.arc(PI);
        arcTODrawOrig = Shape.arc(PI);
    }

    @Override
    public CTPoint getCircleCenter() {
        final Vec vv = A.v.interpolate(B.v, .5);
        return CTPoint.at(vv.x, vv.y);
    }

    @Override
    public Vec getHoldCoordinates(Vec coordinates) {
        //Map A and B to (1,0) and (-1,0)
        AffineJTransform tr = AffineJTransform.createDirect2DIsomorphic(B.getMathObject(), A.getMathObject(), Point.at(1, 0), Point.at(-1, 0), 1);
        Vec v = coordinates.applyAffineTransform(tr);
        if (v.x <= - 1) {
            v.x = -1;
            v.y = 0;
        } else if (v.x >= 1) {
            v.x = 1;
            v.y = 0;
        } else {
            v.y = Math.sqrt(1 - v.x * v.x);
        }
        return v.applyAffineTransform(tr.getInverse());
    }

    @Override
    public Scalar getRadius() {
        return Scalar.make(.5 * A.to(B).norm());
    }

    @Override
    public MathObject getMathObject() {
        return arcTODraw;
    }

    @Override
    public Constructible copy() {
        CTSemiCircle copy = new CTSemiCircle(A.copy(), B.copy());
        copy.getMp().copyFrom(getMp());
        return copy;
    }

    @Override
    public void rebuildShape() {
        AffineJTransform tr = AffineJTransform.createDirect2DIsomorphic(Point.at(-1, 0), Point.at(1, 0), new Point(A.v.x,A.v.y), new Point(B.v.x,B.v.y), 1);
        if (!isFreeMathObject()) {
            for (int i = 0; i < arcTODraw.size(); i++) {
                arcTODraw.get(i).copyControlPointsFrom(arcTODrawOrig.get(i));
            }
            arcTODraw.applyAffineTransform(tr);
        }
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        dependsOn(scene, A, B);
    }

}
