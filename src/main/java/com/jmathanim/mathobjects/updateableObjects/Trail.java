/*
 * Copyright (C) 2020 David Gutiérrez Rubio davidgutierrezrubio@gmail.com
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
package com.jmathanim.mathobjects.updateableObjects;

import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Shape;

/**
 * Shape representing the trail drawed by a moving a point
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Trail extends Shape {

    MathObject marker;
    private boolean cutNext = true;
    private boolean draw = true;

    /**
     * Builds new Trail object. A trail is a updateable Shape that adds a copy
     * of a marker point every frame.
     *
     * @param marker Point to be followed
     * @return The new Trail object
     */
    public static Trail make(MathObject marker) {
        return new Trail(marker);
    }

    /**
     * Returns a new Trail object. A trail is a updateable Shape that adds a
     * copy of a marker point every frame.
     *
     * @param marker Point to be followed
     */
    public Trail(MathObject marker) {
        this.marker = marker;
        getPath().addPoint(marker.getCenter());
        getPath().getJMPoint(0).isThisSegmentVisible = false;
    }

    @Override
    public void update(JMathAnimScene scene) {
        if (draw) {
            JMPathPoint pa = JMPathPoint.lineTo(marker.getCenter());
            pa.isThisSegmentVisible = !cutNext;
            cutNext = false;
            getPath().addJMPoint(pa);
        }
    }

    @Override
    public int getUpdateLevel() {
        return Math.max(super.getUpdateLevel(), marker.getUpdateLevel()) + 1;
    }

    /**
     * Disables adding new elements to the trail, until a call to
     * {@link lowerPen} is made.
     */
    public void raisePen() {
        draw = false;
    }

    /**
     * Enables adding new elements to the trail. By default this is set.
     */
    public void lowerPen() {
        draw = true;
        cutNext = true;
    }
}
