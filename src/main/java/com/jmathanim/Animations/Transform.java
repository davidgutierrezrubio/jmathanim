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

import com.jmathanim.Animations.Strategies.Transform.ArrowTransform;
import com.jmathanim.Animations.Strategies.Transform.FunctionSimpleInterpolateTransform;
import com.jmathanim.Animations.Strategies.Transform.GeneralAffineTransformAnimation;
import com.jmathanim.Animations.Strategies.Transform.HomothecyTransformAnimation;
import com.jmathanim.Animations.Strategies.Transform.MultiShapeTransform;
import com.jmathanim.Animations.Strategies.Transform.Optimizers.NullOptimizationStrategy;
import com.jmathanim.Animations.Strategies.Transform.PointInterpolationCanonical;
import com.jmathanim.Animations.Strategies.Transform.PointInterpolationSimpleShapeTransform;
import com.jmathanim.Animations.Strategies.Transform.RotateAndScaleXYTransform;
import com.jmathanim.Animations.Strategies.Transform.TransformStrategy;
import com.jmathanim.Utils.JMathAnimConfig;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Arrow2D;
import com.jmathanim.mathobjects.FunctionGraph;
import com.jmathanim.mathobjects.Line;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MultiShapeObject;
import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Transform extends AnimationWithEffects {

    public enum TransformMethod {
        INTERPOLATE_SIMPLE_SHAPES_BY_POINT, INTERPOLATE_POINT_BY_POINT, HOMOTHECY_TRANSFORM,
        ROTATE_AND_SCALEXY_TRANSFORM, FUNCTION_INTERPOLATION, MULTISHAPE_TRANSFORM, GENERAL_AFFINE_TRANSFORM,
        ARROW_TRANSFORM
    }

    private final MathObject mobjDestiny;
    private MathObject mobjTransformed;
    private TransformMethod transformMethod;
    private boolean shouldOptimizePathsFirst;
    private TransformStrategy transformStrategy;

    public static Transform make(double runTime, MathObject ob1, MathObject ob2) {
        return new Transform(runTime, ob1, ob2);
    }

    /**
     * Creates a new Transform animation. Chooses the best strategy to transform
     * origin into destiny. After the transformation is done, origin is removed
     * from scene and destiny is added. In most cases, origin object becomes
     * unusable.
     *
     * @param runTime Duration in seconds
     * @param ob1 Origin object
     * @param ob2 Destiny object
     */
    public Transform(double runTime, MathObject ob1, MathObject ob2) {
        super(runTime);
        setDebugName("Transform");
        mobjTransformed = ob1;
        mobjDestiny = ob2;
        transformMethod = null;
        shouldOptimizePathsFirst = true;
        optimizeStrategy = null;
    }

    @Override
    public void initialize(JMathAnimScene scene) {
        super.initialize(scene);
        // Determine optimal transformation, if nothing has been chosen prior to init

        if (transformMethod == null) {
            determineTransformStrategy();
        }
        createTransformStrategy();

        if (!shouldOptimizePathsFirst) {
            transformStrategy.setOptimizationStrategy(new NullOptimizationStrategy());
        } else {
            transformStrategy.setOptimizationStrategy(null);
        }
        // Copy preferences to this strategy
        transformStrategy.setLambda(lambda);
        transformStrategy.setAddObjectsToScene(this.isShouldAddObjectsToScene());
        transformStrategy.setUseObjectState(this.isUseObjectState());
        transformStrategy.setShouldInterpolateStyles(this.isShouldInterpolateStyles());
        transformStrategy.initialize(scene);

    }

    private void determineTransformStrategy() {
        if ((mobjTransformed instanceof Arrow2D) && (mobjDestiny instanceof Arrow2D)) {
            transformMethod = TransformMethod.ARROW_TRANSFORM;
            return;
        }
        if (mobjTransformed instanceof Line) {
            mobjTransformed = ((Line) mobjTransformed).toSegment(JMathAnimConfig.getConfig().getCamera(), 2);
        }
        if ((mobjTransformed instanceof Shape) && (mobjDestiny instanceof MultiShapeObject)) {
            transformMethod = TransformMethod.MULTISHAPE_TRANSFORM;
            return;
        }
        if ((mobjTransformed instanceof MultiShapeObject) && (mobjDestiny instanceof Shape)) {
            transformMethod = TransformMethod.MULTISHAPE_TRANSFORM;
            return;
        }

        if ((mobjTransformed instanceof MultiShapeObject) && (mobjDestiny instanceof MultiShapeObject)) {
            transformMethod = TransformMethod.MULTISHAPE_TRANSFORM;
            return;
        }
        if ((mobjTransformed instanceof FunctionGraph) && (mobjDestiny instanceof FunctionGraph)) {
            transformMethod = TransformMethod.FUNCTION_INTERPOLATION;
            return;
        }
        if ((mobjTransformed instanceof Shape) && (mobjDestiny instanceof Shape)) {
            Shape shTr = (Shape) mobjTransformed;
            Shape shDst = (Shape) mobjDestiny;
            double epsilon = 0.000001;

            if (TransformStrategyChecker.testDirectHomothecyTransform(shTr, shDst, epsilon)) {
                transformMethod = TransformMethod.HOMOTHECY_TRANSFORM;
                return;
            }
            if (TransformStrategyChecker.testRotateScaleXYTransform(shTr, shDst, epsilon)) {
                transformMethod = TransformMethod.ROTATE_AND_SCALEXY_TRANSFORM;
                return;
            }
            if (TransformStrategyChecker.testGeneralAffineTransform(shTr, shDst, epsilon)) {
                transformMethod = TransformMethod.GENERAL_AFFINE_TRANSFORM;
                return;
            }
            // If 2 simple, closed curves, I have something simpler in mind...
            if ((shTr.getPath().getNumberOfConnectedComponents() == 0)
                    && (shDst.getPath().getNumberOfConnectedComponents() == 0)) {
                transformMethod = TransformMethod.INTERPOLATE_SIMPLE_SHAPES_BY_POINT;
                return;
            }
        }
        // Nothing previous worked...try with the most general method
        transformMethod = TransformMethod.INTERPOLATE_POINT_BY_POINT;

    }

    private MultiShapeObject convertToMultiShapeObject(MathObject obj) {
        if (obj instanceof MultiShapeObject) {
            return (MultiShapeObject) obj;
        }
        if (obj instanceof Shape) {
            return new MultiShapeObject((Shape) obj);
        }
        return null;// Don't know how to convert it to multishape, return null
    }

    private void createTransformStrategy() {
        // Now I choose strategy
        switch (transformMethod) {
            case ARROW_TRANSFORM:
                transformStrategy = new ArrowTransform(runTime, (Arrow2D) mobjTransformed, (Arrow2D) mobjDestiny);
                JMathAnimScene.logger.debug("Transform method: Arrow2D");
                break;
            case MULTISHAPE_TRANSFORM:
                transformStrategy = new MultiShapeTransform(runTime, convertToMultiShapeObject(mobjTransformed),
                        convertToMultiShapeObject(mobjDestiny));
                JMathAnimScene.logger.debug("Transform method: Multishape");
                break;

            case INTERPOLATE_SIMPLE_SHAPES_BY_POINT:
                transformStrategy = new PointInterpolationSimpleShapeTransform(runTime, (Shape) mobjTransformed,
                        (Shape) mobjDestiny);
                JMathAnimScene.logger.debug("Transform method: Point interpolation between 2 simple closed curves");
                break;
            case INTERPOLATE_POINT_BY_POINT:
                transformStrategy = new PointInterpolationCanonical(runTime, (Shape) mobjTransformed, (Shape) mobjDestiny);
                JMathAnimScene.logger.debug("Transform method: Point interpolation between 2 curves");
                break;
            case HOMOTHECY_TRANSFORM:
                transformStrategy = new HomothecyTransformAnimation(runTime, (Shape) mobjTransformed, (Shape) mobjDestiny);
                JMathAnimScene.logger.debug("Transform method: Homothecy");

                break;
            case ROTATE_AND_SCALEXY_TRANSFORM:
                transformStrategy = new RotateAndScaleXYTransform(runTime, (Shape) mobjTransformed, (Shape) mobjDestiny);
                JMathAnimScene.logger.debug("Transform method: Rotate and Scale XY");
                break;
            case GENERAL_AFFINE_TRANSFORM:
                transformStrategy = new GeneralAffineTransformAnimation(runTime, (Shape) mobjTransformed,
                        (Shape) mobjDestiny);
                JMathAnimScene.logger.debug("Transform method: General affine transform");
                break;
            case FUNCTION_INTERPOLATION:
                transformStrategy = new FunctionSimpleInterpolateTransform(runTime, (FunctionGraph) mobjTransformed,
                        (FunctionGraph) mobjDestiny);
                JMathAnimScene.logger.debug("Transform method: Interpolation of functions");
                break;
        }
//        if (shouldApplyEffects()) {
        if (transformStrategy instanceof AnimationWithEffects) {
            AnimationWithEffects tr = (AnimationWithEffects) transformStrategy;
            this.copyEffectParametersTo(tr);
        } else {
            JMathAnimScene.logger.error("Cannot apply effects to current transform");
        }
//        }
    }

    @Override
    public void finishAnimation() {
        super.finishAnimation();
        transformStrategy.finishAnimation();
        // Remove fist object and add the second to the scene
        addObjectsToscene(mobjDestiny);
        removeObjectsToscene(mobjTransformed);
    }

    /**
     * Sets if paths should be optimized in any available way, before doing the
     * animation
     *
     * @param shouldOptimizePathsFirst True if should optimize.
     * @return This object
     */
    public Transform setOptimizePaths(boolean shouldOptimizePathsFirst) {
        this.shouldOptimizePathsFirst = shouldOptimizePathsFirst;
        return this;
    }

    /**
     * Return the current transform method determined
     *
     * @return A value of the enum {@link TransformMethod}
     */
    public TransformMethod getTransformMethod() {
        return transformMethod;
    }

    /**
     * Forces to use a specified transform method. Forcing a transform method
     * may give unpredictable results.
     *
     * @param transformMethod The transform method,defined in enum
     * {@link TransformMethod}
     * @return This object
     */
    public Transform setTransformMethod(TransformMethod transformMethod) {
        this.transformMethod = transformMethod;
        return this;
    }

    @Override
    public void doAnim(double t) {
        // Nothing to do here, it delegates trough processAnimation()
    }

    @Override
    public boolean processAnimation() {
        return transformStrategy.processAnimation();
    }

    public MathObject getIntermediateTransformedObject() {
        if (transformStrategy!=null) {
            return transformStrategy.getIntermediateTransformedObject();
        }else
            return null;
    }
}
