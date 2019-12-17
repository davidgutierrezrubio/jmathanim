/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.Animations;

import com.jmathanim.mathobjects.MathObject;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class FadeIn extends Animation{

    public FadeIn(MathObject mo) {
        super(mo);
    }

    @Override
    public void doAnim(double t) {
        mobj.setAlpha(t);
    }
    
}
