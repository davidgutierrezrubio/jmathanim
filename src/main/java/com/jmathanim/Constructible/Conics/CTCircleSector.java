/*
 * Copyright (C) 2022 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
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
package com.jmathanim.Constructible.Conics;

import com.jmathanim.Constructible.Points.CTPoint;
import com.jmathanim.Utils.AffineJTransform;
import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.*;

import static com.jmathanim.jmathanim.JMathAnimScene.PI;

/**
 * Represents a Connstructible circle sector
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class CTCircleSector extends CTAbstractCircle {

    private final CTPoint center;
    private final CTPoint A;
    private final CTPoint B;
    private final Shape arcTODraw;

    /**
     * Creates a new Constructible circle sector
     *
     * @param center Center of circle sector
     * @param A Starting point. This point also determines sector radius
     * @param B A reference point to mark the sector angle.
     * @return The created object
     */
    public static CTCircleSector make(CTPoint center, CTPoint A, CTPoint B) {
        CTCircleSector resul = new CTCircleSector(center, A, B);
        resul.rebuildShape();
        return resul;
    }

    /**
     * Overloaded method. Creates a new Constructible circle sector
     *
     * @param center Center of circle sector
     * @param A Starting point. This point also determines sector radius
     * @param B A reference point to mark the sector angle.
     * @return The created object
     */
    public static CTCircleSector make(Point center, Point A, Point B) {
        CTCircleSector resul = new CTCircleSector(CTPoint.make(center), CTPoint.make(A), CTPoint.make(B));
        resul.rebuildShape();
        return resul;
    }

    private CTCircleSector(CTPoint center, CTPoint A, CTPoint B) {
        this.center = center;
        this.A = A;
        this.B = B;
        arcTODraw = new Shape();
    }

    @Override
    public CTPoint getCircleCenter() {
        return center.copy();
    }

    @Override
    public Scalar getRadius() {
        return Scalar.make(center.to(A).norm());
    }

    @Override
    public Shape getMathObject() {
        return arcTODraw;
    }

    @Override
    public CTCircleSector copy() {
        CTCircleSector copy = CTCircleSector.make(center.copy(), A.copy(), B.copy());
        copy.copyStateFrom(this);
        return copy;
    }

    @Override
    public void copyStateFrom(MathObject obj) {
        if (obj instanceof CTCircleSector) {
            CTCircleSector cnst = (CTCircleSector) obj;
//            arcTODraw.getPath().clear();
//            Shape copyArc = cnst.getMathObject().copy();
//            arcTODraw.getPath().jmPathPoints.addAll(copyArc.getPath().jmPathPoints);
            this.A.copyStateFrom(cnst.A);
            this.B.copyStateFrom(cnst.B);
            this.center.copyStateFrom(cnst.center);
        }
        super.copyStateFrom(obj);
        rebuildShape();
    }

    @Override
    public void rebuildShape() {
        AffineJTransform tr = AffineJTransform.createDirect2DIsomorphic(
                Point.at(0, 0),
                Point.at(1, 0),
                new Point(center.v.x,center.v.y),
                new Point(A.v.x,A.v.y), 1);

        Vec v1 = center.to(A);
        Vec v2 = center.to(B);

        if (!isFreeMathObject()) {
            double angle = v2.getAngle() - v1.getAngle();
            if (angle < 0) {
                angle += 2 * PI;
            }
            Shape referenceArc = Shape.arc(angle);

            referenceArc.get(0).cpEnter.copyFrom(referenceArc.get(0).p.v);
            referenceArc.get(-1).cpExit.copyFrom(referenceArc.get(-1).p.v);
            referenceArc.get(0).isThisSegmentVisible = true;
            JMPathPoint pp = JMPathPoint.lineTo(Point.origin());
            pp.cpEnter.copyFrom(pp.p.v);
            pp.cpExit.copyFrom(pp.p.v);
            referenceArc.getPath().jmPathPoints.add(0, pp);
            referenceArc.applyAffineTransform(tr);
            arcTODraw.getPath().clear();
            arcTODraw.getPath().jmPathPoints.addAll(referenceArc.getPath().jmPathPoints);
        }

    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        dependsOn(scene, center, A, B);
    }

    @Override
    public Vec getHoldCoordinates(Vec coordinates) {
        //TODO: Implement this, for now, act as a simple circle
        Vec v = coordinates.minus(getCircleCenter().v).normalize().mult(getRadius().value);
        return getCircleCenter().v.add(v);

    }
}
