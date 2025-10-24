/*
 * Copyright (C) 2023 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jmathanim.Constructible.Lines;

import com.jmathanim.Constructible.Conics.CTAbstractCircle;
import com.jmathanim.MathObjects.Coordinates;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public final class CTTangentPointCircle extends CTAbstractLine<CTTangentPointCircle> {

    private final CTAbstractCircle<?> C;
    int numTangent;

    /**
     * Builds a tangent line to a circle from a given point
     *
     * @param A Point to draw tangent
     * @param C Circle that the line should be tangent to
     * @param numTangent Number of tangent, a number 0 o 1. 0 means to take the
     * tangent to the right side if you locate at A looking at the center of the
     * circle. 1 means the left one.
     * @return The tangent line
     */
    public static CTTangentPointCircle make(Coordinates<?> A, CTAbstractCircle<?> C, int numTangent) {
        CTTangentPointCircle resul = new CTTangentPointCircle(A, C, numTangent);
        resul.rebuildShape();
        return resul;
    }

    private CTTangentPointCircle(Coordinates<?> A, CTAbstractCircle<?> C, int numTangent) {
        super(A,Vec.to(1,0));//Trivial line
        this.C = C;
        this.numTangent = numTangent;
    }

    @Override
    public CTTangentPointCircle copy() {
        CTTangentPointCircle copy = CTTangentPointCircle.make(getP1().copy(), C.copy(), numTangent);
        copy.copyStateFrom(this);
        return copy;
    }

    @Override
    public void rebuildShape() {
        //First, I compute an isomoporhism that maps center of circle Cc to (0,0)
        //And A to point (dist(A,Cc)/radius,0)
        //In this conditions...
        //Circle is centered unit circle and A=(p,0) lies on the positive side of X Axis, then
        //x coordinate of tangency point is (p*p+1-h*h)/(2*p)
        //y coordinate is +/- sqrt(1-x*x)
        //Where p is x-coordinate of A, h is distance from A to tangent point
        //h=sqrt(p*p-1), (r=1=radius)
        //So, compute this, makeLengthMeasure the inverse transform and...voilá!

        //Distance from A to center of circle
        double r = C.getCircleRadius().getValue();
        double dist = getP1().to(C.getCircleCenter()).norm();
        double p = dist / r;
        double h = Math.sqrt(p * p - 1);//If p<1 this returns Nan, and so xT and yT

        double xT = (p * p + 1 - h * h) / (2 * p);
        double yT = Math.sqrt(1 - xT * xT);

        if (numTangent != 0) {
            yT = -yT;
        }
        //Use Point.at(A.v.x,A.v.y) instead of A.getMathObject() since
        //we cannot ensure that the associated mathoject is properly updated, and
        //we must use Constructible data, not shown data!
        AffineJTransform transform = AffineJTransform.createDirect2DIsomorphic(
                Vec.to(0,0), Vec.to(p, 0),
                C.getCircleCenter(), getP1(),
                1);

        Vec v = Vec.to(xT, yT);
        v.applyAffineTransform(transform);
        this.P2.copyCoordinatesFrom(v);
        super.rebuildShape();
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        super.registerUpdateableHook(scene);
        dependsOn(scene, C);
    }

}
