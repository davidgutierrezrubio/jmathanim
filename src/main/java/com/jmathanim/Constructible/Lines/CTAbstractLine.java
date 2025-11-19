/*
 * Copyright (C) 2022 David Gutierrez Rubio davidgutierrezrubio@gmail.com
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

import com.jmathanim.Constructible.Constructible;
import com.jmathanim.Constructible.PointOwner;
import com.jmathanim.MathObjects.Coordinates;
import com.jmathanim.MathObjects.MathObject;
import com.jmathanim.MathObjects.Shapes.Line;
import com.jmathanim.Utils.Vec;

/**
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class CTAbstractLine<T extends CTAbstractLine<T>> extends Constructible<T> implements HasDirection, PointOwner {

    protected final Vec P2draw;
    protected final Vec P1draw;
    protected final Vec P1;
    protected final Vec P2;
    protected final Line lineToDraw;
    protected LineType lineType;

    public CTAbstractLine(Coordinates<?> P1, Coordinates<?> P2) {
        this.P1draw = P1.getVec().copy();
        this.P2draw = P2.getVec().copy();
        this.P1 = P1.getVec();//Points that define the line. These should be recomputed for dependent lines
        this.P2 = P2.getVec();
        lineToDraw = Line.make(this.P1draw, this.P2draw);
        addDependency(lineToDraw.getMp());
    }

    @Override
    public MathObject<?> getMathObject() {
        return lineToDraw;
    }

    @Override
    public void rebuildShape() {
        if (isFreeMathObject()) return;
        this.P1draw.copyCoordinatesFrom(this.P1);
        this.P2draw.copyCoordinatesFrom(this.P2);
        lineToDraw.rebuildShape();
    }

    @Override
    public Vec getDirection() {
        update();
        return P1.to(P2);
    }

    public Coordinates<?> getP1() {
        return P1;
    }

    public Coordinates<?> getP2() {
        return P2;
    }

    @Override
    public Vec getHoldCoordinates(Vec coordinates) {
        Vec v1 = getDirection().normalize();
        Vec v2 = getP1().to(coordinates);
        return getP1().add(v1.copy().scale(v1.dot(v2))).getVec();
    }
//
//    @Override
//    public boolean needsUpdate() {
//        newLastMaxDependencyVersion = DependableUtils.maxVersion(this.P1, this.P2, getMp());
//        if (dirty) return true;
//        return newLastMaxDependencyVersion != lastCleanedDepsVersionSum;
//    }

    protected enum LineType {
        POINT_POINT, POINT_DIRECTION
    }
}
