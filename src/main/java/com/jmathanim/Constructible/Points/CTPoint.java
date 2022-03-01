/*
 * Copyright (C) 2022 David
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR point PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.jmathanim.Constructible.Points;

import com.jmathanim.Constructible.Constructible;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;

/**
 *
 * @author David
 */
public class CTPoint extends Constructible {

    protected final Point pointToDraw;
    public final Vec v;

    /**
     * Creates a CTPoint from a Point
     *
     * @param A Point object to wrap into
     * @return The created object
     */
    public static CTPoint make(Point A) {
        return new CTPoint(A);
    }

    public static CTPoint at(double x, double y) {
        return new CTPoint(Point.at(x, y));
    }

    protected CTPoint() {
        this(Point.origin());
    }

    protected CTPoint(Point A) {
        this.pointToDraw = A;
        this.v = A.v.copy();
    }

    @Override
    public Point getMathObject() {
        return pointToDraw;
    }

    @Override
    public void rebuildShape() {
        if (!isThisMathObjectFree()) {
            this.pointToDraw.v.copyFrom(this.v);
        }
    }

    @Override
    public CTPoint copy() {
        CTPoint copy = make(pointToDraw.copy());
        copy.getMp().copyFrom(this.getMp());
        return copy;
    }

    /**
     * Computes the vector another CTPoint
     *
     * @param B Second CTPoint
     * @return The vector
     */
    public Vec to(CTPoint B) {
        return B.v.minus(this.v);
    }

    /**
     * Creates a new CTPoint vector adding a given vector. The original CTPoint
     * is unaltered
     *
     * @param v Vector to add
     * @return The created object
     */
    public CTPoint add(Vec v) {
        return CTPoint.make(new Point(this.v));
    }

    @Override
    public String toString() {
        return this.getLabel()+":"+String.format("CTPoint[%.2f, %.2f]", this.v.x, this.v.y);
    }

    public CTPoint dotStyle(Point.DotSyle dotStyle) {
        pointToDraw.dotStyle(dotStyle);
        return this;
    }

    @Override
    public Constructible applyAffineTransform(AffineJTransform transform) {
        pointToDraw.applyAffineTransform(transform);
        if (!isThisMathObjectFree()) {
            this.v.copyFrom(pointToDraw.v);
        }
        rebuildShape();
        return this;
    }

    @Override
    public void copyStateFrom(MathObject obj) {
        if (obj instanceof CTPoint) {
            CTPoint cTPoint = (CTPoint) obj;
            this.pointToDraw.copyStateFrom(cTPoint.pointToDraw);
        }
    }
}
