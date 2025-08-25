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

import com.jmathanim.mathobjects.Coordinates;
import com.jmathanim.mathobjects.Point;

/**
 *
 * @author David
 */
public class CTPoint extends CTAbstractPoint<CTPoint> {

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
        super(A);
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
    public String toString() {
        return this.getObjectLabel() + ":" + String.format("CTPoint[%.2f, %.2f]", this.coordinatesOfPoint.x, this.coordinatesOfPoint.y);
    }

}
