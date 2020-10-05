/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Animations;

import com.jmathanim.jmathanim.JMathAnimScene;

/**
 * Simple animation which does nothing
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class WaitAnimation extends Animation{

    public WaitAnimation(double runTime) {
        super(runTime);
    }

    @Override
    public void initialize() {
    }

    @Override
    public void doAnim(double t) {
    }

    @Override
    public void finishAnimation() {
    }

    @Override
    public void addObjectsToScene(JMathAnimScene scene) {
    }
    
}
