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

import com.jmathanim.Constructible.FixedConstructible;
import com.jmathanim.Constructible.Lines.CTVector;
import com.jmathanim.Constructible.Points.CTPoint;
import com.jmathanim.mathobjects.Arrow2D;
import com.jmathanim.mathobjects.Point;

/**
 *
 * @author David
 */
public class CTTranslatePoint extends CTPoint {

    private final CTVector translationVector;
    private final CTPoint originalPoint;

    public static CTTranslatePoint make(Point originalPoint, Arrow2D translationVector) {
        CTPoint A = CTPoint.make(translationVector.getStart());
        CTPoint B = CTPoint.make(translationVector.getEnd());
        return make(CTPoint.make(originalPoint), CTVector.makeVector(A, B));
    }

    public static CTTranslatePoint make(CTPoint originalPoint, CTVector translationVector) {
        CTTranslatePoint resul = new CTTranslatePoint(originalPoint, translationVector);
        resul.rebuildShape();
        return resul;
    }

    private CTTranslatePoint(CTPoint originalPoint, CTVector translationVector) {
        this.translationVector = translationVector;
        this.originalPoint = originalPoint;
    }
 @Override
    public void rebuildShape() {
        getMathObject().copyFrom(originalPoint.getMathObject());
        getMathObject().shift(translationVector.getDirection());
        
    }
}
