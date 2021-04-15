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
import com.jmathanim.mathobjects.Point;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class CreationStrategy extends Animation {

    private final Point[] pencil;

    public CreationStrategy(double runtime) {
        super(runtime);
        this.pencil = new Point[2];
    }

    /**
     * Returns the "pencil" position.
     *
     * @return An array with 2 point objects. The 0 index stores the previous
     * position of the pencil and 1 stores the current
     */
    public Point[] getPencilPosition() {
        return pencil;
    }

    protected void setPencilPosition(Point previous, Point current) {
        pencil[0] = previous;
        pencil[1] = current;
    }

}
