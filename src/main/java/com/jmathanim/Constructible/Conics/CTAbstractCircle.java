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
import com.jmathanim.Constructible.Points.CTPoint;
import com.jmathanim.mathobjects.Scalar;
import com.jmathanim.mathobjects.hasScalarParameter;

/**
 * An abstract class representing a Constructible circle or similar
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class CTAbstractCircle extends Constructible implements hasScalarParameter,PointOwner {

    /**
     * Returns the geometrical center of the circle or similar
     * @return a newly created CTpoint with the coordinates of center
     */
    public abstract CTPoint getCircleCenter();

    /**
     * Retruns the radius of the circle or similar
     * @return A newly created Scalar object containing the radius
     */
    public abstract Scalar getRadius();

    @Override
    public double getScalar() {
        return getRadius().value;
    }

    @Override
    public void setScalar(double scalar) {
    }
}
