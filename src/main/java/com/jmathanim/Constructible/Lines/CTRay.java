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
import com.jmathanim.mathobjects.Point;
import com.jmathanim.mathobjects.Ray;
import com.jmathanim.mathobjects.updateableObjects.Updateable;

/**
 * A Constructible ray
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class CTRay extends CTAbstractLine {

    private final Ray rayToDraw;
    private HasDirection dir;
    private final CTPoint A;
    private final CTPoint B;

    /**
     * Creates a new Constructible ray with given point and direction
     *
     * @param A Starting point
     * @param dir Direction, given by any object that implements the interface
     * HasDirection
     * @return The created object
     */
    public static CTRay make(CTPoint A, HasDirection dir) {
        CTRay resul = new CTRay(A, A.add(dir.getDirection()));
        resul.dir = dir;
        resul.lineType = LineType.PointVector;
        resul.rebuildShape();
        return resul;
    }

    /**
     * Creates a new CTRay from a Ray object
     *
     * @param ray Ray to wrap into
     * @return The CTRay object created
     */
    public static CTRay make(Ray ray) {
        return make(ray.getP1(), ray.getP2());
    }

    /**
     * Creates a new Constructible ray with given 2 points
     *
     * @param A Starting point
     * @param B Second point
     * @return The created object
     */
    public static CTRay make(CTPoint A, CTPoint B) {
        CTRay resul = new CTRay(A, B);
        resul.lineType = LineType.PointPoint;
        resul.rebuildShape();
        return resul;
    }

    /**
     * Creates a new Constructible ray with given 2 points
     *
     * @param A Starting point
     * @param B Second point
     * @return The created object
     */
    public static CTRay make(Point A, Point B) {
        return make(CTPoint.make(A), CTPoint.make(B));
    }

    private CTRay(CTPoint A, CTPoint B) {
        super();
        this.A = A;
        this.B = B;
        rayToDraw = Ray.make(A.getMathObject().copy(), B.getMathObject().copy());
    }

    @Override
    public CTRay copy() {
        CTRay copy = CTRay.make(A.copy(), B.copy());
        copy.copyStateFrom(this);
        return copy;
    }

    @Override
    public Ray getMathObject() {
        return rayToDraw;
    }

    @Override
    public void rebuildShape() {
        this.P1.v.copyFrom(A.v);
        switch (lineType) {
            case PointPoint:
                this.P2.v.copyFrom(B.v);
                break;
            case PointVector:
                this.P2.v.copyFrom(this.P1.v.add(dir.getDirection()));
        }
        if (!isFreeMathObject()) {
            rayToDraw.getP1().v.copyFrom(this.P1.v);
            rayToDraw.getP2().v.copyFrom(this.P2.v);
        }
    }

    @Override
    public Vec getDirection() {
        return rayToDraw.getDirection();
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
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
     @Override
    public Vec getHoldCoordinates(Vec coordinates) {
        Vec v1 = getDirection().normalize();
        Vec v2 = coordinates.minus(getP1().v);
        double dotProd = v1.dot(v2);
        dotProd = Math.max(dotProd, 0);
        return getP1().v.add(v1.mult(dotProd));
    }
}
