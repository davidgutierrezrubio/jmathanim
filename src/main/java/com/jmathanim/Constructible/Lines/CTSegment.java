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
import com.jmathanim.MathObjects.Shape;
import com.jmathanim.MathObjects.hasScalarParameter;
import com.jmathanim.Utils.DependableUtils;
import com.jmathanim.Utils.Vec;

/**
 * A straight segment,given by 2 points
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class CTSegment extends CTAbstractLine<CTSegment> implements hasScalarParameter {
    private final Shape segmentToDraw;


    private CTSegment(Coordinates<?> A, Coordinates<?> B) {
        super(A, B);
        segmentToDraw = Shape.segment(P1draw, P2draw);
    }

    /**
     * Creates a Constructible segment between 2 points
     *
     * @param A First point
     * @param B Second point
     * @return The created object
     */
    public static CTSegment make(Coordinates<?> A, Coordinates<?> B) {
        CTSegment resul = new CTSegment(A, B);
        resul.rebuildShape();
        return resul;
    }

    /**
     * Creates a Constructible line from a Shape, considering only first and last point
     *
     * @param shape Shape object
     * @return The created object
     */
    public static CTSegment make(Shape shape) {
        return make(shape.getPoint(0), shape.getPoint(-1));
    }

    @Override
    public CTSegment copy() {
        CTSegment copy = CTSegment.make(getP1().getVec().copy(), getP2().getVec().copy());
        copy.copyStateFrom(this);
        return copy;
    }

    @Override
    public Vec getHoldCoordinates(Vec coordinates) {
        Vec v1 = getDirection().normalize();
        Vec v2 = getP1().to(coordinates);
        double dotProd = v1.dot(v2);
        dotProd = Math.max(dotProd, 0);
        dotProd = Math.min(dotProd, getDirection().norm());
        return getP1().add(v1.copy().scale(dotProd)).getVec();
    }

    @Override
    public Shape getMathObject() {
        return segmentToDraw;
    }

    @Override
    public void rebuildShape() {
        if (!isFreeMathObject()) {
            P1draw.copyCoordinatesFrom(this.P1);
            P2draw.copyCoordinatesFrom(this.P2);
        }
    }

    @Override
    public String toString() {
        return String.format("CTSegment[" + getP1().getVec() + ", " + getP2().getVec() + "]");
    }

    @Override
    public double getValue() {
        return getP1().to(getP2()).norm();
    }

    @Override
    public void setValue(double scalar) {
        //Cannot change
    }

    @Override
    public boolean needsUpdate() {
        newLastMaxDependencyVersion = DependableUtils.maxVersion(this.P1, this.P2, getMp());
        if (dirty) return true;
        return newLastMaxDependencyVersion != lastCleanedDepsVersionSum;
    }



}
