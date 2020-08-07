/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Utils.Vec;
import java.util.Properties;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public abstract class JMPathMathObject extends MathObject {

    protected JMPath jmpath;
    protected boolean needsRecalcControlPoints;

    public JMPathMathObject() {
        this(null);
    }

    public JMPathMathObject(Properties configParam) {
        super(configParam);
        jmpath = new JMPath();
        needsRecalcControlPoints = false;
    }

    abstract public void computeJMPath();

    @Override
    public Vec getCenter() {
        Vec resul = new Vec(0, 0);
        for (Vec p : jmpath.getPoints()) {
            resul.addInSite(p);
        }
        resul.multInSite(1./jmpath.size());
        return resul;

    }

}
