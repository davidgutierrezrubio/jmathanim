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
import com.jmathanim.Constructible.Points.CTPoint;
import com.jmathanim.Renderers.Renderer;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Line;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Shape;
import com.jmathanim.mathobjects.updateableObjects.Updateable;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class CTLine extends Constructible implements HasDirection {

    protected enum LineType {
        PointPoint, PointVector
    }
    protected LineType lineType;
    protected Line lineToDraw;
    CTPoint A;
    CTPoint B;
    HasDirection dir;

    /**
     * Creates a Constructible line from a Line
     *
     * @param line Line object
     * @return The created object
     */
    public static CTLine make(Line line) {
        return make(line.getP1(), line.getP2());
    }

    /**
     * Creates a Constructible line from a point and any object that implements
     * the HasDirection interface
     *
     * @param A A point of the line
     * @param dir A MathObject with a direction (Line, Ray, Arrow2D,
     * CTSegment,CTLine...)
     * @return The created object
     */
    public static CTLine make(CTPoint A, HasDirection dir) {
        CTLine resul = new CTLine(A, A.add(dir.getDirection()));
        resul.dir = dir;
        resul.lineType = LineType.PointVector;
        resul.rebuildShape();
        return resul;
    }

    /**
     * Creates a Constructible line given by 2 points
     *
     * @param A First point
     * @param B Second point
     * @return The created object
     */
    public static CTLine make(Point A, Point B) {
        return CTLine.make(CTPoint.make(A), CTPoint.make(B));
    }

    /**
     * Creates a Constructible line given by 2 points
     *
     * @param A First point
     * @param B Second point
     * @return The created object
     */
    public static CTLine make(CTPoint A, CTPoint B) {
        CTLine resul = new CTLine(A, B);
        resul.lineType = LineType.PointPoint;
        resul.rebuildShape();
        return resul;
    }

    protected CTLine(CTPoint A, CTPoint B) {
        this.A = A;
        this.B = B;
        lineType = LineType.PointPoint;
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

    @Override
    public <T extends MathObject> T applyAffineTransform(AffineJTransform transform) {
        A.applyAffineTransform(transform);
        B.applyAffineTransform(transform);
        return (T) this;
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        switch (lineType) {
            case PointPoint:
                scene.registerUpdateable(this.A, this.B);
                setUpdateLevel(Math.max(this.A.getUpdateLevel(), this.B.getUpdateLevel()) + 1);
                break;
            case PointVector:
                scene.registerUpdateable(this.A);
                setUpdateLevel(this.A.getUpdateLevel() + 1);
                if (this.dir instanceof Updateable) {
                    scene.registerUpdateable((Updateable) this.dir);
                    setUpdateLevel(Math.max(this.A.getUpdateLevel(), ((Updateable) this.dir).getUpdateLevel()) + 1);
                }
        }
    }
}
