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

import com.jmathanim.Constructible.Constructible;
import com.jmathanim.Constructible.PointOwner;
import com.jmathanim.mathobjects.*;

/**
 * An abstract class representing a Constructible circle or similar
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class CTAbstractCircle<T extends CTAbstractCircle<T>> extends Constructible<T> implements PointOwner {

    private final Shape circleToDraw;
    private final JMPath originalUnitCirclePath;
    protected Scalar abstractCircleRadius;
    private Coordinates<?> abstractCircleCenter;

    public CTAbstractCircle() {
        circleToDraw = new Shape();
        abstractCircleRadius = Scalar.make(0);
        originalUnitCirclePath = Shape.circle().getPath();
    }

    public CTAbstractCircle(Coordinates<?> abstractCircleCenter, Scalar abstractCircleRadius) {
        this.abstractCircleCenter = abstractCircleCenter;
        this.abstractCircleRadius = abstractCircleRadius;
        circleToDraw = new Shape();
        originalUnitCirclePath = Shape.circle().getPath();
    }

    /**
     * Returns the geometrical center of the circle or similar
     *
     * @return a newly created CTpoint with the coordinates of center
     */
    public final Coordinates<?> getCircleCenter() {
        return abstractCircleCenter;
    }

    public final void setCircleCenter(Coordinates<?> center) {
        abstractCircleCenter = center.getVec();
    }

    /**
     * Retruns the radius of the circle or similar
     *
     * @return A newly created Scalar object containing the radius
     */
    public final Scalar getCircleRadius() {
        return abstractCircleRadius;
    }

    public final void setCircleRadius(Scalar radius) {
        abstractCircleRadius.setValue(radius.getValue());
    }

    public final void setCircleRadius(double radius) {
        abstractCircleRadius = Scalar.make(radius);
    }

    @Override
    public void copyStateFrom(Stateable obj) {
        if (!(obj instanceof CTAbstractCircle)) return;
        super.copyStateFrom(obj);
        CTAbstractCircle<?> ct = (CTAbstractCircle<?>) obj;
        this.setCircleCenter(ct.getCircleCenter());
        this.abstractCircleCenter.copyCoordinatesFrom(ct.getCircleCenter());
        this.abstractCircleRadius.setValue(ct.getCircleRadius().getValue());
    }

    protected JMPath getOriginalUnitCirclePath() {
        return originalUnitCirclePath;
    }

    @Override
    public Shape getMathObject() {
        return circleToDraw;
    }
}
