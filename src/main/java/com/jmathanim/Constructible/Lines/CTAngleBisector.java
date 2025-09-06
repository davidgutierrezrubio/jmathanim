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

import com.jmathanim.Utils.Vec;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Coordinates;

/**
 * A CTLine that is the angle bisector of 2 other lines or 3 points
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class CTAngleBisector extends CTAbstractLine<CTAngleBisector> {

    private enum LineType {
        PointPointPoint, LineLine
    }
    private LineType bisectorType;
    Vec A;
    Vec B;
    Vec C;
    Vec dirPoint;//The second point of the angle bisector. The first is B

    /**
     * Creates the angle bisector of the angle given by 3 points ABC
     *
     * @param A First point
     * @param B Second point (the vertex of the angle)
     * @param C Third point
     * @return The created object
     */
    public static CTAngleBisector make(Coordinates<?> A, Coordinates<?> B, Coordinates<?> C) {
        CTAngleBisector resul = new CTAngleBisector(A, B, C);
        resul.bisectorType = LineType.PointPointPoint;
        resul.rebuildShape();
        return resul;
    }

    private CTAngleBisector(Coordinates<?> A, Coordinates<?> B, Coordinates<?> C) {
        super(A,B);
        this.A = A.getVec();
        this.B = B.getVec();
        this.C = C.getVec();
        dirPoint = Vec.to(0,0);
    }

    @Override
    public CTAngleBisector copy() {
        CTAngleBisector copy = CTAngleBisector.make(A.copy(), B.copy(), C.copy());
        copy.copyStateFrom(this);
        return copy;
    }

    @Override
    public void rebuildShape() {
        switch (bisectorType) {
            case PointPointPoint:
                Vec vdir = B.to(A).normalize().add(B.to(C).normalize());
                dirPoint.copyCoordinatesFrom(B.add(vdir));
                P1draw.copyCoordinatesFrom(B);
                P2draw.copyCoordinatesFrom(dirPoint);
                break;
            case LineLine:
                //TODO: Implement
                JMathAnimScene.logger.warn("CTAngleBisector Line-Line not implemente yet. Don't worry we're working at it!");
                break;
        }
        lineToDraw.rebuildShape();
    }

    @Override
    public void registerUpdateableHook(JMathAnimScene scene) {
        switch (bisectorType) {
            case PointPointPoint:
                dependsOn(scene, this.A, this.B, this.C);
                break;
            case LineLine:
            //TODO: Implement
        }
    }

}
