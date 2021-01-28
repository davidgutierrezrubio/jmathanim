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
package com.jmathanim.Animations.Strategies.ShowCreation;

import com.jmathanim.Animations.Animation;
import com.jmathanim.mathobjects.JMPathPoint;
import com.jmathanim.mathobjects.Point;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class CreationStrategy extends Animation {

    private final JMPathPoint pencil;

    public CreationStrategy(double runtime) {
        super(runtime);
        this.pencil = new JMPathPoint(Point.at(0, 0), false, JMPathPoint.JMPathPointType.NONE);
    }

    /**
     * Returns the "pencil" position, the current JMPathPoint that is drawing in
     * this moment
     *
     * @return A JMathPoint that marks the current drawing
     */
    public JMPathPoint getPencilPosition() {
        return pencil;
    }

    protected void setPencilPosition(JMPathPoint p) {
        pencil.copyFrom(p);
    }

}
