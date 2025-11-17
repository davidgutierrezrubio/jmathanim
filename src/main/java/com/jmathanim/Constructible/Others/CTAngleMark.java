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
import com.jmathanim.MathObjects.Coordinates;
import com.jmathanim.MathObjects.Shape;
import com.jmathanim.MathObjects.Shapes.JMPath;
import com.jmathanim.MathObjects.Stateable;
import com.jmathanim.Utils.Vec;

/**
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class CTAngleMark extends Constructible<CTAngleMark> {

    private final Coordinates<?> center, A, B;
    private final Shape arcToDraw;
    double radius;
    private boolean isRight;
    private double angle;

    protected CTAngleMark(Coordinates<?> center, Coordinates<?> A, Coordinates<?> B) {
        this.center = center;
        this.A = A;
        this.B = B;
        radius = .1;
        arcToDraw = new Shape();
        arcToDraw.style("anglemarkdefault");
        isRight = false;
    }

    /**
     * Creates a constructible angle mark. The angle is defined by its center and starting and ending points.
     *
     * @param center        Center of the angle
     * @param startingPoint Starting point
     * @param endingPoint   Ending point
     * @return The CTAngle created
     */
    public static CTAngleMark make(Coordinates<?> center, Coordinates<?> startingPoint, Coordinates<?> endingPoint) {
        CTAngleMark resul = new CTAngleMark(center, startingPoint, endingPoint);
        resul.rebuildShape();
        return resul;
    }


    @Override
    public CTAngleMark copy() {
        CTAngleMark copy = CTAngleMark.make(center.copy(), A.copy(), B.copy());
        copy.copyStateFrom(this);
        return copy;
    }

    @Override
    public void copyStateFrom(Stateable obj) {
        if (!(obj instanceof CTAngleMark)) return;
        super.copyStateFrom(obj);
        CTAngleMark ang = (CTAngleMark) obj;
        this.setFreeMathObject(ang.isFreeMathObject());
        this.getMathObject().copyStateFrom(ang.getMathObject());
    }

    @Override
    public Shape getMathObject() {
        return arcToDraw;
    }

    public CTAngleMark setIsRight(boolean b) {
        isRight = b;
        return this;
    }

    @Override
    public void rebuildShape() {
        if (isFreeMathObject()) {
            return;
        }
        Shape arc;
        JMPath pa = arcToDraw.getPath();
        pa.clear();
//        pa.addPoint(center.getMathObject().copy());
        pa.addPoint(center);
        Vec v1 = center.to(A).normalize().scale(radius);
        Vec v2 = center.to(B).normalize().scale(radius);
        double dotProduct = v1.dot(v2);
        if ((isRight) || (dotProduct == 0)) {//Right angle
            arc = Shape.polyLine(
                    center.add(v1),
                    center.add(v1).add(v2),
                    center.add(v2)
            );
        } else {
            angle = Math.acos(dotProduct);
            arc = Shape.arc(angle)
                    .scale(Vec.origin(), radius)
                    .rotate(Vec.origin(), v1.getAngle())
                    .shift(center.getVec());
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

    public double getAngle() {
        return angle;
    }
}
