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

import com.jmathanim.MathObjects.Coordinates;
import com.jmathanim.Utils.DependableUtils;
import com.jmathanim.Utils.Vec;

/**
 * A perpendicular bisector of a segment (perpendicular line that pass through midpoint)
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class CTPerpBisector extends CTAbstractLine<CTPerpBisector> {

    private final Vec A, B;

    private CTPerpBisector(Coordinates<?> A, Coordinates<?> B) {
        super(Vec.to(0, 0), Vec.to(1, 0));
        this.A = A.getVec();
        this.B = B.getVec();

        rebuildShape();
    }

    public static CTPerpBisector make(Coordinates<?> A, Coordinates<?> B) {
//        CTPerpBisector resul = CTPerpBisector.makeLengthMeasure(CTPoint.makeLengthMeasure(A), CTPoint.makeLengthMeasure(B));
        CTPerpBisector resul = new CTPerpBisector(A, B);
        resul.rebuildShape();
        return resul;
    }

    public static CTPerpBisector make(CTSegment segment) {
        return make(segment.getP1(), segment.getP2());
    }

    @Override
    public CTPerpBisector copy() {
        CTPerpBisector copy = CTPerpBisector.make(getP1().getVec().copy(), getP2().getVec().copy());
        copy.copyStateFrom(this);
        return copy;
    }

    @Override
    public void rebuildShape() {

        Vec midPoint = A.interpolate(B, .5);
        Vec v = A.to(B);
//        getP2().copyCoordinatesFrom(Vec.to(getP1().x - v.y, getP1().y + v.x));
        Vec vOrthogonal = Vec.to(midPoint.x - v.y, midPoint.y + v.x);
        P1.copyCoordinatesFrom(midPoint);
        P2.copyCoordinatesFrom(vOrthogonal);
        super.rebuildShape();
    }

    @Override
    public boolean needsUpdate() {
        newLastMaxDependencyVersion = DependableUtils.maxVersion(this.A, this.B, getMp());
        if (dirty) return true;
        return newLastMaxDependencyVersion != lastCleanedDepsVersionSum;
    }

    @Override
    public String toString() {
        return String.format("'%s' %s of  %s and %s",
                getObjectLabel(),
                getClass().getSimpleName(),
                A.toString(),
                B.toString());
    }
}
