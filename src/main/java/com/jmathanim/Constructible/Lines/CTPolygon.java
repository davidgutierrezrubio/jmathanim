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
package com.jmathanim.Constructible.Lines;

import com.jmathanim.Constructible.Constructible;
import com.jmathanim.MathObjects.Coordinates;
import com.jmathanim.MathObjects.MathObject;
import com.jmathanim.MathObjects.Shape;

import java.util.ArrayList;

/**
 * A constructible polygon
 *
 * @author David Gutierrez Rubio
 */
public class CTPolygon extends Constructible<CTPolygon> {

    private final Shape shapeToDraw;
    private final Coordinates<?>[] points;

    public static CTPolygon make(Coordinates<?>... points) {
        return new CTPolygon(points);
    }

    public static CTPolygon make(ArrayList<Coordinates<?>> ctPoints) {
        return new CTPolygon(ctPoints.toArray(new Coordinates<?>[0]));
    }

    private CTPolygon(Coordinates<?>... cpoints) {
        super();
        shapeToDraw = Shape.polygon(cpoints);
        this.points = cpoints;
    }

    @Override
    public MathObject<?>  getMathObject() {
        return shapeToDraw;
    }

    @Override
    public void rebuildShape() {
        if (!isFreeMathObject()) {
            for (int i = 0; i < points.length; i++) {
                Coordinates<?> point = points[i];
                shapeToDraw.get(i).getV().copyCoordinatesFrom(point.getVec());
            }
        }
    }

    @Override
    public CTPolygon copy() {
        CTPolygon copy = CTPolygon.make(this.points);
        copy.copyStateFrom(this);
        return copy;
    }

}
