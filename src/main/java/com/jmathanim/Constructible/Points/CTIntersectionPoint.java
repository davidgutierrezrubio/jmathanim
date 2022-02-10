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
import com.jmathanim.Constructible.Constructible;
import com.jmathanim.Constructible.FixedConstructible;
import com.jmathanim.Constructible.Lines.CTLine;
import com.jmathanim.Constructible.Lines.CTRay;
import com.jmathanim.Constructible.Lines.CTSegment;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Line;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MathObjectGroup;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Ray;

/**
 * Represents an intersection point of lines, rays, or circles
 *
 * @author David
 */
public class CTIntersectionPoint extends CTPoint {

    private enum IntersectionType {
        LINEAR, LINE_CIRCLE, CIRCLE_CIRCLE
    }
    private IntersectionType intersectionType;
    private final CTPoint intersectionPoint;
    private CTLine ctline1, ctline2;
    private CTCircle ctcircle1, ctcircle2;
    private final Constructible c1, c2;
    private final int solNumber;

    public static CTIntersectionPoint make(Constructible c1, Constructible c2) {
        return make(c1, c2, 1);
    }

    public static CTIntersectionPoint make(Constructible c1, Constructible c2, int solNumber) {
        CTIntersectionPoint resul = new CTIntersectionPoint(c1, c2, solNumber);
        resul.rebuildShape();
        return resul;
    }

    private CTIntersectionPoint(Constructible c1, Constructible c2, int solNumber) {
        intersectionPoint = CTPoint.make(Point.at(0, 0));
        this.solNumber = solNumber;
        this.c1 = c1;
        this.c2 = c2;
        //Determine intersecion type and define proper variables
        if ((c1 instanceof CTLine) && (c2 instanceof CTLine)) {
            ctline1 = (CTLine) c1;
            ctline2 = (CTLine) c2;
            ctcircle1 = null;
            ctcircle2 = null;
            intersectionType = IntersectionType.LINEAR;
        } else if ((c1 instanceof CTLine) && (c2 instanceof CTCircle)) {
            ctline1 = (CTLine) c1;
            ctline2 = null;
            ctcircle1 = (CTCircle) c2;
            ctcircle2 = null;
            intersectionType = IntersectionType.LINE_CIRCLE;
        } else if ((c1 instanceof CTCircle) && (c2 instanceof CTLine)) {
            ctline1 = (CTLine) c2;
            ctline2 = null;
            ctcircle1 = (CTCircle) c1;
            ctcircle2 = null;
            intersectionType = IntersectionType.LINE_CIRCLE;
        } else if ((c1 instanceof CTCircle) && (c2 instanceof CTCircle)) {
            ctline1 = null;
            ctline2 = null;
            ctcircle1 = null;
            ctcircle2 = null;
            intersectionType = IntersectionType.CIRCLE_CIRCLE;
            JMathAnimScene.logger.error("Don't know still how to compute intersection of 2 circles");
        } else {
            JMathAnimScene.logger.error("Don't know this intersection: "+c1+", "+c2);
        }
    }

    @Override
    public Point getMathObject() {
        return intersectionPoint.getMathObject();
    }

    @Override
    public void rebuildShape() {
        double interX = Double.NaN;
        double interY = Double.NaN;//Result
        //TODO: Implement intersection algorithms for:
        //Circle
        intersectionPoint.getMathObject().copyFrom(Point.at(0, .5));//Debug values to show on screen
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
                    interX = x1 + sols[0] * (x2 - x1);
                    interY = y1 + sols[0] * (y2 - y1);
                }
//        double interX2 = x3 + sols[1] * (x4 - x3);
//        double interY2 = y3 + sols[1] * (y4 - y3);
                intersectionPoint.getMathObject().v.copyFrom(interX, interY);
                break;
            case LINE_CIRCLE:
                //A line/ray/segment with a circle
                double radius = ctcircle1.getRadius().value;
                Vec center = ctcircle1.getCircleCenter().getMathObject().v;

                Point A = ctline1.getP1().copy().shift(center.mult(-1));
                Point B = ctline1.getP2().copy().shift(center.mult(-1));

                double dx = A.to(B).x;
                double dy = A.to(B).y;
                double drSq = dx * dx + dy * dy;
                double D = A.v.x * B.v.y - B.v.x * A.v.y;
                final double discr = Math.sqrt(radius * radius * drSq - D * D);
                if (discr < 0) {
                    intersectionPoint.getMathObject().v.copyFrom(Double.NaN, Double.NaN);
                } else {
                    //Coordinates of 2 intersection points
                    x1 = (D * dy - (dy < 0 ? -1 : 1) * dx * discr) / drSq;
                    y1 = (-D * dx - Math.abs(dy) * discr) / drSq;

                    x2 = (D * dy + (dy < 0 ? -1 : 1) * dx * discr) / drSq;
                    y2 = (-D * dx + Math.abs(dy) * discr) / drSq;
                    int sign = (this.solNumber == 1 ? 1 : -1);
                    //TODO:Determine the nearest solution to A
                    if (sign * ((x1 - A.v.x) * (x1 - A.v.x) + (y1 - A.v.y) * (y1 - A.v.y)) < sign * ((x1 - B.v.x) * (x1 - B.v.x) + (y1 - B.v.y) * (y1 - B.v.y))) {
                        intersectionPoint.getMathObject().v.copyFrom(x1, y1);
                        intersectionPoint.getMathObject().shift(center);
                    } else {
                        intersectionPoint.getMathObject().v.copyFrom(x2, y2);
                        intersectionPoint.getMathObject().shift(center);
                    }
                }
                break;
            case CIRCLE_CIRCLE:
                //Not yet...
        }
    }

    @Override
    public CTIntersectionPoint copy() {
        CTIntersectionPoint copy = make(c1.copy(), c2.copy());
        copy.getMp().copyFrom(this.getMp());
        return copy;
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r
    ) {
        intersectionPoint.draw(scene, r);
    }

    public double[] BezierIntersect(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        double[] solutions = new double[2];
        double det = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        solutions[0] = ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / det;
        solutions[1] = ((x1 - x3) * (y1 - y2) - (y1 - y3) * (x1 - x2)) / det;
        return solutions;
    }

}
