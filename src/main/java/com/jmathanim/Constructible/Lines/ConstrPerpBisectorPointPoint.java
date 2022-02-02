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

import com.jmathanim.Constructible.ConstrPoint;
import com.jmathanim.Constructible.Constructible;
import com.jmathanim.Constructible.FixedConstructible;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Line;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class ConstrPerpBisectorPointPoint extends FixedConstructible implements HasDirection {

    ConstrPoint A, B;
    private final Line lineToDraw;

    public static ConstrPerpBisectorPointPoint make(ConstrPoint A, ConstrPoint B) {
        ConstrPerpBisectorPointPoint resul = new ConstrPerpBisectorPointPoint(A, B);
        resul.rebuildShape();
        return resul;
    }

    private ConstrPerpBisectorPointPoint(ConstrPoint A, ConstrPoint B) {
        this.A = A;
        this.B = B;
        lineToDraw = new Line(A.getMathObject(),B.getMathObject());
    }

    @Override
    public ConstrPerpBisectorPointPoint copy() {
        ConstrPerpBisectorPointPoint copy = new ConstrPerpBisectorPointPoint(A.copy(), B.copy());
        copy.getMp().copyFrom(this.getMp());
        return copy;
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r) {
        lineToDraw.draw(scene, r);
    }

    @Override
    public Vec getDirection() {
        return lineToDraw.getDirection();
    }

    @Override
    public MathObject getMathObject() {
        return lineToDraw;
    }

    @Override
    public void rebuildShape() {
        Point C = A.getMathObject().interpolate(B.getMathObject(), .5);
        Vec v = A.to(B);

        lineToDraw.getP1().v.x = C.v.x;
        lineToDraw.getP1().v.y = C.v.y;

        lineToDraw.getP2().v.x = C.v.x - v.y;
        lineToDraw.getP2().v.y = C.v.y + v.x;
    }

}
