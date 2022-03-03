/*
 * Copyright (C) 2022 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
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

import com.jmathanim.Constructible.Points.CTPoint;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import static com.jmathanim.jmathanim.JMathAnimScene.PI;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Scalar;
import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class CTCIrcleSector extends CTAbstractCircle {

    private final CTPoint A;
    private final CTPoint B;
    private final CTPoint C;
    private final Shape arcTODraw;

    public static CTCIrcleSector make(CTPoint A, CTPoint B, CTPoint C) {
        CTCIrcleSector resul = new CTCIrcleSector(A, B, C);
        resul.rebuildShape();
        return resul;
    }

    private CTCIrcleSector(CTPoint A, CTPoint B, CTPoint C) {
        this.A = A;
        this.B = B;
        this.C = C;
        arcTODraw=new Shape();
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
    public Shape getMathObject() {
        return arcTODraw;
    }

    @Override
    public CTCIrcleSector copy() {
        CTCIrcleSector copy = CTCIrcleSector.make(A.copy(), B.copy(), C.copy());
        copy.getMp().copyFrom(getMp());
        return copy;
    }

    @Override
    public void copyStateFrom(MathObject obj) {
        if (obj instanceof CTCIrcleSector) {
            CTCIrcleSector cnst = (CTCIrcleSector) obj;
            arcTODraw.getPath().clear();
            Shape copyArc = cnst.getMathObject().copy();
            arcTODraw.getPath().jmPathPoints.addAll(copyArc.getPath().jmPathPoints);
        } 
    }
    
    @Override
    public void rebuildShape() {
        AffineJTransform tr = AffineJTransform.createDirect2DIsomorphic(Point.at(0, 0), Point.at(1, 0), new Point(A.v), new Point(B.v), 1);

        Vec v1 = A.to(B);
        Vec v2 = A.to(C);

        if (!isThisMathObjectFree()) {
            double angle = v2.getAngle() - v1.getAngle();
            if (angle < 0) {
                angle += 2 * PI;
            }
            Shape referenceArc = Shape.arc(angle);

            referenceArc.get(0).cpEnter.copyFrom(referenceArc.get(0).p);
            referenceArc.get(-1).cpExit.copyFrom(referenceArc.get(-1).p);
            referenceArc.get(0).isThisSegmentVisible = true;
            JMPathPoint pp = JMPathPoint.lineTo(Point.origin());
            pp.cpEnter.copyFrom(pp.p);
            pp.cpExit.copyFrom(pp.p);
            referenceArc.getPath().jmPathPoints.add(0, pp);
            referenceArc.applyAffineTransform(tr);
             arcTODraw.getPath().clear();
            arcTODraw.getPath().jmPathPoints.addAll(referenceArc.getPath().jmPathPoints);
        }

    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        dependsOn(scene, A, B, C);
    }
}
