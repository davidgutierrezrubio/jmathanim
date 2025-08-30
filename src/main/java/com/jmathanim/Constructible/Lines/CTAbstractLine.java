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
import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.Coordinates;
import com.jmathanim.mathobjects.Line;
import com.jmathanim.mathobjects.MathObject;

/**
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class CTAbstractLine<T extends CTAbstractLine<T>> extends Constructible<T> implements HasDirection, PointOwner {

    protected final Vec P2draw;
    protected final Vec P1draw;
    protected final Coordinates<?> P1;
    protected final Coordinates<?> P2;
    protected final Line lineToDraw;
    protected LineType lineType;
    public CTAbstractLine(Coordinates<?> P1, Coordinates<?> P2) {
        this.P1draw = P1.getVec().copy();
        this.P2draw = P2.getVec().copy();
        this.P1 = P1;
        this.P2 = P2;
        lineToDraw = Line.make(this.P1draw, this.P2draw);
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
        Vec v2 = coordinates.minus(getP1());
        return getP1().add(v1.mult(v1.dot(v2))).getVec();
    }

    protected enum LineType {
        POINT_POINT, POINT_DIRECTION
    }

}
