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
public class ShowCreation extends Animation {

    public ShowCreation(MathObject mobj) {
        super(mobj);
    }

    public ShowCreation(MathObject mobj, double runtime) {
        super(mobj, runtime);
    }

    @Override
    public void doAnim(double t) {
        System.out.println("Anim ShowCreation "+t);
        mobj.setDrawParam(t);
    }

}
