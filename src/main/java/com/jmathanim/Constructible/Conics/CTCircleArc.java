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
 * A Constructible circle arc
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class CTCircleArc extends CTAbstractCircle {

    private final CTPoint center;
    private final CTPoint A;
    private final CTPoint B;
    private Shape arcTODraw;

    /**
     * Creates a new Constructible circle arc
     *
     * @param center Center of arc
     * @param A Starting point. Arc will pass through this point
     * @param B Point that determines the angle of the arc.
     * @return The created arc
     */
    public static CTCircleArc make(CTPoint center, CTPoint A, CTPoint B) {
        CTCircleArc resul = new CTCircleArc(center, A, B);
        resul.rebuildShape();
        return resul;
    }

    /**
     * Overloaded method. Creates a new Constructible circle arc
     *
     * @param center Center of arc
     * @param A Starting point. Arc will pass through this point
     * @param B Point that determines the angle of the arc.
     * @return The created arc
     */
    public static CTCircleArc make(Point center, Point A, Point B) {
        CTCircleArc resul = new CTCircleArc(CTPoint.make(center), CTPoint.make(A), CTPoint.make(B));
        resul.rebuildShape();
        return resul;
    }

    private CTCircleArc(CTPoint center, CTPoint A, CTPoint B) {
        this.center = center;
        this.A = A;
        this.B = B;
    }

    @Override
    public CTPoint getCircleCenter() {
        return center.copy();
    }

    @Override
    public Scalar getRadius() {
        return Scalar.make(center.to(A).norm());
    }

    @Override
    public MathObject getMathObject() {
        return arcTODraw;
    }

    @Override
    public Constructible copy() {
        CTCircleArc copy = CTCircleArc.make(center.copy(), A.copy(), B.copy());
        copy.copyStateFrom(this);
        return copy;
    }

    @Override
    public void copyStateFrom(MathObject obj) {
        if (obj instanceof CTCircleArc) {
            CTCircleArc cnst = (CTCircleArc) obj;
            this.center.copyStateFrom(cnst.center);
            this.A.copyStateFrom(cnst.A);
            this.B.copyStateFrom(cnst.B);
        }
        super.copyStateFrom(obj);
    }

    @Override
    public void rebuildShape() {
        AffineJTransform tr = AffineJTransform.createDirect2DIsomorphic(Point.at(0, 0), Point.at(1, 0), new Point(center.v), new Point(A.v), 1);

        Vec v1 = center.to(A);
        Vec v2 = center.to(B);

        if (!isThisMathObjectFree()) {
            double angle = v2.getAngle() - v1.getAngle();
            if (angle < 0) {
                angle += 2 * PI;
            }
            arcTODraw = Shape.arc(angle);
            arcTODraw.applyAffineTransform(tr);
        }

    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        dependsOn(scene, center, A, B);
    }

}
