/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects.MOProperties;

import com.jmathanim.Animations.AffineJTransform;
import com.jmathanim.mathobjects.MathObject;
import com.jmathanim.mathobjects.Stateable;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public abstract class MathObjectAttributes implements Stateable {

    private MathObject parent;

    public MathObjectAttributes(MathObject parent) {
        this.parent = parent;
    }

    public abstract void applyTransform(AffineJTransform tr);

    public abstract MathObjectAttributes copy();

    public MathObject getParent() {
        return parent;
    }

    abstract public void setParent(MathObject parent);
    

}
