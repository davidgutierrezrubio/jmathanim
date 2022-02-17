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
import com.jmathanim.Constructible.FixedConstructible;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.Rect;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Scalar;
import com.jmathanim.mathobjects.Shape;
import com.jmathanim.mathobjects.updateableObjects.Updateable;

/**
 * Represents a Circle imported from Geogebra with 2 points (center and another
 * one in the perimeter)
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class CTCircle extends FixedConstructible {

    private enum CircleType {
        THREE_POINTS, CENTER_POINT, CENTER_RADIUS
    };
    private CircleType circleType;
    //Currently Geogebra has these methods to create circles:
    // Circle(point,point)
    // Circle(point,number)
    // Circle(point,Segment)
    // Circle(point,point,point)
    CTPoint A;//Point of the circle
    CTPoint B;
    CTPoint C;
    private Scalar radius;
    private CTPoint circleCenter;
    private final Shape originalCircle;
    private final Shape circleToDraw;

    /**
     * Creates a constructible circle with given center that pass through P
     *
     * @param center Center of circle
     * @param P Point of circle
     * @return Created constructible circle
     */
    public static CTCircle make(Point center, Point P) {
        return make(CTPoint.make(center), CTPoint.make(P));
    }

    /**
     * Creates a constructible circle with given center that pass through P
     *
     * @param center Center of circle
     * @param P Point of circle
     * @return Created constructible circle
     */
    public static CTCircle make(CTPoint center, CTPoint P) {
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
    public static CTCircle make(Point center, double radius) {
        return make(CTPoint.make(center), Scalar.make(radius));
    }

    /**
     * Creates a constructible circle with given center and radius
     *
     * @param center Center of circle
     * @param radius Radius of the circle
     * @return Created constructible circle
     */
    public static CTCircle make(CTPoint center, double radius) {
        return make(center, Scalar.make(radius));
    }

    /**
     * Creates a constructible circle with given center and radius
     *
     * @param center Center of circle
     * @param radius Radius of the circle
     * @return Created constructible circle
     */
    public static CTCircle make(CTPoint center, Scalar radius) {
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
    public static CTCircle make(Point A, Point B, Point C) {
        return make(CTPoint.make(A), CTPoint.make(B), CTPoint.make(C));
    }

    /**
     * Creates a constructible circle through 3 given points
     *
     * @param A First point of the circle
     * @param B Second point of the circle
     * @param C Third point of the circle
     * @return Created constructible circle
     */
    public static CTCircle make(CTPoint A, CTPoint B, CTPoint C) {
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
        circleToDraw = new Shape();
        circleCenter = CTPoint.make(Point.at(0, 0));
    }

    @Override
    public CTCircle copy() {
        CTCircle copy = null;
        switch (circleType) {
            case CENTER_POINT:
                copy = CTCircle.make(circleCenter.copy(), A.copy());
                break;
            case THREE_POINTS:
                copy = CTCircle.make(A.copy(), B.copy(), C.copy());
                break;
            case CENTER_RADIUS:
                copy = CTCircle.make(circleCenter.copy(), radius.copy());
        }
        if (copy != null) {
            copy.getMp().copyFrom(this.getMp());
        }
        return copy;
    }

    @Override
    public void copyStateFrom(MathObject obj) {
        CTCircle c = (CTCircle) obj;
        switch (circleType) {
            case CENTER_POINT:
                this.circleCenter = c.circleCenter;
                this.A.copyStateFrom(c.A);
                break;
            case THREE_POINTS:
                this.A.copyStateFrom(c.A);
                this.B.copyStateFrom(c.B);
                this.C.copyStateFrom(c.C);
                break;
            case CENTER_RADIUS:
                this.circleCenter = c.circleCenter;
                this.radius.copyStateFrom(c.radius);
        }
        this.circleType = c.circleType;
        getMp().copyFrom(c.getMp());
        rebuildShape();
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        switch (circleType) {
            case CENTER_POINT:
                scene.registerUpdateable(this.circleCenter, this.A);
                break;
            case THREE_POINTS:
                scene.registerUpdateable(this.A, this.B, this.C);
                break;
            case CENTER_RADIUS:
                scene.registerUpdateable(this.circleCenter, this.radius);

        }
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r) {
        circleToDraw.draw(scene, r);
    }

    @Override
    public Rect getBoundingBox() {
        rebuildShape();
        return circleToDraw.getBoundingBox();
    }

    @Override
    public Shape getMathObject() {
        return circleToDraw;
    }

    @Override
    public final void rebuildShape() {

        computeCircleCenterRadius();
        circleToDraw.getPath().jmPathPoints.clear();
        circleToDraw.getPath().addJMPointsFrom(originalCircle.copy().getPath());
        circleToDraw.scale(this.radius.value);
        circleToDraw.shift(this.circleCenter.v);

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

    public Scalar getRadius() {
        return radius;
    }

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
        this.radius.value = this.circleCenter.getMathObject().to(A.getMathObject()).norm();
        // Center (h,k)
    }
// This code is contributed by chandan_jnu
}
