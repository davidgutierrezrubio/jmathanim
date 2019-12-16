/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Animations;

import com.jmathanim.mathobjects.MathObject;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public class ShowCreation extends Animation {

    public ShowCreation(MathObject mobj) {
        super(mobj);
    }

    @Override
    public void doAnim(double t) {
        mobj.setDrawParam(t);
    }
    
}
