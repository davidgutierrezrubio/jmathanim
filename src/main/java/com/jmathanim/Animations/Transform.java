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
package com.jmathanim.Animations;

import com.jmathanim.Animations.Strategies.Transform.FunctionSimpleInterpolateTransform;
import com.jmathanim.Animations.Strategies.Transform.HomothecyStrategyTransform;
import com.jmathanim.Animations.Strategies.Transform.Optimizers.NullOptimizationStrategy;
import com.jmathanim.Animations.Strategies.Transform.PointInterpolationCanonical;
import com.jmathanim.Animations.Strategies.Transform.PointInterpolationSimpleShapeTransform;
import com.jmathanim.Animations.Strategies.Transform.RotateAndScaleXYStrategyTransform;
import com.jmathanim.Utils.MODrawProperties;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.FunctionGraph;
import com.jmathanim.mathobjects.JMPath;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MathObject.MathObjectType;
import com.jmathanim.mathobjects.Shape;
import java.util.ArrayList;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Transform extends Animation {

    public enum TransformMethod {
        INTERPOLATE_SIMPLE_SHAPES_BY_POINT,
        INTERPOLATE_POINT_BY_POINT,
        HOMOTHECY_TRANSFORM,
        ROTATE_AND_SCALEXY_TRANSFORM,
        FUNCTION_INTERPOLATION
    }

    public final MathObject mobjDestiny;
    public final MathObject mobjTransformed;
    private MODrawProperties propBase;
    private TransformMethod transformMethod;
    private boolean shouldOptimizePathsFirst;
    public boolean forceChangeDirection;
    private boolean isFinished;
//    private JMathAnimScene scene;
    private Animation transformStrategy;

    public static Transform make(double runTime, MathObject ob1, MathObject ob2) {
        return new Transform(runTime, ob1, ob2);
    }

    public Transform(double runTime, MathObject ob1, MathObject ob2) {
        super(runTime);
        mobjTransformed = ob1;
        mobjDestiny = ob2.copy();
        transformMethod = null;
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

        if (!shouldOptimizePathsFirst) {
            transformStrategy.setOptimizationStrategy(new NullOptimizationStrategy());
        } else {
            transformStrategy.setOptimizationStrategy(null);
        }
        //Variable strategy should have proper strategy to transform
        //If method is null means that user didn't force one
        transformStrategy.setLambda(lambda);
        transformStrategy.initialize();

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
        transformMethod = TransformMethod.INTERPOLATE_POINT_BY_POINT;//Default method if not specified

        if ((mobjTransformed instanceof FunctionGraph) && (mobjDestiny instanceof FunctionGraph)) {
            transformMethod = TransformMethod.FUNCTION_INTERPOLATION;
            JMathAnimScene.logger.info("Transform method: Interpolation of functions");
            return;
        }
        if ((mobjTransformed.getObjectType() == MathObjectType.SEGMENT) && (mobjDestiny.getObjectType() == MathObjectType.SEGMENT)) {
            transformMethod = TransformMethod.HOMOTHECY_TRANSFORM;
            JMathAnimScene.logger.info("Transform method: Homothecy");
            return;
        }

        //Circle & Circle
        if ((mobjTransformed.getObjectType() == MathObjectType.CIRCLE) && (mobjDestiny.getObjectType() == MathObjectType.CIRCLE)) {
            transformMethod = TransformMethod.HOMOTHECY_TRANSFORM;
            shouldOptimizePathsFirst = true;
            JMathAnimScene.logger.info("Transform method: Homothecy");
            return;
        }

        //Rectangle & Rectangle
        if ((mobjTransformed.getObjectType() == MathObjectType.RECTANGLE) && (mobjDestiny.getObjectType() == MathObjectType.RECTANGLE)) {
            transformMethod = TransformMethod.ROTATE_AND_SCALEXY_TRANSFORM;
            JMathAnimScene.logger.info("Transform method: Rotate and Scale XY");
            return;
        }

        //Regular Polygons with the same number of vertices
        if ((mobjTransformed.getObjectType() == MathObjectType.REGULAR_POLYGON) && (mobjDestiny.getObjectType() == MathObjectType.REGULAR_POLYGON)) {
            Shape sh = (Shape) mobjTransformed;
            if (sh.jmpath.size() == sh.jmpath.size()) {
                transformMethod = TransformMethod.ROTATE_AND_SCALEXY_TRANSFORM;
                JMathAnimScene.logger.info("Transform method: Rotate and Scale XY");
            } else { //Different number of vertices
                transformMethod = TransformMethod.INTERPOLATE_POINT_BY_POINT;
                JMathAnimScene.logger.info("Transform method: By point");
                return;
            }
        }
        if ((mobjTransformed instanceof Shape) && (mobjDestiny instanceof Shape)) {
            Shape shTr = (Shape) mobjTransformed;
            Shape shDst = (Shape) mobjDestiny;
            //2 simple, closed curves
            if ((shTr.getPath().getNumberOfConnectedComponents() == 0) && (shDst.getPath().getNumberOfConnectedComponents() == 0)) {
                transformMethod = TransformMethod.INTERPOLATE_SIMPLE_SHAPES_BY_POINT;
                JMathAnimScene.logger.info("Transform method: Point interpolation between 2 simple closed curves");
                return;
            }
        }

    }

    private void createTransformStrategy() {
        //Now I choose strategy
        switch (transformMethod) {
            case INTERPOLATE_SIMPLE_SHAPES_BY_POINT:
                transformStrategy = new PointInterpolationSimpleShapeTransform(runTime, (Shape)mobjTransformed, (Shape)mobjDestiny);
                break;
            case INTERPOLATE_POINT_BY_POINT:
                transformStrategy = new PointInterpolationCanonical(runTime, (Shape)mobjTransformed, (Shape)mobjDestiny);
                break;
            case HOMOTHECY_TRANSFORM:
                transformStrategy = new HomothecyStrategyTransform(runTime, (Shape)mobjTransformed, (Shape)mobjDestiny);
                break;
            case ROTATE_AND_SCALEXY_TRANSFORM:
                transformStrategy = new RotateAndScaleXYStrategyTransform(runTime, (Shape)mobjTransformed, (Shape)mobjDestiny);
                break;
            case FUNCTION_INTERPOLATION:
                transformStrategy = new FunctionSimpleInterpolateTransform(runTime, (FunctionGraph) mobjTransformed, (FunctionGraph) mobjDestiny);
                break;
        }
    }

    @Override
    public void finishAnimation() {
        if (isFinished) {
            return;
        } else {
            isFinished = true;
        }
        transformStrategy.finishAnimation();
    }

    @Override
    public void addObjectsToScene(JMathAnimScene scene) {
        transformStrategy.addObjectsToScene(scene);
    }

//    private void createOptimizationStrategy() {
//        switch (optimizeMethod) {
//            case NONE:
//                optimizeStrategy = new NullOptimizationStrategy();
//                JMathAnimScene.logger.info("Optimization strategy chosen: None");
//                break;
//            case SIMPLE_CONNECTED_PATHS:
//                optimizeStrategy = new SimpleConnectedPathsOptimizationStrategy(mobjTransformed, mobjDestiny);
//                JMathAnimScene.logger.info("Optimization strategy chosen: Simple connected paths");
//                break;
//        }
//    }
    public Transform optimizePaths(boolean shouldOptimizePathsFirst) {
        this.shouldOptimizePathsFirst = shouldOptimizePathsFirst;
        return this;
    }

    public TransformMethod getTransformMethod() {
        return transformMethod;
    }

    public Transform transformMethod(TransformMethod transformMethod) {
        this.transformMethod = transformMethod;
        return this;
    }

    @Override
    public void doAnim(double t) {
        //Nothing to do here, it delegates trough processAnimation()
    }

    @Override
    public boolean processAnimation() {
        return transformStrategy.processAnimation();
    }

}
