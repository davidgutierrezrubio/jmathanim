/*
 * Copyright (C) 2020 David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
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
import com.jmathanim.mathobjects.FunctionGraph;
import com.jmathanim.mathobjects.Point;
import java.util.function.DoubleUnaryOperator;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class PointOnFunctionGraph extends Point implements Updateable {

    FunctionGraph fg;
    public Point slopePointRight;
    public Point slopePointLeft;

    public PointOnFunctionGraph(FunctionGraph fg) {
        super();
        this.fg = fg;
        slopePointRight = Point.at(0, 0);
        slopePointLeft = Point.at(0, 0);
    }

    @Override
    public void update(JMathAnimScene scene) {
        super.update(scene);
        this.v.y = this.fg.function.applyAsDouble(this.v.x);
        slopePointRight.v.x = this.v.x + 1;
        slopePointRight.v.y = this.v.y + this.fg.getSlope(this.v.x, -1);

        slopePointLeft.v.x = this.v.x - 1;
        slopePointLeft.v.y = this.v.y - this.fg.getSlope(this.v.x, -1);
    }

}