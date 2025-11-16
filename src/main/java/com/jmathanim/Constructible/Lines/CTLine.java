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
import com.jmathanim.MathObjects.Shapes.Line;
import com.jmathanim.Utils.DependableUtils;
import com.jmathanim.Utils.Vec;

/**
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class CTLine extends CTAbstractLine<CTLine> {

    HasDirection dir;

    protected CTLine(Coordinates<?> A, Coordinates<?> B) {
        super(A, B);
    }

    /**
     * Creates a Constructible line from a Line
     *
     * @param line Line object
     * @return The created object
     */
    public static CTLine make(Line line) {
        return make(line.getP1(), line.getP2());
    }

    /**
     * Creates a Constructible line from a point and any object that implements the HasDirection interface
     *
     * @param A   A point of the line
     * @param dir A MathObject with a direction (Line, Ray, Arrow2D, CTSegment,CTLine...)
     * @return The created object
     */
    public static CTLine makePointDir(Coordinates<?> A, HasDirection dir) {
        CTLine resul = new CTLine(A, A.add(dir.getDirection()));
        resul.dir = dir;
        resul.lineType = LineType.POINT_DIRECTION;
        resul.addDependency(A.getVec());
        resul.addDependency(dir);
        resul.rebuildShape();
        return resul;
    }

    /**
     * Creates a Constructible line given by 2 points
     *
     * @param A First point
     * @param B Second point
     * @return The created object
     */
    public static CTLine make(Coordinates<?> A, Coordinates<?> B) {
        CTLine resul = new CTLine(A, B);
        resul.lineType = LineType.POINT_POINT;
        resul.rebuildShape();
        return resul;
    }

    @Override
    public CTLine copy() {
        CTLine copy = null;
        switch (lineType) {
            case POINT_POINT:
                copy = CTLine.make(P1.copy(), P2.copy());
                copy.copyStateFrom(this);
                break;
            case POINT_DIRECTION:
                copy = CTLine.makePointDir(P1.copy(), this.dir);
                copy.copyStateFrom(this);
                break;
        }
        return copy;
    }

    @Override
    public Vec getDirection() {
        switch (lineType) {
            case POINT_POINT:
                return P1.to(P2);
            case POINT_DIRECTION:
                return dir.getDirection();
        }
        return null;
    }

    @Override
    public boolean needsUpdate() {
        switch (lineType) {
            case POINT_POINT:
                newLastMaxDependencyVersion = DependableUtils.maxVersion(this.P1, this.P2, getMp());
                break;
            case POINT_DIRECTION:
                newLastMaxDependencyVersion = DependableUtils.maxVersion(this.P1, this.dir, getMp());
                break;
        }
        if (dirty) return true;
        return newLastMaxDependencyVersion != lastCleanedDepsVersionSum;
    }


    @Override
    public Vec getHoldCoordinates(Vec coordinates) {
        Vec v1 = getDirection().normalize();
        Vec v2 = coordinates.minus(getP1());
        return getP1().add(v1.mult(v1.dot(v2))).getVec();
    }


    @Override
    public void rebuildShape() {
//            P1.copyCoordinatesFrom(getP1().getVec());
            switch (lineType) {
                case POINT_POINT:
//                    P2.copyCoordinatesFrom(getP2().getVec());
                    break;
                case POINT_DIRECTION:
                    P2.copyCoordinatesFrom(getP1().getVec().add(dir.getDirection()));
            }
            super.rebuildShape();
    }
}
