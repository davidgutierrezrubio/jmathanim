/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Animations;

import com.jmathanim.mathobjects.MathObject;

/**
 *
 * @author David Gutierrez Rubio davidgutierrezrubio@gmail.com
 */
public class FadeIn extends Animation {

    public FadeIn(MathObject mo) {
        super(mo);
    }

    public FadeIn(MathObject mo, double runtime) {
        super(mo, runtime);
    }

    @Override
    public void doAnim(double t) {
        System.out.println("Anim FadeIn "+t);
        mobj.setAlpha(t);
    }

}
