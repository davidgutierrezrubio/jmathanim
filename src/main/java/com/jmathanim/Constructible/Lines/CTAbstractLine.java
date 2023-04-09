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
import com.jmathanim.mathobjects.Point;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class CTAbstractLine extends Constructible implements HasDirection,PointOwner {

    protected enum LineType {
        PointPoint, PointVector
    }
    protected LineType lineType;
    protected final Point P1;
    protected final Point P2;

    public CTAbstractLine() {
        this.P1 = Point.origin();
        this.P2 = Point.origin();
    }

    @Override
    public Vec getDirection() {
        return P1.to(P2);
    }

    public Point getP1() {
        return P1;
    }

    public Point getP2() {
        return P2;
    }

}
