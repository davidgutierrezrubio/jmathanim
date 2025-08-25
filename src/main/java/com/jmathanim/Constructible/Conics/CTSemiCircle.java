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

import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Coordinates;
import com.jmathanim.mathobjects.Shape;

import static com.jmathanim.jmathanim.JMathAnimScene.PI;

/**
 * Represents a Constructible semicircle
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class CTSemiCircle extends CTAbstractCircle<CTSemiCircle>{

    private final Shape arcTODraw;
    private final Shape arcTODrawOrig;
    private final Vec B;
    private final Vec A;


    /**
     * Creates a Constructible semicircle from 2 given points. The semicircle
     * will run counterclockwise from first to second point
     *
     * @param A First point
     * @param B Second point
     * @return The created object
     */
    public static CTSemiCircle make(Coordinates<?> A, Coordinates<?> B) {
        CTSemiCircle resul = new CTSemiCircle(A, B);
        resul.rebuildShape();
        return resul;
    }

    private CTSemiCircle(Coordinates<?> A, Coordinates<?> B) {
        this.A = A.getVec();
        this.B = B.getVec();
        arcTODraw = Shape.arc(PI);
        arcTODrawOrig = Shape.arc(PI);
    }

    @Override
    public Vec getHoldCoordinates(Vec coordinates) {
        //Map A and B to (1,0) and (-1,0)
        AffineJTransform tr = AffineJTransform.createDirect2DIsomorphic(
                B, A,
                Vec.to(1, 0),
                Vec.to(-1, 0), 1);
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
    public Shape getMathObject() {
        return arcTODraw;
    }

    @Override
    public CTSemiCircle copy() {
        CTSemiCircle copy = new CTSemiCircle(A.copy(), B.copy());
        copy.getMp().copyFrom(getMp());
        return copy;
    }

    @Override
    public void rebuildShape() {
        setCircleCenter(A.interpolate(B,.5));
        setCircleRadius(A.to(B).norm());
        AffineJTransform tr = AffineJTransform.createDirect2DIsomorphic(Vec.to(-1, 0), Vec.to(1, 0),
                A, B, 1);
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
