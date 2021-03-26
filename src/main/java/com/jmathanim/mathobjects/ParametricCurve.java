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

import com.jmathanim.Utils.Vec;
import java.util.ArrayList;
import java.util.function.DoubleUnaryOperator;

/**
 * Shape subclass that represents a parametric curve Functions are defined in
 * its lambda function.
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class ParametricCurve extends Shape {

    public static final double DELTA_DERIVATIVE = .000001d;
    public static final int DEFAULT_NUMBER_OF_POINTS = 50;
    private DoubleUnaryOperator functionXBackup;
    private DoubleUnaryOperator functionYBackup;

    /**
     * Different ways to define a function. Right now only lambda is supported
     */
    public enum FunctionDefinitionType {
        /**
         * Function is defined by a lambda expresion, like (x)-&gt;Math.sin(x)
         */
        LAMBDA_CARTESIAN,
        LAMBDA_POLAR
    }

    public final ArrayList<Double> tPoints;
    public FunctionDefinitionType functionType;
    public DoubleUnaryOperator functionX;
    public DoubleUnaryOperator functionY;

    /**
     * Creates a new parametric curve in cartesian coordinates (x(t),y(t)),
     * using the default number of points defined in
     * {@link DEFAULT_NUMBER_OF_POINTS}
     *
     * @param fx x(t), expressed as a lambda function
     * @param fy y(t), expressed as a lambda function
     * @param tmin Starting t parameter
     * @param tmax Ending t parameter
     * @return The created curve
     */
    public static ParametricCurve make(DoubleUnaryOperator fx, DoubleUnaryOperator fy, double tmin, double tmax) {
        return make(fx, fy, tmin, tmax, DEFAULT_NUMBER_OF_POINTS);
    }

    /**
     * Creates a new parametric curve in cartesian coordinates (x(t),y(t)),
     * using the specified number of points.
     *
     * @param fx x(t), expressed as a lambda function
     * @param fy y(t), expressed as a lambda function
     * @param tmin Starting t parameter
     * @param tmax Ending t parameter
     * @param numPoints Number of points to compute
     * @return The created curve
     */
    public static ParametricCurve make(DoubleUnaryOperator fx, DoubleUnaryOperator fy, double tmin, double tmax, int numPoints) {
        ParametricCurve resul = new ParametricCurve(fx, fy, tmin, tmax, numPoints);
        resul.functionType = FunctionDefinitionType.LAMBDA_CARTESIAN;
        resul.generateFunctionPoints();
        return resul;
    }

    /**
     * Creates a new parametric curve in polar coordinates (r(t),theta(t)),
     * using the default number of points defined in
     * {@link DEFAULT_NUMBER_OF_POINTS}
     *
     * @param fr r(t), expressed as a lambda function
     * @param ftheta theta(t), expressed as a lambda function
     * @param tmin Starting t parameter
     * @param tmax Ending t parameter
     * @return The created curve
     */
    public static ParametricCurve makePolar(DoubleUnaryOperator fr, DoubleUnaryOperator ftheta, double tmin, double tmax) {
        return makePolar(fr, ftheta, tmin, tmax, DEFAULT_NUMBER_OF_POINTS);
    }

    /**
     * Creates a new parametric curve in polar coordinates (r(t),theta(t)),
     * using the specified number of points.
     *
     * @param fr r(t), expressed as a lambda function
     * @param ftheta theta(t), expressed as a lambda function
     * @param tmin Starting t parameter
     * @param tmax Ending t parameter
     * @return The created curve
     */
    public static ParametricCurve makePolar(DoubleUnaryOperator fr, DoubleUnaryOperator ftheta, double tmin, double tmax, int numPoints) {
        ParametricCurve resul = new ParametricCurve(fr, ftheta, tmin, tmax, numPoints);
        resul.functionType = FunctionDefinitionType.LAMBDA_POLAR;
        resul.generateFunctionPoints();
        return resul;
    }

    private ParametricCurve(DoubleUnaryOperator fx, DoubleUnaryOperator fy, double tmin, double tmax, int numPoints) {
        this.functionX = fx;
        this.functionY = fy;
        this.functionType = FunctionDefinitionType.LAMBDA_CARTESIAN;
        this.tPoints = new ArrayList<>();
        for (int n = 0; n < numPoints; n++) {
            double t = tmin + (tmax - tmin) * n / (numPoints - 1);
            tPoints.add(t);
        }
    }

    private ParametricCurve(DoubleUnaryOperator fx, DoubleUnaryOperator fy, ArrayList<Double> xPoints) {
        this.functionX = fx;
        this.functionY = fy;
        this.tPoints = xPoints;
        this.functionType = FunctionDefinitionType.LAMBDA_CARTESIAN;
    }

    private void generateFunctionPoints() {
        for (int n = 0; n < tPoints.size(); n++) {
            double t = tPoints.get(n);
//            double x = getFunctionValueX(t);
//            double y = getFunctionValueY(t);
            double xy[] = getFunctionValue(t);
            Point p = Point.at(xy[0], xy[1]);
            final JMPathPoint jmp = JMPathPoint.curveTo(p);
            this.getPath().addJMPoint(jmp);

        }
        this.get(0).isThisSegmentVisible = this.getPoint(0).isEquivalentTo(this.getPoint(-1), 0.0000001);

        generateControlPoints();
    }

    /**
     * Generate the Bezier control points of the Shape representing the graph of
     * the funcion Approximate derivatives are computed to compute these.
     */
    private void generateControlPoints() {
        for (int n = 0; n < tPoints.size(); n++) {
            JMPathPoint jmp = this.getPath().getJMPoint(n);
            double t = tPoints.get(n);
            if (n < tPoints.size() - 1) {
                final double delta = .3 * (tPoints.get(n + 1) - t);
                Vec v = new Vec(getDerivX(t, 1) * delta, getDerivY(t, 1) * delta);
                jmp.cpExit.copyFrom(jmp.p.add(v));
            }
            if (n > 0) {
                final double delta = .3 * (tPoints.get(n - 1) - t);
                Vec v = new Vec(getDerivX(t, -1) * delta, getDerivY(t, -1) * delta);
                jmp.cpEnter.copyFrom(jmp.p.add(v));
            }

        }
    }

    /**
     * Gets the x component of the curve at the given parameter
     *
     * @param t Parameter in the domain of the function
     * @return x(t)
     */
    public double getFunctionValueX(double t) {
        double xy[] = getFunctionValue(t);
        return xy[0];
    }

    /**
     * Gets the y component of the curve at the given parameter
     *
     * @param t Parameter in the domain of the function
     * @return y(t)
     */
    public double getFunctionValueY(double t) {
        double xy[] = getFunctionValue(t);
        return xy[1];
    }

    /**
     * Gets the coordinates of the point at the given parameter
     *
     * @param t Parameter in the domain of the function
     * @return An arrray containing the values {x(t),y(t)}
     */
    public double[] getFunctionValue(double t) {
        double[] value = new double[]{0, 0};
        switch (this.functionType) {
            case LAMBDA_CARTESIAN:
                value[0] = functionX.applyAsDouble(t);
                value[1] = functionY.applyAsDouble(t);
                break;
            case LAMBDA_POLAR:
                double r = functionX.applyAsDouble(t);
                double theta = functionY.applyAsDouble(t);
                value[0] = r * Math.cos(theta);
                value[1] = r * Math.sin(theta);
                break;
        }
        return value;
    }

    private JMPathPoint addT(double t) {
        int n = 0;
        double x0 = tPoints.get(0);
        while (x0 < t) {
            n++;
            x0 = tPoints.get(n);
        }
        if (x0 == t) {
            return this.getPath().getJMPoint(n);
        } else {
            tPoints.add(n, t);
            double x = getFunctionValueX(t);
            double y = getFunctionValueY(t);
            Point p = Point.at(x, y);
            final JMPathPoint jmp = JMPathPoint.curveTo(p);
            this.getPath().jmPathPoints.add(n, jmp);
            return jmp;
        }
    }

    /**
     * Add the given x values of the abscises to the generation of the function
     * curve. This is useful to explicity include singular points as graph may
     * appear curved if this point is not explicitly include in the array of
     * t-points. If the t parameter is already included, this method has no
     * effect, other than recalcuating control points.
     *
     * @param tPoints t coordinates of the independent variable to include.
     * Variable number of arguments.
     */
    public void addTPoint(Double... tPoints) {
        for (double t : tPoints) {
            addTPoint(t);
        }
        generateControlPoints();
    }

    /**
     * Returns the (approximate) derivative in the y direction in cartesian
     * coordinates
     *
     * @param t Position of the point of the curve
     * @param direction 1 forwards, -1 backwards
     * @return The value of the derivative
     */
    public double getDerivY(double t, int direction) {
        double delta = direction * DELTA_DERIVATIVE;
        double slope = (getFunctionValueY(t + delta) - getFunctionValueY(t)) / delta;
        return slope;
    }

    /**
     * Returns the (approximate) derivative in the x direction in cartesian
     * coordinates
     *
     * @param t Position of the point of the curve
     * @param direction 1 forwards, -1 backwards
     * @return The value of the derivative
     */
    public double getDerivX(double t, int direction) {
        double delta = direction * DELTA_DERIVATIVE;
        double slope = (getFunctionValueX(t + delta) - getFunctionValueX(t)) / delta;
        return slope;
    }

    @Override
    public ParametricCurve copy() {
        ArrayList<Double> xPointsCopy = new ArrayList<>(tPoints);
        ParametricCurve resul = new ParametricCurve(functionX, functionY, xPointsCopy);
        resul.functionType = this.functionType;
        resul.generateFunctionPoints();
        resul.getMp().copyFrom(getMp());
        return resul;
    }

    @Override
    public void saveState() {
        super.saveState();
        this.functionXBackup = this.functionX;
        this.functionYBackup = this.functionY;
    }

    @Override
    public void restoreState() {
        super.restoreState();
        this.functionX = this.functionXBackup;
        this.functionY = this.functionYBackup;
    }

    /**
     * Returns the tangent vector at a value of the independent variable. The
     * componentes are the derivatives
     *
     * @param t0 Value to get the tangent vector
     * @return The tangent vector (x'(t),y'(t))
     */
    public Vec getTangentVector(double t0) {
        return new Vec(getDerivX(t0, 1), getDerivY(t0, 1));
    }

}
