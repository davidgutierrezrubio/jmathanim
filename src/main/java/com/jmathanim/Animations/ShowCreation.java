/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Animations;

import com.jmathanim.Animations.Strategies.ShowCreation.FirstDrawThenFillStrategy;
import com.jmathanim.Animations.Strategies.ShowCreation.MultiShapeCreationStrategy;
import com.jmathanim.Animations.Strategies.ShowCreation.SimpleShapeCreationStrategy;
import com.jmathanim.Animations.Strategies.TransformStrategy;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.CanonicalJMPath;
import com.jmathanim.mathobjects.LaTeXMathObject;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MultiShapeObject;
import com.jmathanim.mathobjects.SVGMathObject;
import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class ShowCreation extends Animation {

    MathObject mobj;
    CanonicalJMPath canonPath;
    private MultiShapeObject msh;
    private TransformStrategy strategy;

    public ShowCreation(Shape mobj) {
        super();
        this.mobj = mobj;
    }

    public ShowCreation(MathObject mobj, double runtime) {
        super(runtime);
        this.mobj = mobj;
    }

    @Override
    public void initialize() {
        if (strategy == null) {
            determineCreationStrategy(this.mobj);
        }
        if (strategy != null) {
            strategy.prepareObjects();
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
        strategy.addObjectsToScene();
    }

    public TransformStrategy determineCreationStrategy(MathObject mobj) {

        if (mobj instanceof SVGMathObject) {
            strategy = new FirstDrawThenFillStrategy((MultiShapeObject) mobj, .5, this.runTime, this.scene);
            JMathAnimScene.logger.info("ShowCreation method: FirstDrawThenFillStrategy");
            return strategy;
        }
        if (mobj instanceof Shape) {
            strategy = new SimpleShapeCreationStrategy((Shape) mobj, this.scene);
            JMathAnimScene.logger.info("ShowCreation method: SimpleShapeCreationStrategy");
            return strategy;
        }
        if (mobj instanceof MultiShapeObject) {
            strategy = new MultiShapeCreationStrategy((MultiShapeObject) mobj, .5, this.runTime, this.scene);
            JMathAnimScene.logger.info("ShowCreation method: MultiShapeCreationStrategy");
            return strategy;
        }
        return strategy;

    }

    public void setStrategy(TransformStrategy strategy) {
        this.strategy = strategy;
    }

}
