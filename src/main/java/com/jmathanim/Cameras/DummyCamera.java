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
package com.jmathanim.Cameras;

import com.jmathanim.Utils.Vec;

/**
 * A dummy camera that does not perform scale changes. Used primarily for fast
 * jmpath to fxpath conversions, to benefit from javafx algorithms
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class DummyCamera extends Camera {

    public DummyCamera() {
        super(null, 0, 0);
    }

    @Override
    public double[] mathToScreenFX(Vec p) {
        return new double[]{p.x, p.y};
    }

    @Override
    public double[] mathToScreen(double mathX, double mathY) {
        return new double[]{mathX, mathY};
    }

    @Override
    public double mathToScreen(double mathScalar) {
        return mathScalar;
    }

    @Override
    public double[] screenToMath(double x, double y) {
        return new double[]{x, y};
    }

}
