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

import com.jmathanim.Constructible.Constructible;
import com.jmathanim.Constructible.FixedConstructible;
import com.jmathanim.Constructible.Lines.CTLine;
import com.jmathanim.Constructible.Lines.CTRay;
import com.jmathanim.Constructible.Lines.CTSegment;
import com.jmathanim.Renderers.Renderer;
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
public class CTIntersectionPoint extends FixedConstructible {

    private final CTPoint intersectionPoint;
    private final Constructible c2;
    private final Constructible c1;

    public static CTIntersectionPoint make(Constructible c1, Constructible c2) {
        CTIntersectionPoint resul = new CTIntersectionPoint(c1, c2);
        resul.rebuildShape();
        return resul;
    }

    private CTIntersectionPoint(Constructible c1, Constructible c2) {
        this.c1 = c1;
        this.c2 = c2;
        intersectionPoint = CTPoint.make(Point.at(0, 0));
    }

    @Override
    public MathObject getMathObject() {
        return intersectionPoint;
    }

    @Override
    public void rebuildShape() {
        double interX = Double.NaN;
        double interY = Double.NaN;//Result
        //TODO: Implement intersection algorithms for:
        //Line-Line
        //Line-Ray
        //Line-Segment
        //Segment-Ray
        //Segment-Segment
        //Circle
        intersectionPoint.getMathObject().copyFrom(Point.at(0, .5));//Debug values to show on screen
        double x1, x2, x3, x4, y1, y2, y3, y4;
        if ((c1 instanceof CTLine) && (c2 instanceof CTLine)) {
            CTLine l1 = (CTLine) c1;
            CTLine l2 = (CTLine) c2;
            x1 = l1.getP1().v.x;
            x2 = l1.getP2().v.x;
            x3 = l2.getP1().v.x;
            x4 = l2.getP2().v.x;

            y1 = l1.getP1().v.y;
            y2 = l1.getP2().v.y;
            y3 = l2.getP1().v.y;
            y4 = l2.getP2().v.y;

            double sols[] = BezierIntersect(x1, y1, x2, y2, x3, y3, x4, y4);

            //Consider cases of different intersecting objects
            boolean intersect = true;
            intersect = intersect && !((c1 instanceof CTRay) && (sols[0] < 0));
            intersect = intersect && !((c2 instanceof CTRay) && (sols[1] < 0));
            intersect = intersect && !((c1 instanceof CTSegment) && ((sols[0] < 0) || (sols[0] > 1)));
            intersect = intersect && !((c2 instanceof CTSegment) && ((sols[1] < 0) || (sols[1] > 1)));

            if (intersect) {
                interX = x1 + sols[0] * (x2 - x1);
                interY = y1 + sols[0] * (y2 - y1);
            }
//        double interX2 = x3 + sols[1] * (x4 - x3);
//        double interY2 = y3 + sols[1] * (y4 - y3);
            intersectionPoint.getMathObject().v.copyFrom(interX, interY);
        }
    }

    @Override
    public CTIntersectionPoint copy() {
        return make(c1.copy(), c2.copy());
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r) {
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
