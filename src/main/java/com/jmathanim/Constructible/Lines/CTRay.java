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
import com.jmathanim.MathObjects.Shapes.Ray;
import com.jmathanim.Utils.Vec;

/**
 * A Constructible ray
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class CTRay extends CTAbstractLine<CTRay> {

    private final Ray rayToDraw;
    private HasDirection dir;

    private CTRay(Coordinates<?> A, Coordinates<?> B) {
        super(A, B);
        rayToDraw = Ray.make(P1draw, P2draw);
    }
    /**
     * Creates a new Constructible ray with given point and direction
     *
     * @param A   Starting point
     * @param dir Direction, given by any object that implements the interface HasDirection
     * @return The created object
     */
    public static CTRay makePointDir(Coordinates<?> A, HasDirection dir) {
        CTRay resul = new CTRay(A, A.add(dir.getDirection()));
        resul.dir = dir;
        resul.lineType = LineType.POINT_DIRECTION;
        resul.rebuildShape();
        return resul;
    }

    /**
     * Creates a new CTRay from a Ray object
     *
     * @param ray Ray to wrap into
     * @return The CTRay object created
     */
    public static CTRay make(Ray ray) {
        return make(ray.getP1(), ray.getP2());
    }

    /**
     * Creates a new Constructible ray with given 2 points
     *
     * @param A Starting point
     * @param B Second point
     * @return The created object
     */
    public static CTRay make(Coordinates<?> A, Coordinates<?> B) {
        CTRay resul = new CTRay(A, B);
        resul.lineType = LineType.POINT_POINT;
        resul.rebuildShape();
        return resul;
    }

    @Override
    public CTRay copy() {
        CTRay copy = CTRay.make(getP1().getVec().copy(), getP2().getVec().copy());
        copy.copyStateFrom(this);
        return copy;
    }

    @Override
    public Ray getMathObject() {
        return rayToDraw;
    }

    @Override
    public void rebuildShape() {
        if (!isFreeMathObject()) {
            this.P1draw.copyCoordinatesFrom(P1.getVec());
            switch (lineType) {
                case POINT_POINT:
                    this.P2draw.copyCoordinatesFrom(P2.getVec());
                    break;
                case POINT_DIRECTION:
                    this.P2draw.copyCoordinatesFrom(this.P1.add(dir.getDirection()));
            }
        }
        rayToDraw.rebuildShape();
    }

    @Override
    public Vec getDirection() {
        return rayToDraw.getDirection();
    }



    @Override
    public Vec getHoldCoordinates(Vec coordinates) {
        Vec v1 = getDirection().normalize();
        Vec v2 = coordinates.minus(getP1());
        double dotProd = v1.dot(v2);
        dotProd = Math.max(dotProd, 0);
        return getP1().add(v1.mult(dotProd)).getVec();
    }
}
