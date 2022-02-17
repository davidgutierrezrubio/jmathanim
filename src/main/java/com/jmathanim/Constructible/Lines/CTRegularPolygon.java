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
import com.jmathanim.Constructible.FixedConstructible;
import com.jmathanim.Constructible.Points.CTPoint;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;
import com.jmathanim.mathobjects.updateableObjects.Updateable;
import java.util.ArrayList;

/**
 *
 * @author David
 */
public class CTRegularPolygon extends Constructible {

    private final int nSides;
    private final CTPoint B;
    private final CTPoint A;
    private final Shape polygon;
    private final Shape origPolygon;

    /**
     * Creates a new regular polygon from an ArrayList of CTPoints. All but the
     * first 2 will be updated accordingly. Mostly used for Geogebra import.
     *
     * @param generatedPoints ArrayList of generated CTPoints.
     * @return The generated polygon
     */
    public static CTRegularPolygon makeFromPointList(ArrayList<CTPoint> generatedPoints) {
        CTRegularPolygon resul = new CTRegularPolygon(generatedPoints);
        resul.rebuildShape();
        return resul;
    }

    /**
     * Creates a constructible regular polygon with a side given by 2 points and
     * a number of side
     *
     * @param A First point of side
     * @param B Second point of side
     * @param nSides Number of sides
     * @return The created object
     */
    public static CTRegularPolygon make(CTPoint A, CTPoint B, int nSides) {
        ArrayList<CTPoint> vertices = new ArrayList<>();
        vertices.add(A);
        vertices.add(B);
        for (int i = 0; i < nSides - 2; i++) {
            vertices.add(CTPoint.make(new Point()));
        }
        return makeFromPointList(vertices);
    }

    private CTRegularPolygon(ArrayList<CTPoint> generatedPoints) {
        super();
        this.nSides = generatedPoints.size();
        this.A = generatedPoints.get(0);
        this.B = generatedPoints.get(1);
        Point[] points = generatedPoints.stream().map(t -> (Point) t.getMathObject()).toArray(Point[]::new);
        polygon = Shape.polygon(points);
        origPolygon = Shape.regularPolygon(nSides);//Base polygon q
    }

    @Override
    public MathObject getMathObject() {
        return polygon;
    }

    @Override
    public void rebuildShape() {
        AffineJTransform tr = AffineJTransform.createDirect2DIsomorphic(origPolygon.get(0).p, origPolygon.get(1).p, A.getMathObject(), B.getMathObject(), 1);

        for (int k = 2; k < nSides; k++) {
            polygon.get(k).copyFrom(origPolygon.get(k));
            tr.applyTransform(polygon.get(k));
        }
    }

    @Override
    public CTRegularPolygon copy() {
        CTRegularPolygon copy = make(this.A.copy(), this.B.copy(), this.nSides);
        copy.getMp().copyFrom(this.getMp());
        return copy;
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r) {
        polygon.draw(scene, r);
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        scene.registerUpdateable(this.A, this.B);
    }
}
