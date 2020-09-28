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
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class FadeOut extends Animation {

    MathObject mobj;
    private double alphaDraw;
    private double fillDraw;

    public FadeOut(MathObject mo) {
        super();
        mobj = mo;
    }

    public FadeOut(MathObject mo, double runtime) {
        super(runtime);
        mobj = mo;
    }

    @Override
    public void doAnim(double t) {
//        System.out.println("Anim FadeIn " + t);
        mobj.drawAlpha(alphaDraw*(1-t));
        mobj.fillAlpha(fillDraw*(1-t));
    }

    @Override
    public void finishAnimation() {
        scene.remove(mobj);
    }

    @Override
    public void initialize() {
        //Store alpha values 
        alphaDraw=mobj.mp.drawColor.alpha;
        fillDraw=mobj.mp.fillColor.alpha;
    }

    @Override
    public void addObjectsToScene(JMathAnimScene scene) {
        scene.add(mobj);
    }

}
