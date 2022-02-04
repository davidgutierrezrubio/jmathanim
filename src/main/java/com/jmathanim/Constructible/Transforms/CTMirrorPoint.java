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
import com.jmathanim.Constructible.Lines.CTLine;
import com.jmathanim.Constructible.Lines.CTRay;
import com.jmathanim.Constructible.Lines.CTSegment;
import com.jmathanim.Constructible.Points.CTPoint;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.NullMathObject;

/**
 * A Constructible object mirrored by a Line or Segment or Ray
 *
 * @author David
 */
public class CTMirrorPoint extends CTPoint {

    private CTLine axis;
    private CTPoint orig;
    private CTPoint center;

    private enum MirrorType {
        AXIAL, CENTRAL
    };
    private MirrorType mirrorType;

    public static CTMirrorPoint make(CTPoint orig, Constructible axis) {
        if (axis instanceof CTLine) {
            CTMirrorPoint resul = new CTMirrorPoint(orig, (CTLine) axis, null);
            resul.mirrorType = MirrorType.AXIAL;
            return resul;
        }
        if (axis instanceof CTSegment) {
            CTSegment segment = (CTSegment) axis;
            CTLine line = CTLine.make(segment.getP1(), segment.getP1());
            CTMirrorPoint resul = new CTMirrorPoint(orig, line, null);
            resul.mirrorType = MirrorType.AXIAL;
            return resul;
        }
        if (axis instanceof CTRay) {
            CTRay ray = (CTRay) axis;
            CTLine line = CTLine.make(ray.getP1(), ray.getP1());
            CTMirrorPoint resul = new CTMirrorPoint(orig, line, null);
            resul.mirrorType = MirrorType.AXIAL;
            return resul;
        }
        if (axis instanceof CTPoint) {
            CTPoint cp = (CTPoint) axis;
            CTMirrorPoint resul = new CTMirrorPoint(orig, null, cp);
            resul.mirrorType = MirrorType.CENTRAL;
            return resul;
        }
        JMathAnimScene.logger.warn("Don't know how to build this Mirror object with axis " + axis);
        return null;
    }

    private CTMirrorPoint(CTPoint orig, CTLine axis, CTPoint center) {
        super();
        this.orig = orig;
        this.axis = axis;
        this.center = center;
    }

    @Override
    public void rebuildShape() {
        switch (mirrorType) {
            case AXIAL:
                AffineJTransform tr = AffineJTransform.createReflectionByAxis(axis.getP1(), axis.getP2(), 1);
                getMathObject().copyFrom(orig.getMathObject());
                tr.applyTransform(getMathObject());
                break;
            case CENTRAL:
                getMathObject().copyFrom(orig.getMathObject());
                getMathObject().scale(this.center.getMathObject(), -1, -1);
                break;
        }
    }
}
