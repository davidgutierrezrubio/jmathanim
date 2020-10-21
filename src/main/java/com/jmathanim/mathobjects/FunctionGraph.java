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
package com.jmathanim.mathobjects;

import com.jmathanim.Utils.PathInterpolator;
import java.util.ArrayList;
import java.util.function.DoubleUnaryOperator;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class FunctionGraph extends Shape {

    public DoubleUnaryOperator function;
    public final ArrayList<Double> xPoints;

    public FunctionGraph(DoubleUnaryOperator function, double xmin, double xmax, int numPoints) {
        this.function = function;
        this.xPoints = new ArrayList<>();
        for (int n = 0; n < numPoints; n++) {
            double x = xmin + (xmax - xmin) * n / (numPoints - 1);
            xPoints.add(x);
        }
        generateFunctionPoints();
    }

    public FunctionGraph(DoubleUnaryOperator function, ArrayList<Double> xPoints) {
        this.function = function;
        this.xPoints = xPoints;
        generateFunctionPoints();
    }

    private void generateFunctionPoints() {
        for (int n = 0; n < xPoints.size(); n++) {
            double x = xPoints.get(n);
            double y = function.applyAsDouble(x);
            Point p = Point.at(x, y);
            final JMPathPoint jmp = JMPathPoint.curveTo(p);
            this.jmpath.addJMPoint(jmp);
            if (n == 0) {
                jmp.isThisSegmentVisible = false;
            }
        }
        PathInterpolator.generateControlPointsBySimpleSlopes(this.jmpath);
    }

    public JMPathPoint addX(double x) {

        int n = 0;
        double x0 = xPoints.get(0);
        while (x0 < x) {
            n++;
            x0 = xPoints.get(n);
        }
        if (x0 == x) {
            return this.jmpath.getJMPoint(n);
        } else {
            double y = function.applyAsDouble(x);
            Point p = Point.at(x, y);
            final JMPathPoint jmp = JMPathPoint.curveTo(p);
            this.jmpath.jmPathPoints.add(n, jmp);
            return jmp;
        }
    }

    public void setSingularPoints(Double... singularx) {
        ArrayList<JMPathPoint> jmps = new ArrayList<>();
        for (double x : singularx) {
            JMPathPoint jmp = addX(x);
            jmp.isCurved = false;
            jmps.add(jmp);
        }
        PathInterpolator.generateControlPointsBySimpleSlopes(this.jmpath);
        for (JMPathPoint jmp : jmps) {
            jmp.cp1.copyFrom(jmp.p);
            jmp.cp2.copyFrom(jmp.p);
        }
    }

}
