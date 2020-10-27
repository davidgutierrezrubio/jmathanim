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
package com.jmathanim.Animations;

import com.jmathanim.Animations.Strategies.Transform.AffineStrategyTransform;
import com.jmathanim.Animations.Strategies.Transform.FunctionTransformStrategy;
import com.jmathanim.Animations.Strategies.Transform.HomothecyStrategyTransform;
import com.jmathanim.Animations.Strategies.Transform.Optimizers.NullOptimizationStrategy;
import com.jmathanim.Animations.Strategies.Transform.Optimizers.OptimizePathsStrategy;
import com.jmathanim.Animations.Strategies.Transform.PointInterpolationCanonical;
import com.jmathanim.Animations.Strategies.Transform.PointInterpolationSimpleShape;
import com.jmathanim.Animations.Strategies.Transform.RotateAndScaleXYStrategyTransform;
import com.jmathanim.Animations.Strategies.Transform.Optimizers.SimpleConnectedPathsOptimizationStrategy;
import com.jmathanim.Animations.Strategies.TransformStrategy;
import com.jmathanim.Utils.MODrawProperties;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.FunctionGraph;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Shape;
import java.util.ArrayList;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Transform extends Animation {

    public static final int METHOD_INTERPOLATE_SIMPLE_SHAPES_BY_POINT = 1;
    public static final int METHOD_INTERPOLATE_POINT_BY_POINT = 2;
    public static final int METHOD_HOMOTHECY_TRANSFORM = 3;
    public static final int METHOD_AFFINE_TRANSFORM = 4;
    public static final int METHOD_ROTATE_AND_SCALEXY_TRANSFORM = 5;
    public static final int METHOD_FUNCTION_INTERPOLATION = 6;

    public static final int OPTIMIZE_NONE = 1;
    public static final int OPTIMIZE_SIMPLE_CONNECTED_PATHS = 2;

    private JMPath jmpathOrig, jmpathDstBackup;
    public final Shape mobjDestiny;
    public final Shape mobjTransformed;
    private MODrawProperties propBase;
    private Integer transformMethod;
    private Integer optimizeMethod;
    private boolean shouldOptimizePathsFirst;
    public boolean forceChangeDirection;
    private boolean isFinished;
    private JMathAnimScene scene;
    private TransformStrategy transformStrategy;
    private OptimizePathsStrategy optimizeStrategy;

    public static Transform make(double runTime, Shape ob1, Shape ob2) {
        return new Transform(runTime, ob1, ob2);
    }

    public Transform(double runTime, Shape ob1, Shape ob2) {
        super(runTime);
        mobjTransformed = ob1;
        mobjDestiny = ob2.copy();
        transformMethod = null;
        optimizeMethod = null;
        shouldOptimizePathsFirst = true;
        forceChangeDirection = false;
        isFinished = false;
        optimizeStrategy = null;
    }

    @Override
    public void initialize() {
        //Determine optimal transformation

        //Should use an homothecy instead of point-to-point interpolation 
        //in the following cases:
        //2 segments/lines or segment/line
        //2 circles/ellipses
        //2 regular polygons with same number of sides
        if (transformMethod == null) {
            determineTransformStrategy();
        }
        createTransformStrategy();

        if (shouldOptimizePathsFirst) {
            if (optimizeMethod == null) {
                determineOptimizationStrategy();
            }
            createOptimizationStrategy();
        }
        transformStrategy.setOptimizationStrategy(optimizeStrategy);
        //Variable strategy should have proper strategy to transform
        //If method is null means that user didn't force one
        transformStrategy.prepareObjects();
    }

    /**
     * Cycles the point of closed path (and inverts its orientation if
     * necessary) in order to minimize the sum of squared distances from the
     * points of two paths with the same number of nodes
     *
     * @param path1
     * @param path2
     * @param forceChangeDirection
     */
    public void minimizeSumDistance(JMPath path1, JMPath path2, boolean forceChangeDirection) {
        ArrayList<Double> distances = new ArrayList<Double>();
        double minSumDistances = 999999999;
        int optimalStep = 0;
        //this variable is negative if both paths have different orientation
        //so the transformed path reverses itself to better adjust
        int changeDirection = path1.getOrientation() * path2.getOrientation();
        changeDirection = (forceChangeDirection ? -changeDirection : changeDirection);
        //If the path is open, we can't cycle the path, so 
        //we set numberOfCycles to 1
        int numberOfCycles = path1.size();//(path1.isClosed ? path1.size() : 1);
        //First, without changing direction
        for (int step = 0; step < numberOfCycles; step++) {
            JMPath tempPath = path1.copy();
            tempPath.cyclePoints(step, changeDirection);
            double sumDistances = tempPath.sumDistance(path2);
            distances.add(sumDistances);
            if (sumDistances < minSumDistances) {
                minSumDistances = sumDistances;
                optimalStep = step;
            }

        }
        path1.cyclePoints(optimalStep, changeDirection);
    }

    private void determineTransformStrategy() {
        String methodTextOutput = "Transform method: By point";

        transformMethod = METHOD_INTERPOLATE_POINT_BY_POINT;//Default method if not specified

        if ((mobjTransformed instanceof FunctionGraph) && (mobjDestiny instanceof FunctionGraph)) {
            transformMethod = METHOD_FUNCTION_INTERPOLATION;
            methodTextOutput = "Transform method: Interpolation of functions";
        }
        if ((mobjTransformed.getObjectType() == MathObject.SEGMENT) && (mobjDestiny.getObjectType() == MathObject.SEGMENT)) {
            transformMethod = METHOD_HOMOTHECY_TRANSFORM;
            methodTextOutput = "Transform method: Homothecy";
        }

        //Circle & Circle
        if ((mobjTransformed.getObjectType() == MathObject.CIRCLE) && (mobjDestiny.getObjectType() == MathObject.CIRCLE)) {
            transformMethod = METHOD_HOMOTHECY_TRANSFORM;
            shouldOptimizePathsFirst = true;
            methodTextOutput = "Transform method: Homothecy";
        }

        //Rectangle & Rectangle
        if ((mobjTransformed.getObjectType() == MathObject.RECTANGLE) && (mobjDestiny.getObjectType() == MathObject.RECTANGLE)) {
            transformMethod = METHOD_ROTATE_AND_SCALEXY_TRANSFORM;
            methodTextOutput = "Transform method: Rotate and Scale XY";
        }

        //Regular Polygons with the same number of vertices
        if ((mobjTransformed.getObjectType() == MathObject.REGULAR_POLYGON) && (mobjDestiny.getObjectType() == MathObject.REGULAR_POLYGON)) {
            if (mobjTransformed.jmpath.size() == mobjDestiny.jmpath.size()) {
                transformMethod = METHOD_ROTATE_AND_SCALEXY_TRANSFORM;
                methodTextOutput = "Transform method: Rotate and Scale XY";
            } else { //Different number of vertices
                transformMethod = METHOD_INTERPOLATE_POINT_BY_POINT;
                methodTextOutput = "Transform method: By point";
            }
        }

        //2 simple, closed curves
        if ((mobjTransformed.getPath().getNumberOfConnectedComponents() == 0) && (mobjDestiny.getPath().getNumberOfConnectedComponents() == 0)) {
            transformMethod = METHOD_INTERPOLATE_SIMPLE_SHAPES_BY_POINT;
            methodTextOutput = "Transform method: Point interpolation between 2 simple closed curves";
        }

        JMathAnimScene.logger.info(methodTextOutput);

    }

    private void createTransformStrategy() {
        //Now I choose strategy
        switch (transformMethod) {
            case METHOD_INTERPOLATE_SIMPLE_SHAPES_BY_POINT:
                transformStrategy = new PointInterpolationSimpleShape(mobjTransformed, mobjDestiny, scene);
                break;
            case METHOD_INTERPOLATE_POINT_BY_POINT:
                transformStrategy = new PointInterpolationCanonical(mobjTransformed, mobjDestiny, scene);
                break;
            case METHOD_HOMOTHECY_TRANSFORM:
                transformStrategy = new HomothecyStrategyTransform(mobjTransformed, mobjDestiny, scene);
                break;
            case METHOD_AFFINE_TRANSFORM:
                transformStrategy = new AffineStrategyTransform(mobjTransformed, mobjDestiny, scene);
                break;
            case METHOD_ROTATE_AND_SCALEXY_TRANSFORM:
                transformStrategy = new RotateAndScaleXYStrategyTransform(mobjTransformed, mobjDestiny, scene);
                break;
            case METHOD_FUNCTION_INTERPOLATION:
                transformStrategy = new FunctionTransformStrategy((FunctionGraph) mobjTransformed, (FunctionGraph) mobjDestiny, scene);
                break;
        }
    }

    @Override
    public void doAnim(double t, double lt) {
        transformStrategy.applyTransform(t, lt);

    }

    @Override
    public void finishAnimation() {
        if (isFinished) {
            return;
        } else {
            isFinished = true;
        }
        transformStrategy.finish();
        //Copy type of destiny object to transformed one
        mobjTransformed.setObjectType(mobjDestiny.getObjectType());
    }

    public int getMethod() {
        return transformMethod;
    }

    public void setMethod(int method) {
        this.transformMethod = method;
    }

    @Override
    public void addObjectsToScene(JMathAnimScene scene) {
    }

    private void determineOptimizationStrategy() {
        optimizeMethod = OPTIMIZE_NONE;//default
        //Case 1: 2 simple closed curves (square to circle, for example)
        if ((mobjTransformed.jmpath.getNumberOfConnectedComponents() == 0) && (mobjDestiny.jmpath.getNumberOfConnectedComponents() == 0)) {
            optimizeMethod = OPTIMIZE_SIMPLE_CONNECTED_PATHS;
            return;
        }
    }

    private void createOptimizationStrategy() {
        switch (optimizeMethod) {
            case OPTIMIZE_NONE:
                optimizeStrategy = new NullOptimizationStrategy();
                JMathAnimScene.logger.info("Optimization strategy chosen: None");
                break;
            case OPTIMIZE_SIMPLE_CONNECTED_PATHS:
                optimizeStrategy = new SimpleConnectedPathsOptimizationStrategy(mobjTransformed, mobjDestiny);
                JMathAnimScene.logger.info("Optimization strategy chosen: Simple connected paths");
                break;
        }
    }

    public Transform optimizePaths(boolean shouldOptimizePathsFirst) {
        this.shouldOptimizePathsFirst = shouldOptimizePathsFirst;
        return this;
    }

    public Integer getTransformMethod() {
        return transformMethod;
    }

    public Transform transformMethod(Integer transformMethod) {
        this.transformMethod = transformMethod;
        return this;
    }

}
