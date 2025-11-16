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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.jmathanim.Constructible.Points;

import com.jmathanim.Constructible.Lines.CTSegment;
import com.jmathanim.MathObjects.Coordinates;
import com.jmathanim.MathObjects.Point;
import com.jmathanim.Utils.Vec;

/**
 * This class represents a Midpoint, from 2 given points or a segment
 *
 * @author David Gutierrez Rubio
 */
public class CTMidPoint extends CTAbstractPoint<CTMidPoint> {

    public enum MidPointType {
        TWO_POINTS, SEGMENT
    }

    MidPointType midPointType;
    private final Coordinates<?> A;
    private final Coordinates<?> B;
    private final CTSegment segment;

    /**
     * Creates the middle point from 2 given points
     *
     * @param A First point
     * @param B Second point
     * @return The object created
     */
    public static CTMidPoint make(Coordinates<?> A, Coordinates<?> B) {
        CTMidPoint resul = new CTMidPoint(MidPointType.TWO_POINTS, A, B, null);
        resul.addDependency(A.getVec());
        resul.addDependency(B.getVec());
        return resul;
    }

    /**
     * Creates the midpoint of a given CTSegment
     *
     * @param segment The CTSegment to compute midpoint
     * @return The created object
     */
    public static CTMidPoint make(CTSegment segment) {
        CTMidPoint resul = new CTMidPoint(MidPointType.SEGMENT, null, null, segment);
        resul.addDependency(segment);
        return resul;
    }

    private CTMidPoint(MidPointType midPointType, Coordinates<?> A, Coordinates<?> B, CTSegment segment) {
        super();
        this.midPointType = midPointType;
        this.A = A;
        this.B = B;
        this.segment = segment;
        update(scene);
    }

    @Override
    public CTMidPoint copy() {
        CTMidPoint copy = null;
        switch (midPointType) {
            case TWO_POINTS:
                copy = new CTMidPoint(midPointType, A.copy(), B.copy(), null);
                copy.copyStateFrom(this);
                break;
            case SEGMENT:
                copy = new CTMidPoint(midPointType, null, null, segment);
                copy.copyStateFrom(this);
        }
        return copy;
    }

    @Override
    public void rebuildShape() {
        Point p = getMathObject();
        switch (midPointType) {
            case SEGMENT:
                Vec p1 = segment.getP1().getVec();
                Vec p2 = segment.getP2().getVec();
                coordinatesOfPoint.x = .5 * (p1.x + p2.x);
                coordinatesOfPoint.y = .5 * (p1.y + p2.y);
                coordinatesOfPoint.z = .5 * (p1.z + p2.z);
                break;
            case TWO_POINTS:
                coordinatesOfPoint.copyCoordinatesFrom(A.getVec().interpolate(B.getVec(), .5));
                break;
        }
        if (!isFreeMathObject()) {
            copyCoordinatesFrom(coordinatesOfPoint);
        }
    }
}
