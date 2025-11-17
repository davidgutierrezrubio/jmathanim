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
package com.jmathanim.Constructible.Conics;

import com.jmathanim.MathObjects.Coordinates;
import com.jmathanim.MathObjects.Scalar;
import com.jmathanim.MathObjects.Stateable;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;

/**
 * Represents a Circle imported from Geogebra with 2 points (center and another one in the perimeter)
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class CTCircle extends CTAbstractCircle<CTCircle> {

    @Override
    public String toString() {
        return "CTCircle{" +
                "circleType=" + circleType +
                '}';
    }

    //Currently Geogebra has these methods to create circles:
    // Circle(point,point)
    // Circle(point,number)
    // Circle(point,Segment)
    // Circle(point,point,point)
    Vec A;//Point of the circle
    Vec B;
    Vec C;
    private CircleType circleType;


    public CTCircle(Coordinates<?> abstractCircleCenter, Scalar abstractCircleRadius) {
        super(abstractCircleCenter, abstractCircleRadius);
    }



    /**
     * Creates a constructible circle with given center that pass through P
     *
     * @param center Center of circle
     * @param P      Point of circle
     * @return Created constructible circle
     */
    public static CTCircle makeCenterPoint(Coordinates<?> center, Coordinates<?> P) {
        CTCircle resul = new CTCircle(center, Scalar.make(0));
        resul.circleType = CircleType.CENTER_POINT;
        resul.setCircleCenter(center);
        resul.addDependency(center);
        resul.A = P.getVec();
        resul.addDependency(resul.A);
        resul.rebuildShape();
        return resul;
    }

    /**
     * Creates a constructible circle with given center and radius
     *
     * @param center Center of circle
     * @param radius Radius of the circle
     * @return Created constructible circle
     */
    public static CTCircle makeCenterRadius(Coordinates<?> center, double radius) {
        return makeCenterRadius(center, Scalar.make(radius));
    }

    /**
     * Creates a constructible circle with given center and radius
     *
     * @param center Center of circle
     * @param radius Radius of the circle
     * @return Created constructible circle
     */
    public static CTCircle makeCenterRadius(Coordinates<?> center, Scalar radius) {
        CTCircle resul = new CTCircle(center, radius);
        resul.circleType = CircleType.CENTER_RADIUS;
        resul.addDependency(center);
        resul.addDependency(radius);
        resul.rebuildShape();
        return resul;
    }

    /**
     * Creates a constructible circle through 3 given points
     *
     * @param A First point of the circle
     * @param B Second point of the circle
     * @param C Third point of the circle
     * @return Created constructible circle
     */
    public static CTCircle make3Points(Coordinates<?> A, Coordinates<?> B, Coordinates<?> C) {
        CTCircle resul = new CTCircle(Vec.to(0,0), Scalar.make(0));
        resul.circleType = CircleType.THREE_POINTS;
        resul.A = A.getVec();
        resul.B = B.getVec();
        resul.C = C.getVec();
        resul.addDependency(resul.A);
        resul.addDependency(resul.B);
        resul.addDependency(resul.C);
        resul.rebuildShape();
        return resul;
    }

    @Override
    public Vec getHoldCoordinates(Vec coordinates) {
        Vec v = getCircleCenter().to(coordinates).normalize().scale(getCircleRadius().getValue());
        return getCircleCenter().add(v).getVec();

    }

    @Override
    public CTCircle copy() {
        CTCircle copy = null;
        switch (circleType) {
            case CENTER_POINT:
                copy = CTCircle.makeCenterPoint(getCircleCenter().copy(), A.copy());
                break;
            case THREE_POINTS:
                copy = CTCircle.make3Points(A.copy(), B.copy(), C.copy());
                break;
            case CENTER_RADIUS:
                copy = CTCircle.makeCenterRadius(getCircleCenter().copy(), getCircleRadius());
        }
        if (copy != null) {
            copy.copyStateFrom(this);
        }
        return copy;
    }

    @Override
    public void copyStateFrom(Stateable obj) {
        if (!(obj instanceof CTCircle)) return;
        CTCircle c = (CTCircle) obj;
        super.copyStateFrom(c);
        switch (circleType) {
            case CENTER_POINT:
                this.setCircleCenter(c.getCircleCenter());
                this.A.copyCoordinatesFrom(c.A);
                break;
            case THREE_POINTS:
                this.A.copyCoordinatesFrom(c.A);
                this.B.copyCoordinatesFrom(c.B);
                this.C.copyCoordinatesFrom(c.C);
                break;
            case CENTER_RADIUS:
                this.setCircleCenter(c.getCircleCenter());
                this.abstractCircleRadius.copyStateFrom(c.abstractCircleRadius);
        }
        this.circleType = c.circleType;
        super.copyStateFrom(obj);
        rebuildShape();
    }

    @Override
    public Rect computeBoundingBox() {
        rebuildShape();
        return getMathObject().getBoundingBox();
    }

    @Override
    public void rebuildShape() {

        computeCircleCenterRadius();

        if (!isFreeMathObject()) {
            getMathObject().getPath().copyStateFrom(
                    getOriginalUnitCirclePath().copy()
                            .scale(this.getCircleRadius().getValue())
                            .shift(this.getCircleCenter())
            );
        }
    }

    public void computeCircleCenterRadius() {
        switch (circleType) {
            case CENTER_POINT:
                setCircleRadius(getCircleCenter().to(A).norm());
                break;
            case THREE_POINTS:
                findCircleThatPassThroughThreePoints(A.x, A.y, B.x, B.y, C.x, C.y);
                break;
            case CENTER_RADIUS:
                //Nothing to do, everything is already calculated!
        }
    }

    private void findCircleThatPassThroughThreePoints(double x1, double y1, double x2, double y2, double x3, double y3) {
        double x12 = x1 - x2;
        double x13 = x1 - x3;

        double y12 = y1 - y2;
        double y13 = y1 - y3;

        double y31 = y3 - y1;
        double y21 = y2 - y1;

        double x31 = x3 - x1;
        double x21 = x2 - x1;

        // x1^2 - x3^2
        double sx13 = ((x1 * x1) - (x3 * x3));

        // y1^2 - y3^2
        double sy13 = ((y1 * y1) - (y3 * y3));

        double sx21 = ((x2 * x2) - (x1 * x1));

        double sy21 = ((y2 * y2) - (y1 * y1));

        double f = ((sx13) * (x12) + (sy13) * (x12) + (sx21) * (x13) + (sy21) * (x13))
                / (2 * ((y31) * (x12) - (y21) * (x13)));
        double g = ((sx13) * (y12) + (sy13) * (y12) + (sx21) * (y13) + (sy21) * (y13))
                / (2 * ((x31) * (y12) - (x21) * (y13)));

        double c = -(int) (x1 * x1) - (int) (y1 * y1) - 2 * g * x1 - 2 * f * y1;

        // eqn of circle be x^2 + y^2 + 2*g*x + 2*f*y + c = 0
        // where center is (h = -g, k = -f) and radius r
        // as r^2 = h^2 + k^2 - c
        double h = -g;
        double k = -f;

        // r is the radius
//        this.radius = Math.sqrt(sqr_of_r);//this doesn't work
        setCircleCenter(Vec.to(h, k));
        final Vec radd = A.to(this.getCircleCenter());
        this.setCircleRadius(radd.norm());
        // Center (h,k)
    }
    // Function to find the circle on
    // which the given three points lie
    //Found in https://www.geeksforgeeks.org/equation-of-circle-when-three-points-on-the-circle-are-given/

    private enum CircleType {
        THREE_POINTS, CENTER_POINT, CENTER_RADIUS
    }
// This code is contributed by chandan_jnu
}
