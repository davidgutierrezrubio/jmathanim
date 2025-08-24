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
import com.jmathanim.mathobjects.Coordinates;
import com.jmathanim.mathobjects.Interpolable;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;

/**
 *
 * @author David
 */
public class CTPoint extends Constructible<CTPoint> implements Coordinates<CTPoint>, Interpolable<CTPoint> {
    
    public final Point pointToShow;
    public final Vec coordinatesOfPoint;

    /**
     * Creates a CTPoint from a Point
     *
     * @param A Point object to wrap into
     * @return The created object
     */
    public static CTPoint make(Coordinates<?> A) {
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
        CTPoint resul =  CTPoint.at(x, y);
        resul.rebuildShape();
        return resul;
    }
    
    protected CTPoint() {
        this(Point.origin());
    }
    
    protected CTPoint(Coordinates A) {
        if (A instanceof Point) {
            this.pointToShow = Point.at(A.getVec().copy());
            this.coordinatesOfPoint = A.getVec();
        }
        else {
            this.pointToShow = Point.origin();
            this.coordinatesOfPoint = A.getVec();
        }
    }
    
    @Override
    public Point getMathObject() {
        return pointToShow;
    }
    
    @Override
    public void rebuildShape() {
        if (!isFreeMathObject()) {
            this.pointToShow.v.copyCoordinatesFrom(this.coordinatesOfPoint);
        }
    }
    
    @Override
    public CTPoint copy() {
        CTPoint copy = make(new Point(this.coordinatesOfPoint.x,this.coordinatesOfPoint.y));
        copy.setFreeMathObject(this.isFreeMathObject());
        copy.getMathObject().copyStateFrom(this.getMathObject());
        copy.getMp().copyFrom(this.getMp());
        return copy;
    }
    
    @Override
    public void copyStateFrom(MathObject obj) {
        if (obj instanceof CTPoint) {
            CTPoint cnst = (CTPoint) obj;
            this.coordinatesOfPoint.copyCoordinatesFrom(cnst.coordinatesOfPoint);
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
        return B.coordinatesOfPoint.minus(this.coordinatesOfPoint);
    }

    /**
     * Creates a new CTPoint vector adding a given vector. The original CTPoint
     * is unaltered
     *
     * @param v Vector to add
     * @return The created object
     */
    public CTPoint add(Vec v) {
        return CTPoint.make(new Point(this.coordinatesOfPoint.x+v.x,this.coordinatesOfPoint.y+v.y));
    }
    
    @Override
    public String toString() {
        return this.getObjectLabel() + ":" + String.format("CTPoint[%.2f, %.2f]", this.coordinatesOfPoint.x, this.coordinatesOfPoint.y);
    }
    
    public CTPoint dotStyle(DotStyle dotStyle) {
        pointToShow.dotStyle(dotStyle);
        return this;
    }
    
    @Override
    public CTPoint applyAffineTransform(AffineJTransform transform) {
        pointToShow.applyAffineTransform(transform);
        if (!isFreeMathObject()) {
            this.coordinatesOfPoint.copyCoordinatesFrom(pointToShow.v);
        }
        rebuildShape();
        return this;
    }

    @Override
    public Vec getVec() {
        return coordinatesOfPoint;
    }

    @Override
    public CTPoint add(Coordinates<?> v2) {
        CTPoint copy = copy();
        copy.getVec().addInSite(v2);
        return copy;
    }

    @Override
    public CTPoint minus(Coordinates<?> v2) {
        CTPoint copy = copy();
        copy.getVec().minusInSite(v2);
        return copy;
    }

    @Override
    public CTPoint mult(double lambda) {
        return copy().multInSite(lambda);
    }

    @Override
    public CTPoint interpolate(Coordinates<?> coords2, double alpha) {
        return new CTPoint(getVec().interpolate(coords2.getVec(), alpha));
    }
}
