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
package com.jmathanim.Constructible;

import com.jmathanim.Utils.Vec;

/**
 * Interface that implements any constructible object that can holds a point
 * (Point on Object), like CTLine, CTCircle...
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public interface PointOwner {
    /**
     * Computes the coordinates of the Point projected into the holding object
     * @param coordinates of the point to project
     * @return Projected coordinates
     */
    public Vec getHoldCoordinates(Vec coordinates);
}
