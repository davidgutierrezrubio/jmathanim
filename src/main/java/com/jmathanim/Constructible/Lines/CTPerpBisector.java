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

import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Coordinates;
import com.jmathanim.mathobjects.Line;
import com.jmathanim.mathobjects.MathObject;

/**
 * A perpendicular bisector of a segment (perpendicular line that pass through
 * midpoint)
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class CTPerpBisector extends CTAbstractLine<CTPerpBisector> {

    protected final Coordinates A;
    protected final Coordinates B;
    protected final Line lineToDraw;

    public static CTPerpBisector make(Coordinates A, Coordinates B) {
//        CTPerpBisector resul = CTPerpBisector.make(CTPoint.make(A), CTPoint.make(B));
        CTPerpBisector resul = new CTPerpBisector(A, B);
        resul.rebuildShape();
        return resul;
    }

    public static CTPerpBisector make(CTSegment segment) {
        return make(segment.A, segment.B);
    }

    private CTPerpBisector(Coordinates A, Coordinates B) {
        super();
        this.A = A;
        this.B = B;
        lineToDraw = Line.XAxis();
    }

    @Override
    public CTPerpBisector copy() {
        CTPerpBisector copy = CTPerpBisector.make(A.getVec().copy(), B.getVec().copy());
        copy.copyStateFrom(this);
        return copy;
    }

    @Override
    public void rebuildShape() {
        getP1().copyCoordinatesFrom(A.getVec().interpolate(B, .5));
        Vec v = A.to(B);
        getP2().copyCoordinatesFrom(Vec.to(getP1().x - v.y, getP1().y + v.x));
        if (!isFreeMathObject()) {
            lineToDraw.getP1().copyCoordinatesFrom(P1);
            lineToDraw.getP2().copyCoordinatesFrom(P2);
        }
    }

    @Override
    public MathObject getMathObject() {
        return lineToDraw;
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        dependsOn(scene, this.A, this.B);
    }
//      @Override
//    public Vec getHoldCoordinates(Vec coordinates) {
//        Vec v1 = getDirection().normalize();
//        Vec v2 = coordinates.minus(getP1());
//        return(getP1().add(v1.mult(v1.dot(v2))));
//    }
}
