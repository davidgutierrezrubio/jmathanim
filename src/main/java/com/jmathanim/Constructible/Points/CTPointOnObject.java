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
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Point;

/**
 *
 * @author David
 */
public class CTPointOnObject extends CTPoint {

    private final PointOwner owner;

    public static CTPointOnObject make(PointOwner owner, Point p) {
        CTPointOnObject resul = new CTPointOnObject(owner, p);
        resul.rebuildShape();
        return resul;
    }

    public static CTPointOnObject make(PointOwner owner) {
        return make(owner, Point.origin());
    }

    private CTPointOnObject(PointOwner owner, Point p) {
        super(p);
        this.owner = owner;
    }

    @Override
    public void rebuildShape() {
        Vec projectionPointCoordinates= owner.getHoldCoordinates(this.v);;
        this.v.copyFrom(projectionPointCoordinates);
        if (!isThisMathObjectFree()) {
            pointToDraw.v.copyFrom(projectionPointCoordinates);
        }
    }

    @Override
    public void update(JMathAnimScene scene) {
        rebuildShape();
    }

    @Override
    public CTPointOnObject copy() {
        CTPointOnObject copy = CTPointOnObject.make((PointOwner)((Constructible)owner).copy());
        copy.copyStateFrom(this);
        return copy;
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        dependsOn(scene, (Constructible)owner);
    }

}
