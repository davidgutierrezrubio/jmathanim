/*
 * Copyright (C) 2022 David Gutierrez Rubio davidgutierrezrubio@gmail.com
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
import com.jmathanim.mathobjects.Coordinates;
import com.jmathanim.mathobjects.Scalar;
import com.jmathanim.mathobjects.Shape;
import com.jmathanim.mathobjects.Stateable;

import static com.jmathanim.jmathanim.JMathAnimScene.PI;
import static com.jmathanim.jmathanim.JMathAnimScene.logger;

/**
 * A Constructible circle arc
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class CTCircleArc extends CTAbstractCircle<CTCircleArc> {

    private final CTPoint A;
    private final CTPoint B;

    protected CTCircleArc(CTPoint center, CTPoint A, CTPoint B) {
        super(center, Scalar.make(0));
        this.A = A;
        this.B = B;
    }

    /**
     * Creates a new Constructible circle arc
     *
     * @param center Center of arc
     * @param A      Starting point. Arc will pass through this point
     * @param B      Point that determines the angle of the arc.
     * @return The created arc
     */
    public static CTCircleArc make(Coordinates<?> center, Coordinates<?> A, Coordinates<?> B) {
        CTCircleArc resul = new CTCircleArc(CTPoint.make(center), CTPoint.make(A), CTPoint.make(B));
        resul.rebuildShape();
        return resul;
    }


    @Override
    public CTCircleArc copy() {
        CTCircleArc copy = CTCircleArc.make(getCircleCenter().copy(), A.copy(), B.copy());
        copy.copyStateFrom(this);
        return copy;
    }

    @Override
    public void copyStateFrom(Stateable obj) {
        if (!(obj instanceof CTCircleArc)) return;
        CTCircleArc cnst = (CTCircleArc) obj;
        super.copyStateFrom(cnst);
        this.getCircleCenter().copyCoordinatesFrom(cnst.getCircleCenter());
        this.A.copyStateFrom(cnst.A);
        this.B.copyStateFrom(cnst.B);
        super.copyStateFrom(obj);
    }

    @Override
    public void rebuildShape() {
        AffineJTransform tr = AffineJTransform.createDirect2DIsomorphic(
                Vec.to(0, 0),
                Vec.to(1, 0),
                getCircleCenter().copy(),
                A.copy(), 1);

        Vec v1 = getCircleCenter().to(A);
        Vec v2 = getCircleCenter().to(B);

        if (!isFreeMathObject()) {
            double angle = v2.getAngle() - v1.getAngle();
            if (angle < 0) {
                angle += 2 * PI;
            }
            getMathObject().getPath().clear();
            getMathObject().getPath().copyStateFrom(Shape.arc(angle).getPath());
            getMathObject().applyAffineTransform(tr);
        }

    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        dependsOn(scene, getCircleCenter(), A, B);
    }

    @Override
    public Vec getHoldCoordinates(Vec coordinates) {
        //TODO: Implement this, for now, act as a simple circle
        logger.warn("Hold coordinates not fully implemented yet for CTCircleArc");
        Vec v = coordinates.minus(getCircleCenter()).normalize().mult(getCircleRadius().getValue());
        return getCircleCenter().add(v).getVec();

    }
}
