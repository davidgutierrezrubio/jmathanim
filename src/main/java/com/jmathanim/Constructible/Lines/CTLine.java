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
import com.jmathanim.mathobjects.Line;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class CTLine extends FixedConstructible implements HasDirection {

    private enum LineType {
        PointPoint, PointVector
    }
    private LineType lineType;
    protected Line lineToDraw;
    CTPoint A;
    CTPoint B;
    HasDirection dir;

    public static CTLine make(Line line) {
        return make(line.getP1(), line.getP2());
    }

    public static CTLine make(CTPoint A, HasDirection dir) {
        CTLine resul = new CTLine(A, A.add(dir.getDirection()));
        resul.dir = dir;
        resul.lineType = LineType.PointVector;
        resul.rebuildShape();
        return resul;
    }

    public static CTLine make(Point A, Point B) {
        return CTLine.make(CTPoint.make(A), CTPoint.make(B));
    }

    public static CTLine make(CTPoint A, CTPoint B) {
        CTLine resul = new CTLine(A, B);
        resul.lineType = LineType.PointPoint;
        resul.rebuildShape();
        return resul;
    }

    protected CTLine(CTPoint A, CTPoint B) {
        this.A = A;
        this.B = B;
        lineToDraw = Line.make(A.getMathObject(), B.getMathObject());
    }

    @Override
    public CTLine copy() {
        CTLine copy = CTLine.make(A.copy(), B.copy());
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
        return lineToDraw.getDirection();
    }

    @Override
    public Point getP1() {
        return lineToDraw.getP1();
    }

    @Override
    public Point getP2() {
        return lineToDraw.getP2();
    }
}
