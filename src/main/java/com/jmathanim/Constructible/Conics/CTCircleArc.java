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
import static com.jmathanim.jmathanim.JMathAnimScene.PI;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Scalar;
import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class CTCircleArc extends CTAbstractCircle {

    private final CTPoint A;
    private final CTPoint B;
    private final CTPoint C;
    private final Shape arcTODraw;
    private Shape arcTODrawOrig;

    public static CTCircleArc make(CTPoint A, CTPoint B, CTPoint C) {
        CTCircleArc resul = new CTCircleArc(A, B, C);
        resul.rebuildShape();
        return resul;
    }

    private CTCircleArc(CTPoint A, CTPoint B, CTPoint C) {
        this.A = A;
        this.B = B;
        this.C = C;
        arcTODraw = Shape.arc(PI);
        arcTODrawOrig = new Shape();
    }

    @Override
    public CTPoint getCircleCenter() {
        return A.copy();
    }

    @Override
    public Scalar getRadius() {
        return Scalar.make(A.to(B).norm());
    }

    @Override
    public MathObject getMathObject() {
        return arcTODraw;
    }

    @Override
    public Constructible copy() {
        CTCircleArc copy = new CTCircleArc(A.copy(), B.copy(), C.copy());
        copy.getMp().copyFrom(getMp());
        return copy;
    }

    @Override
    public void rebuildShape() {
        AffineJTransform tr = AffineJTransform.createDirect2DIsomorphic(Point.at(0, 0), Point.at(1, 0), new Point(A.v), new Point(B.v), 1);
       
        Vec v1=A.to(B);
        Vec v2=A.to(C);
        double angle=v2.getAngle()-v1.getAngle();
        if (angle<0) angle+=2*PI;
        arcTODrawOrig=Shape.arc(angle);
        
        if (!isThisMathObjectFree()) {
            for (int i = 0; i < arcTODraw.size(); i++) {
                arcTODraw.get(i).copyFrom(arcTODrawOrig.get(i));
            }
            arcTODraw.applyAffineTransform(tr);
        }

    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        dependsOn(scene, A, B, C);
    }

}
