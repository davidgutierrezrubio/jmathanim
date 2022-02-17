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
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Scalar;

/**
 *
 * @author David
 */
public class CTRotatedPoint extends CTPoint {

    public static CTRotatedPoint make(Point pointToRotate, double angle, Point rotationCenter) {
        return make(CTPoint.make(pointToRotate), Scalar.make(angle), CTPoint.make(rotationCenter));
    }

    public static CTRotatedPoint make(CTPoint pointToRotate, Scalar angle, CTPoint rotationCenter) {
        CTRotatedPoint resul = new CTRotatedPoint(pointToRotate, angle, rotationCenter);
        resul.rebuildShape();
        return resul;
    }
    private final CTPoint pointToRotate;
    private final Scalar angle;
    private final CTPoint rotationCenter;

    private CTRotatedPoint(CTPoint pointToRotate, Scalar angle, CTPoint rotationCenter) {
        this.pointToRotate = pointToRotate;
        this.angle = angle;
        this.rotationCenter = rotationCenter;
    }

    @Override
    public void rebuildShape() {
        getMathObject().copyFrom(pointToRotate.getMathObject());
        getMathObject().rotate(rotationCenter.getMathObject(), angle.value);

    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        scene.registerUpdateable(this.pointToRotate, this.rotationCenter, this.angle);
    }
}
