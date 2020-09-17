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
public class ShowCreation extends Animation {

    private int numSlices = 1;
    MathObject mobj;

    public ShowCreation(MathObject mobj) {
        super();
        this.mobj=mobj;
    }

    public ShowCreation(MathObject mobj, double runtime) {
        super(runtime);
        this.mobj=mobj;
        
    }

    public int getNumSlices() {
        return numSlices;
    }

    public ShowCreation numSlices(int numSlices) {
        this.numSlices = numSlices;
        return this;
    }

    @Override
    public void doAnim(double t) {
        System.out.println("Anim ShowCreation " + t);
        mobj.setDrawParam(t, this.numSlices);
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
