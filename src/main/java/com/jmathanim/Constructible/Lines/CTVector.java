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
import com.jmathanim.mathobjects.Arrow2D;
import com.jmathanim.mathobjects.Point;

/**
 * A constructible vector, which is draw as an Arrow2D MathObject
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class CTVector extends CTLine {

    private final Arrow2D arrowToDraw;

    /**
     * Creates a new CTVector from a Vec object. The created vector is located
     * at (0,0).
     *
     * @param vector The Vec object that determines the CTVector
     * @return The created object
     */
    public static CTVector makeVector(Vec vector) {
        CTVector resul = new CTVector(CTPoint.make(Point.origin()), CTPoint.make(Point.at(vector.x, vector.y)));
        resul.rebuildShape();
        return resul;
    }

    /**
     * Creates a new CTVector from A to B.
     *
     * @param A Origin
     * @param B Destiny
     * @return The created object
     */
    public static CTVector makeVector(CTPoint A, CTPoint B) {
        CTVector resul = new CTVector(A, B);
        resul.rebuildShape();
        return resul;
    }

    /**
     * Creates a new CTVector from A to B.
     *
     * @param A Origin
     * @param B Destiny
     * @return The created object
     */
    public static CTVector makeVector(Point A, Point B) {
        return makeVector(CTPoint.make(A), CTPoint.make(B));
    }

    private CTVector(CTPoint A, CTPoint B) {
        super(A, B);
        this.A = A;
        this.B = B;
        arrowToDraw = Arrow2D.makeSimpleArrow2D(this.A.getMathObject().copy(), this.B.getMathObject().copy());
    }

    @Override
    public CTVector copy() {
        CTVector copy = CTVector.makeVector(this.A.copy(), this.B.copy());
        copy.getMp().copyFrom(this.getMp());
        return copy;
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r) {
        arrowToDraw.draw(scene, r);
    }

    @Override
    public Vec getDirection() {
        return A.to(B);
    }

    @Override
    public Arrow2D getMathObject() {
        return arrowToDraw;
    }

    @Override
    public void rebuildShape() {
        this.P1.v.copyFrom(this.A.v);
        this.P2.v.copyFrom(this.B.v);
        if (!isThisMathObjectFree()) {
            arrowToDraw.getStart().v.copyFrom(this.P1.v);
            arrowToDraw.getEnd().v.copyFrom(this.P2.v);
        }
    }

    @Override
    public String toString() {
        Vec v = getDirection();
        return String.format("CTVector[%.2f, %.2f]", v.x, v.y);
    }

}
