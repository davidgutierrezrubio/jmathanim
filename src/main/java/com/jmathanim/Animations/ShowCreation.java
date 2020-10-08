/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Animations;

import com.jmathanim.Animations.Strategies.ShowCreation.ArrowCreationStrategy;
import com.jmathanim.Animations.Strategies.ShowCreation.FirstDrawThenFillStrategy;
import com.jmathanim.Animations.Strategies.ShowCreation.LineCreationStrategy;
import com.jmathanim.Animations.Strategies.ShowCreation.MultiShapeCreationStrategy;
import com.jmathanim.Animations.Strategies.ShowCreation.SimpleShapeCreationStrategy;
import com.jmathanim.Animations.Strategies.TransformStrategy;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Arrow2D;
import com.jmathanim.mathobjects.CanonicalJMPath;
import com.jmathanim.mathobjects.Line;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MultiShapeObject;
import com.jmathanim.mathobjects.SVGMathObject;
import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class ShowCreation extends Animation {

    public static final int METHOD_NONE = 0;
    public static final int METHOD_FIRST_DRAW_AND_THEN_FILL = 1;
    public static final int METHOD_SIMPLE_SHAPE_CREATION = 2;
    public static final int METHOD_MULTISHAPE_CREATION = 3;
    public static final int METHOD_LINE_CREATION = 4;
    public static final int METHOD_ARROW_CREATION = 5;
    MathObject mobj;
    CanonicalJMPath canonPath;
    private MultiShapeObject msh;
    private TransformStrategy strategy;
    private int strategyType = METHOD_NONE;

    /**
     * Creates an animation that shows the creation of the specified
     * {@link MathObject}
     *
     * @param runtime Run time in seconds
     * @param mobj Mathobject to animate
     */
    public ShowCreation(double runtime, MathObject mobj) {
        super(runtime);
        this.mobj = mobj;
    }

    @Override
    public void initialize() {
        try {
            if (strategyType == METHOD_NONE) {
                determineCreationStrategy(this.mobj);
                createStrategy();
            }
            strategy.prepareObjects();
        } catch (NullPointerException | ClassCastException e) {
            JMathAnimScene.logger.error("Couldn't create ShowCreation strategy. Animation will not be done." + e.getLocalizedMessage());
        }
    }

    @Override
    public void doAnim(double t, double lt) {
        if (strategy != null) {
            strategy.applyTransform(t, lt);
        }
    }

    @Override
    public void finishAnimation() {
        if (strategy != null) {
            strategy.finish();
        }
    }

    @Override
    public void addObjectsToScene(JMathAnimScene scene) {
        if (strategy != null) {
            strategy.addObjectsToScene();
        }
    }

    /**
     * Determines the strategy to animate the creation of the object
     *
     * @param mobj {@link MathObject} which will be animated. Its type
     * determines the type of animation to perform.
     */
    public void determineCreationStrategy(MathObject mobj) {

        if (mobj instanceof SVGMathObject) {
            this.strategyType = METHOD_FIRST_DRAW_AND_THEN_FILL;
            return;
        }
        if (mobj instanceof Arrow2D) {
            this.strategyType = METHOD_ARROW_CREATION;
            return;
        }
        if (mobj instanceof MultiShapeObject) {
            this.strategyType = METHOD_MULTISHAPE_CREATION;
            return;
        }
        if (mobj instanceof Line) {
            this.strategyType = METHOD_LINE_CREATION;
            return;
        }
        if (mobj instanceof Shape) {
            this.strategyType = METHOD_SIMPLE_SHAPE_CREATION;
            return;
        }

    }

    /**
     * Sets the animation strategy
     *
     * @param strategyType Strategy, chosen from
     * {@link METHOD_NONE},{@link METHOD_FIRST_DRAW_AND_THEN_FILL},{@link METHOD_SIMPLE_SHAPE_CREATION}, {@link METHOD_MULTISHAPE_CREATION}
     */
    public void setStrategy(int strategyType) {
        this.strategyType = strategyType;
    }

    /**
     * Creates the strategy object
     *
     * @throws ClassCastException If the current object cannot be cast to the
     * required class.
     */
    private void createStrategy() throws ClassCastException {
        switch (this.strategyType) {
            case METHOD_LINE_CREATION:
                strategy = new LineCreationStrategy((Line) mobj, this.scene);
                JMathAnimScene.logger.info("ShowCreation method: LineCreationStrategy");
                break;
            case METHOD_ARROW_CREATION:
                strategy = new ArrowCreationStrategy((Arrow2D) mobj, this.runTime,this.scene);
                JMathAnimScene.logger.info("ShowCreation method: ArrowCreationStrategy");
                break;
            case METHOD_SIMPLE_SHAPE_CREATION:
                strategy = new SimpleShapeCreationStrategy((Shape) mobj, this.scene);
                JMathAnimScene.logger.info("ShowCreation method: SimpleShapeCreationStrategy");
                break;
            case METHOD_MULTISHAPE_CREATION:
                strategy = new MultiShapeCreationStrategy((MultiShapeObject) mobj, .5, this.runTime, this.scene);
                JMathAnimScene.logger.info("ShowCreation method: MultiShapeCreationStrategy");
                break;
            case METHOD_FIRST_DRAW_AND_THEN_FILL:
                strategy = new FirstDrawThenFillStrategy((MultiShapeObject) mobj, .5, this.runTime, this.scene);
                JMathAnimScene.logger.info("ShowCreation method: FirstDrawThenFillStrategy");
                break;
            default:
                break;
        }
    }

}
