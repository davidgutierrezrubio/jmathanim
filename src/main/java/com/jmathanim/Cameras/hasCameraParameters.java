/*
 * Copyright (C) 2022 David
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
package com.jmathanim.Cameras;

/**
 * Any object that implements this interface have parameters that can be passed
 * to a camera. Camera parameters are defined by minimum and maximum x values
 * and the vertical center.
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public interface hasCameraParameters {

    /**
     * Returns the minimum, leftmost, horizontal value of viewable area, in math
     * coordinates
     *
     * @return The minimum horizontal value
     */
    public double getMinX();

    /**
     * Returns the maxium, rightmost, horizontal value of viewable area, in math
     * coordinates
     *
     * @return The maxium horizontal value
     */
    public double getMaxX();

    /**
     * Returns the vertical coordinate of the center of viewable area, in math
     * coordinates
     *
     * @return The middle y coordinate
     */
    public double getYCenter();
}
