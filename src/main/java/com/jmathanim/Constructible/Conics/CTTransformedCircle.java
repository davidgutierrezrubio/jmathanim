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
package com.jmathanim.Constructible.Conics;

import com.jmathanim.Constructible.Lines.CTAbstractLine;
import com.jmathanim.Constructible.Lines.CTVector;
import com.jmathanim.Constructible.Points.CTAbstractPoint;
import com.jmathanim.MathObjects.Scalar;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class CTTransformedCircle extends CTAbstractCircle<CTTransformedCircle> {

    private final CTAbstractCircle<?> circleToTransform;
    private final CTAbstractLine<?> axis;
    private final CTAbstractPoint<?> center;
    private final CTVector translation;
    private final Scalar angle;
    private transformType transType;

    private CTTransformedCircle(CTAbstractCircle<?> circleToTransform, CTAbstractLine<?> axis, CTAbstractPoint<?> center, CTVector translation, Scalar angle) {
        super(Vec.to(0, 0), Scalar.make(0));
        this.circleToTransform = circleToTransform;
        this.axis = axis;
        this.center = center;
        this.translation = translation;
        this.angle = angle;
    }

    /**
     * Creates a Constructible circle mirrored about a given axis
     *
     * @param circleToTransform Circle to be mirrored
     * @param axis              Mirror axis
     * @return The created object
     */
    public static CTTransformedCircle makeAxisReflectionCircle(CTAbstractCircle<?> circleToTransform, CTAbstractLine<?> axis) {
        CTTransformedCircle resul = new CTTransformedCircle(circleToTransform, axis, null, null, null);
        resul.transType = transformType.AXISMIRROR;
        resul.addDependency(circleToTransform);
        resul.addDependency(resul.center);
        return resul;
    }

    /**
     * Creates a Constructible circle mirrored about a given point
     *
     * @param circleToTransform Circle to be mirrored
     * @param center            Mirror center
     * @return The created object
     */
    public static CTTransformedCircle makePointReflectionCircle(CTAbstractCircle<?> circleToTransform, CTAbstractPoint<?> center) {
        CTTransformedCircle resul = new CTTransformedCircle(circleToTransform, null, center, null, null);
        resul.transType = transformType.CENTRALMIRROR;
        resul.addDependency(circleToTransform);
        resul.addDependency(resul.center);
        return resul;
    }

    /**
     * Creates a Constructible circle translated a given vector
     *
     * @param circleToTransform Circle to be translated
     * @param vector            Translation vector
     * @return The created object
     */
    public static CTTransformedCircle makeTranslatedCircle(CTAbstractCircle<?> circleToTransform, CTVector vector) {
        CTTransformedCircle resul = new CTTransformedCircle(circleToTransform, null, null, vector, null);
        resul.transType = transformType.TRANSLATION;
        resul.addDependency(circleToTransform);
        resul.addDependency(vector);
        return resul;
    }

    /**
     * Creates a Constructible circle rotated arount a given point and angle
     *
     * @param circleToTransform Circle to be rotated
     * @param center            Rotation center
     * @param angle             Rotation angle
     * @return The created object
     */
    public static CTTransformedCircle makeRotatedCircle(CTAbstractCircle<?> circleToTransform, CTAbstractPoint<?> center, Scalar angle) {
        CTTransformedCircle resul = new CTTransformedCircle(circleToTransform, null, center, null, angle);
        resul.transType = transformType.ROTATION;
        return resul;
    }

    @Override
    public Vec getHoldCoordinates(Vec coordinates) {
        JMathAnimScene.logger.warn("Hold coordinates not implemented yet for CTTransformedCircle");
        return null;//TODO: IMplement this
    }

    @Override
    public CTTransformedCircle copy() {
        CTTransformedCircle copy;
        switch (transType) {
            case AXISMIRROR:
                copy = makeAxisReflectionCircle(circleToTransform.copy(), axis.copy());
                break;
            case CENTRALMIRROR:
                copy = makePointReflectionCircle(circleToTransform.copy(), center.copy());
                break;
            case TRANSLATION:
                copy = makeTranslatedCircle(circleToTransform.copy(), translation.copy());
                break;
            default:
                copy = makeRotatedCircle(circleToTransform.copy(), center.copy(), angle.copy());
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
                tr = AffineJTransform.createScaleTransform(center, -1);
                break;
            case TRANSLATION:
                tr = AffineJTransform.createTranslationTransform(translation.getDirection());
                break;
            case ROTATION:
                tr = AffineJTransform.create2DRotationTransform(center, angle.getValue());
                break;
            default:
                tr = null;
        }
        final Vec vv = circleToTransform.getCircleCenter().getVec().copy();
        setCircleCenter(vv);
        getCircleCenter().getVec().applyAffineTransform(tr);
        setCircleRadius(circleToTransform.getCircleRadius());

        if (!isFreeMathObject()) {
            getMathObject().copyStateFrom(circleToTransform);
            getMathObject().scale(this.getCircleRadius().getValue());
            getMathObject().shift(this.getCircleCenter());
        }
    }

    private enum transformType {
        ROTATION, TRANSLATION, AXISMIRROR, CENTRALMIRROR
    }

}

