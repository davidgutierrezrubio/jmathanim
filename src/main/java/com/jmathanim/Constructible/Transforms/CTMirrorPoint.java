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
package com.jmathanim.Constructible.Transforms;

import com.jmathanim.Constructible.Constructible;
import com.jmathanim.Constructible.Lines.CTAbstractLine;
import com.jmathanim.Constructible.Lines.CTSegment;
import com.jmathanim.Constructible.Points.CTAbstractPoint;
import com.jmathanim.Constructible.Points.CTPoint;
import com.jmathanim.MathObjects.Coordinates;
import com.jmathanim.MathObjects.Point;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.jmathanim.JMathAnimScene;

/**
 * A Constructible object mirrored by a Line or Segment or Ray
 *
 * @author David
 */
public class CTMirrorPoint extends CTPoint {

    private final CTAbstractLine<?> axis;
    private final Coordinates originalPoint;
    private final Coordinates center;

    private enum MirrorType {
        AXIAL, CENTRAL
    }

    private MirrorType mirrorType;
//
//    public static CTMirrorPoint make(Coordinates<?> orig, CTAbstractLine<?> axis) {
//        CTMirrorPoint resul = new CTMirrorPoint(orig, axis, null);
//        resul.rebuildShape();
//        resul.mirrorType = MirrorType.AXIAL;
//        return resul;
//    }

    public static CTMirrorPoint make(Coordinates<?> orig, Coordinates<?> A, Coordinates<?> B) {
        CTMirrorPoint resul = new CTMirrorPoint(CTPoint.at(orig), CTSegment.make(A, B), null);
        resul.rebuildShape();
        resul.mirrorType = MirrorType.AXIAL;
        return resul;
    }

    public static CTMirrorPoint make(CTAbstractPoint<?> orig, Constructible<?> axis) {
        if (axis instanceof CTAbstractLine) {
            CTMirrorPoint resul = new CTMirrorPoint(orig, (CTAbstractLine<?>) axis, null);
            resul.mirrorType = MirrorType.AXIAL;
            resul.rebuildShape();
            return resul;
        }
//        if (axis instanceof CTSegment) {
//            CTSegment segment = (CTSegment) axis;
//            CTLine line = CTLine.make(segment.getP1(), segment.getP1());
//            CTMirrorPoint resul = new CTMirrorPoint(orig, line, null);
//            resul.mirrorType = MirrorType.AXIAL;
//            resul.rebuildShape();
//            return resul;
//        }
//        if (axis instanceof CTRay) {
//            CTRay ray = (CTRay) axis;
//            CTLine line = CTLine.make(ray.getP1(), ray.getP1());
//            CTMirrorPoint resul = new CTMirrorPoint(orig, line, null);
//            resul.mirrorType = MirrorType.AXIAL;
//            resul.rebuildShape();
//            return resul;
//        }
        if (axis instanceof CTAbstractPoint<?>) {
            CTPoint cp = (CTPoint) axis;
            CTMirrorPoint resul = new CTMirrorPoint(orig, null, cp);
            resul.mirrorType = MirrorType.CENTRAL;
            resul.rebuildShape();
            return resul;
        }
        JMathAnimScene.logger.warn("Don't know how to build this Mirror object with axis " + axis);
        return null;
    }

    private CTMirrorPoint(CTAbstractPoint<?> orig, CTAbstractLine<?> axis, CTPoint center) {
        super();
        this.originalPoint = orig;
        this.axis = axis;
        this.center = center;
    }

    @Override
    public void rebuildShape() {
        AffineJTransform tr;
        switch (mirrorType) {
            case AXIAL:
                tr = AffineJTransform.createReflectionByAxis(axis.getP1(), axis.getP2(), 1);
                break;
            case CENTRAL:
                //Note that we don't use this.center.getMathObject() because 
                //the mathobject may be free. Instead we create a new point pcenter
                tr = AffineJTransform.createScaleTransform(Point.at(this.center.getVec().x,this.center.getVec().y), -1);
                break;
            default:
                tr = new AffineJTransform();//An identity transform
        }
        this.coordinatesOfPoint.copyCoordinatesFrom(originalPoint.getVec());
        this.coordinatesOfPoint.applyAffineTransform(tr);
        if (!isFreeMathObject()) {
            pointToShow.v.copyCoordinatesFrom(coordinatesOfPoint);
        }
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        switch (mirrorType) {
            case AXIAL:
                dependsOn(scene, axis, originalPoint);
                break;
            case CENTRAL:
                dependsOn(scene, center, originalPoint);
                break;
        }
    }
}
