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

import com.jmathanim.Constructible.Points.CTPoint;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.*;

/**
 * Represents a Circle imported from Geogebra with 2 points (center and another
 * one in the perimeter)
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class CTCircle extends CTAbstractCircle {

    @Override
    public Vec getHoldCoordinates(Vec coordinates) {
        Vec v=coordinates.minus(getCircleCenter().v).normalize().mult(getRadius().value);
        return getCircleCenter().v.add(v);
        
    }

    private enum CircleType {
        THREE_POINTS, CENTER_POINT, CENTER_RADIUS
    }

    private CircleType circleType;
    //Currently Geogebra has these methods to create circles:
    // Circle(point,point)
    // Circle(point,number)
    // Circle(point,Segment)
    // Circle(point,point,point)
    CTPoint A;//Point of the circle
    CTPoint B;
    CTPoint C;
    protected Scalar radius;
    protected CTPoint circleCenter;
    protected final Shape originalCircle;
    protected final Shape circleToDraw;

    /**
     * Creates a constructible circle with given center that pass through P
     *
     * @param center Center of circle
     * @param P Point of circle
     * @return Created constructible circle
     */
    public static CTCircle makeCenterPoint(Point center, Point P) {
        return makeCenterPoint(CTPoint.make(center), CTPoint.make(P));
    }

    /**
     * Creates a constructible circle with given center that pass through P
     *
     * @param center Center of circle
     * @param P Point of circle
     * @return Created constructible circle
     */
    public static CTCircle makeCenterPoint(CTPoint center, CTPoint P) {
        CTCircle resul = new CTCircle();
        resul.circleType = CircleType.CENTER_POINT;
        resul.circleCenter = center;
        resul.A = P;
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
    public static CTCircle makeCenterRadius(Point center, double radius) {
        return makeCenterRadius(CTPoint.make(center), Scalar.make(radius));
    }

    /**
     * Creates a constructible circle with given center and radius
     *
     * @param center Center of circle
     * @param radius Radius of the circle
     * @return Created constructible circle
     */
    public static CTCircle makeCenterRadius(CTPoint center, double radius) {
        return makeCenterRadius(center, Scalar.make(radius));
    }

    /**
     * Creates a constructible circle with given center and radius
     *
     * @param center Center of circle
     * @param radius Radius of the circle
     * @return Created constructible circle
     */
    public static CTCircle makeCenterRadius(CTPoint center, Scalar radius) {
        CTCircle resul = new CTCircle();
        resul.circleType = CircleType.CENTER_RADIUS;
        resul.circleCenter = center;
        resul.radius = radius;
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
    public static CTCircle make3Points(Point A, Point B, Point C) {
        return make3Points(CTPoint.make(A), CTPoint.make(B), CTPoint.make(C));
    }

    /**
     * Creates a constructible circle through 3 given points
     *
     * @param A First point of the circle
     * @param B Second point of the circle
     * @param C Third point of the circle
     * @return Created constructible circle
     */
    public static CTCircle make3Points(CTPoint A, CTPoint B, CTPoint C) {
        CTCircle resul = new CTCircle();
        resul.circleType = CircleType.THREE_POINTS;
        resul.A = A;
        resul.B = B;
        resul.C = C;
        resul.rebuildShape();
        return resul;
    }

    protected CTCircle() {
        super();
        radius = Scalar.make(0);
        originalCircle = Shape.circle();
        circleToDraw = originalCircle.copy();
        circleCenter = CTPoint.make(Point.at(0, 0));
    }

    @Override
    public CTCircle copy() {
        CTCircle copy = null;
        switch (circleType) {
            case CENTER_POINT:
                copy = CTCircle.makeCenterPoint(circleCenter.copy(), A.copy());
                break;
            case THREE_POINTS:
                copy = CTCircle.make3Points(A.copy(), B.copy(), C.copy());
                break;
            case CENTER_RADIUS:
                copy = CTCircle.makeCenterRadius(circleCenter.copy(), radius.copy());
        }
        if (copy != null) {
             copy.copyStateFrom(this);
        }
        return copy;
    }

    @Override
    public void copyStateFrom(MathObject obj) {
        CTCircle c = (CTCircle) obj;
        switch (circleType) {
            case CENTER_POINT:
                this.circleCenter = c.getCircleCenter();
                this.A.copyStateFrom(c.A);
                break;
            case THREE_POINTS:
                this.A.copyStateFrom(c.A);
                this.B.copyStateFrom(c.B);
                this.C.copyStateFrom(c.C);
                break;
            case CENTER_RADIUS:
                this.circleCenter = c.getCircleCenter();
                this.radius.copyStateFrom(c.radius);
        }
        this.circleType = c.circleType;
        super.copyStateFrom(obj);
        rebuildShape();
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        switch (circleType) {
            case CENTER_POINT:
                dependsOn(scene, this.circleCenter, this.A);
                break;
            case THREE_POINTS:
                dependsOn(scene, this.circleCenter, this.A, this.B, this.C);
                break;
            case CENTER_RADIUS:
                dependsOn(scene, this.circleCenter, this.radius);
        }
    }

    @Override
    public Rect computeBoundingBox() {
        rebuildShape();
        return circleToDraw.getBoundingBox();
    }

    @Override
    public Shape getMathObject() {
        return circleToDraw;
    }

    @Override
    public void rebuildShape() {

        computeCircleCenterRadius();
//        circleToDraw.getPath().jmPathPoints.clear();
//        circleToDraw.getPath().addJMPointsFrom(originalCircle.copy().getPath());

        if (!isThisMathObjectFree()) {
            for (int i = 0; i < circleToDraw.size(); i++) {
                JMPathPoint get = circleToDraw.get(i);
                get.copyFrom(originalCircle.get(i));
            }
            circleToDraw.scale(this.radius.value);
            circleToDraw.shift(this.circleCenter.v);
        }
    }

    public void computeCircleCenterRadius() {
        switch (circleType) {
            case CENTER_POINT:
                this.radius.value = circleCenter.getMathObject().to(A.getMathObject()).norm();
                break;
            case THREE_POINTS:
                findCircleThatPassThroughThreePoints(A.v.x, A.v.y, B.v.x, B.v.y, C.v.x, C.v.y);
                break;
            case CENTER_RADIUS:
            //Nothing to do, everything is already calculated!
        }
    }

    @Override
    public Scalar getRadius() {
        return radius;
    }

    @Override
    public CTPoint getCircleCenter() {
        return circleCenter;
    }
//    @Override
//    public int getUpdateLevel() {
//        return Math.max(A.getUpdateLevel(), B.getUpdateLevel()) + 1;
//    }
    // Function to find the circle on
    // which the given three points lie
    //Found in https://www.geeksforgeeks.org/equation-of-circle-when-three-points-on-the-circle-are-given/

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
        this.circleCenter.v.x = h;
        this.circleCenter.v.y = k;
        final Vec radd = A.v.minus(this.circleCenter.v);
        this.radius.value = radd.norm();
        // Center (h,k)
    }
// This code is contributed by chandan_jnu
}
