/*
 * Copyright (C) 2022 David
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
package com.jmathanim.Constructible.Points;

import com.jmathanim.Constructible.Conics.CTCircle;
import com.jmathanim.Constructible.Conics.CTEllipse;
import com.jmathanim.Constructible.Constructible;
import com.jmathanim.Constructible.Lines.CTAbstractLine;
import com.jmathanim.Constructible.Lines.CTLine;
import com.jmathanim.Constructible.Lines.CTRay;
import com.jmathanim.Constructible.Lines.CTSegment;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Line;
import com.jmathanim.mathobjects.Point;

/**
 * Represents an intersection point of lines, rays, or circles
 *
 * @author David
 */
public class CTIntersectionPoint extends CTPoint {

    private enum IntersectionType {
        LINEAR, LINE_CIRCLE, CIRCLE_CIRCLE, CIRCLE_CONIC
    }
    private IntersectionType intersectionType;
    private CTAbstractLine ctline1, ctline2;
    private CTCircle ctcircle1, ctcircle2;
    private final Constructible c1, c2;
    private final int solNumber;

    /**
     * Creates the constructible intersection of 2 lines
     * @param l1 Line 1
     * @param l2 Line 2
     * @return The object created
     */
    public static CTIntersectionPoint make(Line l1, Line l2) {
        return make(CTLine.make(l1), CTLine.make(l2), 0);
    }

    /**
     * Overloaded method. Compute the first intersection point between 2
     * constructible objects. Curent version allows CTCircle, CTLine, CTRay and
     * CTSegment
     *
     * @param c1 First Constructible to intersect.
     * @param c2 Second Constructible to intersect.
     * @return The intersection point created. If the objects do not have
     * intersection at all, the point is created with coordinates NaN
     */
    public static CTIntersectionPoint make(Constructible c1, Constructible c2) {
        return make(c1, c2, 0);
    }

    /**
     * Compute the n-th intersection point between 2 constructible objects.
     * Current version allows CTCircle, CTLine, CTRay and CTSegment
     *
     * @param c1 First Constructible to intersect.
     * @param c2 Second Constructible to intersect.
     * @param solNumber Solution number. For 2 lines, rays, or segments it
     * should be always 9. For lines and circle, may be 0 or 1.
     * @return The intersection point created. If the objects do not have
     * intersection at all, the point is created with coordinates NaN
     */
    public static CTIntersectionPoint make(Constructible c1, Constructible c2, int solNumber) {
        CTIntersectionPoint resul = new CTIntersectionPoint(c1, c2, solNumber);
        resul.rebuildShape();
        return resul;
    }

    private CTIntersectionPoint(Constructible c1, Constructible c2, int solNumber) {
        super();
        this.solNumber = solNumber;
        this.c1 = c1;
        this.c2 = c2;
        //Determine intersection type and define proper variables
        if ((c1 instanceof CTAbstractLine) && (c2 instanceof CTAbstractLine)) {
            ctline1 = (CTAbstractLine) c1;
            ctline2 = (CTAbstractLine) c2;
            ctcircle1 = null;
            ctcircle2 = null;
            intersectionType = IntersectionType.LINEAR;
        } else if ((c1 instanceof CTAbstractLine) && (c2 instanceof CTCircle)) {
            ctline1 = (CTAbstractLine) c1;
            ctline2 = null;
            ctcircle1 = (CTCircle) c2;
            ctcircle2 = null;
            intersectionType = IntersectionType.LINE_CIRCLE;//TODO: consider SEGMENT_CIRCLE
        } else if ((c1 instanceof CTCircle) && (c2 instanceof CTAbstractLine)) {
            ctline1 = (CTAbstractLine) c2;
            ctline2 = null;
            ctcircle1 = (CTCircle) c1;
            ctcircle2 = null;
            intersectionType = IntersectionType.LINE_CIRCLE;
        } else if ((c1 instanceof CTCircle) && (c2 instanceof CTCircle)) {
            ctline1 = null;
            ctline2 = null;
            ctcircle1 = (CTCircle) c1;
            ctcircle2 = (CTCircle) c2;
            intersectionType = IntersectionType.CIRCLE_CIRCLE;
        } else if ((c1 instanceof CTCircle) && (c2 instanceof CTEllipse)) {
            intersectionType = IntersectionType.CIRCLE_CONIC;
            JMathAnimScene.logger.error("Don't know still how to compute intersection of 2 ellipses");
        } else if ((c1 instanceof CTEllipse) && (c2 instanceof CTEllipse)) {
            intersectionType = IntersectionType.CIRCLE_CONIC;
            JMathAnimScene.logger.error("Don't know still how to compute intersection of 2 ellipses");
        } else {
            JMathAnimScene.logger.error("Don't know this intersection: " + c1 + ", " + c2);
        }
    }

    @Override
    public void rebuildShape() {
        Vec inter=Vec.to(Double.NaN, Double.NaN);//Default result: no point at all
        if (intersectionType == null) {
            this.v.copyFrom(inter);
            if (!isThisMathObjectFree()) {
                getMathObject().v.copyFrom(this.v);
                return;
            }
        }
        //TODO: Implement intersection algorithms for:
        //Circle
        double x1, x2, x3, x4, y1, y2, y3, y4;
        switch (intersectionType) {
            case LINEAR:
                x1 = ctline1.getP1().v.x;
                x2 = ctline1.getP2().v.x;
                x3 = ctline2.getP1().v.x;
                x4 = ctline2.getP2().v.x;

                y1 = ctline1.getP1().v.y;
                y2 = ctline1.getP2().v.y;
                y3 = ctline2.getP1().v.y;
                y4 = ctline2.getP2().v.y;

                double sols[] = BezierIntersect(x1, y1, x2, y2, x3, y3, x4, y4);

                //Consider cases of different intersecting objects
                boolean intersect = true;
                intersect = intersect && !((ctline1 instanceof CTRay) && (sols[0] < 0));
                intersect = intersect && !((ctline2 instanceof CTRay) && (sols[1] < 0));
                intersect = intersect && !((ctline1 instanceof CTSegment) && ((sols[0] < 0) || (sols[0] > 1)));
                intersect = intersect && !((ctline2 instanceof CTSegment) && ((sols[1] < 0) || (sols[1] > 1)));

                if (intersect) {
                    inter.x = x1 + sols[0] * (x2 - x1);
                    inter.y = y1 + sols[0] * (y2 - y1);
                }
//        double interX2 = x3 + sols[1] * (x4 - x3);
//        double interY2 = y3 + sols[1] * (y4 - y3);
                this.v.copyFrom(inter);
                break;
            case LINE_CIRCLE:
                //A line/ray/segment with a circle
                double radius = ctcircle1.getRadius().value;
                Vec center = ctcircle1.getCircleCenter().v;

                Point A = ctline1.getP1().copy().shift(center.mult(-1));
                Point B = ctline1.getP2().copy().shift(center.mult(-1));

                double dx = A.to(B).x;
                double dy = A.to(B).y;
                double drSq = dx * dx + dy * dy;
                double D = A.v.x * B.v.y - B.v.x * A.v.y;
                final double discr = Math.sqrt(radius * radius * drSq - D * D);
                if (discr < 0) {
                    inter.x = Double.NaN;
                    inter.y = Double.NaN;
                } else {
                    //Coordinates of 2 intersection points
                    x1 = (D * dy - (dy < 0 ? -1 : 1) * dx * discr) / drSq;
                    y1 = (-D * dx - Math.abs(dy) * discr) / drSq;

                    x2 = (D * dy + (dy < 0 ? -1 : 1) * dx * discr) / drSq;
                    y2 = (-D * dx + Math.abs(dy) * discr) / drSq;
                    int sign = (this.solNumber == 0 ? 1 : -1);
                    //TODO:Determine the nearest solution to A
                    if (sign * ((x1 - A.v.x) * (x1 - A.v.x) + (y1 - A.v.y) * (y1 - A.v.y)) < sign * ((x1 - B.v.x) * (x1 - B.v.x) + (y1 - B.v.y) * (y1 - B.v.y))) {
                        inter.x = x1;
                        inter.y = y1;
                    } else {
                        inter.x = x2;
                        inter.y = y2;
                    }
                    if (!validateSolutionForLines(ctline1, inter)) {
                        inter.x = Double.NaN;
                        inter.y = Double.NaN;
                    }
                }
                this.v.copyFrom(inter);
                this.v.addInSite(center);
                break;
            case CIRCLE_CIRCLE:
                final Vec vecCenterCircles = ctcircle1.getCircleCenter().to(ctcircle2.getCircleCenter()).copy();
                double d = vecCenterCircles.norm();
                double r1 = ctcircle1.getRadius().value;
                double r2 = ctcircle2.getRadius().value;
                double alpha = .5 / d;
                inter.x = alpha * (d * d - r2 * r2 + r1 * r1);

                inter.y= alpha * Math.sqrt((-d + r2 - r1) * (-d - r2 + r1) * (-d + r2 + r1) * (d + r2 + r1));
                Point p = Point.origin();
                p.v.copyFrom(Vec.to(inter.x, (solNumber == 0 ? 1 : -1) * inter.y));
                p.rotate(Point.origin(), vecCenterCircles.getAngle());
                p.shift(ctcircle1.getCircleCenter().v);
                this.v.copyFrom(p.v);
                break;
            case CIRCLE_CONIC:
            //Not implemented yet. Returns a NaN point
        }
        if (!isThisMathObjectFree()) {
            p.v.copyFrom(this.v);
        }

    }

    @Override
    public void update(JMathAnimScene scene) {
        rebuildShape();
    }

    //Determines if given point P of line/ray/segment is valid or not
    private boolean validateSolutionForLines(CTAbstractLine line, Vec v) {
        boolean valid = true;
        Point A = line.getP1();
        Point B = line.getP2();
        Vec vLine = A.to(B);
        Vec vP = v.minus(A.v);
                //Vec.to(x - A.v.x, y - A.v.y);
        double lambda;
        if (vLine.x != 0) {
            lambda = vP.x / vLine.x;
        } else {
            lambda = vP.y / vLine.y;
        }
        valid = valid && !((line instanceof CTRay) && (lambda < 0));
        valid = valid && !((line instanceof CTSegment) && ((lambda < 0) || (lambda > 1)));
        return valid;
    }

    @Override
    public CTIntersectionPoint copy() {
        CTIntersectionPoint copy = make((Constructible) c1.copy(), (Constructible) c2.copy());
        copy.getMp().copyFrom(this.getMp());
        copy.getMathObject().copyFrom(this.getMathObject());
        copy.freeMathObject(this.isThisMathObjectFree());
        return copy;
    }

    private double[] BezierIntersect(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        double[] solutions = new double[2];
        double det = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        solutions[0] = ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / det;
        solutions[1] = ((x1 - x3) * (y1 - y2) - (y1 - y3) * (x1 - x2)) / det;
        return solutions;
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        switch (intersectionType) {
            case CIRCLE_CIRCLE:
                dependsOn(scene, ctcircle1, ctcircle1);
                break;
            case CIRCLE_CONIC:
                //Not implemented yet...
                setUpdateLevel(0);
                break;
            case LINEAR:
                dependsOn(scene, ctline1, ctline2);
                break;
            case LINE_CIRCLE:
                dependsOn(scene, ctline1, ctcircle1);
        }
    }

//    @Override
//    public Constructible applyAffineTransform(AffineJTransform transform) {
//        getMathObject().applyAffineTransform(transform);
//        return this;
//    }
}
