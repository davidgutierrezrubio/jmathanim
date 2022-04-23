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
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Point;

/**
 * This class represents a Midpoint, from 2 given points or a segment
 *
 * @author David Gutierrez Rubio
 */
public class CTMidPoint extends CTPoint {

    public enum MidPointType {
        TWO_POINTS, SEGMENT
    };
    MidPointType midPointType;
    private final CTPoint A;
    private final CTPoint B;
    private final CTSegment segment;

    /**
     * Creates the middle point from 2 given points
     *
     * @param A First point
     * @param B Second point
     * @return The object created
     */
    public static CTMidPoint make(CTPoint A, CTPoint B) {
        CTMidPoint resul = new CTMidPoint(MidPointType.TWO_POINTS, A, B, null);
        return resul;
    }

    /**
     * Creates the middle point from 2 given points
     *
     * @param A First point
     * @param B Second point
     * @return The object created
     */
    public static CTMidPoint make(Point A, Point B) {
        CTMidPoint resul = new CTMidPoint(MidPointType.TWO_POINTS, CTPoint.make(A), CTPoint.make(B), null);
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
        return resul;
    }

    private CTMidPoint(MidPointType midPointType, CTPoint A, CTPoint B, CTSegment segment) {
        super();
        this.midPointType = midPointType;
        this.A = A;
        this.B = B;
        this.segment = segment;
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
                Point p1 = segment.getP1();
                Point p2 = segment.getP2();
                v.x = .5 * (p1.v.x + p2.v.x);
                v.y = .5 * (p1.v.y + p2.v.y);
                v.z = .5 * (p1.v.z + p2.v.z);
                break;
            case TWO_POINTS:
                v.x = .5 * (A.v.x + B.v.x);
                v.y = .5 * (A.v.y + B.v.y);
                v.z = .5 * (A.v.z + B.v.z);
                break;
        }
        if (!isThisMathObjectFree()) {
            p.v.copyFrom(v);
        }
    }

    @Override
    public void update(JMathAnimScene scene) {
        rebuildShape();
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        switch (midPointType) {
            case SEGMENT:
                dependsOn(scene, this.segment);
                break;
            case TWO_POINTS:
                dependsOn(scene, this.A, this.B);
        }
    }
}
