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

    private int numSlices=1;

    public ShowCreation(MathObject mobj) {
        super(mobj);
        this.numSlices=numSlices;
    }

    public ShowCreation(MathObject mobj,double runtime) {
        super(mobj, runtime);
    }

    public int getNumSlices() {
        return numSlices;
    }

    public void setNumSlices(int numSlices) {
        this.numSlices = numSlices;
    }

    @Override
    public void doAnim(double t) {
        System.out.println("Anim ShowCreation "+t);
        mobj.setDrawParam(t,this.numSlices);
    }

}
