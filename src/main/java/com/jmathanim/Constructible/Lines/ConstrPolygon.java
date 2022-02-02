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

import com.jmathanim.Constructible.ConstrPoint;
import com.jmathanim.Constructible.Constructible;
import com.jmathanim.Constructible.FixedConstructible;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;
import java.util.ArrayList;

/**
 * A constructible polygon
 *
 * @author David Gutierrez Rubio
 */
public class ConstrPolygon extends FixedConstructible {

    private final Shape shapeToDraw;
    private final ConstrPoint[] points;

    public static ConstrPolygon make(ConstrPoint... points) {
        return new ConstrPolygon(points);
    }

    public static ConstrPolygon make(ArrayList<ConstrPoint> points) {
        return new ConstrPolygon(points.toArray(new ConstrPoint[0]));
    }

    private ConstrPolygon(ConstrPoint... cpoints) {
        Point[] points = new Point[cpoints.length];
        for (int i = 0; i < cpoints.length; i++) {//TODO: Convert this to stream
            points[i] = cpoints[i].getMathObject();
        }
        shapeToDraw = Shape.polygon(points);
        this.points = cpoints;
    }

    @Override
    public MathObject getMathObject() {
        return shapeToDraw;
    }

    @Override
    public void rebuildShape() {
    }

    @Override
    public ConstrPolygon copy() {
        ConstrPolygon copy = ConstrPolygon.make(this.points);
        copy.getMp().copyFrom(this.getMp());
        return copy;
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r) {
        shapeToDraw.draw(scene, r);
    }

}
