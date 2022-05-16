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
import static com.jmathanim.jmathanim.JMathAnimScene.PI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;

/**
 * Shape subclass that represents the graph of a single variable function
 * Functions are defined in its lambda function. For example (x)-&gt;Math.sin(x)
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class FunctionGraph extends Shape implements hasScalarParameter {

    public static final double DELTA_DERIVATIVE = .00001d;
    public static final int DEFAULT_NUMBER_OF_POINTS = 49;
    public static final double CONTINUUM_THRESHOLD = 100;
    public static final double ANGLE_THRESHOLD = 30 * PI / 180;//10 degrees

    @Override
    public double getScalar() {
        return this.w;
    }

    @Override
    public void setScalar(double scalar) {
        this.w = scalar;
        updatePoints();
    }

    /**
     * Different ways to define a function. Right now only lambda is supported
     */
    public enum FunctionDefinitionType {
        /**
         * Function is defined by a lambda expresion, like (x)-&gt;Math.sin(x)
         */
        LAMBDA
    }

    public DoubleBinaryOperator function;
    public final ArrayList<Double> xPoints;
    public FunctionDefinitionType functionType;
    public DoubleBinaryOperator functionBase;
    private double w;

    public static FunctionGraph make(DoubleBinaryOperator function) {
        Rect r = JMathAnimConfig.getConfig().getCamera().getMathView();
        FunctionGraph resul = new FunctionGraph(function, r.xmin, r.xmax);
        resul.adaptativeAddPoints();
        resul.generateFunctionPoints();
        return resul;
    }

    public static FunctionGraph make(DoubleBinaryOperator function, double xmin, double xmax) {
        FunctionGraph resul = new FunctionGraph(function, xmin, xmax);
        resul.adaptativeAddPoints();
        resul.generateFunctionPoints();
        return resul;
    }

    public static FunctionGraph make(DoubleUnaryOperator function) {
        Rect r = JMathAnimConfig.getConfig().getCamera().getMathView();
        FunctionGraph resul = new FunctionGraph((x, w) -> function.applyAsDouble(x), r.xmin, r.xmax);
        resul.adaptativeAddPoints();
        resul.generateFunctionPoints();
        return resul;
    }

    public static FunctionGraph make(DoubleUnaryOperator function, double xmin, double xmax) {
        FunctionGraph resul = new FunctionGraph((x, w) -> function.applyAsDouble(x), xmin, xmax);
        resul.adaptativeAddPoints();
        resul.generateFunctionPoints();
        return resul;
    }

    public static FunctionGraph make(DoubleUnaryOperator function, double xmin, double xmax, int numPoints) {
        FunctionGraph resul = new FunctionGraph((x, w) -> function.applyAsDouble(x), xmin, xmax, numPoints);
        resul.generateFunctionPoints();
        return resul;
    }

    private FunctionGraph(DoubleBinaryOperator function, double xmin, double xmax) {
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
    private FunctionGraph(DoubleBinaryOperator function, double xmin, double xmax, int numPoints) {
        style("FunctionGraphDefault");// Default style, if any
        this.w = 1;
        this.function = function;
        this.functionBase = function;
        this.functionType = FunctionDefinitionType.LAMBDA;
        this.xPoints = new ArrayList<>();
        for (int n = 0; n < numPoints; n++) {
            double x = xmin + (xmax - xmin) * n / (numPoints - 1);
            xPoints.add(x);
        }

    }

    public FunctionGraph(DoubleBinaryOperator function, ArrayList<Double> xPoints) {
        style("FunctionGraphDefault");// Default style, if any
        this.w = 1;
        this.function = function;
        this.xPoints = xPoints;
        this.functionBase = function;
        this.functionType = FunctionDefinitionType.LAMBDA;
        generateFunctionPoints();
    }

    private void generateFunctionPoints() {
        for (int n = 0; n < xPoints.size(); n++) {
            double x = xPoints.get(n);
            double y = getFunctionValue(x, this.w);
            Point p = Point.at(x, y);
            final JMPathPoint jmp = JMPathPoint.curveTo(p);
            this.getPath().addJMPoint(jmp);
            if (n == 0) {
                jmp.isThisSegmentVisible = false;
            }
        }

        generateControlPoints();
    }

    private void adaptativeAddPoints() {
        ArrayList<Double> newPoints = new ArrayList<>();
        //Add points where needed (adaptative)
        //For 3 consecutive points in the grap p0,p1,p2
        //compute the angle between p0p1 and p1p2
        //if this angle is greater that the ANGLE_THRESHOLD (right now, this is set to 30 degrees)
        //then add the 2 middle points between p0, p1 and p1, p2
        boolean goon = true;
        int recursionCounter = 0;
        while (goon) {
            goon = false; //By default this is false, so if in the first pass no new points are added, stop
            for (int n = 0; n < xPoints.size() - 2; n++) {
                double x0 = xPoints.get(n);
                double x1 = xPoints.get(n + 1);
                double x2 = xPoints.get(n + 2);
                double y0 = getFunctionValue(x0, this.w);
                double y1 = getFunctionValue(x1, this.w);
                double y2 = getFunctionValue(x2, this.w);
                Vec v1 = Vec.to(x1 - x0, y1 - y0);
                Vec v2 = Vec.to(x2 - x1, y2 - y1);
                double ang = v1.getAngle() - v2.getAngle();
                if (Math.abs(ang) > ANGLE_THRESHOLD) {
                    newPoints.add(.5 * (x1 + x2));
                    newPoints.add(.5 * (x0 + x1));
                    goon = true;//New points added, a new complete turn is worth the risk!
                }
            }
            xPoints.addAll(newPoints);
            Collections.sort(xPoints);
            newPoints.clear();
            recursionCounter++;
            if (recursionCounter > 3) {//Make 3 complete turns to the array
                goon = false;
            }

        }
    }

    /**
     * Generate the Bezier control points of the Shape representing the graph of
     * the funcion Approximate derivatives are computed to compute these.
     */
    protected void generateControlPoints() {
        for (int n = 0; n < xPoints.size(); n++) {
            JMPathPoint jmp = this.get(n);
            double x = jmp.p.v.x;
            if (n < xPoints.size() - 1) {
                final double deltaX = .3 * (xPoints.get(n + 1) - x);
                Vec v = new Vec(deltaX, getSlope(x, 1) * deltaX);
                jmp.cpExit.copyFrom(jmp.p.add(v));
            }
            if (n > 0) {
                final double deltaX = .3 * (xPoints.get(n - 1) - x);
                Vec v = new Vec(deltaX, getSlope(x, -1) * deltaX);
                jmp.cpEnter.copyFrom(jmp.p.add(v));
                double h = x - xPoints.get(n - 1);
                double deriv = (getFunctionValue(x, this.w) - getFunctionValue(xPoints.get(n - 1), this.w)) / h;
                jmp.isThisSegmentVisible = (Math.abs(deriv) < CONTINUUM_THRESHOLD);
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
            jmp.p.v.y = getFunctionValue(jmp.p.v.x, this.w);
        }
        generateControlPoints();
    }

    /**
     * Evaluate the function at the given abscise
     *
     * @param x The abscise
     * @return The value of the function
     */
    public double getFunctionValue(double x) {
        return getFunctionValue(x, this.w);
    }

    /**
     * Evaluate the function at the given abscise
     *
     * @param x The abscise
     * @param w Second parameter of function
     * @return The value of the function
     */
    public double getFunctionValue(double x, double w) {
        double y = 0;
        if (this.functionType == FunctionDefinitionType.LAMBDA) {
            y = function.applyAsDouble(x, w);
        }

        return y;
    }

    /**
     * Adds an abscise value to be explicitely drawed in the graph. This may
     * prevent distorst of the graph when a singular point is not evaluated. For
     * example abs(x-PI) has a singular point at PI that needs to be evaluated
     * in order to avoid "rounded corners".
     *
     * @param x Abscise value to add
     * @return The JMPathPoint of the path that results from adding the new
     * point
     */
    public JMPathPoint addX(double x) {
        int n = 0;
        if (!xPoints.isEmpty()) {
            if (xPoints.get(xPoints.size() - 1) < x) {
                n = xPoints.size();
            } else {
                double x0 = xPoints.get(0);
                while (x0 < x) {
                    n++;
                    x0 = xPoints.get(n);
                }
                if (x0 == x) {
                    return this.get(n);
                }
            }
        }
        xPoints.add(n, x);
        double y = getFunctionValue(x, this.w);
        Point p = Point.at(x, y);
        final JMPathPoint jmp = JMPathPoint.curveTo(p);
        this.getPath().jmPathPoints.add(n, jmp);
        return jmp;
    }

    public double getSlope(double x, int direction) {
        double delta = direction * DELTA_DERIVATIVE;
        double slope = (getFunctionValue(x + delta, this.w) - getFunctionValue(x, this.w)) / delta;
        return slope;
    }

    @Override
    public FunctionGraph copy() {
        ArrayList<Double> xPointsCopy = new ArrayList<>(xPoints);
        FunctionGraph resul = new FunctionGraph(function, xPointsCopy);
        resul.getMp().copyFrom(getMp());
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

    /**
     * Returns a Shape object representing the enclosed of the funcion between
     * given limits
     *
     * @param a Lower limit
     * @param b Upper limit
     * @return A Shape object with the area
     */
    public Shape getAreaShape(double a, double b) {
        double ma = Math.min(a, b);
        double mb = Math.max(a, b);
        FunctionGraph funcAux = FunctionGraph.make(function, ma, mb);
        for (double x : xPoints) {//Add any abscise from original function
            if ((x >= ma) && (x <= mb)) {
                funcAux.addX(x);
            }
        }
        funcAux.generateControlPoints();
        JMPath areaPath = funcAux.getPath();
        areaPath.addPoint(Point.at(mb, 0), Point.at(ma, 0));
        areaPath.jmPathPoints.get(0).isThisSegmentVisible = true;
        return new Shape(areaPath);
    }
 @Override
    public void copyStateFrom(MathObject obj) {
        if (!(obj instanceof FunctionGraph)) {
            return;
        }
        FunctionGraph fg = (FunctionGraph) obj;
        this.getMp().copyFrom(fg.getMp());

        getPath().copyStateFrom(fg.getPath());
        function=fg.function;
        setScalar(fg.getScalar());
    }
}
