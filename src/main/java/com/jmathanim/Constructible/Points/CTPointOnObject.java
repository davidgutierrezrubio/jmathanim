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
import com.jmathanim.Constructible.Lines.CTLine;
import com.jmathanim.Constructible.Lines.CTRay;
import com.jmathanim.Constructible.Lines.CTSegment;
import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.Point;

/**
 *
 * @author David
 */
public class CTPointOnObject extends CTPoint {

    private final Constructible owner;

    private enum PointOnObjectType {
        CTLine, CTSegment, CTRay
    };
    PointOnObjectType type = null;

    public static CTPointOnObject make(Constructible owner, Point p) {
        CTPointOnObject resul = new CTPointOnObject(owner, p);
        if (owner instanceof CTLine) {
            resul.type = PointOnObjectType.CTLine;
        }
        if (owner instanceof CTSegment) {
            resul.type = PointOnObjectType.CTSegment;
        }
        if (owner instanceof CTRay) {
            resul.type = PointOnObjectType.CTRay;
        }
        resul.rebuildShape();
        return resul;
    }

    public static CTPointOnObject make(Constructible owner) {
        return make(owner, Point.origin());
    }

    private CTPointOnObject(Constructible ct, Point p) {
        super(p);
        this.owner = ct;
    }

    @Override
    public void rebuildShape() {
        Vec v1, v2;
        Point q;
        double dotProd;
        switch (type) {
            case CTLine://Simple projection onto line
                CTLine line = (CTLine) owner;
                v1 = line.getDirection().normalize();
                v2 = line.getP1().to(point);
                q = line.getP1().add(v1.mult(v1.dot(v2)));
                point.copyFrom(q);
                break;
            case CTSegment://Simple projection onto line
                CTSegment seg = (CTSegment) owner;
                v1 = seg.getDirection().normalize();
                v2 = seg.getP1().to(point);
                dotProd = v1.dot(v2);
                dotProd = Math.max(dotProd, 0);
                dotProd = Math.min(dotProd, seg.getDirection().norm());
                q = seg.getP1().add(v1.mult(dotProd));
                point.copyFrom(q);
                break;
            case CTRay:
                CTRay ray = (CTRay) owner;
                v1 = ray.getDirection().normalize();
                v2 = ray.getP1().to(point);
                dotProd = v1.dot(v2);
                dotProd = Math.max(dotProd, 0);
                q = ray.getP1().add(v1.mult(dotProd));
                point.copyFrom(q);
                break;
        }
    }

    @Override
    public CTPointOnObject copy() {
        CTPointOnObject copy=CTPointOnObject.make((Constructible)owner.copy());
        copy.getMp().copyFrom(this.getMp());
        return copy;
    }

    
}
