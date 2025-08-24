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
import com.jmathanim.Constructible.Points.CTPoint;
import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.Line;

/**
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class CTTangentCircleCircle extends CTAbstractLine<CTTangentCircleCircle> {

    public final CTPoint intersectionTangents;
    private final int numTangent;
    protected CTAbstractCircle<?> c1;
    protected CTAbstractCircle<?> c2;
    protected Line lineToDraw;

    private CTTangentCircleCircle(CTAbstractCircle<?> c1, CTAbstractCircle<?> c2, int numTangent) {
        super();
        this.c1 = c1;
        this.c2 = c2;
        this.numTangent = numTangent;
        this.lineToDraw = Line.XAxis();//Dummy line
        intersectionTangents = CTPoint.at(0, 0);//Dummy point
    }

    public static CTTangentCircleCircle make(CTAbstractCircle<?> c1, CTAbstractCircle<?> c2, int numSolution) {
        CTTangentCircleCircle resul = new CTTangentCircleCircle(c1, c2, numSolution);
        resul.rebuildShape();
        return resul;
    }

    @Override
    public CTTangentCircleCircle copy() {
        CTTangentCircleCircle copy = CTTangentCircleCircle.make(c1.copy(), c2.copy(), numTangent);
        copy.copyStateFrom(this);
        return copy;
    }

    @Override
    public Line getMathObject() {
        return lineToDraw;
    }

    @Override
    public void rebuildShape() {
        double r1 = c1.getRadius().value;
        double r2 = c2.getRadius().value;
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
        Vec v = c1.getCenter().interpolate(c2.getCenter(), baricentricCoordinates);
        intersectionTangents.coordinatesOfPoint.copyCoordinatesFrom(v);
        intersectionTangents.rebuildShape();
        CTTangentPointCircle ct = CTTangentPointCircle.make(intersectionTangents, c1, numTangent % 2);

        this.P1.copyCoordinatesFrom(ct.getP1());
        this.P2.copyCoordinatesFrom(ct.getP2());
        lineToDraw.getP1().copyCoordinatesFrom(this.P1);
        lineToDraw.getP2().copyCoordinatesFrom(this.P2);

    }

}
