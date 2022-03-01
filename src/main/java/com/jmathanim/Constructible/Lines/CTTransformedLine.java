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
import com.jmathanim.Constructible.Points.CTPoint;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.mathobjects.Line;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Scalar;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class CTTransformedLine extends CTAbstractLine {

    private final CTLine transformedLine;
    private final CTLine axis;
    private final CTPoint center;
    private final CTVector translation;
    private final Scalar angle;

    private enum transformType {
        ROTATION, TRANSLATION, AXISMIRROR, CENTRALMIRROR
    };
    private transformType transType;
    private final Line lineToDraw;

    public static CTTransformedLine makeAxisReflectionLine(CTLine transformedLine, CTLine axis) {
        CTTransformedLine resul = new CTTransformedLine(transformedLine, axis, null, null, null);
        resul.transType = transformType.AXISMIRROR;
        return resul;
    }

    public static CTTransformedLine makePointReflectionLine(CTLine transformedLine, CTPoint center) {
        CTTransformedLine resul = new CTTransformedLine(transformedLine, null, center, null, null);
        resul.transType = transformType.CENTRALMIRROR;
        return resul;
    }

    public static CTTransformedLine makeTraslatedLine(CTLine transformedLine, CTVector vector) {
        CTTransformedLine resul = new CTTransformedLine(transformedLine, null, null, vector, null);
        resul.transType = transformType.TRANSLATION;
        return resul;
    }

    public static CTTransformedLine makeRotatedLine(CTLine transformedLine, CTPoint center, Scalar angle) {
        CTTransformedLine resul = new CTTransformedLine(transformedLine, null, center, null, angle);
        resul.transType = transformType.ROTATION;
        return resul;
    }

    private CTTransformedLine(CTLine transformedLine, CTLine axis, CTPoint center, CTVector translation, Scalar angle) {
        this.transformedLine = transformedLine;
        this.axis = axis;
        this.center = center;
        this.translation = translation;
        this.lineToDraw = Line.XAxis();
        this.angle = angle;
    }

    @Override
    public MathObject getMathObject() {
        return lineToDraw;
    }

    @Override
    public Constructible copy() {
        CTTransformedLine copy;
        switch (transType) {
            case AXISMIRROR:
                copy = makeAxisReflectionLine(transformedLine.copy(), axis.copy());
                break;
            case CENTRALMIRROR:
                copy = makePointReflectionLine(transformedLine.copy(), center.copy());
                break;
            case TRANSLATION:
                copy = makeTraslatedLine(transformedLine.copy(), translation.copy());
                break;
            default:
                copy = makeRotatedLine(transformedLine.copy(), center.copy(), angle.copy());
        }
        copy.getMp().copyFrom(getMp());
        copy.rebuildShape();
        return copy;
    }

    @Override
    public void rebuildShape() {
        AffineJTransform tr;
        switch (transType) {
            case AXISMIRROR:
                tr = AffineJTransform.createReflectionByAxis(axis.getP1(), axis.getP2(), 1);
                break;
            case CENTRALMIRROR:
                tr = AffineJTransform.createScaleTransform(new Point(center.v), -1);
                break;
            case TRANSLATION:
                tr = AffineJTransform.createTranslationTransform(translation.getDirection());
                break;
            default:
                tr = AffineJTransform.create2DRotationTransform(new Point(center.v), angle.value);
        }
        getP1().v.copyFrom(transformedLine.getP1().v);
        getP2().v.copyFrom(transformedLine.getP2().v);
        getP1().v.applyAffineTransform(tr);
        getP2().v.applyAffineTransform(tr);
        if (!isThisMathObjectFree()) {
            lineToDraw.getP1().v.copyFrom(getP1().v);
            lineToDraw.getP2().v.copyFrom(getP2().v);
        }
    }

}
