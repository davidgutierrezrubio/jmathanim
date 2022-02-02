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

import com.jmathanim.Constructible.Points.ConstrPoint;
import com.jmathanim.Constructible.Constructible;
import com.jmathanim.Constructible.FixedConstructible;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Arrow2D;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class ConstrVectorPointPoint extends FixedConstructible implements HasDirection {

    public final ConstrPoint A, B;
    private final Arrow2D arrowToDraw;

    public static ConstrVectorPointPoint make(ConstrPoint A, ConstrPoint B) {
        ConstrVectorPointPoint resul = new ConstrVectorPointPoint(A, B);
        resul.rebuildShape();
        return resul;
    }

    private ConstrVectorPointPoint(ConstrPoint A, ConstrPoint B) {
        this.A = A;
        this.B = B;
        arrowToDraw = Arrow2D.makeSimpleArrow2D(this.A.getMathObject(), this.B.getMathObject());
    }

    @Override
    public ConstrVectorPointPoint copy() {
        ConstrVectorPointPoint copy = ConstrVectorPointPoint.make(this.A.copy(), this.B.copy());
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
    public MathObject getMathObject() {
        return arrowToDraw;
    }

//    public Point getA() {
//        return A;
//    }
//
//    public Point getB() {
//        return B;
//    }

    @Override
    public void rebuildShape() {
        // Nothing to do here...
    }

}
