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
import com.jmathanim.mathobjects.Point;

/**
 * A perpendicular bisector of a segment (perpendicular line that pass through
 * midpoint)
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class CTPerpBisector extends CTLine {

    public static CTPerpBisector make(Point A, Point B) {
        CTPerpBisector resul = CTPerpBisector.makePerpBisector(CTPoint.make(A), CTPoint.make(B));
        resul.rebuildShape();
        return resul;
    }

    public static CTPerpBisector makePerpBisector(CTPoint A, CTPoint B) {
        CTPerpBisector resul = new CTPerpBisector(A, B);
        resul.rebuildShape();
        return resul;
    }

    public static CTPerpBisector make(CTSegment segment) {
        return makePerpBisector(segment.A, segment.B);
    }

    private CTPerpBisector(CTPoint A, CTPoint B) {
        super(A, B);
        this.lineType = LineType.PointPoint;
    }

    @Override
    public CTPerpBisector copy() {
        CTPerpBisector copy = CTPerpBisector.makePerpBisector(A.copy(), B.copy());
        copy.getMp().copyFrom(this.getMp());
        return copy;
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r) {
        lineToDraw.draw(scene, r);
    }

    @Override
    public void rebuildShape() {
        getP1().v.copyFrom(A.v.interpolate(B.v, .5));
        Vec v = A.to(B);
        getP2().v.copyFrom(getP1().v.x - v.y, getP1().v.y + v.x);
        if (!isThisMathObjectFree()) {
            lineToDraw.getP1().v.copyFrom(P1.v);
            lineToDraw.getP2().v.copyFrom(P2.v);
        }
    }

}
