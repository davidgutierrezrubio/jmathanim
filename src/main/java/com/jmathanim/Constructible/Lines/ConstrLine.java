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
public class ConstrLine extends FixedConstructible implements HasDirection {

    private enum LineType {
        PointPoint, PointVector
    }
    private LineType lineType;
    private final Line lineToDraw;
    ConstrPoint A, B;
    HasDirection dir;

    public static ConstrLine make(ConstrPoint A, HasDirection dir) {
        ConstrLine resul = new ConstrLine(A, A.add(dir.getDirection()));
        resul.dir = dir;
        resul.lineType = LineType.PointVector;
        resul.rebuildShape();
        return resul;
    }

    public static ConstrLine make(ConstrPoint A, ConstrPoint B) {
        ConstrLine resul = new ConstrLine(A, B);
        resul.lineType = LineType.PointPoint;
        resul.rebuildShape();
        return resul;
    }

    private ConstrLine(ConstrPoint A, ConstrPoint B) {
        this.A = A;
        this.B = B;
        lineToDraw = Line.make(A.getMathObject(), B.getMathObject());
    }

    @Override
    public ConstrLine copy() {
        ConstrLine copy = ConstrLine.make(A.copy(), B.copy());
        copy.getMp().copyFrom(this.getMp());
        return copy;
    }

    @Override
    public void draw(JMathAnimScene scene, Renderer r) {
        lineToDraw.draw(scene, r);

    }

    @Override
    public MathObject getMathObject() {
        return lineToDraw;
    }

    @Override
    public void rebuildShape() {
        switch (lineType) {
            case PointPoint:
//                v.copyFrom(A.to(B));
                break;
            case PointVector:
                B.getMathObject().copyFrom(A.add(dir.getDirection()).getMathObject());
        }
    }

    @Override
    public Vec getDirection() {
        switch (lineType) {
            case PointPoint:
                return A.to(B);
            default:
                return dir.getDirection();
        }
    }

}
