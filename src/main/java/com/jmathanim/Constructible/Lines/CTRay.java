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
import com.jmathanim.Constructible.FixedConstructible;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Ray;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class CTRay extends CTLine {

    private enum RayType {
        PointPoint, PointVector
    }
    private RayType rayType;
    private final Ray rayToDraw;

    public static CTRay make(CTPoint A, HasDirection dir) {
        CTRay resul = new CTRay(A, A.add(dir.getDirection()));
        resul.dir = dir;
        resul.rayType = RayType.PointVector;
        resul.rebuildShape();
        return resul;
    }

    public static CTRay make(CTPoint A, CTPoint B) {
        CTRay resul = new CTRay(A, B);
        resul.rayType = RayType.PointPoint;
        resul.rebuildShape();
        return resul;
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
        switch (rayType) {
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
