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
    
    abstract public void prepareObjects();

    abstract public void applyTransform(double t,double lt);

    abstract public void finish();
    abstract public void addObjectsToScene();

}
