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

import com.jmathanim.Constructible.Points.CTAbstractPoint;
import com.jmathanim.Constructible.Points.CTPoint;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Coordinates;
import com.jmathanim.mathobjects.Scalar;

/**
 *
 * @author David
 */
public class CTRotatedPoint extends CTAbstractPoint<CTRotatedPoint> {

    public static CTRotatedPoint make(Coordinates<?> pointToRotate, double angle, Coordinates<?> rotationCenter) {
        return make(CTPoint.at(pointToRotate), CTPoint.at(rotationCenter), Scalar.make(angle));
    }

    public static CTRotatedPoint make(Coordinates<?> pointToRotate, Coordinates<?> rotationCenter, Scalar angle) {
        CTRotatedPoint resul = new CTRotatedPoint(pointToRotate, angle, rotationCenter);
        resul.rebuildShape();
        return resul;
    }
    private final Coordinates<?> pointToRotate;
    private final Scalar angle;
    private final Vec rotationCenter;

    private CTRotatedPoint(Coordinates<?> pointToRotate, Scalar angle, Coordinates<?> rotationCenter) {
        super();
        this.pointToRotate = pointToRotate;
        this.angle = angle;
        this.rotationCenter = rotationCenter.getVec();
    }

    @Override
    public void rebuildShape() {
        this.coordinatesOfPoint.copyCoordinatesFrom(pointToRotate);
        AffineJTransform tr = AffineJTransform.create2DRotationTransform(this.rotationCenter, angle.getValue());
        this.coordinatesOfPoint.copyCoordinatesFrom(this.pointToRotate);
        this.coordinatesOfPoint.applyAffineTransform(tr);
        if (!isFreeMathObject()) {
            pointToShow.v.copyCoordinatesFrom(coordinatesOfPoint);
        }
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
       dependsOn(scene,this.pointToRotate, this.rotationCenter, this.angle);
    }

    @Override
    public CTRotatedPoint copy() {
        return new CTRotatedPoint(pointToRotate.getVec().copy(), angle.copy(), rotationCenter.copy());
    }
}
