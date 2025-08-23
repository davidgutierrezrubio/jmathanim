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
import com.jmathanim.Enum.DotStyle;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.Interpolable;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.updaters.Coordinates;

/**
 *
 * @author David
 */
public class CTPoint extends Constructible<CTPoint> implements Coordinates, Interpolable<CTPoint> {
    
    public final Point p;
    public final Vec v;

    /**
     * Creates a CTPoint from a Point
     *
     * @param A Point object to wrap into
     * @return The created object
     */
    public static CTPoint make(Coordinates A) {
        Point buildPoint;
        if (A instanceof Point) {
            buildPoint = (Point) A;
        } else {
            buildPoint = new Point(A.getVec());
        }
        CTPoint resul = new CTPoint(buildPoint);
        resul.rebuildShape();
        return resul;
    }
    
    public static CTPoint at(double x, double y) {
        CTPoint resul = new CTPoint(Point.at(x, y));
        resul.rebuildShape();
        return resul;
    }
    
    protected CTPoint() {
        this(Point.origin());
    }
    
    protected CTPoint(Coordinates A) {
        if (A instanceof Point) {
            this.p = ((Point) A).copy();
            this.v = A.getVec();
        }
        else {
            this.p = Point.origin();
            this.v = A.getVec();
        }
    }
    
    @Override
    public Point getMathObject() {
        return p;
    }
    
    @Override
    public void rebuildShape() {
        if (!isFreeMathObject()) {
            this.p.v.copyFrom(this.v);
        }
    }
    
    @Override
    public CTPoint copy() {
        CTPoint copy = make(new Point(this.v.x,this.v.y));
        copy.setFreeMathObject(this.isFreeMathObject());
        copy.getMathObject().copyStateFrom(this.getMathObject());
        copy.getMp().copyFrom(this.getMp());
        return copy;
    }
    
    @Override
    public void copyStateFrom(MathObject obj) {
        if (obj instanceof CTPoint) {
            CTPoint cnst = (CTPoint) obj;
            this.v.copyFrom(cnst.v);
            this.getMathObject().copyStateFrom(cnst.getMathObject());
            this.setFreeMathObject(cnst.isFreeMathObject());
        }
        super.copyStateFrom(obj);
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
        return CTPoint.make(new Point(this.v.x+v.x,this.v.y+v.y));
    }
    
    @Override
    public String toString() {
        return this.getObjectLabel() + ":" + String.format("CTPoint[%.2f, %.2f]", this.v.x, this.v.y);
    }
    
    public CTPoint dotStyle(DotStyle dotStyle) {
        p.dotStyle(dotStyle);
        return this;
    }
    
    @Override
    public CTPoint applyAffineTransform(AffineJTransform transform) {
        p.applyAffineTransform(transform);
        if (!isFreeMathObject()) {
            this.v.copyFrom(p.v);
        }
        rebuildShape();
        return this;
    }

    @Override
    public Vec getVec() {
        return p.v;
    }

    @Override
    public CTPoint interpolate(Coordinates coords2, double alpha) {
        return new CTPoint(getVec().interpolate(coords2.getVec(), alpha));
    }
}
