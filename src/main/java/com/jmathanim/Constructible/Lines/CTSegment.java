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
import com.jmathanim.mathobjects.Shape;
import com.jmathanim.mathobjects.hasScalarParameter;

/**
 * A straight segment,given by 2 points
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class CTSegment extends CTAbstractLine<CTSegment> implements hasScalarParameter {

    protected final Coordinates B;
    protected final Coordinates A;
    private final Shape segmentToDraw;


    /**
     * Creates a Constructible segment between 2 points
     *
     * @param A First point
     * @param B Second point
     * @return The created object
     */
    public static CTSegment make(Coordinates A, Coordinates B) {
        CTSegment resul = new CTSegment(A, B);
        resul.rebuildShape();
        return resul;
    }

    private CTSegment(Coordinates A, Coordinates B) {
        super();
        this.A = A;
        this.B = B;
        segmentToDraw = Shape.segment(this.A.getVec().copy(), this.B.getVec().copy());
    }

    /**
     * Creates a Constructible line from a Shape, considering only first and
     * last point
     *
     * @param shape Shape object
     * @return The created object
     */
    public static CTSegment make(Shape shape) {
        return make(shape.getPoint(0), shape.getPoint(-1));
    }

    @Override
    public CTSegment copy() {
        CTSegment copy = CTSegment.make(this.A.getVec().copy(), this.B.getVec().copy());
        copy.copyStateFrom(this);
        return copy;
    }

    @Override
    public Vec getHoldCoordinates(Vec coordinates) {
        Vec v1 = getDirection().normalize();
        Vec v2 = coordinates.minus(getP1());
        double dotProd = v1.dot(v2);
        dotProd = Math.max(dotProd, 0);
        dotProd = Math.min(dotProd, getDirection().norm());
        return getP1().add(v1.mult(dotProd));
    }

    @Override
    public Shape getMathObject() {
        return segmentToDraw;
    }

    @Override
    public void rebuildShape() {
        this.P1.copyCoordinatesFrom(this.A.getVec());
        this.P2.copyCoordinatesFrom(this.B.getVec());
        if (!isFreeMathObject()) {
            segmentToDraw.get(0).v.copyCoordinatesFrom(this.P1);
            segmentToDraw.get(1).v.copyCoordinatesFrom(this.P2);
        }
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        dependsOn(scene, this.A, this.B);
    }

    @Override
    public String toString() {
        return String.format("CTSegment[" + this.A.getVec() + ", " + this.B.getVec() + "]");
    }

    @Override
    public double getScalar() {
        return getP1().to(getP2()).norm();
    }

    @Override
    public void setScalar(double scalar) {
        //Cannot change
    }
}
