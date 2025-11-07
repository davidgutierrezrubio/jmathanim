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

import com.jmathanim.Constructible.Points.CTAbstractPoint;
import com.jmathanim.MathObjects.Scalar;
import com.jmathanim.MathObjects.Shapes.Line;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Vec;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class CTTransformedLine extends CTAbstractLine<CTTransformedLine> {

    private final CTAbstractLine<?> lineToTransform;
    private final CTAbstractLine<?> axis;
    private final CTAbstractPoint<?> center;
    private final CTVector translation;
    private final Scalar angle;

    private enum transformType {
        ROTATION, TRANSLATION, AXISMIRROR, CENTRALMIRROR
    }

    private transformType transType;
    private final Line lineToDraw;

    public static CTTransformedLine makeAxisReflectionLine(CTAbstractLine<?> lineToTransform, CTAbstractLine<?> axis) {
        CTTransformedLine resul = new CTTransformedLine(lineToTransform, axis, null, null, null);
        resul.transType = transformType.AXISMIRROR;
        resul.addDependency(resul.lineToTransform);
        resul.addDependency(resul.axis);
        return resul;
    }

    public static CTTransformedLine makePointReflectionLine(CTAbstractLine<?> lineToTransform, CTAbstractPoint<?> center) {
        CTTransformedLine resul = new CTTransformedLine(lineToTransform, null, center, null, null);
        resul.transType = transformType.CENTRALMIRROR;
        resul.addDependency(resul.lineToTransform);
        resul.addDependency(resul.center);
        return resul;
    }

    public static CTTransformedLine makeTranslatedLine(CTAbstractLine<?> lineToTransform, CTVector vector) {
        CTTransformedLine resul = new CTTransformedLine(lineToTransform, null, null, vector, null);
        resul.transType = transformType.TRANSLATION;
        resul.addDependency(resul.lineToTransform);
        resul.addDependency(resul.translation);
        return resul;
    }

    public static CTTransformedLine makeRotatedLine(CTAbstractLine<?> lineToTransform, CTAbstractPoint<?> center, Scalar angle) {
        CTTransformedLine resul = new CTTransformedLine(lineToTransform, null, center, null, angle);
        resul.transType = transformType.ROTATION;
        resul.addDependency(resul.lineToTransform);
        resul.addDependency(resul.center);
        resul.addDependency(resul.angle);
        return resul;
    }

    private CTTransformedLine(CTAbstractLine<?> lineToTransform, CTAbstractLine<?> axis, CTAbstractPoint<?> center, CTVector translation, Scalar angle) {
        super(Vec.to(0,0), Vec.to(1,0));//Trivial line
        this.lineToTransform = lineToTransform;
        this.axis = axis;
        this.center = center;
        this.translation = translation;
        this.lineToDraw = Line.XAxis();
        this.angle = angle;
    }

    @Override
    public Line getMathObject() {
        return lineToDraw;
    }

    @Override
    public CTTransformedLine copy() {
        CTTransformedLine copy;
        switch (transType) {
            case AXISMIRROR:
                copy = makeAxisReflectionLine(lineToTransform.copy(), axis.copy());
                break;
            case CENTRALMIRROR:
                copy = makePointReflectionLine(lineToTransform.copy(), center.copy());
                break;
            case TRANSLATION:
                copy = makeTranslatedLine(lineToTransform.copy(), translation.copy());
                break;
            default:
                copy = makeRotatedLine(lineToTransform.copy(), center.copy(), angle.copy());
        }
        copy.copyStateFrom(this);
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
                tr = AffineJTransform.createScaleTransform(center.getVec().copy(), -1);
                break;
            case TRANSLATION:
                tr = AffineJTransform.createTranslationTransform(translation.getDirection());
                break;
            default:
                tr = AffineJTransform.create2DRotationTransform(center.getVec().copy(), angle.getValue());
        }
        getP1().copyCoordinatesFrom(lineToTransform.getP1());
        getP2().copyCoordinatesFrom(lineToTransform.getP2());
        getP1().getVec().applyAffineTransform(tr);
        getP2().getVec().applyAffineTransform(tr);
        if (!isFreeMathObject()) {
            lineToDraw.getP1().copyCoordinatesFrom(getP1());
            lineToDraw.getP2().copyCoordinatesFrom(getP2());
        }
        lineToDraw.rebuildShape();
    }

}
