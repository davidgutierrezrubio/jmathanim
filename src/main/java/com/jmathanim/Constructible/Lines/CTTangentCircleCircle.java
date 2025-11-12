/*
 * Copyright (C) 2023 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jmathanim.Constructible.Lines;

import com.jmathanim.Constructible.Conics.CTAbstractCircle;
import com.jmathanim.Utils.DependableUtils;
import com.jmathanim.Utils.Vec;

/**
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class CTTangentCircleCircle extends CTAbstractLine<CTTangentCircleCircle> {

    public final Vec intersectionTangents;
    private final int numTangent;
    protected final CTAbstractCircle<?> circle1;
    protected final CTAbstractCircle<?> circle2;

    private CTTangentCircleCircle(CTAbstractCircle<?> circle1, CTAbstractCircle<?> circle2, int numTangent) {
        super(Vec.to(0, 0), Vec.to(1, 0));//Trivial line
        this.circle1 = circle1;
        this.circle2 = circle2;
        addDependency(this.circle1);
        addDependency(this.circle2);
        this.numTangent = numTangent;
        intersectionTangents = Vec.to(0, 0);//Dummy point
    }

    public static CTTangentCircleCircle make(CTAbstractCircle<?> c1, CTAbstractCircle<?> c2, int numSolution) {
        CTTangentCircleCircle resul = new CTTangentCircleCircle(c1, c2, numSolution);
        resul.rebuildShape();
        return resul;
    }

    @Override
    public CTTangentCircleCircle copy() {
        CTTangentCircleCircle copy = CTTangentCircleCircle.make(circle1.copy(), circle2.copy(), numTangent);
        copy.copyStateFrom(this);
        return copy;
    }

    @Override
    public void rebuildShape() {
        double r1 = circle1.getCircleRadius().getValue();
        double r2 = circle2.getCircleRadius().getValue();
        double baricentricCoordinates;
        if ((numTangent == 0) || (numTangent == 1)) {//External tangents
            //If r1>r2, intersection of tangents has baricentric coordinates
            //between centers of c1 and c2
            // r1/(r1-r2)
            baricentricCoordinates = r1 / (r1 - r2);
        } else if ((numTangent == 2) || (numTangent == 3)) {//Internal tangents
            //This center of internal tangents has baricentric coordinates
            //between centers of c1 and c2
            // r1/(r1+r2), r2/(r1+r2)
            baricentricCoordinates = r1 / (r1 + r2);

        } else {
            return;
        }
        Vec v = circle1.getCenter().interpolate(circle2.getCenter(), baricentricCoordinates);
        intersectionTangents.copyCoordinatesFrom(v);

        //TODO: Optimize this. No need to create this object
        CTTangentPointCircle ct = CTTangentPointCircle.make(intersectionTangents, circle1, numTangent % 2);

        this.P1.copyCoordinatesFrom(ct.getP1());
        this.P2.copyCoordinatesFrom(ct.getP2());
        super.rebuildShape();
    }

    @Override
    public boolean needsUpdate() {
        newLastMaxDependencyVersion = DependableUtils.maxVersion(this.circle1, this.circle2, getMp());
        if (dirty) return true;
        return newLastMaxDependencyVersion != lastCleanedDepsVersionSum;
    }

}
