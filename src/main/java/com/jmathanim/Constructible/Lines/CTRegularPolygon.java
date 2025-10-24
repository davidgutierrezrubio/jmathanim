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
import com.jmathanim.Constructible.Points.CTAbstractPoint;
import com.jmathanim.Constructible.Points.CTPoint;
import com.jmathanim.MathObjects.Coordinates;
import com.jmathanim.MathObjects.MathObject;
import com.jmathanim.MathObjects.Shape;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;

import java.util.ArrayList;

/**
 *
 * @author David
 */
public class CTRegularPolygon extends Constructible<CTRegularPolygon> {

    private final int nSides;
    private final Coordinates<?> B;
    private final Coordinates<?> A;
    private final Shape poligonToView;
    private final Shape origPolygon;
    private final ArrayList<CTAbstractPoint<?>> generatedPoints;

    /**
     * Creates a new regular polygon from an ArrayList of CTPoints. All but the
     * first 2 will be updated accordingly. Mostly used for Geogebra import.
     *
     * @param generatedPoints ArrayList of generated CTPoints.
     * @return The generated polygon
     */
    public static CTRegularPolygon makeFromPointList(ArrayList<CTAbstractPoint<?>> generatedPoints) {
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
    public static CTRegularPolygon make(Coordinates<?> A, Coordinates<?> B, int nSides) {
        ArrayList<CTAbstractPoint<?>> vertices = new ArrayList<>();
        vertices.add(CTPoint.at(A.getVec()));
        vertices.add(CTPoint.at(B.getVec()));
        for (int i = 0; i < nSides - 2; i++) {
            vertices.add(CTPoint.at(Vec.to(0,0)));
        }
        CTRegularPolygon resul = makeFromPointList(vertices);
        resul.rebuildShape();
        return resul;
    }


    private CTRegularPolygon(ArrayList<CTAbstractPoint<?>> generatedPoints) {
        super();
        this.generatedPoints = generatedPoints;
        this.nSides = generatedPoints.size();
        this.A = generatedPoints.get(0);
        this.B = generatedPoints.get(1);
//        generatedPoints.remove(0);
//        generatedPoints.remove(0);
//        generatedPoints.add(0,this.B.copy());
//        generatedPoints.add(0,this.A.copy());
        Coordinates<?>[] pointsPolToView = generatedPoints.toArray(Coordinates<?>[]::new);
        poligonToView = Shape.polygon(pointsPolToView);
        origPolygon = Shape.regularPolygon(nSides).visible(false);//Base polygon q
    }

    @Override
    public MathObject<?> getMathObject() {
        return poligonToView;
    }

    @Override
    public void rebuildShape() {
        AffineJTransform tr = AffineJTransform.createDirect2DIsomorphic(origPolygon.get(0), origPolygon.get(1), A.copy(), B.copy(), 1);
        for (int k = 0; k < nSides; k++) {
            generatedPoints.get(k).copyCoordinatesFrom(origPolygon.get(k).getV().copy().applyAffineTransform(tr));
        }

        if (!isFreeMathObject()) {
            for (int k = 0; k < nSides; k++) {
                poligonToView.get(k).getV().copyCoordinatesFrom(generatedPoints.get(k));
            }
        }

    }

    @Override
    public CTRegularPolygon copy() {
        CTRegularPolygon copy = make(this.A.copy(), this.B.copy(), this.nSides);
         copy.copyStateFrom(this);
        return copy;
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        dependsOn(scene, this.A, this.B);
    }

}
