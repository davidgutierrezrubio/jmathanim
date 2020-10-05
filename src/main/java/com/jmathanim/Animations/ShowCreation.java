/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Animations;

import com.jmathanim.Animations.Strategies.ShowCreation.SimpleShapeCreation;
import com.jmathanim.Animations.Strategies.TransformStrategy;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.CanonicalJMPath;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.MultiShapeObject;
import com.jmathanim.mathobjects.Shape;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class ShowCreation extends Animation {

    MathObject mobj;
    CanonicalJMPath canonPath;
    private MultiShapeObject msh;
    TransformStrategy strategy;

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
        determineCreationStrategy(this.mobj);
        if (strategy != null) {
            strategy.prepareObjects();
        }
    }

    @Override
    public void doAnim(double t) {
        if (strategy != null) {
            strategy.applyTransform(t);
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
        scene.add(mobj);
    }

    public TransformStrategy determineCreationStrategy(MathObject mobj) {

        if (mobj instanceof Shape) {
            strategy = new SimpleShapeCreation((Shape) mobj, this.scene);
        }
        return strategy;
    }

}
