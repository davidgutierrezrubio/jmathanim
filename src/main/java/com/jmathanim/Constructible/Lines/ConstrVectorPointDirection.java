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

import com.jmathanim.Constructible.Constructible;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class ConstrVectorPointDirection extends Constructible implements HasDirection {

    public final Point A, B;
    public final Vec v;
    private final Shape segmentToDraw;

    public static ConstrVectorPointDirection make(Point A, Vec v) {
        ConstrVectorPointDirection resul = new ConstrVectorPointDirection(A, v);
        resul.rebuildShape();
        return resul;
    }

    private ConstrVectorPointDirection(Point A, Vec v) {
        this.A = A;
        this.B = A.add(v);
        this.v = v;
        segmentToDraw = Shape.segment(this.A, B);
    }

    @Override
    public ConstrVectorPointDirection copy() {
        ConstrVectorPointDirection copy = ConstrVectorPointDirection.make(this.A.copy(), v.copy());
        copy.getMp().copyFrom(this.getMp());
        return copy;
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r) {
        rebuildShape();
        segmentToDraw.draw(scene, r);
    }

    @Override
    public Vec getDirection() {
        return v.copy();
    }

    @Override
    public MathObject getMathObject() {
        return segmentToDraw;
    }

    public Point getA() {
        return A;
    }

    public Point getB() {
        return B;
    }

    @Override
    public void rebuildShape() {
        this.B.copyFrom(A.add(v));
    }

}
