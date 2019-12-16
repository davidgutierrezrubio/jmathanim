/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Animations;

import com.jmathanim.mathobjects.MathObject;

/**
 * This abstract class stores an Animation
 * Animations are always played using a parameter t from 0 to 1.
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public abstract class Animation {
double t,dt;
protected final MathObject mobj;

    public Animation(MathObject mobj) {
        this.mobj = mobj;
    }
    
    /**
     * Do animation
     * @param t double betwenn 0 and 1 0=start, 1=end
     */
    abstract public void doAnim(double t);
}
