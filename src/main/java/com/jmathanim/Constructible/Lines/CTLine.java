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
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Line;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.updateableObjects.Updateable;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class CTLine extends CTAbstractLine {

    protected Line lineToDraw;
    protected CTPoint A;
    protected CTPoint B;
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
        super();
        this.A = A;
        this.B = B;
        lineType = LineType.PointPoint;
        lineToDraw = Line.make(A.getMathObject().copy(), B.getMathObject().copy());
    }

    @Override
    public CTLine copy() {
        CTLine copy = null;
        switch (lineType) {
            case PointPoint:
                copy = CTLine.make(A.copy(), B.copy());
                copy.copyStateFrom(this);
                break;
            case PointVector:
                copy = CTLine.make(A.copy(), this.dir);
                copy.copyStateFrom(this);
                break;
        }
        return copy;
    }

    @Override
    public Vec getDirection() {
        switch (lineType) {
            case PointPoint:
                return P1.to(P2);
            case PointVector:
                return dir.getDirection();
        }
        return null;
    }

    @Override
    public Vec getHoldCoordinates(Vec coordinates) {
        Vec v1 = getDirection().normalize();
        Vec v2 = coordinates.minus(getP1().v);
        return(getP1().v.add(v1.mult(v1.dot(v2))));
    }

    @Override
    public MathObject getMathObject() {
        return lineToDraw;
    }

    @Override
    public void rebuildShape() {
        switch (lineType) {
            case PointPoint:
                P1.v.copyFrom(A.v);
                P2.v.copyFrom(B.v);
                break;
            case PointVector:
                P1.v.copyFrom(A.v);
                P2.v.copyFrom(A.v.add(dir.getDirection()));
        }
        if (!isThisMathObjectFree()) {
            lineToDraw.getP1().v.copyFrom(P1.v);
            lineToDraw.getP2().v.copyFrom(P2.v);
        }
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene
    ) {
        switch (lineType) {
            case PointPoint:
                dependsOn(scene, this.A, this.B);
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
