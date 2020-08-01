/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import java.util.Properties;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public abstract class JMPathMathObject extends MathObject{
    
    protected JMPath jmpath;
    protected boolean needsRecalcControlPoints;
    public JMPathMathObject() {
        this(null);
    }

    public JMPathMathObject(Properties configParam) {
        super(configParam);
        needsRecalcControlPoints=false;
    }
    
    abstract public void computeJMPath();
    
}
