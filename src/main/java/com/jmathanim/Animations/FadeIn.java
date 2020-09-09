/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Animations;

import com.jmathanim.jmathanim.JMathAnimScene;
import com.jmathanim.mathobjects.MathObject;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class FadeIn extends Animation {

    MathObject mobj;

    public FadeIn(MathObject mo) {
        super();
        mobj = mo;
    }

    public FadeIn(MathObject mo, double runtime) {
        super(runtime);
        mobj = mo;
    }

    @Override
    public void doAnim(double t) {
        System.out.println("Anim FadeIn " + t);
        mobj.setAlpha(t);
    }

    @Override
    public void finishAnimation() {
    }

    @Override
    public void initialize() {
    }

    @Override
    public void addObjectsToScene(JMathAnimScene scene) {
        scene.add(mobj);
    }

}
