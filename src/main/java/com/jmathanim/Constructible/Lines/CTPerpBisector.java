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
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Line;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.updaters.Coordinates;

/**
 * A perpendicular bisector of a segment (perpendicular line that pass through
 * midpoint)
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class CTPerpBisector extends CTAbstractLine {

    protected final CTPoint A;
    protected final CTPoint B;
    protected final Line lineToDraw;

    public static CTPerpBisector make(Coordinates A, Coordinates B) {
        CTPerpBisector resul = CTPerpBisector.make(CTPoint.make(A), CTPoint.make(B));
        resul.rebuildShape();
        return resul;
    }

    public static CTPerpBisector make(CTSegment segment) {
        return make(segment.A, segment.B);
    }

    private CTPerpBisector(CTPoint A, CTPoint B) {
        super();
        this.A = A;
        this.B = B;
        lineToDraw = Line.XAxis();
    }

    @Override
    public CTPerpBisector copy() {
        CTPerpBisector copy = CTPerpBisector.make(A.copy(), B.copy());
        copy.copyStateFrom(this);
        return copy;
    }

    @Override
    public void rebuildShape() {
        getP1().v.copyFrom(A.v.interpolate(B.v, .5));
        Vec v = A.to(B);
        getP2().v.copyFrom(Vec.to(getP1().v.x - v.y, getP1().v.y + v.x));
        if (!isFreeMathObject()) {
            lineToDraw.getP1().v.copyFrom(P1.v);
            lineToDraw.getP2().v.copyFrom(P2.v);
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
      @Override
    public Vec getHoldCoordinates(Vec coordinates) {
        Vec v1 = getDirection().normalize();
        Vec v2 = coordinates.minus(getP1().v);
        return(getP1().v.add(v1.mult(v1.dot(v2))));
    }
}
