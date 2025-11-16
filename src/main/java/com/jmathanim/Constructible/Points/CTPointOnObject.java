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
package com.jmathanim.Constructible.Points;

import com.jmathanim.Constructible.Constructible;
import com.jmathanim.Constructible.PointOwner;
import com.jmathanim.Utils.Vec;

/**
 *
 * @author David
 */
public class CTPointOnObject extends CTAbstractPoint<CTPointOnObject> {

    private final PointOwner owner;

    public static CTPointOnObject make(PointOwner owner) {
        CTPointOnObject resul = new CTPointOnObject(owner);
        resul.rebuildShape();
        return resul;
    }

    private CTPointOnObject(PointOwner owner) {
        super(Vec.to(0,0));
        this.owner = owner;
        addDependency(this.owner);
    }

    @Override
    public void rebuildShape() {
        Vec projectionPointCoordinates= owner.getHoldCoordinates(this.coordinatesOfPoint);
        this.coordinatesOfPoint.copyCoordinatesFrom(projectionPointCoordinates);
        if (!isFreeMathObject()) {
            pointToShow.v.copyCoordinatesFrom(projectionPointCoordinates);
        }
    }

    @Override
    public CTPointOnObject copy() {
        CTPointOnObject copy = CTPointOnObject.make((PointOwner)((Constructible)owner).copy());
        copy.copyStateFrom(this);
        return copy;
    }

}
