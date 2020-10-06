/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Animations.Strategies.ShowCreation;

import com.jmathanim.Animations.Animation;
import com.jmathanim.Animations.Concatenate;
import com.jmathanim.Animations.ShowCreation;
import com.jmathanim.Animations.Strategies.TransformStrategy;
import com.jmathanim.Animations.WaitAnimation;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MultiShapeObject;
import com.jmathanim.mathobjects.Shape;
import java.util.ArrayList;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class MultiShapeCreationStrategy extends TransformStrategy {

    private final double timegap;
    private final double runtime;
    private final MultiShapeObject shapes;
    private final ArrayList<Concatenate> creators;

    public MultiShapeCreationStrategy(MultiShapeObject shapes, double runtime, double timegap, JMathAnimScene scene) {
        super(scene);
        this.timegap = timegap;
        this.runtime = runtime;
        this.shapes = shapes;
        creators = new ArrayList<>();
    }

    @Override
    public void prepareObjects() {
        double realRuntime = this.runtime - this.timegap * shapes.shapes.size();
        scene.remove(shapes);
        int n = 0;
        for (Shape sh : shapes.shapes) {
            this.scene.remove(sh);
            WaitAnimation wait = new WaitAnimation(this.timegap * n);
            ShowCreation cr = new ShowCreation(sh, realRuntime);
            Concatenate anim = new Concatenate();
            anim.add(wait);
            anim.add(cr);
            n++;
            creators.add(anim);
            anim.initialize();
        }
    }

    @Override
    public void applyTransform(double t,double lt) {
        for (Animation anim:creators)
        {
            anim.doAnim(t,lt);
        }
    }

    @Override
    public void finish() {
//       for (Animation anim:creators)
//        {
//            anim.finishAnimation();
//        }
       scene.add(shapes);
    }

    @Override
    public void addObjectsToScene() {
    }

}
