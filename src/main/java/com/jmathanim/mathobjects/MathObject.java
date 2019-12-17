/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jmathanim.mathobjects;

import com.jmathanim.Utils.ConfigUtils;
import com.jmathanim.Utils.Vec;
import java.util.Properties;

/**
 *
 * @author David Gutiérrez Rubio <davidgutierrezrubio@gmail.com>
 */
public abstract class MathObject implements Drawable {

    String[] DEFAULT_CONFIG_MATHOBJECT = {
        "VISIBLE", "TRUE",
        "ALPHA", "1",
        "COLOR", "255"
    };
    Properties cnf;
    //This parameter specifies the amount of object to be drawn
    //0=none, 1/2=draw half
    protected double drawParam;
    protected double alpha;

    public MathObject() {
        this(null);
    }

    

    public MathObject(Properties configParam) {
        cnf = new Properties();
        ConfigUtils.digest_config(cnf, DEFAULT_CONFIG_MATHOBJECT, configParam);
        alpha=Float.parseFloat(cnf.getProperty("ALPHA"));
        drawParam=1;
    }

    public abstract Vec getCenter();
    public abstract void moveTo(Vec coords);
    public abstract void shift(Vec shiftVector);

    public final double getDrawParam() {
        return drawParam;
    }

    public final void setDrawParam(double drawParam) {
        this.drawParam = drawParam;
    }
    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }
    
}
