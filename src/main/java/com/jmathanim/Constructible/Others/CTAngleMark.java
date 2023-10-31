/*
 * Copyright (C) 2023 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jmathanim.Constructible.Others;

import com.jmathanim.Constructible.Constructible;
import com.jmathanim.Constructible.Points.CTPoint;
import com.jmathanim.Utils.Vec;
import static com.jmathanim.jmathanim.JMathAnimScene.PI;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class CTAngleMark extends Constructible {

    private boolean isRight;

    double radius;
    private final CTPoint center, A, B;
    private final Shape arcToDraw;

    public static CTAngleMark make(CTPoint center, CTPoint A, CTPoint B) {
        CTAngleMark resul = new CTAngleMark(center, A, B);
        resul.rebuildShape();
        return resul;
    }

    public static CTAngleMark make(Point center, Point A, Point B) {
        return CTAngleMark.make(CTPoint.make(center), CTPoint.make(A), CTPoint.make(B));
    }

    public CTAngleMark(CTPoint center, CTPoint A, CTPoint B) {
        this.center = center;
        this.A = A;
        this.B = B;
        radius = .1;
        arcToDraw = new Shape();
        arcToDraw.style("anglemarkdefault");
        isRight=false;
    }

    @Override
    public Constructible copy() {
        CTAngleMark copy = CTAngleMark.make(center.copy(), A.copy(), B.copy());
        copy.freeMathObject(this.isThisMathObjectFree());
        copy.getMathObject().copyStateFrom(this.getMathObject());
        copy.getMp().copyFrom(this.getMp());
        return copy;
    }

    @Override
    public Shape getMathObject() {
        return arcToDraw;
    }

    public CTAngleMark setIsRight(boolean b) {
        isRight=b;
        return this;
    }


    @Override
    public void rebuildShape() {
        if (isThisMathObjectFree()) {
            return;
        }
        Shape arc;
        JMPath pa = arcToDraw.getPath();
        pa.clear();
//        pa.addPoint(center.getMathObject().copy());
        pa.addPoint(Point.at(center.v));
        Vec v1 = center.to(A).normalize();
        Vec v2 = center.to(B).normalize();
        double dotProduct = v1.dot(v2);
        if ((isRight)||(dotProduct == 0)) {//Right angle
            arc = Shape.polyLine(
                    Point.at(center.v.add(v1.mult(radius))),
                    Point.at(center.v.add(v1.mult(radius)).add(v2.mult(radius))),
                    Point.at(center.v.add(v2.mult(radius)))
            );
        } else {
            double angle = Math.acos(dotProduct);
            arc = Shape.arc(angle)
                    .scale(Point.origin(), radius)
                    .rotate(Point.origin(), v1.getAngle())
                    .shift(center.v);
        }
        arcToDraw.merge(arc, true, true);
        arcToDraw.getPath().distille();
    }

    public double getRadius() {
        return radius;
    }

    public CTAngleMark setRadius(double radius) {
        this.radius = radius;
        rebuildShape();
        return this;
    }

}
