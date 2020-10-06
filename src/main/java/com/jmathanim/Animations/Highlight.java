/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Animations;

import com.jmathanim.Animations.commands.Commands;
import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;

/**
 * Performs a short scale up and down to highlight an object
 *
 * @author David Gutiérrez Rubio davidgutierrezrubio@gmail.com
 */
public class Highlight extends Animation {

    ApplyCommand scale;
    public double standOutFactor = 1.1;
    public static double defaultStandOutTime = 2;

    public Highlight(MathObject obj, double runTime) {
        super(runTime);
        scale = Commands.scale(obj, obj.getCenter(), standOutFactor, 1);
    }

    public Highlight(MathObject obj) {
        this(obj, defaultStandOutTime);
    }

    @Override
    public void initialize() {
        scale.initialize();
    }

    @Override
    public void doAnim(double t,double lt) {
        double tt = 4 * t * (1 - t);
        double ltt = 4 * lt * (1 - lt);
        scale.doAnim(tt,ltt);
    }

    @Override
    public void finishAnimation() {
        scale.doAnim(0,0);
    }

    @Override
    public void addObjectsToScene(JMathAnimScene scene) {
    }

}
