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
    private DoubleUnaryOperator functionZBackup;

    /**
     * Different ways to define a function. Right now only lambda is supported
     */
    public enum FunctionDefinitionType {
        /**
         * Function is defined by a lambda expresion, like (x)-&gt;Math.sin(x)
         */
        LAMBDA_CARTESIAN, LAMBDA_POLAR
    }

    public final ArrayList<Double> tPoints;
    public FunctionDefinitionType functionType;
    public DoubleUnaryOperator functionX;
    public DoubleUnaryOperator functionY;
    public DoubleUnaryOperator functionZ;

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
        return make(fx, fy, x -> 0, tmin, tmax, DEFAULT_NUMBER_OF_POINTS);
    }

    /**
     * Creates a new parametric curve (3D version) in cartesian coordinates
     * (x(t),y(t),z(t)), using the specified number of points.
     *
     * @param fx x(t), expressed as a lambda function
     * @param fy y(t), expressed as a lambda function
     * @param fz z(t), expressed as a lambda function
     * @param tmin Starting t parameter
     * @param tmax Ending t parameter
     * @param numPoints Number of points to compute
     * @return The created curve
     */
    public static ParametricCurve make(DoubleUnaryOperator fx, DoubleUnaryOperator fy, DoubleUnaryOperator fz, double tmin, double tmax,
            int numPoints) {
        ParametricCurve resul = new ParametricCurve(fx, fy, fz, tmin, tmax, numPoints);
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
    public static ParametricCurve makePolar(DoubleUnaryOperator fr, DoubleUnaryOperator ftheta, double tmin,
            double tmax) {
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
    public static ParametricCurve makePolar(DoubleUnaryOperator fr, DoubleUnaryOperator ftheta, double tmin,
            double tmax, int numPoints) {
        ParametricCurve resul = new ParametricCurve(fr, ftheta, t -> 0, tmin, tmax, numPoints);
        resul.functionType = FunctionDefinitionType.LAMBDA_POLAR;
        resul.generateFunctionPoints();
        return resul;
    }

    private ParametricCurve(DoubleUnaryOperator fx, DoubleUnaryOperator fy, DoubleUnaryOperator fz, double tmin, double tmax, int numPoints) {
        this.functionX = fx;
        this.functionY = fy;
        this.functionZ = fz;
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
        this.functionZ = t -> 0;
        this.tPoints = xPoints;
        this.functionType = FunctionDefinitionType.LAMBDA_CARTESIAN;
    }

    private void generateFunctionPoints() {
        for (int n = 0; n < tPoints.size(); n++) {
            double t = tPoints.get(n);
            Vec v = getFunctionValue(t);
            Point p = new Point(v);
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
            JMPathPoint jmp = get(n);
            double t = tPoints.get(n);
            if (n < tPoints.size() - 1) {
                Vec tv = getTangentVector(t, 1);
                final double delta = .3 * (tPoints.get(n + 1) - t);
                Vec v = tv.mult(delta);
                jmp.cpExit.copyFrom(jmp.p.add(v));
            }
            if (n > 0) {
                Vec tv = getTangentVector(t, -1);
                final double delta = .3 * (tPoints.get(n - 1) - t);
                Vec v = tv.mult(delta);
                jmp.cpEnter.copyFrom(jmp.p.add(v));
            }

        }
    }

    /**
     * Gets the coordinates of the point at the given parameter
     *
     * @param t Parameter in the domain of the function
     * @return An arrray containing the values {x(t),y(t)}
     */
    public Vec getFunctionValue(double t) {
        double[] value = new double[]{0, 0, 0};
        switch (this.functionType) {
            case LAMBDA_CARTESIAN:
                value[0] = functionX.applyAsDouble(t);
                value[1] = functionY.applyAsDouble(t);
                value[2] = functionZ.applyAsDouble(t);
                break;
            case LAMBDA_POLAR:
                double r = functionX.applyAsDouble(t);
                double theta = functionY.applyAsDouble(t);
                value[0] = r * Math.cos(theta);
                value[1] = r * Math.sin(theta);
                value[2] = 0;//TODO: adapt this to 3D
                break;
        }
        return Vec.to(value[0], value[1], value[2]);
    }

    private JMPathPoint addT(double t) {
        int n = 0;
        double x0 = tPoints.get(0);
        while (x0 < t) {
            n++;
            x0 = tPoints.get(n);
        }
        if (x0 == t) {
            return get(n);
        } else {
            tPoints.add(n, t);
            Vec v = getFunctionValue(t);
            Point p = new Point(v);
            final JMPathPoint jmp = JMPathPoint.curveTo(p);
            this.getPath().jmPathPoints.add(n, jmp);
            return jmp;
        }
    }

    /**
     * Add the given t values of the parameter to the generation of the function
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
            addT(t);
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
    public Vec getTangentVector(double t, int direction) {
        double delta = direction * DELTA_DERIVATIVE;
        Vec v1 = getFunctionValue(t + delta);
        Vec v2 = getFunctionValue(t);
        return v1.minusInSite(v2).multInSite(1 / delta);
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
        this.functionZBackup = this.functionZ;
    }

    @Override
    public void restoreState() {
        super.restoreState();
        this.functionX = this.functionXBackup;
        this.functionY = this.functionYBackup;
        this.functionZ = this.functionZBackup;
    }

    /**
     * Returns a Shape object with the form of the curve
     *
     * @return The Shape created
     */
    public Shape getShape() {
        Shape resul = new Shape(getPath());
        resul.getMp().copyFrom(getMp());
        return resul;
    }
}
