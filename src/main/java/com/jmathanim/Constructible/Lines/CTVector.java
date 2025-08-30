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
import com.jmathanim.Enum.ArrowType;
import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.Arrow;
import com.jmathanim.mathobjects.Coordinates;

/**
 * A constructible vector, which is draw as an Arrow2D MathObject
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class CTVector extends CTAbstractLine<CTVector> {

    protected final Arrow arrowToDraw;

    /**
     * Creates a new CTVector from a Vec object. The created vector is located
     * at (0,0).
     *
     * @param vector The Vec object that determines the CTVector
     * @return The created object
     */
    public static CTVector makeVector(Vec vector) {
        CTVector resul = new CTVector(CTPoint.at(0,0), CTPoint.at(vector.x, vector.y));
        resul.rebuildShape();
        return resul;
    }

    /**
     * Creates a new CTVector from A to B.
     *
     * @param A Origin
     * @param B Destiny
     * @return The created object
     */
    public static CTVector makeVector(Coordinates<?> A, Coordinates<?>  B) {
        CTVector resul = new CTVector(A, B);
        resul.rebuildShape();
        return resul;
    }


    private CTVector(Coordinates<?>  A, Coordinates<?>  B) {
        super(A,B);
        arrowToDraw = Arrow.make(this.P1draw, this.P2draw, ArrowType.ARROW1);
    }

    @Override
    public CTVector copy() {
        CTVector copy = CTVector.makeVector(this.getP1().copy(), this.getP2().copy());
        copy.copyStateFrom(this);
        return copy;
    }

    @Override
    public Arrow getMathObject() {
        return arrowToDraw;
    }

    @Override
    public void rebuildShape() {
        super.rebuildShape();
        if (!isFreeMathObject()) {
            arrowToDraw.getStart().copyCoordinatesFrom(this.P1);
            arrowToDraw.getEnd().copyCoordinatesFrom(this.P2);
        }
        arrowToDraw.rebuildShape();
    }

    @Override
    public String toString() {
        Vec v = getDirection();
        return String.format("CTVector[%.2f, %.2f]", v.x, v.y);
    }

    @Override
    public Vec getHoldCoordinates(Vec coordinates) {
        Vec v1 = getDirection().normalize();
        Vec v2 = coordinates.minus(getP1());
        double dotProd = v1.dot(v2);
        dotProd = Math.max(dotProd, 0);
        dotProd = Math.min(dotProd, getDirection().norm());
        return getP1().add(v1.mult(dotProd)).getVec();
    }
}
