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

import com.jmathanim.Renderers.JOGLRenderer.JOGLRenderer;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;
import java.util.ArrayList;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class Surface extends MathObject {

    public final ArrayList<Face> faces;

    public static Surface extrude(Shape sh, Vec normal) {
        Surface resul = new Surface();
        Shape sh2 = sh.copy().shift(normal);
        resul.addFace(Face.make(sh));
        resul.addFace(Face.make(sh2));

        for (int n = 0; n < sh.size(); n++) {
            Point p1 = sh.getPath().getJMPoint(n).p;
            Point p2 = sh.getPath().getJMPoint(n + 1).p;
            Point p3 = sh2.getPath().getJMPoint(n).p;
            Point p4 = sh2.getPath().getJMPoint(n + 1).p;
            resul.addFace(p1,p2,p4,p3);
        }

        return resul;
    }

    public Surface() {
        faces = new ArrayList<>();
    }

    @Override
    public Surface copy() {
        Surface copy = new Surface();
        for (Face f : this.faces) {
            copy.faces.add(f.copy());
        }
        copy.getMp().copyFrom(this.getMp());
        return copy;
    }

    @Override
    public Rect getBoundingBox() {
        return null;
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r) {
        if (r instanceof JOGLRenderer) {
            ((JOGLRenderer) r).drawSurface(this);//Only for JOGL renderers
        }
    }

    public Face addFace(Face face) {
        this.faces.add(face);
        return face;//TODO: Handle MODrawProperties
    }

    public Face addFace(Point... vertices) {
        final Face face = Face.make(vertices);
        return addFace(face);
    }

    public Face addFace(double... coords) {
        Point[] vertices = new Point[coords.length / 3];
        int k = 0;
        for (int i = 0; i < coords.length; i += 3) {
            vertices[k] = Point.at(coords[i], coords[i + 1], coords[i + 2]);
            k++;
        }
        return addFace(vertices);
    }

    @Override
    public <T extends MathObject> T applyAffineTransform(AffineJTransform transform) {
        for (Face f : faces) {
            f.applyAffineTransform(transform);
        }
        return (T) this;
    }

}
