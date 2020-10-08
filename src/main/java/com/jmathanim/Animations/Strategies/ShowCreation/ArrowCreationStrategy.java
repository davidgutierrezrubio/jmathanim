/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Animations.Strategies.ShowCreation;

import com.jmathanim.Animations.ApplyCommand;
import com.jmathanim.Animations.Strategies.TransformStrategy;
import com.jmathanim.Animations.commands.Commands;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.Arrow2D;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class ArrowCreationStrategy extends TransformStrategy {

    private final Arrow2D obj;


    public ArrowCreationStrategy(Arrow2D obj, double runTime, JMathAnimScene scene) {
        super(scene);
        this.obj=obj;
    }

    @Override
    public void prepareObjects() {
        obj.saveState();
    }

    @Override
    public void applyTransform(double t, double lt) {
        obj.restoreState();
        obj.scale(obj.getBody().getPoint(0), lt, lt);
        obj.scaleArrowHead(lt);
    }

    @Override
    public void finish() {
        applyTransform(1, 1);
    }

    @Override
    public void addObjectsToScene() {
        scene.add(obj);
    }

}
