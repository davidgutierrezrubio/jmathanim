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

import com.jmathanim.Constructible.Lines.CTVector;
import com.jmathanim.Constructible.Points.CTPoint;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Point;

/**
 *
 * @author David
 */
public class CTTranslatedPoint extends CTPoint {

    private final CTVector translationVector;
    private final CTPoint originalPoint;

    public static CTTranslatedPoint make(Point originalPoint, Vec translationVector) {
        return make(CTPoint.make(originalPoint), CTVector.makeVector(translationVector));
    }

    public static CTTranslatedPoint make(CTPoint originalPoint, CTVector translationVector) {
        CTTranslatedPoint resul = new CTTranslatedPoint(originalPoint, translationVector);
        resul.rebuildShape();
        return resul;
    }

    private CTTranslatedPoint(CTPoint originalPoint, CTVector translationVector) {
        this.translationVector = translationVector;
        this.originalPoint = originalPoint;
    }

    @Override
    public void rebuildShape() {
        getMathObject().copyFrom(originalPoint.getMathObject());
        getMathObject().shift(translationVector.getDirection());

    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        scene.registerUpdateable(this.translationVector, this.originalPoint);
        setUpdateLevel(
                Math.max(this.translationVector.getUpdateLevel(),
                        this.originalPoint.getUpdateLevel()) + 1);
    }
}
