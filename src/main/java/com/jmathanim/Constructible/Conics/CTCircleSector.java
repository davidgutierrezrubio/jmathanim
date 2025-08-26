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
public class CTCircleSector extends CTAbstractCircle<CTCircleSector> {

    private final Vec A;
    private final Vec B;

    private CTCircleSector(Coordinates<?> center, Coordinates<?> A, Coordinates<?> B) {
        super();
        setCircleCenter(center);
        this.A = A.getVec();
        this.B = B.getVec();
    }

    /**
     * Creates a new Constructible circle sector
     *
     * @param center Center of circle sector
     * @param A      Starting point. This point also determines sector radius
     * @param B      A reference point to mark the sector angle.
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
     * @param A      Starting point. This point also determines sector radius
     * @param B      A reference point to mark the sector angle.
     * @return The created object
     */
    public static CTCircleSector make(Coordinates<?> center, Coordinates<?> A, Coordinates<?> B) {
        CTCircleSector resul = new CTCircleSector(center, A, B);
        resul.rebuildShape();
        return resul;
    }

    @Override
    public CTCircleSector copy() {
        CTCircleSector copy = CTCircleSector.make(getCircleCenter().copy(), A.copy(), B.copy());
        copy.copyStateFrom(this);
        return copy;
    }

    @Override
    public void copyStateFrom(Stateable obj) {
        if (!(obj instanceof CTCircleSector)) return;
        CTCircleSector cnst = (CTCircleSector) obj;
        super.copyStateFrom(obj);
//            arcTODraw.getPath().clear();
//            Shape copyArc = cnst.getMathObject().copy();
//            arcTODraw.getPath().jmPathPoints.addAll(copyArc.getPath().jmPathPoints);
        this.A.copyCoordinatesFrom(cnst.A);
        this.B.copyCoordinatesFrom(cnst.B);
        rebuildShape();
    }

    @Override
    public void rebuildShape() {
        AffineJTransform tr = AffineJTransform.createDirect2DIsomorphic(
                Vec.to(0, 0),
                Vec.to(1, 0),
                getCircleCenter(),
                A, 1);

        Vec v1 = getCircleCenter().to(A);
        Vec v2 = getCircleCenter().to(B);

        if (!isFreeMathObject()) {
            double angle = v2.getAngle() - v1.getAngle();
            if (angle < 0) {
                angle += 2 * PI;
            }
            Shape referenceArc = Shape.arc(angle);

            referenceArc.get(0).getvEnter().copyCoordinatesFrom(referenceArc.get(0).getV());
            referenceArc.get(-1).getvExit().copyCoordinatesFrom(referenceArc.get(-1).getV());
            referenceArc.get(0).setThisSegmentVisible(true);
            JMPathPoint pp = JMPathPoint.lineTo(Point.origin());
            pp.getvEnter().copyCoordinatesFrom(pp.getV());
            pp.getvExit().copyCoordinatesFrom(pp.getV());
            referenceArc.getPath().getJmPathPoints().add(0, pp);
            referenceArc.applyAffineTransform(tr);
            getMathObject().getPath().clear();
            getMathObject().getPath().getJmPathPoints().addAll(referenceArc.getPath().getJmPathPoints());
        }

    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        dependsOn(scene, getCircleCenter(), A, B);
    }

    @Override
    public Vec getHoldCoordinates(Vec coordinates) {
        //TODO: Implement this, for now, act as a simple circle
        JMathAnimScene.logger.warn("Hold coordinates not implemented yet for CTCircleSector");
        Vec v = coordinates.minus(getCircleCenter()).normalize().mult(getCircleRadius().getValue());
        return getCircleCenter().add(v).getVec();

    }
}
