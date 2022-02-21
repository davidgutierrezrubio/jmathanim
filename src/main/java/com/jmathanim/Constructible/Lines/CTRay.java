/*
 * Copyright (C) 2021 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
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
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Ray;

/**
 * A Constructible ray
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class CTRay extends CTLine {

    private final Ray rayToDraw;

    /**
     * Creates a new Constructible ray with given point and direction
     *
     * @param A Starting point
     * @param dir Direction, given by any object that implements the interface
     * HasDirection
     * @return The created object
     */
    public static CTRay make(CTPoint A, HasDirection dir) {
        CTRay resul = new CTRay(A, A.add(dir.getDirection()));
        resul.dir = dir;
        resul.lineType = LineType.PointVector;
        resul.rebuildShape();
        return resul;
    }

    /**
     * Creates a new Constructible ray with given 2 points
     *
     * @param A Starting point
     * @param B Second point
     * @return The created object
     */
    public static CTRay make(CTPoint A, CTPoint B) {
        CTRay resul = new CTRay(A, B);
        resul.lineType = LineType.PointPoint;
        resul.rebuildShape();
        return resul;
    }

    /**
     * Creates a new Constructible ray with given 2 points
     *
     * @param A Starting point
     * @param B Second point
     * @return The created object
     */
    public static CTRay make(Point A, Point B) {
        return make(CTPoint.make(A), CTPoint.make(B));
    }

    private CTRay(CTPoint A, CTPoint B) {
        super(A, B);
        this.A = A;
        this.B = B;
        rayToDraw = Ray.make(A.getMathObject(), B.getMathObject());
    }

    @Override
    public CTRay copy() {
        CTRay copy = CTRay.make(A.copy(), B.copy());
        copy.getMp().copyFrom(this.getMp());
        return copy;
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r) {
        rayToDraw.draw(scene, r);

    }

    @Override
    public Ray getMathObject() {
        return rayToDraw;
    }

    @Override
    public void rebuildShape() {
        switch (lineType) {
            case PointPoint:
                break;
            case PointVector:
                B.getMathObject().copyFrom(A.add(dir.getDirection()).getMathObject());
        }
    }

    @Override
    public Vec getDirection() {
        return rayToDraw.getDirection();
    }

}
