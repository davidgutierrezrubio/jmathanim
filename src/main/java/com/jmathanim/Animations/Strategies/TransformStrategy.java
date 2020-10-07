/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Animations.Strategies;

import com.jmathanim.jmathanim.JMathAnimScene;

/**
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public abstract class TransformStrategy {

    protected final JMathAnimScene scene;

    public TransformStrategy(JMathAnimScene scene) {
        this.scene = scene;
    }

    /**
     * Prepare necessary objects to perform transformation. This method is
     * called immediately before playing the first frame.
     */
    abstract public void prepareObjects();

    /**
     * Apply current transform
     *
     * @param t Time of the animation 0<=t<=1 @ param lt lambda(t), where lambda
     * is a "smooth" function. Actual animation is computed for this value.
     */
    abstract public void applyTransform(double t, double lt);

    /**
     * Performs clean up and finishing methods. This method is called right
     * after the animation ends.
     */
    abstract public void finish();

    /**
     * Add necessary objects to scene
     */
    abstract public void addObjectsToScene();

}
