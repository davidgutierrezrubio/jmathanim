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
package com.jmathanim.mathobjects;

import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.Utils.Rect;
import com.jmathanim.Utils.Vec;
import java.util.ArrayList;
import java.util.function.DoubleUnaryOperator;

/**
 * Shape subclass that represents the graph of a single variable function
 * Functions are defined in its lambda function. For example (x)-&gt;Math.sin(x)
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class FunctionGraph extends Shape {

    public static final double DELTA_DERIVATIVE = .00001d;
    public static final int DEFAULT_NUMBER_OF_POINTS = 49;

    /**
     * Different ways to define a function. Right now only lambda is supported
     */
    public enum FunctionDefinitionType {
        /**
         * Function is defined by a lambda expresion, like (x)-&gt;Math.sin(x)
         */
        LAMBDA
    }

    public DoubleUnaryOperator function;
    public final ArrayList<Double> xPoints;
    public FunctionDefinitionType functionType;
    public DoubleUnaryOperator functionBase;

    public static FunctionGraph make(DoubleUnaryOperator function) {
        Rect r = JMathAnimConfig.getConfig().getCamera().getMathView();
        return new FunctionGraph(function, r.xmin, r.xmax);
    }

    public static FunctionGraph make(DoubleUnaryOperator function, double xmin, double xmax) {
        return new FunctionGraph(function, xmin, xmax);
    }

    public FunctionGraph(DoubleUnaryOperator function, double xmin, double xmax) {
        this(function, xmin, xmax, DEFAULT_NUMBER_OF_POINTS);
    }

    /**
     * Creates a function graph, which is a subclass of Shape
     *
     * @param function Function to draw, in lambda function, for example:
     * (x)-&gt;Math.sin(x)
     * @param xmin Minimum x of the interval to draw the function
     * @param xmax Maximum x of the interval to draw the function
     * @param numPoints Number of points to calculate
     */
    public FunctionGraph(DoubleUnaryOperator function, double xmin, double xmax, int numPoints) {
        style("FunctionGraphDefault");//Default style, if any
        this.function = function;
        this.functionBase = function;
        this.functionType = FunctionDefinitionType.LAMBDA;
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
        this.functionBase = function;
        this.functionType = FunctionDefinitionType.LAMBDA;
        generateFunctionPoints();
    }

    private void generateFunctionPoints() {
        for (int n = 0; n < xPoints.size(); n++) {
            double x = xPoints.get(n);
            double y = getFunctionValue(x);
            Point p = Point.at(x, y);
            final JMPathPoint jmp = JMPathPoint.curveTo(p);
            this.getPath().addJMPoint(jmp);
            if (n == 0) {
                jmp.isThisSegmentVisible = false;
            }
        }
        generateControlPoints();
    }

    /**
     * Generate the Bezier control points of the Shape representing the graph of
     * the funcion Approximate derivatives are computed to compute these.
     */
    private void generateControlPoints() {
        for (int n = 0; n < xPoints.size(); n++) {
            JMPathPoint jmp = this.getPath().getJMPoint(n);
            double x = jmp.p.v.x;
            if (n < xPoints.size() - 1) {
                final double deltaX = .3 * (xPoints.get(n + 1) - x);
                Vec v = new Vec(deltaX, getSlope(x, 1) * deltaX);
                jmp.cp1.copyFrom(jmp.p.add(v));
            }
            if (n > 0) {
                final double deltaX = .3 * (xPoints.get(n - 1) - x);
                Vec v = new Vec(deltaX, getSlope(x, -1) * deltaX);
                jmp.cp2.copyFrom(jmp.p.add(v));
            }

        }
    }

    /**
     * Update the value of the y-points of the graph. This method should be
     * called when the function is changed. Control points are also
     * recalculated.
     */
    public void updatePoints() {
        for (JMPathPoint jmp : this.getPath().jmPathPoints) {
            jmp.p.v.y = getFunctionValue(jmp.p.v.x);
        }
        generateControlPoints();
    }

    public double getFunctionValue(double x) {
        double y = 0;
        if (this.functionType == FunctionDefinitionType.LAMBDA) {
            y = function.applyAsDouble(x);
        }

        return y;
    }

    private JMPathPoint addX(double x) {

        int n = 0;
        double x0 = xPoints.get(0);
        while (x0 < x) {
            n++;
            x0 = xPoints.get(n);
        }
        if (x0 == x) {
            return this.getPath().getJMPoint(n);
        } else {
            xPoints.add(n, x);
            double y = getFunctionValue(x);
            Point p = Point.at(x, y);
            final JMPathPoint jmp = JMPathPoint.curveTo(p);
            this.getPath().jmPathPoints.add(n, jmp);
            return jmp;
        }
    }

    /**
     * Add the given x values of the abscises to the generation of the function
     * curve. This is useful to explicity include singular points (for example,
     * x=0 with (x)-&gt;Math.abs(x)) as graph may appear curved if this point is
     * not explicitly include in the array of x-points. If the x coordinate is
     * always included, this method has no effect, other than recalcuating
     * control points.
     *
     * @param xPoints x coordinates of the abscise to include. Variable number
     * of arguments.
     */
    public void addXPoint(Double... xPoints) {
        for (double x : xPoints) {
            addXPoint(x);
        }
        generateControlPoints();
    }

    public double getSlope(double x, int direction) {
        double delta = direction * DELTA_DERIVATIVE;
        double slope = (getFunctionValue(x + delta) - getFunctionValue(x)) / delta;
        return slope;
    }

    @Override
    public FunctionGraph copy() {
        ArrayList<Double> xPointsCopy = new ArrayList<>(xPoints);
        FunctionGraph resul = new FunctionGraph(function, xPointsCopy);
        resul.mp.copyFrom(mp);
        return resul;
    }

    @Override
    public void saveState() {
        super.saveState();
        this.functionBase = this.function;
    }

    @Override
    public void restoreState() {
        super.restoreState();
        this.functionBase = this.function;
    }

}
