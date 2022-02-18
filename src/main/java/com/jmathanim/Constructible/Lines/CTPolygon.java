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

import com.jmathanim.Constructible.Points.CTPoint;
import com.jmathanim.Constructible.Constructible;
import com.jmathanim.Constructible.FixedConstructible;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;
import com.jmathanim.mathobjects.updateableObjects.Updateable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.OptionalInt;

/**
 * A constructible polygon
 *
 * @author David Gutierrez Rubio
 */
public class CTPolygon extends FixedConstructible {

    private final Shape shapeToDraw;
    private final CTPoint[] points;

    public static CTPolygon make(Point... points) {
        CTPoint[] ArrayCtpoints = Arrays.stream(points).map(t -> CTPoint.make(t)).toArray(CTPoint[]::new);
        return new CTPolygon(ArrayCtpoints);
    }

    public static CTPolygon make(CTPoint... ctPoints) {
        return new CTPolygon(ctPoints);
    }

    public static CTPolygon make(ArrayList<CTPoint> ctPoints) {
        return new CTPolygon(ctPoints.toArray(new CTPoint[0]));
    }

    private CTPolygon(CTPoint... cpoints) {
        Point[] arrayPoints = Arrays.stream(cpoints).map(t -> t.getMathObject()).toArray(Point[]::new);
        shapeToDraw = Shape.polygon(arrayPoints);
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
    public CTPolygon copy() {
        CTPolygon copy = CTPolygon.make(this.points);
        copy.getMp().copyFrom(this.getMp());
        return copy;
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r) {
        shapeToDraw.draw(scene, r);
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        scene.registerUpdateable(points);
         OptionalInt m = Arrays.stream(points).mapToInt(t -> t.getUpdateLevel()).max();
        if (m.isPresent()) {
            setUpdateLevel(m.getAsInt() + 1);
        } else {
            setUpdateLevel(0);
        }
    }
}
