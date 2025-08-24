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

import com.jmathanim.Constructible.Points.CTPoint;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Scalar;

/**
 *
 * @author David
 */
public class CTRotatedPoint extends CTPoint {

    public static CTRotatedPoint make(Point pointToRotate, double angle, Point rotationCenter) {
        return make(CTPoint.make(pointToRotate), CTPoint.make(rotationCenter), Scalar.make(angle));
    }

    public static CTRotatedPoint make(CTPoint pointToRotate, CTPoint rotationCenter, Scalar angle) {
        CTRotatedPoint resul = new CTRotatedPoint(pointToRotate, angle, rotationCenter);
        resul.rebuildShape();
        return resul;
    }
    private final CTPoint pointToRotate;
    private final Scalar angle;
    private final CTPoint rotationCenter;
    private final Point protationCenter;

    private CTRotatedPoint(CTPoint pointToRotate, Scalar angle, CTPoint rotationCenter) {
        this.pointToRotate = pointToRotate;
        this.angle = angle;
        this.rotationCenter = rotationCenter;
        this.protationCenter = new Point(rotationCenter.coordinatesOfPoint.x,rotationCenter.coordinatesOfPoint.y);
    }

    @Override
    public void rebuildShape() {
        this.coordinatesOfPoint.copyCoordinatesFrom(pointToRotate.coordinatesOfPoint);
        AffineJTransform tr = AffineJTransform.create2DRotationTransform(this.protationCenter, angle.value);
        this.coordinatesOfPoint.copyCoordinatesFrom(this.pointToRotate.coordinatesOfPoint);
        this.coordinatesOfPoint.applyAffineTransform(tr);
        if (!isFreeMathObject()) {
            pointToShow.v.copyCoordinatesFrom(coordinatesOfPoint);
        }
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
       dependsOn(scene,this.pointToRotate, this.rotationCenter, this.angle);
    }
}
