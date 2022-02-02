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
public class ConstrAngleBisectorPointPoint extends FixedConstructible implements HasDirection {

    private enum LineType {
        PointPointPoint, LineLine
    }
    private LineType lineType;
    private final Line lineToDraw;
    ConstrPoint A, B, C;
    Point dirPoint;
    Vec dir;

    public static ConstrAngleBisectorPointPoint make(ConstrPoint A, ConstrPoint B, ConstrPoint C) {
        ConstrAngleBisectorPointPoint resul = new ConstrAngleBisectorPointPoint(A, B, C);
        resul.lineType = LineType.PointPointPoint;
        resul.rebuildShape();
        return resul;
    }

    private ConstrAngleBisectorPointPoint(ConstrPoint A, ConstrPoint B, ConstrPoint C) {
        this.A = A;
        this.B = B;
        this.C = C;
        dirPoint=Point.origin();
        lineToDraw = Line.make(B.getMathObject(), dirPoint);
    }

    @Override
    public ConstrAngleBisectorPointPoint copy() {
        ConstrAngleBisectorPointPoint copy = ConstrAngleBisectorPointPoint.make(A.copy(), B.copy(),C.copy());
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
            case PointPointPoint:
                dir=B.to(A).normalize().add(B.to(C).normalize());
                dirPoint.copyFrom(B.getMathObject().add(dir));
                break;
            case LineLine:
               //TODO: Implement
                break;
        }
    }

    @Override
    public Vec getDirection() {
       rebuildShape();
       return dir;
    }

}
