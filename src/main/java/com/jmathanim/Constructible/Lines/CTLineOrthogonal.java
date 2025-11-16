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

import static com.jmathanim.jmathanim.JMathAnimScene.PI;

/**
 * A Constructible Line that pass through A and is orthogonal to a given direction
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class CTLineOrthogonal extends CTAbstractLine<CTLineOrthogonal> {

    protected final HasDirection dir;
    private final Vec A;

    /**
     * A CTLine that pass through A and is perpendicular to a object with direction
     *
     * @param A   Point of line
     * @param dir An object that implements the HasDirection interface
     */
    private CTLineOrthogonal(Coordinates<?> A, HasDirection dir) {
        super(A.copy(), A.add(dir.getDirection().rotate(PI / 2)));
        this.lineType = LineType.POINT_DIRECTION;
        this.dir = dir;
        this.A = A.getVec();
        addDependency(dir);
        addDependency(this.A);
    }

    /**
     * A CTLine that pass through A and is perpendicular to the segment AB
     *
     * @param A First point
     * @param B Second point
     * @return The created object
     */
    public static CTLineOrthogonal make(Coordinates<?> A, Coordinates<?> B) {
        return makePointDir(A, CTSegment.make(A, B));
    }

    /**
     * A CTLine that pass through A and is perpendicular to a object with direction
     *
     * @param A   Point of line
     * @param dir An object that implements the HasDirection interface
     * @return The created object
     */
    public static CTLineOrthogonal makePointDir(Coordinates<?> A, HasDirection dir) {
        CTLineOrthogonal resul = new CTLineOrthogonal(A, dir);
        resul.rebuildShape();
        return resul;
    }

    @Override
    public CTLineOrthogonal copy() {
        CTLineOrthogonal copy = makePointDir(getP1().copy(), dir);
        copy.copyStateFrom(this);
        return copy;
    }

    @Override
    public void rebuildShape() {
        Vec v = getP1().getVec();
        Vec direction = dir.getDirection();
        P1.copyCoordinatesFrom(A);
        P2.copyCoordinatesFrom(Vec.to(v.x - direction.y, v.y + direction.x));
        super.rebuildShape();
    }

    @Override
    public boolean needsUpdate() {
        newLastMaxDependencyVersion = DependableUtils.maxVersion(this.A, this.dir, getMp());
        if (dirty) return true;
        return newLastMaxDependencyVersion != lastCleanedDepsVersionSum;
    }


}
