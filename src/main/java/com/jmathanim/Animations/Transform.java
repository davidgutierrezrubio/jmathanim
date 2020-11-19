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
import com.jmathanim.Animations.Strategies.Transform.MultiShapeTransform;
import com.jmathanim.Animations.Strategies.Transform.Optimizers.NullOptimizationStrategy;
import com.jmathanim.Animations.Strategies.Transform.PointInterpolationCanonical;
import com.jmathanim.Animations.Strategies.Transform.PointInterpolationSimpleShapeTransform;
import com.jmathanim.Animations.Strategies.Transform.RotateAndScaleXYStrategyTransform;
import com.jmathanim.Utils.MODrawProperties;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.FunctionGraph;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MathObject.MathObjectType;
import com.jmathanim.mathobjects.MultiShapeObject;
import com.jmathanim.mathobjects.Shape;

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
        FUNCTION_INTERPOLATION,
        MULTISHAPE_TRANSFORM
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
        mobjDestiny = ob2;
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

    private void determineTransformStrategy() {
        transformMethod = TransformMethod.INTERPOLATE_POINT_BY_POINT;//Default method if not specified
        if ((mobjTransformed instanceof MultiShapeObject) && (mobjDestiny instanceof MultiShapeObject)) {
            transformMethod = TransformMethod.MULTISHAPE_TRANSFORM;
            JMathAnimScene.logger.info("Transform method: Multishape");
            return;
        }
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
            Shape shTransformed = (Shape) mobjTransformed;
            Shape shDst = (Shape) mobjDestiny;
            if (shTransformed.jmpath.size() == shDst.jmpath.size()) {
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
        JMathAnimScene.logger.info("Transform method: Point interpolation between 2 curves");
    }

    private void createTransformStrategy() {
        //Now I choose strategy
        switch (transformMethod) {
            case MULTISHAPE_TRANSFORM:
                transformStrategy = new MultiShapeTransform(runTime, (MultiShapeObject) mobjTransformed, (MultiShapeObject) mobjDestiny);
                break;
            case INTERPOLATE_SIMPLE_SHAPES_BY_POINT:
                transformStrategy = new PointInterpolationSimpleShapeTransform(runTime, (Shape) mobjTransformed, (Shape) mobjDestiny);
                break;
            case INTERPOLATE_POINT_BY_POINT:
                transformStrategy = new PointInterpolationCanonical(runTime, (Shape) mobjTransformed, (Shape) mobjDestiny);
                break;
            case HOMOTHECY_TRANSFORM:
                transformStrategy = new HomothecyStrategyTransform(runTime, (Shape) mobjTransformed, (Shape) mobjDestiny);
                break;
            case ROTATE_AND_SCALEXY_TRANSFORM:
                transformStrategy = new RotateAndScaleXYStrategyTransform(runTime, (Shape) mobjTransformed, (Shape) mobjDestiny);
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
        //Remove fist object and add the second to the scene
        scene.add(mobjDestiny);
        scene.remove(mobjTransformed);
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
