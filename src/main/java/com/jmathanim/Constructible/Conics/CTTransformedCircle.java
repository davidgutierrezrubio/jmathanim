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

import com.jmathanim.Constructible.Lines.CTLine;
import com.jmathanim.Constructible.Lines.CTVector;
import com.jmathanim.Constructible.Points.CTPoint;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Scalar;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class CTTransformedCircle extends CTCircle {

    private final CTCircle circleToTransform;
    private final CTLine axis;
    private final CTPoint center;
    private final CTVector translation;
    private final Scalar angle;

    private enum transformType {
        ROTATION, TRANSLATION, AXISMIRROR, CENTRALMIRROR
    }

    private transformType transType;

    /**
     * Creates a Constructible circle mirrored about a given axis
     *
     * @param circleToTransform Circle to be mirrored
     * @param axis Mirror axis
     * @return The created object
     */
    public static CTTransformedCircle makeAxisReflectionCircle(CTCircle circleToTransform, CTLine axis) {
        CTTransformedCircle resul = new CTTransformedCircle(circleToTransform, axis, null, null, null);
        resul.transType = transformType.AXISMIRROR;
        return resul;
    }

    /**
     * Creates a Constructible circle mirrored about a given point
     *
     * @param circleToTransform Circle to be mirrored
     * @param center Mirror center
     * @return The created object
     */
    public static CTTransformedCircle makePointReflectionCircle(CTCircle circleToTransform, CTPoint center) {
        CTTransformedCircle resul = new CTTransformedCircle(circleToTransform, null, center, null, null);
        resul.transType = transformType.CENTRALMIRROR;
        return resul;
    }

    /**
     * Creates a Constructible circle translated a given vector
     *
     * @param circleToTransform Circle to be translated
     * @param vector Translation vector
     * @return The created object
     */
    public static CTTransformedCircle makeTranslatedCircle(CTCircle circleToTransform, CTVector vector) {
        CTTransformedCircle resul = new CTTransformedCircle(circleToTransform, null, null, vector, null);
        resul.transType = transformType.TRANSLATION;
        return resul;
    }
 /**
     * Creates a Constructible circle rotated arount a given point and angle
     *
     * @param circleToTransform Circle to be rotated
     * @param center Rotation center
     * @param angle Rotation angle
     * @return The created object
     */
    public static CTTransformedCircle makeRotatedCircle(CTCircle circleToTransform, CTPoint center, Scalar angle) {
        CTTransformedCircle resul = new CTTransformedCircle(circleToTransform, null, center, null, angle);
        resul.transType = transformType.ROTATION;
        return resul;
    }

    private CTTransformedCircle(CTCircle circleToTransform, CTLine axis, CTPoint center, CTVector translation, Scalar angle) {
        super();
        this.circleToTransform = circleToTransform;
        this.axis = axis;
        this.center = center;
        this.translation = translation;
        this.angle = angle;
    }

    @Override
    public CTCircle copy() {
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
                tr = AffineJTransform.createScaleTransform(new Point(center.v.x,center.v.y), -1);
                break;
            case TRANSLATION:
                tr = AffineJTransform.createTranslationTransform(translation.getDirection());
                break;
            case ROTATION:
                tr = AffineJTransform.create2DRotationTransform(new Point(center.v.x,center.v.y), angle.value);
                break;
            default:
                tr = null;
        }
        final Vec vv = circleToTransform.getCenter().v;
        getCircleCenter().v.copyFrom(vv);
        this.radius.setScalar(circleToTransform.radius.getScalar());
        getCircleCenter().v.applyAffineTransform(tr);
        if (!isFreeMathObject()) {
            for (int i = 0; i < circleToDraw.size(); i++) {
                JMPathPoint get = circleToDraw.get(i);
                get.copyFrom(originalCircle.get(i));
            }
            circleToDraw.scale(this.radius.value);
            circleToDraw.shift(this.circleCenter.v);
        }
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        switch (transType) {
            case AXISMIRROR:
                dependsOn(scene, this.circleToTransform, this.axis);
                break;
            case CENTRALMIRROR:
                dependsOn(scene, this.circleToTransform, this.center);
                break;
            case ROTATION:
                dependsOn(scene, this.circleToTransform, this.center, this.angle);
                break;
            case TRANSLATION:
                dependsOn(scene, this.circleToTransform, this.translation);
                break;
        }
    }

}
