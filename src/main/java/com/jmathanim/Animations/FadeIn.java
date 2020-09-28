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
    private double alphaDraw;
    private double fillDraw;

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
//        System.out.println("Anim FadeIn " + t);
        mobj.drawAlpha(alphaDraw * t);
        mobj.fillAlpha(fillDraw * t);
    }

    @Override
    public void finishAnimation() {
        mobj.mp.drawColor.alpha = alphaDraw;
        mobj.mp.fillColor.alpha = fillDraw;
    }

    @Override
    public void initialize() {
        //Store alpha values 
        alphaDraw = mobj.mp.drawColor.alpha;
        fillDraw = mobj.mp.fillColor.alpha;
    }

    @Override
    public void addObjectsToScene(JMathAnimScene scene) {
        scene.add(mobj);
    }

}
