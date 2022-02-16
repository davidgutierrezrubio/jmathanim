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
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Line;
import com.jmathanim.mathobjects.Point;

/**
 * A CTLine that is the angle bisector of 2 other lines or 3 points
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class CTAngleBisector extends CTLine {

    private enum LineType {
        PointPointPoint, LineLine
    }
    private LineType lineType;
    CTPoint C;
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
        resul.lineType = LineType.PointPointPoint;
        resul.rebuildShape();
        return resul;
    }

    private CTAngleBisector(CTPoint A, CTPoint B, CTPoint C) {
        super(A, B);
        this.C = C;
        dirPoint = Point.origin();
        lineToDraw = Line.make(B.getMathObject(), dirPoint);
    }

    @Override
    public CTAngleBisector copy() {
        CTAngleBisector copy = CTAngleBisector.make(A.copy(), B.copy(), C.copy());
        copy.getMp().copyFrom(this.getMp());
        return copy;
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r) {
        lineToDraw.draw(scene, r);

    }

    @Override
    public Line getMathObject() {
        return lineToDraw;
    }

    @Override
    public void rebuildShape() {
        switch (lineType) {
            case PointPointPoint:
                Vec vdir = B.to(A).normalize().add(B.to(C).normalize());
                dirPoint.copyFrom(B.getMathObject().add(vdir));
                break;
            case LineLine:
                //TODO: Implement
                break;
        }
    }
}
