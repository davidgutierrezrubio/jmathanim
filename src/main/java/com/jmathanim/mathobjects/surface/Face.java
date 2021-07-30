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

import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Styling.Stylable;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Rect;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class Face extends MathObject {

    private static final double EPSILON = 0.00001;
    public final ArrayList<Point> points;

    public static Face make(Shape shape) {
        Face resul = new Face();
        for (JMPathPoint p : shape.getPath().jmPathPoints) {
            resul.points.add(p.p);
        }
        return resul;
    }

    public static Face make(Point... vertices) {
        Face resul = new Face();
        resul.points.addAll(Arrays.asList(vertices));
        return resul;
    }

    public Face() {
        points = new ArrayList<>();
    }

    @Override
    public Face copy() {
        Face copy = new Face();
        for (Point p : points) {
            copy.points.add(p.copy());
        }
        copy.getMp().copyFrom(this.getMp().getFirstMP());
        return copy;
    }

    @Override
    public Rect getBoundingBox() {
        return null;//
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r) {
        //Nothing to do here...
    }

    public Shape toShape() {
        Shape resul = Shape.polygon(points.toArray(new Point[points.size()]));
        resul.getMp().copyFrom(this.getMp().getFirstMP());
        return resul;
    }

    /**
     * Join a face with another. If they share a common vertex, the second one
     * is discarded and replace by the first.
     *
     * @param face The second face (vertices who will be discarded belong to
     * this face)
     */
    public void join(Face face) {
        for (int n = 0; n < this.points.size(); n++) {
            Point p = this.points.get(n);
            for (int k = 0; k < face.points.size(); k++) {
                Point q = face.points.get(k);
                if (p.isEquivalentTo(q, EPSILON)) {
                    face.points.set(k, p);//Replace Point q with p
                }

            }

        }
    }

    @Override
    public <T extends MathObject> T applyAffineTransform(AffineJTransform transform) {
        for (Point p : points) {
            p.applyAffineTransform(transform);
        }
        return (T) this;
    }

}
