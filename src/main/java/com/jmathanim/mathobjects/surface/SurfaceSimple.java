/*
 * Copyright (C) 2021 David Gutierrez Rubio davidgutierrezrubio@gmail.com
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
package com.jmathanim.mathobjects.surface;

import com.jmathanim.mathobjects.MultiShapeObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class SurfaceSimple extends MultiShapeObject {

    public SurfaceSimple() {
    }

    public Shape addFace(Shape face) {
        this.add(face);
        return face;
    }

    public Shape addFace(Point... vertices) {
        final Shape face = Shape.polygon(vertices);
        return addFace(face);
    }

    public Shape addFace(double... coords) {
        Point[] vertices = new Point[coords.length / 3];
        int k = 0;
        for (int i = 0; i < coords.length; i += 3) {
            vertices[k] = Point.at(coords[i], coords[i + 1], coords[i + 2]);//TODO: Replace with Vec
            k++;
        }
        return addFace(vertices);
    }
}
