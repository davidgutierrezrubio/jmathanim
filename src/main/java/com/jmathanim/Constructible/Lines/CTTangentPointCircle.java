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
import com.jmathanim.Constructible.Constructible;
import com.jmathanim.Constructible.Points.CTPoint;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Vec;
import com.jmathanim.mathobjects.Line;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Point;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public final class CTTangentPointCircle extends CTAbstractLine {

    private CTPoint A;
    private Line lineToDraw;
    private CTAbstractCircle C;
    int numTangent;

    /**
     * Builds a tangent line to a circle from a given point
     *
     * @param A Point to draw tangent
     * @param C Circle that the line should be tangent to
     * @param numTangent Number of tangent, a number 1 o 2. 1 means to take the
     * tangent to the right side if you locate at A looking at the center of the
     * circle. 2 means the left one.
     * @return The tangent line
     */
    public static CTTangentPointCircle make(CTPoint A, CTAbstractCircle C, int numTangent) {
        CTTangentPointCircle resul = new CTTangentPointCircle(A, C, numTangent);
        resul.rebuildShape();
        return resul;
    }

    private CTTangentPointCircle(CTPoint A, CTAbstractCircle C, int numTangent) {
        super();
        this.C = C;
        this.A = A;
        this.numTangent = numTangent;
        this.lineToDraw = Line.XAxis();//Trivial Line to initialize
    }

    @Override
    public Constructible copy() {
        CTTangentPointCircle copy = CTTangentPointCircle.make(A.copy(), (CTAbstractCircle) C.copy(), numTangent);
        copy.copyStateFrom(this);
        return copy;
    }

    @Override
    public MathObject getMathObject() {
        return lineToDraw;
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
        //So, compute this, make the inverse transform and...voilá!

        //Distance from A to center of circle
        double r = C.getRadius().value;
        double dist = A.to(C.getCircleCenter()).norm();
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
                Point.origin(), Point.at(p, 0),
                C.getCenter(), Point.at(A.v.x, A.v.y),
                1);

        Vec v = Vec.to(xT, yT);
        v.applyAffineTransform(transform);
        this.P2.v.copyFrom(v); //Tangent point
        this.P1.v.copyFrom(this.A.v); //Exterior point
        lineToDraw.getP1().v.copyFrom(this.P1.v);
        lineToDraw.getP2().v.copyFrom(this.P2.v);
    }

}
