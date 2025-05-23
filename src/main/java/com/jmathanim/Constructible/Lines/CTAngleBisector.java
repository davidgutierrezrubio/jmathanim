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
package com.jmathanim.Constructible.Lines;

import com.jmathanim.Constructible.Points.CTPoint;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Line;
import com.jmathanim.mathobjects.Point;

/**
 * A CTLine that is the angle bisector of 2 other lines or 3 points
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class CTAngleBisector extends CTAbstractLine {

    private enum LineType {
        PointPointPoint, LineLine
    }
    private LineType bisectorType;
    CTPoint A;
    CTPoint B;
    CTPoint C;
    private final Line lineToDraw;
    Point dirPoint;//The second point of the angle bisector. The first is B

    /**
     * Creates the angle bisector of the angle given by 3 points ABC
     *
     * @param A First point
     * @param B Second point (the vertex of the angle)
     * @param C Third point
     * @return The created object
     */
    public static CTAngleBisector make(Point A, Point B, Point C) {
        return CTAngleBisector.make(CTPoint.make(A), CTPoint.make(B), CTPoint.make(C));
    }

    /**
     * Creates the angle bisector of the angle given by 3 points ABC
     *
     * @param A First point
     * @param B Second point (the vertex of the angle)
     * @param C Third point
     * @return The created object
     */
    public static CTAngleBisector make(CTPoint A, CTPoint B, CTPoint C) {
        CTAngleBisector resul = new CTAngleBisector(A, B, C);
        resul.bisectorType = LineType.PointPointPoint;
        resul.rebuildShape();
        return resul;
    }

    private CTAngleBisector(CTPoint A, CTPoint B, CTPoint C) {
        super();
        this.A = A;
        this.B = B;
        this.C = C;
        dirPoint = Point.origin();
        lineToDraw = Line.make(B.getMathObject(), dirPoint);
    }

    @Override
    public CTAngleBisector copy() {
        CTAngleBisector copy = CTAngleBisector.make(A.copy(), B.copy(), C.copy());
        copy.copyStateFrom(this);
        return copy;
    }

    @Override
    public Line getMathObject() {
        return lineToDraw;
    }

    @Override
    public void rebuildShape() {
        switch (bisectorType) {
            case PointPointPoint:
                Vec vdir = B.to(A).normalize().add(B.to(C).normalize());
                dirPoint.v.copyFrom(B.getMathObject().add(vdir).v);
                P1.v.copyFrom(B.v);
                P2.v.copyFrom(dirPoint.v);
                break;
            case LineLine:
                //TODO: Implement
                break;
        }
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        switch (bisectorType) {
            case PointPointPoint:
                dependsOn(scene, this.A, this.B, this.C);
                break;
            case LineLine:
            //TODO: Implement
        }
    }

    @Override
    public Vec getHoldCoordinates(Vec coordinates) {
        Vec v1 = getDirection().normalize();
        Vec v2 = coordinates.minus(getP1().v);
        return (getP1().v.add(v1.mult(v1.dot(v2))));
    }
}
