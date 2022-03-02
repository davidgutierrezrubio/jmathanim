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
 * A Constructible Line that pass through A and is orthogonal to a given
 * direction
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class CTLineOrthogonal extends CTAbstractLine {

    protected final CTPoint A;
    protected final HasDirection dir;
    protected final Line lineToDraw;

    /**
     * A CTLine that pass through A and is perpendicular to the segment AB
     *
     * @param A First point
     * @param B Second point
     * @return The created object
     */
    public static CTLineOrthogonal make(Point A, Point B) {
        return make(CTPoint.make(A), CTPoint.make(B));
    }

    /**
     * A CTLine that pass through A and is perpendicular to the segment AB
     *
     * @param A First point
     * @param B Second point
     * @return The created object
     */
    public static CTLineOrthogonal make(CTPoint A, CTPoint B) {
        return make(A, CTSegment.make(A, B));
    }

    /**
     * A CTLine that pass through A and is perpendicular to a object with
     * direction
     *
     * @param A Point of line
     * @param dir An object that implements the HasDirection interface
     * @return The created object
     */
    public static CTLineOrthogonal make(CTPoint A, HasDirection dir) {
        CTLineOrthogonal resul = new CTLineOrthogonal(A, dir);
        resul.rebuildShape();
        return resul;
    }

    /**
     * A CTLine that pass through A and is perpendicular to a object with
     * direction
     *
     * @param A Point of line
     * @param dir An object that implements the HasDirection interface
     * @return The created object
     */
    private CTLineOrthogonal(CTPoint A, HasDirection dir) {
        super();
        this.A = A;
        this.lineType = LineType.PointVector;
        this.dir = dir;
        this.lineToDraw = Line.XAxis();
    }

    @Override
    public CTLineOrthogonal copy() {
        CTLineOrthogonal copy = make(A.copy(), dir);
        copy.getMp().copyFrom(this.getMp());
        return copy;
    }

    @Override
    public void rebuildShape() {
        Vec v = A.v;
        Vec direction = dir.getDirection();
        P1.v.copyFrom(v);

        P2.v.x = v.x - direction.y;
        P2.v.y = v.y + direction.x;
        if (!isThisMathObjectFree()) {
            lineToDraw.getP1().v.copyFrom(P1.v);
            lineToDraw.getP2().v.copyFrom(P2.v);
        }
    }

    @Override
    public MathObject getMathObject() {
        return lineToDraw;
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        scene.registerUpdateable(this.A);
        setUpdateLevel(this.A.getUpdateLevel() + 1);
        if (this.dir instanceof Updateable) {
            scene.registerUpdateable((Updateable) this.dir);
            setUpdateLevel(Math.max(this.A.getUpdateLevel(), ((Updateable) this.dir).getUpdateLevel()) + 1);
        }
    }
}
